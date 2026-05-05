package com.mycompany.app;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.By;

public class Task2 {
    public static void getMyIP() {
        System.setProperty("webdriver.chrome.driver", "C:\\Program Files\\ChromeDriver\\chromedriver.exe");
        
        WebDriver driver = new ChromeDriver();

        try {
            driver.manage().timeouts().pageLoadTimeout(10, java.util.concurrent.TimeUnit.SECONDS);
            
            driver.get("https://api.ipify.org/?format=json");
            
            WebElement elem = driver.findElement(By.cssSelector("body pre"));
            String jsonStr = elem.getText();

            JSONParser parser = new JSONParser();
            JSONObject obj = (JSONObject) parser.parse(jsonStr);
            String ip = (String) obj.get("ip");

            System.out.println("Ваш IP-адрес: " + ip);
            
        } catch (Exception e) {
            System.err.println("Ошибка при получении IP: " + e.getMessage());
        } finally {
            if (driver != null) {
                driver.quit();
            }
        }
    }
    
    public static void main(String[] args) {
        getMyIP();
    }
}