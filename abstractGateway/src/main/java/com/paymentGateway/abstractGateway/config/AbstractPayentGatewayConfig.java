package com.paymentGateway.abstractGateway.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import javax.servlet.http.HttpSession;

import com.paymentGateway.abstractGateway.model.Constants;
import com.paymentGateway.abstractgatewya.response.HttpResponse;

public class AbstractPayentGatewayConfig {
	
	protected HttpSession getConfig(HttpSession session) {
		
		Properties prop = new Properties();
		ClassLoader loader = Thread.currentThread().getContextClassLoader();           
		InputStream stream = loader.getResourceAsStream("application.properties");
		try {
			prop.load(stream);
			session.setAttribute(Constants.PROPERTIES, prop);
		} catch (IOException e) {
			HttpResponse resp= new HttpResponse();
			resp.setDescriptiong("Unable to load properties file");
		}
		return session;
		
		
	}

}
