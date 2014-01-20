package controllers;

import javax.sql.DataSource;

import models.User;
import play.db.*;
import cz.opendata.tenderstats.Mailer;
import play.*;
import play.data.Form;
import play.mvc.*;
import views.html.*;

public class Application extends Controller {

	public static Form<User> registerForm = Form.form(User.class);
    public static Result index() {
  	
        return ok(index.render(registerForm));
    }
    
    public static Result register() {
    	DataSource ds = DB.getDataSource();    	
    	Form<User> filledForm = registerForm.bindFromRequest();
    	User newUser = filledForm.get();
    	return ok(index.render(null));
    }

}
