package net.coldie.wurmunlimited.mods.portals;

import java.util.logging.Logger;

import org.gotti.wurmunlimited.modsupport.ModSupportDb;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;




public class pollportals  {
	private static Logger logger = Logger.getLogger(portalmod.class.getName());		
	 static long lastPoll = 0L;
	 static int pollFrequency = (60*60*1000);	//1 hour 
	 public static void pollportal(){
		 if (lastPoll + pollFrequency > System.currentTimeMillis()) {
	      return;
	     }
	     lastPoll = System.currentTimeMillis();
	      Connection dbcon2 = null;
	      PreparedStatement ps2 = null; 	
	      try
	      {	      
		      dbcon2 = ModSupportDb.getModSupportDb();
		      ps2 = dbcon2.prepareStatement("UPDATE ColdiePortals SET bank = bank - ? WHERE bank >= ?");
		      ps2.setInt(1, portalmod.costpermin*60);//updates every hour
		      ps2.setInt(2, portalmod.costpermin*60);
		      ps2.executeUpdate();
		      ps2.close();
		      dbcon2.close();
		      logger.info("Updated player portal upkeeps");
	    }
    catch (SQLException e) {
        throw new RuntimeException(e);
      }	

	 }
}