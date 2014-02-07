package models;

import play.db.ebean.Model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

/**
 * Created by aaron on 2/7/14.
 */
@Entity
public class SharedText extends Model {
    @Id
    private long id;

    @Column(columnDefinition = "TEXT")
    private String sharedText;

    public static Finder<Long, SharedText> find = new Finder<Long, SharedText>(Long.class, SharedText.class);

    public SharedText() {
        this.sharedText = "";
    }

    public static SharedText create() {
        SharedText newText = new SharedText();
        newText.save();
        return newText;
    }

    public static boolean sharedTextExists() {
        if (find.all().size() == 0 ) {
            return false;
        }
        else {
            return true;
        }
    }

    public String getSharedText() {
        return this.sharedText;
    }

    public String getSSESharedText() {
        return "data: " + getSharedText() + "\n\n";
    }

    public void addToSharedText(String toAdd) {
        this.sharedText += " " + toAdd;
        this.save();
    }

}
