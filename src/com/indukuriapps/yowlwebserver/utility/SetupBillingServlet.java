package com.indukuriapps.yowlwebserver.utility;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.amazonaws.fps.AmazonFPS;
import com.amazonaws.fps.AmazonFPSClient;
import com.amazonaws.fps.AmazonFPSException;
import com.amazonaws.fps.model.VerificationStatus;
import com.amazonaws.fps.model.VerifySignatureRequest;
import com.amazonaws.fps.model.VerifySignatureResponse;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;

public class SetupBillingServlet extends HttpServlet {
	private static final long serialVersionUID = -2153618323876183617L;
	AccountManager accman = new AccountManager() ;
	DatastoreService datastore = DatastoreServiceFactory.getDatastoreService() ;
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)	throws ServletException, IOException {
		String callerReference = req.getParameter("callerReference");
		if(callerReference == null){
			resp.sendError(402) ;
			return ;
		}
		
		AmazonFPS service ;
		try {
			service = new AmazonFPSClient("AKIAI7M73ILMOGDZKJGQ", "wlUwqCRXxTL7L/v4WvMZYlxlRsE6RPmmKzR/AbM1");
		} catch (AmazonFPSException e) {
			resp.sendError(500, e.getMessage());
			return ;
		}
		
		VerifySignatureRequest request = new VerifySignatureRequest();
		request.setHttpParameters(req.getQueryString());
		request.setUrlEndPoint("http://yowl.theyowl.com/public/setupbilling");
		
		VerifySignatureResponse response = null; 
		try {
			 response = service.verifySignature(request) ;
		} catch (AmazonFPSException e) {
			resp.sendError(500, e.getMessage());
			return ;
		}
		
		if(response.getVerifySignatureResult().getVerificationStatus() != VerificationStatus.SUCCESS){
			resp.sendError(401);  return;
		}
		
		String username = callerReference.split("-")[0];
		resp.getWriter().write("Success!  "+username);
	}
}