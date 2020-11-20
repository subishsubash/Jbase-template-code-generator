package com.temenos.template.process;

import java.util.ArrayList;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.temenos.template.dao.ComponentMasterRepo;
import com.temenos.template.dao.FieldMasterRepo;
import com.temenos.template.dao.TemplateMasterRepo;
import com.temenos.template.model.ComponentMaster;
import com.temenos.template.model.FieldMaster;
import com.temenos.template.model.TemplateMaster;


@Transactional
@Component
public class TemplateProcess {

	@Autowired
	private ComponentMasterRepo componentMasterRepo;
	
	@Autowired
	private TemplateMasterRepo templateMasterRepo;
	
	@Autowired
	private FieldMasterRepo fieldMasterRepo;
	
	@Autowired
	private Componentation componentation;
	
	@Autowired
	private ApplicationRoutine applicationRoutine;
	
	@Autowired
	private Fields fields;
	
	@Autowired
	private HelpText helpText;
	
	
	/* Get the template details to the UI screen
	* API fetch list of required details, field properties to create template
	* API fetch from the Database.
	* */
	@SuppressWarnings("unchecked")
	public JSONObject getTemplateDetails()throws Exception{
		
		// JOSN Objects/ Array to store the required data.
		JSONObject templatedetails = new JSONObject();
		JSONArray componentArray = new JSONArray();
		JSONArray templateArray = new JSONArray();
		JSONArray fieldArray = new JSONArray();
		
		// Array list 
		List<ComponentMaster> componentMasterList = new ArrayList<>();
		List<TemplateMaster> templateMasterList =  new ArrayList<>();
		List<FieldMaster> fieldMasterList = new ArrayList<>();
			
		//Form JSON for component master 
		componentMasterList = (List<ComponentMaster>) componentMasterRepo.findAll(); // Database call
		for (ComponentMaster componentMaster : componentMasterList) {
			componentArray.add(componentMaster.getComponentProperties());
		}
		templatedetails.put("componentProperties",componentArray); // Put into the json object
		
		//Form JSON for template master
		templateMasterList = (List<TemplateMaster>) templateMasterRepo.findAll(); //Database call
		for (TemplateMaster templateMaster : templateMasterList) {
			templateArray.add(templateMaster.getAppProperties());
		}
		templatedetails.put("appProperties",templateArray); // Put into the json object
		
		//Form JSON for field master
		fieldMasterList = (List<FieldMaster>) fieldMasterRepo.findAll(); //Database call.
		for (FieldMaster fieldMaster : fieldMasterList) {
			fieldArray.add(fieldMaster.getFieldProperties());
		}
		templatedetails.put("fieldProperties",fieldArray); // Put into the json object
			
		return templatedetails;
	}
	
	
	/* API accepts selected and entered values and field properties to create the template.
	* API response Success or fail status with error msg.
	* API Create .Component, .b (Main template routine) & . Fields
	* */
	@SuppressWarnings("unchecked")
	public JSONObject createTemplate(JSONObject jsonObject,boolean isExcelProcess)  throws Exception
	{
		JSONObject response = new JSONObject();
		boolean successFlag = false;
		
		String appStatus = applicationRoutine.createApplication(jsonObject); // Method call to create .b (main routine)
		String fieldStatus = fields.createFields(jsonObject,isExcelProcess); //  Method call to create .fields
		
		String isHelpText =  (String)jsonObject.get("isHelpText");
		String helpTextStatus = new String();
		if(isHelpText.equals("Yes")) {
			helpTextStatus = helpText.createHelpText(jsonObject,isExcelProcess); // Method call to create helpText
		}
		
		//Only  files are created, response send as success
		if(appStatus.equals("Success") && fieldStatus.equals("Success")){
			String componentStatus = componentation.createComponent(jsonObject,isExcelProcess); // Method call to create .component
			if(componentStatus.equals("Success")) {
				response.put("Data",".Component, .Table, Application.b, .Fields and Helptext(.xml) are created");
				response.put("status","Success");
				successFlag = true;
			}
		}	
		if(!(successFlag)) {
			response.put("Data","Required files are not created properly. Kindly check the data.");
			response.put("status","Error");
		}
		return response;
	}

	
}
