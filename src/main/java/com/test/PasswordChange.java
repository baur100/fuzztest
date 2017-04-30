package com.test;


import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.FindBy;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * Created by Baurz on 4/21/2017.
 */
public class PasswordChange {
    WebDriver driver = new FirefoxDriver();

    public PasswordChange(WebDriver driver) throws IOException {
        this.driver = driver;
    }

    public void goTo() {
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        driver.findElement(By.xpath("//*[@class='password_change']")).click();
    }

    @FindBy(id = "old_password")
    WebElement oldPassword;
    @FindBy(id = "op_submit")
    WebElement opbutton;
    @FindBy(id = "new_password")
    WebElement newPassword;
    @FindBy(id = "confirm_new_password")
    WebElement newPassword1;
    @FindBy(id = "submit")
    WebElement button;

    public Boolean enterOldPass(String oldpass) {
        oldPassword.sendKeys(oldpass);
        opbutton.click();
        if (driver.findElement(By.id("old_pass_accepted")).isDisplayed())
            return true;
        return false;
    }

    public Boolean enterNewPassword(String password) {
        newPassword.sendKeys(password);
        newPassword1.sendKeys(password);
        button.click();
        if (driver.findElement(By.id("password_changes_successfully")).isDisplayed())
            return true;
        return false;
    }

    public Boolean emailSend() {
        if (driver.findElement(By.id("confirmation_email_has_been_send")).isDisplayed())
            return true;
        return false;
    }
}



