package info.papdt.lolistat.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import info.papdt.lolistat.R;
import info.papdt.lolistat.model.AppModel;

public class AppAdapter extends BaseAdapter
{
	private List<AppModel> mList;
	private LayoutInflater mInflater;
	
	public AppAdapter(Context context, List<AppModel> list) {
		mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mList = list;
	}
	
	public List<AppModel> getList() {
		return mList;
	}

	@Override
	public int getCount() {
		return mList.size();
	}

	@Override
	public Object getItem(int pos) {
		return mList.get(pos);
	}

	@Override
	public long getItemId(int pos) {
		return pos;
	}

	@Override
	public View getView(int pos, View convertView, ViewGroup parent) {
		if (pos >= getCount())
			return null;
		
		View v = convertView;
		if (v == null) {
			v = mInflater.inflate(R.layout.list_item, parent, false);
		}
		
		final AppModel app = mList.get(pos);
		
		ImageView icon = (ImageView) v.findViewById(R.id.icon);
		icon.setImageDrawable(app.icon);
		
		TextView text = (TextView) v.findViewById(R.id.name);
		text.setText(app.title);
		
		final CheckBox check = (CheckBox) v.findViewById(R.id.check);
		check.setOnCheckedChangeListener(null);
		v.setOnClickListener(null);
		check.setChecked(app.checked);
		
		check.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton button, boolean checked) {
				app.checked = checked;
			}
		});
		
		v.setClickable(true);
		v.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				check.setChecked(!check.isChecked());
			}
		});
		
		return v;
	}

}
