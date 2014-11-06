package com.indukuriapps.yowlwebserver.utility;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

import javax.servlet.http.HttpServletRequest;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.FilterPredicate;

public class AccountManager {
	DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
	
	public synchronized void chargeAccount(int numcalls, Entity account){
		double ob = Double.parseDouble(account.getProperty("outstandingbill")+"") ;
		ob += (numcalls*.10) ;
		account.setProperty("outstandingbill", ob+"") ;
		
		datastore.put(account) ;
	}
	
	public synchronized Entity getAccount(String accountname){
		Entity account ;
		Query q = new Query("Account");
		q.setFilter(new FilterPredicate("username", FilterOperator.EQUAL, accountname));
		
		try{
			account = datastore.prepare(q).asSingleEntity() ;
			return account;
		}catch(Exception e){ e.printStackTrace(); return null;}
	}
	
	public synchronized String authenticatedVerbal(HttpServletRequest req) throws UnsupportedEncodingException{
		Entity account = getAccount(req.getParameter("username"));
		
		if(account == null) return new String("no-user") ;
		
		try {
			
			return new String("password:"+String.valueOf(PasswordEncryptionService.authenticate(
						req.getParameter("password"), 
						respStrToByteArr((String) account.getProperty("password")), 
						respStrToByteArr((String) account.getProperty("salt"))))
					);
		} 
		catch (NoSuchAlgorithmException e) {	e.printStackTrace();} 
		catch (InvalidKeySpecException e) {	e.printStackTrace();	}
		
		//should never return this
		return "FATAL_ERROR" ;
	}
	
	public synchronized boolean authenticated(HttpServletRequest req, Entity account){
        if(account == null) return false ;
        try {
                
                return PasswordEncryptionService.authenticate(
                                req.getParameter("password"), 
                                respStrToByteArr((String) account.getProperty("password")), 
        						respStrToByteArr((String) account.getProperty("salt")));
        } 
        catch (NoSuchAlgorithmException e) {    e.printStackTrace();} 
        catch (InvalidKeySpecException e) {     e.printStackTrace();    }
        
        return true ;
	}

	public synchronized boolean isBillingSetup(Entity account){
        if (account == null) return false ;
        if (account.getProperty("creditcard") == null) return false;
        return true ;
	}
	
	public static byte[] respStrToByteArr(String tobyte){
		String[] toparse = tobyte.substring(1, tobyte.length()-1).split(",");
		byte[] result = new byte[toparse.length];
		for(int i =0; i<toparse.length; i++){
			result[i] = Byte.valueOf(toparse[i].trim());
		}
		
		return result;
	}
}