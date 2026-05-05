package com.mycompany.app;

import org.json.simple.JSONObject;
import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.By;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Task3 {
    private static final String DRIVER_PATH = "C:\\Users\\user\\Downloads\\chromedriver-win64\\chromedriver-win64\\chromedriver.exe";
    private static final String API_URL = "https://api.open-meteo.com/v1/forecast?latitude=56.3287&longitude=44.002&hourly=temperature_2m,rain&timezone=Europe%2FMoscow&forecast_days=1";
    private static final String OUTPUT_DIR = "result";
    private static final String OUTPUT_FILE = "forecast.txt";

    public static void getWeatherForecast() {
        System.setProperty("webdriver.chrome.driver", DRIVER_PATH);
        WebDriver driver = new ChromeDriver();

        try {
            driver.manage().timeouts().pageLoadTimeout(15, java.util.concurrent.TimeUnit.SECONDS);
            driver.get(API_URL);

            WebElement elem = driver.findElement(By.cssSelector("pre"));
            String jsonStr = elem.getText();
            System.out.println("Получены данные погоды");

            processWeatherData(jsonStr);
            
        } catch (Exception e) {
            System.err.println("Ошибка при получении данных: " + e.getMessage());
        } finally {
            if (driver != null) {
                driver.quit();
            }
        }
    }

    private static void processWeatherData(String jsonStr) throws Exception {
        JSONParser parser = new JSONParser();
        JSONObject obj = (JSONObject) parser.parse(jsonStr);

        if (!obj.containsKey("hourly")) {
            throw new Exception("Отсутствуют данные о погоде в ответе API");
        }

        JSONObject hourly = (JSONObject) obj.get("hourly");
        JSONArray times = (JSONArray) hourly.get("time");
        JSONArray temps = (JSONArray) hourly.get("temperature_2m");
        JSONArray rains = (JSONArray) hourly.get("rain");

        createOutputDirectory();
        String filePath = OUTPUT_DIR + "/" + OUTPUT_FILE;

        try (FileWriter writer = new FileWriter(filePath)) {
            writeWeatherHeader(writer);
            writeWeatherData(writer, times, temps, rains);
            System.out.println("Данные успешно сохранены в " + filePath);
        }
    }

    private static void createOutputDirectory() throws IOException {
        if (!Files.exists(Paths.get(OUTPUT_DIR))) {
            Files.createDirectories(Paths.get(OUTPUT_DIR));
        }
    }

    private static void writeWeatherHeader(FileWriter writer) throws IOException {
        String timestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        writer.write("Прогноз погоды в Нижнем Новгороде (актуально на " + timestamp + ")\n");
        writer.write("№\tВремя\t\tТемпература\tОсадки\n");
        
        System.out.println("\nПрогноз погоды в Нижнем Новгороде:");
        System.out.println("№\tВремя\t\tТемпература\tОсадки");
    }

    private static void writeWeatherData(FileWriter writer, JSONArray times, JSONArray temps, JSONArray rains) throws IOException {
        for (int i = 0; i < times.size(); i++) {
            String time = ((String) times.get(i)).substring(11);
            String temp = String.format("%.1f", Double.parseDouble(temps.get(i).toString()));
            String rain = rains.get(i).toString();

            String line = String.format("%d\t%s\t%s°C\t\t%s мм\n", i+1, time, temp, rain);
            writer.write(line);
            System.out.print(line);
        }
    }

    public static void main(String[] args) {
        getWeatherForecast();
    }
}