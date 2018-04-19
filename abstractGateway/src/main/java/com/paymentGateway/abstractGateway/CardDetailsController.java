package com.paymentGateway.abstractGateway;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Properties;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.ws.rs.Consumes;
import javax.ws.rs.core.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.paymentGateway.abstractGateway.config.AbstractPayentGatewayConfig;
import com.paymentGateway.abstractGateway.model.CardDetails;
import com.paymentGateway.abstractGateway.service.GatewayService;
import com.paymentGateway.abstractgatewya.response.HttpResponse;
import com.paymentGateway.abstractGateway.model.Customer;
import com.paymentGateway.abstractGateway.model.Constants;


@Controller
public class CardDetailsController extends AbstractPayentGatewayConfig{
	static CardDetailsController config= new CardDetailsController();
	static HttpResponse resp;
	static GatewayService gatewayService=new GatewayService();
	static HttpSession session;
	@RequestMapping( value ="/card" ,method = RequestMethod.POST)
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	public String getReference(@RequestParam ("cardNumber") String cardNumber, 
			@RequestParam ("expirydate") String expDate, @RequestParam ("cvc") String cvc , 

			@RequestParam("gateway") String gateway, 
			@RequestParam("cust_name") String cust_name,
			@RequestParam ("cust_emailId") String cust_emailId,
			@RequestParam ("address") String address,
			Model model, HttpServletRequest request) {

		session=request.getSession();
		session=config.getConfig(session);
		session.setAttribute(Constants.GATEWAY, gateway);
		session.setAttribute(Constants.TRANSACTION, "purchase");

		Customer cust=new Customer();
        cust.setName(cust_name);
        cust.setEmailId(cust_emailId);
        cust.setAddress(address);

		session.setAttribute(Constants.CUSTOMER, cust);


		CardDetails cardDetails=new CardDetails();
		cardDetails.setNumber(cardNumber);
		cardDetails.setCVN(cvc);
		cardDetails.setExpiryMonth(expDate.substring(0, 2));
		cardDetails.setExpiryYear(expDate.substring(3));

		session.setAttribute(Constants.CARD_DETAILS, cardDetails);

		resp=gatewayService.service(session);
		if(resp.getDescription() == null)
		{

			model.addAttribute(Constants.REFERENCE, resp.getRefId());
			model.addAttribute(Constants.CUSTOMER, cust_name);
			return "response";
		}

		else 
		{
			model.addAttribute(Constants.DESCRIPTION, resp.getDescription());
			return "error";
		}
	}


	@RequestMapping( value ="/refund" ,method = RequestMethod.POST)
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	public String refund(@RequestParam("gateway") String gateway,
			@RequestParam("transactionId") String transactionId, Model model,
			HttpServletRequest request)
	{
		session=request.getSession();
		session=config.getConfig(session);
		session.setAttribute(Constants.TRANSACTION, "refund");
		session.setAttribute(Constants.GATEWAY, gateway);

		session.setAttribute("transactionId", transactionId);

		resp=gatewayService.service(session);
		String refund_app_refId=(String) session.getAttribute("refund_app_refId");

		if(resp.getDescription() == null)
		{
			String name=(String) session.getAttribute("name");
			System.out.println(name);
			model.addAttribute(Constants.REFERENCE, refund_app_refId);
			model.addAttribute(Constants.CUSTOMER, name);
			
			return "response";
		}

		else 
		{
			model.addAttribute("message", resp.getDescription());
			return "error";
		}


	}

}

