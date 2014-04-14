package models;

import playextension.EventSource;

/**
 * Class that represents the desire to be registered for an event source for a certain sessionId.
 */
public class EventSourceRegisterRequest {

    private EventSource requestSource;

    public long getSessionId() {
        return sessionId;
    }

    public EventSource getRequestSource() {
        return requestSource;
    }

    private long sessionId;

    public EventSourceRegisterRequest(EventSource eventSource, long sessionId) {
        this.requestSource = eventSource;
        this.sessionId = sessionId;
    }

}
