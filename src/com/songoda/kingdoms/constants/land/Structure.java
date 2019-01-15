package com.songoda.kingdoms.constants.land;

import org.apache.commons.lang.Validate;
import com.songoda.kingdoms.constants.StructureType;

public class Structure{
	private SimpleLocation loc;
	private StructureType type;

	public Structure(SimpleLocation loc, StructureType type) {
		super();
		Validate.notNull(loc);
		this.loc = loc;
		this.type = type;
	}
	public StructureType getType() {
		return type;
	}
	public void setType(StructureType type) {
		this.type = type;
	}
	public SimpleLocation getLoc() {
		return loc;
	}
	public void setLoc(SimpleLocation loc) {
		this.loc = loc;
	}
}
