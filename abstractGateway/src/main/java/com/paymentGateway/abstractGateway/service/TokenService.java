package com.paymentGateway.abstractGateway.service;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;

import com.stripe.Stripe;
import com.stripe.exception.APIConnectionException;
import com.stripe.exception.APIException;
import com.stripe.exception.AuthenticationException;
import com.stripe.exception.CardException;
import com.stripe.exception.InvalidRequestException;
import com.stripe.model.Token;
import com.mysql.cj.api.Session;
import com.paymentGateway.abstractGateway.model.*;

@Service
public class TokenService {

	static Token token;
	static Properties prop;

	public static String createToken(CardDetails cardDetails, HttpSession session)
	{
		
		prop=(Properties)session.getAttribute("properties");
		Stripe.apiKey = prop.getProperty("stripe_api_key");

		Map<String, Object> tokenParams = new HashMap<String, Object>();
		Map<String, Object> cardParams = new HashMap<String, Object>();

		cardParams.put("number", cardDetails.getNumber());

		int expMonth=Integer.parseInt(cardDetails.getExpiryMonth());
		cardParams.put("exp_month",expMonth);

		int expYear=Integer.parseInt(cardDetails.getExpiryYear());
		cardParams.put("exp_year", expYear);

		cardParams.put("cvc", cardDetails.getCVN());

		tokenParams.put("card", cardParams);

		try {
			token=Token.create(tokenParams);
		} catch (AuthenticationException | InvalidRequestException | APIConnectionException | CardException
				| APIException e) {
		}

		return token.getId();
	}
}