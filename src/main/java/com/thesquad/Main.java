package com.thesquad;  // Adjust the package name as necessary

import com.thesquad.connection.DBConnection;

public class Main {
    public static void main(String[] args) {
        DBConnection dbConnection = null;
        try {
            // Create a new DBConnection instance
            dbConnection = new DBConnection();

            // Check if the connection is established
            if (dbConnection.getConnection() != null) {
                System.out.println("Database connection is successful!");
            } else {
                System.out.println("Failed to make a connection!");
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("An error occurred while trying to connect to the database.");
        } finally {
            // Close the connection if it was established
            if (dbConnection != null ) {
                dbConnection.closeConnection();
            }
        }
    }
}
