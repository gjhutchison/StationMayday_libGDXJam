package com.hutchison.mayday.station;

import java.util.Random;

import com.hutchison.mayday.crew.CrewManager;

public class Station {

	/*
	 * Jobs 1 = Gen maintenance 2 = water maintenance 3 = heat maintenance 4 =
	 * Radio for Help 5 = Scavenge for Goods 6 = heal
	 */

	// HP or Quality of each aspect

	private int water;
	private int gen;
	private int heat;
	private int radioHelpChance;

	private int genThresh;
	private int waterThresh;
	private int heatThresh;

	private int scavThresh;

	private Boolean genMaintained;
	private Boolean waterMaintained;
	private Boolean heatMaintained;

	private Boolean onRadio;
	private Boolean powerFailure;

	private Boolean radiationLeak;
	private Boolean saved;

	private Boolean helpFound;
	private int daysTillRescue;

	private Random r;

	private CrewManager crewManager;

	private String waterLog;
	private String heatLog;
	private String genLog;

	private String jobLog;

	private String eventLog;

	private int radiationDay;

	public Station(CrewManager cm) {
		water = 100;
		gen = 100;
		heat = 100;
		radioHelpChance = 0;

		onRadio = false;
		helpFound = false;

		daysTillRescue = -1;

		crewManager = cm;

		waterLog = "";
		heatLog = "";
		genLog = "";
		eventLog = "";
		jobLog = "WORK:\nNo one has done anything yet";

		r = new Random();

		radiationDay = r.nextInt(3) + 29;

		// radiationDay = 69;

		genThresh = 16;
		waterThresh = 17;
		heatThresh = 16;

		scavThresh = 17;

		genMaintained = false;
		waterMaintained = false;
		heatMaintained = false;

		powerFailure = false;

		radiationLeak = false;
		saved = false;
	}

