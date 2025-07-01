package com.LsmFiServices.pojo.lodgeCollateral;

public class policySecurityDetails {

	String Policy_No;
	String Policy_Amount;

	public String getPolicy_No() {
		return Policy_No;
	}

	public void setPolicy_No(String policy_No) {
		Policy_No = policy_No;
	}

	public String getPolicy_Amount() {
		return Policy_Amount;
	}

	public void setPolicy_Amount(String policy_Amount) {
		Policy_Amount = policy_Amount;
	}

	@Override
	public String toString() {
		return "policySecurityDetails [Policy_No=" + Policy_No + ", Policy_Amount=" + Policy_Amount + "]";
	}
}
