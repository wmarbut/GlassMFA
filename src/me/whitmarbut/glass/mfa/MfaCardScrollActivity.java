package me.whitmarbut.glass.mfa;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import me.whitmarbut.glass.mfa.model.MFASecret;
import me.whitmarbut.glass.mfa.model.SecretCard;
import me.whitmarbut.glass.mfa.util.MFASecretsProvider;
import me.whitmarbut.mfa.TOTP;

import com.google.android.glass.touchpad.Gesture;
import com.google.android.glass.touchpad.GestureDetector;
import com.google.android.glass.widget.CardScrollAdapter;
import com.google.android.glass.widget.CardScrollView;
import com.google.android.glass.app.Card;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

public class MfaCardScrollActivity extends Activity {
	private List<Card> cards;
	private CardScrollView cardScrollView;
	private MFASecretsProvider secretProvider;
	private Timer updateTimer;
	private GestureDetector gestureDetector;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		secretProvider = new MFASecretsProvider(this);
		
		buildCards();
		
		
		cardScrollView = new CardScrollView(this);
		MFACardScrollAdapter adapter = new MFACardScrollAdapter();
		cardScrollView.setAdapter(adapter);
		
		gestureDetector = buildGestureDetector();
		
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
			card.setSecret(secret.getSecret());
			card.setText(secret.getLabel());
			cards.add(card);
		}
		
		updateCards();
	}
	
	private void updateCards() {
		TOTP totp = new TOTP();
		int card_len = cards.size();
		for (int i = 0; i < card_len; i++) {
			SecretCard card = (SecretCard) cards.get(i);
			card.setText(totp.getToken(card.getSecret()));
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
	
	private GestureDetector buildGestureDetector() {
		
		GestureDetector detector = new GestureDetector(this);
		detector.setBaseListener(new GestureDetector.BaseListener() {
			
			@Override
			public boolean onGesture(Gesture gesture) {
				Log.i("GlassMFA", "Received gesture: " + gesture.toString());
				if (gesture  == Gesture.TAP || gesture == Gesture.LONG_PRESS) {
					Intent menu_intent = new Intent(MfaCardScrollActivity.this, MFAMenuActivity.class);
					menu_intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
					Log.i("GlassMFA", "Starting MFA menu intent");
					
					startActivity(menu_intent);
					return true;
				}
				return false;
			}
		});
		
		return detector;
	}
	
	@Override
    public boolean onGenericMotionEvent(MotionEvent event) {
        if (gestureDetector != null) {
            return gestureDetector.onMotionEvent(event);
        }
        return false;
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
