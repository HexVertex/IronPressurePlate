package xelitez.ironpp;

public class Version 
{
	public static int majorVersion = 3;
	public static int minorVersion = 2;
	public static int majorBuild = 0;
	public static int minorBuild = 0;
	public static String MC = "MC:1.3.2";
	
	public static String getVersion()
	{
		boolean var2 = minorVersion != 0;
		boolean var3 = majorBuild != 0;
		boolean var4 = minorBuild != 0;
		StringBuilder Str1 = new StringBuilder();
		Str1.append(majorVersion);
		if(var2)
		{
			Str1.append(".");
			Str1.append(minorVersion);
		}
		if(var3)
		{
			Str1.append(".");
			Str1.append(majorBuild);
		}
		if(var4)
		{
			Str1.append(".");
			Str1.append(minorBuild);
		}
		return Str1.toString();
	}

}
