package com.wurmonline.server.questions;


import com.wurmonline.server.behaviours.Action;
import com.wurmonline.server.behaviours.NoSuchActionException;
import com.wurmonline.server.creatures.Creature;
import com.wurmonline.server.villages.Village;
import com.wurmonline.server.zones.Zones;
import java.util.Properties;
import java.util.logging.Logger;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.gotti.wurmunlimited.modsupport.ModSupportDb;

import net.coldie.wurmunlimited.mods.portals.activateaction;
import net.coldie.wurmunlimited.mods.portals.portalmod;

import net.coldie.tools.BmlForm;



public class activatequestion extends Question
{
	private static Logger logger = Logger.getLogger(portalmod.class.getName());
	  private boolean properlySent = false;
	  
	  activatequestion(Creature aResponder, String aTitle, String aQuestion, int aType, long aTarget)
	  {
	    super(aResponder, aTitle, aQuestion, aType, aTarget);
	  }
	  
	  public activatequestion(Creature aResponder, String aTitle, String aQuestion, long aTarget)
	  {
	    super(aResponder, aTitle, aQuestion, 79, aTarget);
	  }
	  


	  public void answer(Properties answer)
	  {
		  int coins = portalmod.costtoactivate;
	    if (!properlySent) {
	      return;
	    }

	    // check drop down and accepted
	    boolean accepted = (answer.containsKey("accept")) && (answer.get("accept") == "true");	

	      if (accepted)
	    {
	    	  if (coins > getResponder().getMoney()){
	    		  getResponder().getCommunicator().sendNormalServerMessage("you don't have enough coins in bank for that");
	    		  return;
	    	  }

		      Connection dbcon = null;
		      PreparedStatement ps = null;
		      Village v = Zones.getVillage( getResponder().getTileX(), getResponder().getTileY(), true);
		      if (v != null){
		      
		      String duplicatevillage = "";
		      
	    	  try {
				getResponder().chargeMoney(coins);
				logger.info("Removing "+coins+" iron from "+getResponder().getName()
						+" for activating portal. portal wurmid "
				+portalmod.myMapportal.get("0"));
				getResponder().getCommunicator().sendNormalServerMessage("Removed "+coins+" iron from your bank to activate portal at "+v.getName());
				
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

		      
		      Connection dbcon3 = null;
		      PreparedStatement ps3 = null;
		      ResultSet rs3 = null;
		      try
		      {	      
		      dbcon3 = ModSupportDb.getModSupportDb();
		      ps3 = dbcon3.prepareStatement("SELECT * FROM ColdieGMPortals ORDER BY name");
		      rs3 = ps3.executeQuery();

		      while (rs3.next()) {
		    	  	if (rs3.getString("name").equals(v.getName())){
		    	  		
		    	  		duplicatevillage = " 2";
		    	  	}
		    	  	if (rs3.getString("name").equals(v.getName()+" 2")){
		    	  		
		    	  		duplicatevillage = " 3";
		    	  	}
		      }
		      rs3.close();
		      ps3.close();
		      dbcon3.close();
		    }
		      catch (SQLException e) {
		          throw new RuntimeException(e);
		        }		      

		      Connection dbcon2 = null;
		      PreparedStatement ps2 = null;
		      ResultSet rs2 = null;
		      try
		      {	      
		      dbcon2 = ModSupportDb.getModSupportDb();
		      ps2 = dbcon2.prepareStatement("SELECT * FROM ColdiePortals ORDER BY name");
		      rs2 = ps2.executeQuery();

		      while (rs2.next()) {
		    	  	if (rs2.getString("name").equals(v.getName())){
		    	  		
		    	  		duplicatevillage = " 2";
		    	  	}
		    	  	if (rs2.getString("name").equals(v.getName()+" 2")){
		    	  		
		    	  		duplicatevillage = " 3";
		    	  	}
		      }
		      rs2.close();
		      ps2.close();
		      dbcon2.close();
		    }
		      catch (SQLException e) {
		          throw new RuntimeException(e);
		        }	      
		      
		      
		      try
		      {
	  
		      dbcon = ModSupportDb.getModSupportDb();
		      ps = dbcon.prepareStatement("INSERT INTO ColdiePortals (name,posx,posy,itemid,bank) VALUES(?,?,?,?,?)");
		      ps.setString(1, v.getName()+duplicatevillage);
		      ps.setFloat(2,  getResponder().getPosX()/4);
		      ps.setFloat(3,  getResponder().getPosY()/4);
		      ps.setLong(4,  portalmod.myMapportal.get("0"));
		      ps.setInt(5, portalmod.activatebankamount);
		      ps.executeUpdate();
		      ps.close();
		      dbcon.close();
		      
		    }
		      catch (SQLException e) {
		          throw new RuntimeException(e);
		    }					    	
		      }else{
		      
		      getResponder().getCommunicator().sendNormalServerMessage("The portal must be on a deed");
			}
	    }
	      
	  }

	  public void sendQuestion()
	  {
	    boolean ok = true;
	    

	      try {
	        ok = false;
	        Action act = getResponder().getCurrentAction();
	        if (act.getNumber() == activateaction.actionId) {
	          ok = true;
	        }
	      }
	      catch (NoSuchActionException act) {
	        throw new RuntimeException("No such action", act);
	      }
	    
	    if (ok) {
	      properlySent = true;
	      
	      BmlForm f = new BmlForm("");
	      
	      f.addHidden("id", id+"");
	      f.addBoldText(getQuestion(), new String[0]);
	      f.addText("\nIt costs "+portalmod.costpermin+" iron per minute to keep portals active\n ", new String[0]);
	      f.addText("\n Activating this portal will cost you "+portalmod.costtoactivate+" iron from your bank, this will pay for activation and also "+portalmod.activatebankamount+" iron into this portals bank", new String[0]);

	      f.addText("\n\n\n\n ", new String[0]);
	      f.beginHorizontalFlow();
	 	  f.addButton("Activate portal now", "accept");
	      f.addText("               ", new String[0]);
	      f.addButton("Cancel", "decline");
	      
	      
	      f.endHorizontalFlow();
	      f.addText(" \n", new String[0]);
	      f.addText(" \n", new String[0]);
	      
	      getResponder().getCommunicator().sendBml(
	        300, 
	        350, 
	        true, 
	        true, 
	        f.toString(), 
	        150, 
	        150, 
	        200, 
	        title);
	    }
	  }

}