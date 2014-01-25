package com.aa_software.farm_adventure.model.farm;

import com.aa_software.farm_adventure.model.item.crop.CarrotCrop;
import com.aa_software.farm_adventure.model.item.spell.IllusionistSpell;
import com.aa_software.farm_adventure.model.item.tool.ScytheTool;
import com.aa_software.farm_adventure.model.season.SpringSeason;

public class TutorialFarm extends AbstractFarm {
	
	public TutorialFarm() {
		super();
		for (int i = 0; i < seasons.length; i++) {
			seasons[i] = new SpringSeason();
		}
		
		cropMap.put(new CarrotCrop(), 5);
		equipmentMap.put(new ScytheTool(), 1);
		spellMap.put(new IllusionistSpell(), 1);
	}
}
