package retromodder.advanced.rtm;

import android.accessibilityservice.*;
import android.app.*;
import android.app.backup.*;
import android.content.*;
import android.os.*;
import android.util.*;
import android.view.*;
import android.view.accessibility.*;
import android.view.animation.*;
import java.text.*;
import java.util.*;

public class MyUtil
{
    public final static String PACKAGE_NAME = "tinyclipboardmanager";

    public static int dip2px(Context context, float dipValue)
	{
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int)(dipValue * scale + 0.5f);
    }

    public static int px2dip(Context context, float pxValue)
	{
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int)(pxValue / scale + 0.5f);
    }

    public static String stringLengthCut(String string)
	{
        return stringLengthCut(string, 200);
    }

    public static String stringLengthCut(String string, int length)
	{
        string = string.trim();
        return  (string.length() > length) ?
			string.substring(0, length - 2).trim() + "…"
			: string.trim();
    }

    public static String getFormatDate(Context context, Date date)
	{
        final SimpleDateFormat dateFormat = new SimpleDateFormat("EEE, MMM dd");
        return dateFormat.format(date);
    }

    public static String getFormatTime(Context context, Date date)
	{
        final SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm");
        return dateFormat.format(date);
    }

    public static String getFormatTimeWithSecond(Context context, Date date)
	{
        final SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
        return dateFormat.format(date);
    }

    public static class ResizeWidthAnimation extends Animation
	{
        private int mWidth;
        private int mStartWidth;
        private View mView;

        public ResizeWidthAnimation(View view, int width)
        {
            mView = view;
            mWidth = width;
            mStartWidth = view.getWidth();
        }

        @Override
        protected void applyTransformation(float interpolatedTime, Transformation t)
        {
            int newWidth = mStartWidth + (int) ((mWidth - mStartWidth) * interpolatedTime);

            mView.getLayoutParams().width = newWidth;
            mView.requestLayout();
        }

        @Override
        public void initialize(int width, int height, int parentWidth, int parentHeight)
        {
            super.initialize(width, height, parentWidth, parentHeight);
        }

        @Override
        public boolean willChangeBounds()
        {
            return true;
        }
    }

    public static void requestBackup(Context context)
	{
        Log.d(MyUtil.PACKAGE_NAME, "requestBackup");
        BackupManager backupManager = new BackupManager(context);
        backupManager.dataChanged();
    }

    public static SharedPreferences getLocalSharedPreferences(Context context)
	{
        return context.getSharedPreferences("LocalSharedPreference", Context.MODE_PRIVATE);
    }

    public static boolean isMyServiceRunning(Context context, Class<?> serviceClass)
	{
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE))
		{
            if (serviceClass.getName().equals(service.service.getClassName()))
			{
                return true;
            }
        }
        return false;
    }

    public static boolean isAccessibilityEnabled(Context context, String id)
	{

        AccessibilityManager am = (AccessibilityManager) context
			.getSystemService(Context.ACCESSIBILITY_SERVICE);

        List<AccessibilityServiceInfo> runningServices = am
			.getEnabledAccessibilityServiceList(AccessibilityEvent.TYPES_ALL_MASK);
        for (AccessibilityServiceInfo service : runningServices)
		{
            if (service.getId().contains(id))
			{
                return true;
            }
        }

        return false;
    }

    public static void vibrator(Context context)
	{
        //This need <uses-permission android:name="android.permission.VIBRATE" />
        Vibrator vib = (Vibrator) context.getSystemService(Service.VIBRATOR_SERVICE);
        vib.vibrate(20);
    }

}
