package me.whitmarbut.glass.mfa.model;
/**
 * @copyright 2014 Whit Marbut
 * Licensed under Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International.
 * Full text of the license may be found here http://creativecommons.org/licenses/by-nc-sa/4.0/deed.en_US
 */

import android.content.Context;

import com.google.android.glass.app.Card;

public class SecretCard extends Card {
	private MFASecret secret;
	
	public SecretCard(Context context) {
		super(context);
	}
	
	public void setSecret(MFASecret secret) {
		this.secret = secret;
	}
	
	public String getSecret() {
		return this.secret.getSecret();
	}
	
	public MFASecret getSecretObject() {
		return this.secret;
	}
	
}
