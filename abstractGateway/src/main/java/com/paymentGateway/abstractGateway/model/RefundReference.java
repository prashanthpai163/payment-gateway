package com.paymentGateway.abstractGateway.model;

public class RefundReference {

	public static String name;
	public static double amount ;
	public static String ref;
	public static String getName() {
		return name;
	}
	public static void setName(String name) {
		RefundReference.name = name;
	}
	public static double getAmount() {
		return amount;
	}
	public static void setAmount(double amount) {
		RefundReference.amount = amount;
	}
	public static String getRef() {
		return ref;
	}
	public static void setRef(String app_ref) {
		RefundReference.ref = app_ref;
	}
	
	
}
