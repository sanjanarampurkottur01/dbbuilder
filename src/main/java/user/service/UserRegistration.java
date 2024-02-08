package main.java.user.service;

import main.java.user.filehandling.FileOperations;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;
import java.util.Scanner;

import static main.java.gui.UserAuthenticationUI.emailCheck;
import static main.java.gui.UserAuthenticationUI.login;

/**
 * Class used to sign up the user to the application
 */
public class UserRegistration {

    /**
     * Method used to register the user to the application
     */
    public static void registerUser() {
        Scanner scanner = new Scanner(System.in);

        System.out.print("Enter your username: ");
        String username = scanner.nextLine();

        System.out.print("Enter your email: ");
        String email = scanner.nextLine();
        if (emailCheck(email)) {

            System.out.println("Please enter password");
            String password = scanner.nextLine();
            System.out.print("Confirm your password: ");
            String confirmPassword = scanner.nextLine();
            if (password.equals(confirmPassword)) {
                String generatedCaptcha = generateCaptcha();
                System.out.println("Enter the captcha " + generatedCaptcha + " below.");
                String enteredCaptcha = scanner.nextLine();

                while (!isValidCaptcha(generatedCaptcha, enteredCaptcha)) {
                    System.out.println("Incorrect Captcha! Please try again...");
                    generatedCaptcha = generateCaptcha();
                    System.out.println("Enter the captcha " + generatedCaptcha + " below.");
                    enteredCaptcha = scanner.nextLine();
                }
                System.out.println("SignUp successful");
                FileOperations.addUserToFile(username, email, hashThePassword(password));
                login();
            } else {
                System.out.println("Please try again, password does not match");
            }

        } else {
            System.out.println("Invalid email address. Try signing up again");
            registerUser();
        }

        scanner.close();
    }

    private static boolean isValidInput(String username, String email, String password) {
        return !username.isEmpty() && !email.isEmpty() && !password.isEmpty() && email.contains("@");
    }

    /**
     * A method used to generate a random captcha to the user for verification
     *
     * @return the captcha to be entered by the user
     */
    public static String generateCaptcha() {
        Random random = new Random();
        String alphabets = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        StringBuilder captcha = new StringBuilder();
        for (int i = 0; i < 7; i++) {
            captcha.append(alphabets.charAt(random.nextInt(alphabets.length())));
        }
        return captcha.toString();
    }

    private static boolean isValidCaptcha(String generatedCaptcha, String userCaptcha) {
        return generatedCaptcha.equals(userCaptcha);
    }

    /**
     * Hash the password with the salt using SHA-256
     *
     * @param password - the user password to be hashed and stored in the file
     * @return
     */
    public static String hashThePassword(String password) {
        byte[] hashedPasswordValue = hashValueOfPassword(password);
        return bytesToHex(hashedPasswordValue);
    }

    /**
     * Hash the password with the salt using SHA-256
     */
    public static byte[] hashValueOfPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] passwordBytes = password.getBytes();
            byte[] hashedPassword = md.digest(passwordBytes);
            return hashedPassword;
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Convert a byte array to a hexadecimal string
     *
     * @param bytes
     * @return
     */
    public static String bytesToHex(byte[] bytes) {
        StringBuilder hexString = new StringBuilder();
        for (byte b : bytes) {
            hexString.append(String.format("%02x", b));
        }
        return hexString.toString();
    }
}
