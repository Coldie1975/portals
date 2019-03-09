package com.wurmonline.server.questions;


import com.wurmonline.server.behaviours.Action;
import com.wurmonline.server.behaviours.NoSuchActionException;
import com.wurmonline.server.creatures.Creature;
import java.util.Properties;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.gotti.wurmunlimited.modsupport.ModSupportDb;

import net.coldie.wurmunlimited.mods.portals.portaladdlocation;
import net.coldie.wurmunlimited.mods.portals.portalmod;
import net.coldie.tools.BmlForm;

public class portaladdlocationquestion extends Question
{
	  private boolean properlySent = false;	  
	  portaladdlocationquestion(Creature aResponder, String aTitle, String aQuestion, int aType, long aTarget)
	  {
	    super(aResponder, aTitle, aQuestion, aType, aTarget);
	  }
	  
	  public portaladdlocationquestion(Creature aResponder, String aTitle, String aQuestion, long aTarget)
	  {
	    super(aResponder, aTitle, aQuestion, 79, aTarget);
	  }
	  
	  int g = 0;

	  public void answer(Properties answer)
	  {
	    if (!properlySent) {
	      return;
	    }
	    // check drop down and accepted
	    boolean accepted = (answer.containsKey("accept")) && (answer.get("accept") == "true");    
	    if (accepted)
	    {
		      Connection dbcon = null;
		      PreparedStatement ps = null;

		      try
		      {
	  
		      dbcon = ModSupportDb.getModSupportDb();
		      ps = dbcon.prepareStatement("INSERT INTO ColdieGMPortals (name,posx,posy,itemid,bank) VALUES(?,?,?,?,?)");
		      ps.setString(1, answer.getProperty("name"));
		      ps.setString(2, answer.getProperty("xcoords"));
		      ps.setString(3, answer.getProperty("ycoords"));
		      ps.setLong(4, portalmod.myMapportal.get("0"));
		      ps.setInt(5, 500);
		      
		      ps.executeUpdate();
		      ps.close();
		      
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
	        if (act.getNumber() == portaladdlocation.actionId) {
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

	      f.addText("Insert name here\t");
	      f.addInput("name", 20, "");
	      f.addText("\n", new String[0]);
	      f.addText("Insert x coords here\t");
	      f.addInput("xcoords", 10, (getResponder().getPosX()/4)+"");
	      f.addText("\n", new String[0]);
	      f.addText("Insert y coords here\t");
	      f.addInput("ycoords", 10, (getResponder().getPosY()/4)+"");
	      f.addText("\n", new String[0]);

	      f.addText("\n", new String[0]);
	      f.beginHorizontalFlow();
	 	  f.addButton("Insert now", "accept");
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