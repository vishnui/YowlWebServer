package com.indukuriapps.yowlwebserver;

import java.io.IOException;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.datastore.AsyncDatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.FilterPredicate;
import com.google.apphosting.api.ApiProxy.OverQuotaException;
import com.indukuriapps.yowlwebserver.utility.CallManager;
import com.indukuriapps.yowlwebserver.utility.PlivoClient;

public class PlivoCallMakerServlet extends HttpServlet{
	private static final long serialVersionUID = -2175144039268646990L;
	
	AsyncDatastoreService datastore = DatastoreServiceFactory.getAsyncDatastoreService();
	PlivoClient pc = new PlivoClient() ;
	CallManager calman = new CallManager();
	
	public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException{
		Query q = new Query("Call") ;
		long current = System.currentTimeMillis();
		q.setFilter(new FilterPredicate("time", FilterOperator.LESS_THAN, current));
		
		try{
			Iterable<Entity> calls = datastore.prepare(q).asIterable();
			for(Entity call : calls){
				if(call.getProperty("paid").equals("true")){
					pc.makeCall(call) ;
					datastore.put(call) ;
				}
				else{
					call.setProperty("error", "unpaid") ;
					calman.save(call) ;
				}
			}
		} catch(OverQuotaException e){e.printStackTrace(); return ;}
	}
}