	public void updateStation() {

		waterLog = "";
		heatLog = "";
		genLog = "";
		jobLog = "WORK:\n";
		eventLog = "";

		radiationDay--;

		if (radiationDay <= 0) {
			radiationLeak = true;
			return;
		}

		Boolean noWork = true;

		int roll = 0;

		for (int i = 0; i < 5; i++) {
			int job = crewManager.getJob(i);
			int bonus = 0;
			String name = crewManager.getName(i);

			// fix generator
			if (job == 1) {
				noWork = false;
				roll = crewManager.requestRoll(i, 0);
				if (powerFailure) {

					if (roll >= genThresh) {
						powerFailure = false;
						gen = 100;
						jobLog += name + " fixed the power failure\n";
					} else {
						jobLog += name + " failed to fix the power failure\n";
					}

				} else {
					if (roll >= genThresh) {

						if (gen > 85) {
							jobLog += name + " found nothing wrong with the generator\n";
						} else {
							bonus = crewManager.requestRoll(i, 0) / 2;
							if (bonus < 0) {
								bonus = 0;
							}

							gen += 10 + bonus;
							genMaintained = true;
							jobLog += name + " worked on the generator\n";
						}

					} else if (roll == -1) {
						jobLog += name + " zapped themselves while working on the generator\n";

						crewManager.injure(i, true);

					} else {
						jobLog += name + " didn't accomplish anything with the generator\n";
					}

				}

			} else if (job == 2) {
				noWork = false;
				roll = crewManager.requestRoll(i, 1);
				if (roll >= waterThresh) {

					if (water > 85) {
						jobLog += name + " found nothing wrong with the filter\n";
					} else {
						bonus = crewManager.requestRoll(i, 1) / 2;
						if (bonus < 0) {
							bonus = 0;
						}

						water += 10 + bonus;

						waterMaintained = true;
						jobLog += name + " worked on the water filter\n";
					}

				} else if (roll == -1) {
					jobLog += name + " caught their hand in some gears while working on the filter.\n";

					crewManager.injure(i, true);

				} else {
					jobLog += name + " didn't accomplish anything with the water filter\n";
				}
			} else if (job == 3) {
				noWork = false;
				roll = crewManager.requestRoll(i, 2);

				if (roll >= heatThresh) {

					if (heat > 85) {
						jobLog += name + " found nothing wrong with the heater\n";
					} else {
						bonus = crewManager.requestRoll(i, 2) / 2;
						if(bonus < 0){
							bonus = 0;
						}
						heat += 10 + bonus;

						heatMaintained = true;

						jobLog += name + " worked on the heater\n";
					}

				} else if (roll == -1) {
					jobLog += name + " burned themselves while working on the heater\n";

					crewManager.injure(i, true);

				} else {
					jobLog += name + " didn't accomplish anything with the heater\n";
				}
			} else if (job == 4) {
				if (powerFailure) {
					jobLog += "The radio is unusable with the power out\n";
					onRadio = false;

				} else {
					onRadio = true;
					if (helpFound) {
						daysTillRescue -= 1;

						if (daysTillRescue == 0) {
							saved = true;
							jobLog = "";
							return;
						}

					}
				}
			}

			else if (job == 5) {
				noWork = false;
				roll = crewManager.requestRoll(i, 4);

				if (roll >= scavThresh) {

					bonus = +(crewManager.requestRoll(i, 4) / 9);
					if (bonus < 0) {
						bonus = 0;
					}

					int crewScale = crewManager.crewAlive() - 1;
					if (crewScale <= 1) {
						crewScale = 2;
					}

					int rationsAdded = r.nextInt(crewScale) + 1 + bonus;

					crewManager.addRations(rationsAdded);

					jobLog += name + " found " + rationsAdded + " rations\n";

				} else if (roll == -1) {
					jobLog += name + " was injured while looking for rations\n";

					crewManager.injure(i, true);

				} else {
					jobLog += name + " didn't find any food\n";
				}
			}

			else if (job >= 6) {
				noWork = false;
				roll = crewManager.requestRoll(i, 3);

				if (roll >= 15) {
					crewManager.injure(job - 6, false);
					jobLog += name + " healed " + crewManager.getName(job - 6) + "\n";
				} else {
					jobLog += name + " failed to heal " + crewManager.getName(job - 6) + "\n";
				}
			}

		}

		if (noWork) {
			jobLog += "Nobody did any work\n";
		}

		// see if help was found

		// powerFailure = true;

		if (!helpFound && onRadio) {
			if (!powerFailure) {
				roll = r.nextInt(100);
				if (roll < radioHelpChance) {
					helpFound = true;
					daysTillRescue = r.nextInt(radiationDay / 2) + 4;
					eventLog += "Success! the crew managed to contact a rescue ship. It will be here in "
							+ daysTillRescue + " days " + "but will need constant radio contact to locate you.\n";
				} else {
					radioHelpChance += r.nextInt(4) + 1;
				}
			}
		}

		if (powerFailure == false) {
			if (!genMaintained) {
				gen -= r.nextInt(20);

				if (r.nextInt(61) > gen) {
					powerFailure = true;
				}

				if (gen < 1) {
					gen = 1;
					// powerFailure = true;
					eventLog += "The power has failed\n";

				}
			}
			if (!waterMaintained) {
				water -= r.nextInt(20);
				if (water < 1) {
					water = 1;
				}
			}
			if (!heatMaintained) {
				heat -= r.nextInt(20);
				if (heat < 1) {
					heat = 1;
				}
			}
		}

		// RANDOM EVENTS

		roll = r.nextInt(100);
		if (roll < 20) {
			roll = r.nextInt(2);
			// BAD EVENT
			if (roll == 0) {
				roll = 0;
				roll = r.nextInt(6);

				switch (roll) {
				// Water malfunction
				case 0:
					eventLog += "The water filter made a strange noise and now is not working right.\n";
					water -= 30;

					break;
				// Sick
				case 1:
					eventLog += "There is a stomach bug going around that has made some of the crew sick.";

					for (int i = 0; i <= r.nextInt(5); i++) {
						crewManager.setSick(r.nextInt(5), true);
					}

					break;

				// Injury
				case 2:
					roll = crewManager.pickRandomAlive();

					crewManager.injure(roll, true);
					eventLog += "The ship rumbles and debris falls from the ceiling landing on "
							+ crewManager.getName(roll) + " injuring them\n";

					break;
				case 3:

					if (crewManager.getRations() >= 1) {

						eventLog += "Some rations have disappeared during the night.\n";
						crewManager.addRations(0 - r.nextInt(3) - 1);
					}

					break;
				case 4:
					crewManager.changeCrewMorale(-15);
					eventLog += "A large argument breaks out between the crew members.\n";
					break;
				case 5:
					gen -= 30;
					eventLog += "The generator makes an odd clunking noise\n";
					break;
				}

			}
			// GOOD EVENT
			else {
				roll = 0;
				roll = r.nextInt(6);

				switch (roll) {
				// Water Working fine
				case 0:
					eventLog += "The water filter seems to be working better for no reason.\n";
					water += 30;

					break;
				// extra food
				case 1:
					eventLog += "Some extra rations were found in the room that no one noticed before.\n";

					crewManager.addRations(r.nextInt(4) + 1);

					break;

				// Happy
				case 2:
					crewManager.changeCrewMorale(25);
					int n = crewManager.pickRandomAlive();
					eventLog += crewManager.getName(n) + " tells a fun joke.\n";

					break;
				case 3:
					eventLog += "The crew remembers a better time.\n";
					crewManager.changeCrewMorale(10);

					break;
				case 4:

					eventLog += "The generator hums along happily.\n";
					gen += 30;
					powerFailure = false;

					break;
				case 5:

					eventLog += "The heater springs to life warming the room.\n";
					heat += 30;

					break;

				}
			}
		}

		if (powerFailure) {
			crewManager.updateCrew(1, 1, powerFailure);
		} else {
			crewManager.updateCrew(water, heat, powerFailure);
		}

	}

