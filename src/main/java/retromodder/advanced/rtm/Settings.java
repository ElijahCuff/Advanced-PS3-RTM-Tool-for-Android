package retromodder.advanced.rtm;

import android.content.*;
import android.preference.*;

public class Settings
{
	protected static SharedPreferences pref;

	protected Settings(SharedPreferences preferences)
	{
		pref = preferences;
	}


	public static Settings get(Context context)
	{
		SharedPreferences preferences =  PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
		return new Settings(preferences);
	}

	public static int getFontSize()
	{
		try
		{
			return Integer.parseInt(pref.getString("font_size", "2"));
		}
		catch (Exception e)
		{
			return 2;
		}
	}
	public static boolean useAudio()
	{
		return pref.getBoolean("audio", false);
	}

	public static boolean isFullscreen()
	{
		return pref.getBoolean("fullscreen", false);
	}
	public static String getIP()
	{
		return pref.getString("ip", "192.168.0.0");
	}
}
