package loginsystem;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.ArrayList;

public class LoginTest {

    private Login login;


    // Tests for checkUserName
    @Test
    void checkUserName_ValidUsername_ReturnsTrue() {
        assertTrue(login.checkUserName("user_"), "Username 'user_' should be valid");
        assertTrue(login.checkUserName("a_b"), "Username 'a_b' should be valid");
    }

    @Test
    void checkUserName_NoUnderscore_ReturnsFalse() {
        assertFalse(login.checkUserName("user"), "Username without underscore should be invalid");
    }

    @Test
    void checkUserName_TooLong_ReturnsFalse() {
        assertFalse(login.checkUserName("toolong_"), "Username longer than 5 characters should be invalid");
    }

    @Test
    void checkUserName_NullInput_ReturnsFalse() {
        assertFalse(login.checkUserName(null), "Null username should be invalid");
    }

    @Test
    void checkUserName_EmptyString_ReturnsFalse() {
        assertFalse(login.checkUserName(""), "Empty username should be invalid");
    }

    // Tests for checkPasswordComplexity
    @Test
    void checkPasswordComplexity_ValidPassword_ReturnsTrue() {
        assertTrue(login.checkPasswordComplexity("Pass123!@"), "Password with all required characters should be valid");
        assertTrue(login.checkPasswordComplexity("Ab1@56789"), "Password with all required characters should be valid");
    }

    @Test
    void checkPasswordComplexity_TooShort_ReturnsFalse() {
        assertFalse(login.checkPasswordComplexity("Pass1!@"), "Password shorter than 8 characters should be invalid");
    }

    @Test
    void checkPasswordComplexity_NoUppercase_ReturnsFalse() {
        assertFalse(login.checkPasswordComplexity("pass123!@"), "Password without uppercase should be invalid");
    }

    @Test
    void checkPasswordComplexity_NoLowercase_ReturnsFalse() {
        assertFalse(login.checkPasswordComplexity("PASS123!@"), "Password without lowercase should be invalid");
    }

    @Test
    void checkPasswordComplexity_NoDigit_ReturnsFalse() {
        assertFalse(login.checkPasswordComplexity("Password!@"), "Password without digit should be invalid");
    }

    @Test
    void checkPasswordComplexity_NoSpecialChar_ReturnsFalse() {
        assertFalse(login.checkPasswordComplexity("Password123"), "Password without special character should be invalid");
    }

    @Test
    void checkPasswordComplexity_NullInput_ReturnsFalse() {
        assertFalse(login.checkPasswordComplexity(null), "Null password should be invalid");
    }

    // Tests for checkCellPhoneNumber
    @Test
    void checkCellPhoneNumber_ValidSANumber_ReturnsTrue() {
        assertTrue(login.checkCellPhoneNumber("+27612345678"), "Valid SA number starting with +276 should be valid");
        assertTrue(login.checkCellPhoneNumber("+27712345678"), "Valid SA number starting with +277 should be valid");
        assertTrue(login.checkCellPhoneNumber("+27812345678"), "Valid SA number starting with +278 should be valid");
    }

    @Test
    void checkCellPhoneNumber_InvalidCountryCode_ReturnsFalse() {
        assertFalse(login.checkCellPhoneNumber("+28123456789"), "Number with invalid country code should be invalid");
    }

    @Test
    void checkCellPhoneNumber_WrongLength_ReturnsFalse() {
        assertFalse(login.checkCellPhoneNumber("+276123456"), "Number with incorrect length should be invalid");
    }

    @Test
    void checkCellPhoneNumber_InvalidFourthDigit_ReturnsFalse() {
        assertFalse(login.checkCellPhoneNumber("+27512345678"), "Number with fourth digit not 6, 7, or 8 should be invalid");
    }

    @Test
    void checkCellPhoneNumber_NonNumeric_ReturnsFalse() {
        assertFalse(login.checkCellPhoneNumber("+276abc45678"), "Number with non-numeric characters should be invalid");
    }

