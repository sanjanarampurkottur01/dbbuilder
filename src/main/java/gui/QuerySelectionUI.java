package main.java.gui;

import main.java.sqlengine.basic.*;
import main.java.sqlengine.basic.service.*;
import main.java.sqlengine.transaction.service.TransactionEngine;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * The Query GUI used to accept user queries and send it for processing based on the user choice of query
 */
public class QuerySelectionUI {

    /**
     * A static method used to accept user input queries and make a decision on calling the corresponding Engine to
     * process the queries.
     *
     * @param username - user logged in.
     */
    public static void create(String username) {
        Scanner scanner = new Scanner(System.in);
        boolean flag = true;
        QueryEngine queryEngine;

        while (flag) {
            System.out.println("Hey " + username + " Choose from the following options: \n 1 for Create\n 2 for Insert\n " +
                    "3 for Update\n 4 for Delete\n 5 for Select \n 6 for transactions \n 7 to Quit ");
            String choiceInput = scanner.nextLine();
            String userInput = "";
            switch (choiceInput) {
                case "1": {
                    System.out.print("Enter CREATE SQL query: ");
                    userInput = scanner.nextLine();
                    queryEngine = new CreateEngine();
                    boolean result = queryEngine.processUserQuery(userInput, username);
                    if (result) {
                        System.out.println("Table successfully created!");
                    } else {
                        System.out.println("There was an error while processing the Create query! Please try again!");
                    }
                    break;
                }
                case "2": {
                    System.out.print("Enter INSERT SQL query: ");
                    userInput = scanner.nextLine();
                    queryEngine = new InsertEngine();
                    boolean result = queryEngine.processUserQuery(userInput, username);
                    if (result) {
                        System.out.println("Data successfully created!");
                    } else {
                        System.out.println("There was an error while processing the Insert query! Please try again!");
                    }
                    break;
                }
                case "3": {
                    System.out.print("Enter UPDATE SQL query: ");
                    userInput = scanner.nextLine();
                    queryEngine = new UpdateEngine();
                    boolean result = queryEngine.processUserQuery(userInput, username);
                    if (result) {
                        System.out.println("Data successfully updated!");
                    } else {
                        System.out.println("There was an error while processing the Update query! Please try again!");
                    }
                    break;
                }
                case "4": {
                    System.out.print("Enter DELETE SQL query: ");
                    userInput = scanner.nextLine();
                    queryEngine = new DeleteEngine();
                    boolean result = queryEngine.processUserQuery(userInput, username);
                    if (result) {
                        System.out.println("Data successfully deleted!");
                    } else {
                        System.out.println("There was an error while processing the Deleted query! Please try again!");
                    }
                    break;
                }
                case "5": {
                    System.out.print("Enter SELECT SQL query: ");
                    userInput = scanner.nextLine();
                    queryEngine = new SelectEngine();
                    boolean result = queryEngine.processUserQuery(userInput, username);
                    if (result) {
                        System.out.println("Data successfully fetched!");
                    } else {
                        System.out.println("There was an error while processing the Select query! Please try again!");
                    }
                    break;
                }
                case "6": {
                    List<String> queries = new ArrayList<>();
                    while (true) {
                        System.out.println("Choose 1 to enter the query and 2 to stop creating the transaction");
                        String transactionUserInput = scanner.nextLine();
                        if (transactionUserInput.equalsIgnoreCase("1")) {
                            System.out.println("Enter the transaction query");
                            queries.add(scanner.nextLine());
                        } else if (transactionUserInput.equalsIgnoreCase("2")) {
                            break;
                        } else {
                            System.out.println("Incorrect choice\n");
                        }
                    }
                    if (queries.isEmpty()) {
                        flag = false;
                    } else {
                        new TransactionEngine().processTransactions(queries, username);
                    }
                    break;
                }
                case "7": {
                    flag = false;
                }
                default:
                    continue;
            }
        }
    }
}