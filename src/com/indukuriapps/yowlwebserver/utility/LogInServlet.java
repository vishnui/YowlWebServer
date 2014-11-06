package com.indukuriapps.yowlwebserver.utility;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.datastore.Entity;

public class LogInServlet extends HttpServlet {
	private static final long serialVersionUID = -3380465719847972835L;
	private static final String[] accResponses = {
		"password:true",
		"no-user",
		"password:false",
		"FATAL_ERROR"
	};
	private static final String[] serverResponses = {
		"all-ok",
		"no-billing",
		"no-such-user",
		"incorrect-pass"
	};
	AccountManager accman = new AccountManager() ;
	Entity account ;

	@SuppressWarnings("deprecation")
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)	throws ServletException, IOException {
		//resp.addHeader("Access-Control-Allow-Origin", "*");
		if(req.getParameter("username") !=null && req.getParameter("password") !=null){
			String accResp = accman.authenticatedVerbal(req);
			resp.setStatus(200);
			if(accResp.equals(accResponses[0])){
				if(accman.isBillingSetup(accman.getAccount(req.getParameter("username")))){
					setRespText(resp, serverResponses[0]);
					return ;
				}
				setRespText(resp, serverResponses[1]);
				return ;
			}
			else if(accResp.equals(accResponses[1])){
				setRespText(resp, serverResponses[2]);
				return ;
			}
			else if(accResp.equals(accResponses[2])){
				setRespText(resp, serverResponses[3]);
			}
		}
		else resp.setStatus(401, "Invalid request");
	}
	
	private void setRespText(HttpServletResponse r, String buff) throws IOException{
		r.getWriter().write(buff);
		r.getWriter().flush();
		r.getWriter().close();
	}
}
