package com.utilities;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.testng.annotations.Test;



	public class DataSupplier {
	    public static void readExcelData() {
	        String filePath = "./src/test/resources/excel/IngredientsAndComorbidities-ScrapperHackathon.xlsx";

	        File excelFile = new File(filePath);

	        if (!excelFile.exists()) {
	            System.err.println("The file does not exist: " + filePath);
	            return;
	        }

	        try (FileInputStream fis = new FileInputStream(excelFile);
	             XSSFWorkbook workbook = new XSSFWorkbook(fis)) {

	            // Print available sheet names
	            workbook.forEach(sheet -> System.out.println("Sheet name: " + sheet.getSheetName()));

	            // Get the desired sheet
	            XSSFSheet sheet = workbook.getSheet("Filter -1 Allergies - Bonus Poi");
	            if (sheet == null) {
	                System.err.println("The sheet does not exist: Filter -1 Allergies - Bonus Poi");
	                return;
	            }

	            // Initialize DataFormatter to handle different cell types
	            DataFormatter formatter = new DataFormatter();

	            // Iterate through each row and cell
	            for (Row row : sheet) {
	                // Skip empty rows
	                if (isRowEmpty(row)) continue;

	                StringBuilder rowData = new StringBuilder();
	                for (Cell cell : row) {
	                    rowData.append(formatter.formatCellValue(cell)).append("\t");
	                }
	                System.out.println(rowData);
	            }

	        } catch (IOException e) {
	            System.err.println("Error reading the Excel file: " + e.getMessage());
	        }
	    }

	    // Utility method to check if a row is empty
	    private static boolean isRowEmpty(Row row) {
	        if (row == null) return true;
	        for (Cell cell : row) {
	            if (cell != null && !cell.toString().trim().isEmpty()) {
	                return false;
	            }
	        }
	        return true;
	    }
}
