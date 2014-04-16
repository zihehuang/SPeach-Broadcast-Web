package models.forms;

import play.data.validation.Constraints;

/**
 * Class that represents the form for creating a session.
 */
public class CreateSessionForm {
    public String getName() {
        return name;
    }

    @Constraints.Required
    public String name;
}
