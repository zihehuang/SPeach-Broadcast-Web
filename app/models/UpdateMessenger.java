package models;

import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.UntypedActor;
import play.Logger;
import play.libs.Akka;
import play.libs.F.Callback0;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;
import org.apache.commons.lang3.StringEscapeUtils;
import play.mvc.Results.*;
import playextension.EventSource;

/**
 * Actor that relays messages to the clients.
 */
public class UpdateMessenger extends UntypedActor {

    /**
     * Singleton class to access the single update messenger.
     */
    public static ActorRef singleton = Akka.system().actorOf(Props.create(UpdateMessenger.class));

    /**
     * List of connections.
     */
    private List<EventSource> sockets = new ArrayList<EventSource>();

    /**
     * The actions the messenger takes when it receives an update.
     * @param message The message that the messenger receives
     * @throws Exception
     */
    @Override
    public void onReceive(Object message) throws Exception {
        if (message instanceof EventSource) {
            final EventSource eventSource = (EventSource) message;

            // if there is a connection.
            if (sockets.contains(eventSource)) {
                // conection needs to be removed.
                sockets.remove(eventSource);
                Logger.info("Browser disconnected (" + sockets.size() + " browsers currently connected)");
            } else {
                // connection needs to be added.
                eventSource.onDisconnected(new Callback0() {
                    @Override
                    public void invoke() throws Throwable {
                        getContext().self().tell(eventSource, null);
                    }
                });
                sockets.add(eventSource);

                SharedTranscript ourText = SharedTranscript.getOnlySharedTranscript();
                eventSource.sendData(ourText.toSSEForm());

                Logger.info("New browser connected (" + sockets.size() + " browsers currently connected)");
            }
        }
        // if the actor needs to send out an update to connected clients, do so.
        if ("UPDATE".equals(message)) {
            List<EventSource> shallowCopy = new ArrayList<EventSource>(sockets); //prevent ConcurrentModificationException
            for(EventSource es: shallowCopy) {
                SharedTranscript ourText = SharedTranscript.getOnlySharedTranscript();
                es.sendData(ourText.toSSEForm());
            }
        }
    }
}
