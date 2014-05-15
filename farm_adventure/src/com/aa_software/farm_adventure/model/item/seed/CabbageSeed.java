package com.aa_software.farm_adventure.model.item.seed;

import com.aa_software.farm_adventure.model.item.crop.CabbageCrop;

/**
 * Represents cabbage seeds, which will grow into a basket of cabbage.
 * 
 * @author Bebop
 * 
 */
public class CabbageSeed extends AbstractSeed {

	public static final int COST = 20;
	public static final int VALUE = 10;
	public static final float GROWTH_RATE = 5;
	public static final String NAME = "Cabbage Seed";
	public static final String DESCRIPTION = "Plant this seed and it will produce cabbage over time.";
	public static final String SEED_TEXTURE_NAME = "cabbageSeed";

	public CabbageSeed() {
		super(COST, VALUE, NAME, DESCRIPTION, GROWTH_RATE, 1,
				new CabbageCrop(), SEED_TEXTURE_NAME);
	}
}
