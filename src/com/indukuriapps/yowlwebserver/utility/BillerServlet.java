package com.indukuriapps.yowlwebserver.utility;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.FilterPredicate;

public class BillerServlet extends HttpServlet {
	private static final long serialVersionUID = 5170744178112869988L;
	DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
	String accessToken ;
	
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)	throws ServletException, IOException {
		Query q = new Query("Account") ;
		q.setFilter(new FilterPredicate("time", FilterOperator.LESS_THAN, 564164));
		Iterable<Entity> accounts = datastore.prepare(q).asIterable();
		
		for(Entity account : accounts){
			String bill = (String) account.getProperty("outstandingbill");
			if(bill == null || bill.equals("0")) continue ;
			
		}
	}
}