package com.indukuriapps.yowlwebserver.utility;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.LinkedHashMap;

import net.sf.json.JSONObject;

import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.urlfetch.FetchOptions;
import com.google.appengine.api.urlfetch.HTTPHeader;
import com.google.appengine.api.urlfetch.HTTPMethod;
import com.google.appengine.api.urlfetch.HTTPRequest;
import com.google.appengine.api.urlfetch.HTTPResponse;
import com.google.appengine.api.urlfetch.URLFetchService;
import com.google.appengine.api.urlfetch.URLFetchServiceFactory;

public class PlivoClient {
	
	URLFetchService fetcher = URLFetchServiceFactory.getURLFetchService() ;
	LinkedHashMap<String, String> params = new LinkedHashMap<String, String>();
	
	public void makeCall(Entity callentity){
		params.put("from", (String) callentity.getProperty("origin"));
		params.put("to",  (String) callentity.getProperty("destination"));
		params.put("answer_url", "http://yowl.theyowl.com/private/callinstructions");
		params.put("callback_url", "http://yowl.theyowl.com/private/callcomplete");
		
		HTTPRequest httpreq = null;
		try {
			httpreq = new HTTPRequest(new URL("https://api.plivo.com/v1/Account/MANDU4MZUYMZDHOWM3ZJ/Call/"), 
					HTTPMethod.POST, FetchOptions.Builder.validateCertificate());
		} catch (MalformedURLException e) {	e.printStackTrace(); }
		
		httpreq.addHeader(new HTTPHeader("Authorization", "Basic TUFORFU0TVpVWU1aREhPV00zWko6WldSbU5UazVZekpqTVdFd1lqZzBOemhqT1RReU1ETTBObU00TlRSaw=="));
		httpreq.addHeader(new HTTPHeader("Content-Type", "application/json"));
		httpreq.setPayload(JSONObject.fromObject(params).toString().getBytes()) ;
		
		HTTPResponse httpresp = null;
		try {
			httpresp = fetcher.fetch(httpreq);
		} catch (IOException e) {e.printStackTrace(); }
		
		try {
			JSONObject response = JSONObject.fromObject(handleResponse(httpresp.getContent()));
			callentity.setProperty("api_id", response.getString("api_id")) ;
		} catch (IOException e) {	e.printStackTrace();	}
	}
	
	public String handleResponse(byte[] responsebytes) throws IOException{
		BufferedReader  datastream = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(responsebytes)));
		String line = "";
		String total = "";
		while(( line = datastream.readLine()) != null){
			total += line ;
		}
		return total;
	}
}