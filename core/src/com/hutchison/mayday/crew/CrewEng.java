package com.hutchison.mayday.crew;

public class CrewEng extends CrewMember{
	public CrewEng(){
		super("Sparks",2);
		
		int stats[] = {
				7,7,7,0,5
		};
		
		super.setStats(stats);
		
	}
}
