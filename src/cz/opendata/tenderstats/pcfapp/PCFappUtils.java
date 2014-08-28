package cz.opendata.tenderstats.pcfapp;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.GregorianCalendar;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import cz.opendata.tenderstats.ComponentConfiguration;

public class PCFappUtils implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -128129525758003394L;
	private ComponentConfiguration config;
	
	public PCFappUtils(ComponentConfiguration config) {
		this.config = config;
	}
	
	public void retreiveDocument(HttpServletRequest request,
			HttpServletResponse response) {
		
		java.sql.Blob doc;

		try {
			Connection con = connectDB();
			PreparedStatement pst = con.prepareStatement("SELECT * FROM documents WHERE token = ? ");			
			pst.setString(1, request.getParameter("token"));			
			java.sql.ResultSet rs = pst.executeQuery();
			if (rs.next()) {
				
				String contentType = rs.getString("contentType");
				String filename = rs.getString("filename");
				doc = rs.getBlob("data");
				
				response.setContentType(contentType);
				response.setHeader("Content-Disposition", "attachment; filename="+filename);

				byte[] docBytes = doc.getBytes(1,(int) doc.length());
				OutputStream respOS = response.getOutputStream();
				respOS.write(docBytes, 0, docBytes.length);
				respOS.flush();
				
			}
			else
			{
				response.sendError(404,"File not found");
			}
			
		} catch (SQLException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public String getFileName(final Part part) {
	    //final String partHeader = part.getHeader("content-disposition");	    
	    for (String content : part.getHeader("content-disposition").split(";")) {
	        if (content.trim().startsWith("filename")) {
	            return content.substring(
	                    content.indexOf('=') + 1).trim().replace("\"", "");
	        }
	    }
	    return null;
	}
	
	private static Connection connection = null;
	
	private Connection connectDB() throws SQLException { 
	
	if ( connection != null && connection.isValid(0) ) return connection;
		
	return DriverManager.getConnection(config.getRdbAddress() + config.getRdbDatabase(),
			config.getRdbUsername(),
			config.getRdbPassword());		
	}
	
	/*
	 * returns
	 * 1  - success
	 * 0  - file part does not exists
	 * -1 - failed
	 */	
	public String processFileUpload(HttpServletRequest request, Part part,String owner,String token) {
					    
	    InputStream filecontent = null;	    	    
	    Connection con;
		try {			
			final String fileName = getFileName(part);
			if ( fileName.isEmpty() ) return "";
	        filecontent = part.getInputStream();
			
			con = connectDB();
			PreparedStatement pst = con.prepareStatement("INSERT INTO documents (owner,filename,data,contentType,token) VALUES (?,?,?,?,?)");
			pst.setString(1, owner);
			pst.setString(2, fileName);
			pst.setBinaryStream(3, filecontent);
			pst.setString(4, part.getContentType());
			pst.setString(5, token);
			
			
			return ( pst.executeUpdate() > 0 ? fileName : null ) ;			
			
		} catch (SQLException | IllegalStateException | IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	    
		return null;
	    		
	}
	
		public static String currentXMLTime() {
		
		DatatypeFactory df;
		try {
			df = DatatypeFactory.newInstance();
			XMLGregorianCalendar calendar = df.newXMLGregorianCalendar(new GregorianCalendar());
			return calendar.toXMLFormat();
		} catch (DatatypeConfigurationException e) {
			e.printStackTrace();
		}
		return null;
	}
	
}
