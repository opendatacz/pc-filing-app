package cz.opendata.tenderstats.pcfapp;

import com.google.gson.JsonObject;

public class PCFappException extends Exception {

	private JsonObject responseJson = new JsonObject();
	private int errorCode;	
	private static final long serialVersionUID = 4489180841579689085L;

	public PCFappException() {
		super();
	}
	
	public PCFappException(String message) {
		super(message);
		responseJson.addProperty("message", message);		
	}	
	
	
	
//	public PCFappException(int error) {
//		super();
//		this.errorCode = error;
//	}
	
	public PCFappException(int error, String message) {
		super(message);		
		this.errorCode = error;
		responseJson.addProperty("message", message);
	}
	
	public int getErrorCode() {
		return errorCode; 
	}
	
	public JsonObject getResponseJson() {		
		return responseJson;
	}
}
