package models;

import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.UntypedActor;
import play.Logger;
import play.libs.Akka;
import play.libs.F.Callback0;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

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
     * Maps a session id to a list of corresponding Event Sources.
     */
    private ConcurrentHashMap<Long, List<EventSource>> sessionIdToESListMap = new ConcurrentHashMap<Long, List<EventSource>>();

    /**
     * The actions the messenger takes when it receives an update.
     * @param message The message that the messenger receives
     * @throws Exception
     */
    @Override
    public void onReceive(Object message) throws Exception {
        if (message instanceof EventSourceRegisterRequest) {
            EventSourceRegisterRequest request = (EventSourceRegisterRequest) message;
            final EventSource eventSource = request.getRequestSource();
            long sessionId = request.getSessionId();


            List<EventSource> editorSockets = null;
            if (sessionIdToESListMap.containsKey(sessionId)) {
                editorSockets = sessionIdToESListMap.get(sessionId);
            }
            else {
                editorSockets = new ArrayList<EventSource>();
            }


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
                sessionIdToESListMap.put(sessionId, editorSockets);

                SharedTranscript ourText = SharedTranscript.find.byId(sessionId);
                eventSource.sendData(ourText.toSSEForm().replace("\n", "\t"));

                Logger.info("New browser connected (" + editorSockets.size() + " browsers currently connected)");
            }
        }
        // if the actor needs to send out an update to connected clients, do so.
        if (message instanceof UpdateRequest) {
            UpdateRequest request = (UpdateRequest) message;
            long sessionId = request.getSessionId();

            List<EventSource> editorSockets = sessionIdToESListMap.get(sessionId);

            List<EventSource> shallowCopy = new ArrayList<EventSource>(editorSockets); //prevent ConcurrentModificationException

            SharedTranscript ourText = SharedTranscript.find.byId(sessionId);
            String textToAdd = ourText.getTextToAdd();

            int indexToHelp = ourText.getIndexToHelp();

            if (indexToHelp != -1) {
                textToAdd += "###" + indexToHelp;
            }

            for(EventSource es: shallowCopy) {
                es.sendData(textToAdd);
            }

            ourText.clearTextToAdd();

            ViewerUpdateMessenger.singleton.tell(new UpdateRequest(sessionId), getSender());
        }
    }
}
