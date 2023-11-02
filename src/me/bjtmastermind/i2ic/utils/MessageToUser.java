package me.bjtmastermind.i2ic.utils;

import me.bjtmastermind.i2ic.GUI;

public class MessageToUser {
    public enum UserType {
        CLI,
        GUI
    }

    private static UserType userType;

    public static void sendMessage(String msg) {
        if (userType == UserType.CLI) {
            System.out.println(msg);
        }
        if (userType == UserType.GUI) {
            GUI.appendConsole(msg);
        }
    }

    public static void setUserType(UserType newUserType) {
        userType = newUserType;
    }
}
