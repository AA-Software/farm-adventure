package com.aa_software.farm_adventure.model.selectable.item.upgrade;

public class WaterPumpUpgrade extends AbstractUpgrade {
	public static final String TEXTURE_NAME = "water_pump_upgrade";
	public static final String WATERPUMP_NAME = "Water Pump";
	public static int DEFAULT_COST = 30;
	public static int DEFAULT_VALUE = 15;
	
	public WaterPumpUpgrade() {
		this.cost = DEFAULT_COST;
		this.value = DEFAULT_VALUE;
		this.name = WATERPUMP_NAME;
	}
}
