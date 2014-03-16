package models;

import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.UntypedActor;
import play.Logger;
import play.libs.Akka;
import play.libs.F;
import playextension.EventSource;

import java.util.ArrayList;
import java.util.List;

/**
 * Actor that relays updates to transcript viewers.
 */
public class ViewerUpdateMessenger extends UntypedActor {

    /**
     * Singleton class to access the single update messenger.
     */
    public static ActorRef singleton = Akka.system().actorOf(Props.create(ViewerUpdateMessenger.class));

    /**
     * List of editor connections.
     */
    private List<EventSource> viewerSockets = new ArrayList<EventSource>();

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
            if (viewerSockets.contains(eventSource)) {
                // conection needs to be removed.
                viewerSockets.remove(eventSource);
                Logger.info("Browser disconnected (" + viewerSockets.size() + " viewers currently connected)");
            } else {
                // connection needs to be added.
                eventSource.onDisconnected(new F.Callback0() {
                    @Override
                    public void invoke() throws Throwable {
                        getContext().self().tell(eventSource, null);
                    }
                });
                viewerSockets.add(eventSource);

                SharedTranscript ourText = SharedTranscript.getOnlySharedTranscript();
                eventSource.sendData(ourText.toSSEForm());

                Logger.info("New browser connected (" + viewerSockets.size() + " viewers currently connected)");
            }
        }
        // if the actor needs to send out an update to connected clients, do so.
        if ("UPDATE".equals(message)) {
            ArrayList<EventSource> shallowCopy = new ArrayList<EventSource>(viewerSockets);
            for (EventSource es : shallowCopy) {
                SharedTranscript ourText = SharedTranscript.getOnlySharedTranscript();
                es.sendData(ourText.toSSEForm());
            }
        }
    }

}
