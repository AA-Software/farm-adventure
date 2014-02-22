package com.aa_software.farm_adventure.model.selectable.item.crop;

public class BananaCrop extends AbstractCrop {
	public static final String TEXTURE_NAME = "bananaCrop";
	public static final String SEED_NAME = "bananaSeed";
	public static final String BANANA_NAME = "Banana";
	public static final int DEFAULT_GROWTH_TIME = 60;
	public static final int DEFAULT_OUTPUT = 500;
	public static final int DEFAULT_COST = 20;
	public static final int DEFAULT_VALUE = 10;

	public BananaCrop() {
		this.growthTime = DEFAULT_GROWTH_TIME;
		this.output = DEFAULT_OUTPUT;
		this.cost = DEFAULT_COST;
		this.value = DEFAULT_VALUE;
		this.name = BANANA_NAME ;
	}

	public BananaCrop(int growthTime, int output, int cost, int value) {
		super();
	}
	
	@Override
	public String getTextureName() {
		return TEXTURE_NAME;
	}
	
	public String getSeedName() {
		return SEED_NAME;
	}

}
