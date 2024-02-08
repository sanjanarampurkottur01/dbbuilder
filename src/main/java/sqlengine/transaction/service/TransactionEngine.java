package main.java.sqlengine.transaction.service;

import main.java.sqlengine.basic.*;
import main.java.sqlengine.basic.service.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * TransactionEngine class is used to process, validate, and perform transactions on the database.
 */
public class TransactionEngine {

    private QueryEngine createEngine = null;
    private QueryEngine selectEngine = null;
    private QueryEngine insertEngine = null;
    private QueryEngine updateEngine = null;
    private QueryEngine deleteEngine = null;

    public TransactionEngine() {
        createEngine = new CreateEngine();
        insertEngine = new InsertEngine();
        updateEngine = new UpdateEngine();
        deleteEngine = new DeleteEngine();
        selectEngine = new SelectEngine();
    }

    /**
     * Method to process a transactional query input by the user
     *
     * @param queries transaction consisting of a batch of queries. Usually begins with BEGIN TRANSACTION;
     *                and ends with END TRANSACTION;
     * @param dbName  the string containing the DATABASE SCHEMA NAME
     * @return true if the transaction was successful, false otherwise
     */
    public boolean processTransactions(List<String> queries, String dbName) {
        if (validateTransactionQueries(queries)) {
            return false;
        }
        String originalDB = "database/" + dbName;
        String backupDB = "database/" + dbName + "_backup";
        try {
            copyDirectory(originalDB, backupDB);
        } catch (IOException e) {
            System.out.println("There was some error while processing your request. Please try again!");
            return false;
        }

        System.out.println("The transaction is");
        for (String query : queries) {
            System.out.println(query);
        }

        boolean commitStatus = false;
        for (String query : queries) {
            if (query.toUpperCase().equalsIgnoreCase("COMMIT;")) {
                commitStatus = true;
                break;
            } else if (query.toUpperCase().equalsIgnoreCase("BEGIN TRANSACTION;")) {
                System.out.println("Transaction started!");
            } else if (query.toUpperCase().equalsIgnoreCase("END TRANSACTION;")) {
                System.out.println("Transaction ended!");
                break;
            } else {
                if (!processTransactionQuery(query, dbName)) {
                    System.out.println("Error while processing query!");
                    commitStatus = false;
                }
            }
        }
        if (commitStatus) {
            System.out.println("Transaction committed!");
            deleteDirectory(backupDB);
        } else {
            System.out.println("Transaction rolling back! Not committed!");
            deleteDirectory(originalDB);
            File file = new File(backupDB);
            file.renameTo(new File(originalDB));
        }

        return true;
    }

    private void deleteDirectory(String directory) {
        File directoryObj = new File(directory);
        for (File file : directoryObj.listFiles()) {
            System.gc();
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                System.out.println("Error while deleting");
            }
            file.delete();
        }
        directoryObj.delete();
    }

    private boolean processTransactionQuery(String query, String dbName) {
        if (query.toUpperCase().startsWith("SELECT")) {
            return selectEngine.processUserQuery(query, dbName);
        } else if (query.toUpperCase().startsWith("INSERT")) {
            return insertEngine.processUserQuery(query, dbName);
        } else if (query.toUpperCase().startsWith("UPDATE")) {
            return updateEngine.processUserQuery(query, dbName);
        } else if (query.toUpperCase().startsWith("DELETE")) {
            return deleteEngine.processUserQuery(query, dbName);
        } else if (query.toUpperCase().startsWith("CREATE")) {
            return createEngine.processUserQuery(query, dbName);
        } else {
            System.out.println("Incorrect query");
            return false;
        }
    }

    private boolean validateTransactionQueries(List<String> queries) {
        String startQuery = queries.get(0);
        String endQuery = queries.get(queries.size() - 1);
        Pattern pattern = Pattern.compile("BEGIN TRANSACTION;", Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(startQuery);
        if (!matcher.find()) {
            System.out.println("Transactions should begin with BEGIN TRANSACTION!");
            return true;
        }
        pattern = Pattern.compile("END TRANSACTION;", Pattern.CASE_INSENSITIVE);
        matcher = pattern.matcher(endQuery);
        if (!matcher.find()) {
            System.out.println("Transactions should begin with BEGIN TRANSACTION!");
            return true;
        }
        return false;
    }

    private void copyDirectory(String originalDB, String backupDB) throws IOException {
        Path originalDirectory = Paths.get(originalDB);
        File theDir = new File(backupDB);
        if (!theDir.exists()) {
            theDir.mkdirs();
        }
        Path backupDirectory = Paths.get(backupDB);
        Files.walk(originalDirectory)
                .forEach(source -> copy(source, backupDirectory.resolve(originalDirectory.relativize(source))));
    }

    private void copy(Path source, Path dest) {
        try {
            Files.copy(source, dest, StandardCopyOption.REPLACE_EXISTING);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }
}

