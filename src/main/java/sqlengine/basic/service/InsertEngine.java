package main.java.sqlengine.basic.service;

import main.java.sqlengine.basic.QueryEngine;

import java.io.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * InsertEngine class is used to process, validate, and insert data to the table in the database.
 */
public class InsertEngine implements QueryEngine {

    public InsertEngine() {

    }

    /**
     * Method to process the Insert Query Request from the user
     *
     * @param insertQuery string containing the INSERT TABLE QUERY
     * @param dbName      the string containing the DATABASE SCHEMA NAME
     * @return true if insert is successful, false otherwise
     */
    @Override
    public boolean processUserQuery(String insertQuery, String dbName) {
        if (insertQuery != null && dbName != null) {
            if (validateInsertQuery(insertQuery)) {
                processQuery(insertQuery, dbName);
            } else {
                return false;
            }
        } else {
            return false;
        }
        return true;
    }

    private boolean validateInsertQuery(String insertQuery) {
        Pattern pattern = Pattern.compile("INSERT INTO (\\w+)[\\s\\n\\r]*[(](.*)[)] VALUES[\\s\\n\\r]*[(](.*)[)];", Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(insertQuery);
        if (!matcher.find()) {
            System.out.println("Invalid SQL CREATE QUERY! Please try again!!");
            return false;
        }
        return true;
    }

    private boolean processQuery(String insertQuery, String dbName) {
        insertQuery = insertQuery.toLowerCase();
        Pattern pattern = Pattern.compile("INSERT INTO (\\w+)[\\s\\n\\r]*[(](.*)[)] VALUES[\\s\\n\\r]*[(](.*)[)];", Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(insertQuery);
        if (matcher.find()) {
            String tableName = matcher.group(1).strip();
            String columns = matcher.group(2).strip();
            String values = matcher.group(3).strip();
            return checkAndInsertValues(tableName, columns, values, dbName);
        }
        return false;
    }

    private boolean checkAndInsertValues(String tableName, String columns, String values, String dbName) {
        String directoryPath = "database/" + dbName;
        String metadataPath = directoryPath + "/" + tableName + "_metadata.txt";
        String[] queryColumnsList = columns.split(",");
        File tableObj = new File(directoryPath + "/" + tableName + ".txt");
        if (tableObj.exists()) {
            try {
                BufferedReader reader = new BufferedReader(new FileReader(metadataPath));
                String metadataLine = reader.readLine();
                String[] tableColumns = metadataLine.split("\\$\\$");
                if (metadataLine != null) {

                    // Checking if columns exist in the table to insert data
                    for (String column : queryColumnsList) {
                        if (!metadataLine.contains(column)) {
                            System.out.println("Table " + tableName + " does not contain column" + column + " !!!");
                            return false;
                        }
                    }

                    // Checking if datatypes match for the columns to insert data
                    String dataTypeLine = reader.readLine();
                    String[] dataTypes = dataTypeLine.split("\\$\\$");
                    String[] valueList = values.strip().split(",");
                    if (dataTypes.length != valueList.length) {
                        System.out.println("Query does not have proper columns and values count");
                        return false;
                    }
                    if (tableColumns.length != queryColumnsList.length) {
                        System.out.println("Query does not have proper columns count");
                        return false;
                    }
                    for (int i = 0; i < queryColumnsList.length; i++) {
                        switch (queryColumnsList[i]) {
                            case "int": {
                                try {
                                    int value = Integer.parseInt(valueList[i]);
                                } catch (NumberFormatException e) {
                                    System.out.println("Incorrect value for column " + queryColumnsList[i]);
                                    return false;
                                }
                                break;
                            }
                            case "float": {
                                try {
                                    float value = Float.parseFloat(valueList[i]);
                                } catch (NumberFormatException e) {
                                    System.out.println("Incorrect value for column " + queryColumnsList[i]);
                                    return false;
                                }
                                break;
                            }
                            case "boolean": {
                                if (!(valueList[i].toUpperCase() == "FALSE" || valueList[i].toUpperCase() == "TRUE")) {
                                    System.out.println("Incorrect value for column " + queryColumnsList[i]);
                                    return false;
                                }
                                break;
                            }
                            default: {
                            }
                        }
                    }
                }
                reader.close();
            } catch (IOException e) {
                System.out.println("There was some error while processing your request. Please try again!");
                return false;
            }
            try {
                FileWriter tableWriter = new FileWriter(directoryPath + "/" + tableName + ".txt", true);
                String valueText = String.join("$$", values.split(","));
                tableWriter.write("\n" + valueText);
                tableWriter.close();
                System.out.println("Data inserted to the table " + tableName + " successfully!");
            } catch (IOException e) {
                System.out.println("An error occurred while adding data to the table " + tableName);
                return false;
            }
        } else {
            System.out.println("Table " + tableName + " does not exist in " + dbName + " database!");
            return false;
        }
        return true;
    }

}