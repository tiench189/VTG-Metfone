package com.vtg.app.model;

public class ModelBalance {
	public String expire;
	public String id;
	public String name;
	public String value;
	public float val;

	public ModelBalance(String expire, String id, String name, String value) {
		this.expire = "";
		this.id = "";
		this.name = "";
		this.value = "";
		this.expire = expire;
		this.id = id;
		this.name = name;
		this.value = value;
	}

	public ModelBalance() {
		this.expire = "";
		this.id = "";
		this.name = "";
		this.value = "";
		this.val = 0;
	}
}
