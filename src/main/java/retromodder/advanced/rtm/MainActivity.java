package retromodder.advanced.rtm;

import android.app.*;
import android.content.*;
import android.graphics.*;
import android.os.*;
import android.preference.*;
import android.support.design.widget.*;
import android.support.v4.view.*;
import android.support.v4.widget.*;
import android.support.v7.app.*;
import android.support.v7.widget.*;
import android.view.*;
import android.widget.*;
import java.net.*;

import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;


public class MainActivity extends AppCompatActivity implements
SharedPreferences.OnSharedPreferenceChangeListener
{

	Context context;
    Toolbar toolbar;
    DrawerLayout mDrawerLayout;
    NavigationView mNavigationView;
    SharedPreferences prefs;
	SharedPreferences.Editor editor;
	Activity main;
	Settings settings;
	FragmentManager mFragmentManager;
    FragmentTransaction mFragmentTransaction;
	boolean onSettings;
	boolean backFromSettings;
	Typeface wrecked;
	Typeface skate;
	Typeface snake;
	Typeface defaultFont;
	String ip;

    @Override
    protected void onCreate(Bundle savedInstanceState)
	{
        super.onCreate(savedInstanceState);
		settings = Settings.get(this);
		sharedPrefsUpdate();
		onSettings = false;
		setContentView(R.layout.activity_main);
		context = this.getApplicationContext();
		main = this;
		prefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
		editor = prefs.edit();
		toolbar = (Toolbar) findViewById(R.id.toolbar);
		if (toolbar != null)
		{
			setSupportActionBar(toolbar);
			toolbar.setNavigationIcon(null);
	    }
		initializeFonts();
		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		mNavigationView = (NavigationView) findViewById(R.id.main_drawer) ;
		int width = getResources().getDisplayMetrics().widthPixels;
		int height = getResources().getDisplayMetrics().heightPixels;

		mNavigationView.setItemTextAppearance(R.style.TextViewAppearance);
		DrawerLayout.LayoutParams params = (android.support.v4.widget.DrawerLayout.LayoutParams) mNavigationView.getLayoutParams();
		if (width > height)
		{
			params.width = width / 4;
		}
		else
		{
			params.width = width / 2;
		}

		mNavigationView.setBackgroundResource(R.drawable.nabdrawer);
		mDrawerLayout.setBackgroundResource(R.drawable.header);
		mNavigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
				@Override
				public boolean onNavigationItemSelected(MenuItem menuItem)
				{
					ip = settings.getIP();
					// ITEMS IN DRAWER
					if (menuItem.getItemId() == R.id.mod)
					{
						menuItem.setChecked(true);
						mod_mem();
					}
					if (menuItem.getItemId() == R.id.notify)
					{
						menuItem.setChecked(false);
						notify_ps3();
					}
					if (menuItem.getItemId() == R.id.power)
					{
						menuItem.setChecked(false);
						power_ps3();
					}
					if (menuItem.getItemId() == R.id.restart)
					{
						menuItem.setChecked(false);
						restart_ps3();
					}
					if (menuItem.getItemId() == R.id.eject)
					{
						menuItem.setChecked(false);
						eject_game();
					}
					// CLOSE DRAWER
					mDrawerLayout.closeDrawer(GravityCompat.START, true);
					return false;
				}

			});
		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		ActionBarDrawerToggle mDrawerToggle = new ActionBarDrawerToggle(MainActivity.this, mDrawerLayout, toolbar, R.string.drawer_open, R.string.drawer_close);
		mDrawerLayout.setDrawerListener(mDrawerToggle);
		mDrawerToggle.syncState();
		toolbar.setTitleTextAppearance(this, R.style.TextViewAppearance);
		toolbar.setPopupTheme(R.style.toolPopup);
		sharedPrefsUpdate();
		prefs.registerOnSharedPreferenceChangeListener(MainActivity.this);
	    PS3.setContext(this.getApplicationContext());
		ip = settings.getIP();
		mod_mem();
	}
	String currentProc;
	// ACTIONS

	public void mod_mem()
	{
		TextView actionTitle = ActionBarTitle(toolbar);
		actionTitle.setText(getString(R.string.launch_name));
		inflate(R.layout.mod_mem);
		actionTitle.setTypeface(skate);
		boolean agreed = prefs.getBoolean("agreed=", false);
		if (!agreed)
		{
			new AlertDialog.Builder(this)
				.setCancelable(false)
				.setTitle("License Agreement")
				.setMessage("The usage of this app is subject to a license agreement, you must agree that you have read and agree to comply with the following statements, disclaimers, licenses and agreements.\n\n" + getString(R.string.licence))
				.setNegativeButton(" I AGREE ", new DialogInterface.OnClickListener()
				{
					public void onClick(DialogInterface dialog, int which)
					{
						editor.putBoolean("agreed=", true);
						editor.commit();
						
									reconnect();
									doOnClicks();
				
					}
				})
				.setNeutralButton(" I DO NOT AGREE ", new DialogInterface.OnClickListener()
				{
					public void onClick(DialogInterface dialog, int which)
					{
						finishAndRemoveTask();
					}
				})
				.setIcon(R.drawable.icon)
				.show();
		}
		else
		{
			reconnect();
			doOnClicks();
		}
	}
	public void reconnect()
	{
		final TextView proc = (TextView) findViewById(R.id.currentProcess);
		snackBar("CHECKING CONNECTION", false);
		proc.setText("CHECKING, WAIT !");
		new Thread(new Runnable(){
				@Override
				public void run()
				{
					try
					{
						connectPS3();
						if (checkingConnection)
						{
							while (checkingConnection)
							{
								// loopout
							}
						}
					}
					catch (Error e)
					{
						snackBar(e.getMessage(), false);
					}
					catch (Exception ex)
					{
						snackBar(ex.getMessage(), false);
					}
					finally
					{
						String proce;
						if (PS3.isReady())
						{
							proce = (PS3.getProcess(true));
							currentProc = proce;
						}
						else
						{
							proce = ("NO PROCESS");
							currentProc = null;
						}
						final String out = proce;
						main.runOnUiThread(new Runnable(){

								@Override
								public void run()
								{
									proc.setText(out);
								}
							});
					}
				}
			}).start();
	}

	public void doOnClicks()
	{
		final EditText offsetTextBox = (EditText) findViewById(R.id.modMemOffset);
		final EditText lengthTextBox = (EditText) findViewById(R.id.byteCount);
		final EditText valueTextBox = (EditText) findViewById(R.id.valueModMem);
		final Switch ebootOffsetSwitch = (Switch) findViewById(R.id.addRTM);
		final Switch hexOrTextSwitch = (Switch) findViewById(R.id.hexSwitch);
		final ImageButton updateProcButton = (ImageButton) findViewById(R.id.updateProc);
		final Button setMemoryButton = (Button) findViewById(R.id.setMemButton);
		final Button getMemoryButton = (Button) findViewById(R.id.getMemButton);

		ebootOffsetSwitch.setOnCheckedChangeListener(new Switch.OnCheckedChangeListener(){
				@Override
				public void onCheckedChanged(CompoundButton p1, boolean useEboot)
				{
				    boolean hasOffset = (offsetTextBox.getText().length() > 0);
					if (hasOffset)
					{
						String offsetText = offsetTextBox.getText().toString();
						if (offsetText.contains("0x"))
						{
							offsetText = offsetText.replace("0x", "");
							while (offsetText.startsWith("0"))
							{
								offsetText = offsetText.replaceFirst("0", "");
							}
						}
						int offsetDecimal = PS3.hexToDecimal(offsetText);
						int rtmAddDecimal = PS3.hexToDecimal("10000");
						if (useEboot)
						{
							String newOffset = PS3.decimalToHex(offsetDecimal + rtmAddDecimal);
							offsetTextBox.setText("0x" + newOffset);
						}
						else
						{
							String newOffset = PS3.decimalToHex(offsetDecimal - rtmAddDecimal);
							offsetTextBox.setText("0x" + newOffset);
						}
					}
					else
					{
						p1.setChecked(false);
					}
				}
			});
		hexOrTextSwitch.setOnCheckedChangeListener(new Switch.OnCheckedChangeListener(){

				@Override
				public void onCheckedChanged(CompoundButton switchBox, boolean useText)
				{
					boolean hasValue = (valueTextBox.getText().length() > 0);
					if (hasValue)
					{
						String values = valueTextBox.getText().toString().toUpperCase();
						String vals = valueTextBox.getText().toString();
						boolean isText = values.contains("G") | values.contains("H") | values.contains("I") | values.contains("J") | values.contains("K")
							| values.contains("L") | values.contains("M") | values.contains("N") | values.contains("O") | values.contains("P") | values.contains("Q")
							| values.contains("R") | values.contains("S") | values.contains("T") | values.contains("U") | values.contains("V") | values.contains("W")
							| values.contains("X") | values.contains("Y") | values.contains("Z") | values.length() < 2;
						if (useText)
						{
							if (isText)
							{ 
								snackBar("TEXT ALREADY FOUND", false);
							}
							else
							{
								valueTextBox.setText(PS3.hexToText(vals));
							}
						}
						else
						{
							valueTextBox.setText(PS3.textToHex(vals).toUpperCase());
						}
					}
					else
					{
						snackBar("NO VALUE CONVERTED", false);
					}
				}
			});
		updateProcButton.setOnClickListener(new ImageButton.OnClickListener(){
				@Override
				public void onClick(View p1)
				{
					reconnect();
				}
			});
		setMemoryButton.setOnClickListener(new Button.OnClickListener(){
				@Override
				public void onClick(View p1)
				{
					if (connected)
					{
						boolean hasValue = (valueTextBox.getText().length() > 0);
						boolean hasOffset = (offsetTextBox.getText().length() > 0);
						boolean usingText = hexOrTextSwitch.isChecked();
						if (hasValue & hasOffset)
						{
							String offsetText = offsetTextBox.getText().toString();
							String vals = valueTextBox.getText().toString();
							if (offsetText.contains("0x"))
							{
								offsetText = offsetText.replace("0x", "");
								while (offsetText.startsWith("0"))
								{
									offsetText = offsetText.replaceFirst("0", "");
								}
							}
							if (usingText)
							{
								vals = PS3.textToHex(vals);
							}
							if (vals.length() <= 4096)
							{
								if (PS3.setMemory(offsetText, vals))
								{
									snackBar("SET MEMORY OK", false);
								}
								else
								{
									snackBar("SET MEMORY FAILED", false);
								}
							}
							else
							{
								snackBar("2048 MAX BYTE EXCEEDED", false);
							}
						}
						else
						{
							if (hasValue & ! hasOffset)
							{
								snackBar("NO OFFSET SET", false);
							}
							else if (hasOffset & ! hasValue)
							{
								snackBar("NO VALUE SET", false);
							}
						}
					}
					else
					{
						snackBar("NOT CONNECTED", false);
					}
				}
			});
		getMemoryButton.setOnClickListener(new Button.OnClickListener(){
				@Override
				public void onClick(View p1)
				{
				    if (connected)
					{
						boolean hasOffset = (offsetTextBox.getText().length() > 0);
						boolean hasLength = ((lengthTextBox.getText()).length() > 0);
						if (hasOffset & hasLength)
						{
							String offsetText = offsetTextBox.getText().toString();
							String length = lengthTextBox.getText().toString();
							boolean usingText = hexOrTextSwitch.isChecked();
							if (offsetText.contains("0x"))
							{
								offsetText = offsetText.replace("0x", "");
								while (offsetText.startsWith("0"))
								{
									offsetText = offsetText.replaceFirst("0", "");
								}
							}
							String results;
							int lengthDec;
							try
							{
								lengthDec = Integer.parseInt(length);
							}
							catch (Exception x)
							{
								lengthDec = 0;
							}
							if (lengthDec != 0 & lengthDec <= 2048)
							{
								if ((results = PS3.getMemory(offsetText, length)).length() > 0)
								{
									if (usingText)
									{
										results = PS3.hexToText(results);
									}
									valueTextBox.setText(results);
								}
								else
								{
									snackBar("NO RESULTS", false);
								}
							}
							else if (lengthDec == 0)
							{
								snackBar("0 IS NOT A VALID LENGTH", false);
							}
							else if (lengthDec > 2048)
							{
								snackBar("2048 MAX BYTE LENGTH", false);
							}
						}
						else if (hasOffset & ! hasLength)
						{
							snackBar("NO LENGTH DEFINED", false);
						}
						else if (hasLength & ! hasOffset)
						{
							snackBar("NO OFFSET DEFINED", false);
						}
					}
					else
					{
						snackBar("NOT CONNECTED", false);
					}
				}
			});

	}

	public void notify_ps3()
	{
		if (connected)
		{
			AlertDialog.Builder alert = new AlertDialog.Builder(this, R.style.AppCompatDialogTheme);
			alert.setTitle("NOTIFY");
			alert.setMessage("Enter your message to be displayed on-screen.");
			final EditText input = new EditText(this);
			input.setMaxEms(10);
			alert.setView(input);
			alert.setPositiveButton("SEND", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton)
					{
						String value = input.getText().toString();
						URL to;
						try
						{
							to = new URL(value);
							value = String.valueOf(to);
						}
						catch (MalformedURLException e)
						{
							//
						}
						PS3.notify(value);
					}
				});
			alert.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton)
					{
						// Canceled.
					}
				});
			alert.setIcon(R.drawable.notify);
			alert.show();
		}
		else
		{
			snackBar("NOT CONNECTED", false);
		}
	}
	public void power_ps3()
	{
		if (connected)
		{
			new AlertDialog.Builder(this, R.style.AppCompatDialogTheme)
				.setCancelable(false)
				.setTitle("SHUTDOWN")
				.setMessage("Power OFF the PlayStation 3 Console.")
				.setPositiveButton(" SHUTDOWN ", new DialogInterface.OnClickListener()
				{
					public void onClick(DialogInterface dialog, int which)
					{
						PS3.shutdown();
					}
				})
				.setNeutralButton(" CANCEL ", new DialogInterface.OnClickListener()
				{
					public void onClick(DialogInterface dialog, int which)
					{
					}
				})
				.setIcon(R.drawable.power)
				.show();
		}
		else
		{
			snackBar("NOT CONNECTED", false);
		}
	}
	public void restart_ps3()
	{
		if (connected)
		{
			new AlertDialog.Builder(this, R.style.AppCompatDialogTheme)
				.setCancelable(false)
				.setTitle("RESTART")
				.setMessage("Restart the PlayStation 3 Console.")
				.setPositiveButton(" RESTART ", new DialogInterface.OnClickListener()
				{
					public void onClick(DialogInterface dialog, int which)
					{

						PS3.restart();
					}
				})
				.setNeutralButton(" CANCEL ", new DialogInterface.OnClickListener()
				{
					public void onClick(DialogInterface dialog, int which)
					{
					}
				})
				.setIcon(R.drawable.power)
				.show();
		}
		else
		{
			snackBar("NOT CONNECTED", false);
		}
	}
	public void eject_game()
	{
		if (connected)
		{
			new AlertDialog.Builder(this, R.style.AppCompatDialogTheme)
				.setCancelable(false)
				.setTitle("EJECT")
				.setMessage("Eject the PlayStation 3 Game.")
				.setPositiveButton(" EJECT ", new DialogInterface.OnClickListener()
				{
					public void onClick(DialogInterface dialog, int which)
					{

						PS3.eject();
					}
				})
				.setNeutralButton(" CANCEL ", new DialogInterface.OnClickListener()
				{
					public void onClick(DialogInterface dialog, int which)
					{
					}
				})
				.setIcon(R.drawable.power)
				.show();
		}
		else
		{
			snackBar("NOT CONNECTED", false);
		}

	}

	// PROGRAM
	boolean checkingConnection;
	boolean connected;
	public void connectPS3()
	{
		checkingConnection = true;
		String ip = settings.getIP();
		final TextView status = (TextView) findViewById(R.id.statusText);

		if (ip.contains("192.168.0.0") | ip.length() < 7)
		{
			snackBar("PLEASE SET YOUR IP IN SETTINGS", false);
		}
		else
		{
			snackBar("CHECKING NETWORK", false);
			status.setText("CHECKING NETWORK");
			new Thread(new Runnable(){
					@Override
					public void run()
					{
						String result;
						if (PS3.isReady())
						{
							result = "CONNECTED";
							connected = true;
						}
						else
						{
							result = "NOT CONNECTED";
							connected = false;
						}
						final String out = result;
						main.runOnUiThread(new Runnable(){

								@Override
								public void run()
								{
									status.setText(out);
								}
							});
						checkingConnection = false;
					}
				}).start();
		}
	}
	@Override
    public boolean onCreateOptionsMenu(Menu menu)
	{
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        return true;
	}

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem)
	{
        switch (menuItem.getItemId())
		{
            case R.id.settings:
				{
					onSettings = true;
					startActivity(new Intent(this, Preferences.class));
					return true;
				}
			case  R.id.drawer_exit:
				{
					finishAndRemoveTask();
					return true;
				}
			case  R.id.drawer_about:
				{

					new AlertDialog.Builder(this)
						.setCancelable(false)
						.setTitle("About")
						.setMessage(getString(R.string.about))
						.setNeutralButton(" OK ", new DialogInterface.OnClickListener()
						{
							public void onClick(DialogInterface dialog, int which)
							{

							}
						})
						.setIcon(R.drawable.icon)
						.show();

					return true;
				}
            default:
				return super.onOptionsItemSelected(menuItem);
		}
	}
	@Override
	public void onWindowFocusChanged(boolean hasFocus)
	{
		// TODO: Implement this method
		super.onWindowFocusChanged(hasFocus);
		if (hasFocus)
		{
			if (settings.isFullscreen())
			{
				getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
			}
			else
			{
				getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
			}

			// PATCH DRAWER WIDTH BUG
			int width = getResources().getDisplayMetrics().widthPixels;
			int height = getResources().getDisplayMetrics().heightPixels;
			DrawerLayout.LayoutParams params = (android.support.v4.widget.DrawerLayout.LayoutParams) mNavigationView.getLayoutParams();
			if (width > height)
			{
				params.width = width / 4;
			}
			else
			{
				params.width = width / 2;
			}

			mNavigationView.destroyDrawingCache();
			mNavigationView.refreshDrawableState();
			mNavigationView.clearFocus();
			mNavigationView.requestFocus();

		}
	}

	boolean oldFullscreen ;
	@Override
	public void onSharedPreferenceChanged(SharedPreferences p1, String p2)
	{
		if (settings.isFullscreen())
		{
			getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		}
		else
		{
			getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
		}
		int width = getResources().getDisplayMetrics().widthPixels / 2;
		DrawerLayout.LayoutParams params = (android.support.v4.widget.DrawerLayout.LayoutParams) mNavigationView.getLayoutParams();
		params.width = width;
		mNavigationView.destroyDrawingCache();
		mNavigationView.refreshDrawableState();
		mNavigationView.clearFocus();
		mNavigationView.requestFocus();
	}

	@Override
	public void onBackPressed()
	{
		super.onBackPressed();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event)
	{
		if (keyCode == KeyEvent.KEYCODE_BACK)
		{
			this.moveTaskToBack(true);
		}
		return super.onKeyDown(keyCode, event);
	}
    @Override
    protected void onPause()
	{
        super.onPause();
    }

    @Override
    protected void onResume()
	{
        super.onResume();
        prefs = PreferenceManager.getDefaultSharedPreferences(this);
		if (onSettings)
		{
			onSettings = false;
			backFromSettings = true;
		}

    }

	public void sharedPrefsUpdate()
	{
		if (settings.isFullscreen())
		{
			getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
								 WindowManager.LayoutParams.FLAG_FULLSCREEN);
		}
	}

	private void hideItem(int item_id_toHide)
	{
		NavigationView navigationView = (NavigationView) findViewById(R.id.main_drawer);
		Menu nav_Menu = navigationView.getMenu();
		nav_Menu.findItem(item_id_toHide).setVisible(false);
	}

	public void initializeFonts()
	{
		try
		{
			if (wrecked != font("Wrecked"))
			{
				wrecked = font("Wrecked");
			}
			if (skate != font("Skate"))
			{
				skate = font("Skate");
			}
			if (snake != font("Snake"))
			{
				snake = font("Snake");
			}
			if (defaultFont != font("Roboto-regular"))
			{
				defaultFont = font("Roboto-regular");
			}
		}
		catch (Exception exc)
		{

		}
	}

	private TextView ActionBarTitle(Toolbar toolbarForRead)
	{
		TextView title = null;
		if (toolbarForRead != null)
		{
			for (int i= 0; i < toolbarForRead.getChildCount(); i++)
			{            
				if (toolbarForRead.getChildAt(i) instanceof TextView)
				{
					title = (TextView) toolbarForRead.getChildAt(i);
					return title;
				}
			}
		}
		return null;
	}
	private Typeface font(String fontName)
	{
		return Typeface.createFromAsset(this.getAssets(), "fonts/" + fontName + ".ttf");
	}



	public void copyToClipboard(String key, String value)
	{
		ClipboardManager clipboard = (ClipboardManager)getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText(key, value);
        clipboard.setPrimaryClip(clip);
		toast("Copied To Clipboard");
	}

	public void toast(final String toToast)
	{
		new Thread(new Runnable() {
				@Override
				public void run()
				// BROKEN OUT OF UI THREAD
				{ 
					// REOPENED RUNNABLE ON UI THREAD
					main.runOnUiThread(new Runnable()
						{
							public void run() 
							{
								Toast.makeText(MainActivity.this, toToast, Toast.LENGTH_SHORT).show();
								// STUFF TO DO ON MAIN UI THREAD
							}
						});

				}

			}).start();
	}
	public void inflate(final int toInherit)
	{
		FrameLayout container = (FrameLayout) findViewById(R.id.frame_container);
		Context ctx = container.getContext();
		container.clearFocus();
		container.clearAnimation();
		container.destroyDrawingCache();
		container.removeAllViews();
		container.refreshDrawableState();
		LayoutInflater inflater = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		inflater.inflate(toInherit, container);
	}



	boolean isShown = false;
	Snackbar snack;
	public void snackBar(String snackMessage, boolean useOK)
	{
		if (snack != null && snack.isShown())
		{
			snack.dismiss();
		}
		snack = Snackbar.make(findViewById(R.id.main_content), snackMessage, 3000);
		snack.setActionTextColor(getResources().getColor(R.color.green));

		View snackView = snack.getView();
		FrameLayout.LayoutParams params =(FrameLayout.LayoutParams)snackView.getLayoutParams();
		params.gravity =  Gravity.CENTER_HORIZONTAL | Gravity.TOP;
		params.setMargins(0, 140, 0, 0);
		snackView.setLayoutParams(params);
		int snackTextId = android.support.design.R.id.snackbar_text;  
		TextView snackText = (TextView)snackView.findViewById(snackTextId);  
		snackText.setTypeface(defaultFont);
		snackText.setTextColor(getResources().getColor(R.color.material_blue_grey_900));
		snackText.setWidth(WindowManager.LayoutParams.MATCH_PARENT);
		snackText.setGravity(Gravity.CENTER_HORIZONTAL);
		snackView.setBackgroundColor(Color.WHITE);  

		if (useOK)
		{
			snack.setAction("OK", new View.OnClickListener(){
					@Override
					public void onClick(View p1)
					{
						snack.dismiss();

					}
				});
		}
		snack.show();
	}

	public void exiter(View view)
	{
		finishAndRemoveTask();
	}


}
