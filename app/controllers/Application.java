package controllers;

import com.fasterxml.jackson.databind.JsonNode;
import models.RawUtterance;
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

    public static Result requestHelp() {
        Http.RequestBody body = request().body();
        String textBody = body.asText();
        if (null == textBody) {
            textBody = "";
        }

        if (textBody.equals("")) {
            textBody = "0";
        }

        int indexToHelpWith = Integer.parseInt(textBody);
        SharedTranscript ourText = SharedTranscript.getOnlySharedTranscript();

        ourText.requestHelp(indexToHelpWith);

        return ok();
    }

    public static Result addUtterance() {
        Http.RequestBody body = request().body();
        String textBody = body.asText();
        if (null == textBody) {
            textBody = "";
        }

        // pull out the text and confidence from the sent string.
        String[] textAndConfidence = textBody.split("&&&");
        String text = textAndConfidence[0];
        Double confidence = Double.parseDouble(textAndConfidence[1]);

        // create the raw utterance in the database.
        RawUtterance.create(text, confidence);

        // write out to a file. the filename should be unique to each session
        RawUtterance.WriteToFile("Utterances");

        SharedTranscript ourText = SharedTranscript.getOnlySharedTranscript();

//        // set the confidence levels
//        if (confidence > .9) {
//            ourText.addToSharedTranscript(text+"\t");
//        }
//        else if (confidence > .8) {
//            ourText.addToSharedTranscript("*"+text+"\t");
//        }
//        else {
//            ourText.addToSharedTranscript("**"+text+"\t");
//        }
        String toAdd = "";
        if (!ourText.getTranscript().equals("")) {
            toAdd = "\t";
        }

        ourText.addToSharedTranscript(toAdd+text);

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
