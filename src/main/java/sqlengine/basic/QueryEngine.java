package main.java.sqlengine.basic;

public interface QueryEngine {

    /**
     * Method to process the user query
     *
     * @param userQuery the query sent by the user
     * @param dbName    the database schema assigned to the logged in user
     * @return true if the query was processed successfully, false otherwise.
     */
    boolean processUserQuery(String userQuery, String dbName);
}
