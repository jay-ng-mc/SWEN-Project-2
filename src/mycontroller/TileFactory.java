package mycontroller;

import tiles.*;

public class TileFactory {
	
	public TrapTile getTrapTile(String mapType){
	      if(mapType == null){
	         return null;
	      }		
	      if(mapType.equalsIgnoreCase("lava")){
	         return new LavaTrap();
	         
	      } else if(mapType.equalsIgnoreCase("health")){
	         return new HealthTrap();
	         
	      } else if(mapType.equalsIgnoreCase("water")){
	         return new WaterTrap();
	      } else if(mapType.equalsIgnoreCase("parcel")){
	    	  return new ParcelTrap();
	      }
	      
	      return null;
	   }
}
