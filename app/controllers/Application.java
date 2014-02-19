package controllers;

import java.security.SecureRandom;
import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;

import com.avaje.ebean.Ebean;
import com.ning.http.util.Base64;

import models.User;
import models.UserLogin;
import play.db.*;
import cz.opendata.tenderstats.Mailer;
import play.*;
import play.data.Form;
import play.data.validation.Constraints.Required;
import play.mvc.*;
import views.html.*;
import play.mvc.*;
import play.data.*;
import static play.data.Form.*;
import play.data.validation.Constraints.*;

import org.mindrot.jbcrypt.BCrypt;

public class Application extends Controller {

	public static Form<User> registerForm = Form.form(User.class);
	public static Form<UserLogin> loginForm = Form.form(UserLogin.class);
    public static Result index() {
    	System.out.println("Index");
        return ok(index.render(form(User.class)));
    }
    
    /**
     * TODO - delete this
     * @return
     */
    public static Result registerUser() {
    	System.out.println("Register");
    	DataSource ds = DB.getDataSource();    	
    	Form<User> filledForm = registerForm.bindFromRequest();
    	User newUser = filledForm.get();
    	System.out.println("Registered user: " + newUser.email + ";" + newUser.password);
    	
    	SecureRandom random = new SecureRandom();        
        String hashedPassword = BCrypt.hashpw(newUser.password, BCrypt.gensalt());
        
        User e2 = Ebean.find(User.class, 1);  
        System.out.println("Got "+e2.email);  
    	
    	return ok(register.render(form(User.class)));
    }
    
    public static Result register() {
    	return ok(register.render(form(User.class)));
    }
    
    
    public static Result login() {
    	return ok(login.render(form(UserLogin.class)));
    }
    
    public static Result loginResult() {
    	Form<UserLogin> filledForm = loginForm.bindFromRequest();
    	UserLogin loggingUser = filledForm.get();
    	
    	User user = Ebean.find(User.class).where().eq("email", loggingUser.email).findUnique();
    	
    	
    	
    	if (BCrypt.checkpw(loggingUser.password, user.password)) {
    		return ok(loginResult.render("Ok"));
    	}
    	else {
    		return ok(loginResult.render("Login failed"));
    	}

    	
    	
    }
    
    public static Result registerResult() {
    	Form<User> filledForm = registerForm.bindFromRequest();
    	User newUser = filledForm.get();
    	    	        
        String hashedPassword = BCrypt.hashpw(newUser.password, BCrypt.gensalt());
        newUser.password = hashedPassword;
    
        
        Ebean.save(newUser);
        
    	return ok(registerResult.render("Ok"));
    }
    
    
}
