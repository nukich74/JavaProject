package ru.fizteh.fivt.orlovNikita.server;

import java.io.BufferedInputStream;
import java.util.Scanner;

public class ServerManager {

    private static final String serverName = "<server>";

    ServerManager() {
    }


    public void launchServer() {
        Scanner in = new Scanner(new BufferedInputStream(System.in));
        while (true) {
            String query = in.nextLine();
            if (query.matches("listen [0-9]{4}]")) {
            } else if (query.equals("/stop")) {

            } else if (query.equals("/list")) {

            } else if (query.equals("/send")) {

            } else if (query.equals("/sendAll")) {

            } else if (query.equals("/kill")) {

            } else if (query.equals("/exit")) {

            } else {
                System.out.println("Bad command to server, try again");
            }
        }
    }

    public static void main(String[] args) {

    }
}
