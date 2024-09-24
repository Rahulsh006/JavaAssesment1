package org.example;

import com.google.gson.Gson;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        List<Student> students = readExcelFile("C:\\Users\\RahulSH\\IdeaProjects\\StudentPro2\\src\\main\\resources\\studentdata.xlsx");
        if (students.isEmpty()) {
            System.out.println("No students found in Excel file.");
            return;
        }

        String jsonData = convertStudentsToJSON(students);
        System.out.println("\nWELCOME TO ABC SCHOOL!!!!");
        System.out.println("\n------------------------------------------------");
        System.out.println("COMPLETE STUDENT DATA IN JSON FORMAT: " + jsonData);



        DatabaseHelper dbHelper = new DatabaseHelper();

        for (Student student : students) {
            dbHelper.saveStudentToDatabase(student);
        }

        searchMenu(dbHelper);
    }

    // Excel reading and JSON conversion methods
    public static List<Student> readExcelFile(String excelFileName) {
        List<Student> students = new ArrayList<>();
        try (InputStream excelFile = new FileInputStream(excelFileName);
             Workbook workbook = new XSSFWorkbook(excelFile)) {
            Sheet sheet = workbook.getSheetAt(0);
            for (Row row : sheet) {
                if (row.getRowNum() == 0) continue;
                students.add(new Student(
                        getCellValueAsString(row.getCell(0)),
                        getCellValueAsString(row.getCell(1)),
                        getCellValueAsInt(row.getCell(2)),
                        getCellValueAsInt(row.getCell(3)),
                        getCellValueAsInt(row.getCell(4))));
            }
        } catch (Exception e) {
            System.err.println("Error reading Excel file: " + e.getMessage());
        }
        return students;
    }

    public static String convertStudentsToJSON(List<Student> students) {
        return new Gson().toJson(students);
    }

    private static void searchMenu(DatabaseHelper dbHelper) {
        Scanner scanner = new Scanner(System.in);
        boolean exit = false;

        while (!exit) {
            System.out.println("\nPLEASE SELECT YOUR OPTION FOR STUDENT SEARCHING:");
            System.out.println("1. SEARCH BY ADMISSION NUMBER");
            System.out.println("2. SEARCH BY NAME");
            System.out.println("3. EXIT FROM SEARCH");
            System.out.print("ENTER YOUR CHOICE: ");
            int choice = scanner.nextInt();
            scanner.nextLine();

            if (choice == 3) {
                exit = true;
                System.out.println("EXITING FROM THE PROGRAM, BYE!!!!!");
            } else {
                System.out.print(choice == 1 ? "ENTER ADMISSION NUMBER: " : "ENTER NAME: ");
                String searchKey = scanner.nextLine();
                String studentData = dbHelper.findStudentByAdmissionNumberOrName(searchKey);
                System.out.println("SEARCHED STUDENT: " + studentData);
            }
        }
    }

    // Utility methods
    private static String getCellValueAsString(Cell cell) {
        if (cell == null) return "";
        return cell.getCellType() == CellType.STRING ? cell.getStringCellValue() : String.valueOf((int) cell.getNumericCellValue());
    }

    private static int getCellValueAsInt(Cell cell) {
        if (cell == null) return 0;
        return cell.getCellType() == CellType.NUMERIC ? (int) cell.getNumericCellValue() : Integer.parseInt(cell.getStringCellValue());
    }
}


