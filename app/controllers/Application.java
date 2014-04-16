package controllers;

import models.*;
import models.forms.CreateSessionForm;
import models.forms.JoinSessionForm;
import play.data.Form;
import play.mvc.*;
import playextension.EventSource;

import static play.data.Form.form;

public class Application extends Controller {

    public static Result index() {
        return ok(views.html.index.render(Form.form(CreateSessionForm.class), Form.form(JoinSessionForm.class)))    ;
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
            long sessionId = filledForm.get().getSessionId();
            Session session = Session.find.byId(sessionId);

            if (session == null) {
                return badRequest(views.html.index.render(Form.form(CreateSessionForm.class), filledForm));
            }
            else {
                return redirect(routes.Application.viewTranscript(sessionId));
            }
        }
    }

    public static Result editTranscript(Long id) {
        return ok(views.html.volunteer.render(id));
    }

    public static Result viewTranscript(Long id) {
        return ok(views.html.viewTranscript.render(id));
    }

    public static Result requestHelp(Long id) {
        Http.RequestBody body = request().body();
        String textBody = body.asText();
        if (null == textBody) {
            textBody = "";
        }

        if (textBody.equals("")) {
            textBody = "0";
        }

        int indexToHelpWith = Integer.parseInt(textBody);
        SharedTranscript ourText = SharedTranscript.find.byId(id);

        ourText.requestHelp(indexToHelpWith, id);

        return ok();
    }

    public static Result addUtterance(Long id) {
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

        SharedTranscript ourText = SharedTranscript.find.byId(id);

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

        ourText.addToSharedTranscript(toAdd+text, id);

        return ok();
    }

    public static Result getUtterances(final Long id) {
        response().setContentType("text/event-stream");
        SharedTranscript ourText = SharedTranscript.find.byId(id);

        return ok(new EventSource() {
            @Override
            public void onConnected() {
                UpdateMessenger.singleton.tell(new EventSourceRegisterRequest(this, id), null);
            }
        });
    }

    public static Result getTranscriptData(final Long id) {
        return ok(new EventSource() {
            @Override
            public void onConnected() {
                ViewerUpdateMessenger.singleton.tell(new EventSourceRegisterRequest(this, id), null);
            }
        });
    }

    public static Result modifyOption(Long id) {
        Http.RequestBody body = request().body();
        String textBody = body.asText();
        if (null == textBody) {
            textBody = "";
        }
        SharedTranscript ourText = SharedTranscript.find.byId(id);

        ourText.modifySharedTranscript(textBody, id);

        return ok();
    }

    public static Result upvoteOption(Long id) {
        return ok();
    }

    public static Result speaker(Long id) {
        return ok(views.html.speaker.render());
    }

}
