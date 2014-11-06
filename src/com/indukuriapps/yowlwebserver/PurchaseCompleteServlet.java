package com.indukuriapps.yowlwebserver;

import java.io.IOException;

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

public class PurchaseCompleteServlet extends HttpServlet{
	
	static final long serialVersionUID = -7356267291772623641L;
	
	DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
	
	@Override
	public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException{
		JSONObject response =  JSONObject.fromObject(req.getParameter("jwt"));
		JSONObject response_request = response.getJSONObject("request");
		
		Query q = new Query("Call") ;
		q.setFilter(new FilterPredicate("batchID", FilterOperator.EQUAL, response_request.getString("sellerData"))) ;
		
		try{
			Iterable<Entity> calls = datastore.prepare(q).asIterable();
			
			for(Entity call : calls){
				call.setProperty("paid", "true");
				datastore.put(call) ;
			}
		} catch(OverQuotaException e){e.printStackTrace(); return ;}
		resp.setStatus(200) ;
	}
}