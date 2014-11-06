package com.indukuriapps.yowlwebserver.utility;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.FilterPredicate;

public class CallManager {
	
	DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
	
	public Entity getCorrespondingCall(String apiid){
		Query q = new Query("Call") ;
		q.setFilter(new FilterPredicate("api_id", FilterOperator.EQUAL, apiid)) ;
		
		return datastore.prepare(q).asSingleEntity() ;
	}
	
	public void save(Entity call){
		Entity callover = new Entity("CallOver") ;
		callover.setPropertiesFrom(call) ;
		
		datastore.delete(call.getKey()) ;
		datastore.put(callover) ;
	}

}
