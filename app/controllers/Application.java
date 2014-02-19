package controllers;

import com.fasterxml.jackson.databind.JsonNode;

import models.Option;
import models.SharedTranscript;
import models.UpdateMessenger;
import models.Vote;
import play.mvc.*;
import playextension.EventSource;

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

    public static Result upvoteOption(Long optionId) {
    		String ip = request().remoteAddress();
    		
    		
    		List<Vote> votesForIp = Vote.findByIP(ip);
    		for (Vote vote : votesForIp) {
    			if vote.getOption().getId() == optionId) {
    				// person has already voted. stopit. return something.
    			}
    		}
    		
    		// if the person has not voted yet.
    		Option optionToUpvote = Option.find.byId(optionId);
    		optionToUpvote.addVote(ip)
    		
        return ok();
    }

}
