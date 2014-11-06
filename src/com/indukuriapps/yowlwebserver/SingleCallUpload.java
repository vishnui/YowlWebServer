package com.indukuriapps.yowlwebserver;

import java.io.IOException;
import java.util.LinkedHashMap;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.indukuriapps.yowlwebserver.utility.AccountManager;

public class SingleCallUpload extends HttpServlet{
	private static final long serialVersionUID = -4374594808656966164L;
	AccountManager accman = new AccountManager() ;
	DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
	LinkedHashMap<String, String> params = new LinkedHashMap<String, String>();
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)	throws ServletException, IOException {
		Entity account = accman.getAccount(req.getParameter("username"));
		if(!accman.authenticated(req, account)){
			resp.sendError(401) ;
			return ;
		}
		if(!accman.isBillingSetup(account)){
			resp.sendError(402);
			return ;
		}
		
		
		Entity call = null;
		try{
			call = new Entity("Call", account.getKey()) ;
		}catch(NullPointerException e){	resp.sendError(403) ;	return ; }
		
		call.setProperty("owner", account.getProperty("username"));
		call.setProperty("origin", req.getParameter("origin"));
		call.setProperty("destination", req.getParameter("destination"));
		call.setProperty("time", req.getParameter("time"));
		
		datastore.put(call) ;
		
		resp.setStatus(200) ;
	}
}