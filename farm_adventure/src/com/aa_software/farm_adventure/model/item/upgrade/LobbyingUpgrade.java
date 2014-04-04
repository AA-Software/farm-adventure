package com.aa_software.farm_adventure.model.item.upgrade;

public class LobbyingUpgrade extends AbstractUpgrade {
	public static final String TEXTURE_NAME = "lobbying_upgrade";
	public static final String LOBBYING_NAME = "Lobbying";	
	public static int DEFAULT_COST = 30;
	public static int DEFAULT_VALUE = 15;
	
	public LobbyingUpgrade() {
		this.cost = DEFAULT_COST;
		this.value = this.cost/2;
		this.name = LOBBYING_NAME;
	}
}
