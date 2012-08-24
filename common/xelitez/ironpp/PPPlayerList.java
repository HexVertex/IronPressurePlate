/**
 * instance class for players in the list.
 * 
 * @author Kalvin
 */
package xelitez.ironpp;

public class PPPlayerList 
{
	private String username;
	public boolean isEnabled = true;
	
	public PPPlayerList(String var1)
	{
		username = var1;
		isEnabled = true;
	}
	
	public PPPlayerList(String var1, boolean var2)
	{
		username = var1;
		isEnabled = var2;
	}
	
	public PPPlayerList()
	{
	}
	
	public boolean getEnabled()
	{
		return isEnabled;
	}
	
	public String getUsername()
	{
		return username;
	}
	
	public void setEnabled(Boolean var1)
	{
		isEnabled = var1;
	}
	
	public void setMobname(String var1)
	{
		username = var1;
	}
	
	public void enable()
	{
		this.isEnabled = true;
	}
	
	public void disable()
	{
		this.isEnabled = false;
	}
}