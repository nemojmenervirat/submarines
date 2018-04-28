package com.nemojmenervirat.submarines.logic;

import java.io.Serializable;
import java.util.Date;

public class User implements Serializable {

	private String username;
	private String address;
	private Date loginDate;

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public Date getLoginDate() {
		return loginDate;
	}

	public void setLoginDate(Date loginDate) {
		this.loginDate = loginDate;
	}

	@Override
	public String toString() {
		return username;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof User) {
			return username.equals(((User) obj).getUsername());
		}
		return super.equals(obj);
	}

}
