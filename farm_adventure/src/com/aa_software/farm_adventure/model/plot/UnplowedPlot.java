package com.aa_software.farm_adventure.model.plot;

import com.aa_software.farm_adventure.model.Irrigation;

public class UnplowedPlot extends AbstractPlot{
	public static final String TEXTURE_NAME = "unplowed_plot";

	public UnplowedPlot() {
		super();
	}
	
	public UnplowedPlot(Irrigation irrigation) {
		super(irrigation);
	}
}
