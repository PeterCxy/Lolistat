package info.papdt.lolistat.ui.app;

import android.view.View;
import android.webkit.WebView;

import info.papdt.lolistat.R;
import info.papdt.lolistat.ui.base.BaseFragment;
import static info.papdt.lolistat.ui.utils.UiUtility.*;

public class LicenseFragment extends BaseFragment
{

	@Override
	protected int getLayoutId() {
		return R.layout.webview;
	}

	@Override
	protected void onFinishInflate(View view) {
		showHomeAsUp();
		setTitle(getString(R.string.license));
		WebView w = $(view, R.id.web);
		w.loadUrl("file:///android_asset/licenses.html");
	}

}
