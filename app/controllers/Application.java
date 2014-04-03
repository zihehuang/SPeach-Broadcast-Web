package controllers;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;

import models.RawUtterance;
import models.SharedTranscript;
import models.UpdateMessenger;
import models.ViewerUpdateMessenger;
upstream/request-help
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


        String toAdd = "";
        if (!ourText.getTranscript().equals("")) {
            toAdd = "\t";
        }

        // set the confidence levels
        if (confidence > .9) {
            ourText.addToSharedTranscript(toAdd+text);
        }
        else if (confidence > .8) {
            ourText.addToSharedTranscript(toAdd+"*"+text);
        }
        else {
            ourText.addToSharedTranscript(toAdd+"**"+text);
        }

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

    /**
     * create a vote ballot for an Option by requesting the client IP
     * Ensure there's one option vote per utterance per IP
     * @param optionId ID for an Option 
     * @return
     */
    public static Result upvoteOption(long optionId) {
    		String ip = request().remoteAddress();
    		
    		// find out all the votes the client has casted
    		List<Vote> votesForIp = Vote.findByIP(ip);
    		Option optionToUpvote = Option.find.byId(optionId);
    		Long utterOfOption = optionToUpvote.getUtter().getId();
    		
    		// gather all votes by this client for this utterance
    		// if zero votes, create a new vote, increment the option 
    		// else go through all votes, 
    		//   verify the optionId, 
    		//    if success, make valid, done, return
    		//		if fails, mark it invalid, decrement the associated option
    		// verification complete, no valid votes for this utterance, create a new vote
    		
    		List<Vote> votesOfUtter = new ArrayList<Vote>();
    		for (Vote vote : votesForIp) {
    			if (vote.getOption().getUtter().getId() == utterOfOption) {
    				votesOfUtter.add(vote);
    			}
    		}
    		// saved for debugging purpose
    		/*if (votesOfUtter.size() == 0) {
    			Vote.create(ip, optionToUpvote);
    			optionToUpvote.increment();
    			return ok();
    		}else{*/
    		if (votesOfUtter.size() != 0) {
	    		for (Vote vote : votesOfUtter) {
	    			if (vote.getOption().getId() == optionToUpvote.getId()) {
	    				if (vote.getValid() == true) {
	    					return ok(); // method ends here if verification complete
	    				}else if (vote.getValid() == false) {
	    					vote.setValid(true);
	    					optionToUpvote.increment();
	    				}
	    			}else if (vote.getOption().getId() != optionToUpvote.getId()) {
	    				vote.setValid(false);
	    				vote.getOption().decrement();
	    			}
	    		}
    		}
    		// only reaches here when all votes are flagged invalid for this utterance
    		// create a new vote
    		Vote.create(ip, optionToUpvote);
    		optionToUpvote.increment();
    		
        return ok();
    }

    public static Result speaker() {
        return ok(views.html.speaker.render());
    }

}
