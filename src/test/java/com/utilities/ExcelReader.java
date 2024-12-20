package com.utilities;


import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.poi.ss.usermodel.DataFormatter;

import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class ExcelReader {

	 private static final ReentrantLock lock = new ReentrantLock();

	    public static List<String> getDataFromExcel(String sheetName, List<String> columnNames, String inputDataPath) throws IOException {
	        List<String> allColumnsData = new ArrayList<>();
	        lock.lock();
	        try (FileInputStream fis = new FileInputStream(inputDataPath);
	             XSSFWorkbook workbook = new XSSFWorkbook(fis)) {

	            XSSFSheet sheet = workbook.getSheet(sheetName);

	            if (sheet == null) {
	                throw new IllegalArgumentException("Sheet with name " + sheetName + " not found in the Excel file");
	            }

	            DataFormatter formatter = new DataFormatter(Locale.US);
	            XSSFRow headerRow = sheet.getRow(1); // Assuming header is in the first row

	            if (headerRow == null) {
	                throw new IllegalArgumentException("Header row not found in the sheet " + sheetName);
	            }

	            // Find the column indices for all column names
	            int[] columnIndices = new int[columnNames.size()];
	            for (int i = 0; i < columnNames.size(); i++) {
	                columnIndices[i] = findColumnIndex(headerRow, columnNames.get(i), formatter);
	            }

	            // Read data for each column
	            for (int columnIndex : columnIndices) {
	                for (int i = 2; i <= sheet.getLastRowNum(); i++) {
	                    XSSFRow row = sheet.getRow(i);
	                    if (row != null) {
	                        XSSFCell cell = row.getCell(columnIndex, XSSFRow.MissingCellPolicy.RETURN_BLANK_AS_NULL);
	                        if (cell != null) {
	                            String cellValue = formatter.formatCellValue(cell);
	                            allColumnsData.add(cellValue);
	                        }
	                    }
	                }
	            }
	        } finally {
	            lock.unlock();
	        }

	        return allColumnsData;
	    }

	    private static int findColumnIndex(XSSFRow headerRow, String columnName, DataFormatter formatter) {
	        for (int j = 0; j < headerRow.getLastCellNum(); j++) {
	            XSSFCell cell = headerRow.getCell(j, XSSFRow.MissingCellPolicy.RETURN_BLANK_AS_NULL);
	            if (cell != null && formatter.formatCellValue(cell).trim().equalsIgnoreCase(columnName)) {
	                return j;
	            }
	        }
	        throw new IllegalArgumentException("Column with name " + columnName + " not found in the sheet");
	    }
}