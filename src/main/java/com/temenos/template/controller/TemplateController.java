package com.temenos.template.controller;


import java.nio.file.Path;
import java.nio.file.Paths;

/*
 * Subashchandar S.
 */

import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.temenos.template.process.ExcelProcess;
import com.temenos.template.process.TemplateProcess;
 

@Controller
public class TemplateController {
	
	@Autowired
	private TemplateProcess templateProcess;
	
	@Autowired
	private ExcelProcess excelProcess;
	
	@RequestMapping("/")	
	public String index() {
		return "index.html";
	}
	
	/* Get the template details to the UI screen
	* API fetch list of required details, field properties to create template
	* API fetch from the Database.
	* Method - GET
	* */
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/templatedetails", method = RequestMethod.GET)
    @ResponseBody
	public JSONObject getTemplateDetails() {
		
		JSONObject response = new JSONObject();
		try {
			response.put("data",templateProcess.getTemplateDetails()); // Process call
			response.put("status","Success");
		}catch(Exception ex) {
			ex.printStackTrace();
			response.put("status","Error");
			response.put("data",ex);
		}
		return response; 	
	}
	
	/* API accepts selected and entered values and field properties to create the template.
	* Method - POST
	* API response Success or fail status with error msg.
	* API Create .Component, .b (Main template routine), . Fields .xml (Helptext)
	* */
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/createtemplate",method = RequestMethod.POST)
	@ResponseBody
	public JSONObject createTemplate(@RequestBody JSONObject jsonObject) {
		JSONObject response = new JSONObject();
		try {
			return templateProcess.createTemplate(jsonObject,false); // Process Call with JSON
		}catch(Exception ex) {
			ex.printStackTrace();
			response.put("status","Error");
			response.put("data",ex);
		}
		return response;
	}
	
	
	
	/* API accepts an excel file
	 * Method - POST
	 * API Create .Component, .b (Main template routine), . Fields .xml (Helptext)
	*/
	@RequestMapping(value = "/uploadexcel",method = RequestMethod.POST,produces="application/zip")
	@ResponseBody
	public ResponseEntity<Object>  uploadExcel(@RequestParam("template") MultipartFile excelFile) {
	
		 
        Resource resource = null;
        String filePath = new String();
        String responseMsg = new String();
        try {
			filePath = excelProcess.uploadExcel(excelFile);// Process Call with JSON
	        Path path = Paths.get(filePath);	
	        resource = new UrlResource(path.toUri());
	        responseMsg = "Successful";	
		}catch(Exception ex) {
			responseMsg = "Error in processing the file, check the template format of the inputted excel";
			ex.printStackTrace();
			return ResponseEntity.ok().header("responseMsg",responseMsg).header("errorMsg",ex.toString()).body(resource);
		}
        
        if (resource.exists()) {
            return ResponseEntity.ok().header("fileName", resource.getFilename()).header("filePath", filePath)
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"").header("responseMsg",responseMsg)
                    .body(resource);
 
            // return new ResponseEntity<Object>(resource, HttpStatus.OK);
        } else {
        	return ResponseEntity.ok().header("responseMsg",responseMsg).body(resource);
         
        }
		
	}
		
}