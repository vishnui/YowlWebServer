package com.indukuriapps.yowlwebserver.utility;

import java.io.IOException;
import java.security.SignatureException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.amazonaws.cbui.AmazonFPSMultiUsePipeline;
import com.google.appengine.api.datastore.Entity;


public class SetupBillingURLGeneratorServlet extends HttpServlet {
	private static final long serialVersionUID = 178412021436593916L;
	AccountManager accman = new AccountManager() ;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)	throws ServletException, IOException {
		String username = req.getParameter("username");
		Entity account = accman.getAccount(username);
		if(!accman.authenticated(req, account)){
			resp.sendError(402);
			return;
		}
		
        AmazonFPSMultiUsePipeline pipeline= new AmazonFPSMultiUsePipeline("AKIAI7M73ILMOGDZKJGQ", "wlUwqCRXxTL7L/v4WvMZYlxlRsE6RPmmKzR/AbM1");
        pipeline.setMandatoryParameters(username+"-"+String.valueOf(UUID.randomUUID()), "http://yowl.theyowl.com/public/setupbilling", "5000");
        
        Map<String, String> params = new LinkedHashMap<String, String>();
        params.put("cobrandingStyle", "logo");
        params.put("cobrandingUrl", "http://yowl.theyowl.com/images/rework-infinitum-yowl-logo.png");
        params.put("websiteDescription", "Yowl");
        params.put("validityExpiry", ""+(System.currentTimeMillis()/1000) + 94608000);
        
        pipeline.addOptionalParameters(params);

        //MultiUse url
        try {
			resp.sendRedirect(pipeline.getUrl());
		} catch (SignatureException e) {                        // Should never happen
			e.printStackTrace();
		}
		
	}
}