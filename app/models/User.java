package models;

import play.data.validation.Constraints.Required;


/**
 * Holds user information, such as email and user preferences.<br>
 * 
 * @author Jan Cerny
 * 
 */
public class User {
	@Required
	public String email;
	@Required
	public String password;
	@Required
	public int role;	
	
	/**
	 * Validates the user entry
	 * @return
	 */
	public String validate() {
		return null;
	}
	
}
