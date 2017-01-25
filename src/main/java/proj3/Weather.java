package proj3;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Scanner;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import java.util.Date;

public class Weather {
	private String fileName;
	private JSONObject json = null;

	public Weather(String fileName) {
		this.fileName = fileName;
	}

	public String getWeather() {
		if (null == this.json) {
			readFile();
		}
		if (!isUpdate()) {
			downloadAndSave();
		}
		return getInfo();
	}

	private boolean isUpdate() {
		if (null == json)
			return false;
		Object object = json.get("downloadTime");
		if (!(object instanceof Long))
			return false;
		Date date = new Date();
		long time = (long) object;
		if (time + 3600 * 1000 > date.getTime()) {
			return true;
		} else {
			return false;
		}
	}

	private void readFile() {
		try (Scanner read = new Scanner(new File(fileName))) {
			if (read.hasNextLine()) {
				this.json = (JSONObject) JSONValue.parse(read.nextLine());
			}
		} catch (FileNotFoundException e) {
			// it's ok
		}
	}

	@SuppressWarnings("unchecked")
	private void downloadAndSave() {
		JsonFromUrl jsonDownload = new JsonFromUrl("http://api.openweathermap.org/data/2.5/weather?q=" + fileName
				+ "&APPID=faac9b445a83f2ddd67d9b0f0fcb87a8");
		try (PrintWriter write = new PrintWriter(fileName)) {
			this.json = jsonDownload.pobierz();
			Date date = new Date();
			this.json.put("downloadTime", (long) date.getTime());
			write.print(this.json);
		} catch (IOException e) {
			System.out.println("write problem");
		}
	}

	private String getInfo() {
		String weather, temp, pressure, humidity, tempMin, tempMax, windSpeed, windDeg, clouds;

		JSONObject obj;
		JSONArray arr = (JSONArray) json.get("weather");
		obj = (JSONObject) arr.get(0);
		weather = jsonGetString(obj, "main");

		obj = (JSONObject) json.get("main");
		temp = jsonGetString(obj, "temp", true);
		pressure = jsonGetString(obj, "pressure");
		humidity = jsonGetString(obj, "humidity");
		tempMax = jsonGetString(obj, "temp_max", true);
		tempMin = jsonGetString(obj, "temp_min", true);

		obj = (JSONObject) json.get("wind");
		windSpeed = jsonGetString(obj, "speed");
		windDeg = jsonGetString(obj, "deg");

		obj = (JSONObject) json.get("clouds");
		clouds = jsonGetString(obj, "all");

		String ret = "Actual weather in " + fileName + ": weather: " + weather + ", temperature: " + temp
				+ "[°C], pressure: " + pressure + "[hPa], humidity: " + humidity + "[%], max temperature: " + tempMax
				+ "[°C], min temperature: " + tempMin + "[°C], wind speed: " + windSpeed + "[m/s], wind direction: "
				+ windDeg + "[degrees (meteorological)], cloudiness: " + clouds + "[%]";

		return ret;
	}

	private String jsonGetString(JSONObject obj, String name) {
		return jsonGetString(obj, name, false);
	}

	private String jsonGetString(JSONObject jObj, String name, Boolean kelvins) {
		String ret = null;

		Object object = jObj.get(name);
		if (object instanceof String) {
			ret = (String) object;

		} else if (object instanceof Long) {
			long number = (long) object;
			if (kelvins) {
				ret = String.valueOf((double) number - 273.15);
			} else {
				ret = String.valueOf(number);
			}

		} else if (object instanceof Double) {
			double number = (double) object;
			if (kelvins) {
				number -= 273.15;
			}
			ret = String.valueOf(number);

		} else
			System.out.println("unknow type");

		return ret;
	}
}
