package com.utilities;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.concurrent.locks.ReentrantLock;


import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;



import java.io.FileOutputStream;
public class ExcelWriter {

	private static final ReentrantLock lock = new ReentrantLock();

    public static void writeToExcel(String sheetName, String recipeId, String recipeName, String recipeCategory,
                                    String foodCategory, String ingredients, String preparationTime, String cookingTime, String tag,
                                    String noOfServings, String cuisineCategory, String recipeDescription, String preparationMethod,
                                    String nutrientValues, String recipeUrl, String filePath) throws IOException {
        lock.lock();
        try (FileInputStream fileInputStream = new FileInputStream(filePath);
             Workbook workbook = WorkbookFactory.create(fileInputStream)) {

            Sheet sheet = workbook.getSheet(sheetName);
            if (sheet == null) {
                sheet = workbook.createSheet(sheetName);
            }
            System.out.println("Sheet Name: " + sheet.getSheetName());

            // Create a new row
            int lastRowNum = sheet.getLastRowNum();
            Row row = sheet.createRow(lastRowNum + 1);

            // Write data to the row
            row.createCell(0).setCellValue(recipeId);
            row.createCell(1).setCellValue(recipeName);
            row.createCell(2).setCellValue(recipeCategory);
            row.createCell(3).setCellValue(foodCategory);
            row.createCell(4).setCellValue(ingredients);
            row.createCell(5).setCellValue(preparationTime);
            row.createCell(6).setCellValue(cookingTime);
            row.createCell(7).setCellValue(tag);
            row.createCell(8).setCellValue(noOfServings);
            row.createCell(9).setCellValue(cuisineCategory);
            row.createCell(10).setCellValue(recipeDescription);
            row.createCell(11).setCellValue(preparationMethod);
            row.createCell(12).setCellValue(nutrientValues);
            row.createCell(13).setCellValue(recipeUrl);

            // Ensure workbook is written to file output stream
            try (FileOutputStream outputStream = new FileOutputStream(filePath)) {
                workbook.write(outputStream);
            }

        } catch (IOException e) {
            throw new IOException("Error writing to Excel file: " + e.getMessage(), e);
        } finally {
            lock.unlock();
        }
    }
}
