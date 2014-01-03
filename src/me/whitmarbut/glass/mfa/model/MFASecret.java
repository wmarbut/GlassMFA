package me.whitmarbut.glass.mfa.model;
/**
 * @copyright 2014 Whit Marbut
 * Licensed under Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International.
 * Full text of the license may be found here http://creativecommons.org/licenses/by-nc-sa/4.0/deed.en_US
 */
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
