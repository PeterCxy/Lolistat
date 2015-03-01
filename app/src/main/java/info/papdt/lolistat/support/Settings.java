package info.papdt.lolistat.support;

import android.content.Context;
import android.content.SharedPreferences;

import de.robv.android.xposed.XSharedPreferences;

public class Settings
{
	
	public static final String PREF_TINT_NAVIGATION = "tint_navigation";
	public static final String PREF_TINT_ICONS = "tint_icons";
	
	private static final String PREF = "pref";
	private static SharedPreferences sPref;
	private static Settings sInstance = null;
	
	private SharedPreferences mPref;
	
	public static void init() {
		sPref = new XSharedPreferences("info.papdt.lolistat", PREF);
		((XSharedPreferences) sPref).makeWorldReadable();
	}
	
	public static Settings getInstance(Context context) {
		if (sInstance == null) {
			sInstance = new Settings(context);
		}
		
		return sInstance;
	}
	
	public static boolean getBooleanStatic(String key, boolean def) {
		return sPref.getBoolean(key, def);
	}
	
	private Settings(Context context) {
		mPref = context.getSharedPreferences(PREF, Context.MODE_WORLD_READABLE);
	}
	
	public boolean getBoolean(String key, boolean def) {
		return mPref.getBoolean(key, def);
	}
	
	public void putBoolean(String key, boolean value) {
		mPref.edit().putBoolean(key, value).commit();
	}
	
}
