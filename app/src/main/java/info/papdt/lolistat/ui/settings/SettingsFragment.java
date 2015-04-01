package info.papdt.lolistat.ui.settings;

import android.content.Intent;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceScreen;

import info.papdt.lolistat.R;
import info.papdt.lolistat.support.Settings;

public class SettingsFragment extends PreferenceFragment implements Preference.OnPreferenceChangeListener
{

	private Settings mSettings;

	private CheckBoxPreference mTintNav;
	private CheckBoxPreference mTintIcons;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.pref);

		mSettings = Settings.getInstance(getActivity());

		mTintNav = (CheckBoxPreference) findPreference(Settings.PREF_TINT_NAVIGATION);
		mTintNav.setChecked(mSettings.getBoolean(Settings.PREF_TINT_NAVIGATION, true));
		mTintNav.setOnPreferenceChangeListener(this);

		mTintIcons = (CheckBoxPreference) findPreference(Settings.PREF_TINT_ICONS);
		mTintIcons.setChecked(mSettings.getBoolean(Settings.PREF_TINT_ICONS, true));
		mTintIcons.setOnPreferenceChangeListener(this);
	}

	@Override
	public boolean onPreferenceChange(Preference pref, Object newValue) {
		if (pref == mTintNav) {
			mSettings.putBoolean(Settings.PREF_TINT_NAVIGATION, Boolean.valueOf(newValue.toString()));
		} else if (pref == mTintIcons) {
			mSettings.putBoolean(Settings.PREF_TINT_ICONS, Boolean.valueOf(newValue.toString()));
		}
		return true;
	}

	@Override
	public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
		String key = preference.getKey();
		Intent i = new Intent();
		switch (key) {
			case "blacklist":
				i.setAction(Intent.ACTION_MAIN);
				i.setClass(getActivity(), BlackListActivity.class);
				break;
			case "about":
				i.setAction(SettingsActivity.INTENT_ABOUT);
				i.setClass(getActivity(), SettingsActivity.class);
				break;
			default:
				return super.onPreferenceTreeClick(preferenceScreen, preference);
		}

		startActivity(i);
		return true;
	}

}
