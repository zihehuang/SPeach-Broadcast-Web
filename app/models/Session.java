package models;

import play.db.ebean.Model;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

/**
 * Represents a broadcasting session
 */
@Entity
public class Session extends Model {

    /**
     * Id of the shared transcript in the database.
     */
    @Id
    private long id;

    /**
     * The name of the session.
     */
    private String name;

    /**
     * The transcript that belongs to this session.
     */
    @ManyToOne
    private SharedTranscript transcript;

    /**
     * Finder for the Session model.
     */
    public static Finder<Long, Session> find = new Finder<Long, Session>(Long.class, Session.class);

    /**
     * Constructor for session.
     * @param name The name of the session.
     * @param transcript the transcript to initialize this session with.
     */
    public Session(String name, SharedTranscript transcript) {
        this.name = name;
        this.transcript = transcript;
    }

    /**
     * Static creator for sessions; handles database saving.
     * @param name The name of the session being created.
     * @return The newly created session.
     */
    public static Session create(String name) {
        SharedTranscript transcript = SharedTranscript.create();

        Session session = new Session(name, transcript);
        session.save();

        return session;
    }

}