	public void resetJobs() {
		onRadio = false;
		genMaintained = false;
		waterMaintained = false;
		heatMaintained = false;

		jobLog = "WORK:\n";
		eventLog = "\n";
	}

	public Boolean getSaved() {
		return saved;
	}

	public boolean getRadiationLeak() {
		return radiationLeak;
	}

	public String requestStationStatus() {

		String radioLog = "";

		if (radiationLeak) {
			eventLog = "Radiation has leaked throughout the station killing everyone.\n";

			crewManager.killAll();

			return eventLog;
		}

		if (saved) {
			eventLog = "A small rescue vessle pulls up next to the station and saves the crew.\n";

			return eventLog;
		}

		if (crewManager.crewAlive() == 0) {
			eventLog = "Everybody's dead, Dave.\n";

			return eventLog;
		}

		// Radio Messages
		if (!helpFound && !onRadio) {
			radioLog += "The radio sits silently\n";
		} else if (helpFound && !onRadio) {
			radioLog += "The rescue ship is waiting for guidence\n";
		} else if (!helpFound && onRadio) {
			radioLog += "No contact was made with anyone who could help.\n";
		}

		if (helpFound) {
			radioLog += "With guidence the rescue ship will arrive in " + daysTillRescue + " days\n";
		}

		if (powerFailure) {
			genLog += "The Generator has failed\n";
		} else if (gen < 85 && gen >= 50) {
			genLog += "The lights are flickering\n";
		} else if (gen < 50 && gen >= 35) {
			genLog += "There are rolling blackouts\n";
		} else if (gen < 35) {
			genLog += "The Power will soon fail\n";
		}

		// Water Messages
		if (water < 40 || powerFailure) {
			waterLog += "Someone will get sick from drinking the water\n";
		} else if (water < 85 && water >= 60) {
			waterLog += "The water is starting to look murky\n";
		} else if (water < 60 && water >= 40) {
			waterLog += "The water smells off\n";
		}

		// Heater Messages
		if (heat < 30 || powerFailure) {
			heatLog += "Ice is forming on the windows, the cold is very uncomfortable.\n";
		} else if (heat < 85 && heat >= 50) {
			heatLog += "The room feels chilly\n";
		} else if (heat < 50 && heat >= 30) {
			heatLog += "The crew can see their breath\n";
		}

		return "\nSTATION:\n" + genLog + waterLog + heatLog + radioLog + "\n" + jobLog + "\n" + eventLog
				+ "\n\nRations Remaining: " + crewManager.getRations();
	}

	public String getStationStats() {
		return "GEN: " + gen + " WATER: " + water + " HEAT: " + heat + " help chance: " + radioHelpChance;
	}

}
