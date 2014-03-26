package models;

import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.UntypedActor;
import play.Logger;
import play.libs.Akka;
import play.libs.F.Callback0;

import java.util.ArrayList;
import java.util.List;

import playextension.EditorEventSource;
import playextension.EventSource;
import playextension.ViewerEventSource;

/**
 * Actor that relays messages to the clients.
 */
public class UpdateMessenger extends UntypedActor {

    /**
     * Singleton class to access the single update messenger.
     */
    public static ActorRef singleton = Akka.system().actorOf(Props.create(UpdateMessenger.class));

    /**
     * List of editor connections.
     */
    private List<EventSource> editorSockets = new ArrayList<EventSource>();

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
            if (editorSockets.contains(eventSource)) {
                // conection needs to be removed.
                editorSockets.remove(eventSource);
                Logger.info("Browser disconnected (" + editorSockets.size() + " browsers currently connected)");
            } else {
                // connection needs to be added.
                eventSource.onDisconnected(new Callback0() {
                    @Override
                    public void invoke() throws Throwable {
                        getContext().self().tell(eventSource, null);
                    }
                });
                editorSockets.add(eventSource);

                SharedTranscript ourText = SharedTranscript.getOnlySharedTranscript();
                eventSource.sendData(ourText.toSSEForm().replace("\n", "\t"));

                Logger.info("New browser connected (" + editorSockets.size() + " browsers currently connected)");
            }
        }
        // if the actor needs to send out an update to connected clients, do so.
        if ("UPDATE".equals(message)) {
            List<EventSource> shallowCopy = new ArrayList<EventSource>(editorSockets); //prevent ConcurrentModificationException

            SharedTranscript ourText = SharedTranscript.getOnlySharedTranscript();
            String textToAdd = ourText.getTextToAdd();

            int indexToHelp = ourText.getIndexToHelp();

            if (indexToHelp != -1) {
                textToAdd += "###" + indexToHelp;
            }

            for(EventSource es: shallowCopy) {
                es.sendData(textToAdd);
            }

            ourText.clearTextToAdd();

            ViewerUpdateMessenger.singleton.tell("UPDATE", getSender());
        }
    }
}
