package com.songoda.kingdoms.utils;

import java.util.Date;

import org.bukkit.entity.Arrow;

public class TimedArrow {
	private Arrow arrow;
	private Long shootTime;
	public TimedArrow(Arrow arrow) {
		super();
		this.arrow = arrow;
		this.shootTime = new Date().getTime();
	}
	public Arrow getArrow() {
		return arrow;
	}
	public Long getShootTime() {
		return shootTime;
	}
}
