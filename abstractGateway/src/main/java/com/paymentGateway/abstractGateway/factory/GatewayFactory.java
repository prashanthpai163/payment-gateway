package com.paymentGateway.abstractGateway.factory;


import com.paymentGateway.abstractGateway.abstraction.EwayGateway;
import com.paymentGateway.abstractGateway.abstraction.Gateway;
import com.paymentGateway.abstractGateway.abstraction.StripeGateway;
import com.paymentGateway.abstractGateway.model.Gateways;

public class GatewayFactory {
	
	static Gateway gateway; 
	public static Gateway gatewayFactory(String factory) {
		System.out.println(factory);
		
		if(factory.equalsIgnoreCase(Gateways.STRIPE.toString()))
		{
			gateway=new StripeGateway();
		}
		else if(factory.equalsIgnoreCase(Gateways.EWAY.toString()))
		
		{
			gateway=new EwayGateway();
		}
		return gateway;
		
	}
	
	

}
