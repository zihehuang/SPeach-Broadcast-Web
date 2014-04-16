package models.forms;

import play.data.validation.Constraints;

/**
 * Class that represents the form for joining a session.
 */
public class JoinSessionForm {
    public String getSessionId() {
        return sessionId;
    }

    @Constraints.Required
    public String sessionId;
}
