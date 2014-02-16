package models;

import org.apache.commons.lang3.StringEscapeUtils;
import play.db.ebean.Model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

/**
 * Class that represents a single utterance in a shared transcript.
 */
@Entity
public class Utterance extends Model {
    /**
     * Id of the utterance, in the database.
     */
    @Id
    private long id;

    /**
     * The text of the utterance.
     */
    @Column(columnDefinition = "TEXT")
    private String text;

    /**
     * Finder for utterances.
     */
    public static Finder<Long, Utterance> find = new Finder<Long, Utterance>(Long.class, Utterance.class);

    /**
     * Constructor for utterance. Takes in the text that it should be initialized with.
     * @param text The text for the utterance to be initialized with.
     */
    public Utterance(String text) {
        this.text = text;
    }

    /**
     * Static helper that creates a new utterance and saves it.
     * @param text The text that the utterance should be initialized with.
     * @return The newly created utterance.
     */
    public static Utterance create(String text) {
        Utterance utterance = new Utterance(text);
        utterance.save();
        return utterance;
    }

    /**
     * Returns the utterance wrapped with quotation marks.
     * @return The String representation of the utterance, which is the text wrapped with quotation marks.
     */
    public String toString() {
        return "\"" + this.text + "\"";
    }

    /**
     * Gets the utterance wrapped with quotation marks, with the inner text escaped.
     * @return The String representation of the utterance with characters escaped.
     */
    public String toEscapedString() {
        return "\"" + StringEscapeUtils.escapeEcmaScript(this.text) + "\"";
    }

    /**
     * Gets the String of the utterance as a key:pair in a JSON dict.
     * @return The String of the utterance as a key:pair entry in a JSON dict.
     */
    public String toJSONEntry() {
        return "\""+this.id+"\":{\"upvotes\":"+1+",\"text\":"+this.toString()+"}";
    }

    /**
     * Changes the value of the utterance.
     * @param newValue The value to replace the old with.
     */
    public void changeText(String newValue) {
        this.text = newValue;
        this.save();
    }
}
