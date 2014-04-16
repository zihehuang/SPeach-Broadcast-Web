package models;

/**
 * Class that represents the desire to update the clients with a server sent event for a particular session.
 */
public class UpdateRequest {

    private String sessionId;

    public String getSessionId() {
        return sessionId;
    }

    public UpdateRequest(String sessionId) {
        this.sessionId = sessionId;
    }

}
