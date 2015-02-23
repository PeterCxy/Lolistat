package info.papdt.lolistat.mod;

import android.app.Activity;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XC_MethodReplacement;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import info.papdt.lolistat.support.Utility;
import static info.papdt.lolistat.BuildConfig.DEBUG;

public class ModLoli implements IXposedHookLoadPackage
{
	private static final String TAG = ModLoli.class.getSimpleName() + ":";
	private static final long MIN_BREAK = 2000;
	private static int STATUS_HEIGHT = 0;

	@Override
	public void handleLoadPackage(final XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {
		if (lpparam.packageName.startsWith("com.android")) {
			if (lpparam.packageName.equals("com.android.systemui")) {
				ModSystemUI.hookSystemUI(lpparam.classLoader);
			}
			
			return;
		}
		
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
				
				final Window window = activity.getWindow();
				int flags = window.getAttributes().flags;
				
				if ((flags & WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS) != 0) {
					
					return;
				}
				
				window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
				
				View decor = window.getDecorView();
				int sysui = decor.findViewById(android.R.id.content).getSystemUiVisibility();
				
				if ((sysui & View.SYSTEM_UI_FLAG_FULLSCREEN) != 0) {
					return;
				}
				
				decor.setDrawingCacheEnabled(false);
				
				XposedHelpers.setAdditionalInstanceField(activity, "shouldTint", true);
			}
		});
		
		XposedHelpers.findAndHookMethod(Activity.class, "onResume", new XC_MethodHook() {
			@Override
			protected void afterHookedMethod(XC_MethodHook.MethodHookParam mhparams) throws Throwable {
				Activity activity = (Activity) mhparams.thisObject;
				Boolean shouldTint = (Boolean) XposedHelpers.getAdditionalInstanceField(activity, "shouldTint");
				if (shouldTint != null && shouldTint) {
					final Window window = activity.getWindow();
					final View decor = window.getDecorView();
					decor.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
						long last = 0;
						@Override
						public void onGlobalLayout() {
							long now = System.currentTimeMillis();
							
							if (now - last >= MIN_BREAK) {
								XposedHelpers.setAdditionalInstanceField(decor, "isDecor", true);
								XposedHelpers.setAdditionalInstanceField(decor, "window", window);
								decor.invalidate();
								last = now;
							}
							//decor.getViewTreeObserver().removeGlobalOnLayoutListener(this);
						}
					});
				}
			}
		});
		
		XposedHelpers.findAndHookMethod(View.class, "draw", Canvas.class, new XC_MethodHook() {
			@Override
			protected void beforeHookedMethod(XC_MethodHook.MethodHookParam mhparams) throws Throwable {
				Boolean isDecor = (Boolean) XposedHelpers.getAdditionalInstanceField(mhparams.thisObject, "isDecor");
				if (isDecor != null && isDecor) {
					Canvas canvas = (Canvas) mhparams.args[0];
					
					Bitmap newBitmap = (Bitmap) XposedHelpers.getAdditionalInstanceField(mhparams.thisObject, "newBitmap");
					Canvas newCanvas = (Canvas) XposedHelpers.getAdditionalInstanceField(mhparams.thisObject, "newCanvas");
					
					View v = (View) mhparams.thisObject;
					
					if (STATUS_HEIGHT == 0) {
						STATUS_HEIGHT = Utility.getStatusBarHeight(v.getContext()) + 1;
					}
					
					if (newBitmap == null) {
						// We only crop the top part of the view.
						newBitmap = Bitmap.createBitmap(v.getWidth(), STATUS_HEIGHT + 1, Bitmap.Config.ARGB_4444);
						newBitmap.setHasAlpha(false);
						newBitmap.setDensity(canvas.getDensity());
					}
					
					if (newCanvas == null) {
						newCanvas = new Canvas();
						newCanvas.setBitmap(newBitmap);
					}
					
					mhparams.args[0] = newCanvas;
					XposedHelpers.setAdditionalInstanceField(mhparams.thisObject, "oldCanvas", canvas);
					XposedHelpers.setAdditionalInstanceField(mhparams.thisObject, "newBitmap", newBitmap);
					XposedHelpers.setAdditionalInstanceField(mhparams.thisObject, "newCanvas", newCanvas);
				}
			}
			
			@Override
			protected void afterHookedMethod(XC_MethodHook.MethodHookParam mhparams) throws Throwable {
				Boolean isDecor = (Boolean) XposedHelpers.getAdditionalInstanceField(mhparams.thisObject, "isDecor");
				if (isDecor != null && isDecor) {
					Window window = (Window) XposedHelpers.getAdditionalInstanceField(mhparams.thisObject, "window");
					Canvas oldCanvas = (Canvas) XposedHelpers.getAdditionalInstanceField(mhparams.thisObject, "oldCanvas");
					Bitmap newBitmap = (Bitmap) XposedHelpers.getAdditionalInstanceField(mhparams.thisObject, "newBitmap");
					mhparams.args[0] = oldCanvas;
					
					View v = (View) mhparams.thisObject;
					
					int width = v.getWidth();
					
					int color1 = newBitmap.getPixel(width / 2, STATUS_HEIGHT);
					int color2 = newBitmap.getPixel(1, STATUS_HEIGHT);
					int color3 = newBitmap.getPixel(width - 1, STATUS_HEIGHT);
					int color4 = newBitmap.getPixel(width / 4, STATUS_HEIGHT);
					int color5 = newBitmap.getPixel(width / 4 * 3, STATUS_HEIGHT);
					int color = Utility.colorAverage(color1, color2, color3, color4, color5);
					
					window.setStatusBarColor(Utility.darkenColor(color, 0.85f));
					
					XposedHelpers.setAdditionalInstanceField(mhparams.thisObject, "isDecor", false);
					
					// We must mask the view as dirty, or we will never see it flush
					((Method) mhparams.method).invoke(mhparams.thisObject, oldCanvas);
				}
			}
		});
	}

}
