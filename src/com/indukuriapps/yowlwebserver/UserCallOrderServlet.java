package com.indukuriapps.yowlwebserver;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONObject;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.FilterPredicate;
import com.google.apphosting.api.ApiProxy.OverQuotaException;
import com.indukuriapps.yowlwebserver.utility.AccountManager;

public class UserCallOrderServlet extends HttpServlet{
	private static final long serialVersionUID = -1328388662590355992L;
	DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
	AccountManager accman = new AccountManager() ;

	@Override
	public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException{
		Entity account = accman.getAccount(req.getParameter("username"));
		if(!accman.authenticated(req, account)){
			resp.sendError(401) ;
			return ;
		}
		if(!accman.isBillingSetup(account)){
			resp.sendError(402);
			return ;
		}
		
		PrintWriter responseStream = resp.getWriter() ;
		
		Query q = new Query("CallOrder") ;
		q.setAncestor(
				datastore.prepare(
						new Query("Account").setFilter(
								new FilterPredicate("username",FilterOperator.EQUAL, account.getProperty("username")))).
				asSingleEntity().
				getKey()) ;
		
		try{
			Iterable<Entity> callorders = datastore.prepare(q).asIterable();
			
			for(Entity callorder : callorders){
				JSONObject json = JSONObject.fromObject(callorder.getProperties()) ;
				responseStream.write(json.toString());
			}
		} catch(OverQuotaException e){e.printStackTrace(); return ;}
		
		responseStream.close() ;
	}
}