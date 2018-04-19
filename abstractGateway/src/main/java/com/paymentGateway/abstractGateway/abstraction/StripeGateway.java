package com.paymentGateway.abstractGateway.abstraction;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.servlet.http.HttpSession;

import com.paymentGateway.abstractGateway.dao.DatabaseConnection;
import com.paymentGateway.abstractGateway.model.CardDetails;
import com.paymentGateway.abstractGateway.model.Currency;
import com.paymentGateway.abstractGateway.model.RefundReference;
import com.paymentGateway.abstractGateway.service.TokenService;
import com.paymentGateway.abstractgatewya.response.HttpResponse;
import com.stripe.Stripe;
import com.stripe.exception.APIConnectionException;
import com.stripe.exception.APIException;
import com.stripe.exception.AuthenticationException;
import com.stripe.exception.CardException;
import com.stripe.exception.InvalidRequestException;
import com.stripe.model.Charge;
import com.stripe.model.Refund;
import com.stripe.net.RequestOptions;


import com.paymentGateway.abstractGateway.model.Constants;


public class StripeGateway implements Gateway{
	static String token;
	static String ref;
	static Charge charge;
	static HttpResponse httpResp=new HttpResponse();
	static String stripe_apiKey;
	static Properties prop;
	@Override

	public HttpResponse purchase(HttpSession session) 
	{
		CardDetails cardDetails=(CardDetails) session.getAttribute(Constants.CARD_DETAILS);
		token=TokenService.createToken(cardDetails, session);

		if(token != null)
		{    
			prop=getProp(session);
			stripe_apiKey = (String)prop.getProperty(Constants.STRIPE_API_KEY);
			RequestOptions requestOptions=RequestOptions.builder().setApiKey(stripe_apiKey).build();

			Map<String, Object> chargeParams = new HashMap<String, Object>();
			chargeParams.put(Constants.AMOUNT, (String)prop.get("amount"));
			chargeParams.put(Constants.CURRENCY, Currency.USD);
			chargeParams.put(Constants.DESCRIPTION, "Charge for shruthi2602@outlook.com");
			chargeParams.put(Constants.SOURCE, token);

			try {
				charge=Charge.create(chargeParams, requestOptions);

			} catch (AuthenticationException e) {
				httpResp.setDescriptiong(e.getMessage());

			} catch (InvalidRequestException e) {
				httpResp.setDescriptiong(e.getMessage());
				return httpResp;
			} catch (APIConnectionException e) {
				httpResp.setDescriptiong(e.getMessage());
				return httpResp;
			} catch (CardException e) {
				httpResp.setDescriptiong(e.getMessage());
				return httpResp;
			} catch (APIException e) {
				httpResp.setDescriptiong(e.getMessage());
				return httpResp;
			} 

			ref=charge.getId();

			if(ref != null)
			{
				String app_ref="stp_ref_"+ref;
				try {
					session.setAttribute("refId", ref);
					session.setAttribute("app_ref", app_ref);
					session.setAttribute("amount", Integer.parseInt(prop.getProperty(Constants.AMOUNT)));
					session.setAttribute("transactionId", charge.getId());
					DatabaseConnection.insert( session);
				} catch (ClassNotFoundException e) {
					httpResp.setDescriptiong(e.getMessage());
					return httpResp;
				}

				httpResp.setRefId(app_ref);
			}

			else 
			{
				httpResp.setStatusCode(charge.getFailureCode());
				httpResp.setDescriptiong(charge.getFailureMessage());
				return httpResp;

			}

		}
		else
		{
			httpResp.setDescriptiong("Token is null");
			return httpResp;
		}
		return httpResp;	
	}

	@Override
	public HttpResponse refund(HttpSession session) {

		Refund refund;
		prop=getProp(session);
		stripe_apiKey = (String)prop.getProperty(Constants.STRIPE_API_KEY);
		Stripe.apiKey = stripe_apiKey;
		//get ref_id
		RefundReference refRes=DatabaseConnection.retrive(session);

		Map<String, Object> refundParams = new HashMap<String, Object>();
		refundParams.put("charge", refRes.getRef());
		int amount= (int) refRes.amount;
		refundParams.put("amount", amount);

		try {
			refund=Refund.create(refundParams);
			String refundId=refund.getId();
			httpResp.setRefId(refundId);
			String refund_app_refId="app_refund_st"+refRes.getRef();
			
			session.setAttribute("refund_app_refId", refund_app_refId);
			session.setAttribute("refundId", refundId);
			//insert refund into database 
			DatabaseConnection.insertRefund(session);
		} catch (AuthenticationException | InvalidRequestException | APIConnectionException | CardException
				| APIException e) {
			httpResp.setDescriptiong(e.getMessage());
			return httpResp;
		}

		return httpResp;
	}


	public Properties  getProp(HttpSession session) {
		prop=(Properties)session.getAttribute(Constants.PROPERTIES);

		return prop;
	}


}
