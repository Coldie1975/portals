package com.wurmonline.server.questions;


import com.wurmonline.server.behaviours.Action;
import com.wurmonline.server.behaviours.NoSuchActionException;
import com.wurmonline.server.creatures.Creature;


import java.util.Properties;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.gotti.wurmunlimited.modsupport.ModSupportDb;

import net.coldie.wurmunlimited.mods.portals.portalaction;
import net.coldie.wurmunlimited.mods.portals.portalmod;

import net.coldie.tools.BmlForm;



public class portalquestion extends Question
{
	//public HashMap<String, String> myMap = new HashMap<String,String>();

	  private boolean properlySent = false;
	  
	  portalquestion(Creature aResponder, String aTitle, String aQuestion, int aType, long aTarget)
	  {
	    super(aResponder, aTitle, aQuestion, aType, aTarget);
	  }
	  
	  public portalquestion(Creature aResponder, String aTitle, String aQuestion, long aTarget)
	  {
	    super(aResponder, aTitle, aQuestion, 79, aTarget);
	  }
	  


	  public void answer(Properties answer)
	  {
	    if (!properlySent) {
	      return;
	    }
	    if (Integer.parseInt(answer.getProperty("portalchoice")) == 0){
	    	  return;
	    }
	      
	    // check drop down and accepted
	    boolean accepted = (answer.containsKey("accept")) && (answer.get("accept") == "true");
	      float tx = 500;
	      float ty = 500;	
	      String name = "";

	      if (accepted)
	    {
	    	  
			    String mynumber = answer.getProperty("portalchoice");
		    	//getResponder().getCommunicator().sendNormalServerMessage(mynumber+" "+myMap.get(mynumber));

			      Connection dbcon2 = null;
			      PreparedStatement ps2 = null;
			      ResultSet rs2 = null; 
			    try
			      {	      
			      dbcon2 = ModSupportDb.getModSupportDb();
			      ps2 = dbcon2.prepareStatement("SELECT * FROM ColdieGMPortals");
			      rs2 = ps2.executeQuery();
			      
			      while (rs2.next()){
					if (rs2.getString("name").equals(portalmod.myMap.get(mynumber))){
			      name = portalmod.myMap.get(mynumber);
			      tx = rs2.getFloat("posx");
			      ty = rs2.getFloat("posy");
			      }
			      }
			      rs2.close();
			      ps2.close();
			      dbcon2.close();
			    }
			      catch (SQLException e) {
			          throw new RuntimeException(e);
			        }			    
			    
		      Connection dbcon = null;
		      PreparedStatement ps = null;
		      ResultSet rs = null; 
		    try
		      {	      
		      dbcon = ModSupportDb.getModSupportDb();
		      ps = dbcon.prepareStatement("SELECT * FROM ColdiePortals");
		      rs = ps.executeQuery();
		      while (rs.next()){
		    	  if (rs.getString("name").equals(portalmod.myMap.get(mynumber))){
		      name = portalmod.myMap.get(mynumber);
		      tx = rs.getFloat("posx");
		      ty = rs.getFloat("posy");
		      }
		      }
		      rs.close();
		      ps.close();
		      dbcon.close();
		    }
		      catch (SQLException e) {
		          throw new RuntimeException(e);
		        }
		    if (name == "") {
		    	return;
		    }
		    tx = (tx *4);
		    ty = (ty *4);
			int layer = 0;
			int floorLevel = getResponder().getFloorLevel();
			getResponder().getCommunicator().sendNormalServerMessage("Teleporting to: "+name+" "+(tx/4)+", "+(ty/4));
			getResponder().setTeleportPoints(tx, ty, layer, floorLevel);
			getResponder().startTeleporting();
			getResponder().getCommunicator().sendNormalServerMessage("You feel a slight tingle in your spine.");
			getResponder().getCommunicator().sendTeleport(false);
			getResponder().teleport(true);
			getResponder().stopTeleporting();
			}
	      
	  }

	  public void sendQuestion()
	  {
	    boolean ok = true;
	    

	      try {
	        ok = false;
	        Action act = getResponder().getCurrentAction();
	        if (act.getNumber() == portalaction.actionId) {
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
	      f.addText("\n ", new String[0]);
	      f.addRaw("harray{label{text='Current portal location choices'}dropdown{id='portalchoice';options=\"");
	      // use table data

	      Connection dbcon2 = null;
	      PreparedStatement ps2 = null;
	      ResultSet rs2 = null;
	      portalmod.myMap.clear();
	      int i = 1;	      
	      try
	      {	      
	      dbcon2 = ModSupportDb.getModSupportDb();
	      ps2 = dbcon2.prepareStatement("SELECT * FROM ColdieGMPortals ORDER BY name");
	      rs2 = ps2.executeQuery();
	      f.addRaw("--Choose Destination--,");

	      while (rs2.next()) {
	    	portalmod.myMap.put(i+"", rs2.getString("name"));
	    	f.addRaw("*"+rs2.getString("name")+"*");
	        f.addRaw(",");
	        i = i + 1;
	      }
	      rs2.close();
	      ps2.close();
	      dbcon2.close();
	    }
	      catch (SQLException e) {
	    	  
	          throw new RuntimeException(e);
	        }
	      	      
	      
	      
	      Connection dbcon = null;
	      PreparedStatement ps = null;
	      ResultSet rs = null;
	      try
	      {	      
	      dbcon = ModSupportDb.getModSupportDb();
	      ps = dbcon.prepareStatement("SELECT * FROM ColdiePortals WHERE bank >= ? ORDER BY name");
	      ps.setInt(1, portalmod.costpermin*60);//updates every hour
	      rs = ps.executeQuery();
	      while (rs.next()) {
	    	  portalmod.myMap.put(i+"", rs.getString("name"));
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

	      f.addText("\n\n\n\n ", new String[0]);
	      f.beginHorizontalFlow();
	 	  f.addButton("Portal now", "accept");
	      f.addText("               ", new String[0]);
	      f.addButton("Cancel", "decline");
	      
	      
	      f.endHorizontalFlow();
	      f.addText(" \n", new String[0]);
	      f.addText(" \n", new String[0]);
	      
	      getResponder().getCommunicator().sendBml(
	        300, 
	        250, 
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