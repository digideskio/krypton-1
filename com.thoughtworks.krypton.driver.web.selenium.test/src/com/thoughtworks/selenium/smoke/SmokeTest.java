/****************************************************************************
 * Copyright 2008-2011 ThoughtWorks, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Initial Contributors:
 *   Håkan Råberg
 *   Manish Chakravarty
 *   Pavan K S
 ***************************************************************************/
package com.thoughtworks.selenium.smoke;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.thoughtworks.selenium.DefaultSelenium;
import com.thoughtworks.selenium.Selenium;

import static org.junit.Assert.*;


public class SmokeTest {
    private static Selenium browser;

    @BeforeClass
    public static void startBrowser() {
        browser = new DefaultSelenium("localhost", 4444, "*firefox", "http://www.google.com");
        browser.start();
    }

    @Test
    public void testGoogle() throws InterruptedException {
        browser.open("http://www.google.com/webhp?hl=en");
        browser.type("q", "hello world\r");

        assertTrue(browser.isTextPresent("helloworld.com"));
        assertEquals("hello world - Google Search", browser.getTitle());
    }

//    @Test
//	public void testGMail() throws Exception {
//    	System.out.println("Enter your gmail password: ");
//    	String password = new BufferedReader(new InputStreamReader(System.in)).readLine();
//		browser.open("http://mail.google.com");
//		browser.type("Email", "hakan.raberg");
//		browser.type("Passwd", password);
//		browser.click("signIn");
//		browser.selectFrame("canvas_frame");
//		System.out.println(browser.getText("//a[contains(text(),'Inbox')]"));
//		browser.click("//span[text() = 'LinkedIn Updates']");
//		browser.click("//div[text() = 'Reply']");
//	}

	// Doesn't work on Windows for now.
//    @Test
//    public void testDigg() throws Exception {
//        browser.open("http://www.digg.com");
//        browser.click("//div[contains(@class, 'news-summary')][1]//a[@class = 'tool comments']");
//        browser.click("//a[text() = 'digg it']");
//
//        assertTrue(browser.isTextPresent("You've got to login or join to Digg that!"));
//        assertTrue(browser.isElementPresent("//div[@class = 'login-digg']//input[@name = 'username']"));
//
//        assertTrue(browser.isElementPresent("//a[text() = 'Login now...']"));
//        assertTrue(browser.isVisible("//a[text() = 'Login now...']"));
//        browser.click("//a[text() = 'Login now...']");
//
//        assertTrue(browser.isVisible("//div[@class = 'login-digg']//input[@name = 'username']"));
//        browser.type("//div[@class = 'login-digg']//input[@name = 'username']", "digguser");
//        assertEquals("digguser", browser.getValue("//div[@class = 'login-digg']//input[@name = 'username']"));
//        browser.type("//div[@class = 'login-digg']//input[@name = 'password']", "diggpassword");
//
//        browser.click("//input[@type = 'submit' and @value = 'Login']");
//        assertTrue(browser.isTextPresent("Your Username or Password was incorrect"));
//    }

    @AfterClass
    public static void stopBrowser() {
        browser.stop();
    }
}
