package info.papdt.lolistat.ui.settings;

import android.app.Activity;
import android.os.Bundle;

import info.papdt.lolistat.R;

public class SettingsActivity extends Activity
{

	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dummy);
		getFragmentManager().beginTransaction().replace(R.id.frame, new SettingsFragment()).commit();
	}
	
}
