package me.bjtmastermind.i2ic;

import java.io.IOException;

public class Main {
	public static int userType = -1;
	
	public static void main(String[] args) throws IOException, InterruptedException {
		if(args.length > 0) {
			userType = 0;
			CLI.getArgs(args);
		} else {
			userType = 1;
			GUI.open();
		}
	}
}
