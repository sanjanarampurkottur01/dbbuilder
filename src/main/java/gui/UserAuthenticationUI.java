package main.java.gui;

import main.java.user.model.AppUser;
import main.java.user.filehandling.FileOperations;
import main.java.user.service.UserRegistration;

import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * The signup/login GUI of the application which provides user to login and signup before moving to make query requests.
 */
public class UserAuthenticationUI {
    private static Scanner scanner;

    /**
     * Method used to provide user with the choice to login or sign up.
     *
     * @param args
     */
    public static void main(String[] args) {
        scanner = new Scanner(System.in);
        System.out.println("User Authentication");
        System.out.println("Please choose a relevant option:\r\n1. Signup\r\n2. Login\r\nPlease enter your choice: ");

        String inputString = scanner.nextLine();
        if (inputString.equals("1")) {
            UserRegistration.registerUser();
        } else if (inputString.equals("2")) {
            login();
        }
    }

    /**
     * Method to check if the user can log into the application using the supplied credentials.
     */
    public static void login() {
        System.out.print("Enter an email address: ");
        String email = scanner.nextLine();

        if (emailCheck(email)) {
            System.out.println("Please enter password");
            String password = scanner.nextLine();
            List<AppUser> users = FileOperations.readUsersFromFile("output.csv");
            for (AppUser user : users) {
                if (user.getHashedPassword().equals(UserRegistration.hashThePassword(password))) {
                    System.out.println("Login successful!");
                    QuerySelectionUI.create(user.getUserName());
                }
            }
        } else {
            System.out.println("Invalid email address.");
        }

        scanner.close();
    }

    /**
     * Checks if the email follows standard format
     *
     * @param email - the email used for signing up
     * @return true if email is valid, false otherwise
     */
    public static boolean emailCheck(String email) {
        String email_pattern = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[com|org|ca|in]{2,}$";
        Pattern pattern = Pattern.compile(email_pattern);
        Matcher m = pattern.matcher(email);
        return m.matches();
    }
}
