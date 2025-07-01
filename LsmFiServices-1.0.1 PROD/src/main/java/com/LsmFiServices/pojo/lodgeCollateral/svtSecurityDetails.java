package com.LsmFiServices.pojo.lodgeCollateral;

public class svtSecurityDetails {

	String Sub_Type_Security;
	String Type_Of_Security;
	String Products;
	String Type_Of_Charge;
	String Value_Of_SubTypeSecuirty;
	String Security_value_In_Mn;

	public String getSub_Type_Security() {
		return Sub_Type_Security;
	}

	public void setSub_Type_Security(String sub_Type_Security) {
		Sub_Type_Security = sub_Type_Security;
	}

	public String getType_Of_Security() {
		return Type_Of_Security;
	}

	public void setType_Of_Security(String type_Of_Security) {
		Type_Of_Security = type_Of_Security;
	}

	public String getProducts() {
		return Products;
	}

	public void setProducts(String products) {
		Products = products;
	}

	public String getType_Of_Charge() {
		return Type_Of_Charge;
	}

	public void setType_Of_Charge(String type_Of_Charge) {
		Type_Of_Charge = type_Of_Charge;
	}

	public String getValue_Of_SubTypeSecuirty() {
		return Value_Of_SubTypeSecuirty;
	}

	public void setValue_Of_SubTypeSecuirty(String value_Of_SubTypeSecuirty) {
		Value_Of_SubTypeSecuirty = value_Of_SubTypeSecuirty;
	}

	public String getSecurity_value_In_Mn() {
		return Security_value_In_Mn;
	}

	public void setSecurity_value_In_Mn(String security_value_In_Mn) {
		Security_value_In_Mn = security_value_In_Mn;
	}

	@Override
	public String toString() {
		return "svtSecurityDetails [Sub_Type_Security=" + Sub_Type_Security + ", Type_Of_Security=" + Type_Of_Security
				+ ", Products=" + Products + ", Type_Of_Charge=" + Type_Of_Charge + ", Value_Of_SubTypeSecuirty="
				+ Value_Of_SubTypeSecuirty + ", Security_value_In_Mn=" + Security_value_In_Mn + "]";
	}

}
