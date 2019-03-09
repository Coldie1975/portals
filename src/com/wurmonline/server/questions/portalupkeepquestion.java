package com.wurmonline.server.questions;


import com.wurmonline.server.behaviours.Action;
import com.wurmonline.server.behaviours.NoSuchActionException;
import com.wurmonline.server.creatures.Creature;
import com.wurmonline.server.economy.Economy;
import com.wurmonline.server.economy.Change;
import java.util.Properties;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.gotti.wurmunlimited.modsupport.ModSupportDb;

import net.coldie.wurmunlimited.mods.portals.addupkeepaction;
import net.coldie.wurmunlimited.mods.portals.portalmod;

import net.coldie.tools.BmlForm;



public class portalupkeepquestion extends Question
{
	
	  private boolean properlySent = false;
	  
	  portalupkeepquestion(Creature aResponder, String aTitle, String aQuestion, int aType, long aTarget)
	  {
	    super(aResponder, aTitle, aQuestion, aType, aTarget);
	  }
	  
	  public portalupkeepquestion(Creature aResponder, String aTitle, String aQuestion, long aTarget)
	  {
	    super(aResponder, aTitle, aQuestion, 79, aTarget);
	  }
	  


	  public void answer(Properties answer)
	  {
		  int coins = 0;
	    if (!properlySent) {
	      return;
	    }
	    if (Integer.parseInt(answer.getProperty("portalchoice")) == 0){
	    	  return;
	    }
	    
	    if (answer.getProperty("gold") != null && Integer.parseInt(answer.getProperty("gold")) >= 0)
	    {
	    	coins = Integer.parseInt(answer.getProperty("gold"))*1000000;
	    	
	    } 
	    
	    
	    if (answer.getProperty("silver") != null && Integer.parseInt(answer.getProperty("silver")) >= 0){
	    	coins = coins + (Integer.parseInt(answer.getProperty("silver"))*10000);
	    }
	    
	    if (coins == 0) 
	    {
	    	getResponder().getCommunicator().sendNormalServerMessage("You either had 0 or didn't do the numbers properly");
	    	return;
	    }
	    
	    // check drop down and accepted
	    boolean accepted = (answer.containsKey("accept")) && (answer.get("accept") == "true");	

	      if (accepted)
	    {
	    	  String mynumber = answer.getProperty("portalchoice");
	    	  if (coins > getResponder().getMoney()){
	    		  getResponder().getCommunicator().sendNormalServerMessage("you don't have enough coins in bank for that");
	    		  return;
	    	  }

	    	  try {
				getResponder().chargeMoney(coins);
				getResponder().getCommunicator().sendNormalServerMessage("Removed "+coins+" from your account");
				logger.info("Removing "+coins+" iron from "+getResponder().getName()
						+" for adding upkeep to portal. portal wurmid "
				+portalmod.myMapbank.get(mynumber));				
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

				
		      Connection dbcon2 = null;
		      PreparedStatement ps2 = null;
		      	
		      try
		      {	      
			      dbcon2 = ModSupportDb.getModSupportDb();
			      ps2 = dbcon2.prepareStatement("UPDATE ColdiePortals "
			      + "SET bank = bank + "+ coins
			      + " WHERE itemid = "+ portalmod.myMapbank.get(mynumber));
			      ps2.executeUpdate();
			      ps2.close();
			      dbcon2.close();

		    }
	      catch (SQLException e) {
	          throw new RuntimeException(e);
	        }	    	


			}
	      
	  }

	  public void sendQuestion()
	  {
	    boolean ok = true;
	    

	      try {
	        ok = false;
	        Action act = getResponder().getCurrentAction();
	        if (act.getNumber() == addupkeepaction.actionId) {
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
	      f.addText("\nIt costs "+portalmod.costpermin+" iron per minute \n ", new String[0]);

	      f.addRaw("harray{label{text='Portal choices you can add upkeep to'}dropdown{id='portalchoice';options=\"");
	      // use table data
	      
	      Connection dbcon = null;
	      PreparedStatement ps = null;
	      ResultSet rs = null;
	      portalmod.myMapbank.clear();
	      try
	      {	      
	      dbcon = ModSupportDb.getModSupportDb();
	      ps = dbcon.prepareStatement("SELECT * FROM ColdiePortals ORDER BY name");
	      rs = ps.executeQuery();
	      f.addRaw("--Choose Portal--,");
	      int i = 1;
	      while (rs.next()) {
	    	  portalmod.myMapbank.put(i+"", rs.getLong("itemid")+"");
	    	f.addRaw(rs.getString("name"));
	        f.addRaw(",");
	        i = i + 1;
	      }
	      rs.close();
	      ps.close();
	      dbcon.close();
	    }
	      catch (SQLException e) {
	    	  
	          throw new RuntimeException(e);
	        }
	      

		  f.addRaw("\"}}");
            long money = getResponder().getMoney();		  
	        Change change = Economy.getEconomy().getChangeFor(money);		  
	        long gold = change.getGoldCoins();
	        long silver = change.getSilverCoins();		  
		  
	        if (gold > 0L) {
	        	f.addRaw("harray{input{maxchars=\"10\";id=\"gold\";text=\"0\"};label{text=\"(" + gold + ") Gold coins\"}}");
	          }
	          if ((silver > 0L) || (gold > 0L)) {
	        	  f.addRaw("harray{input{maxchars=\"10\";id=\"silver\";text=\"0\"};label{text=\"(" + silver + ") Silver coins\"}}");
	          }	
	          
	      f.addText("\n\n\n\n ", new String[0]);
	      f.beginHorizontalFlow();
	 	  f.addButton("Add upkeep now", "accept");
	      f.addText("               ", new String[0]);
	      f.addButton("Cancel", "decline");
	      f.endHorizontalFlow(); 
	      f.addText("\n\nDeed name: portal bank in irons\n", new String[0]);	      
	      Connection dbcon2 = null;
	      PreparedStatement ps2 = null;
	      ResultSet rs2 = null;
	      try
	      {	      
	      dbcon2 = ModSupportDb.getModSupportDb();
	      ps2 = dbcon2.prepareStatement("SELECT * FROM ColdiePortals ORDER BY name");
	      rs2 = ps2.executeQuery();
	      while (rs2.next()) {
	    	f.addText(rs2.getString("name")+" : "+rs2.getInt("bank")+"\n", new String[0]);
	      }
	      rs2.close();
	      ps2.close();
	      dbcon2.close();
	    }
	      catch (SQLException e) {
	    	  
	          throw new RuntimeException(e);
	        }	      

	      f.addText(" \n", new String[0]);
	      f.addText(" \n", new String[0]);
	      
	      getResponder().getCommunicator().sendBml(
	        350, 
	        450, 
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