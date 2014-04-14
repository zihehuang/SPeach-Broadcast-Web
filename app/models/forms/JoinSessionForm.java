package models.forms;

import play.data.validation.Constraints;

/**
 * Class that represents the form for joining a session.
 */
public class JoinSessionForm {
    public long getSessionId() {
        return sessionId;
    }

    @Constraints.Required
    public long sessionId;
}
