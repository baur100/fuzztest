package com.test;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.PageFactory;
import org.testng.Assert;
import org.testng.annotations.*;
import org.testng.asserts.SoftAssert;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;


/**
 * Created by Baurz on 4/23/2017.
 */
public class FuzzTest {

    static WebDriver driver;
    //Properties file contains all test data
    private static Properties prop = new Properties();
    ;

    @BeforeClass
    public void init() throws IOException {
        FileInputStream fis = new FileInputStream("data//env.properties");
        driver = new FirefoxDriver();

        prop.load(fis);
    }

    @AfterMethod
    public void closeTest() {
        driver.close();
    }

    @AfterTest
    public void closeAll() {
        driver.quit();
    }

    /*User story - Login Account
     As a user, I would like to login into my account, so that I may browse my chats
        If i enter my credentials incorrectly, I am told no account found.
        Once I enter my credentials, I am taken to the Home Page.
      */
    @DataProvider()
    public Object[][] testData1and2() {
        return new Object[][]{{"username1", "password1"}, {"username2", "password2"}, {"username3", "password3"}};
    }

    @Test(dataProvider = "testData1and2")
    public void test1and2(String username, String password) {
        Boolean accountAssert = false;
        String message = null;
        LoginPagePom login = PageFactory.initElements(driver, LoginPagePom.class);
        login.goTo();
        if (!login.accountCredentials(username, password) && (driver.findElement(By.id("validation_field")).getText() == "Home Page")) {
            accountAssert = true;
            message = "Successful login with wrong credentials";
        } else if (login.accountCredentials(username, password) && (driver.findElement(By.id("validation_field")).getText() == "Account not found")) {
            accountAssert = true;
            message = "Fail to login with good credentials";
        }
        Assert.assertFalse(accountAssert, message);
    }

    /*User story - New User creation
    As a new user, I would like to create an account so that I can chat with my friends
        Validate that my chosen username hasn’t been used before
        Use a valid email
        Confirm user password
            Ensure password is at least 16 characters
    As a Chatter Admin, I would like to ensure that no foul language is used as a user name.
        All curse words are forbidden
     */
    @DataProvider()
    public Object[][] testData3() {//We can make a various combination of login data
        return new Object[][]{{"UniqueUserA", "AllowedEmail@gmail.com", "password_length17"},//pass length=15 chars
                {"NonUniqueUserB", "NonUnigueA@wongdomailn", "passwordlength16"},//pass length=16 chars
                {"BadWordName", "Wrongemail", "passwrdlength15"}//pass length=17 chars
        };
    }

    @Test(dataProvider = "testData3")
    public void test3(String newUsername, String newEmail, String newPassword) {
        Boolean passwordAssert = false;
        Boolean emailAssert = false;
        Boolean userUniqueAssert = false;
        Boolean userSwearAssert = false;
        Boolean userCreated = false;
        SoftAssert sa = new SoftAssert();
        NewUser createAccount = PageFactory.initElements(driver, NewUser.class);
        createAccount.goTo();
        createAccount.createNewUser(prop.getProperty(newUsername), prop.getProperty(newEmail), prop.getProperty(newPassword));
        if (createAccount.userExistingCheck(newUsername) && (driver.findElement(By.id("uid_unique")).getText() == "Username is unique")) {
            userUniqueAssert = true;
        } else if (!createAccount.userExistingCheck(newUsername) && (driver.findElement(By.id("uid_unique")).getText() == "Username is exist")) {
            userUniqueAssert = true;
        }
        sa.assertTrue(userUniqueAssert, "Username uniqness assert failed");
        if (createAccount.emailCheck(newEmail) && (driver.findElement(By.id("email_is_correct")).getText() == "Email is correct")) {
            emailAssert = true;
        } else if ((!createAccount.emailCheck(newEmail)) && (driver.findElement(By.id("email_is_correct")).getText() == "Email wrong format")) {
            emailAssert = true;
        }
        sa.assertTrue(emailAssert, "Email assert failed");
        if ((newPassword.length() < 16) && (driver.findElement(By.id("password_length")).getText() == "Too short")) {
            passwordAssert = true;
        } else if ((newPassword.length() >= 16) && (driver.findElement(By.id("password_length")).getText() == "password is ok")) {
            passwordAssert = true;
        }
        sa.assertTrue(passwordAssert, "Password Length Check Error");
        if (createAccount.swearCheck(newUsername) && (driver.findElement(By.id("uid_swear")).getText() == "Username not allowed")) {
            userSwearAssert = true;
        } else if (!createAccount.swearCheck(newUsername) && (driver.findElement(By.id("uid_swear")).getText() == "Username is allowed")) {
            userSwearAssert = true;
        }
        sa.assertTrue(userSwearAssert, "User name check for bad word using is failed");
        if (userUniqueAssert && !userSwearAssert && passwordAssert && emailAssert && (driver.findElement(By.id("Conformation")).getText() == "User Created Successfully")) {
            userCreated = true;
        }

        sa.assertTrue(userCreated, "User creation assert failed");
        sa.assertAll();
        logOut(driver);
    }

