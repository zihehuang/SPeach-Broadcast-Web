package models;

/**
 * Class that represents the desire to update the clients with a server sent event for a particular session.
 */
public class UpdateRequest {

    private long sessionId;

    public long getSessionId() {
        return sessionId;
    }

    public UpdateRequest(long sessionId) {
        this.sessionId = sessionId;
    }

}
