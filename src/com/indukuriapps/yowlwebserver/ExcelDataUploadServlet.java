package com.indukuriapps.yowlwebserver;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItemIterator;
import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.blobstore.BlobstoreInputStream;
import com.google.appengine.api.blobstore.BlobstoreService;
import com.google.appengine.api.blobstore.BlobstoreServiceFactory;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Key;
import com.indukuriapps.yowlwebserver.utility.AccountManager;

@SuppressWarnings("deprecation")
public class ExcelDataUploadServlet extends HttpServlet{
	private static final long serialVersionUID = 8937170522548469106L;
	DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
	BlobstoreService blobstore = BlobstoreServiceFactory.getBlobstoreService();
	AccountManager accman = new AccountManager() ;
	
	
	// USER INTERACTION HTTP METHODS
	// ---------------------------------
	@Override
	public void doPost(HttpServletRequest req, HttpServletResponse resp)throws IOException {
		LinkedHashMap<String, String> params = new LinkedHashMap<String, String>();
		
		Entity account = accman.getAccount(req.getParameter("username"));
		if(!accman.authenticated(req, account)){
			resp.sendError(401) ;
			return ;
		}
		if(!accman.isBillingSetup(account)){
			resp.sendError(402);
			return ;
		}
		
		Map<String, BlobKey> blobs = blobstore.getUploadedBlobs(req);
		BlobKey blobKey = blobs.get("uploadfile");
		BlobstoreInputStream filestream = new BlobstoreInputStream(blobKey);
		
		String filename = blobstore.getBlobInfos(req).get("uploadfile").get(0).getFilename();
		
		
		String batchID = String.valueOf(UUID.randomUUID());
		try{
			ServletFileUpload uploadfile = new ServletFileUpload();
			FileItemIterator  itemiterator = uploadfile.getItemIterator(req) ;
		    while(itemiterator.hasNext()){
		    	FileItemStream item = itemiterator.next();
		    	
		    	if(item.isFormField()){
		    		BufferedReader  datastream = new BufferedReader(new InputStreamReader(item.openStream()));
		        	String urlencoded = datastream.readLine();
		        	params.put(item.getFieldName(), URLDecoder.decode(urlencoded));
		        	datastream.close();
		    	}
		    }
		}	catch (FileUploadException e1) {	resp.sendError(500, "Something Went Terribly Wrong With The Upload. :("); return;	} 
		
		// CREATE CALL ORDER
		Entity callorder = new Entity("CallOrder", account.getKey());
		callorder.setProperty("batchID", batchID) ;
		callorder.setProperty("owner", account.getProperty("username")) ;
		callorder.setProperty("title", params.get("title")) ;
		callorder.setProperty("description", params.get("description"));
		
		Key callorderkey = datastore.put(callorder);
		String date = params.get("date") ;
		Object[] batchparams = { callorderkey, date, batchID, } ;
		
		int callcount = 0;
		if(filename.endsWith(".csv")){
			callcount = handleCSVFiles(filestream, account, batchparams) ;
		}
		else if(filename.endsWith(".xls") || filename.endsWith(".xlsx")){
			callcount = handleExcelFiles(filestream, account, batchparams);
		}
		else{
			resp.sendError(400, "Unsupported File Format") ; filestream.close(); return;
		}
		
		accman.chargeAccount(callcount, account) ;
		callcount = 0;
		
		blobstore.delete(blobKey) ;
	}
	
	// FILE UPLOAD PARSING
	// -----------------------------------------------
	public int handleExcelFiles(InputStream is, Entity account, Object[] batchparams){
		Workbook wb = null;
		try {
			wb = WorkbookFactory.create(is); } 
		catch (InvalidFormatException e1) { e1.printStackTrace();	} 
		catch (IOException e1) { e1.printStackTrace(); }
		
		int callcount = 0;
		ArrayList<Entity> calls = new ArrayList<Entity>() ;
		
		try{
		    Sheet sheet = wb.getSheetAt(0);
		    int x = sheet.getPhysicalNumberOfRows();
		    Cell cell;
			for(int i=0; i < x;i++){
				Row row = sheet.getRow(i);
				Entity call = new Entity("Call", (Key) batchparams[0]);
				
				long scheduledtime = row.getCell(0).getDateCellValue().getTime() ;
				long realtime = Date.parse((String) batchparams[1]);
				long timedelay = scheduledtime - realtime ;
				call.setProperty("time", System.currentTimeMillis() + timedelay) ;
				
				cell = row.getCell(1);
				cell.setCellType(Cell.CELL_TYPE_STRING);
				call.setProperty("origin", cell.getStringCellValue());
				
				cell = row.getCell(2);
				cell.setCellType(Cell.CELL_TYPE_STRING);
				call.setProperty("destination", cell.getStringCellValue()) ;
				
				call.setProperty("message", row.getCell(3).getStringCellValue()) ;
				call.setProperty("batchID", (String) batchparams[2]);
				
				call.setProperty("owner", account.getProperty("username")) ;
				
				call.setProperty("paid", "false"); 
				
				calls.add(call) ;
				callcount++ ;
				
				if(calls.size() == 10){
					datastore.put(calls);
					calls.clear();
				}
			}
			
			datastore.put(calls);
			calls.clear();
			is.close();
		}catch(NullPointerException e){	} 
		catch (IOException e) {	e.printStackTrace(); }
		
		return callcount ;
	}
	
	public int handleCSVFiles(InputStream is, Entity account, Object[] batchparams) throws IOException {
		BufferedReader br = new BufferedReader(new InputStreamReader(is));
		String line = "";
		int callcount = 0;
		ArrayList<Entity> calls = new ArrayList<Entity>() ;
		
		while((line = br.readLine()) != null){
			Entity call = new Entity("Call", (Key) batchparams[0]);
			String[] csvargs = line.split(",") ;
			
			long scheduledtime = Date.parse(csvargs[0]) ;
			long realtime = Date.parse((String) batchparams[1]);
			long timedelay = scheduledtime - realtime ;
			call.setProperty("time", System.currentTimeMillis() + timedelay) ;
			call.setProperty("origin", csvargs[1]);
			call.setProperty("destination", csvargs[2]) ;
			call.setProperty("message", csvargs[3]) ;
			call.setProperty("batchID", (String) batchparams[2]);
			call.setProperty("paid", "false"); 
			call.setProperty("owner", account.getProperty("username")) ;
			
			calls.add(call) ;
			callcount++ ;
			
			if(calls.size() == 10){
				datastore.put(calls);
				calls.clear();
			}
		}
		
		datastore.put(calls) ;
		calls.clear();
		br.close() ;
		
		return callcount ;
	}
}