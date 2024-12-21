package com.utilities;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class CreateExcel {

	public static void createExcelWorkBook() {

		String path = System.getProperty("user.dir");
		Path filePath = Paths.get(path + "/src/test/resources/output/FinalRecipeScraping.xlsx");

		// Check if the file exists
		if (Files.exists(filePath)) {
			System.out.println("Excel file already exists at: " + filePath);
			return;
		}

		try (Workbook workbook = new XSSFWorkbook()) {
			String[] sheetNames = {"LFVEliminate", "LFVAdd","LFVAddNotFullyVegan","LFVRecipesToAvoid","LCHFEliminate","LCHFAdd","LCHFFoodProcessing","Allergy" };

			for (String sheetName : sheetNames) {
				Sheet sheet = workbook.createSheet(sheetName);
				createHeaderRow(sheet);
			}

			try (FileOutputStream fileOut = new FileOutputStream(filePath.toFile())) {
				workbook.write(fileOut);
				System.out.println("Excel sheet created successfully at: " + filePath);
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static void createHeaderRow(Sheet sheet) {
		Row headerRow = sheet.createRow(0);
		String[] headers = { "Recipe ID", "Recipe Name", "Recipe Category", "Food Category", "Ingredients",
				"Preparation Time", "Cooking Time", "Tag", "No of servings", "Cuisine category", "Recipe Description",
				"Preparation method", "Nutrient values", "Recipe URL" };

		for (int i = 0; i < headers.length; i++) {
			headerRow.createCell(i).setCellValue(headers[i]);
		}
	}
}