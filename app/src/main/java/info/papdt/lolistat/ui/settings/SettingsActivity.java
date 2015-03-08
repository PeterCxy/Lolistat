package info.papdt.lolistat.ui.settings;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import info.papdt.lolistat.R;

public class SettingsActivity extends Activity
{
	public static final String INTENT_ABOUT = "info.papdt.lolistat.about";

	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dummy);
		
		Intent i = getIntent();
		
		Class<? extends Fragment> frag = SettingsFragment.class;
		if (i != null) {
			String action = i.getAction();
			if (action.equals(INTENT_ABOUT)) {
				frag = AboutFragment.class;
				getActionBar().setTitle(R.string.about);
			}
		}
		
		if (frag != SettingsFragment.class) {
			getActionBar().setDisplayHomeAsUpEnabled(true);
		}
		
		try {
			getFragmentManager().beginTransaction().replace(R.id.frame, frag.newInstance()).commit();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case android.R.id.home:
				finish();
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}
	
}
