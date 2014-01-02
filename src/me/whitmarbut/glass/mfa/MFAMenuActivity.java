package me.whitmarbut.glass.mfa;

import com.example.glassmfa.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

public class MFAMenuActivity extends Activity {
	boolean has_resumed = false;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        openOptionsMenu();
       
	}
	
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
    	getMenuInflater().inflate(R.menu.mfa_menu, menu);
        
        return true;
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        has_resumed = true;
        openOptionsMenu();
    }
    
    @Override
    protected void onPause() {
        super.onPause();
        has_resumed = false;
    }
    
    @Override
    public void openOptionsMenu() {
        if (has_resumed) {
            super.openOptionsMenu();
        }
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.scan_qr_code:
            	Intent qr_intent = new Intent(this, MFAScanQRActivity.class);
            	startActivity(qr_intent);
            	return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
