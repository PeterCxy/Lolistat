package info.papdt.lolistat.mod;

import android.app.Activity;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Window;

import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.XC_MethodHook;

import java.lang.reflect.Field;

import info.papdt.lolistat.support.Settings;

public class ModNavigationBar
{
	public static void hookNavigationBar(ClassLoader loader) throws Throwable {
		Settings settings = Settings.getInstance(null);
		
		if (!settings.getBoolean("global", "global", Settings.TINT_NAVIGATION, true)) return;

		final Class<?> internalStyleable = XposedHelpers.findClass("com.android.internal.R.styleable", loader);
		final Field internalThemeField = XposedHelpers.findField(internalStyleable, "Theme");
		final Field internalColorPrimaryDarkField = XposedHelpers.findField(internalStyleable, "Theme_colorPrimaryDark");
		final int[] theme = (int[]) internalThemeField.get(null);
		final int theme_colorPrimaryDark = internalColorPrimaryDarkField.getInt(null);

		XposedHelpers.findAndHookMethod("com.android.internal.policy.impl.PhoneWindow", loader, "setStatusBarColor", int.class, new XC_MethodHook() {
			@Override
			protected void afterHookedMethod(XC_MethodHook.MethodHookParam mhparams) throws Throwable {
				int color = Integer.valueOf(mhparams.args[0].toString());

				if (color != 0)
					((Window) mhparams.thisObject).setNavigationBarColor(color);
			}
		});

		XposedHelpers.findAndHookMethod(Activity.class, "onCreate", Bundle.class, new XC_MethodHook() {
			@Override
			protected void afterHookedMethod(XC_MethodHook.MethodHookParam mhparams) throws Throwable {
				Activity activity = (Activity) mhparams.thisObject;

				TypedArray a = activity.getTheme().obtainStyledAttributes(theme);
				int colorPrimaryDark = a.getColor(theme_colorPrimaryDark, Color.TRANSPARENT);
				a.recycle();

				if (colorPrimaryDark != Color.TRANSPARENT && colorPrimaryDark != Color.BLACK) {
					activity.getWindow().setNavigationBarColor(colorPrimaryDark);
				}
			}
		});
	}
}
