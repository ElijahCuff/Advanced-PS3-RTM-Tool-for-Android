package retromodder.advanced.rtm;

import android.content.*;
import android.os.*;
import android.preference.*;
import android.support.v7.widget.*;
import android.view.*;
import android.widget.*;

import android.support.v7.widget.Toolbar;

public class Preferences extends PreferenceActivity implements
SharedPreferences.OnSharedPreferenceChangeListener
{
	Settings settings;
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		settings = Settings.get(getApplicationContext());
		// ON CREATE UPDATE PREFS
		if (settings.isFullscreen())
		{
			getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		}
		addPreferencesFromResource(R.xml.settings);
		if (Build.VERSION.SDK_INT < 11)
		{

			// getPreferenceScreen().findPreference("pref_key").setEnabled(false);

		}

	}
	@Override
	public void onSharedPreferenceChanged(SharedPreferences p1, String p2)
	{
		// reload settings
		settings = Settings.get(getApplicationContext());
	}



    @Override
    public void setContentView(int layoutResID)
	{
        ViewGroup contentView = (ViewGroup) LayoutInflater.from(this).inflate(
			R.layout.activity_setting, new LinearLayout(this), false);

		Toolbar mActionBar = (Toolbar) contentView.findViewById(R.id.my_toolbar);
        mActionBar.setNavigationOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v)
				{
					finish();
				}
			});

        ViewGroup contentWrapper = (ViewGroup) contentView.findViewById(R.id.content_wrapper);
        LayoutInflater.from(this).inflate(layoutResID, contentWrapper, true);
        getWindow().setContentView(contentView);
		mActionBar.setTitle("Settings");

    }

}
