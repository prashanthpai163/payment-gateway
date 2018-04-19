package com.paymentGateway.abstractGateway.abstraction;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Properties;

import javax.servlet.http.HttpSession;
import com.eway.payment.rapid.sdk.RapidClient;
import com.eway.payment.rapid.sdk.RapidSDK;
import com.eway.payment.rapid.sdk.beans.external.CardDetails;
import com.eway.payment.rapid.sdk.beans.external.Customer;
import com.eway.payment.rapid.sdk.beans.external.PaymentDetails;
import com.eway.payment.rapid.sdk.beans.external.PaymentMethod;
import com.eway.payment.rapid.sdk.beans.external.Refund;
import com.eway.payment.rapid.sdk.beans.external.Transaction;
import com.eway.payment.rapid.sdk.beans.external.TransactionType;
import com.eway.payment.rapid.sdk.beans.internal.RefundDetails;
import com.eway.payment.rapid.sdk.output.CreateTransactionResponse;
import com.eway.payment.rapid.sdk.output.RefundResponse;
import com.paymentGateway.abstractGateway.dao.DatabaseConnection;
import com.paymentGateway.abstractGateway.model.Currency;
import com.paymentGateway.abstractGateway.model.RefundReference;
import com.paymentGateway.abstractgatewya.response.HttpResponse;
import com.paymentGateway.abstractGateway.model.Constants;
public class EwayGateway implements Gateway {
	private static Properties prop;
	private static String apiKey;
	private static String password;
	private static String rapidEnd;
	private static CardDetails extenalCardDetails;

	private static HttpResponse httpResponse=new HttpResponse();

	@Override
	public HttpResponse purchase(HttpSession session) {

		//fetching card deatils 
		com.paymentGateway.abstractGateway.model.CardDetails cardDetails=(com.paymentGateway.abstractGateway.model.CardDetails) session.getAttribute("cardDetails");

		prop=(Properties)session.getAttribute(Constants.PROPERTIES);
		apiKey=prop.getProperty(Constants.EWAY_API_KEY);
		password=prop.getProperty(Constants.EWAY_PASSWORD);
		rapidEnd=prop.getProperty(Constants.EWAY_RAPIDENDPOINT);

		RapidClient rapidClient=RapidSDK.newRapidClient(apiKey,password, rapidEnd);
		Transaction txn=new Transaction();
		PaymentDetails paymentDetails= new PaymentDetails();
		Customer customer=new Customer();

		//set card details
		extenalCardDetails=new CardDetails();
		extenalCardDetails.setNumber(cardDetails.getNumber());
		extenalCardDetails.setExpiryYear(cardDetails.getExpiryYear());
		extenalCardDetails.setExpiryMonth(cardDetails.getExpiryMonth());
		extenalCardDetails.setCVN(cardDetails.getCVN());
		extenalCardDetails.setName((String)prop.getProperty("eway_customer_name"));
		customer.setCardDetails(extenalCardDetails);

		//setting payment amount 
		paymentDetails.setTotalAmount(Integer.parseInt(prop.getProperty(Constants.AMOUNT)));
		//setting txn parameters
		txn.setPaymentDetails(paymentDetails);
		txn.setCurrencyCode(Currency.USD.toString());
		txn.getSecuredCardData();
		txn.setCustomer(customer);
		txn.setTransactionType(TransactionType.Purchase);
		//perform transaction
		CreateTransactionResponse response = rapidClient.create(PaymentMethod.Direct, txn);

		if(response.getErrors().isEmpty())
		{
			Transaction txn2=response.getTransaction();
			String refId=""+response.getTransactionStatus().getTransactionID();
			String app_ref="ew_ref_"+refId;
			session.setAttribute("refId", refId);
			session.setAttribute("app_ref", app_ref);
			session.setAttribute("amount", Integer.parseInt(prop.getProperty(Constants.AMOUNT)));
			session.setAttribute("transactionId", response.getTransactionStatus().getTransactionID());
			try {
				DatabaseConnection.insert(session);
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}	
			httpResponse.setRefId(app_ref);
		}


		else if (!response.getErrors().isEmpty()) {
			String description="";
			for (String errorcode: response.getErrors()) {
				description=errorcode+"\n";
				/*	System.out.println("Error Message: " 
						+ RapidSDK.userDisplayMessage(errorcode, "en"));*/
			}
			httpResponse.setDescriptiong(description);

		}
		return httpResponse;	
	}

	/*@Override
	public HttpResponse refund(HttpSession session) {
		String transactionID="";
		RapidClient client = RapidSDK.newRapidClient(apiKey, password, rapidEnd);

		RefundDetails refundDetails = new RefundDetails();
		refundDetails.setOriginalTransactionID(transactionID);
		refundDetails.setTotalAmount(50);

		Refund refund = new Refund();
		refund.setRefundDetails(refundDetails);

		RefundResponse response = client.refund(refund);
		if (response.getTransactionStatus().isStatus()) {
			System.out.println("Refund successful! ID: " + response.getTransactionStatus().getTransactionID());
		} else {
			if (!response.getErrors().isEmpty()) {
				for (String errorcode: response.getErrors()) {
					System.out.println("Error Message: " + RapidSDK.userDisplayMessage(errorcode, "en"));
				}
			} else {
				System.out.println("Sorry, your refund failed");
			}
		}

		httpResponse.setRefId(""+response.getTransactionStatus().getTransactionID());
		return httpResponse;	
		}*/

	@Override
	public HttpResponse refund(HttpSession session) {

		RefundReference refRes=null;
		prop=(Properties)session.getAttribute(Constants.PROPERTIES);
		apiKey=prop.getProperty(Constants.EWAY_API_KEY);
		password=prop.getProperty(Constants.EWAY_PASSWORD);
		rapidEnd=prop.getProperty(Constants.EWAY_RAPIDENDPOINT);
		
		RapidClient client = RapidSDK.newRapidClient(apiKey, password, rapidEnd);
		
		try {
			refRes=DatabaseConnection.retrive(session);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		RefundDetails refundDetails = new RefundDetails();
		
		String trans_ID =refRes.getRef();
		int amount= (int) refRes.amount;
		
		refundDetails.setOriginalTransactionID(trans_ID);
		refundDetails.setTotalAmount(amount);

		Refund refund = new Refund();
		refund.setRefundDetails(refundDetails);

		RefundResponse response = client.refund(refund);
		if (response.getTransactionStatus().isStatus()) {
			System.out.println("Refund successful! ID: " + response.getTransactionStatus().getTransactionID());
		} else {
			if (!response.getErrors().isEmpty()) {
				for (String errorcode: response.getErrors()) {
					System.out.println("Error Message: " + RapidSDK.userDisplayMessage(errorcode, "en"));
				}
			} else {
				System.out.println("Sorry, your refund failed");
			}
		}
		
		
		int refID=response.getTransactionStatus().getTransactionID();
		session.setAttribute("refund_refID", refID);		
		String app_ref="ew_refund_"+refID;
		session.setAttribute("refundId", ""+refID);
		session.setAttribute("refund_app_refId", app_ref);
		
		try {
			DatabaseConnection.insertRefund(session);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		System.out.println("ew_refund_"+response.getTransactionStatus().getTransactionID());
		httpResponse.setRefId("ew_refund_"+response.getTransactionStatus().getTransactionID());
		return httpResponse;	
		}

	

}
