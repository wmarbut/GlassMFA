package me.whitmarbut.glass.mfa.model;

public class MFASecret {
	private String label;
	private String secret;
	
	public MFASecret(String label, String secret) {
		this.label = label;
		this.secret = secret;
	}
	
	public String getLabel() {
		return this.label;
	}
	
	public String getSecret() {
		return this.secret;
	}
}
