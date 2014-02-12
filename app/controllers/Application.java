package controllers;

import com.fasterxml.jackson.databind.JsonNode;
import models.SharedTranscript;
import models.UpdateMessenger;
import play.libs.EventSource;
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
        SharedTranscript ourText = SharedTranscript.getOnlySharedTranscript();
        ourText.addToSharedTranscript(textBody);

        return ok();
    }

    public static Result getUtterances() {
        response().setContentType("text/event-stream");
        SharedTranscript ourText = SharedTranscript.getOnlySharedTranscript();

        return ok(new EventSource() {
            @Override
            public void onConnected() {
                UpdateMessenger.singleton.tell(this, null);
            }
        });
//        return ok(ourText.toSSEForm());
    }

    public static Result modifyOption() {
        Http.RequestBody body = request().body();
        JsonNode jsonNode = body.asJson();

        int index = jsonNode.get(0).asInt();
        String newValue = jsonNode.get(1).asText();

        SharedTranscript ourText = SharedTranscript.getOnlySharedTranscript();

        ourText.modifySharedTranscript(index, newValue);

        return ok();
    }

    public static Result upvoteOption() {
        return ok();
    }

}
