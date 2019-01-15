package com.songoda.kingdoms.utils;

import java.util.Random;

public class ProbabilityTool {
	
	public static boolean testProbability100(int prob){
		if(randInt(0,100) <= prob) return true;
		
		return false;
	}
	

	public static int randInt(int min, int max) {

	    Random rand = new Random();

	    int randomNum = rand.nextInt((max - min) + 1) + min;

	    return randomNum;
	}
}
