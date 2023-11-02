package me.bjtmastermind.i2ic;

import java.io.File;
import java.io.IOException;

import me.bjtmastermind.i2ic.indev.IndevLevel;
import me.bjtmastermind.i2ic.infdev.InfdevWorld;

public class CLI {

    public static void parseArgs(String[] args) {
        String inputFile = "";
        boolean gotInput = false;
        String outVersion = "";
        boolean gotOutput = false;

        for (int i = 0; i < args.length; i++) {
            if(args[i].startsWith("-i=") || args[i].startsWith("--input=")) {
                String check = args[i].split("=")[1];
                if(!gotInput && check.contains(".mclevel")) {
                    inputFile = args[i].split("=")[1];
                    gotInput = true;
                } else if(gotInput) {
                    System.err.println("Found duplicate flag \"-i\" or \"--input\"");
                    return;
                } else if(args.length > 2) {
                    System.err.println("Found to many flags!");
                    return;
                }
            }

            if(args[i].startsWith("-o=") || args[i].startsWith("--outputVersion=")) {
                String check = args[i].split("=")[1];
                if(!gotOutput && check.equals("infdev") || check.equals("beta") || check.equals("1.12.2")) {
                    outVersion = args[i].split("=")[1];
                    gotOutput = true;
                } else if(gotOutput) {
                    System.err.println("Found duplicate flag \"-o\" or \"--outputVersion\"");
                    return;
                }
            }
        }

        if(!inputFile.isEmpty() && !outVersion.isEmpty()) {
            runConvert(inputFile, outVersion);
        } else {
            System.err.println("Not all argument were given, or argument values are in the wrong place.");
        }
    }

    private static void runConvert(String inFile, String outType) {
        try {
            IndevLevel indevLevel = new IndevLevel(new File(inFile));

            if(outType.equals("infdev")) {
                System.out.println("To Infdev");

                InfdevWorld infdevWorld = new InfdevWorld(indevLevel);
                infdevWorld.generateChunkFiles();
                infdevWorld.generateLevelFile();

                System.out.println("Conversion of " + new File(inFile).getName() + " Complete!");
            } else if(outType.equals("beta")) {
                System.out.println("To Beta");
            } else if(outType.equals("1.12.2")) {
                System.out.println("To 1.12.2");
            }
        } catch(IOException e) {
            e.printStackTrace();
        }
    }
}
