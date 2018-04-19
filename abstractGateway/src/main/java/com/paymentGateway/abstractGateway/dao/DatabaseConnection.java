package com.paymentGateway.abstractGateway.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Properties;

import javax.servlet.http.HttpSession;

import com.eway.payment.rapid.sdk.output.RefundResponse;
import com.paymentGateway.abstractGateway.model.Constants;
import com.paymentGateway.abstractGateway.model.Customer;
import com.paymentGateway.abstractGateway.model.RefundReference;
import com.paymentGateway.abstractgatewya.response.HttpResponse;

public class DatabaseConnection {

	static String driver;
	static String url;
	static String userName;
	static String password;
	static Connection con;
	static PreparedStatement stmnt;
	static HttpResponse resp=new HttpResponse();
	public static void insert( HttpSession session) throws ClassNotFoundException
	{
		String txnId="";
		try {
			Connection con= connectDatabase(session);

			String database=(String) session.getAttribute("gateway");
			if(database.equalsIgnoreCase("stripe"))
			{
				txnId="chargeId"; 

			}
			else if(database.equalsIgnoreCase("eway"))
			{
				txnId="transactionId"; 
			}

			/*         create table paymentgateway.eway(id int,
	        		 ref_Id varchar(80),
	        		 app_Id varchar(80),
	        		 amount double,
	        		 datetime timestamp,
	        		 cust_name varchar(30),
	        		 cust_email_Id varchar(50),
	        		 cust_address varchar(100));*/


			String query="insert into "+ database+"(refId, app_ref, amount, dateTime, cust_name, cust_emailId, cust_address)"+"values(?,?,?,?,?,?,?)";
			stmnt=con.prepareStatement(query);

			String refId=(String) session.getAttribute("refId");
			String app_ref=(String) session.getAttribute("app_ref");
			double amount=(int) session.getAttribute("amount");


			/*	if(database.equalsIgnoreCase("stripe"))
			{
				String transactionId=(String) session.getAttribute("transactionId");
				stmnt.setString(3, transactionId);
			}
			else if(database.equalsIgnoreCase("eway"))
			{
				int transactionId=(int) session.getAttribute("transactionId");
				stmnt.setString(3, ""+transactionId);
			}*/


			LocalDateTime now=LocalDateTime.now();
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
			String dateTime = now.format(formatter);

			Customer cust=(Customer) session.getAttribute("customer");

			stmnt.setString(1, refId);
			stmnt.setString(2, app_ref);
			stmnt.setDouble(3, amount);
			stmnt.setString(4, dateTime);
			stmnt.setString(5, cust.getName());
			stmnt.setString(6, cust.getEmailId());
			stmnt.setString(7, cust.getAddress());
			stmnt.execute();
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}  
	}

	public static RefundReference retrive(HttpSession session)
	{ 
		RefundReference refResp=new RefundReference();
		String refId="";
		con=connectDatabase(session);
		String transactionId=(String) session.getAttribute("transactionId");
		String database=(String) session.getAttribute("gateway");
		String query="select *from "+database+" where app_ref='"+transactionId+"'";
		try {
			stmnt=con.prepareStatement(query);
			ResultSet res=stmnt.executeQuery(query);
			if(res!=null)
			{
				if(res.next())
				{

					RefundReference.setRef(res.getString("refId"));
					RefundReference.setAmount(res.getDouble("amount"));
					RefundReference.setName(res.getString("cust_name"));
					session.setAttribute("name", res.getString("cust_name"));
					session.setAttribute("refResp", refResp);
				}

			}

		} catch (SQLException e) {
			resp.setDescriptiong(e.getMessage());

		}

		finally
		{
			try {
				stmnt.close();
				con.close();
			} catch (SQLException e) {
			}

		}


		return refResp;
	}

	public static void insertRefund(HttpSession session) {

		con=connectDatabase(session);

		String database=(String) session.getAttribute("gateway");
		String query="insert into "+database+"_refund(ref_app_refId, refund_id, transaction_id, amount, datetime) values(?,?,?,?,?)";

		String ref_app_refId=(String) session.getAttribute("refund_app_refId");
		String transactionId=(String)session.getAttribute("transactionId");
		String refundId=(String)session.getAttribute("refundId");
		double amount=((RefundReference)session.getAttribute("refResp")).getAmount();
		try {
			stmnt=con.prepareStatement(query);

			LocalDateTime now=LocalDateTime.now();
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
			String dateTime = now.format(formatter);


			stmnt.setString(1, ref_app_refId);
			stmnt.setString(2, refundId);
			stmnt.setString(3, transactionId);
			stmnt.setDouble(4, amount);
			stmnt.setString(5, dateTime);
			stmnt.execute();


		} catch (SQLException e) {
			HttpResponse httpResp=new HttpResponse();
			httpResp.setDescription(e.getMessage());
		}

	}
	public static Connection connectDatabase(HttpSession session)
	{
		Properties prop=(Properties)session.getAttribute(Constants.PROPERTIES);
		driver=prop.getProperty("driver");
		url=prop.getProperty("url");
		userName=prop.getProperty("username");
		password=prop.getProperty("password");

		try {
			Class.forName(driver);
			con=DriverManager.getConnection(  
					url,userName,password); 
		} catch (ClassNotFoundException | SQLException e) {
			e.printStackTrace();
		}

		return con;
	}




}
