package com.songoda.kingdoms.constants.land;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

/**
 * <p>represent location in simple form</p>
 * <p>use Bukkit Location in order to store exact location as this class only store block location</p>
 *
 * @author wysohn
 */
public class SimpleLocation {
  @Getter
  @Setter
  private String world;
  @Getter
  @Setter
  private int x;
  @Getter
  @Setter
  private int y;
  @Getter
  @Setter
  private int z;
  @Getter
  private Location location;

  /*	private double pitch;
	  private double yaw;*/
  public SimpleLocation(Location loc){
	this.world = loc.getWorld().getName();
	this.x = loc.getBlockX();
	this.y = loc.getBlockY();
	this.z = loc.getBlockZ();
	this.location = loc;
/*		this.pitch = loc.getPitch();
		this.yaw = loc.getYaw();*/
  }

  public SimpleLocation(String world, int x, int y, int z/*, float pitch, float yaw*/){
	Validate.notNull(world);

	this.world = world;
	this.x = x;
	this.y = y;
	this.z = z;
	this.location = new Location(Bukkit.getWorld(world), x, y, z);

/*		this.pitch = pitch;
		this.yaw = yaw;*/
  }
	
/*	public SimpleLocation(String world, int x, int y, int z, double pitch, double yaw) {
		super();
		this.world = world;
		this.x = x;
		this.y = y;
		this.z = z;
		this.pitch = pitch;
		this.yaw = yaw;
	}*/

  public Location toLocation(){
	Location loc = new Location(Bukkit.getWorld(world), x, y, z);
/*		loc.setPitch((float) pitch);
		loc.setYaw((float) yaw);*/
	return loc;
  }

  public SimpleChunkLocation toSimpleChunk(){
	SimpleChunkLocation sc = new SimpleChunkLocation(world, x >> 4, z >> 4);

	return sc;
  }

  /*	public float getPitch() {
		  return (float) pitch;
	  }
	  public void setPitch(float pitch) {
		  this.pitch = pitch;
	  }
	  public float getYaw() {
		  return (float) yaw;
	  }
	  public void setYaw(float yaw) {
		  this.yaw = yaw;
	  }*/
  public SimpleLocation clone(){
	return new SimpleLocation(world, x, y, z/*, pitch, yaw*/);
  }


  @Override
  public int hashCode(){
	final int prime = 31;
	int result = 1;
	result = prime * result + x;
	result = prime * result + y;
	result = prime * result + z;
	return result;
  }

  @Override
  public boolean equals(Object obj){
	if(this == obj)
	  return true;
	if(obj == null)
	  return false;
	if(getClass() != obj.getClass())
	  return false;
	SimpleLocation other = (SimpleLocation) obj;
	if(world == null){
	  if(other.world != null)
		return false;
	}
	else if(!world.equals(other.world))
	  return false;
	if(x != other.x)
	  return false;
	if(y != other.y)
	  return false;
	if(z != other.z)
	  return false;
	return true;
  }

  @Override
  public String toString(){
	return world + " , " + x + " , " + y + " , " + z /*+ " , " + pitch + " , "+ yaw*/;
  }

  public static String locToStr(Location loc){
	if(loc == null) return "";

	String world = loc.getWorld().getName();
	double x = loc.getX();
	double y = loc.getY();
	double z = loc.getZ();
	float pitch = loc.getPitch();
	float yaw = loc.getYaw();

	return world + " , " + x + " , " + y + " , " + z; /*+ " , " + pitch + " , "+ yaw;*/
  }

  //world , 839.0 , 66.0 , -728.0 , 0.0 , 0.0
  public static Location strToLoc(String loc){
	if(loc == null) return null;

	String[] split = loc.replaceAll(" ", "").split(",");
	if(split.length != 4 && split.length != 6){
	  return null;
	}

	World world = Bukkit.getWorld(split[0]);
	if(world == null)
	  return null;

	double x = Double.parseDouble(split[1]);
	double y = Double.parseDouble(split[2]);
	double z = Double.parseDouble(split[3]);
/*		float pitch = Float.parseFloat(split[4]);
		float yaw = Float.parseFloat(split[5]);*/

	/*		location.setPitch(pitch);
		location.setYaw(yaw);*/

	return new Location(world, x, y, z);
  }

  //world , -237 , 42 , -653 , 0 , 0 , -15 , -41 , world
  public static Location oldComplexStrToLoc(String reverseChunk){
	if(reverseChunk == null) return null;

	String[] split = reverseChunk.replaceAll(" ", "").split(",");

	World world = Bukkit.getWorld(split[0]);
	if(world == null)
	  return null;

	double x = Double.parseDouble(split[1]);
	double y = Double.parseDouble(split[2]);
	double z = Double.parseDouble(split[3]);
	float pitch = Float.parseFloat(split[4]);
	float yaw = Float.parseFloat(split[5]);
		
/*		int Chunkx = Integer.parseInt(split[6]);
		int Chunkz = Integer.parseInt(split[7]);
		World world = Bukkit.getWorld(split[8]);*/

	//Chunk c = world.getChunkAt(x, z);

	Location loc = new Location(world, x, y, z);
	loc.setPitch(pitch);
	loc.setYaw(yaw);

	return loc;
  }
}
