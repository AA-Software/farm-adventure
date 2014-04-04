package com.aa_software.farm_adventure.model.item.upgrade;

public class SteelToolsUpgrade extends CopperToolsUpgrade {
	public static final String TEXTURE_NAME = "steel_tools_upgrade";
	public static final float WORK_RATE_MOD = .6f;
	public static final String STEELTOOL_NAME = "Steel Upgrade";	
	public static int DEFAULT_COST = 30;
	public static int DEFAULT_VALUE = 15;
	
	public SteelToolsUpgrade() {
		this.cost = DEFAULT_COST;
		this.value = this.cost/2;
		this.name = STEELTOOL_NAME;
	}
}
