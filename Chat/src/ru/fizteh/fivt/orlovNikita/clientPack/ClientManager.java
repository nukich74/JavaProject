package ru.fizteh.fivt.orlovNikita.clientPack;

public class ClientManager {
    private String clientName;


    ClientManager(String tClientName) {
        clientName = tClientName;
    }


    public static void main(String[] args) {
        try {
            ClientManager clientManager = new ClientManager(args[0]);
        } catch (Exception e) {

        }
    }
}
