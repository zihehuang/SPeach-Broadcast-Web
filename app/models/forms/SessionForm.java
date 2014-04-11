package models.forms;

import play.data.validation.Constraints;

/**
 * Class that represents a session form.
 */
public class SessionForm {
    @Constraints.Required
    public String name;
}
