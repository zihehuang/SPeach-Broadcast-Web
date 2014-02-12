package controllers;

import com.fasterxml.jackson.databind.JsonNode;
import models.SharedTranscript;
import play.mvc.*;

public class Application extends Controller {

    public static Result index() {
        return ok(views.html.index.render());
    }

    public static Result addUtterance() {
        Http.RequestBody body = request().body();
        String textBody = body.asText();
        if (null == textBody) {
            textBody = "";
        }
        SharedTranscript ourText = SharedTranscript.find.byId((long)1);
        if (ourText == null) {
            SharedTranscript.create();
        }
        ourText = SharedTranscript.find.byId((long)1);
        ourText.addToSharedTranscript(textBody);

        return ok();
    }

    public static Result getUtterances() {
        response().setContentType("text/event-stream");
        SharedTranscript ourText = SharedTranscript.find.byId((long)1);
        if (ourText == null) {
            SharedTranscript.create();
        }
        ourText = SharedTranscript.find.byId((long)1);

        return ok(ourText.toSSEForm());
    }

    public static Result modifyOption() {
        Http.RequestBody body = request().body();
        JsonNode jsonNode = body.asJson();

        int index = jsonNode.get(0).asInt();
        String newValue = jsonNode.get(1).asText();

        SharedTranscript ourText = SharedTranscript.find.byId((long)1);
        if (ourText == null) {
            SharedTranscript.create();
        }
        ourText = SharedTranscript.find.byId((long)1);

        ourText.modifySharedTranscript(index, newValue);

        return ok();
    }

    public static Result upvoteOption() {
        return ok();
    }

}
