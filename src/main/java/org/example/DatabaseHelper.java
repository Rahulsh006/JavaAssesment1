package org.example;

import com.google.gson.Gson;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper {

    private static final String URL = "jdbc:mysql://localhost:3306/student_db";
    private static final String USER = "root";
    private static final String PASSWORD = "root";

    // Method to save a student into the MySQL database
    public void saveStudentToDatabase(Student student) {
        if (studentExists(student.getAdmissionNumber())) return;

        String sql = "INSERT INTO students (name, admission_number, physics_mark, chemistry_mark, maths_mark) VALUES (?, ?, ?, ?, ?)";
        try (Connection connection = getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, student.getName());
            preparedStatement.setString(2, student.getAdmissionNumber());
            preparedStatement.setInt(3, student.getPhysicsMark());
            preparedStatement.setInt(4, student.getChemistryMark());
            preparedStatement.setInt(5, student.getMathsMark());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Database error: " + e.getMessage());
        }
    }

    // Method to check if a student already exists in the database
    private boolean studentExists(String admissionNumber) {
        String sql = "SELECT COUNT(*) FROM students WHERE admission_number = ?";
        try (Connection connection = getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, admissionNumber);
            ResultSet resultSet = preparedStatement.executeQuery();
            return resultSet.next() && resultSet.getInt(1) > 0;
        } catch (SQLException e) {
            System.err.println("Database error: " + e.getMessage());
        }
        return false;

    }

    // Method to find a student by admission number or name
    public String findStudentByAdmissionNumberOrName(String searchKey) {
        String sql = "SELECT * FROM students WHERE admission_number = ? OR name = ?";
        try (Connection connection = getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, searchKey);
            preparedStatement.setString(2, searchKey);
            ResultSet resultSet = preparedStatement.executeQuery();

            List<Student> foundStudents = new ArrayList<>();
            while (resultSet.next()) {
                foundStudents.add(new Student(
                        resultSet.getString("name"),
                        resultSet.getString("admission_number"),
                        resultSet.getInt("physics_mark"),
                        resultSet.getInt("chemistry_mark"),
                        resultSet.getInt("maths_mark")));
            }

            return foundStudents.isEmpty() ? "SORRY! STUDENT NOT FOUND" : new Gson().toJson(foundStudents);

        } catch (SQLException e) {
            System.err.println("Database error: " + e.getMessage());
        }
        return "{ \"message\": \"Error retrieving student data\" }";
    }

    // Method to establish connection to MySQL database
    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}
