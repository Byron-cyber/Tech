package loginsystem;

import javax.swing.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
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
    private final List<Message> disregardedMessages = new ArrayList<>();
    private final List<Message> storedMessages = new ArrayList<>();
    private final List<String> messageHashes = new ArrayList<>();
    private final List<String> messageIDs = new ArrayList<>();
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
        String flag;

        public Message(String messageID, int messageNumber, String recipient, String message, String messageHash, String flag) {
            this.messageID = messageID;
            this.messageNumber = messageNumber;
            this.recipient = recipient;
            this.message = message;
            this.messageHash = messageHash;
            this.flag = flag;
        }

        @Override
        public String toString() {
            return String.format("ID: %s | Num: %d | Recipient: %s | Message: %s | Hash: %s | Flag: %s",
                    messageID, messageNumber, recipient, message, messageHash, flag);
        }
    }

    @SuppressWarnings("empty-statement")
    public static void main(String[] args) {
        ; // Use default look and feel if system look and feel fails
        ChatApp app = new ChatApp();
        app.runApplication();
    }

    private void runApplication() {
        JOptionPane.showMessageDialog(null, """
                                            Welcome to the ChatApp!
                                            
                                            This application will guide you through:
                                            1. User Registration
                                            2. User Login
                                            3. Chat Interface
                                            
                                            Please follow the prompts to continue.""",
                "Welcome", JOptionPane.INFORMATION_MESSAGE);

        performRegistration();

        if (isUserRegistered()) {
            performLogin();
        } else {
            JOptionPane.showMessageDialog(null,
                    "Registration was not completed successfully.\nApplication will now exit.",
                    "Exit", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (loggedIn) {
            JOptionPane.showMessageDialog(null, "Welcome to the ChatApp.", "Welcome", JOptionPane.INFORMATION_MESSAGE);
            int maxMessages = promptForMaxMessages();
            populateTestData(); // Populate test data
            runChatInterface(maxMessages);
        }
    }

    private void performRegistration() {
        String registrationMessage = """
                                     === USER REGISTRATION ===
                                     
                                     Please provide the following information:
                                     \u2022 First and Last Name
                                     \u2022 Username (must contain '_' and be \u2264 5 characters)
                                     \u2022 Password (\u2265 8 chars, uppercase, lowercase, digit, special char)
                                     \u2022 South African phone number (+27xxxxxxxxx)""";
        JOptionPane.showMessageDialog(null, registrationMessage, "Registration", JOptionPane.INFORMATION_MESSAGE);

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

        boolean validatePhone = checkCellPhoneNumber(phone);
        boolean validateUsername = checkUserName(username);
        boolean validatePassword = checkPasswordComplexity(password);

        StringBuilder validationResults = new StringBuilder("Validation Results:\n\n");
        if (validateUsername) validationResults.append("✓ Username: Valid\n");
        else validationResults.append("✗ Username: Invalid (must contain '_' and be ≤ 5 characters)\n");
        if (validatePassword) validationResults.append("✓ Password: Valid\n");
        else validationResults.append("✗ Password: Invalid (must be ≥ 8 chars with uppercase, lowercase, digit, and special character)\n");
        if (validatePhone) validationResults.append("✓ Phone Number: Valid\n");
        else validationResults.append("✗ Phone Number: Invalid (must be +27 followed by 9 digits, starting with 6, 7, or 8)\n");

        JOptionPane.showMessageDialog(null, validationResults.toString(), "Validation Results",
                (validateUsername && validatePassword && validatePhone) ? JOptionPane.INFORMATION_MESSAGE : JOptionPane.WARNING_MESSAGE);

        if (validateUsername && validatePassword && validatePhone) {
            String registrationResult = registerUser(username, password, phone);
            if (registrationResult.equals("User is successfully registered.")) {
                JOptionPane.showMessageDialog(null,
                        """
                        \ud83c\udf89 Registration Successful! \ud83c\udf89
                        
                        User: """ + firstname + " " + lastname + "\n" +
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
        JOptionPane.showMessageDialog(null, """
                                            === USER LOGIN ===
                                            
                                            Please enter your credentials to login.""",
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
                        """
                        \ud83c\udf89 Login Successful! \ud83c\udf89
                        
                        Welcome back, """ + firstname + " " + lastname + "!\n" +
                                "It's great to see you again.\n\n" +
                                "Login Time: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
                        "Login Success", JOptionPane.INFORMATION_MESSAGE);
                return;
            } else {
                attempts++;
                int remaining = maxAttempts - attempts;
                if (remaining > 0) {
                    JOptionPane.showMessageDialog(null,
                            """
                            Login Failed!
                            
                            Invalid username or password.
                            Attempts remaining: """ + remaining,
                            "Login Failed", JOptionPane.ERROR_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(null, """
                                                        Login Failed!
                                                        
                                                        Maximum login attempts exceeded.
                                                        Please restart the application to try again.""",
                            "Login Locked", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }

    private void runChatInterface(int maxMessages) {
        boolean running = true;
        while (running) {
            String menu = """
                          Please choose an option:
                          1) Send Messages
                          2) Show recently sent messages
                          3) Display sender and recipient of all sent messages
                          4) Display the longest sent message
                          5) Search for a message ID
                          6) Search for all messages sent to a recipient
                          7) Delete a message using message hash
                          8) Display a report of all sent messages
                          9) Quit""";
            String choice = JOptionPane.showInputDialog(null, menu, "Main Menu", JOptionPane.QUESTION_MESSAGE);

            if (choice == null) {
                running = false;
                continue;
            }

            switch (choice.trim()) {
                case "1" -> {
                    if (!loggedIn) {
                        JOptionPane.showMessageDialog(null, "You must be logged in to send messages.", "Error", JOptionPane.ERROR_MESSAGE);
                    } else {
                        sendMessages(maxMessages);
                    }
                }
                case "2" -> showRecentMessages();
                case "3" -> displaySenderRecipient();
                case "4" -> displayLongestMessage();
                case "5" -> searchByMessageID();
                case "6" -> searchByRecipient();
                case "7" -> deleteMessageByHash();
                case "8" -> displayReport();
                case "9" -> {
                    JOptionPane.showMessageDialog(null, "Goodbye!", "Exit", JOptionPane.INFORMATION_MESSAGE);
                    running = false;
                }
                default -> JOptionPane.showMessageDialog(null, "Invalid option. Please enter 1-9.", "Error", JOptionPane.ERROR_MESSAGE);
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

            Message msg = new Message(messageID, messageCounter, recipient, messageText, messageHash, "Sent");
            sentMessages.add(msg);
            messageHashes.add(messageHash);
            messageIDs.add(messageID);

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
        } catch (NoSuchAlgorithmException e) {
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

    // Populate test data
    private void populateTestData() {
        String[][] testData = {
            {"+27834567896", "Did you get the cake?", "Sent"},
            {"+2783844567", "Where are you? You are late! I have asked you to be on time.", "Stored"},
            {"+2783444567", "Yahooo, I am at your gate.", "Disregarded"},
            {"Developer", "It is dinner time!", "Sent"},
            {"+2783844567", "Ok, I am leaving without you.", "Stored"}
        };

        for (int i = 0; i < testData.length; i++) {
            String recipient = testData[i][0];
            String message = testData[i][1];
            String flag = testData[i][2];
            String messageID = generateUniqueMessageID();
            messageCounter++;
            String messageHash = generateMessageHash(messageID, messageCounter, recipient, message);

            Message msg = new Message(messageID, messageCounter, recipient, message, messageHash, flag);
            if ("Sent".equals(flag)) sentMessages.add(msg);
            else if ("Disregarded".equals(flag)) disregardedMessages.add(msg);
            else if ("Stored".equals(flag)) storedMessages.add(msg);
            messageHashes.add(messageHash);
            messageIDs.add(messageID);
        }
    }

    // Implement required functions
    private void displaySenderRecipient() {
        if (sentMessages.isEmpty()) {
            JOptionPane.showMessageDialog(null, "No sent messages to display.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        StringBuilder sb = new StringBuilder("Sender and Recipient of Sent Messages:\n\n");
        for (Message msg : sentMessages) {
            sb.append("Sender: ").append(username).append(" | Recipient: ").append(msg.recipient).append("\n");
        }
        JOptionPane.showMessageDialog(null, sb.toString(), "Sender/Recipient", JOptionPane.INFORMATION_MESSAGE);
    }

    private void displayLongestMessage() {
        if (sentMessages.isEmpty()) {
            JOptionPane.showMessageDialog(null, "No sent messages to display.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        Message longest = sentMessages.get(0);
        for (Message msg : sentMessages) {
            if (msg.message.length() > longest.message.length()) {
                longest = msg;
            }
        }
        JOptionPane.showMessageDialog(null, "Longest Message:\n\nRecipient: " + longest.recipient + "\nMessage: " + longest.message,
                "Longest Message", JOptionPane.INFORMATION_MESSAGE);
    }

    private void searchByMessageID() {
        String id = getValidInput("Enter Message ID to search:", "Search by ID", false);
        if (id == null) return;
        for (Message msg : sentMessages) {
            if (msg.messageID.equals(id)) {
                JOptionPane.showMessageDialog(null, "Found:\nRecipient: " + msg.recipient + "\nMessage: " + msg.message,
                        "Search Result", JOptionPane.INFORMATION_MESSAGE);
                return;
            }
        }
        JOptionPane.showMessageDialog(null, "Message ID not found.", "Search Result", JOptionPane.WARNING_MESSAGE);
    }

    private void searchByRecipient() {
        String recipient = getValidInput("Enter Recipient to search:", "Search by Recipient", false);
        if (recipient == null) return;
        StringBuilder sb = new StringBuilder("Messages for Recipient " + recipient + ":\n\n");
        boolean found = false;
        for (Message msg : sentMessages) {
            if (msg.recipient.equals(recipient)) {
                sb.append("Message: ").append(msg.message).append("\n");
                found = true;
            }
        }
        if (!found) sb.append("No messages found for this recipient.");
        JOptionPane.showMessageDialog(null, sb.toString(), "Search Result", JOptionPane.INFORMATION_MESSAGE);
    }

    private void deleteMessageByHash() {
        String hash = getValidInput("Enter Message Hash to delete:", "Delete by Hash", false);
        if (hash == null) return;
        for (int i = 0; i < sentMessages.size(); i++) {
            if (sentMessages.get(i).messageHash.equals(hash)) {
                sentMessages.remove(i);
                messageHashes.remove(hash);
                JOptionPane.showMessageDialog(null, "Message with hash " + hash + " deleted successfully.", "Delete Result", JOptionPane.INFORMATION_MESSAGE);
                return;
            }
        }
        JOptionPane.showMessageDialog(null, "Message hash not found.", "Delete Result", JOptionPane.WARNING_MESSAGE);
    }

    private void displayReport() {
        if (sentMessages.isEmpty()) {
            JOptionPane.showMessageDialog(null, "No sent messages to report.", "Report", JOptionPane.WARNING_MESSAGE);
            return;
        }
        StringBuilder sb = new StringBuilder("Full Report of Sent Messages:\n\n");
        for (Message msg : sentMessages) {
            sb.append("Message Hash: ").append(msg.messageHash).append("\n");
            sb.append("Recipient: ").append(msg.recipient).append("\n");
            sb.append("Message: ").append(msg.message).append("\n\n");
        }
        JOptionPane.showMessageDialog(null, sb.toString(), "Report", JOptionPane.INFORMATION_MESSAGE);
    }
}