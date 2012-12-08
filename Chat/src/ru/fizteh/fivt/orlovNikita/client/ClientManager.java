package ru.fizteh.fivt.orlovNikita.client;

import ru.fizteh.fivt.orlovNikita.MessageProcessor;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.*;

public class ClientManager {
    private String clientName;
    private HashMap<InetSocketAddress, Object[]> serverTable; // obj[0] = socket [1] = selector;
    private InetSocketAddress curServer = null;
    private Scanner in;

    public ClientManager(String tClientName) {
        clientName = tClientName;
        in = new Scanner(System.in);
        serverTable = new HashMap<InetSocketAddress, Object[]>();
    }

    public void launchClient() {
        try {
            for (; ; ) {
                if (in.hasNextLine()) {
                    this.interpretConsole();
                }
                this.workOutServer();
            }
        } catch (Exception e) {
            System.out.println("Something goes wrong with the client! : " + e.getMessage());
        }
    }

    private void workOutServer() {
        try {
            for (SelectionKey key : ((Selector)this.serverTable.get(curServer)[1]).selectedKeys()) {
                if (key.isReadable()) {
                    SocketChannel sc = (SocketChannel) key.channel();
                    ByteBuffer mes = ByteBuffer.allocate(512);
                    if (sc.read(mes) == -1) {
                        disconnect(curServer);
                    }
                    if (mes.array()[0] == 2) {
                        ArrayList<String> l = MessageProcessor.parseBytesToMessages(mes.array());
                        StringBuilder builder = new StringBuilder(l.get(0) + ": ");
                        for (int i = 1; i < l.size(); ++i) {
                            builder.append(l.get(i));
                        }
                        System.out.println(builder.toString());
                    } else if (mes.array()[0] == 3) {
                        disconnect(curServer);
                    } else if (mes.array()[0] == 127) {
                        ArrayList<String> l = MessageProcessor.parseBytesToMessages(mes.array());
                        StringBuilder builder = new StringBuilder();
                        for (int i = 0; i < l.size(); ++i) {
                            builder.append(l.get(i));
                        }
                        System.out.println("Error: " + builder.toString());
                    } else {
                        throw new RuntimeException("Unknown message!");
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("Error while working out server! " + e.getMessage());
        }
    }

    private void disconnect(InetSocketAddress key) {
    }

    private void interpretConsole() {
        Scanner in = new Scanner(new BufferedInputStream(System.in));
        String query = in.nextLine();
        if (query.matches("/connect [0-9a-z]*\\:[0-9]{4}]")) {
            connectToServer(query.split(" ")[1].split(":")[0], query.split(" ")[1].split(":")[1]);
        } else if (query.equals("disconnect")) {
            if (this.curServer == null) {
                System.out.println("You are not connected!");
            } else {
                this.disconnect(curServer);
            }
        } else if (query.equals("whereami")) {
            if (this.curServer != null) {
                System.out.println(curServer.getHostString() + ":" + curServer.getPort());
            } else {
                System.out.println("You are not in any room!");
            }
        } else if (query.equals("list")) {
            System.out.println("Connected to:");
            for (Map.Entry<InetSocketAddress, Object[]> pair: serverTable.entrySet()) {
                System.out.println(pair.getKey().getHostString() + ":" + pair.getKey().getPort());
            }
        } else if (query.equals("use")) {

        } else if (query.equals("exit")) {
            for (Map.Entry<InetSocketAddress, Object[]> pair: serverTable.entrySet()) {
                disconnect(pair.getKey());
            }
        }
        System.out.print('/');

    }

    public static void connect(String query) {
        try {
            String host = query.split(":")[0];
            String port = query.split(":")[1];
            InetSocketAddress newAddress = new InetSocketAddress(host, Integer.valueOf(port));

            if () {
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
