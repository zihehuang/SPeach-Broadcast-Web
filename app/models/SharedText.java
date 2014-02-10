package models;

import play.db.ebean.Model;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Class that represents a shared transcript in the database.
 */
@Entity
public class SharedText extends Model {
    @Id
    private long id;

    @ManyToMany
    private List<Utterance> sharedText = new ArrayList<Utterance>();

    public static Finder<Long, SharedText> find = new Finder<Long, SharedText>(Long.class, SharedText.class);

    public SharedText() {
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

    public List<Utterance> getSharedText() {
        return this.sharedText;
    }

    public String getSharedTextJSON() {
        List<Utterance> sharedText = getSharedText();

        StringBuilder sb = new StringBuilder();
        sb.append("[");

        for (int i = 0; i < sharedText.size(); i++) {
            sb.append(sharedText.get(i).toString());
            // if it is the last element, don't add a comma.
            if (i < sharedText.size() - 1) {
                sb.append(",");
            }
        }
        sb.append("]");

        return sb.toString();
    }

    public String getSSESharedText() {
        return "retry: 500\ndata: " + getSharedTextJSON() + "\n\n";
    }

    public void addToSharedText(String toAdd) {
        Utterance addedUtterance = Utterance.create(toAdd);
        this.sharedText.add(addedUtterance);
        this.save();
    }

}
