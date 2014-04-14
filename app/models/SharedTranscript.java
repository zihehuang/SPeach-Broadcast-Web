package models;

import play.db.ebean.Model;

import javax.persistence.*;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

/**
 * Class that represents a shared transcript in the database.
 */
@Entity
public class SharedTranscript extends Model {

    /**
     * Id of the shared transcript in the database.
     */
    @Id
    private long id;

    /**
     * The list of utterances in the shared transcript.
     */
    @Column(columnDefinition = "TEXT")
    private String transcript;

    /**
     * The text that needs to be added to the transcript (from the speaker's phone).
     */
    @Column(columnDefinition = "TEXT")
    private String toAdd;

    public int getIndexToHelp() {
        return indexToHelp;
    }

    /**
     * Index that needs help.
     */
    private int indexToHelp = -1;

    /**
     * Finder for the SharedTranscript model.
     */
    public static Finder<Long, SharedTranscript> find = new Finder<Long, SharedTranscript>(Long.class, SharedTranscript.class);

    /**
     * Gets the text to add to the transcript.
     * @return
     */
    public String getTextToAdd() {
        return toAdd.replace("\n", "\t");
    }

    /**
     * Clears the text to add for this transcript.
     */
    public void clearTextToAdd() {
        this.transcript += this.toAdd;
        this.toAdd = "";
        this.indexToHelp = -1;
        this.save();
    }

    /**
     * Constructor for shared transcript.
     */
    public SharedTranscript() {
        this.transcript = "";
        this.toAdd = "";
    }

    /**
     * Static method for encapsulating the creation and saving of a SharedTranscript.
     * @return A new Shared Transcript that is saved in the database.
     */
    public static SharedTranscript create() {
        SharedTranscript newText = new SharedTranscript();
        newText.save();
        return newText;
    }

    /**
     * Checks to see if a row exists in the shared transcript table.
     *
     * Method that will soon become legacy, because it operates on the assumption that there
     * is only one row in the SharedTranscript table.
     * @return Whether a row exists in the shared transcript table.
     */
    public static boolean sharedTextExists() {
        if (find.all().size() == 0 ) {
            return false;
        }
        else {
            return true;
        }
    }

    /**
     * Gets the list of utterances in this shared transcript.
     * @return The list of utterances.
     */
    public String getTranscript() {
        return this.transcript;
    }

    /**
     * Gets this SharedTranscript for client consumption.
     * @return Gets this SharedTranscript for client consumption.
     */
    public String toSSEForm() {
        return getTranscript().replace("\n", "\t");
    }

    /**
     * Gets this shared transcript as the viewer will see it. Includes potentials as well.
     * @return The shared transcript as the viewer will see it.
     */
    public String getViewerText() {
        String[] lines = getTranscript().split("\n");
        StringBuilder sb = new StringBuilder();
        String prefix = "";
        sb.append("[");
        for (String line : lines) {
            line = line.replace("\n", "\t");

            sb.append(prefix);
            sb.append("\"");
            sb.append(line);
            sb.append("\"");
            prefix = ",";
        }
        sb.append("]");
        return sb.toString();
    }

    /**
     * Adds new string to this SharedTranscript. Creates a new utterance in the database for this.
     * @param toAdd The text to add to the shared transcript.
     */
    public void addToSharedTranscript(String toAdd, long sessionId) {
        String[] splitToAdd = toAdd.split("===");

        if (splitToAdd.length == 1) {
            this.toAdd += toAdd;
            this.save();
            UpdateMessenger.singleton.tell(new UpdateRequest(sessionId), null);
        }

    }

    /**
     * Adds stars to utterances where the viewer needs help.
     * @param indexToHelpWith The index that the viewer needs help with.
     */
    public void requestHelp(int indexToHelpWith, long sessionId) {
        String[] lines = getTranscript().split("\n");
        lines[indexToHelpWith] = "**" + lines[indexToHelpWith];
        this.indexToHelp = indexToHelpWith;

        StringBuilder sb = new StringBuilder();
        String newTranscript = "";
        String prefix = "";
        for (String line : lines) {
            sb.append(prefix);
            sb.append(line);
            prefix = "\n";
        }

        this.transcript = sb.toString();

        this.save();

        UpdateMessenger.singleton.tell(new UpdateRequest(sessionId), null);
    }

    /**
     * Changes the value of the shared transcript at an index.
     * @param newSharedTranscript the new value for the shared transcript.
     */
    public void modifySharedTranscript(String newSharedTranscript, long sessionId) {
//        Utterance utteranceToChange = Utterance.find.byId((long) utteranceId);
//        utteranceToChange.changeText(optionId, newValue);
        this.transcript = newSharedTranscript;
        this.save();
        this.WriteToFile("final_transcript");

        ViewerUpdateMessenger.singleton.tell(new UpdateRequest(sessionId), null);
    }

    /**
     * For our demo, creates the only shared transcript if it does not exist and returns it.
     * @return The singular shared transcript in the database.
     */
    public static SharedTranscript getOnlySharedTranscript() {
        SharedTranscript ourText = SharedTranscript.find.byId((long)1);
        if (ourText == null) {
            SharedTranscript.create();
        }
        return SharedTranscript.find.byId((long)1);
    }

    /**
     * Static method that writes all the modification to a file.
     * @param filename The filename for the .csv file.
     */
    public static void WriteToFile(String filename) {
        PrintWriter writer;
        try {
            writer = new PrintWriter(filename+".csv", "UTF-8");
            writer.println(getOnlySharedTranscript().getTranscript());
            writer.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

}
