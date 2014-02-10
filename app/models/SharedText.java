package models;

import play.db.ebean.Model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.ArrayList;

/**
 * Class that represents a shared transcript in the database.
 */
@Entity
public class SharedText extends Model {
    @Id
    private long id;

    @Column(columnDefinition = "TEXT")
    private ArrayList<String> sharedText;

    public static Finder<Long, SharedText> find = new Finder<Long, SharedText>(Long.class, SharedText.class);

    public SharedText() {
        this.sharedText = new ArrayList<String>();
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

    public ArrayList<String> getSharedText() {
        return this.sharedText;
    }

    public String getSharedTextJSON() {
        ArrayList<String> sharedText = getSharedText();

        StringBuilder sb = new StringBuilder();
        sb.append("[");

        for (int i = 0; i < sharedText.size(); i++) {
            sb.append("\"");
            sb.append(sharedText.get(i));
            // if it is the last element, don't add a comma.
            if (i < sharedText.size() - 1) {
                sb.append("\",");
            }
            else {
                sb.append("\"");
            }
        }
        sb.append("]");

        return sb.toString();
    }

    public String getSSESharedText() {
        return "retry: 500\ndata: " + getSharedTextJSON() + "\n\n";
    }

    public void addToSharedText(String toAdd) {
        this.sharedText.add(toAdd);
        this.save();
    }

}
