package com.bit.core.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class ApplicationConfig {
	private static ApplicationConfig INSTANCE;
	public static ApplicationConfig getInstance() {
		if(INSTANCE == null) {
			INSTANCE = new ApplicationConfig();
		}
		return INSTANCE;
	}
	private Map<String, GatewayConfig> gatewayConfigMap = new HashMap<>();
	private Properties prop;
	private String environment;
	private ApplicationConfig() {
    	try(InputStream input = ClassLoader.getSystemResourceAsStream("application.properties")){
    		addGatewayConfig("TEST", new TestGatewayConfig());
    		prop = new Properties();
            prop.load(input);
            environment = prop.getProperty("environment");
    	}catch(IOException ex) {
    		System.out.println("CONFIG PROPERTIES ERROR");
            ex.printStackTrace();
        }
	}
	
	public void setGateways() {
		gatewayConfigMap.get(environment).setGateways();
	}
	
	public void addGatewayConfig(String key, GatewayConfig gatewayConfig) {
		gatewayConfigMap.put(key, gatewayConfig);
	}
	
	public void cleanGatewayStoreage() {
		gatewayConfigMap.get(environment).cleanStoreage();
	}
	
	public GatewayConfig getGatewayConfig() {
		return gatewayConfigMap.get(environment);
	}

}
