package ru.fizteh.fivt.orlovNikita.client;

import ru.fizteh.fivt.chat.MessageUtils;
import ru.fizteh.fivt.orlovNikita.MessageProcessor;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class ClientManager {
    private String clientName;
    private HashMap<InetSocketAddress, Object[]> serverTable; // obj[0] = socket [1] = selector;
    private InetSocketAddress curServer = null;
    private BufferedReader in;

    public ClientManager(String tClientName) {
        clientName = tClientName;
        in = new BufferedReader(new InputStreamReader(System.in));
        serverTable = new HashMap<InetSocketAddress, Object[]>();
    }

    public void launchClient() {
        try {
            for (; ; ) {
                if (in.ready()) {
                    this.interpretConsole();
                }
                if (curServer != null && !(((Selector) serverTable.get(curServer)[1]).selectNow() == 0)) {
                    this.workOutServer();
                }
            }
        } catch (Exception e) {
            System.out.println("Something goes wrong with the client! : " + e.getMessage());
        }
    }

    private void workOutServer() {
        try {
            Set<SelectionKey> keySet = ((Selector) this.serverTable.get(curServer)[1]).selectedKeys();
            for (SelectionKey key : keySet) {
               // System.out.println("new Key!");
                if ((key.readyOps() & SelectionKey.OP_READ) == SelectionKey.OP_READ) {
                    SocketChannel sc = (SocketChannel) key.channel();
                    ByteBuffer mes = ByteBuffer.allocate(512);
                    if (sc.read(mes) == -1) {
                        disconnect(curServer);
                    }
                    if (mes.array()[0] == 2) {
                        ArrayList<String> l = MessageProcessor.parseBytesToMessages(mes.array());
                        StringBuilder builder = new StringBuilder(l.get(0) + ": ");

             //           System.out.println("Got new message from " + l.get(0));
                        for (int i = 1; i < l.size(); ++i) {
                            builder.append(l.get(i));
                        }
                        System.out.println(builder.toString());
                    } else if (mes.array()[0] == 3) {
                        disconnect(curServer);
                    } else if (mes.array()[0] == 127) {
                        ArrayList<String> l = MessageProcessor.parseBytesToMessages(mes.array());
                        StringBuilder builder = new StringBuilder();
                        for (String aL : l) {
                            builder.append(aL).append(" ");
                        }
                        System.out.println("Error: " + builder.toString());
                    }
                }
            }
            keySet.clear();
        } catch (Exception e) {
            System.out.println("Error while working out server! " + e.getMessage());
        }
    }

    private void disconnect(InetSocketAddress key) {
        try {
            if (key.equals(curServer)) {
                curServer = null;
            }
            ((SocketChannel) serverTable.get(key)[0]).write(ByteBuffer.wrap(MessageUtils.bye()));
            ((SocketChannel) serverTable.get(key)[0]).close();
            ((Selector) serverTable.get(key)[1]).close();
            System.out.println("Disconnected successfully!");
        } catch (Exception e) {
            System.out.println("Error disconnecting user!");
            System.exit(1);
        } finally {
            serverTable.remove(key);
        }
    }

    private void interpretConsole() {
        try {
            String query = in.readLine();
            if (query.matches("/connect [0-9a-zA-Z\\.]*:[0-9]*")) {
                connectToServer(query.split(" ")[1].split(":")[0], query.split(" ")[1].split(":")[1]);
            } else if (query.equals("/disconnect")) {
                if (this.curServer == null) {
                    System.out.println("You are not connected!");
                } else {
                    this.disconnect(curServer);
                }
            } else if (query.equals("/whereami")) {
                if (this.curServer != null) {
                    System.out.println(curServer.getHostString() + ":" + curServer.getPort());
                } else {
                    System.out.println("You are not in any room!");
                }
            } else if (query.equals("/list")) {
                System.out.println("Connected to:");
                for (Map.Entry<InetSocketAddress, Object[]> pair : serverTable.entrySet()) {
                    System.out.println(pair.getKey().getHostString() + ":" + pair.getKey().getPort());
                }
            } else if (query.matches("/use .*")) {
                String[] array = query.split("[ :]");
                InetSocketAddress goTo = new InetSocketAddress(array[1], Integer.valueOf(array[2]));
                if (serverTable.containsKey(goTo)) {
                    curServer = goTo;
                } else {
                    System.out.println("Can't go, because not connected!");
                }
            } else if (query.equals("/exit")) {
                for (Map.Entry<InetSocketAddress, Object[]> pair : serverTable.entrySet()) {
                    disconnect(pair.getKey());
                }
                System.exit(0);
            } else if (query.equals("/disconnect")) {
                if (curServer != null) {
                    disconnect(curServer);
                }
            } else if (curServer != null) {
                System.out.println("New query: " + query);
                sendMessage((SocketChannel) serverTable.get(curServer)[0], MessageUtils.message(this.clientName, query));
            }

        } catch (Exception e) {
            System.out.println("Error while executing command, try again!");
        }
    }

    public void connectToServer(String host, String port) {
        try {
            InetSocketAddress newAddress = new InetSocketAddress(host, Integer.valueOf(port));
            if (this.serverTable.containsKey(newAddress)) {
                System.out.println("User is already connected to this server!");
            } else {
                Object[] newSocket = new Object[]{SocketChannel.open(), Selector.open()};
                ((SocketChannel) newSocket[0]).connect(newAddress);
                ((SocketChannel) newSocket[0]).configureBlocking(false);
                ((SocketChannel) newSocket[0]).
                        register((Selector) newSocket[1], SelectionKey.OP_READ);
                serverTable.put(newAddress, newSocket);
                curServer = newAddress;
                sendMessage(((SocketChannel) serverTable.get(newAddress)[0]), MessageUtils.hello(this.clientName));
            }
        } catch (Exception e) {
            System.out.println("Error of connecting to server!");
            for (Map.Entry<InetSocketAddress, Object[]> pair : serverTable.entrySet()) {
                disconnect(pair.getKey());
            }
            System.exit(1);
        }

    }

    public void sendMessage(SocketChannel socketChannel, byte[] message) {
        try {
            if (socketChannel != null) {
                socketChannel.write(ByteBuffer.wrap(message));
            } else {
                System.err.println("socket is down!");
            }
        } catch (Exception e) {
            System.out.println("Error of sending message! : " + e.getMessage());
        }
    }


    public static void main(String[] args) {
        try {
            if (args.length == 0) {
                throw new RuntimeException("When launch give your name as 1st param!");
            } else {
                ClientManager manager = new ClientManager(args[0]);
                manager.launchClient();
            }
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }
}
