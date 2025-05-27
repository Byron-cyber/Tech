package loginsystem;

import javax.swing.*;
import java.security.MessageDigest;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ChatApp {

    // User Information
    private final ArrayList<User> users = new ArrayList<>();
    private String firstname, lastname, username, password, phone;
    
    // Message Information
    private boolean loggedIn = false;
    private final List<Message> sentMessages = new ArrayList<>();
    private int messageCounter = 0;

    // User class to store user details
    public static class User {
        String username;
        String password;
        String phone;

        public User(String username, String password, String phone) {
            this.username = username;
            this.password = password;
            this.phone = phone;
        }

        public String getUsername() { return username; }
        public String getPassword() { return password; }
        public String getPhone() { return phone; }
    }

    // Message class to store message details
    public static class Message {
        String messageID;
        int messageNumber;
        String recipient;
        String message;
        String messageHash;

        public Message(String messageID, int messageNumber, String recipient, String message, String messageHash) {
            this.messageID = messageID;
            this.messageNumber = messageNumber;
            this.recipient = recipient;
            this.message = message;
            this.messageHash = messageHash;
        }

        @Override
        public String toString() {
            return String.format("ID: %s | Num: %d | Recipient: %s | Message: %s | Hash: %s",
                    messageID, messageNumber, recipient, message, messageHash);
        }
    }

    @SuppressWarnings("empty-statement")
    public static void main(String[] args) {
        // Set look and feel for better appearance
        ; // Use default look and feel if system look and feel fails

        ChatApp app = new ChatApp();
        app.runApplication();
    }

    private void runApplication() {
        // Show welcome message
        JOptionPane.showMessageDialog(null, """
                                            Welcome to the ChatApp!
                                            
                                            This application will guide you through:
                                            1. User Registration
                                            2. User Login
                                            3. Chat Interface
                                            
                                            Please follow the prompts to continue.""",
                "Welcome", JOptionPane.INFORMATION_MESSAGE);

        // Registration process
        performRegistration();

        // Login process if registration was successful
        if (isUserRegistered()) {
            performLogin();
        } else {
            JOptionPane.showMessageDialog(null,
                    "Registration was not completed successfully.\nApplication will now exit.",
                    "Exit", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Proceed to chat interface if logged in
        if (loggedIn) {
            JOptionPane.showMessageDialog(null, "Welcome to the ChatApp.", "Welcome", JOptionPane.INFORMATION_MESSAGE);
            int maxMessages = promptForMaxMessages();
            runChatInterface(maxMessages);
        }
    }

    private void performRegistration() {
        String registrationMessage = "=== USER REGISTRATION ===\n\n" +
                "Please provide the following information:\n" +
                "• First and Last Name\n" +
                "• Username (must contain '_' and be ≤ 5 characters)\n" +
                "• Password (≥ 8 chars, uppercase, lowercase, digit, special char)\n" +
                "• South African phone number (+27xxxxxxxxx)";
        JOptionPane.showMessageDialog(null, registrationMessage, "Registration", JOptionPane.INFORMATION_MESSAGE);

        // Get user inputs
        firstname = getValidInput("Enter First Name:", "First Name", false);
        if (firstname == null) return;

        lastname = getValidInput("Enter Last Name:", "Last Name", false);
        if (lastname == null) return;

        username = getValidInput("Enter username (must contain '_' and be max 5 characters):", "Username", false);
        if (username == null) return;

        password = getValidInput("Enter Password (min 8 chars, must include: uppercase, lowercase, digit, special character):", "Password", false);
        if (password == null) return;

        phone = getValidInput("Enter Phone Number (format: +27xxxxxxxxx):", "Phone Number", false);
        if (phone == null) return;

        // Validate inputs
        boolean validatePhone = checkCellPhoneNumber(phone);
        boolean validateUsername = checkUserName(username);
        boolean validatePassword = checkPasswordComplexity(password);

        // Show validation results
        StringBuilder validationResults = new StringBuilder("Validation Results:\n\n");
        if (validateUsername) {
            validationResults.append("✓ Username: Valid\n");
        } else {
            validationResults.append("✗ Username: Invalid (must contain '_' and be ≤ 5 characters)\n");
        }
        if (validatePassword) {
            validationResults.append("✓ Password: Valid\n");
        } else {
            validationResults.append("✗ Password: Invalid (must be ≥ 8 chars with uppercase, lowercase, digit, and special character)\n");
        }
        if (validatePhone) {
            validationResults.append("✓ Phone Number: Valid\n");
        } else {
            validationResults.append("✗ Phone Number: Invalid (must be +27 followed by 9 digits, starting with 6, 7, or 8)\n");
        }

        JOptionPane.showMessageDialog(null, validationResults.toString(), "Validation Results",
                (validateUsername && validatePassword && validatePhone) ? JOptionPane.INFORMATION_MESSAGE : JOptionPane.WARNING_MESSAGE);

        // Register user if all validations pass
        if (validateUsername && validatePassword && validatePhone) {
            String registrationResult = registerUser(username, password, phone);
            if (registrationResult.equals("User is successfully registered.")) {
                JOptionPane.showMessageDialog(null,
                        "🎉 Registration Successful! 🎉\n\n" +
                                "User: " + firstname + " " + lastname + "\n" +
                                "Username: " + username + "\n" +
                                "Phone: " + phone + "\n\n" +
                                "You can now proceed to login.",
                        "Registration Success", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(null,
                        "Registration Failed!\n\n" + registrationResult,
                        "Registration Error", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(null,
                    "Registration Failed!\n\nPlease correct the validation errors and try again.",
                    "Registration Failed", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void performLogin() {
        JOptionPane.showMessageDialog(null,
                "=== USER LOGIN ===\n\n" +
                        "Please enter your credentials to login.",
                "Login", JOptionPane.INFORMATION_MESSAGE);

        int maxAttempts = 3;
        int attempts = 0;

        while (attempts < maxAttempts) {
            String loginUsername = getValidInput("Enter username:", "Login - Username", false);
            if (loginUsername == null) return;

            String loginPassword = getValidInput("Enter password:", "Login - Password", false);
            if (loginPassword == null) return;

            if (loginUser(loginUsername, loginPassword)) {
                loggedIn = true;
                JOptionPane.showMessageDialog(null,
                        "🎉 Login Successful! 🎉\n\n" +
                                "Welcome back, " + firstname + " " + lastname + "!\n" +
                                "It's great to see you again.\n\n" +
                                "Login Time: " + LocalDateTime.now().format(
                                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
                        "Login Success", JOptionPane.INFORMATION_MESSAGE);
                return;
            } else {
                attempts++;
                int remaining = maxAttempts - attempts;
                if (remaining > 0) {
                    JOptionPane.showMessageDialog(null,
                            "Login Failed!\n\n" +
                                    "Invalid username or password.\n" +
                                    "Attempts remaining: " + remaining,
                            "Login Failed", JOptionPane.ERROR_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(null,
                            "Login Failed!\n\n" +
                                    "Maximum login attempts exceeded.\n" +
                                    "Please restart the application to try again.",
                            "Login Locked", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }

    private void runChatInterface(int maxMessages) {
        boolean running = true;
        while (running) {
            String menu = "Please choose an option:\n" +
                    "1) Send Messages\n" +
                    "2) Show recently sent messages\n" +
                    "3) Quit";
            String choice = JOptionPane.showInputDialog(null, menu, "Main Menu", JOptionPane.QUESTION_MESSAGE);

            if (choice == null) {
                running = false;
                continue;
            }

            switch (choice.trim()) {
                case "1":
                    if (!loggedIn) {
                        JOptionPane.showMessageDialog(null, "You must be logged in to send messages.", "Error", JOptionPane.ERROR_MESSAGE);
                    } else {
                        sendMessages(maxMessages);
                    }
                    break;
                case "2":
                    showRecentMessages();
                    break;
                case "3":
                    JOptionPane.showMessageDialog(null, "Goodbye!", "Exit", JOptionPane.INFORMATION_MESSAGE);
                    running = false;
                    break;
                default:
                    JOptionPane.showMessageDialog(null, "Invalid option. Please enter 1, 2, or 3.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // Validation Methods
    private boolean checkUserName(String username) {
        return username != null && username.contains("_") && username.length() <= 5;
    }

    private boolean checkPasswordComplexity(String password) {
        if (password == null) return false;
        String capital = ".*[A-Z].*";
        String small = ".*[a-z].*";
        String digit = ".*\\d.*";
        String special = ".*[!@#$%^&*(),.?\":{}|<>].*";
        return password.length() >= 8 &&
                password.matches(capital) &&
                password.matches(small) &&
                password.matches(digit) &&
                password.matches(special);
    }

    private boolean checkCellPhoneNumber(String phone) {
        if (phone == null) return false;
        String saCode = "+27";
        if (phone.length() == 12 && phone.startsWith(saCode)) {
            char fourthDigitChar = phone.charAt(3);
            if (!Character.isDigit(fourthDigitChar)) return false;
            int fourthDigit = Character.getNumericValue(fourthDigitChar);
            return fourthDigit >= 6 && fourthDigit <= 8;
        }
        return false;
    }

    private String registerUser(String username, String password, String phone) {
        boolean validatePhone = checkCellPhoneNumber(phone);
        boolean validateUsername = checkUserName(username);
        boolean validatePassword = checkPasswordComplexity(password);

        if (validatePhone && validateUsername && validatePassword) {
            for (User user : users) {
                if (user.username.equals(username)) {
                    return "Username already exists!";
                }
            }
            users.add(new User(username, password, phone));
            return "User is successfully registered.";
        } else {
            return "User registration failed!";
        }
    }

    private boolean loginUser(String username, String password) {
        boolean validateUsername = checkUserName(username);
        boolean validatePassword = checkPasswordComplexity(password);

        if (validateUsername && validatePassword) {
            for (User user : users) {
                if (user.username.equals(username) && user.password.equals(password)) {
                    return true;
                }
            }
        }
        return false;
    }

    private int promptForMaxMessages() {
        int maxMessages = 0;
        while (maxMessages <= 0) {
            String input = JOptionPane.showInputDialog(null, "How many messages would you like to enter?", "Set Message Limit", JOptionPane.QUESTION_MESSAGE);
            if (input == null) {
                System.exit(0);
            }
            try {
                maxMessages = Integer.parseInt(input.trim());
                if (maxMessages <= 0) {
                    JOptionPane.showMessageDialog(null, "Please enter a positive number.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(null, "Invalid number. Try again.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
        return maxMessages;
    }

    private void sendMessages(int maxMessages) {
        int messagesSent = 0;
        while (messagesSent < maxMessages) {
            String recipient = null;
            while (true) {
                recipient = JOptionPane.showInputDialog(null,
                        String.format("Enter recipient number (must start with '+' and max 15 characters)\nMessage %d of %d",
                                messagesSent + 1, maxMessages),
                        "Recipient Input", JOptionPane.QUESTION_MESSAGE);
                if (recipient == null) {
                    return;
                }
                recipient = recipient.trim();
                if (validateRecipient(recipient)) {
                    break;
                } else {
                    JOptionPane.showMessageDialog(null, "Invalid recipient number. Please try again.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }

            String messageText = null;
            while (true) {
                messageText = JOptionPane.showInputDialog(null,
                        "Enter your message (max 250 characters):",
                        "Message Input", JOptionPane.QUESTION_MESSAGE);
                if (messageText == null) {
                    return;
                }
                if (messageText.length() <= 250) {
                    break;
                } else {
                    JOptionPane.showMessageDialog(null, "Please enter a message of 250 characters or less.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }

            String messageID = generateUniqueMessageID();
            messageCounter++;

            String messageHash = generateMessageHash(messageID, messageCounter, recipient, messageText);

            Message msg = new Message(messageID, messageCounter, recipient, messageText, messageHash);
            sentMessages.add(msg);

            JOptionPane.showMessageDialog(null, "Message sent successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);

            messagesSent++;
        }

        JOptionPane.showMessageDialog(null,
                String.format("All %d messages have been sent successfully!", maxMessages),
                "Batch Complete", JOptionPane.INFORMATION_MESSAGE);
    }

    private void showRecentMessages() {
        if (sentMessages.isEmpty()) {
            JOptionPane.showMessageDialog(null, "No messages sent yet.", "Recent Messages", JOptionPane.INFORMATION_MESSAGE);
        } else {
            StringBuilder sb = new StringBuilder();
            sb.append("Recent Messages (").append(sentMessages.size()).append(" total):\n\n");

            for (int i = sentMessages.size() - 1; i >= 0; i--) {
                Message msg = sentMessages.get(i);
                sb.append("Message #").append(msg.messageNumber).append(":\n");
                sb.append("  To: ").append(msg.recipient).append("\n");
                sb.append("  Text: ").append(msg.message).append("\n");
                sb.append("  ID: ").append(msg.messageID).append("\n");
                sb.append("  Hash: ").append(msg.messageHash.substring(0, 16)).append("...\n\n");
            }

            JTextArea textArea = new JTextArea(sb.toString());
            textArea.setEditable(false);
            textArea.setRows(15);
            textArea.setColumns(50);
            JScrollPane scrollPane = new JScrollPane(textArea);

            JOptionPane.showMessageDialog(null, scrollPane, "Recent Messages", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private boolean validateRecipient(String recipient) {
        if (recipient == null) return false;
        if (recipient.startsWith("+") && recipient.length() <= 15 && recipient.length() > 1) {
            String numberPart = recipient.substring(1);
            return numberPart.matches("\\d+");
        }
        return false;
    }

    private String generateUniqueMessageID() {
        Random random = new Random();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 10; i++) {
            sb.append(random.nextInt(10));
        }
        return sb.toString();
    }

    private String generateMessageHash(String messageID, int msgNumber, String recipient, String message) {
        String input = messageID + msgNumber + recipient + message;
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = md.digest(input.getBytes());
            StringBuilder hexString = new StringBuilder();
            for (byte b : hashBytes) {
                hexString.append(String.format("%02x", b));
            }
            return hexString.toString();
        } catch (Exception e) {
            return "hash_error_" + System.currentTimeMillis();
        }
    }

    private String getValidInput(String message, String title, boolean allowEmpty) {
        String input;
        do {
            input = JOptionPane.showInputDialog(null, message, title, JOptionPane.QUESTION_MESSAGE);
            if (input == null) {
                return null;
            }
            input = input.trim();
            if (!allowEmpty && input.isEmpty()) {
                JOptionPane.showMessageDialog(null, "This field cannot be empty. Please try again.", "Invalid Input", JOptionPane.WARNING_MESSAGE);
            }
        } while (!allowEmpty && input.isEmpty());
        return input;
    }

    private boolean isUserRegistered() {
        return !users.isEmpty();
    }
}