package com.paymentGateway.abstractGateway.model;

public class Customer {

	public static String name;
	public static String emailId;
	public static String address;
	public static String getName() {
		return name;
	}
	public static void setName(String name) {
		Customer.name = name;
	}
	public static String getEmailId() {
		return emailId;
	}
	public static void setEmailId(String emailId) {
		Customer.emailId = emailId;
	}
	public static String getAddress() {
		return address;
	}
	public static void setAddress(String address) {
		Customer.address = address;
	}

}