    /*User story - Password Recovery
    As a user who has forgotten their password, I would like to reset my password, so that I may log in again.
        Enter my email which is associated with my account
        Receive an email with a password reset link to my email
        Enter a new password, and be logged in
     */
    @DataProvider()
    public Object[][] testData4() {
        return new Object[][]{{"username", "newPasswordEnoughLong", "email@adomain.com", "emailPassword"},
                {"username", "newPassShort", "email@adomain.com", "emailPassword"}
        };
    }

    @Test(dependsOnMethods = {"test1and2"}, dataProvider = "testData4")
    public void test4(String username, String password, String email, String epassword) {
        PasswordRecovery recovery = PageFactory.initElements(driver, PasswordRecovery.class);
        recovery.goTo();
        Assert.assertTrue(recovery.emailSend(email), "Recovery email has not been send");
        Assert.assertTrue(recovery.emailCheck(email, epassword), "Recovery email has not been received");
        if (password.length() < 16) {
            Assert.assertFalse(recovery.passwordChange(password), "System accepted short password");
        } else Assert.assertTrue(recovery.passwordChange(password), "Password has not been changed");

        LoginPagePom login = PageFactory.initElements(driver, LoginPagePom.class);
        login.goTo();
        login.login(username, password);
        Assert.assertEquals(driver.findElement(By.id("validation_field")).getText(), "Home Page");
        logOut(driver);
    }
    /*User story - Password Change
    As a user who would like to change my password.
        Enter my current password to ensure its me
        Enter and confirm my new password
        Email a confirmation that my password was changed
    */

    @DataProvider()
    public Object[][] testData5() {
        return new Object[][]{{"username", "oldPasswordEnoughLong", "newPasswordEnoughLong"},
                {"username", "oldPasswordEnoughLong", "newPassShort"}
        };
    }

    @Test(dependsOnMethods = {"test1and2"}, dataProvider = "testData5")
    public void test5(String username, String oldPassword, String newPassword) {
        LoginPagePom login = PageFactory.initElements(driver, LoginPagePom.class);
        login.goTo();
        login.login(username, oldPassword);

        PasswordChange pch = PageFactory.initElements(driver, PasswordChange.class);
        pch.goTo();
        Assert.assertTrue(pch.enterOldPass(oldPassword), "Old password verification fail");
        if (newPassword.length() < 16) {
            Assert.assertFalse(pch.enterNewPassword(newPassword), "System accepted short password");
        } else Assert.assertTrue(pch.enterNewPassword(newPassword), "Password has not been changed");
        Assert.assertTrue(pch.emailSend(), "Recovery email has not been send");

        logOut(driver);
    }

    public void logOut(WebDriver driver) {
        driver.findElement(By.id("log_out")).click();
    }
}
