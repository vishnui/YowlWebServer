package com.indukuriapps.yowlwebserver;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.indukuriapps.yowlwebserver.utility.CallManager;

public class CallInstructionsServlet extends HttpServlet{
	private static final long serialVersionUID = 8341745804344736459L;
	DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
	CallManager calman = new CallManager();
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)	throws ServletException, IOException {
		resp.setContentType("application/xml") ;
        XMLStreamWriter writer;
		try {
			writer = XMLOutputFactory.newInstance().createXMLStreamWriter(resp.getOutputStream());
			
			writer.writeStartDocument();
	        writer.writeStartElement("Response");
	        writer.writeStartElement("Speak");
	        
	        String reqid = req.getParameter("ALegRequestUUID");
	        Entity call = calman.getCorrespondingCall(reqid);
	        String message = (String) call.getProperty("message");
	        writer.writeCharacters(message);
	        
	        writer.writeEndDocument();
	        writer.flush();
	        
	        writer.close();
	        calman.save(call) ;
		} catch (XMLStreamException e) {
			e.printStackTrace();
		}
	}
	
}