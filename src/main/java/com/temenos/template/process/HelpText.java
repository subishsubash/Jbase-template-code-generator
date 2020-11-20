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
public class HelpText {


	@Autowired
	private UtilProcess utilProcess;
	
	
	//Method to create HELP text
	public String createHelpText(JSONObject jsonObject,boolean isExcelProcess) throws Exception{
		String tableDescription = (String)jsonObject.get("tableDescription");
		
		// To store the field names. 
		Integer noOfField = (Integer) jsonObject.get("numberOfFields");
		String fieldDescription[] = new String[noOfField];
		String fieldValidation[] = new String[noOfField];
		String[] fieldName = new String[noOfField];
		
		String applicationName = (String) jsonObject.get("applicationName");
		String moduleName = (String) jsonObject.get("moduleName");
		String componentName = (String) jsonObject.get("componentName");
		String location = (String) jsonObject.get("location");
		
		Integer i;
		
		if(isExcelProcess) {
			JSONArray fieldProperties = (JSONArray) jsonObject.get("fieldProperties");
			for(i=0;i<fieldProperties.size();i++) {
				JSONObject fieldObjects = (JSONObject) fieldProperties.get(i);
				fieldDescription[i] = (String) fieldObjects.get("fieldDescription");
				fieldValidation[i] = (String) fieldObjects.get("fieldValidation");
				fieldName[i] = (String) fieldObjects.get("name");
			}
		}else {
			//Get the array from the Json object
			List<LinkedHashMap> fieldProperties = (ArrayList) jsonObject.get("fieldProperties");
			i=0;
			//get the field names from the Array.
			for(LinkedHashMap fieldObjects:fieldProperties) {
				fieldDescription[i] = (String) fieldObjects.get("fieldDescription");
				fieldValidation[i] = (String) fieldObjects.get("fieldValidation");
				fieldName[i] = (String) fieldObjects.get("name");
				i++;
			}
		}
		//get the base location
		location = utilProcess.getBaseLocation(componentName, location, moduleName);
				
		File helpTextFile = new File(location+"/HelpText/"+applicationName+".xml"); //Open file to write
		helpTextFile.getParentFile().mkdirs();
		FileWriter helpTextFileWriter =  new FileWriter(helpTextFile); 
		BufferedWriter helpTextWriter = new BufferedWriter(helpTextFileWriter);
		helpTextWriter.append("<?xml version=\"1.0\" encoding=\"utf-8\"?>");
		helpTextWriter.append("\n<t24help>");
		helpTextWriter.append("\n<header>");
		helpTextWriter.append("\n<product>"+moduleName+"</product>");
		helpTextWriter.append("\n<table>"+applicationName+"</table>");
		helpTextWriter.append("\n</header>");
		helpTextWriter.append("\n<overview>");
		helpTextWriter.append("\n<ovdesc>");
		helpTextWriter.append("\n<p>"+tableDescription+"</p>");
		helpTextWriter.append("\n</ovdesc>");
		helpTextWriter.append("\n<ovimage></ovimage>");
		helpTextWriter.append("\n</overview>");
		helpTextWriter.append("\n<menu>");
		i=0;
		/*
		 * while(i<noOfField) { fieldDescription[i] = nullCheck(fieldDescription[i]);
		 * fieldValidation[i] = nullCheck(fieldValidation[i]); i++; }
		 */
		String fieldNameWithoutMultiMarker = new String();
		while(i<noOfField) {
			fieldNameWithoutMultiMarker = utilProcess.removeValueMarkers(fieldName[i]);
			helpTextWriter.append("\n<t>");
			helpTextWriter.append("\n<field>"+fieldNameWithoutMultiMarker+"</field>");
			helpTextWriter.append("\n<desc>");
			if(!(fieldDescription[i].equals("-"))){
				fieldDescription[i] = changeTag(fieldDescription[i]);
				helpTextWriter.append("\n<p>"+fieldDescription[i]+".</p>");
			}
			if(!(fieldValidation[i].equals("-"))) {
				helpTextWriter.append("\n<p>Validation Rules:</p>");
				fieldValidation[i] = changeTag(fieldValidation[i]);
				helpTextWriter.append("\n<p>"+fieldValidation[i]+".</p>");
			}
			helpTextWriter.append("\n</desc>");
			helpTextWriter.append("\n</t>");
			i++;
		}
		helpTextWriter.append("\n</menu>");
		helpTextWriter.append("\n</t24help>");
		helpTextWriter.close();
		return "Success";
	}
	
	//Convert dot (.) into XML <p></p> tag.  
	public String changeTag(String fieldValue) {
		if(fieldValue.endsWith(".")) {
			fieldValue = fieldValue.substring(0,fieldValue.length()-1); 
		}
		String fieldValueWithTag =fieldValue.replaceAll("\n", ".</p>\n<p>"); // value of \n consider as regulare expression.
		return fieldValueWithTag;
	}
}
