package com.vtg.app.model;

public class ModelDebit {
	public String charge = "0";
	public String credit = "0";
	public String debitStart = "0";
	public String payment = "0";

	public ModelDebit(String charge, String debitStart, String payment,
			String credit) {
		this.charge = charge;
		this.debitStart = debitStart;
		this.payment = payment;
		this.credit = credit;
	}

	public ModelDebit() {

	}
}
