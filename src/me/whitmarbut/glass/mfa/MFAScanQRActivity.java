package me.whitmarbut.glass.mfa;

import java.net.URI;

import me.whitmarbut.glass.mfa.model.MFASecret;
import me.whitmarbut.glass.mfa.util.MFASecretsProvider;

import com.google.android.glass.app.Card;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

public class MFAScanQRActivity extends Activity {
	Card scan_card;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Intent intent = new Intent("com.google.zxing.client.android.SCAN");
		intent.putExtra("SCAN_MODE", "QR_CODE_MODE");
		startActivityForResult(intent, 0);
		
		scan_card = new Card(this);
		scan_card.setText("");
		scan_card.setFootnote("Preparing to scan");
		setContentView(scan_card.toView());
		
	}
	
	public void onActivityResult(int requestCode, int resultCode, Intent intent) {
		if (requestCode == 0) {
			if (resultCode == RESULT_OK) {
				String contents = intent.getStringExtra("SCAN_RESULT");
				String format = intent.getStringExtra("SCAN_RESULT_FORMAT");
				// Handle successful scan
				scan_card = new Card(this);
				scan_card.setText(contents);
				scan_card.setFootnote("Scanned text");
				
				MFASecret secret = this.getSecret(contents);
				if (secret != null) {
					scan_card.setText("Label: " + secret.getLabel());
					MFASecretsProvider provider = new MFASecretsProvider(this);
					provider.addSecret(secret);
				}
				
				this.setContentView(scan_card.toView());
				Log.i("QRCode", "Scanned QR Code: " + contents);
			} else if (resultCode == RESULT_CANCELED) {
				// Handle cancel
			}
		}
	}
	
	protected MFASecret getSecret(String qr_string) {
		MFASecret secret;
		try {
			URI uri = new URI(qr_string);
			String label = uri.getPath();
			String secret_str = uri.getQuery();
			
			label = label.substring(1);
			secret_str = secret_str.replaceFirst("secret=", "");
			
			secret = new MFASecret(label, secret_str);
		} catch (Exception ex) {
			return null;
		}
		return secret;
	}
}
