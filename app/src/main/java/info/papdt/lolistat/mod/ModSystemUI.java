package info.papdt.lolistat.mod;

import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.widget.ImageView;

import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.XC_MethodHook;

import info.papdt.lolistat.support.Settings;
import static info.papdt.lolistat.BuildConfig.DEBUG;

public class ModSystemUI
{
	private static final String TAG = ModSystemUI.class.getSimpleName() + ":";
	
	public static void hookSystemUI(ClassLoader loader) {
		if (DEBUG) {
			XposedBridge.log(TAG + "Loading SystemUI");
		}
		
		Settings settings = Settings.getInstance(null);
		if (!settings.getBoolean("global", "global", Settings.TINT_ICONS, false))
			return;
		
		// Thanks to MohanmmadAG
		final Class<?> iconView = XposedHelpers.findClass("com.android.systemui.statusbar.StatusBarIconView", loader);
		
		XposedHelpers.findAndHookMethod(ImageView.class, "setImageDrawable", Drawable.class, new XC_MethodHook() {
			@Override
			protected void beforeHookedMethod(XC_MethodHook.MethodHookParam mhparams) throws Throwable {
				if (iconView.isInstance(mhparams.thisObject)) {
					
					if (DEBUG) {
						XposedBridge.log(TAG + "applying filter to Drawable");
					}
					
					Drawable d = (Drawable) mhparams.args[0];
					d.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP);
				}
			}
		});
	}
}
