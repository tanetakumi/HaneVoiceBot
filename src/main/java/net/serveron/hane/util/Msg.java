package net.serveron.hane.util;

public class Msg {

    public enum MessageType{
        INFO,
        WARNING,
        ERROR
    }
    public static void sendToConsole(String msg){sendToConsole(msg, MessageType.INFO);}
    public static void sendToConsole(String msg, MessageType type){
        switch (type) {
            case ERROR -> System.out.println("\u001B[31m" + msg + "\u001B[0m");
            case WARNING -> System.out.println("\u001B[33m" + msg + "\u001B[0m");
            default -> System.out.println(msg);
        }
    }
}
