package info.papdt.lolistat.ui.app;

import android.content.Intent;
import android.net.Uri;
import android.preference.Preference;

import info.papdt.lolistat.R;
import info.papdt.lolistat.ui.base.BasePreferenceFragment;
import static info.papdt.lolistat.ui.utils.UiUtility.*;

public class AboutFragment extends BasePreferenceFragment
{
	private static final String DONATION_URI = "https://www.paypal.com/cgi-bin/webscr?cmd=_xclick&business=xqsx43cxy@126.com&lc=US&item_name=SwipeBack+Donation&no_note=1&no_shipping=1&currency_code=USD";
	
	private static final String VERSION = "version";
	private static final String SOURCE_CODE = "source_code";
	private static final String LICENSE = "license";
	private static final String DONATION = "donation";

	private Preference mVersion, mSourceCode, mLicense, mDonation;

	@Override
	protected void onPreferenceLoaded() {
		setTitle(getString(R.string.about));
		showHomeAsUp();
		
		// Obtain preferences
		mVersion = $(this, VERSION);
		mSourceCode = $(this, SOURCE_CODE);
		mLicense = $(this, LICENSE);
		mDonation = $(this, DONATION);

		// TODO: Make donations available
		getPreferenceScreen().removePreference(mDonation);
		
		// Set values
		String ver;
		try {
			ver = getActivity().getPackageManager().getPackageInfo("info.papdt.swipeback", 0).versionName;
		} catch (Exception e) {
			ver = "?";
		}
		mVersion.setSummary(ver);
		
		$$(mVersion, mSourceCode, mLicense, mDonation);
	}

	@Override
	protected int getPreferenceXml() {
		return R.xml.about;
	}

	@Override
	public boolean onPreferenceClick(Preference pref) {
		if (pref == mSourceCode) {
			Intent i = new Intent(Intent.ACTION_VIEW);
			i.setData(Uri.parse("https://github.com/PaperAirplane-Dev-Team/SwipeBack"));
			startActivity(i);
			return true;
		} else if (pref == mLicense) {
			startFragment("license");
			return true;
		} else if (pref == mDonation) {
			Intent i = new Intent(Intent.ACTION_VIEW);
			i.setData(Uri.parse(DONATION_URI));
			startActivity(i);
			return true;
		} else {
			return super.onPreferenceClick(pref);
		}
	}
}
