package models;

import play.db.ebean.Model;
import play.db.ebean.Model.Finder;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

@Entity
public class Vote extends Model {
	@Id
	private long id;
	
	@ManyToOne
	private Option parent;
	
	private String ipAddress;
	
	/**
   * Constructor for Option.
   * @param text Takes in default String.
   * @param parent The utterance that this option belongs to.
   */
  public Vote(String ipAddress, Option parent) {
      this.ipAddress = ipAddress;
      this.parent = parent;
  }
	
	/**
   * Finder for votes, using the IP
   */
  public static Finder<String, Vote> find = new Finder<String, Vote>(String.class, Vote.class);
  
  /**
   * Static helper for creating a vote and saving it in the database.
   * @param ip The IP to initialize the option with.
   * @return The option that is created.
   */
  public static Vote create(String ip, Option parent) {
      Vote newVote = new Vote(ip, parent);
      newVote.save();
      return newVote;
  }
  
  /**
   * change the Option a client wants to vote to
   */
  public void changeVote(Option newOption) {
  	this.parent = newOption;
  }
  

}
