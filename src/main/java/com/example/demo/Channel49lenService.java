package com.example.demo;

import java.util.HashMap;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;

public class Channel49lenService {

	@Autowired
    BeanFactory factory;
	
	//TODO: to replace by content template engine like freemarker or jmustche
	private static final HashMap<Integer, Parser> template = new HashMap<>();
	
	private static final String E = "10000100";
	
	static {
		template.put(132, 
				new Parser(132, "{\n" + 
				"   \"humidity\":{\n" + 
				"      \"value\":\"%.2f\",\n" + 
				"      \"number\":2,\n" + 
				"      \"unit\":\"%%RH\"\n" + 
				"   },\n" + 
				"   \"temperature\":{\n" + 
				"      \"value\":\"%.2f\",\n" + 
				"      \"number\":1,\n" + 
				"      \"unit\":\"°C\"\n" + 
				"   },\n" + 
				"   \"voltage\":{\n" + 
				"      \"value\":\"%.3f\",\n" + 
				"      \"number\":3,\n" + 
				"      \"unit\":\"V\"\n" + 
				"   },\n" + 
				"   \"lqi\":{\n" + 
				"      \"number\":0,\n" + 
				"      \"value\":%d\n" + 
				"   }\n" + 
				"}\n" + 
				"")
		);
		template.put(152, 
				new Parser(152, "{\n" + 
				"   \"CO2\":{\n" + 
				"      \"value\":\"%d\",\n" + 
				"      \"number\":3,\n" + 
				"      \"unit\":\"ppm\"\n" + 
				"   },\n" + 
				"   \"humidity\":{\n" + 
				"      \"value\":\"%.2f\",\n" + 
				"      \"number\":2,\n" + 
				"      \"unit\":\"%%RH\"\n" + 
				"   },\n" + 
				"   \"temperature\":{\n" + 
				"      \"value\":\"%.2f\",\n" + 
				"      \"number\":1,\n" + 
				"      \"unit\":\"°C\"\n" + 
				"   },\n" + 
				"   \"lqi\":{\n" + 
				"      \"number\":0,\n" + 
				"      \"value\":%d\n" + 
				"   }\n" + 
				"}\n")
		);
	}
	
    public void receive(Message<int[]> message) {
    	int[] content = message.getPayload();
    	int type = content[9];
    	System.out.println(template.get(type).toJSONString(content));
    }
    
    private static int mergeByteValue(int hByte, int lByte) {
    	int mergeInt = (hByte << 8) | lByte;
    	return mergeInt;
    }
    
    private static class Parser {
    	private final String jsonFormat;
    	private final int equipType;
    	
    	public Parser(int equipType, String jsonFormat) {
    		this.equipType = equipType;
    		this.jsonFormat = jsonFormat;
    	}

		public String toJSONString(int[] content) {
			String result = null;
			switch (equipType) {
			case 132:
				result = String.format(jsonFormat, 
						mergeByteValue(content[14], content[15]) / 100f, //humidity
						mergeByteValue(content[12], content[13]) / 100f, //temperature
						mergeByteValue(content[16], content[17]) / 1000f, //voltage
						content[45]//lqi
						);
				break;
			case 152:
				result = String.format(jsonFormat, 
						mergeByteValue(content[16], content[17]), //CO2
						mergeByteValue(content[14], content[15]) / 100f, //humidity
						mergeByteValue(content[12], content[13]) / 100f, //temperature
						content[45]//lqi
						);
				break;
			}
			return result;
		}
    }
    
}