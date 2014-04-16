package controllers;

<<<<<<< HEAD
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;

import models.RawUtterance;
import models.SharedTranscript;
import models.UpdateMessenger;
import models.ViewerUpdateMessenger;
upstream/request-help
=======
import models.*;
import models.forms.CreateSessionForm;
import models.forms.JoinSessionForm;
import play.data.Form;
>>>>>>> upstream/master
import play.mvc.*;
import playextension.EventSource;
import java.io.File;

import static play.data.Form.form;

public class Application extends Controller {

    public static Result index() {
        return ok(views.html.index.render(Form.form(CreateSessionForm.class), Form.form(JoinSessionForm.class)));
    }

    public static Result instructions() {
        return ok(views.html.instructions.render(Form.form(CreateSessionForm.class), Form.form(JoinSessionForm.class)));
    }

    public static Result downloadApp() {
        response().setContentType("application/x-download");
        response().setHeader("Content-disposition","attachment; filename=SPeachAPP.md");
        return ok(new File("public/android-app/SPeachAPP.md"));
    }

    public static Result newSession() {
        final Form<CreateSessionForm> filledForm = form(CreateSessionForm.class).bindFromRequest();

        if (filledForm.hasErrors()) {
            return badRequest(views.html.index.render(filledForm, Form.form(JoinSessionForm.class)));
        } else {
            Session session = Session.create(filledForm.get().getName());
            return redirect(routes.Application.viewTranscript(session.getId()));
        }
    }

    public static Result joinSession() {
        final Form<JoinSessionForm> filledForm = form(JoinSessionForm.class).bindFromRequest();

        if (filledForm.hasErrors()) {
            return badRequest(views.html.index.render(Form.form(CreateSessionForm.class), filledForm));
        } else {
            String sessionId = filledForm.get().getSessionId();
            Session session = Session.findById(sessionId);

            if (session == null) {
                return badRequest(views.html.index.render(Form.form(CreateSessionForm.class), filledForm));
            }
            else {
                return redirect(routes.Application.viewTranscript(sessionId));
            }
        }
    }

    public static Result editTranscript(String id) {
        if (Session.findById(id) == null) {
            return redirect(routes.Application.index());
        }
        return ok(views.html.editor.render(id));
    }

    public static Result viewTranscript(String id) {
        if (Session.findById(id) == null) {
            return redirect(routes.Application.index());
        }
        return ok(views.html.viewTranscript.render(id));
    }

    public static Result requestHelp(String id) {
        Http.RequestBody body = request().body();
        String textBody = body.asText();
        if (null == textBody) {
            textBody = "";
        }

        if (textBody.equals("")) {
            textBody = "0";
        }

        int indexToHelpWith = Integer.parseInt(textBody);
        SharedTranscript ourText = SharedTranscript.findBySessionId(id);

        ourText.requestHelp(indexToHelpWith, id);

        return ok();
    }

    public static Result addUtterance(String id) {
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

//        // write out to a file. the filename should be unique to each session
//        RawUtterance.WriteToFile("Utterances");

        SharedTranscript ourText = SharedTranscript.findBySessionId(id);

<<<<<<< HEAD

=======
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
>>>>>>> upstream/master
        String toAdd = "";
        if (!ourText.getTranscript().equals("")) {
            toAdd = "\t";
        }

<<<<<<< HEAD
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
=======
        ourText.addToSharedTranscript(toAdd+text, id);
>>>>>>> upstream/master

        return ok();
    }

    public static Result getUtterances(final String id) {
        response().setContentType("text/event-stream");
        SharedTranscript ourText = SharedTranscript.findBySessionId(id);

        return ok(new EventSource() {
            @Override
            public void onConnected() {
                UpdateMessenger.singleton.tell(new EventSourceRegisterRequest(this, id), null);
            }
        });
    }

    public static Result getTranscriptData(final String id) {
        return ok(new EventSource() {
            @Override
            public void onConnected() {
                ViewerUpdateMessenger.singleton.tell(new EventSourceRegisterRequest(this, id), null);
            }
        });
    }

    public static Result modifyOption(String id) {
        Http.RequestBody body = request().body();
        String textBody = body.asText();
        if (null == textBody) {
            textBody = "";
        }
        SharedTranscript ourText = SharedTranscript.findBySessionId(id);

        ourText.modifySharedTranscript(textBody, id);

        return ok();
    }

<<<<<<< HEAD
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
    		
=======
    public static Result upvoteOption(String id) {
>>>>>>> upstream/master
        return ok();
    }

    public static Result speaker(String id) {
        return ok(views.html.speaker.render());
    }

}
