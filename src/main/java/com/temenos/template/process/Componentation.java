package com.temenos.template.process;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class Componentation {

	
	@Autowired
	private UtilProcess utilProcess;
	
	/*
	 * Method to write into .component file.
	 */
	public String createComponent(JSONObject response,boolean isExcelProcess) throws Exception{
		Integer i,j;
		
		//Get the required data from JSONObject.
		String applicationName = (String) response.get("applicationName");
		String moduleName = (String) response.get("moduleName");
		String componentName = (String) response.get("componentName");
		String isReservedFieldRequired = (String) response.get("isReservedFieldRequired");
		Integer numberOfReservedFields = 0;
		if(isReservedFieldRequired.equals("Yes")) {
			 numberOfReservedFields = (Integer) response.get("numberOfReservedFields");
		}
		String isAuditFieldRequired = (String) response.get("isAuditFieldRequired");
		String isLocalFieldRequired = (String) response.get("isLocalFieldRequired");
		String isOverrideFieldRequired = (String) response.get("isOverrideFieldRequired");
		String location = (String) response.get("location");
		String equatePrefix = (String) response.get("equatePrefix");
		Integer numberOfFields = (Integer) response.get("numberOfFields");
		// To store the field names. 
		String[] fieldName = new String[numberOfFields];
		if(isExcelProcess) {
			JSONArray fieldProperties = (JSONArray) response.get("fieldProperties");
			for(i=0;i<fieldProperties.size();i++) {
				JSONObject fieldProperty = (JSONObject) fieldProperties.get(i);
				fieldName[i] = (String) fieldProperty.get("name");
			}
			 
		} else {
			//Get the array from the Json object
			List<LinkedHashMap> fieldProperties = (ArrayList) response.get("fieldProperties");
			i=0;
			//get the field names from the Array.
			for(LinkedHashMap fieldObjects:fieldProperties) {
				fieldName[i] = (String) fieldObjects.get("name");
				i++;
			}
		}
		i=0;
		for(i=0;i<numberOfFields;i++) {
			fieldName[i] = utilProcess.removeValueMarkers(fieldName[i]);
		}
		
		//get the base location
		location = utilProcess.getBaseLocation(componentName, location, moduleName);
		
		//Convert into camel case
		String prefixInCamelCase = utilProcess.convertCamelCase(equatePrefix);
		File componentFile = new File(location+"/Definition/"+componentName+".component"); //Open file to write
		componentFile.getParentFile().mkdirs();
		FileWriter componentFileWriter =  new FileWriter(componentFile); // Instance of file write with append mode(by passing 2nd param as true)
		BufferedWriter componentWriter = new BufferedWriter(componentFileWriter);
		
		
		//Convert into camel case
		String appNameInCamelCase = utilProcess.convertCamelCase(applicationName);
		String appNameWoModuleInCamelCase = utilProcess.convertCamelCase(applicationName.substring(applicationName.indexOf(".")));
		//Append or write into file
		//Application declarations
		String methodLine1 = "private method "+appNameWoModuleInCamelCase+"(";
		componentWriter.append("component "+ componentName);
		componentWriter.append("\n# Component Isolation Definition");
		componentWriter.append("\nmetamodelVersion 1.6");
		componentWriter.append("\n# -------------------------------------------------");
		componentWriter.append("\n"+methodLine1);
		componentWriter.append("\n)");
		componentWriter.append("\n{");
		componentWriter.append("\njBC: "+applicationName);
		componentWriter.append("\n}");
		
		//Field declarations
		componentWriter.append("\n");
		String methodLine2 = "private method "+appNameWoModuleInCamelCase+"Fields"+"(";
		componentWriter.append(methodLine2);
		componentWriter.append("\n)");
		componentWriter.append("\n{");
		componentWriter.append("\njBC: "+applicationName+".FIELDS");
		componentWriter.append("\n}");
		
		componentWriter.close();
		
		File tableFile = new File(location+"/Definition/"+appNameWoModuleInCamelCase+".table"); //Open file to write
		tableFile.getParentFile().mkdirs();
		FileWriter tableFileWriter =  new FileWriter(tableFile); // Instance of file write with append mode(by passing 2nd param as true)
		BufferedWriter tableWriter = new BufferedWriter(tableFileWriter);
		

		//Write table definition
		String tableLine1 = "public table "+appNameInCamelCase+" {";
		tableWriter.append(tableLine1);
		tableWriter.append("\nt24: "+applicationName);
		tableWriter.append("\nfields: {");
		String componentField = new String();
		String fieldNameInCamelCase = new String();
		Integer position = 1;
		for(i=0;i<numberOfFields;i++) {
			fieldNameInCamelCase = utilProcess.convertCamelCase(fieldName[i]);
			componentField = prefixInCamelCase+fieldNameInCamelCase+"("+equatePrefix+"."+fieldName[i]+") = "+position;
			tableWriter.append("\n"+componentField);
			position++;
		}
		String componentReservedField = new String();
		j = numberOfReservedFields;
		//write Reserved fields
		if(isReservedFieldRequired.equals("Yes")) {
			while(j>0) {
				componentReservedField = prefixInCamelCase+"Reserved"+j+"("+equatePrefix+".RESERVED."+j+") = " + position;
				tableWriter.append("\n"+componentReservedField);
				position++;
				j--;
			}
		}
		
		//write Local field
		if(isLocalFieldRequired.equals("Yes")) {
			String localField = prefixInCamelCase+"LocalRef"+"("+equatePrefix+".LOCAL.REF) = "+position;
			tableWriter.append("\n"+localField);
			position++;
		}
		 
		//write override field
		if(isOverrideFieldRequired.equals("Yes")) {
			String overrideField = prefixInCamelCase+"Override"+"("+equatePrefix+".OVERRIDE) = "+position;
			tableWriter.append("\n"+overrideField);
			position++;
		}
		
		//audit fields
		if(isAuditFieldRequired.equals("Yes")) {
			
			String recordStatus = prefixInCamelCase+"RecordStatus"+'('+equatePrefix+".RECORD.STATUS"+')'+" = "+position;
            position++;
            tableWriter.append("\n"+recordStatus);
            
            String currNo = prefixInCamelCase+"CurrNo"+'('+equatePrefix+".CURR.NO"+')'+" = "+position;
            position++;
            tableWriter.append("\n"+currNo);
            
            String inputter = prefixInCamelCase+"Inputter"+'('+equatePrefix+".INPUTTER"+')'+" = "+position;
            position++;
            tableWriter.append("\n"+inputter);
            
            String dateTime = prefixInCamelCase+"DateTime"+'('+equatePrefix+".DATE.TIME"+')'+" = "+position;
            position++;
            tableWriter.append("\n"+dateTime);
            
            String authoriser = prefixInCamelCase+"Authoriser"+'('+equatePrefix+".AUTHORISER"+')'+" = "+position;
            position++;
            tableWriter.append("\n"+authoriser);
            
            String coCode = prefixInCamelCase+"CoCode"+'('+equatePrefix+".CO.CODE"+')'+" = "+position;
            position++;
            tableWriter.append("\n"+coCode);
            
            String deptCode = prefixInCamelCase+"DeptCode"+'('+equatePrefix+".DEPT.CODE"+')'+" = "+position;
            position++;
            tableWriter.append("\n"+deptCode);
            
            String auditorCode = prefixInCamelCase+"AuditorCode"+'('+equatePrefix+".AUDITOR.CODE"+')'+" = "+position;
            position++;
            tableWriter.append("\n"+auditorCode);
            
            String auditDateTime = prefixInCamelCase+"AuditDateTime"+'('+equatePrefix+".AUDIT.DATE.TIME"+')'+" = "+position;
            position++;
            tableWriter.append("\n"+auditDateTime);
		}
		tableWriter.append("\n}");
		tableWriter.append("\n}");
		
		tableWriter.close();

		return "Success";
	}
}
	