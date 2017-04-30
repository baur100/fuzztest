package com.test;

/**
 * Created by Baurz on 4/21/2017.
 * Checking GMAIL account for Recovery and Password Change email using IMAP protocol
 */

import org.openqa.selenium.WebDriver;

import javax.mail.Flags;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.search.SubjectTerm;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Properties;

public class MailHelper {
    Properties props = System.getProperties();
    WebDriver driver;

    public MailHelper(WebDriver driver) {
        this.driver = driver;
    }

    public WebDriver checkMail(String email, String password, String pattern) throws Exception {
        props.setProperty("mail.store.protocol", "imaps");
        Session session = Session.getDefaultInstance(props, null);
        Store store = session.getStore("imaps");
        store.connect("imap.gmail.com", email, password);
        Folder folder = store.getFolder("INBOX");
        folder.open(Folder.READ_WRITE);

        Message[] messages = null;
        boolean isMailFound = false;
        Message mailFromRecovery = null;

        //Search for mail from Recovery
        for (int i = 0; i < 5; i++) {
            messages = folder.search(new SubjectTerm(pattern), folder.getMessages());
            //Wait for 10 seconds
            if (messages.length == 0) {
                Thread.sleep(10000);
            }
        }
        //Search for unread mail from Recovery
        for (Message mail : messages) {
            if (!mail.isSet(Flags.Flag.SEEN)) {
                mailFromRecovery = mail;
                isMailFound = true;
            }
        }
        //Test fails if no unread mail was found from Recovery
        if (!isMailFound) {
            throw new Exception("Could not find confirmation email");
            //Read the content of mail and launch registration URL
        } else {
            String line;
            StringBuffer buffer = new StringBuffer();
            BufferedReader reader = new BufferedReader(new InputStreamReader(mailFromRecovery.getInputStream()));
            while ((line = reader.readLine()) != null) {
                buffer.append(line);
            }
            //Your logic to split the message and get the Registration URL goes here
            String mailURL = buffer.toString().split("http://www.chatter.site/members/?")[0].split("href=")[1];
            driver.get(mailURL);
            return driver;
        }
    }
}