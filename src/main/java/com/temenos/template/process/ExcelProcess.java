package com.temenos.template.process;

import java.util.Iterator;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Component
public class ExcelProcess {

	@Autowired
	private TemplateProcess templateProcess;
	
	@Autowired
	private ZipProcess zipProcess;
	

	//Upload excel 
	@SuppressWarnings({ "incomplete-switch", "unchecked", "resource" })
	public String uploadExcel(MultipartFile excelFile) throws Exception {
		
		
		String[] jsonKeysFields = {"name","dataType","length","property1","property2","propertyValue","fieldDescription","fieldValidation"};
		String[] jsonKeysComponent = {"header","applicationName","moduleName","componentName","isReservedFieldRequired","numberOfReservedFields","isAuditFieldRequired","isLocalFieldRequired","isOverrideFieldRequired","isModificationHistoryRequired","enhancementNo","taskNumber","tableTitle","stereotype","subProduct","classification","systemClearfile","relatedFiles","postClosingFile","equatePrefix","tableIdPrefix","blockedFunctions","triggerfield","CNsOperation","numberOfFields","isHelpText","tableDescription"};
		
		
		//Declare required POI variables 
		XSSFRow row;
		XSSFWorkbook workbook = new XSSFWorkbook(excelFile.getInputStream()); //Read the excel file
		XSSFSheet spreadsheetTable = workbook.getSheetAt(0); //Get the 1st Sheet Component & Table 
		Iterator <Row> rowIteratorTable = spreadsheetTable.iterator(); //iterator for the 1st sheet.
		
		//Loop will iterate all the rows in 1st Sheet (Component & Table)
		int rowNumber =0;
		JSONObject jsonObject =  new JSONObject();
		while (rowIteratorTable.hasNext())
		{
			
			row = (XSSFRow) rowIteratorTable.next(); //Get the each row from the sheet
			if(rowNumber == 0) {
				rowNumber++;
				continue;
			}
			//Read the 2nd column of each row to get the value.
			Cell cell = row.getCell(1,Row.MissingCellPolicy.CREATE_NULL_AS_BLANK); // Make null cell values as blank.
			
			switch (cell.getCellType())
			{
				case NUMERIC: //get the numeric value
					int valueInInt =(int) cell.getNumericCellValue();
					jsonObject.put(jsonKeysComponent[rowNumber],valueInInt);
					break;
				case STRING: // to get the string value.
					String value = cell.getStringCellValue();
					jsonObject.put(jsonKeysComponent[rowNumber],value);
					break;
				case BLANK: //Consider the null values as - in json
					String valueBlank = "-";
					jsonObject.put(jsonKeysComponent[rowNumber],valueBlank);
					break;
			}
			rowNumber++;
		}
		
		rowNumber=0;
		XSSFSheet spreadsheetField = workbook.getSheetAt(1);  //Get the 2nd sheet Field.		
		Iterator<Row> rowIteratorField = spreadsheetField.iterator(); //iterator for the 2st sheet
		
		JSONArray fieldProperties = new JSONArray();
		//Loop will iterate all the rows in 2st Sheet (Fields)
		while (rowIteratorField.hasNext())
		{
			
			JSONObject fieldObject = new  JSONObject();
			row = (XSSFRow) rowIteratorField.next();
			if(rowNumber == 0) {
				rowNumber++;
				continue;

			}
			for(int i=0;i<8;i++) {
				Cell cell = row.getCell(i,Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
				switch (cell.getCellType())
				{
				case NUMERIC:
					int valueInInt =(int) cell.getNumericCellValue();
					fieldObject.put(jsonKeysFields[i],valueInInt);
					break;
				case STRING:
					String value = cell.getStringCellValue();
					fieldObject.put(jsonKeysFields[i],value);
					break;
				case BLANK:
					String valueBlank = "-";
					fieldObject.put(jsonKeysFields[i],valueBlank);
					break;
				}
			}
			fieldProperties.add(fieldObject);
		}
		jsonObject.put("fieldProperties",fieldProperties);
		
		jsonObject.put("location",System.getProperty("java.io.tmpdir"));
		templateProcess.createTemplate(jsonObject,true);
		return zipProcess.createZipFolder(jsonObject.get("location").toString(),jsonObject.get("moduleName").toString());
	}
}
