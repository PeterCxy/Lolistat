package info.papdt.lolistat.ui.base;

import android.content.Intent;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.widget.Toolbar;

public abstract class BasePreferenceFragment extends PreferenceFragment implements Preference.OnPreferenceClickListener, Preference.OnPreferenceChangeListener
{

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(getPreferenceXml());
		onPreferenceLoaded();
	}

	@Override
	public boolean onPreferenceClick(Preference pref) {
		return false;
	}

	@Override
	public boolean onPreferenceChange(Preference preference, Object newValue) {
		return false;
	}
	
	protected GlobalActivity getGlobalActivity() {
		return (GlobalActivity) getActivity();
	}

	protected Toolbar getToolbar() {
		return getGlobalActivity().getToolbar();
	}

	protected String getExtraPass() {
		return getGlobalActivity().getExtraPass();
	}

	protected void setTitle(String title) {
		getGlobalActivity().getActionBar().setTitle(title);
	}

	protected void showHomeAsUp() {
		getGlobalActivity().getActionBar().setDisplayHomeAsUpEnabled(true);
	}
	
	protected void $$(Preference... preferences) {
		for (Preference preference : preferences) {
			preference.setOnPreferenceClickListener(this);
			preference.setOnPreferenceChangeListener(this);
		}
	}
	
	protected void startFragment(String name) {
		Intent i = new Intent();
		i.setAction(Intent.ACTION_MAIN);
		i.setClass(getActivity(), GlobalActivity.class);
		i.putExtra("fragment", name);
		startActivity(i);
	}

	protected void startFragment(String name, String pass) {
		Intent i = new Intent();
		i.setAction(Intent.ACTION_MAIN);
		i.setClass(getActivity(), GlobalActivity.class);
		i.putExtra("fragment", name);
		i.putExtra("pass", pass);
		startActivity(i);
	}
	
	protected abstract int getPreferenceXml();
	protected abstract void onPreferenceLoaded();
}
