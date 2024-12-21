package com.utilities;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class ExcelValueCheck {

	// ********************** Method to get the row value of "Recipe ID" column
	// based on a specific value*****************

	private static final ReentrantLock lock = new ReentrantLock();

	public static boolean recipeExistsInExcelCheck(String sheetName, String valueToCheck, String filePath) {
		lock.lock();
		try (FileInputStream fileInputStream = new FileInputStream(filePath);
				XSSFWorkbook workbook = new XSSFWorkbook(fileInputStream)) {

			XSSFSheet sheet = workbook.getSheet(sheetName);
			if (sheet == null) {
				throw new IllegalArgumentException("Sheet with name '" + sheetName + "' not found in the Excel file.");
			}

			// Check if the value exists in the first column
			for (int rowIndex = 1; rowIndex <= sheet.getLastRowNum(); rowIndex++) {
				XSSFRow row = sheet.getRow(rowIndex);
				if (row != null) {
					Cell firstColumnCell = row.getCell(0); // Assuming the first column is at index 0
					if (firstColumnCell != null
							&& firstColumnCell.getStringCellValue().equalsIgnoreCase(valueToCheck)) {
						return true; // Value found in the first column
					}
				}
			}

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			lock.unlock();
		}
		return false;

	}
}