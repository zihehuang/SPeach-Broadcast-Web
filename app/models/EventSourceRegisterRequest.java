package models;

import playextension.EventSource;

/**
 * Class that represents the desire to be registered for an event source for a certain sessionId.
 */
public class EventSourceRegisterRequest {

    private EventSource requestSource;

    public String getSessionId() {
        return sessionId;
    }

    public EventSource getRequestSource() {
        return requestSource;
    }

    private String sessionId;

    public EventSourceRegisterRequest(EventSource eventSource, String sessionId) {
        this.requestSource = eventSource;
        this.sessionId = sessionId;
    }

}
