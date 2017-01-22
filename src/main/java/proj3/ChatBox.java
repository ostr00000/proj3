package proj3;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Pattern;


public class ChatBox extends Chat{
	private static Pattern time=Pattern.compile(".*[Tt]ime.*");
	private static Pattern weather=Pattern.compile(".*[Ww]eather.*");
	private static Pattern day=Pattern.compile(".*[Dd]ay.*");
	
	private Weather weatherInCracow;
	
	ChatBox(String name, ChatWebSocketHandler webSocket) {
		super(name, webSocket);
		weatherInCracow=new Weather("Krakow");
	}
	
	 public void broadcastMessage(String userName,String message) {
	        super.broadcastMessage(userName, message);
	        if(time.matcher(message).matches()){
	        	String time = new SimpleDateFormat("HH:mm:ss").format(new Date());
	        	super.broadcastMessage(this.name,"The time is "+time);
	        }
	        if(weather.matcher(message).matches()){
	        	String weather=this.weatherInCracow.getWeather();
	        	super.broadcastMessage(this.name,weather);
	        }
	        if(day.matcher(message).matches()){ 
	        	String time = new SimpleDateFormat("EEEEEEEEEEEE").format(new Date());
	        	super.broadcastMessage(this.name,"Today is "+time);
	        }
	    }
	 
	
}
