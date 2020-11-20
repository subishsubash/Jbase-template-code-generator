package com.temenos.template.process;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;

import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ApplicationRoutine {

	
	@Autowired
	private UtilProcess utilProcess;
	
	//Method to create .b(Main routine)
	public String createApplication(JSONObject response) throws Exception{
		//Get the required data from JSONObject.
		String applicationName = (String) response.get("applicationName");
		String moduleName = (String) response.get("moduleName");
		String componentName = (String) response.get("componentName");
		String location = (String) response.get("location");
		String isModificationHistory = (String) response.get("isModificationHistoryRequired");
		String tableTitle = (String) response.get("tableTitle");
		tableTitle = utilProcess.nullCheck(tableTitle);
		String stereotype = (String) response.get("stereotype");
		stereotype = utilProcess.nullCheck(stereotype);
		String subProduct = (String) response.get("subProduct");
		subProduct = utilProcess.nullCheck(subProduct);
		String classification = (String) response.get("classification");
		classification = utilProcess.nullCheck(classification);
		String systemClearfile = (String) response.get("systemClearfile");
		systemClearfile = utilProcess.nullCheck(systemClearfile);
		String relatedFiles = (String) response.get("relatedFiles");
		relatedFiles = utilProcess.nullCheck(relatedFiles);
		String postClosingFile = (String) response.get("postClosingFile");
		postClosingFile = utilProcess.nullCheck(postClosingFile);
		String equatePrefix = (String) response.get("equatePrefix");
		equatePrefix = utilProcess.nullCheck(equatePrefix);
		String tableIdPrefix = (String) response.get("tableIdPrefix");
		tableIdPrefix = utilProcess.nullCheck(tableIdPrefix);
		String blockedFunctions = (String) response.get("blockedFunctions");
		blockedFunctions = utilProcess.nullCheck(blockedFunctions);
		String triggerfield = (String) response.get("triggerfield");
		triggerfield = utilProcess.nullCheck(triggerfield);
		String CNsOperation = (String) response.get("CNsOperation");
		CNsOperation = utilProcess.nullCheck(CNsOperation);
		
		//Convert into camelcase
		String appNameInCamelCase = utilProcess.convertCamelCase(applicationName);
		
		//get the base location
		location = utilProcess.getBaseLocation(componentName, location, moduleName);
				
		String componentNameInCamelCase = componentName.replace('.', '_');
		File appFile = new File(location+"/Source/Private/"+applicationName+".b"); //Open file to write
		appFile.getParentFile().mkdirs();
		FileWriter appFileWriter =  new FileWriter(appFile); // Instance of file write with append mode(by passing 2nd param as true)
		BufferedWriter appWriter = new BufferedWriter(appFileWriter);
		String dateString = utilProcess.getTodayDate();
		
		//Write .b(Main routine)
		appWriter.append("*-----------------------------------------------------------------------------");
		appWriter.append("\n$PACKAGE "+componentName);
		appWriter.append("\n*");
		appWriter.append("\n*Implementation of "+componentName+"."+appNameInCamelCase.substring(2));
		appWriter.append("\n*");
		appWriter.append("\nSUBROUTINE "+applicationName);
		appWriter.append("\n* Developed By	: ");
		appWriter.append("\n* Program Name	: "+applicationName);
		appWriter.append("\n* Module Name	: "+moduleName);
		appWriter.append("\n* @package 		: "+componentNameInCamelCase);
		appWriter.append("\n*-----------------------------------------------------------------------------");
		appWriter.append("\n* <desc>");
		appWriter.append("\n* Program Description");
		appWriter.append("\n* Template Definition for "+applicationName);
		appWriter.append("\n* </desc>");
		appWriter.append("\n*-----------------------------------------------------------------------------");
		if(isModificationHistory.equals("Yes")) {
			Integer enhancementNo = (Integer) response.get("enhancementNo");
			Integer taskNo = (Integer) response.get("taskNumber");
			appWriter.append("\n* Modification History :");
			appWriter.append("\n*-----------------------");
			appWriter.append("\n*");
			appWriter.append("\n*"+dateString+" - Enhancement "+enhancementNo+" / Task "+taskNo);
			appWriter.append("\n*				Table Definition for "+applicationName);
			appWriter.append("\n*");
			appWriter.append("\n*-----------------------------------------------------------------------------");
		}
		appWriter.append("\n* <region name= Inserts>");
		appWriter.append("\n	$USING EB.Template");
		appWriter.append("\n	$USING EB.SystemTables");
		appWriter.append("\n");
		appWriter.append("\n* </region>");
		appWriter.append("\n*-----------------------------------------------------------------------------");
		appWriter.append("\n	EB.Template.setTableName('"+applicationName+"') ;* Full application name including product prefix");
		appWriter.append("\n 	EB.Template.setTableTitle('"+tableTitle+"')  ;* Screen title");
		appWriter.append("\n	EB.Template.setTableStereotype('"+stereotype+"')                ;* H, U, L, W or T");
		appWriter.append("\n 	EB.Template.setTableProduct('"+moduleName+"')                  ;* Must be on EB.PRODUCT");
		appWriter.append("\n 	EB.Template.setTableSubproduct('"+subProduct+"')                 ;* Must be on EB.SUB.PRODUCT");	
		appWriter.append("\n 	EB.Template.setTableClassification('"+classification+"')          ;* As per FILE.CONTROL");
		appWriter.append("\n 	EB.Template.setTableSystemclearfile('"+systemClearfile+"')            ;* As per FILE.CONTROL");
		appWriter.append("\n	EB.Template.setTableRelatedfiles('"+relatedFiles+"')               ;* Related Files");
		appWriter.append("\n	EB.Template.setTableIspostclosingfile('"+postClosingFile+"')          ;* postCLosing File");
		appWriter.append("\n	EB.Template.setTableEquateprefix('"+equatePrefix+"')         ;* Use to create field with prefix.");
		appWriter.append("\n 	EB.Template.setTableIdprefix('"+tableIdPrefix+"')                   ;* Table Id prefix");
		appWriter.append("\n*-----------------------------------------------------------------------------");
		appWriter.append("\n 	EB.Template.setTableBlockedfunctions('"+blockedFunctions+"')          ;* Space delimited list of blocked functions");
		appWriter.append("\n*-----------------------------------------------------------------------------");
		appWriter.append("\n	EB.Template.setTableTriggerfield('"+triggerfield+"')               ;* Trigger Field");
		appWriter.append("\n 	EB.SystemTables.setCNsOperation('"+CNsOperation+"') ;* Enable NS");
		appWriter.append("\n");
		appWriter.append("\nRETURN");
		appWriter.append("\nEND");
		appWriter.append("\n");
		appWriter.append("\n");
		appWriter.close();
		return "Success";
	}
	
}
