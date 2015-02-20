package info.papdt.lolistat.mod;

import android.app.Activity;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XC_MethodReplacement;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

import java.lang.reflect.Field;

import info.papdt.lolistat.support.Utility;
import static info.papdt.lolistat.BuildConfig.DEBUG;

public class ModLoli implements IXposedHookLoadPackage
{
	private static final String TAG = ModLoli.class.getSimpleName() + ":";

	@Override
	public void handleLoadPackage(final XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {
		if (lpparam.packageName.startsWith("com.android")) return;
		
		if (DEBUG) {
			XposedBridge.log(TAG + "Loaded package " + lpparam.packageName);
		}
		
		final Class<?> internalStyleable = XposedHelpers.findClass("com.android.internal.R.styleable", lpparam.classLoader);
		final Field internalThemeField = XposedHelpers.findField(internalStyleable, "Theme");
		final Field internalColorPrimaryDarkField = XposedHelpers.findField(internalStyleable, "Theme_colorPrimaryDark");
		final int[] theme = (int[]) internalThemeField.get(null);
		final int theme_colorPrimaryDark = internalColorPrimaryDarkField.getInt(null);
		
		XposedHelpers.findAndHookMethod(Activity.class, "onPostCreate", Bundle.class, new XC_MethodHook() {
			@Override
			protected void afterHookedMethod(XC_MethodHook.MethodHookParam mhparams) throws Throwable {
				Activity activity = (Activity) mhparams.thisObject;
				
				// Ignore if launcher
				if (Utility.isLauncher(activity, lpparam.packageName)) return;
				
				// Ignore if have defined colorPrimaryDark already
				TypedArray a = activity.getTheme().obtainStyledAttributes(theme);
				int colorPrimaryDark = a.getColor(theme_colorPrimaryDark, 0x00000000);
				a.recycle();
				
				if (colorPrimaryDark != 0x00000000 && colorPrimaryDark != 0xff000000) return;
				
				Window window = activity.getWindow();
				View decor = window.getDecorView();
				
				window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
				
				XposedHelpers.setAdditionalInstanceField(decor, "isDecor", true);
				XposedHelpers.setAdditionalInstanceField(decor, "window", window);
			}
		});
		
		XposedHelpers.findAndHookMethod(View.class, "draw", Canvas.class, new XC_MethodHook() {
			@Override
			protected void beforeHookedMethod(XC_MethodHook.MethodHookParam mhparams) throws Throwable {
				Boolean isDecor = (Boolean) XposedHelpers.getAdditionalInstanceField(mhparams.thisObject, "isDecor");
				if (isDecor != null && isDecor) {
					Canvas canvas = (Canvas) mhparams.args[0];
					
					Bitmap bitmap = (Bitmap) XposedHelpers.getAdditionalInstanceField(mhparams.thisObject, "newBitmap");
					Canvas newCanvas = (Canvas) XposedHelpers.getAdditionalInstanceField(mhparams.thisObject, "newCanvas");
					if (bitmap == null) {
						bitmap = Bitmap.createBitmap(canvas.getWidth(), canvas.getHeight(), Bitmap.Config.ARGB_8888);
						newCanvas = new Canvas(bitmap);
					} else {
						bitmap.eraseColor(0);
					}
					
					mhparams.args[0] = newCanvas;
					XposedHelpers.setAdditionalInstanceField(mhparams.thisObject, "oldCanvas", canvas);
					XposedHelpers.setAdditionalInstanceField(mhparams.thisObject, "newCanvas", newCanvas);
					XposedHelpers.setAdditionalInstanceField(mhparams.thisObject, "newBitmap", bitmap);
				}
			}
			
			@Override
			protected void afterHookedMethod(XC_MethodHook.MethodHookParam mhparams) throws Throwable {
				Boolean isDecor = (Boolean) XposedHelpers.getAdditionalInstanceField(mhparams.thisObject, "isDecor");
				if (isDecor != null && isDecor) {
					Window window = (Window) XposedHelpers.getAdditionalInstanceField(mhparams.thisObject, "window");
					Canvas oldCanvas = (Canvas) XposedHelpers.getAdditionalInstanceField(mhparams.thisObject, "oldCanvas");
					Bitmap newBitmap = (Bitmap) XposedHelpers.getAdditionalInstanceField(mhparams.thisObject, "newBitmap");
					Paint p = new Paint();
					oldCanvas.drawBitmap(newBitmap, 0, 0, p);
					mhparams.args[0] = oldCanvas;
					
					View v = (View) mhparams.thisObject;
					
					int statHeight = Utility.getStatusBarHeight(v.getContext()) + 1;
					int width = v.getWidth();
					int color1 = newBitmap.getPixel(width / 2, statHeight);
					int color2 = newBitmap.getPixel(1, statHeight);
					int color3 = newBitmap.getPixel(width - 1, statHeight);
					int color = Utility.colorAverage(color1, color2, color3);
					
					window.setStatusBarColor(Utility.darkenColor(color, 0.85f));
					
					XposedHelpers.setAdditionalInstanceField(mhparams.thisObject, "isDecor", false);
				}
			}
		});
	}

}
