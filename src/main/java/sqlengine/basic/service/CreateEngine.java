package main.java.sqlengine.basic.service;

import main.java.sqlengine.basic.QueryEngine;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * CreateEngine class is used to process, validate, and create tables in the database.
 */
public class CreateEngine implements QueryEngine {

    public CreateEngine() {

    }

    /**
     * Method to process the Create Query Request
     *
     * @param createQuery string containing the CREATE TABLE QUERY
     * @param dbName      the string containing the DATABASE SCHEMA NAME
     * @return true if create is successful, false otherwise
     */
    @Override
    public boolean processUserQuery(String createQuery, String dbName) {
        if (createQuery != null && dbName != null) {
            String tableName = validateTable(createQuery);
            if (tableName == null) {
                return false;
            }
            ArrayList[] columnAndDatatypeList = validateColumns(createQuery);
            if (columnAndDatatypeList == null) {
                return false;
            }
            if (createDB(dbName)) {
                String directoryPath = "database/" + dbName;
                if (createTable(tableName, directoryPath)) {
                    return addColumns(tableName, directoryPath, columnAndDatatypeList);
                } else {
                    return false;
                }
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    private String validateTable(String sqlQuery) {
        Pattern pattern = Pattern.compile("CREATE TABLE (\\w+)[\\s\\n\\r]*[(](.*)[)];", Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(sqlQuery);
        if (!matcher.find()) {
            System.out.println("Invalid SQL CREATE QUERY! Please try again!!");
            return null;
        }
        String tableName = matcher.group(1).strip();
        return tableName;
    }

    private ArrayList[] validateColumns(String sqlQuery) {
        ArrayList<String> finalColumnList = new ArrayList<>();
        ArrayList<String> columnDatatypeList = new ArrayList<>();
        sqlQuery = sqlQuery.toLowerCase();
        Pattern pattern = Pattern.compile("CREATE TABLE (\\w+)[\\s\\n\\r]*[(](.*)[)];", Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(sqlQuery);
        if (!matcher.find()) {
            System.out.println("Invalid SQL CREATE QUERY! Please try again!!");
            return null;
        }
        String columnList = matcher.group(2).strip();
        String[] allowedDatatypes = {"int", "boolean", "varchar", "float"};

        for (String columnInfo : columnList.split(",")) {
            String[] columnInfoList = columnInfo.strip().split("\\s+");
            if (columnInfoList.length < 2) {
                System.out.println("Incorrect Query");
                return null;
            }
            String columnName = columnInfoList[0];
            String columnDatatype = "";
            for (String datatype : allowedDatatypes) {
                if (!columnInfoList[1].contains(datatype)) {
                    continue;
                } else {
                    columnDatatype = datatype;
                    columnDatatypeList.add(columnInfoList[1]);
                }
            }
            if (columnDatatype.isEmpty()) {
                System.out.println("INVALID DATATYPE for the columns");
                return null;
            } else {
                finalColumnList.add(columnName);
            }
        }
        return new ArrayList[]{finalColumnList, columnDatatypeList};
    }

    private boolean createDB(String userName) {
        String dbDirectory = "database/" + userName;
        File theDir = new File(dbDirectory);
        if (!theDir.exists()) {
            theDir.mkdirs();
            System.out.println("DB " + userName + " creation successful!");
        }
        return true;
    }

    private boolean createTable(String tableName, String directoryPath) {
        try {
            File tableObj = new File(directoryPath + "/" + tableName + ".txt");
            if (tableObj.createNewFile()) {
                System.out.println("Table " + tableName + " created!");
            } else {
                System.out.println("Table " + tableName + " already exists");
                return false;
            }
        } catch (IOException e) {
            System.out.println("An error occurred.");
            return false;
        }
        return true;
    }

    private boolean addColumns(String tableName, String directoryPath, ArrayList[] columnAndDatatypes) {
        ArrayList<String> columns = columnAndDatatypes[0];
        ArrayList<String> datatypes = columnAndDatatypes[1];
        try {
            FileWriter tableWriter = new FileWriter(directoryPath + "/" + tableName + ".txt", true);
            String columnText = String.join("$$", columns);
            tableWriter.write(columnText);
            tableWriter.close();
            System.out.println("Columns created in the table " + tableName + " successfully!");
        } catch (IOException e) {
            System.out.println("An error occurred while adding columns to the table " + tableName);
            return false;
        }
        try {
            FileWriter tableMetadataWriter = new FileWriter(directoryPath + "/" + tableName + "_metadata.txt", true);
            String columnText = String.join("$$", columns);
            tableMetadataWriter.write(columnText);
            String datatypeText = String.join("$$", datatypes);
            tableMetadataWriter.write("\n" + datatypeText);
            tableMetadataWriter.close();
            System.out.println("Columns created in the table " + tableName + " successfully!");
        } catch (IOException e) {
            System.out.println("An error occurred while adding columns to the table " + tableName);
            return false;
        }
        return true;
    }

}
