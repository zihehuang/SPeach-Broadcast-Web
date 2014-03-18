package controllers;

import com.fasterxml.jackson.databind.JsonNode;
import models.SharedTranscript;
import models.UpdateMessenger;
import models.ViewerUpdateMessenger;
import play.mvc.*;
import playextension.EditorEventSource;
import playextension.EventSource;
import playextension.ViewerEventSource;

public class Application extends Controller {

    public static Result index() {
        return ok(views.html.index.render());
    }

    public static Result editTranscript() {
        return ok(views.html.volunteer.render());
    }

    public static Result viewTranscript() {
        return ok(views.html.viewTranscript.render());
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
    }

    public static Result getTranscriptData() {
        return ok(new EventSource() {
            @Override
            public void onConnected() {
                ViewerUpdateMessenger.singleton.tell(this, null);
            }
        });
    }

    public static Result modifyOption() {
//        Http.RequestBody body = request().body();
//        JsonNode jsonNode = body.asJson();
//
//        int utteranceIndex = jsonNode.get(0).asInt();
//        int optionIndex = jsonNode.get(1).asInt();
//        String newValue = jsonNode.get(2).asText();
        Http.RequestBody body = request().body();
        String textBody = body.asText();
        if (null == textBody) {
            textBody = "";
        }
        SharedTranscript ourText = SharedTranscript.getOnlySharedTranscript();

        ourText.modifySharedTranscript(textBody);

        return ok();
    }

    public static Result upvoteOption() {
        return ok();
    }

    public static Result speaker() {
        return ok(views.html.speaker.render());
    }

}
