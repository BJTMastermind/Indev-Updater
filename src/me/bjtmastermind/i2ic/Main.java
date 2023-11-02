package me.bjtmastermind.i2ic;

import java.io.IOException;

import me.bjtmastermind.i2ic.utils.MessageToUser;

public class Main {

    public static void main(String[] args) throws IOException {
        if(args.length > 0) {
            MessageToUser.setUserType(MessageToUser.UserType.CLI);
            CLI.parseArgs(args);
        } else {
            MessageToUser.setUserType(MessageToUser.UserType.GUI);
            GUI.open();
        }
    }
}
