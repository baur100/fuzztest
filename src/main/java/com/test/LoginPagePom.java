package com.test;

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
 * POM page for Main (Login) page of Chatter
 */
public class LoginPagePom {

    WebDriver driver = new FirefoxDriver();
    Properties prop = new Properties();

    //Class constructor
    public LoginPagePom(WebDriver driver) throws IOException {
        this.driver = driver;
        FileInputStream fis = new FileInputStream("data//env.properties");
        prop.load(fis);
    }

    public void goTo() {
        driver.get(prop.getProperty("WEB_ADDRESS"));
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
    }

    @FindBy(id = "user_id_field")
    WebElement username;

    @FindBy(id = "user_pass_field")
    WebElement password;

    @FindBy(id = "submit_button")
    WebElement submit;

    public void login(String uid, String pass) {
        username.sendKeys(uid);
        password.sendKeys(pass);
        submit.click();
    }
    public Boolean accountCredentials(String uid, String password){//Check in database that credentials is good, return true if credentials good
        if(uid!=password) //Just kind of mock, here is REST or SQL request to backend
            return true;
        return false;
    }

}
