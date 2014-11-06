package com.indukuriapps.yowlwebserver;

import java.io.IOException;
import java.net.URLDecoder;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONException;
import net.sf.json.JSONObject;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.indukuriapps.yowlwebserver.utility.CallManager;

public class CallCompleteServlet extends HttpServlet{
	private static final long serialVersionUID = 636525375508871279L;
	DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
	CallManager calman = new CallManager();

	@SuppressWarnings("deprecation")
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)	throws ServletException, IOException {
		String response = URLDecoder.decode(req.getParameter("response"));
		JSONObject responsejson = JSONObject.fromObject(response);
		Entity call = calman.getCorrespondingCall(responsejson.getString("api_id"));
		call.setProperty("request_uuid", responsejson.getString("request_uuid")) ;
			
		try{
			call.setProperty("requestmessage", responsejson.getString("message")) ;
			datastore.put(call) ;
		} catch (JSONException e){
			call.setProperty("error", responsejson.getString("error"));
			calman.save(call) ;
		}
		resp.setStatus(200);
	}
}