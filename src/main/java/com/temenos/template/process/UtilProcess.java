package com.temenos.template.process;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.springframework.stereotype.Component;

@Component
public class UtilProcess {


	//Remove the sub value and multi value markers from the fields
	public String removeValueMarkers(String fieldName) {
		
		boolean isMultiField = false;
		//check the field has multi value.
		if(fieldName.substring(0,2).equals("XX")) {
			isMultiField = true;
		}
		
		if(isMultiField) {
			//When the field have sub value
			if(fieldName.substring(3,5).equals("XX") || fieldName.substring(3,5).equals("LL") ) {
				fieldName = fieldName.substring(6);
			} else {
				fieldName = fieldName.substring(3); //field has multi value
			}
			
		}
		return fieldName;
	}
	
	//Method accepts content in upper-case with dot separated and return in camel-case 
	public String convertCamelCase(String stringInUpperCase) {
		String stringInCamelCase = new String();
		String stringInLowerCase = stringInUpperCase.toLowerCase();
		stringInCamelCase =  Character.toUpperCase(stringInLowerCase.charAt(0)) + stringInLowerCase.substring(1);
		Integer i;
		for(i=0;i<stringInCamelCase.length();i++) {
			if(stringInCamelCase.charAt(i) == '.') {
				stringInCamelCase = stringInCamelCase.substring(0,i) + Character.toUpperCase(stringInCamelCase.charAt(i+1)) + stringInCamelCase.substring (i+2);
			}
		}
		return stringInCamelCase;
	}
	
	//Method to check the null value from the JSON(null passed as - from UI)
	public String nullCheck(String valueString) {
		if(valueString.equals("-")) {
			valueString="";
		}
		return valueString;
	}
	
	//Method will the return system date.
	public String getTodayDate() {
		DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
		Date date = new Date();
		String dateString = dateFormat.format(date);
		return dateString;
	}
	
	
	//Build and get the location of Project.
	public String getBaseLocation(String component, String location,String moduleName) {
		String componentNameInCamelCase = component.replace('.', '_');
		location = location+"/"+moduleName+"/"+componentNameInCamelCase;
		File baseFolder = new File(location);
		baseFolder.getParentFile().mkdirs();
		//Create the public folder
		File baseFolderPublic = new File(location+"/Source/Public"); 
		baseFolderPublic.mkdirs();
		//Unit test folder
		File baseFolderUnitTest = new File(location+"/Test/UnitTest"); 
		baseFolderUnitTest.mkdirs();
		//Data folder
		File baseFolderData = new File(location+"/Data/Public"); 
		baseFolderData.mkdirs();
		return location;
	}
		
}

