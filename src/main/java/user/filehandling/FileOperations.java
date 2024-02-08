package main.java.user.filehandling;

import main.java.user.model.AppUser;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * A class designed to just handle file operations related to user authentication and verification
 */
public class FileOperations {

    /**
     * A static method used to add user details to a file
     *
     * @param username          - user name which would be used to specify the user's schema
     * @param email             - user email used for authentication purpose
     * @param encryptedPassword - encrypted password for the user authentication
     */
    public static void addUserToFile(String username, String email, String encryptedPassword) {
        String csvFilePath = "output.csv";
        try {
            // Create a FileWriter to write to the CSV file
            FileWriter fileWriter = new FileWriter(csvFilePath);

            // Define the data you want to write to the CSV file
            String[] title = {"Username", "Email", "Hashed Password Value"};
            String[] values = {username, email, encryptedPassword};

            // Write the headers to the CSV file
            for (String heading : title) {
                fileWriter.append(heading);
                fileWriter.append(",");
            }
            fileWriter.append("\n");

            // Write the data to the CSV file
            for (String value : values) {
                fileWriter.append(value);
                fileWriter.append(",");
            }
            fileWriter.append("\n");
            // Close the FileWriter
            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void readUserFromFile(String username, String email, String md_output) {

        String fileName = "output.csv"; // Replace with the actual file path

        List<AppUser> appUsers = readUsersFromFile(md_output);

        for (AppUser appUser : appUsers) {
            System.out.println("User Name: " + appUser.getUserName());
            System.out.println("Email: " + appUser.getEmail());
            System.out.println("Hashed Password Value: " + appUser.getHashedPassword());
            System.out.println("----------");
        }
    }

    public static List<AppUser> readUsersFromFile(String fileName) {
        List<AppUser> appUsers = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] userData = line.split(",");
                if (userData.length == 3) {
                    String name = userData[0].trim();
                    String email = userData[1].trim();
                    String hashedPassword = userData[2].trim();

                    AppUser appUser = new AppUser(name, email, hashedPassword);
                    appUsers.add(appUser);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return appUsers;
    }
}