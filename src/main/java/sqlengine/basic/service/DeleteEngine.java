package main.java.sqlengine.basic.service;

import main.java.sqlengine.basic.QueryEngine;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * DeleteEngine class is used to process, validate, and delete data from the tables in the database.
 */
public class DeleteEngine implements QueryEngine {

    /**
     * Method to process the Delete Query Request from the user
     *
     * @param deleteQuery string containing the DELETE TABLE QUERY
     * @param dbName      the string containing the DATABASE SCHEMA NAME
     * @return true if delete is successful, false otherwise
     */
    @Override
    public boolean processUserQuery(String deleteQuery, String dbName) {
        if (deleteQuery != null && dbName != null) {
            if (validateDeleteQuery(deleteQuery)) {
                String tableName = extractTableName(deleteQuery);
                if (tableName != null) {
                    return checkAndDeleteData(tableName, dbName);
                }
                return false;
            }
            return false;
        }
        return false;

    }

    private boolean checkAndDeleteData(String tableName, String dbName) {
        String directoryPath = "database/" + dbName;
        File tableObj = new File(directoryPath + "/" + tableName + ".txt");
        if (tableObj.exists()) {
            try {
                FileWriter tableWriter = new FileWriter(directoryPath + "/" + tableName + ".txt", false);
                tableWriter.write("");
                tableWriter.close();
                System.out.println("Data deleted from the table " + tableName + " successfully!");
            } catch (IOException e) {
                System.out.println("Table " + tableName + " does not exist");
                return false;
            }
        } else {
            System.out.println("Table " + tableName + " does not exist in " + dbName + " database!");
            return false;
        }
        return true;
    }

    private boolean validateDeleteQuery(String selectQuery) {
        Pattern pattern = Pattern.compile("DELETE FROM (.*);", Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(selectQuery);
        if (!matcher.find()) {
            System.out.println("Invalid SQL DELETE QUERY! Please try again!!");
            return false;
        }
        return true;
    }

    private String extractTableName(String selectQuery) {
        Pattern pattern = Pattern.compile("DELETE FROM (.*);", Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(selectQuery);
        if (matcher.find()) {
            return matcher.group(1).strip();
        }
        return null;
    }
}
