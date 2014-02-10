package models;

import play.db.ebean.Model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

/**
 * Class that represents a single utterance in a shared transcript.
 */
@Entity
public class Utterance extends Model {
    @Id
    private long id;

    @Column(columnDefinition = "TEXT")
    private String text;

    public static Finder<Long, Utterance> find = new Finder<Long, Utterance>(Long.class, Utterance.class);

    public Utterance(String text) {
        this.text = text;
    }

    public static Utterance create(String text) {
        Utterance utterance = new Utterance(text);
        utterance.save();
        return utterance;
    }

    public String toString() {
        return "\"" + this.text + "\"";
    }
}
