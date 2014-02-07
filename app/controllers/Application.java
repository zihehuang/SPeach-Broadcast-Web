package controllers;

import models.SharedText;
import play.*;
import play.data.DynamicForm;
import play.data.Form;
import play.mvc.*;

import views.html.*;

public class Application extends Controller {

    public static Result index() {
        return ok(index.render());
    }

    public static Result receiveData() {
        DynamicForm data = Form.form().bindFromRequest();
        SharedText ourText = SharedText.find.byId((long)1);
        if (ourText != null) {
            SharedText.create();
        }
        ourText = SharedText.find.byId((long)1);
        ourText.addToSharedText(data.toString());

        return ok();
    }

    public static Result getData() {
        SharedText ourText = SharedText.find.byId((long)1);
        if (ourText != null) {
            SharedText.create();
        }
        ourText = SharedText.find.byId((long)1);

        return ok(ourText.getSSESharedText());
    }

}
