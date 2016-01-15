package com.hutchison.mayday.crew;

import java.util.Random;

public class CrewMember {
	private String name;
	private int morale;
	private int hunger;
	private int health;

	private Boolean eat;

	private Boolean sick;
	private Boolean injured;
	private Boolean snapped;

	private Boolean dead;

	private int job;

	private int id;

	public Random r;

	private int stats[];

	private Boolean deathFlag;
	private Boolean snappedFlag;
	
	private String cod;

	/*
	 * Jobs 1 = Gen maintenance 2 = water maintenance 3 = heat maintenance 4 =
	 * Radio for Help 5 = Scavenge for Goods 6 = Rest 7 or above heal that id
	 * crew member if it is not themselves
	 */

	public CrewMember(String name, int id) {
		this.name = name;
		this.id = id;

		hunger = 0;
		morale = 100;
		health = 100;

		eat = true;

		sick = false;
		injured = false;
		snapped = false;

		dead = false;

		deathFlag = false;
		snappedFlag = false;

		r = new Random();

		// Gen Repair
		// water repair
		// heat repair
		// doctoring
		// scavenging
		
		cod = "";
		
		stats = new int[5];
		stats[0] = 5;
		stats[1] = 5;
		stats[2] = 5;
		stats[3] = 5;
		stats[4] = 5;

	}

	public int makeRoll(int stat) {
		int roll = CrewManager.r.nextInt(20) + 1;
		if (roll == 1) {
			return -1;
		} else {
			roll += stats[stat];
		}
		return roll;
	}

	// Getters and Setters

	public void setSick(Boolean s) {
		sick = s;
	}

	public void changeMoral(int i) {
		morale += i;
		if (morale > 100) {
			morale = 100;
		} else if (morale < 0) {
			morale = 0;

			if (snapped == false) {
				snapped = true;
				snappedFlag = true;
			}
		}
	}

	public void changeHealth(int i) {
		health += i;
		if (health > 100) {
			health = 100;
		} else if (health < 0) {
			health = 0;

			if (dead == false) {
				dead = true;
				deathFlag = true;
				cod = "succumb to their condition.";
			}

		}
	}

	public void starve() {
		if (!dead) {
			hunger += 20 + CrewManager.r.nextInt(20);

			if (hunger >= 100) {

				hunger = 100;

				if (dead == false) {
					dead = true;
					deathFlag = true;
					cod = "starved to death";
				}
			}
		}
	}

	public void feed() {
		if (!dead) {
			hunger -= 40 + CrewManager.r.nextInt(15);
			if (hunger < 0) {
				hunger = 0;
			}
		}
	}

	public void update(Boolean resting) {
		int roll;
		if (!snapped && !dead) {

			if (sick) {
				changeMoral(-3);
				changeHealth(-5);

				if (resting) {
					roll = CrewManager.r.nextInt(4);
					if (roll == 0) {
						sick = false;
					}
				}
			}

			if (injured) {
				changeMoral(-5);
				changeHealth(-25);
			}

			if (!sick && !injured) {
				if (resting) {
					changeMoral(5+r.nextInt(6));
					changeHealth(25);
				}
			}

			if (!resting) {
				changeMoral(0 - r.nextInt(10));
			}
		}

	}

	public String getName() {
		return name;
	}

	public int getID() {
		return id;
	}

	public int getHunger() {
		return hunger;
	}

	public int getMorale() {
		return morale;
	}

	public int getHealth() {
		return health;
	}

	public int getJob() {
		return job;
	}

	public Boolean getEat() {
		return eat;
	}

	public Boolean isGone() {
		if (dead || snapped) {
			return true;
		} else {
			return false;
		}
	}

	public Boolean isDead() {
		return dead;
	}

	public Boolean isSick() {
		return sick;
	}

	public Boolean isInjured() {
		return injured;
	}

	public Boolean isSnapped() {
		return snapped;
	}

	public Boolean getSnappedFlag() {
		if (snappedFlag) {
			snappedFlag = false;
			return true;
		}
		return false;
	}

	public Boolean getDeathFlag() {
		if (deathFlag) {
			deathFlag = false;
			return true;
		}
		return false;
	}
	
	public String getCOD(){
		return cod;
	}

	public void setJob(int j) {
		job = j;
	}

	public void setEat(Boolean e) {
		eat = e;
	}

	public void setStats(int[] stats) {
		if (stats.length == 5) {
			this.stats = stats;
		} else {
			return;
		}
	}

	public void setInjured(Boolean b) {
		injured = b;

	}

	public void kill() {
		dead = true;
	}

}
