package main.java.sqlengine.basic.service;

import main.java.sqlengine.basic.QueryEngine;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * UpdateEngine class is used to process, validate, and update rows of the table in the database.
 */
public class UpdateEngine implements QueryEngine {

    /**
     * Method to process the Update Query Request from the user
     *
     * @param updateQuery string containing the UPDATE TABLE QUERY
     * @param dbName      the string containing the DATABASE SCHEMA NAME
     * @return true if insert is successful, false otherwise
     */
    @Override
    public boolean processUserQuery(String updateQuery, String dbName) {
        if (updateQuery != null && dbName != null) {
            if (validateUpdateQuery(updateQuery)) {
                processQuery(updateQuery, dbName);
            } else {
                return false;
            }
        } else {
            return false;
        }
        return true;

    }

    private boolean validateUpdateQuery(String updateQuery) {
        Pattern pattern = Pattern.compile("UPDATE (.*) SET (.*);", Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(updateQuery);
        if (!matcher.find()) {
            System.out.println("Invalid SQL UPDATE QUERY! Please try again!!");
            return false;
        }
        return true;
    }

    private boolean processQuery(String updateQuery, String dbName) {
        updateQuery = updateQuery.toLowerCase();
        Pattern pattern = Pattern.compile("UPDATE (.*) SET (.*);", Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(updateQuery);
        if (matcher.find()) {
            String tableName = matcher.group(1).strip();
            String settingInfo = matcher.group(2).strip();
            return checkAndUpdateValues(tableName, settingInfo, dbName);
        }
        return false;
    }

    private Map<String, String> extractSettingColumn(String settingInfo) {
        Pattern pattern = Pattern.compile("(.*)[\\s\\n\\t]*" + "=" + "[\\s\\n\\t]*(.*)", Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(settingInfo);
        Map<String, String> columnAndValueMap = new HashMap<>();
        if (matcher.find()) {
            String column = matcher.group(1).strip();
            String value = matcher.group(2).strip();
            if (value.startsWith("'") && value.endsWith("'")) {
                value = value.substring(1, value.length() - 1);
            }
            columnAndValueMap.put(column, value);
        }
        return columnAndValueMap;
    }


    private boolean checkAndUpdateValues(String tableName, String settingInfo, String dbName) {
        if (tableName.strip().isEmpty() || settingInfo.strip().isEmpty()) {
            System.out.println("Incorrect Query");
            return false;
        }
        String directoryPath = "database/" + dbName;
        String metadataPath = directoryPath + "/" + tableName + "_metadata.txt";
        File tableObj = new File(directoryPath + "/" + tableName + ".txt");
        if (tableObj.exists()) {
            try {
                BufferedReader reader = new BufferedReader(new FileReader(metadataPath));
                String metadataLine = reader.readLine();
                String[] tableColumns = metadataLine.split("\\$\\$");
                if (metadataLine != null) {
                    Map<String, String> columnAndValueSetInfo = extractSettingColumn(settingInfo);
                    if (columnAndValueSetInfo.isEmpty()) {
                        System.out.println("Incorrect Setting Values");
                        return false;
                    }
                    String settingColumn = new ArrayList<>(columnAndValueSetInfo.keySet()).get(0);
                    if (!(metadataLine.contains(settingColumn))) {
                        System.out.println("Column " + settingColumn + " does not exist in table " + tableName);
                        return false;
                    }
                    String dataTypeLine = reader.readLine();
                    String[] dataTypes = dataTypeLine.split("\\$\\$");
                    for (int i = 0; i < tableColumns.length; i++) {
                        if (tableColumns[i].equals(settingColumn)) {
                            String settingDatatype = dataTypes[i];
                            switch (settingDatatype) {
                                case "int": {
                                    try {
                                        int value = Integer.parseInt(columnAndValueSetInfo.get(settingColumn));
                                    } catch (NumberFormatException e) {
                                        System.out.println("Incorrect value for column " + settingColumn);
                                        return false;
                                    }
                                    break;
                                }
                                case "float": {
                                    try {
                                        float value = Float.parseFloat(columnAndValueSetInfo.get(settingColumn));
                                    } catch (NumberFormatException e) {
                                        System.out.println("Incorrect value for column " + settingColumn);
                                        return false;
                                    }
                                    break;
                                }
                                case "boolean": {
                                    if (!(columnAndValueSetInfo.get(settingColumn).toUpperCase() == "FALSE" ||
                                            columnAndValueSetInfo.get(settingColumn).toUpperCase() == "TRUE")) {
                                        System.out.println("Incorrect value for column " + settingColumn);
                                        return false;
                                    }
                                    break;
                                }
                                default: {
                                }
                            }
                        }
                    }
                    return updateAndWriteToTable(settingColumn, columnAndValueSetInfo, tableName, dbName);
                }
                reader.close();
            } catch (IOException e) {
                System.out.println("There was some error while processing your request. Please try again!");
                return false;
            }
        } else {
            System.out.println("Table " + tableName + " does not exist in " + dbName + " database!");
            return false;
        }
        return true;
    }

    private boolean updateAndWriteToTable(String settingColumn, Map<String, String> columnAndValueSetInfo,
                                          String tableName, String dbName) {
        String directoryPath = "database/" + dbName;
        String tablePath = directoryPath + "/" + tableName + ".txt";
        String updatedTable = "";
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(tablePath));
            String headerLine = reader.readLine();
            String[] headers = headerLine.split("\\$\\$");
            int columnIndexToBeModified = -1;
            for (int i = 0; i < headers.length; i++) {
                if (headers[i].equalsIgnoreCase(settingColumn)) {
                    columnIndexToBeModified = i;
                    break;
                }
            }
            updatedTable = updatedTable + headerLine;
            String row;
            while ((row = reader.readLine()) != null) {
                String[] rowArray = row.split("\\$\\$");
                rowArray[columnIndexToBeModified] = columnAndValueSetInfo.get(settingColumn);
                updatedTable = updatedTable + "\n" + String.join("$$", rowArray);
            }
        } catch (IOException e) {
            System.out.println("There was some error while processing your request. Please try again!");
            return false;
        }
        try {
            FileWriter tableWriter = new FileWriter(directoryPath + "/" + tableName + ".txt", false);
            tableWriter.write(updatedTable);
            tableWriter.close();
            System.out.println("Data updated to the table " + tableName + " successfully!");
        } catch (IOException e) {
            System.out.println("An error occurred while adding data to the table " + tableName);
            return false;
        }
        return true;
    }

}
