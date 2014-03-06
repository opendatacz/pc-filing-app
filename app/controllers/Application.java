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

import com.hp.hpl.jena.query.*;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.Literal;
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
     * @returnLiteral label = soln.getLiteral("label") ;   // Get a result variable - must be a literal
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
    
    public static Result jenaTest() {
    	String rs = getPublicContractData();
    	return ok(jenaTest.render(rs));
    }
    
    public static String getPublicContractData() {

		/* @formatter:off */
		Query query = QueryFactory.create(
				"PREFIX pc: <http://purl.org/procurement/public-contracts#> " +
					"PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> " +
					"SELECT ?label ?desc ?startDate " +
					"WHERE { GRAPH <http://ld.opendata.cz/tenderstats/dataset/isvzus.cz> {"+
					  "?g a pc:Contract ."+
					  "?g rdfs:label ?label ."+ 
					  "?g <http://purl.org/dc/terms/description> ?desc ."+
					  "?g pc:startDate ?startDate ."+
					"}"+
					"}"+
					"LIMIT 10");
		
		ResultSet rs = QueryExecutionFactory.sparqlService("http://localhost:3030/public/sparql", query).execSelect();
		String table = "<table><tr><th>Label</th><th>Description</th><th>Start date</th></tr>";
		 for ( ; rs.hasNext() ; )
		    {
		      QuerySolution soln = rs.nextSolution() ;
		      Literal label = soln.getLiteral("label") ;   // Get a result variable - must be a literal
		      Literal desc = soln.getLiteral("desc") ;   // Get a result variable - must be a literal
		      Literal startDate = soln.getLiteral("startDate") ;   // Get a result variable - must be a literal
		      
		      table+= "<tr><td>" + label.getString() + "</td><td>" + desc.getString() + "</td><td>" + startDate.getString()+ "</td></tr>";
		    }
		 table += "</table>";
		return table;
		

	}
}
