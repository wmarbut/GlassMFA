package me.whitmarbut.glass.mfa;

/**
 * @copyright 2014 Whit Marbut
 * Licensed under Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International.
 * Full text of the license may be found here http://creativecommons.org/licenses/by-nc-sa/4.0/deed.en_US
 */

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import me.whitmarbut.glass.mfa.model.MFASecret;
import me.whitmarbut.glass.mfa.model.SecretCard;
import me.whitmarbut.glass.mfa.util.MFASecretsProvider;
import me.whitmarbut.mfa.TOTP;

import com.example.glassmfa.R;
import com.google.android.glass.touchpad.Gesture;
import com.google.android.glass.touchpad.GestureDetector;
import com.google.android.glass.widget.CardScrollAdapter;
import com.google.android.glass.widget.CardScrollView;
import com.google.android.glass.app.Card;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;

public class MfaCardScrollActivity extends Activity {
	private List<Card> cards;
	private CardScrollView cardScrollView;
	private MFASecretsProvider secretProvider;
	private Timer updateTimer;
	private SecretCard current_card;
	private int current_card_id;
	private static final String DEFAULT_CARD_TEXT = "Please use the Scan a QR code function in the menu.";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		secretProvider = new MFASecretsProvider(this);
		
		buildCards();
		
		
		cardScrollView = new CardScrollView(this);
		cardScrollView.setOnItemClickListener(new MFACardScrollResponder());
		MFACardScrollAdapter adapter = new MFACardScrollAdapter();
		cardScrollView.setAdapter(adapter);
		
		cardScrollView.activate();
		setContentView(cardScrollView);
		setupTimer();
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		System.out.println("Cancelling timer");
		updateTimer.cancel();
		updateTimer = null;
	}
	
	private void buildCards() {
		cards = new ArrayList<Card>();
		MFASecret[] secrets = secretProvider.getSecrets();
		
		SecretCard card;
		
		//Intent menuIntent = new Intent(this, CompassMenuActivity.class);
        //menuIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        //mLiveCard.setAction(PendingIntent.getActivity(this, 0, menuIntent, 0));
		
		for (MFASecret secret : secrets) {
			card = new SecretCard(this);
			card.setFootnote(secret.getLabel());
			card.setSecret(secret);
			card.setText(secret.getLabel());
			cards.add(card);
		}
		handleNoCards();
		
		updateCards();
	}
	
	private void handleNoCards() {
		if (cards.size() > 1) {
			int card_size = cards.size();
			for (int i = 0; i < card_size; i++) {
				if (cards.get(i).getText() == DEFAULT_CARD_TEXT) {
					cards.remove(i);
					cardScrollView.updateViews(true);
				}
			}
		} else if (cards.size() == 0) {
			Card default_card = new Card(this);
			default_card.setText(DEFAULT_CARD_TEXT);
			default_card.setFootnote("Contribute code or bug reports on github.com/grep-awesome/GlassMFA");
			cards.add(default_card);
			
			if (cardScrollView != null) {
				cardScrollView.updateViews(true);
			}
		}
	}
	
	public SecretCard getSelectedCard() {
		if (cardScrollView != null) {
			return (SecretCard) cardScrollView.getSelectedItem();
		}
		return null;
	}
	
	private void updateCards() {
		TOTP totp = new TOTP();
		int card_len = cards.size();
		for (int i = 0; i < card_len; i++) {
			if (cards.get(i) instanceof SecretCard) {
				SecretCard card = (SecretCard) cards.get(i);
				card.setText(totp.getToken(card.getSecret()));
			}
		}
		
	}
	
	private void setupTimer() {
		if (updateTimer != null) {
			updateTimer.cancel();
		}
		updateTimer = new Timer();
		TimerTask task = new UpdateCardTimerTask();
		
		
		long seconds = (System.currentTimeMillis()/1000);
		long next = seconds - (seconds%30);
		Date next_date = new Date(next*1000);
		long period = 30*1000;
		
		System.out.println("Scheduling Timer for: " + next_date.toString() + " e.g. " + ((Long) next).toString());
		
		updateTimer.scheduleAtFixedRate(task, next_date, period);
	}
	
	
	private class UpdateCardTimerTask extends TimerTask {

		@Override
		public void run() {
			System.out.println("Timer called to update cards");
			 runOnUiThread(new Runnable() {              
	                @Override
	                public void run() {
	                		updateCards();
	                		cardScrollView.updateViews(true);
	                }
			 });
		}
		
	}
	
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
    	getMenuInflater().inflate(R.menu.mfa_menu, menu);
        
        return true;
    }
	
	protected void deleteSecret() {
    	MFASecretsProvider provider = new MFASecretsProvider(this);
   		if (current_card != null) {
   			if (current_card instanceof SecretCard) {
   				secretProvider.deleteSecret( current_card.getSecretObject() );
   				cards.remove(current_card_id);
   				cardScrollView.updateViews(true);
   				handleNoCards();
   			}
   		} else {
   			Log.e("GlassMFA", "Unable to delete current card, it was null");
   		}
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.scan_qr_code:
            	Intent qr_intent = new Intent(this, MFAScanQRActivity.class);
            	startActivity(qr_intent);
            	return true;
            case R.id.delete_secret:
            	this.deleteSecret();
            	return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    
    private class MFACardScrollResponder implements OnItemClickListener, OnItemLongClickListener{
		
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			Log.i("GlassMFA", "Item click received. Should open menu");
			if (cards.get(position) instanceof SecretCard) {
				MfaCardScrollActivity.this.current_card = (SecretCard) cards.get(position);
				MfaCardScrollActivity.this.current_card_id = position;
			}
			MfaCardScrollActivity.this.openOptionsMenu();
			
		}

		@Override
		public boolean onItemLongClick(AdapterView<?> parent, View view,
				int position, long id) {
			onItemClick(parent, view, position, id);
			return true;
		}
    	
    }
	
	private class MFACardScrollAdapter extends CardScrollAdapter {

		@Override
	    public int findIdPosition(Object id) {
	        return -1;
	    }

	    @Override
	    public int findItemPosition(Object item) {
	        return cards.indexOf(item);
	    }

	    @Override
	    public int getCount() {
	        return cards.size();
	    }

	    @Override
	    public Object getItem(int position) {
	        return cards.get(position);
	    }

	    @Override
	    public View getView(int position, View convertView, ViewGroup parent) {
	        return cards.get(position).toView();
	    }

		
		
	}
}
