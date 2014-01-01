package me.whitmarbut.glass.mfa.util;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import me.whitmarbut.glass.mfa.model.MFASecret;

public class MFASecretsProvider extends SQLiteOpenHelper {
	
    // Database Version
    private static final int DATABASE_VERSION = 1;
 
    // Database Name
    private static final String DATABASE_NAME = "secrets_manager";
    private static final String TABLE_NAME = "secrets";
 
	
	public MFASecretsProvider(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	public MFASecret[] getSecrets() {
		String query = "SELECT * FROM " + TABLE_NAME;
		SQLiteDatabase db = this.getReadableDatabase();
		List<MFASecret> secrets = new ArrayList<MFASecret>();
		
		Cursor cursor = db.rawQuery(query, null);
		if (cursor.moveToFirst()) {
			do {
				secrets.add(new MFASecret(cursor.getString(0), cursor.getString(1)));
			} while (cursor.moveToNext());
		}
		
		return secrets.toArray(new MFASecret[secrets.size()]);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		String create_table = "CREATE TABLE secrets (label VARCHAR, secret VARCHAR)";
		db.execSQL(create_table);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		
	}
	
	public void addSecret(MFASecret secret) {
		SQLiteDatabase db = this.getReadableDatabase();
		String update = "INSERT INTO secrets (label, secret) VALUES (?, ?)";
		db.execSQL(update, new String[]{ secret.getLabel(), secret.getSecret()}); 
	}
	
	public void deleteSecret(MFASecret secret) {
		SQLiteDatabase db = this.getReadableDatabase();
		String delete = "DELETE FROM secrets WHERE label = ? AND secret = ?";
		db.execSQL(delete, new String[]{ secret.getLabel(), secret.getSecret()});
	}
	

}
