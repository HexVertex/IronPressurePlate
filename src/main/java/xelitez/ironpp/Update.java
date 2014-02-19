package xelitez.ironpp;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

import xelitez.updateutility.IXEZUpdate;
import xelitez.updateutility.XEZLog;

public class Update implements IXEZUpdate
{

	@Override
	public String getCurrentVersion() 
	{
		return Version.getVersion() + " for " + Version.MC;
	}

	@Override
	public String getNewVersion() 
	{
		return Version.newVersion;
	}

	@Override
	public void checkForUpdates() 
	{
		Version.checkForUpdates();
	}

	@Override
	public boolean doesModCheckForUpdates() 
	{
		return IronPP.instance.checkForUpdates;
	}

	@Override
	public boolean isUpdateAvailable() 
	{
		return Version.available;
	}

	@Override
	public String getModIcon() 
	{
		return "ironpp:xezmods.png";
	}

	@Override
	public String getUpdateUrl() 
	{
		return "http://www.minecraftforum.net/topic/842232-/#KIPP";
	}

	@Override
	public String getDownloadUrl() 
	{
List<String> strings = new ArrayList<String>();
		
		try
		{
			URL url = new URL("https://raw2.github.com/XEZKalvin/UpdateUtility/master/updateURLstorage.txt");
			URLConnection connect = url.openConnection();
			connect.setConnectTimeout(5000);
			connect.setReadTimeout(5000);
			BufferedReader in = new BufferedReader(new InputStreamReader(connect.getInputStream()));
			String str;
			
			while ((str = in.readLine()) != null)
			{
				strings.add(str);
			}
			
			in.close();
		}
		catch(Exception E)
		{
			XEZLog.severe("Unable to obtain download URL");
		}
		for (int i = 0; i < strings.size(); i++)
		{
			String line = "";
			
			if (strings.get(i) != null)
			{
				line = (String)strings.get(i);
			}
			if(line.contains("<pressureplate>"))
			{
				return line.substring(line.indexOf("<pressureplate>") + 15, line.indexOf("</pressureplate>"));
			}
		}
		return null;
	}

	@Override
	public String stringToDelete() 
	{
		return "IronPressurePlate";
	}
}
