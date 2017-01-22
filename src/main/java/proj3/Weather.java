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
	String fileName;
	JSONObject json = null;
	long time = 0;

	Weather(String fileName) {
		this.fileName = fileName;
	}

	public String getWeather() {
		String ret = null;
		if (null != json) {
			Date date = new Date();
			time = (long) json.get("downloadTime");
			if (time + 3600 * 1000 > date.getTime()) {
				return getInfo();
			} else {
				return download();
			}
		}
		try (Scanner read = new Scanner(new File(fileName))) {
			if (read.hasNextLine()) {
				json = (JSONObject) JSONValue.parse(read.nextLine());
				ret = getInfo();
			} else
				throw new FileNotFoundException();

		} catch (FileNotFoundException e) {
			ret = download();
		}
		return ret;
	}

	private String getInfo() {
		String weather, temp, pressure, humidity, tempMin, tempMax, windSpeed, windDeg, clouds;

		JSONObject obj;
		JSONArray arr = (JSONArray) json.get("weather");
		obj = (JSONObject) arr.get(0);
		weather = jsonGetString(obj,"main");

		obj = (JSONObject) json.get("main");
		temp = jsonGetString(obj,"temp",true);
		pressure = jsonGetString(obj,"pressure");
		humidity = jsonGetString(obj,"humidity");
		tempMax = jsonGetString(obj,"temp_max",true);
		tempMin = jsonGetString(obj,"temp_min",true);

		obj = (JSONObject) json.get("wind");
		windSpeed = jsonGetString(obj,"speed");
		windDeg = jsonGetString(obj,"deg");

		obj = (JSONObject) json.get("clouds");
		clouds = jsonGetString(obj,"all");

		String ret = "Actual weather in "+fileName+": weather: " + weather + ", temperature: " + temp + "[°C], pressure: " + pressure
				+ "[hPa], humidity: " + humidity + "[%], max temperature: " + tempMax + "[°C], min temperature: "
				+ tempMin + "[°C], wind speed: " + windSpeed + "[m/s], wind direction: " + windDeg
				+ "[degrees (meteorological)], cloudiness: " + clouds + "[%]";
		
		return ret;
	}
	
	private String jsonGetString(JSONObject obj,String name){
		return jsonGetString(obj, name,false);
	}
	private String jsonGetString(JSONObject obj,String name,Boolean kelvins){
		String ret = null;
		try{
			ret=(String) obj.get(name);
		}catch (ClassCastException e){
			try{
				long number = (long) obj.get(name);
				ret=String.valueOf(number);
			}catch (ClassCastException e1){
				try{
					double number= (double) obj.get(name);
					if(kelvins){
						number-=273.15;
					}
					ret=String.valueOf(number);
				}catch (ClassCastException e2){
					System.out.println("unknow value");
				}
			}
		}
		return ret;
	}
	
	private String download() {
		String ret = null;
		JsonFromUrl jsonDownload = new JsonFromUrl("http://api.openweathermap.org/data/2.5/weather?q=" + fileName
				+ "&APPID=faac9b445a83f2ddd67d9b0f0fcb87a8");
		try (PrintWriter write = new PrintWriter(fileName)) {
			JSONObject json = jsonDownload.pobierz();
			Date date = new Date();
			json.put("downloadTime", (long) date.getTime());
			write.print(json);
			ret = getInfo();
		} catch (IOException e) {
			System.out.println("write problem");
		}
		return ret;
	}

}
