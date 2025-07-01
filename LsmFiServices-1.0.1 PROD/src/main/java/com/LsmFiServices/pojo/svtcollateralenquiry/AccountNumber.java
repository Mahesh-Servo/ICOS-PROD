package com.LsmFiServices.pojo.svtcollateralenquiry;

public class AccountNumber {

	private String accountNumber;

	public AccountNumber(String accountNumber) {
		this.accountNumber = accountNumber;
	}

	public String getAccountNumber() {
		return accountNumber;
	}

	@Override
	public String toString() {
		return "AccountNumber [accountNumber=" + accountNumber + "]";
	}
}
