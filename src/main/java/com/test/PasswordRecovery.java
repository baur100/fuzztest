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
public class PasswordRecovery {
    WebDriver driver = new FirefoxDriver();
    Properties prop = new Properties();

    public PasswordRecovery(WebDriver driver) throws IOException {
        this.driver = driver;
        FileInputStream fis = new FileInputStream("data//env.properties");
        prop.load(fis);
    }

    public void goTo() {
        driver.get(prop.getProperty("WEB_ADDRESS"));
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        driver.findElement(By.xpath("//*[@text()='password_recovery']")).click();
    }

    @FindBy(id = "email")
    WebElement email;
    @FindBy(id = "submit")
    WebElement button;

    public Boolean emailSend(String email) {
        this.email.sendKeys(email);
        button.click();
        if (driver.findElement(By.id("email_send")).getText() == "Password recovery email send successfully")
            return true;
        return false;
    }

    public Boolean emailCheck(String email, String password) {
        /*
        Some procedure to check email for confirmation recovery email receiving
         sample code at MailHelper.java
         */
        if (email.length() == 20) {//Kind of mock
            driver.findElement(By.linkText("Click here to recover email")).click();
            return true;
        }
        return false;
    }

    public Boolean passwordChange(String password) {
        driver.findElement(By.id("new_password")).sendKeys(password);
        driver.findElement(By.id("confirm_new_password")).sendKeys(password);
        driver.findElement(By.id("changeIt")).click();
        if (driver.findElement(By.id("Password changed")).isEnabled())
            return true;
        return false;
    }
}
