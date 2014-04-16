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
import java.util.concurrent.ConcurrentHashMap;

/**
 * Actor that relays updates to transcript viewers.
 */
public class ViewerUpdateMessenger extends UntypedActor {

    /**
     * Singleton class to access the single update messenger.
     */
    public static ActorRef singleton = Akka.system().actorOf(Props.create(ViewerUpdateMessenger.class));

    /**
     * Maps a session id to a list of corresponding Event Sources.
     */
    private ConcurrentHashMap<String, List<EventSource>> sessionIdToESListMap = new ConcurrentHashMap<String, List<EventSource>>();

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
            String sessionId = request.getSessionId();

            List<EventSource> viewerSockets = null;
            if (sessionIdToESListMap.containsKey(sessionId)) {
                viewerSockets = sessionIdToESListMap.get(sessionId);
            }
            else {
                viewerSockets = new ArrayList<EventSource>();
            }

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
                sessionIdToESListMap.put(sessionId, viewerSockets);

                SharedTranscript ourText = SharedTranscript.findBySessionId(sessionId);
                eventSource.sendData(ourText.getViewerText());

                Logger.info("New browser connected (" + viewerSockets.size() + " viewers currently connected)");
            }
        }
        // if the actor needs to send out an update to connected clients, do so.
        if (message instanceof UpdateRequest) {
            UpdateRequest request = (UpdateRequest) message;
            String sessionId = request.getSessionId();

            // if there are viewers, then udpate them.
            if (sessionIdToESListMap.containsKey(sessionId)) {
                List<EventSource> viewerSockets = sessionIdToESListMap.get(sessionId);

                ArrayList<EventSource> shallowCopy = new ArrayList<EventSource>(viewerSockets);
                for (EventSource es : shallowCopy) {
                    SharedTranscript ourText = SharedTranscript.findBySessionId(sessionId);
                    es.sendData(ourText.getViewerText());
                }
            }

        }
    }

}
