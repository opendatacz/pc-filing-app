package models;


import javax.persistence.*;

import play.data.validation.Constraints.Required;
import play.db.ebean.Model;


/**
 * Holds user information, such as email and user preferences.<br>
 * 
 * @author Jan Cerny
 * 
 */
@Entity
@Table(name="users")
public class User extends Model {
	@Id
	public Long id;
	
	@Required
	public String email;
	@Required
	public String password;
	
	public int role;	
	
	/**
	 * Validates the user entry
	 * @return
	 */
	public String validate() {
		return null;
	}
	
	public static Finder<Long,User> find = new Finder<Long,User>(
		    Long.class, User.class
	); 	
}
