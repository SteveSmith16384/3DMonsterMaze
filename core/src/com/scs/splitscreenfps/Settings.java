package com.scs.splitscreenfps;

import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;
import java.util.Random;

public class Settings {
	
	public static final int MODE_START = 0; // Started
	public static final int MODE_MONSTER_MAZE = 2; // Finished
	
	public static final boolean RELEASE_MODE = new File("../../debug_mode.tmp").exists() == false;

	public static int CURRENT_MODE = MODE_MONSTER_MAZE;//RELEASE_MODE ? MODE_START : MODE_QUANTUM_LEAGUE;
	public static final String VERSION = "1.2";
	
	// Debugging Hacks
	public static final boolean AUTO_START = !RELEASE_MODE && false;
	public static final boolean START_4_PLAYERS = !RELEASE_MODE && false;
	public static final boolean SMALL_MAP = !RELEASE_MODE && false;
	public static final boolean TEST_SCREEN_COORDS = !RELEASE_MODE && false;
	public static final boolean SHOW_FPS = !RELEASE_MODE && false;
	
	public static final boolean STRICT = !RELEASE_MODE && true;

	public static final float MIN_AXIS = 0.2f; // Movement less than this is ignored
	public static final float PLAYER_HEIGHT = 0.52f;
	public static final float CAM_OFFSET = 0.14f;
	
	public static String TITLE;

	public static final int WINDOW_WIDTH_PIXELS = RELEASE_MODE ? 1024 : 1024;
	public static final int WINDOW_HEIGHT_PIXELS = (int)(WINDOW_WIDTH_PIXELS * .68);

	public static Properties prop;
	
	public static Random random = new Random();

	private Settings() {

	}
	
	
	public static void init() {
		// load any settings file
		prop = new Properties();
		try {
			prop.load(new FileInputStream("config.txt"));
		} catch (Exception e) {
			//e.printStackTrace();
		}
		
		switch (CURRENT_MODE) {
		case MODE_MONSTER_MAZE:
			TITLE = "3D Monster Maze";
			break;
		default:
			TITLE = "[Unknown]";
			//throw new RuntimeException("Unknown Mode: " + CURRENT_MODE);
		}
		
	}

	
	public static final void p(String s) {
		System.out.println(s);
	}


	public static final void pe(String s) {
		System.err.println(s);
	}

}
