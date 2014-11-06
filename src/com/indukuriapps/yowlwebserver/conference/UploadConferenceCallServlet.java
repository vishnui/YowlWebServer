package com.indukuriapps.yowlwebserver.conference;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.datastore.Entity;

public class UploadConferenceCallServlet extends HttpServlet{
	private static final long serialVersionUID = 5527021759004736110L;

	@SuppressWarnings("unused")
	@Override
	public void doPost(HttpServletRequest req, HttpServletResponse resp)	throws ServletException, IOException {
		Entity entity = new Entity("ConferenceCall");
		req.getParameter("time");
	}

}
