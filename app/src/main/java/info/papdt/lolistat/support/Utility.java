package info.papdt.lolistat.support;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;

public class Utility
{
	public static int getStatusBarHeight(Context context) { 
		int result = 0;
		int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
		if (resourceId > 0) {
			result = context.getResources().getDimensionPixelSize(resourceId);
		}
		return result;
	}
	
	public static int darkenColor(int color, float factor) {
		float[] hsv = new float[3];
		Color.colorToHSV(color, hsv);
		hsv[2] *= factor;
		return Color.HSVToColor(hsv);
	}
	
	public static int removeAlpha(int color) {
		int r = Color.red(color);
		int g = Color.green(color);
		int b = Color.blue(color);
		return Color.rgb(r, g, b);
	}
	
	public static int addAlpha(int color, float alpha) {
		int r = Color.red(color);
		int g = Color.green(color);
		int b = Color.blue(color);
		int a = (int) (alpha * 255);
		return Color.argb(a, r, g, b);
	}
	
	public static int colorAverage(int... colors) {
		int r = 0;
		int g = 0;
		int b = 0;
		
		for (int color : colors) {
			r += Color.red(color);
			g += Color.green(color);
			b += Color.blue(color);
		}
		
		return Color.rgb(r / colors.length, g / colors.length, b / colors.length);
	}
	
	public static String colorToDebugString(int color) {
		int r = Color.red(color);
		int g = Color.green(color);
		int b = Color.blue(color);
		return String.format("{%d,%d,%d}", r, g, b);
	}
	
	public static boolean isLauncher(Context context, String packageName) {
		ActivityInfo homeInfo = new Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_HOME).resolveActivityInfo(context.getPackageManager(), 0);
		if (homeInfo != null) {
			return homeInfo.packageName.equals(packageName);
		} else {
			return false;
		}
	}
}
