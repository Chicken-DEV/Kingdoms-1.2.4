package com.songoda.kingdoms.manager.task;

import com.songoda.kingdoms.constants.player.KingdomPlayer;

public class DelayedDeclineOfferTask implements Runnable{
	KingdomPlayer kp;
	
	public DelayedDeclineOfferTask(KingdomPlayer kp) {
		super();
		this.kp = kp;
	}

	@Override
	public void run() {
		if(kp.getInvited() == null) return;
		
		kp.sendMessage("You declined to join the kingdom ["+kp.getInvited().getKingdomName()+"].");
		kp.setInvited(null);
	}

}
