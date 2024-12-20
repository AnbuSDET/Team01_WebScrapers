package com.utilities;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;


import java.io.FileOutputStream;
public class ExcelWriter {

	
	public XSSFRow row;
	public XSSFCell cell;

	public void WriteData(String sheetname, int rownum, int column, String Recipe_Id, String Recipe_Name,
			String Recipe_Category,String Food_Category, String Ingredients, String preparation_Time, String cooking_Time, String tag,
			String noOfServings, String cuisine_category, String recipe_Description,String prep_method,String nutrient_values,String Recipe_url,String filePath) throws IOException {


		FileInputStream inputStream = new FileInputStream(new File(filePath));
		XSSFWorkbook wb = new XSSFWorkbook(inputStream);

		XSSFSheet sheet = wb.getSheet(sheetname);

		row = sheet.getRow(rownum + 1);
		cell = row.createCell(column);
		cell.setCellValue(Recipe_Id);

		row = sheet.getRow(rownum + 2);
		cell = row.createCell(column);
		cell.setCellValue(Recipe_Name);

		row = sheet.getRow(rownum + 3);
		cell = row.createCell(column);
		cell.setCellValue(Recipe_Category);
		
		row = sheet.getRow(rownum + 4);
		cell = row.createCell(column);
		cell.setCellValue(Food_Category);

		row = sheet.getRow(rownum + 5);
		cell = row.createCell(column);
		cell.setCellValue(Ingredients);

		row = sheet.getRow(rownum + 6);
		cell = row.createCell(column);
		cell.setCellValue(preparation_Time);

		row = sheet.getRow(rownum + 7);
		cell = row.createCell(column);
		cell.setCellValue(cooking_Time);

		row = sheet.getRow(rownum + 8);
		cell = row.createCell(column);
		cell.setCellValue(tag);

		row = sheet.getRow(rownum + 9);
		cell = row.createCell(column);
		cell.setCellValue(noOfServings);

		row = sheet.getRow(rownum + 10);
		cell = row.createCell(column);
		cell.setCellValue(cuisine_category);

		row = sheet.getRow(rownum + 11);
		cell = row.createCell(column);
		cell.setCellValue(recipe_Description);
		
		row = sheet.getRow(rownum + 12);
		cell = row.createCell(column);
		cell.setCellValue(prep_method);
		
		row = sheet.getRow(rownum + 13);
		cell = row.createCell(column);
		cell.setCellValue(nutrient_values);
		
		row = sheet.getRow(rownum + 14);
		cell = row.createCell(column);
		cell.setCellValue(Recipe_url);
		
		

		FileOutputStream outputStream = new FileOutputStream(filePath);
		wb.write(outputStream);
		wb.close();
		inputStream.close();
		outputStream.close();

	}
}
