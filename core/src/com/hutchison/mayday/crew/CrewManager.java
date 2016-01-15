package com.hutchison.mayday.crew;

import java.util.Random;

public class CrewManager {
	public static Random r;

	private CrewMember crew[] = { new CrewCapt(), new CrewDoc(), new CrewEng(), new CrewJan(), new CrewSec() };

	private String moraleLog;
	private String snappedLog;

	private String healthLog;
	private String deathLog;

	private String hungerLog;
	private String starveLog;

	private final int DOC = 1;

	private int rations;

	/*
	 * Jobs 1 = Gen maintenance 2 = water maintenance 3 = heat maintenance 4 =
	 * Radio for Help 5 = Scavenge for Goods 6 or above heal that id crew member
	 * if it is not themselves
	 * 
	 * 
	 * 
	 * 
	 */

	public CrewManager() {
		r = new Random();

		moraleLog = "MORALE:\n";
		snappedLog = "";

		healthLog = "HEALTH:\n";
		deathLog = "";

		starveLog = "HUNGER:\n";
		hungerLog = "";

		rations = 6;
	}

	public void setJob(int id, int job) {

		// For self resting
		if (id == job - 6) {
			crew[id].setJob(0);
			return;
		}

		// For Doctoring
		if (id == DOC && job > 5) {
			crew[DOC].setJob(job);
			crew[job - 6].setJob(0);
			// return;
		}

		// Inturrupt healing if being healed
		if (crew[DOC].getJob() == id + 6) {
			crew[DOC].setJob(0);
			// crew[id].setJob(job);
			// return;
		}

		for (int i = 0; i < crew.length; i++) {
			if (crew[i].getJob() == job) {
				crew[i].setJob(0);
				// crew[id].setJob(job);
				// return;
			}
		}

		crew[id].setJob(job);

	}

	public void updateCrew(int water, int heat, Boolean powerOut) {

		moraleLog = "MORALE:\n";
		snappedLog = "";

		healthLog = "HEALTH:\n";
		deathLog = "";

		starveLog = "HUNGER:\n";
		hungerLog = "";

		// Handle water effects
		int roll;
		int heatDecayMoral[] = { 2, -2, -4, -8 };
		float sickWaterChanceBase = 5f;

		// Water effects on crew
		if (water <= 85) {
			float sickChance = sickWaterChanceBase * (85 / water);

			roll = r.nextInt(100) + 1;
			if (roll < sickChance) {
				crew[r.nextInt(5)].setSick(true);
			}
		}

		// Heat effects on crew
		if (heat < 85) {
			if (heat < 50) {
				if (heat < 30) {
					changeCrewMorale(heatDecayMoral[3]);
				} else {
					changeCrewMorale(heatDecayMoral[2]);
				}
			} else {
				changeCrewMorale(heatDecayMoral[1]);
			}
		} else {
			changeCrewMorale(heatDecayMoral[0]);
		}

		for (int i = 0; i < crew.length; i++) {
			Boolean willEat;

			willEat = crew[i].getEat();
			if (!crew[i].isGone()) {
				if (rations > 0 && willEat) {
					hungerLog += crew[i].getName() + " has eaten\n";
					crew[i].changeMoral(10);
					crew[i].feed();
					rations--;
				} else {
					crew[i].changeMoral(-10);
					crew[i].starve();
				}
			}

		}

		for (int i = 0; i < crew.length; i++) {
			Boolean resting = false;

			if (crew[i].getJob() == 0) {
				resting = true;
			}

			if (!crew[i].isGone()) {
				crew[i].update(resting);

			}

		}

		Boolean issues = true;

		while (issues) {

			issues = false;

			for (int i = 0; i < crew.length; i++) {

				if (crew[i].getDeathFlag()) {
					changeCrewMorale(-15);
					deathLog += crew[i].getName() + " " + crew[i].getCOD() + "\n";
					issues = true;
				}

				if (crew[i].getSnappedFlag()) {
					snappedLog += crew[i].getName() + " has snapped and run away\n";
					issues = true;
				}
			}
		}
	}

	public int getRations() {
		return rations;
	}

	public void addRations(int i) {
		rations += i;

		if (rations < 0) {
			rations = 0;
		}
	}

	public void resetJobs() {
		for (int i = 0; i < crew.length; i++) {
			crew[i].setJob(0);
		}
	}

	public void changeIdvMorale(int id, int m) {
		if (!crew[id].isGone()) {
			crew[id].changeMoral(m);
		}

	}

	public void injure(int id, Boolean b) {
		if (!crew[id].isGone()) {
			crew[id].setInjured(b);
		}
	}

	public void changeCrewMorale(int m) {
		for (int i = 0; i < crew.length; i++) {
			if (!crew[i].isGone()) {
				crew[i].changeMoral(m);
			}
		}
	}

