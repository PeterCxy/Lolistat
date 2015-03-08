package info.papdt.lolistat.ui.settings;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.text.Collator;

import info.papdt.lolistat.R;
import info.papdt.lolistat.model.AppModel;
import info.papdt.lolistat.support.Settings;
import info.papdt.lolistat.ui.adapter.AppAdapter;

public class BlackListActivity extends Activity
{
	private ListView mList;
	private Settings mSettings;
	private AppAdapter mAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.list);
		getActionBar().setDisplayHomeAsUpEnabled(true);
		
		mSettings = Settings.getInstance(this);
		
		mList = (ListView) findViewById(R.id.list);
		mList.setFastScrollEnabled(true);
		
		new LoadAppTask().execute();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.blacklist, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case android.R.id.home:
				finish();
				return true;
			case R.id.save:
				new SaveTask().execute();
				return true;
		}
		
		return false;
	}
	
	private class LoadAppTask extends AsyncTask<Void, Void, List<AppModel>> {
		private ProgressDialog prog;
		
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			prog = new ProgressDialog(BlackListActivity.this);
			prog.setMessage(getString(R.string.plz_wait));
			prog.setCancelable(false);
			prog.show();
		}

		@Override
		protected List<AppModel> doInBackground(Void... params) {
			PackageManager pm = getPackageManager();
			List<ApplicationInfo> appInfos = pm.getInstalledApplications(pm.GET_META_DATA);
			ArrayList<AppModel> apps = new ArrayList<AppModel>();
			
			for (ApplicationInfo appInfo : appInfos) {
				AppModel app = new AppModel();
				app.title = pm.getApplicationLabel(appInfo).toString();
				app.icon = pm.getApplicationIcon(appInfo);
				app.packageName = appInfo.packageName;
				app.checked = mSettings.getBoolean(app.packageName, false);
				app.orig = app.checked;
				apps.add(app);
			}
			
			Collections.sort(apps, new Comparator<AppModel>() {
				@Override
				public int compare(AppModel p1, AppModel p2) {
					return Collator.getInstance().compare(p1.title, p2.title);
				}
			});
			
			return apps;
		}

		@Override
		protected void onPostExecute(List<AppModel> result) {
			super.onPostExecute(result);
			prog.cancel();
			
			mAdapter = new AppAdapter(BlackListActivity.this, result);
			mList.setAdapter(mAdapter);
		}
		
	}
	
	private class SaveTask extends AsyncTask<Void, Void, Void> {
		private ProgressDialog prog;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			prog = new ProgressDialog(BlackListActivity.this);
			prog.setMessage(getString(R.string.plz_wait));
			prog.setCancelable(false);
			prog.show();
		}

		@Override
		protected Void doInBackground(Void... params) {
			List<AppModel> list = mAdapter.getList();
			
			for (AppModel app : list) {
				if (app.checked != app.orig) {
					// State changed
					mSettings.putBoolean(app.packageName, app.checked);
					app.orig = app.checked;
				}
			}
			
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			prog.cancel();
		}
	}
}
