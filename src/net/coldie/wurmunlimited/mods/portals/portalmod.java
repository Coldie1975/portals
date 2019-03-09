package net.coldie.wurmunlimited.mods.portals;

import org.gotti.wurmunlimited.modloader.interfaces.Configurable;
import org.gotti.wurmunlimited.modloader.interfaces.ItemTemplatesCreatedListener;
import org.gotti.wurmunlimited.modloader.interfaces.PreInitable;
import org.gotti.wurmunlimited.modloader.interfaces.ServerPollListener;
import org.gotti.wurmunlimited.modloader.interfaces.ServerStartedListener;
import org.gotti.wurmunlimited.modloader.interfaces.WurmServerMod;
import org.gotti.wurmunlimited.modsupport.actions.ModActions;

import com.wurmonline.server.creatures.Creature;
import com.wurmonline.server.items.Item;

import org.gotti.wurmunlimited.modsupport.ModSupportDb;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Properties;


public class portalmod implements WurmServerMod, PreInitable, Configurable, ServerStartedListener, ItemTemplatesCreatedListener, ServerPollListener  {
	//private static Logger logger = Logger.getLogger(petmod.class.getName());
	public static HashMap<String, String> myMap = new HashMap<String,String>();
	public static HashMap<String, String> myMapbank = new HashMap<String,String>();
	public static HashMap<String, String> myMapGM1 = new HashMap<String,String>();
	public static HashMap<String, String> myMapGM2 = new HashMap<String,String>();
	public static HashMap<String, Long> myMapportal = new HashMap<String,Long>();	
	
	
	public static int costpermin = 1; //1 iron per min default
	public static int costtoactivate = 10000; //1s default
	public static int activatebankamount = 5000; //50c default
	public static boolean newconcrete = false;
	public static boolean craftportals = false;
	public static int newconcreteitem1 = 146;
	public static int newconcreteitem2 = 492;
	@Override
	public void onItemTemplatesCreated() {
		new PortalItems();
	}
	
	  public void configure(Properties properties)
	  {
	    costpermin = Integer.valueOf(properties.getProperty("costpermin", String.valueOf(costpermin)));
	    costtoactivate = Integer.valueOf(properties.getProperty("costtoactivate", String.valueOf(costtoactivate)));
	    activatebankamount = Integer.valueOf(properties.getProperty("activatebankamount", String.valueOf(activatebankamount)));
	    newconcrete = Boolean.parseBoolean(properties.getProperty("newconcrete", Boolean.toString(newconcrete)));
	    craftportals = Boolean.parseBoolean(properties.getProperty("craftportals", Boolean.toString(craftportals)));	
	    newconcreteitem1 = Integer.valueOf(properties.getProperty("newconcreteitem1", String.valueOf(newconcreteitem1)));
	    newconcreteitem2 = Integer.valueOf(properties.getProperty("newconcreteitem2", String.valueOf(newconcreteitem2)));
	 
	  
	  }
	  
		public static boolean checkaction(Item target){
			int clicked = target.getTemplateId();
			if (clicked == 4002 || clicked == 4003 || clicked == 4004 || clicked == 4010 || clicked == 4011) return true;
			return false;
		};
		
		public static boolean checkGM(Creature performer){
			if (performer.getPower() >= 2) return true;
			return false;
		};
		
		
	@Override
	public void onServerStarted() {
		ModActions.registerAction(new portalaction());
		ModActions.registerAction(new portaladdlocation());
		ModActions.registerAction(new portaleditaction());
		ModActions.registerAction(new addupkeepaction());
		ModActions.registerAction(new activateaction());
	    try
	    {
	      Connection con = ModSupportDb.getModSupportDb();
	      String sql = "";
	      
	      if (!ModSupportDb.hasTable(con, "ColdiePortals")) {
	        sql = "CREATE TABLE ColdiePortals (\t\tname\t\t\t\t\tVARCHAR(20)\t\tNOT NULL DEFAULT 'Unknown',"
	        		+ "\t\tposx\t\t\t\t\tINT\t\tNOT NULL DEFAULT 100,"
	        		+ "\t\tposy\t\t\t\t\tINT\t\tNOT NULL DEFAULT 100,"
	        		+ "\t\tbank\t\t\t\t\tINT\t\tNOT NULL DEFAULT 0,"
	        		+ "\t\titemid\t\t\t\t\tLONG\t\tNOT NULL DEFAULT 0)";
	        PreparedStatement ps = con.prepareStatement(sql);
	        ps.execute();
	        ps.close();
	        con.close();
	      }
	    }
	    catch (SQLException e)
	    {
	      e.printStackTrace();
	      throw new RuntimeException(e);
	    }
	    
	    try
	    {
	      Connection con2 = ModSupportDb.getModSupportDb();
	      String sql2 = "";
	      
	      if (!ModSupportDb.hasTable(con2, "ColdieGMPortals")) {
	        sql2 = "CREATE TABLE ColdieGMPortals (\t\tname\t\t\t\t\tVARCHAR(20)\t\tNOT NULL DEFAULT 'Unknown',"
	        		+ "\t\tposx\t\t\t\t\tINT\t\tNOT NULL DEFAULT 100,"
	        		+ "\t\tposy\t\t\t\t\tINT\t\tNOT NULL DEFAULT 100,"
	        		+ "\t\tbank\t\t\t\t\tINT\t\tNOT NULL DEFAULT 0,"
	        		+ "\t\titemid\t\t\t\t\tLONG\t\tNOT NULL DEFAULT 0)";
	        PreparedStatement ps2 = con2.prepareStatement(sql2);
	        ps2.execute();
	        ps2.close();
	        con2.close();
	      }
	    }
	    catch (SQLException e)
	    {
	      e.printStackTrace();
	      throw new RuntimeException(e);
	    }	    
	}
	    
	@Override
	public void preInit() {
		ModActions.init();
	}
	  public void onServerPoll()
	  {
		  pollportals.pollportal();
	  }	

}