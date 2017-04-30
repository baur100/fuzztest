package com.test;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.FindBy;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.concurrent.TimeUnit;


/**
 * Created by Baurz on 4/21/2017.
 */
public class NewUser {
    WebDriver driver = new FirefoxDriver();
    Properties prop = new Properties();

    public NewUser(WebDriver driver) throws IOException {
        this.driver = driver;
        FileInputStream fis = new FileInputStream("data//env.properties");
        prop.load(fis);
    }

    public void goTo() {
        driver.get(prop.getProperty("WEB_ADDRESS"));
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        driver.findElement(By.xpath("//*[@qatag='create_user']")).click();
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
    }

    @FindBy(id = "username")
    WebElement username;

    @FindBy(id = "email")
    WebElement email;

    @FindBy(id = "password")
    WebElement password;

    @FindBy(id = "create_button")
    WebElement create;

    public void createNewUser(String uid, String eml, String pass) {
        username.sendKeys(uid);
        password.sendKeys(pass);
        create.click();
    }

    public Boolean emailCheck(String email) {// Check email by including some external library - true if format allowed
        if (email.length() == 20) //Just kind of mock, function to check email format
            return false;
        return true;
    }

    public Boolean userExistingCheck(String userName) {//Check username in database. Returns true if username unique
        if (userName.length() == 20) //Just kind of mock, here is REST or SQL request to backend
            return false;
        return true;
    }

    public Boolean swearCheck(String userName) { //Check username in DB for is it BAD or not) return true if bad
        if (userName.length() == 20) //Just kind of mock, function to check on swear
            return false;
        return true;
    }
}
