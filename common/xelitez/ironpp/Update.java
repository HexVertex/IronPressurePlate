package xelitez.ironpp;

import xelitez.updateutility.IXEZUpdate;

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
		return "/xezmods.png";
	}

	@Override
	public String getUpdateUrl() 
	{
		return "http://www.minecraftforum.net/topic/842232-/#KIPP";
	}

	@Override
	public String getDownloadUrl() 
	{
		return "http://adf.ly/HH94L";
	}

	@Override
	public String stringToDelete() 
	{
		return "IronPressurePlate";
	}
}