    @Test
    void checkCellPhoneNumber_NullInput_ReturnsFalse() {
        assertFalse(login.checkCellPhoneNumber(null), "Null phone number should be invalid");
    }

    // Tests for registerUser
    @Test
    void registerUser_ValidInputs_SuccessfullyRegisters() {
        String result = login.registerUser("user_", "Pass123!@", "+27612345678");
        assertEquals("User is successfully registered.", result, "Valid inputs should register successfully");
        ArrayList<Login.User> users = login.getUsers();
        assertEquals(1, users.size(), "User list should contain one user");
        assertEquals("user_", users.get(0).getUsername(), "Registered username should match");
    }

    @Test
    void registerUser_DuplicateUsername_Fails() {
        login.registerUser("user_", "Pass123!@", "+27612345678");
        String result = login.registerUser("user_", "Pass123!@", "+27712345678");
        assertEquals("Username already exists!", result, "Duplicate username should fail registration");
        assertEquals(1, login.getUsers().size(), "User list should not grow on failed registration");
    }

    @Test
    void registerUser_InvalidUsername_Fails() {
        String result = login.registerUser("invalid", "Pass123!@", "+27612345678");
        assertEquals("User registration failed!", result, "Invalid username should fail registration");
        assertEquals(0, login.getUsers().size(), "User list should remain empty");
    }

    @Test
    void registerUser_InvalidPassword_Fails() {
        String result = login.registerUser("user_", "pass123", "+27612345678");
        assertEquals("User registration failed!", result, "Invalid password should fail registration");
        assertEquals(0, login.getUsers().size(), "User list should remain empty");
    }

    @Test
    void registerUser_InvalidPhone_Fails() {
        String result = login.registerUser("user_", "Pass123!@", "+27512345678");
        assertEquals("User registration failed!", result, "Invalid phone number should fail registration");
        assertEquals(0, login.getUsers().size(), "User list should remain empty");
    }

    // Tests for loginUser
    @Test
    void loginUser_ValidCredentials_ReturnsTrue() {
        login.registerUser("user_", "Pass123!@", "+27612345678");
        assertTrue(login.loginUser("user_", "Pass123!@"), "Valid credentials should allow login");
    }

    @Test
    void loginUser_InvalidUsername_ReturnsFalse() {
        login.registerUser("user_", "Pass123!@", "+27612345678");
        assertFalse(login.loginUser("wrong_", "Pass123!@"), "Invalid username should prevent login");
    }

    @Test
    void loginUser_InvalidPassword_ReturnsFalse() {
        login.registerUser("user_", "Pass123!@", "+27612345678");
        assertFalse(login.loginUser("user_", "Wrong123!@"), "Invalid password should prevent login");
    }

    @Test
    void loginUser_InvalidFormatCredentials_ReturnsFalse() {
        login.registerUser("user_", "Pass123!@", "+27612345678");
        assertFalse(login.loginUser("toolong_", "pass123"), "Credentials with invalid format should prevent login");
    }

    // Tests for getLoginStatusMessage
    @Test
    void getLoginStatusMessage_ValidCredentials_ReturnsSuccessMessage() {
        login.registerUser("user_", "Pass123!@", "+27612345678");
        String result = login.getLoginStatusMessage("user_", "Pass123!@");
        assertEquals("Login successful!", result, "Valid credentials should return success message");
    }

    @Test
    void getLoginStatusMessage_InvalidCredentials_ReturnsFailureMessage() {
        login.registerUser("user_", "Pass123!@", "+27612345678");
        String result = login.getLoginStatusMessage("user_", "Wrong123!@");
        assertEquals("Login failed! Invalid credentials.", result, "Invalid credentials should return failure message");
    }

    // Tests for getUsers
    @Test
    void getUsers_ReturnsCopyOfUserList() {
        login.registerUser("user_", "Pass123!@", "+27612345678");
        ArrayList<Login.User> users = login.getUsers();
        assertEquals(1, users.size(), "User list should contain one user");
        users.add(new Login.User("test_", "Test123!@", "+27712345678"));
        assertEquals(1, login.getUsers().size(), "Modifying returned list should not affect internal list");
    }
}