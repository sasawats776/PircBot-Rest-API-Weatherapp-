import org.jibble.pircbot.*;
import java.text.*;
import java.math.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class MyBot extends PircBot {
	private String defaultURL = "http://api.openweathermap.org/data/2.5/weather?q=dallas&APPID=22c78246187f28c8a6586ce7bfd81709";
	private static DecimalFormat df = new DecimalFormat("#.##");
	public MyBot(){
		df.setRoundingMode(RoundingMode.CEILING);
		this.setName("SasawatBot");
	}
	
	public void onMessage(String channel, String sender, String login, String hostname, String message){
		String [] words = message.split(" ");
		String json = " ";
		
		if(words[0].equalsIgnoreCase("time")){ //time request
			String time = new java.util.Date().toString();
			sendMessage(channel, sender + ": The time is now " + time + " in dallas");
		}
		else if(words.length == 2){ //advanced weather request, requires weather and zip/city name
			if(words[0].equalsIgnoreCase("weather") && isInteger(words[1])){
				try{
					json = getJSON(words[1]);
					sendMessage(channel, sender + ": The weather in " + words[1] + " is now " + getTemp(json) + "\u00b0" + "F with a high of " + getHigh(json) + "\u00b0" + "F and a low of " + getLow(json) + "\u00b0" + "F");
					sendMessage(channel, sender + ": The weather is predicted to have " + getDescription(json) + " with winds of " + getWinds(json) + " meters per second");
				}
				catch(IOException e){
					return;
				}
				
			}
			else if(words[1].equalsIgnoreCase("weather") && isInteger(words[0])){
				try{
					json = getJSON(words[0]);
					sendMessage(channel, sender + ": The weather in " + words[0] + " is now " + getTemp(json) + "\u00b0" + "F with a high of " + getHigh(json) + "\u00b0" + "F and a low of " + getLow(json) + "\u00b0" + "F");
					sendMessage(channel, sender + ": The weather is predicted to have " + getDescription(json) + " with winds of " + getWinds(json) + " meters per second");
				}
				catch(IOException e){
					return;
				}
				
			}
			else if(words[0].equalsIgnoreCase("weather")){
				try{
					json = getJSON(words[1]);
					sendMessage(channel, sender + ": The weather in " + words[1] + " is now " + getTemp(json) + "\u00b0" + "F with a high of " + getHigh(json) + "\u00b0" + "F and a low of " + getLow(json) + "\u00b0" + "F");
					sendMessage(channel, sender + ": The weather is predicted to have " + getDescription(json) + " with winds of " + getWinds(json) + " meters per second");
				}
				catch(IOException e){
					return;
				}
			}
			else if(words[1].equalsIgnoreCase("weather")){
				try{
					json = getJSON(words[0]);
					sendMessage(channel, sender + ": The weather in " + words[0] + " is now " + getTemp(json) + "\u00b0" + "F with a high of " + getHigh(json) + "\u00b0" + "F and a low of " + getLow(json) + "\u00b0" + "F");
					sendMessage(channel, sender + ": The weather is predicted to have " + getDescription(json) + " with winds of " + getWinds(json) + " meters per second");
				}
				catch(IOException e){
					return;
				}
			}
			else if((words[0].equalsIgnoreCase("coordinates") && isInteger(words[1])) || (words[1].equalsIgnoreCase("coordinates") && isInteger(words[0]))){
				try{
					json = getJSON(words[1]);
					sendMessage(channel, sender + ": the coordinates of " + words[1] + " are: longitude = " + getlongitude(json) + ", latitude = " + getlatitude(json));
				}
				catch(IOException e){
					return;
				}
			}
		}
		else if(words[0].equalsIgnoreCase("weather")){ //weather default request
			try{
				json = getJSON("dallas");
				sendMessage(channel, sender + ": The weather in dallas is now " + getTemp(json) + "\u00b0" + "F with a high of " + getHigh(json) + "\u00b0" + "F and a low of " + getLow(json) + "\u00b0" + "F (DEFAULT RESPONSE)");
				sendMessage(channel, sender + ": The weather is predicted to have " + getDescription(json) + " with winds of " + getWinds(json) + " meters per second");
			}
			catch(IOException e){
				return;
			}
		}
		else if(words[0].equalsIgnoreCase("coordinates")){ //coordinates request
				try{
					json = getJSON("dallas");
					sendMessage(channel, sender + ": the coordinates of dallas are: longitude = " + getlongitude(json) + ", latitude = " + getlatitude(json) + "(DEFAULT RESPONSE)");
				}
				catch(IOException e){
					return;
				}
		}
	}
	
	static String getJSON(String city) throws IOException{
        StringBuilder result = new StringBuilder();
        String urlToRead = " ";
        if(isInteger(city)){
        	urlToRead = "http://api.openweathermap.org/data/2.5/weather?q="+city+",us&APPID=22c78246187f28c8a6586ce7bfd81709";
        }
        else{
        	urlToRead = "http://api.openweathermap.org/data/2.5/weather?q=dallas&APPID=22c78246187f28c8a6586ce7bfd81709";
        
        URL url = new URL(urlToRead);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        
        conn.setRequestMethod("GET");
        
        BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        String line;
        
        while ((line = rd.readLine()) != null) {
        	result.append(line);
        }  
        
        rd.close();
        }
        return result.toString();
        
	}
	
	static double getlatitude(String weatherjson)
	{
	    JsonElement jelement = new JsonParser().parse(weatherjson);
	    
	    JsonObject mainObject = jelement.getAsJsonObject();
	    
	    JsonObject coordinateObject = mainObject.getAsJsonObject("coord");
	
	    double latitude = coordinateObject.get("lat").getAsDouble();
	    
	    return Double.parseDouble(df.format(latitude));
	}
	
	static double getlongitude(String weatherjson)
	{
	    JsonElement jelement = new JsonParser().parse(weatherjson);
	    
	    JsonObject mainObject = jelement.getAsJsonObject();
	    
	    JsonObject coordinateObject = mainObject.getAsJsonObject("coord");
	
	    double longitude = coordinateObject.get("lon").getAsDouble();
	
	    return Double.parseDouble(df.format(longitude));
	}
	
	static double getTemp(String weatherjson)
	{
	    JsonElement jelement = new JsonParser().parse(weatherjson);
	    
	    JsonObject mainObject = jelement.getAsJsonObject();
	    
	    JsonObject coordinateObject = mainObject.getAsJsonObject("main");
	
	    double temp = coordinateObject.get("temp").getAsDouble();
	    
	    temp = temp * (9.0/5.0) - 459.67;
	
	    return Double.parseDouble(df.format(temp));
	}
	
	static double getLow(String weatherjson)
	{
	    JsonElement jelement = new JsonParser().parse(weatherjson);
	    
	    JsonObject mainObject = jelement.getAsJsonObject();
	    
	    JsonObject coordinateObject = mainObject.getAsJsonObject("main");
	    
	    double low = coordinateObject.get("temp_min").getAsDouble();
	    
	    low = low * (9.0/5.0) - 459.67;
	    
	    return Double.parseDouble(df.format(low));
	}
	
	static double getHigh(String weatherjson)
	{
	    JsonElement jelement = new JsonParser().parse(weatherjson);
	    
	    JsonObject mainObject = jelement.getAsJsonObject();
	    
	    JsonObject coordinateObject = mainObject.getAsJsonObject("main");
	    
	    double high = coordinateObject.get("temp_max").getAsDouble();
	    
	    high = high * (9.0/5.0) - 459.67;
	    
	    return Double.parseDouble(df.format(high));
	}
	
	static String getDescription(String weatherjson){
		
		JsonElement jelement = new JsonParser().parse(weatherjson);
		    
		JsonObject mainObject = jelement.getAsJsonObject();
		    
		JsonArray descriptObject = mainObject.getAsJsonArray("weather");
		
		mainObject = descriptObject.get(0).getAsJsonObject();
		
		String description = mainObject.get("description").getAsString();
		
		return description;
	}
	
	static double getWinds(String weatherjson){
		
		JsonElement jelement = new JsonParser().parse(weatherjson);
		    
		JsonObject mainObject = jelement.getAsJsonObject();
		    
		JsonObject windObject = mainObject.getAsJsonObject("wind");
		
		double wind = windObject.get("speed").getAsDouble();
		
		return wind;
	}
	
	static boolean isInteger( String input ) {
	    try {
	        Integer.parseInt( input );
	        return true;
	    }
	    catch( NumberFormatException e ) {
	        return false;
	    }
	}
}
