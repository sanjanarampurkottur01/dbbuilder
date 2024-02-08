package main.java.sqlengine.basic.service;

import main.java.sqlengine.basic.QueryEngine;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * SelectEngine class is used to process, validate, and fetch data from the table in the database.
 */
public class SelectEngine implements QueryEngine {

    /**
     * Method to process the Select Query Request from the user
     *
     * @param selectQuery string containing the SELECT TABLE QUERY
     * @param dbName      the string containing the DATABASE SCHEMA NAME
     * @return true if insert is successful, false otherwise
     */
    @Override
    public boolean processUserQuery(String selectQuery, String dbName) {
        if (selectQuery != null && dbName != null) {
            if (validateSelectQuery(selectQuery)) {
                String tableName = extractTableName(selectQuery);
                List<String> fetchColumns = extractFetchColumns(selectQuery);
                return checkAndFetchData(tableName, fetchColumns, dbName);
            }
            return false;
        }
        return false;

    }

    private boolean validateSelectQuery(String selectQuery) {
        Pattern pattern = Pattern.compile("select[\\s\\r\\n]+(.*)[\\s\\r\\n]+from[\\s\\r\\n]*(\\w+)" +
                "([\\s\\r\\n]+where[\\s\\r\\n]+(\\w+)[\\s\\r\\n]*(=|<=|>=|<>|<|>)[\\s\\r\\n]*(" +
                "['](\\w+)[']|(\\w+)))?;", Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(selectQuery);
        if (!matcher.find()) {
            System.out.println("Invalid SQL SELECT QUERY! Please try again!!");
            return false;
        }
        return true;
    }

    private String extractTableName(String selectQuery) {
        Pattern pattern = Pattern.compile("FROM(.*);", Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(selectQuery);
        if (matcher.find()) {
            return matcher.group(1).strip();
        }
        return null;
    }

    private List<String> extractFetchColumns(String selectQuery) {
        Pattern pattern = Pattern.compile("SELECT(.*)FROM", Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(selectQuery);
        List<String> fetchColumns = new ArrayList<>();
        if (matcher.find()) {
            String rawColumns = matcher.group(1).strip();
            pattern = Pattern.compile(".*[*].*", Pattern.CASE_INSENSITIVE);
            matcher = pattern.matcher(rawColumns);
            if (matcher.find()) {
                fetchColumns.add("ALL");
            } else {
                String[] columns = rawColumns.split(",");
                for (String column : columns) {
                    if (!column.strip().isEmpty()) {
                        fetchColumns.add(column.strip());
                    }
                }
            }
        }
        return fetchColumns;
    }

    private boolean checkAndFetchData(String tableName, List<String> fetchColumns, String dbName) {
        if (tableName.strip().isEmpty() || fetchColumns.isEmpty()) {
            System.out.println("Incorrect SELECT Query");
            return false;
        }
        String directoryPath = "database/" + dbName;
        String metadataPath = directoryPath + "/" + tableName + "_metadata.txt";
        try {
            File tableObj = new File(directoryPath + "/" + tableName + ".txt");
            if (!tableObj.createNewFile()) {
                try {
                    BufferedReader reader = new BufferedReader(new FileReader(metadataPath));
                    String metadataLine = reader.readLine();
                    if (metadataLine != null) {
                        if (!fetchColumns.contains("ALL")) {
                            for (String column : fetchColumns) {
                                if (!(metadataLine.contains(column))) {
                                    System.out.println("Column " + column + " does not exist in table " + tableName);
                                    return false;
                                }
                            }
                        }
                        return fetchDataFromTable(tableName, fetchColumns, dbName);
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
        } catch (IOException e) {
            System.out.println("There was some error while processing your request. Please try again!");
            return false;
        }
        return true;
    }

    private boolean fetchDataFromTable(String tableName, List<String> fetchColumns, String dbName) {
        String directoryPath = "database/" + dbName;
        String tablePath = directoryPath + "/" + tableName + ".txt";
        String fetchDataWithALL = "";
        String fetchData = "";
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(tablePath));
            String headerLine = reader.readLine();
            String[] headers = headerLine.split("\\$\\$");
            String[] tempHeadersYN = headerLine.split("\\$\\$");
            String row = "";
            if (fetchColumns.contains("ALL")) {
                fetchDataWithALL = fetchDataWithALL + String.join("\t\t", headers);
                while ((row = reader.readLine()) != null) {
                    fetchDataWithALL = fetchDataWithALL + "\n" + String.join("\t\t", row.split("\\$\\$"));
                }
            } else {
                List<String> selectedColumns = new ArrayList<>();
                for (int i = 0; i < headers.length; i++) {
                    if (fetchColumns.contains(headers[i])) {
                        selectedColumns.add(headers[i]);
                        tempHeadersYN[i] = "Y";
                    }
                }
                fetchData = fetchData + "\n" + String.join("\t\t", selectedColumns);
                List<String> selectedData = new ArrayList<>();
                while ((row = reader.readLine()) != null) {
                    selectedData = new ArrayList<>();
                    String[] rowArray = row.split("\\$\\$");
                    for (int i = 0; i < rowArray.length; i++) {
                        if (tempHeadersYN[i].equalsIgnoreCase("Y")) {
                            selectedData.add(rowArray[i]);
                        }
                    }
                    fetchData = fetchData + "\n" + String.join("\t\t", selectedData);
                }

            }
            System.out.println("Selected Data is: ");
            if (!fetchDataWithALL.isEmpty()) {
                System.out.println(fetchDataWithALL);
            } else {
                System.out.println(fetchData);
            }
            reader.close();
        } catch (IOException e) {
            System.out.println("There was some error while processing your request. Please try again!");
            return false;
        }
        return true;
    }
}
