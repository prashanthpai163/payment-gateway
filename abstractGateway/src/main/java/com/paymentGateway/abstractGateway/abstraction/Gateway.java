package com.paymentGateway.abstractGateway.abstraction;

import javax.servlet.http.HttpSession;

import com.paymentGateway.abstractgatewya.response.HttpResponse;

public interface Gateway {
	HttpResponse purchase(HttpSession session);
	HttpResponse refund(HttpSession session);
	

}
