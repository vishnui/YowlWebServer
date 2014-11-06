package com.indukuriapps.yowlwebserver;

import java.io.IOException;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@SuppressWarnings("unused")
public class BillingServlet extends HttpServlet{
	private static final long serialVersionUID = 7159720786520133971L;
	private static final String Google_Seller_Identifier = "06117809806367138484" ;
	private static final String Google_Seller_Secret = "BctPKjl4HV9xEfJG0py9Bw" ;
	
	
	
	@Override
	public void doGet(HttpServletRequest req, HttpServletResponse resp)throws IOException {
		resp.getWriter().write("success");
	}

}