	public void setSick(int id, Boolean b) {
		if (!crew[id].isGone()) {
			crew[id].setSick(b);
		}
	}

	public int pickRandomAlive() {
		int num = r.nextInt(crewAlive());

		for (int i = 0; i < crew.length; i++) {
			if (!crew[i].isGone()) {
				if (num > 1) {
					num--;
				} else {
					return i;
				}
			}
		}

		return -1;

	}

	public int requestRoll(int id, int stat) {
		return crew[id].makeRoll(stat);
	}

	public String getName(int id) {
		return crew[id].getName();
	}

	public int getJob(int id) {
		return crew[id].getJob();
	}

	public int crewAlive() {
		int count = 0;
		for (int i = 0; i < crew.length; i++) {
			if (!crew[i].isGone() && !crew[i].isSnapped()) {
				count++;
			}
		}
		return count;
	}

	public String getJobDesc(int id) {
		int jerb = crew[id].getJob();

		String s = "";

		switch (jerb) {
		case 0:
			s = "Rest\n";
			break;
		case 1:
			s = "Fix Gen.\n";
			break;
		case 2:
			s = "Fix Filt.\n";
			break;
		case 3:
			s = "Fix Heat\n";
			break;
		case 4:
			s = "Radio Help\n";
			break;
		case 5:
			s = "Scavenge\n";
			break;
		default:
			break;
		}

		if (jerb > 5) {
			s = "Heal " + crew[jerb - 6].getName().charAt(0) + crew[jerb - 6].getName().charAt(1) + ".";
		}

		return s;
	}

	public int getEat(int id) {
		if (crew[id].getEat()) {
			return 0;
		} else {
			return 1;
		}
	}

	public Boolean isGone(int id) {
		return crew[id].isGone();
	}

	public Boolean isSnapped(int id) {
		return crew[id].isSnapped();
	}

	public Boolean isDead(int id) {
		return crew[id].isDead();
	}

	public Boolean isSick(int id) {
		return crew[id].isSick();
	}

	public Boolean isInjured(int id) {
		return crew[id].isInjured();
	}

	public Boolean jobFree(int job) {
		for (int i = 0; i < crew.length; i++) {
			if (crew[i].getJob() == job) {
				return false;
			}
		}

		return true;
	}

	public void toggleEat(int id) {
		if (crew[id].getEat()) {
			crew[id].setEat(false);
		} else {
			crew[id].setEat(true);
		}

	}

	public String requestCrewReport() {

		Boolean issues = false;
		int morale;
		for (int i = 0; i < 5; i++) {
			if (crew[i].isGone() == false) {

				morale = crew[i].getMorale();
				if (morale < 85) {
					issues = true;

					if (morale <= 84 && morale > 60) {
						moraleLog += crew[i].getName() + " appears to be uneasy\n";
					} else if (morale <= 60 && morale > 50) {
						moraleLog += crew[i].getName() + " is very agitated\n";
					} else if (morale <= 50 && morale > 35) {
						moraleLog += crew[i].getName() + " is lashing out at others\n";
					} else if (morale <= 35 && morale > 10) {
						moraleLog += crew[i].getName() + " has almost lost all hope\n";
					} else if (morale <= 10) {
						moraleLog += crew[i].getName() + " is about to break\n";
					}
				}
			}

		}

		if (issues == false) {
			moraleLog += "No one is too shaken at the moment.\n";
		}

		issues = false;

		int health;
		for (int i = 0; i < 5; i++) {

			if (crew[i].isGone() == false) {

				health = crew[i].getHealth();
				if (crew[i].isSick()) {
					healthLog += crew[i].getName() + " is sick\n";
					issues = true;
				}
				if (crew[i].isInjured()) {
					healthLog += crew[i].getName() + " is injured\n";
					issues = true;
				}
				if (health < 35) {
					healthLog += crew[i].getName() + " is close to death\n";
					issues = true;
				}

			}
		}

		if (issues == false) {
			healthLog += "No one is currently sick or injured\n";
		}

		// Hunger log

		issues = false;

		for (int i = 0; i < 5; i++) {
			if (crew[i].getHunger() > 50 && !crew[i].isGone()) {
				starveLog += crew[i].getName() + " is starving\n";
				issues = true;
			}
		}

		if (!issues) {
			starveLog += "No one is starving\n";
		}

		String s = moraleLog + snappedLog + "\n" + healthLog + deathLog + "\n" + starveLog + hungerLog;

		return s;
	}

	public void killAll() {
		for (int i = 0; i < crew.length; i++) {
			crew[i].kill();
		}
	}

	public String getCrewStats() {
		String s = "";

		for (int i = 0; i < crew.length; i++) {
			s += crew[i].getName() + " hunger: " + crew[i].getHunger() + " morale: " + crew[i].getMorale() + " dead: "
					+ crew[i].isDead() + " snapped: " + crew[i].isSnapped() + "\n";
		}
		return s;
	}

}
