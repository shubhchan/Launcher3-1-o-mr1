package com.google.android.apps.nexuslauncher;

import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;

import com.android.launcher3.AppInfo;
import com.android.launcher3.Launcher;
import com.android.launcher3.R;
import com.android.launcher3.Utilities;
import com.android.launcher3.config.FeatureFlags;
import com.android.launcher3.util.ComponentKeyMapper;
import com.android.launcher3.util.ViewOnDrawExecutor;
import com.google.android.libraries.launcherclient.GoogleNow;

import java.util.List;

public class NexusLauncherActivity extends Launcher {
    private final static String PREF_IS_RELOAD = "pref_reload_workspace";
    private static boolean sShouldReload;
    private NexusLauncher mLauncher;
    private boolean mIsReload;

    public NexusLauncherActivity() {
        mLauncher = new NexusLauncher(this);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        FeatureFlags.QSB_ON_FIRST_SCREEN = showSmartspace();
        super.onCreate(savedInstanceState);

        SharedPreferences prefs = Utilities.getPrefs(this);
        if (mIsReload = prefs.getBoolean(PREF_IS_RELOAD, false)) {
            prefs.edit().remove(PREF_IS_RELOAD).apply();
            getWorkspace().setCurrentPage(0);
            showOverviewMode(false);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        if (FeatureFlags.QSB_ON_FIRST_SCREEN != showSmartspace()
                || sShouldReload) {
            sShouldReload = false;
            Utilities.getPrefs(this).edit().putBoolean(PREF_IS_RELOAD, true).apply();
            if (Utilities.ATLEAST_NOUGAT) {
                recreate();
            } else {
                finish();
                startActivity(getIntent());
            }
        }
    }

    @Override
    public void clearPendingExecutor(ViewOnDrawExecutor executor) {
        super.clearPendingExecutor(executor);
        if (mIsReload) {
            mIsReload = false;
            showOverviewMode(false);
        }
    }

    private boolean showSmartspace() {
        return Utilities.getPrefs(this).getBoolean(SettingsActivity.SMARTSPACE_PREF, true);
    }

    public void setShouldReload(boolean reload) {
        sShouldReload = reload;
    }

    public void overrideTheme(boolean isDark, boolean supportsDarkText) {
        //boolean darktheme = Utilities.getPrefs(this).getBoolean("pref_darktheme_enabled", false);
        //int darkthemestyle = Integer.valueOf(Utilities.getPrefs(this).getString("pref_darkthemestyle", "1"));
        int themestyle = Integer.valueOf(Utilities.getPrefs(this).getString("pref_themestyle", "1"));
        boolean googlebarinappmenu = Utilities.getPrefs(this).getBoolean("pref_googleinappmenu_enabled", false);
       /* boolean darktext = Utilities.getPrefs(this).getBoolean("pref_darktext_enabled", false);
        setTheme(R.style.GoogleSearchLauncherTheme);
        if (darktheme && googlebarinappmenu) {
            if (darkthemestyle == 1) setTheme(R.style.GoogleSearchLauncherThemeDark);
            else setTheme(R.style.GoogleSearchLauncherThemeBlack);*/
        if (googlebarinappmenu) {
            switch (themestyle) {
                case 1:
                    setTheme(R.style.GoogleSearchLauncherTheme);
                case 2:
                    if (Utilities.ATLEAST_NOUGAT)
                        setTheme(R.style.GoogleSearchLauncherThemeDarkText);
                case 3:
                    setTheme(R.style.GoogleSearchLauncherThemeDark);
                case 4:
                    setTheme(R.style.GoogleSearchLauncherThemeBlack);
            }
        } else {
            switch (themestyle) {
                case 1:
                    setTheme(R.style.LauncherTheme);
                case 2:
                    if (Utilities.ATLEAST_NOUGAT) setTheme(R.style.LauncherThemeDarkText);
                case 3:
                    setTheme(R.style.LauncherThemeDark);
                case 4:
                    setTheme(R.style.LauncherThemeBlack);
            }
        }
    }

    public List<ComponentKeyMapper<AppInfo>> getPredictedApps() {
        return mLauncher.fA.getPredictedApps();
    }

    public GoogleNow getGoogleNow() {
        return mLauncher.fy;
    }
}
