package ru.fizteh.fivt.orlovNikita.client;

import java.io.BufferedInputStream;
import java.net.InetSocketAddress;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.HashMap;
import java.util.Scanner;

public class ClientManager {
    private String clientName;
    private InetSocketAddress currentServerAddress = null;
    SocketChannel channel;
    Selector select;
    private HashMap<SocketChannel, String> channels;
    private Scanner in;

    public ClientManager(String tClientName) {
        clientName = tClientName;
        in = new Scanner(System.in);
        channels = new HashMap<SocketChannel, String>();
    }

    public void launchClient() {
        try {
            for (; ; ) {
                if (in.hasNextLine()) {
                    this.interpretConsole();
                }
                if (currentServerAddress != null) {
                    this.workOutServer();
                }
            }
        } catch (Exception e) {

        }
    }

    private void interpretConsole() {
        Scanner in = new Scanner(new BufferedInputStream(System.in));
        String query = in.nextLine();
        if (query.matches("/connect [0-9a-z]*\\:[0-9]{4}]")) {
            connectToServer(query.split(" ")[1].split(":")[0], query.split(" ")[1].split(":")[1]);
        } else if (query.equals("disconnect")) {
            if (this.currentServerAddress != null) {

            }
        } else if (query.equals("whereami")) {
            if (this.currentServerAddress != null) {
                System.out.println(currentServerAddress.getHostString() + ":" + currentServerAddress.getPort());
            } else {
                System.out.println("You are not in any room!");
            }
        } else if (query.equals("list")) {
            System.out.println("Connected to:");
            for (InetSocketAddress address : channels) {
                System.out.println(address.getHostString() + ":" + address.getPort());
            }
        } else if (query.equals("use")) {

        } else if (query.equals("exit")) {

        }
        System.out.print('/');

    }

    private void connectToServer(String host, String port) {

    }


    public static void connect(String query) {
        try {
            String host = query.split(":")[0];
            String port = query.split(":")[1];
            InetSocketAddress newAddress = new InetSocketAddress(host, Integer.valueOf(port));

            if (channels.contains(newAddress)) {
                System.out.println("You are already have connection to this server");
            } else {

            }
        } catch (Exception e) {

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
