package com.indukuriapps.yowlwebserver.utility;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Arrays;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;

public class RegisterServlet extends HttpServlet{
	private static final long serialVersionUID = -2892734644336699615L;
	DatastoreService datastore = DatastoreServiceFactory.getDatastoreService() ;

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)			throws ServletException, IOException {
		//resp.addHeader("Access-Control-Allow-Origin", "*");
		
		String username = req.getParameter("username");
		String password = req.getParameter("password");
		
		
		byte[] salt = null;
		byte[] encryptedpassword = null;
		try {
			
			salt = PasswordEncryptionService.generateSalt();
			encryptedpassword = PasswordEncryptionService.getEncryptedPassword(password, salt);
		} 
		catch (NoSuchAlgorithmException e) {	e.printStackTrace();} 
		catch (InvalidKeySpecException e) {		}
		
		Entity newaccount = new Entity("Account") ;
		newaccount.setProperty("username", username);
		newaccount.setProperty("salt", Arrays.toString(salt)) ;
		newaccount.setProperty("password", Arrays.toString(encryptedpassword)) ;
		
		datastore.put(newaccount);
		
		
		
	}
}
