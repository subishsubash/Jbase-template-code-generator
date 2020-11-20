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
public class Fields {

	@Autowired
	private UtilProcess utilProcess;
	
	
	//Write into .FIELDS
	public String createFields(JSONObject response,boolean isExcelProcess) throws Exception{
		
		Integer i;
		String applicationName = (String) response.get("applicationName");
		String moduleName = (String) response.get("moduleName");
		String componentName = (String) response.get("componentName");
		String location = (String) response.get("location");
		String isModificationHistory = (String) response.get("isModificationHistoryRequired");
		Integer numberOfFields = (Integer) response.get("numberOfFields");
		
		//Convert into camelcase
		String appNameInCamelCase = utilProcess.convertCamelCase(applicationName);
		
		//get the base location
		location = utilProcess.getBaseLocation(componentName, location, moduleName);
				
		String componentNameInCamelCase = componentName.replace('.', '_');
		File fieldFile = new File(location+"/Source/Private/"+applicationName+".FIELDS.b"); //Open file to write
		fieldFile.getParentFile().mkdirs();
		FileWriter fieldFileWriter =  new FileWriter(fieldFile); // Instance of file write with append mode(by passing 2nd param as true)
		BufferedWriter fieldWriter = new BufferedWriter(fieldFileWriter);
		String dateString = utilProcess.getTodayDate();
		
		// To store the field names. 
		String[] fieldName = new String[numberOfFields];
		String[] fieldType = new String[numberOfFields];
		Integer[] fieldLength =  new Integer[numberOfFields];
		String[] property1 =  new String[numberOfFields];
		String[] property2 = new String[numberOfFields];
		String[] propertyValue = new String[numberOfFields];
		
		
		
		if(isExcelProcess) {
			JSONArray fieldProperties = (JSONArray) response.get("fieldProperties");
			for(i=0;i<fieldProperties.size();i++) {
				JSONObject fieldObjects = (JSONObject) fieldProperties.get(i);
				fieldName[i] = (String) fieldObjects.get("name");
				fieldType[i] = (String) fieldObjects.get("dataType");
				fieldLength[i] = (Integer) fieldObjects.get("length");
				property1[i] = (String) fieldObjects.get("property1");
				property2[i] = (String) fieldObjects.get("property2");
				propertyValue[i] = (String)fieldObjects.get("propertyValue");
			}
			
		} else {
			//Get the array from the Json object
			List<LinkedHashMap> fieldProperties = (ArrayList) response.get("fieldProperties");
			i=0;
			//get the field names from the Array.
			for(LinkedHashMap fieldObjects:fieldProperties) {
				fieldName[i] = (String) fieldObjects.get("name");
				fieldType[i] = (String) fieldObjects.get("dataType");
				fieldLength[i] = (Integer) fieldObjects.get("length");
				property1[i] = (String) fieldObjects.get("property1");
				property2[i] = (String) fieldObjects.get("property2");
				propertyValue[i] = (String)fieldObjects.get("propertyValue");
				i++;
			}
		}
		
		String isReservedFieldRequired = (String) response.get("isReservedFieldRequired");
		Integer numberOfReservedFields = 0;
		if(isReservedFieldRequired.equals("Yes")) {
			 numberOfReservedFields = (Integer) response.get("numberOfReservedFields");
		}
		String isAuditFieldRequired = (String) response.get("isAuditFieldRequired");
		String isLocalFieldRequired = (String) response.get("isLocalFieldRequired");
		String isOverrideFieldRequired = (String) response.get("isOverrideFieldRequired");
		
		
		//Write .FIELDS
		fieldWriter.append("*-----------------------------------------------------------------------------");
		fieldWriter.append("\n$PACKAGE "+componentName);
		fieldWriter.append("\n*");
		fieldWriter.append("\n*Implementation of "+componentName+"."+appNameInCamelCase.substring(2)+"Fields");
		fieldWriter.append("\n*");
		fieldWriter.append("\nSUBROUTINE "+applicationName+".FIELDS");
		fieldWriter.append("\n* Developed By	: ");
		fieldWriter.append("\n* Program Name	: "+applicationName+".FIELDS");
		fieldWriter.append("\n* Module Name	: "+moduleName);
		fieldWriter.append("\n* @package 		: "+componentNameInCamelCase);
		fieldWriter.append("\n*-----------------------------------------------------------------------------");
		fieldWriter.append("\n* <desc>");
		fieldWriter.append("\n* Program Description");
		fieldWriter.append("\n* Fields Definition for "+applicationName);
		fieldWriter.append("\n* </desc>");
		fieldWriter.append("\n*-----------------------------------------------------------------------------");
		if(isModificationHistory.equals("Yes")) {
			Integer enhancementNo = (Integer) response.get("enhancementNo");
			Integer taskNo = (Integer) response.get("taskNumber");
			fieldWriter.append("\n* Modification History :");
			fieldWriter.append("\n*-----------------------");
			fieldWriter.append("\n*");
			fieldWriter.append("\n*"+dateString+" - Enhancement "+enhancementNo+" / Task "+taskNo);
			fieldWriter.append("\n*				Fields Definition for "+applicationName);
			fieldWriter.append("\n*");
			fieldWriter.append("\n*-----------------------------------------------------------------------------");
		}
		fieldWriter.append("\n* <region name= Inserts>");
		fieldWriter.append("\n	$USING EB.Template");
		fieldWriter.append("\n	$USING EB.SystemTables");
		fieldWriter.append("\n");
		fieldWriter.append("\n* </region>");
		fieldWriter.append("\n*-----------------------------------------------------------------------------");
		fieldWriter.append("\n*"+applicationName+" table id");
		fieldWriter.append("\nEB.Template.TableDefineid('"+applicationName.substring(3)+".ID',EB.Template.T24String)");
		
		i=0;
		String fieldNameForComment = new String(); 
		String property1Value = new String();
		//Write the fields
		while(i<numberOfFields) {
			
			fieldNameForComment = utilProcess.removeValueMarkers(fieldName[i]);
			if(property1[i].equals("NoInput")) {
				property1Value = "EB.Template.NoInput";
			}else if(property1[i].equals("Mandatory")) {
				property1Value = "EB.Template.Mandatory";
			}
			
			if(property2[i].equals("-") || property2[i].equals("CheckFile") ) {
				fieldWriter.append("\n");
				fieldWriter.append("\n*"+fieldNameForComment);
				fieldWriter.append("\nFieldName = '"+fieldName[i]+"'");
				fieldWriter.append("\nFieldLength = "+fieldLength[i]);
				fieldWriter.append("\nFieldType = ''");
				fieldWriter.append("\nFieldType<1> = '"+fieldType[i]+"'");
				if(property1[i].equals("NoInput")) {
					fieldWriter.append("\nFieldType<3> = 'NOINPUT'");
				}else if(property1[i].equals("Mandatory")) {
					fieldWriter.append("\nFieldType<3> = 'MANDATORY'");
				}
				fieldWriter.append("\nNeighbour = ''");
				fieldWriter.append("\nEB.Template.TableAddfielddefinition(FieldName,FieldLength,FieldType,Neighbour)");
				if(property2[i].equals("CheckFile")) {
					fieldWriter.append("\nEB.Template.FieldSetcheckfile('"+propertyValue[i]+"')");
				}
				
			}
			else if(property2[i].equals("Options")) {
				fieldWriter.append("\n");
				fieldWriter.append("\n*"+fieldNameForComment);
				fieldWriter.append("\nFieldName = '"+fieldName[i]+"'");
				fieldWriter.append("\nNeighbour = ''");
				fieldWriter.append("\nEB.Template.TableAddoptionsfield(FieldName,'"+propertyValue[i]+"',"+property1Value+",Neighbour)");
				
			}else if(property2[i].equals("Lookup")) {
				fieldWriter.append("\n");
				fieldWriter.append("\n*"+fieldNameForComment);
				fieldWriter.append("\nFieldName = '"+fieldName[i]+"'");
				fieldWriter.append("\nNeighbour = ''");
				fieldWriter.append("\nEB.Template.TableAddfieldwitheblookup(FieldName,'"+propertyValue[i]+"',Neighbour)");
			
			}else if(property2[i].equals("VirtualFile")) {
				fieldWriter.append("\n");
				fieldWriter.append("\n*"+fieldNameForComment);
				fieldWriter.append("\nFieldName = '"+fieldName[i]+"'");
			}
		
			i++;
		}
		//Write the reserved fields
		if(isReservedFieldRequired.equals("Yes")) {
			fieldWriter.append("\n");
			fieldWriter.append("\n*Reserved fields");
			Integer j = numberOfReservedFields;
			while(j>0) {
				fieldWriter.append("\nFieldName = 'RESERVED."+j+"'");
				fieldWriter.append("\nEB.Template.TableAddreservedfield(FieldName)");	
				j--;
			}
		}
		//Write Local ref field
		if(isLocalFieldRequired.equals("Yes")) {
			fieldWriter.append("\n");
			fieldWriter.append("\n* Local Ref field");
			fieldWriter.append("\nNeighbour =''");
			fieldWriter.append("\nEB.Template.TableAddlocalreferencefield(Neighbour)");
		}
		//Write override field
		if(isOverrideFieldRequired.equals("Yes")) {
			fieldWriter.append("\n");
			fieldWriter.append("\n* Override Field");
			fieldWriter.append("\nEB.Template.TableAddoverridefield()");
		}
		//Write Audit fields
		if(isAuditFieldRequired.equals("Yes")) {
			fieldWriter.append("\n");
			fieldWriter.append("\n*-----------------------------------------------------------------------------");
			fieldWriter.append("\nEB.Template.TableSetauditposition() ;* Audit fields");
			fieldWriter.append("\n*-----------------------------------------------------------------------------");
		}
		fieldWriter.append("\n");
		fieldWriter.append("\nRETURN");
		fieldWriter.append("\nEND");
		fieldWriter.append("\n");
		fieldWriter.append("\n");
		
		fieldWriter.close();	
				
		return "Success";
	}
	
	
}
