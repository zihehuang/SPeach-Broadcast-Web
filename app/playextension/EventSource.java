package playextension;

/*
 * Modified version of play.libs.EventSource to not send escaped strings.
 *
 * Copyright (C) 2009-2013 Typesafe Inc. <http://www.typesafe.com>
 */

import org.apache.commons.lang3.StringEscapeUtils;
import play.libs.F;
import play.mvc.Results;

/**
 * Implementation of Server-Sent Events.
 * @see <a href="http://dev.w3.org/html5/eventsource/">Server-Sent Events specification</a>
 */
public abstract class EventSource extends Results.Chunks<String> {
    private Results.Chunks.Out<String> out;

    /**
     * Create a new EventSource socket
     *
     */
    public EventSource() {
        super(play.core.j.JavaResults.writeString("text/event-stream", play.api.mvc.Codec.javaSupported("utf-8")));
    }

    public void onReady(Results.Chunks.Out<String> out) {
        this.out = out;
        onConnected();
    }

    /**
     * A single event source can generate different types events by including an event name. On the client, an event listener can be
     * setup to listen to that particular event.
     *
     * @param eventName Unique name of the event.
     * @param data data associated with event
     * @deprecated Replaced by send
     */
    public void sendDataByName(String eventName, String data) {
        out.write("event: " + eventName + "\r\n"
                + "data: " + StringEscapeUtils.escapeEcmaScript(data) + "\r\n\r\n");
    }

    /**
     * Setting an ID lets the browser keep track of the last event fired so that if, the connection to the server is dropped,
     * a special HTTP header (Last-Event-ID) is set with the new request. This lets the browser determine which event is
     * appropriate to fire.
     *
     * @param eventId Unique event id.
     * @param data data associated with event
     */
    public void sendDataById(String eventId, String data) {
        out.write("id: " + eventId + "\r\n"
                + "data: " + StringEscapeUtils.escapeEcmaScript(data) + "\r\n\r\n");

    }

    /**
     * Sending a generic event. On the client, 'message' event listener can be setup to listen to this event.
     *
     * @param data data associated with event
     */
    public void sendData(String data) {
        out.write("data: " + data + "\r\n\r\n");
    }

    /**
     * The socket is ready, you can start sending messages.
     */
    public abstract void onConnected();

    /**
     * Add a callback to be notified when the client has disconnected.
     */
    public void onDisconnected(F.Callback0 callback) {
        out.onDisconnected(callback);
    }

    /**
     * Close the channel
     */
    public void close() {
        out.close();
    }

}
