package retromodder.advanced.rtm;
import android.content.*;
import android.os.*;
import java.net.*;
import java.nio.charset.*;
import org.jsoup.*;
import org.jsoup.nodes.*;

public class PS3
{
	public static Context context;
	static Boolean ps3ready;
	static Boolean checking;
	static Thread x;
	static Boolean processUpdating;
	static Settings settings;


	public static void setContext(Context contextOfCall)
	{
		context = contextOfCall;
	}

	// Global Charset Encoding
    public static Charset encodingType = StandardCharsets.US_ASCII;

	public static boolean setMemory(String offset, String hexText)
	{
		if (currentProc == null)
		{
			getProcess(true);
		}
		boolean ok = !ps3do("setmem", "proc=" + currentProc + "&addr=" + offset + "&val=" + hexText, true).text().contains("Error: -2147418099");
		return ok;
	}

	public static String getMemory(String offset, String len)
	{
		String result;
		if (currentProc == null)
		{
			getProcess(true);
		}
		try
		{
			result = ps3do("getmem", "proc=" + currentProc + "&addr=" + offset + "&len=" + len, true).getElementById("output").text();
		}
		catch (Exception x)
		{
			result = "";
		}
		return result;
	}

	public static String currentProc;
	public static String getProcess(boolean useName)
	{
		try
		{
			String getMemHTML = "none";
			if ((getMemHTML = ps3do("getmem", "", true).getElementsByClass("propfont").first().html()) != "none")
			{
				int procStart = getMemHTML.indexOf("option value=\"") + 14;
				int procEnd = getMemHTML.indexOf("\"", procStart);
				int valStart = getMemHTML.indexOf("option>", procEnd) + 7;
				int valEnd = getMemHTML.indexOf("<", valStart);
				String Name = getMemHTML.substring(valStart, valEnd);
				String Value = getMemHTML.substring(procStart, procEnd);
				currentProc = Value;
				if (useName)
				{
					return Name;
				}
				else
				{
					return Value;
				}
			}
			else
			{
				return "NO PROC ID";
			}
		}
		catch (Exception e)
		{
			return "NO PROC ID";
		}
	}

	public static boolean shutdown()
	{
		try
		{
			if (ps3do("shutdown", "", false) != null)
			{
				return true;
			}
			else
			{
				return false;
			}
		}
		catch (Exception e)
		{
			return false;
		}
	}
	public static boolean beep()
	{
		try
		{
			if (ps3do("buzzer", "mode=1", true) != null)
			{
				return true;
			}
			else
			{
				return false;
			}
		}
		catch (Exception e)
		{
			return false;
		}
	}
	public static boolean restart()
	{
		try
		{
			if (ps3do("restart", "", false) != null)
			{
				return true;
			}
			else
			{
				return false;
			}
		}
		catch (Exception e)
		{
			return false;
		}
	}

	public static boolean eject()
	{
		try
		{
			if (ps3do("eject", "", false) != null)
			{
				return true;
			}
			else
			{
				return false;
			}
		}
		catch (Exception e)
		{
			return false;
		}
	}
	public static boolean notify(String message)
	{
		try
		{
			message = URLEncoder.encode(message).toString();
			if (ps3do("notify", "msg=" + message, true) != null)
			{
				return true;
			}
			else
			{
				return false;
			}
		}
		catch (Exception e)
		{
			return false;
		}
	}

	static boolean allReady;
	static long lastCheckTime;
	public static boolean isReady()
	{
		long now = SystemClock.uptimeMillis() / 1000;
		int waitSeconds = 20;
		if ((lastCheckTime + waitSeconds) < now | !allReady)
		{
			lastCheckTime = SystemClock.uptimeMillis() / 1000;
			if (ps3do("home", "", true) != null)
			{
				allReady = true;
			}
			else
			{
				allReady = false;
			}
			return allReady;
		}
		return allReady;
	}

	static Document resultDoc;
	public static Document ps3do(String Action, String Command, boolean useMapi)
	{
		String ip = settings.getIP();
		String mapi = "";
		if (useMapi)
		{
			mapi = "mapi?";
		}
		final String url = "http://" + ip + "/" + Action + ".ps3" + mapi + Command;
		Thread net = new Thread(new Runnable(){

				@Override
				public void run()
				{
					try
					{
						Document doc = Jsoup.connect(url).get();
						try
						{
							if ((doc.title()).contains("wMAN"))
							{
								resultDoc = doc;
							}
						}
						catch (Error e)
						{
							resultDoc = null;
						}
						catch (Exception ex)
						{
							resultDoc = null;
						}
					}
					catch (Exception e)
					{
						resultDoc = null;
					}
				}
			});
		net.start();
		if (net.isAlive())
		{
			while (net.isAlive())
			{
				// thread run freeze
			}
		}
		return resultDoc;
	}

	// Text To Hex
	public static String textToHex(String text)
	{
		byte[] buf = null;
		buf = text.getBytes(encodingType);
		char[] HEX_CHARS = "0123456789ABCDEF".toCharArray();
		char[] chars = new char[2 * buf.length];
		for (int i = 0; i < buf.length; ++i)
		{
			chars[2 * i] = HEX_CHARS[(buf[i] & 0xF0) >>> 4];
			chars[2 * i + 1] = HEX_CHARS[buf[i] & 0x0F];
		}
		return new String(chars);
	}

	// Hex To Text
	public static String hexToText(String hex)
	{
		int l = hex.length();
		byte[] data = new byte[l / 2];
		for (int i = 0; i < l; i += 2)
		{
			data[i / 2] = (byte) ((Character.digit(hex.charAt(i), 16) << 4)
				+ Character.digit(hex.charAt(i + 1), 16));
		}
		String st = new String(data, encodingType);
		return st;
	}

	// Dec 2 Hex
	public static String decimalToHex(int decimal)
	{
		if (decimal !=  0)
		{
			return Integer.toHexString(decimal).toUpperCase();
		}
		else
		{
			return "No Entry";
		}
	}

	// Hex 2 Dec
	public static Integer hexToDecimal(String hex)
	{
		if (hex.contains("0x") | hex.contains(" "))
		{
			hex = hex.replace("0x", "").replace(" ", "");
		}
		return Integer.valueOf(hex, 16);
	}

    // DecStr 2 Hex
	public static String decimalStringToHex(String decimal)
	{
		String string = decimal;
		int no = Integer.parseInt(string);
		String hex = Integer.toHexString(no);
		return hex.toUpperCase();
	}

}
