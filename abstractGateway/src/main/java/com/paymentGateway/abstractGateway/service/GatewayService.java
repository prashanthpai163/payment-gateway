package com.paymentGateway.abstractGateway.service;

import javax.servlet.http.HttpSession;
import com.paymentGateway.abstractGateway.abstraction.Gateway;
import com.paymentGateway.abstractGateway.factory.GatewayFactory;
import com.paymentGateway.abstractgatewya.response.HttpResponse;

public class GatewayService {
	
	public HttpResponse  service(HttpSession session) {
		
		HttpResponse resp=null;
		
		String gatewaysel=(String) session.getAttribute("gateway");
		System.out.println(gatewaysel);
		Gateway  gateway =GatewayFactory.gatewayFactory(gatewaysel);
		
		String transaction=(String)session.getAttribute("transaction");
		
		
		if(transaction.equalsIgnoreCase("purchase"))
		{
			 resp= gateway.purchase(session);
		}
		
		else if(transaction.equalsIgnoreCase("refund"))
		{
			 resp=gateway.refund(session);
		}
		
		return resp;
	}

}
