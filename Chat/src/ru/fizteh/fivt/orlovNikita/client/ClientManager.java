package ru.fizteh.fivt.orlovNikita.client;

import java.io.BufferedInputStream;
import java.util.Scanner;

public class ClientManager {
    private String clientName;


    public ClientManager(String tClientName) {
        clientName = tClientName;
    }

    public void launchClient() {
        try {
            Scanner in = new Scanner(new BufferedInputStream(System.in));
            for (; ;) {

            }
        } catch (Exception e) {

        }
    }


    private void imitateConsole() {
        Scanner in = new Scanner(new BufferedInputStream(System.in));
        while (true) {
            String query = in.nextLine();
            if (query.matches(" [a-z]*\\:[0-9]{4}]")) {
            }
            System.out.print('/');
        }
    }


    public static void main(String[] args) {
        try {
            if (args == null) {
                throw new RuntimeException("When launch give your name as 1st param!");
            } else {
                ClientManager manager = new ClientManager(args[0]);
            }
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }
}
