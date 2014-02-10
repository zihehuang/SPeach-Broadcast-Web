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

    public static Result addUtterance() {
        Http.RequestBody body = request().body();
        String textBody = body.asText();
        if (null == textBody) {
            textBody = "";
        }
        SharedText ourText = SharedText.find.byId((long)1);
        if (ourText == null) {
            SharedText.create();
        }
        ourText = SharedText.find.byId((long)1);
        ourText.addToSharedText(textBody);

        return ok();
    }

    public static Result getUtterances() {
        response().setContentType("text/event-stream");
        SharedText ourText = SharedText.find.byId((long)1);
        if (ourText == null) {
            SharedText.create();
        }
        ourText = SharedText.find.byId((long)1);

        return ok(ourText.getSSESharedText());
    }

    public static Result modifyOption() {
        return ok();
    }

    public static Result upvoteOption() {
        return ok();
    }

}
