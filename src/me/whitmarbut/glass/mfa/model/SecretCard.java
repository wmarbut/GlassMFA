package me.whitmarbut.glass.mfa.model;

import android.content.Context;

import com.google.android.glass.app.Card;

public class SecretCard extends Card {
	private String secret;
	
	public SecretCard(Context context) {
		super(context);
	}
	
	public void setSecret(String secret) {
		this.secret = secret;
	}
	
	public String getSecret() {
		return this.secret;
	}
	
}
