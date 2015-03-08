package info.papdt.lolistat.ui.settings;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceScreen;

import info.papdt.lolistat.R;

public class AboutFragment extends PreferenceFragment
{
	private static final String DONATION_URI = "https://www.paypal.com/cgi-bin/webscr?cmd=_xclick&business=xqsx43cxy@126.com&lc=US&item_name=Lolistat&no_note=1&no_shipping=1&currency_code=USD";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.about);
		
		try {
			String version = getActivity().getPackageManager().getPackageInfo(getActivity().getPackageName(), 0).versionName;
			findPreference("version").setSummary(version);
		} catch (Exception e) {
			
		}
	}

	@Override
	public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
		Intent i = new Intent();
		switch (preference.getKey()) {
			case "source_code":
				i.setAction(Intent.ACTION_VIEW);
				i.setData(Uri.parse(preference.getSummary().toString()));
				break;
			case "donation":
				i.setAction(Intent.ACTION_VIEW);
				i.setData(Uri.parse(DONATION_URI));
				break;
			default:
				return super.onPreferenceTreeClick(preferenceScreen, preference);
		}
		startActivity(i);
		return true;
	}
}
