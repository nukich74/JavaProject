package ru.fizteh.fivt.orlovNikita.server;

import ru.fizteh.fivt.chat.MessageUtils;
import ru.fizteh.fivt.orlovNikita.MessageProcessor;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.*;

public class ServerManager {
    private Selector selector;
    private ServerSocketChannel socketChannel;
    private BufferedReader in;
    private static final String serverName = "<server>";
    private InetSocketAddress address = null;
    private HashSet<SocketChannel> incomingSockets = new HashSet<SocketChannel>();
    private HashMap<String, SocketChannel> userTable;

    public ServerManager() {
        userTable = new HashMap<String, SocketChannel>();
        in = new BufferedReader(new InputStreamReader(System.in));
    }

    private void interpretConsole() {
        try {
            String query = in.readLine();
            if (query.matches("/listen [0-9]*")) {
                this.serverListen(Integer.valueOf(query.split(" ")[1]));
            } else if (query.equals("/stop")) {
                this.serverStop();
            } else if (query.equals("/list")) {
                this.serverList();
            } else if (query.matches("/send .* .*")) {
                String[] array = query.split(" ");
                if (userTable.containsKey(array[1])) {
                    StringBuilder builder = new StringBuilder();
                    for (int i = 1; i < array.length; i++) {
                        builder.append(array[i]).append(" ");
                    }
                    sendMessage(userTable.get(array[1]), MessageUtils.message(serverName, builder.toString()));
                } else {
                    System.out.println("No such user in table!");
                }
            } else if (query.matches("/sendall .*")) {
                String[] array = query.split(" ");
                StringBuilder builder = new StringBuilder();
                for (int i = 1; i < array.length; i++) {
                    builder.append(array[i]).append(" ");
                }
                this.sendMessageToAll(MessageUtils.message(serverName, builder.toString()));
            } else if (query.matches("/kill .*")) {
                serverKillUser(query.split(" ")[1]);
            } else if (query.equals("/exit")) {
                serverStop();
                selector.close();
                System.exit(0);
            } else {
                throw new RuntimeException();
            }
        } catch (Exception e) {
            System.out.println("Bad command to server, try again");
        }
    }

    void workOutClient(SelectionKey key) throws Exception {
  //      System.out.println("Working out key!");
        if ((key.readyOps() & SelectionKey.OP_ACCEPT) == SelectionKey.OP_ACCEPT) {
            SocketChannel channel = socketChannel.accept();
            if (channel == null) {
                throw new RuntimeException("Error while accepting. Shutting down the server!");
            } else {
                channel.configureBlocking(false);
                channel.register(selector, SelectionKey.OP_READ);
                incomingSockets.add(channel);
            }
        } else if ((key.readyOps() & SelectionKey.OP_READ) == SelectionKey.OP_READ) {
            SocketChannel sc = (SocketChannel) key.channel();
            ByteBuffer buffer = ByteBuffer.allocate(512);
            if (getMessageFromSocket(sc, buffer)) {
                if (buffer.array()[0] == 1) {
                    System.out.println("We have new registered user!");
                    processHelloMessage(sc, buffer);
                } else if (buffer.array()[0] == 2) {
                    processToRoomMessage(buffer);
                } else if (buffer.array()[0] == 3) {
                    this.clientStop(sc);
                } else if (buffer.array()[0] == 127) {
                    this.processErrorMessage(sc, buffer);
                } else {
                    this.badCaseOfMessage(sc);
                }
            }
        }
    }

    private void serverListen(Integer port) {
        try {
            if (address == null) {
                socketChannel = ServerSocketChannel.open();
                socketChannel.configureBlocking(false);
                address = new InetSocketAddress(port);
                socketChannel.socket().bind(address);
                socketChannel.register(selector, SelectionKey.OP_ACCEPT);
            } else {
                throw new RuntimeException("Port is already opened");
            }
        } catch (Exception e) {
            System.out.println("Can't stat listening " + e.getMessage());
            System.out.println("Try another port, or wait");
        }
    }

    private void serverStop() {
        if (address == null) {
            return;
        }
        try {
            for (Map.Entry<String, SocketChannel> pair : userTable.entrySet()) {
                this.sendMessageToAll(MessageUtils.message(serverName, "Room is closed! Goodbye!"));
                this.sendMessage(pair.getValue(), MessageUtils.bye());
                pair.getValue().close();
            }
            this.socketChannel.close();
            userTable.clear();
            incomingSockets.clear();
            address = null;
        } catch (Exception e) {
            System.out.println("Error stopping server!" + e.getMessage());
            System.out.println("Terminating server!");
            System.exit(1);
        }
    }

    private void serverList() {
        System.out.println("User list:");
        for (Map.Entry<String, SocketChannel> pair : userTable.entrySet()) {
            System.out.println(pair.getKey());
        }
    }

    private void serverKillUser(String name) {
        SocketChannel channel = userTable.get(name);
        this.sendMessage(channel, MessageUtils.message(serverName, "You have been disconnected by server!"));
        this.sendMessage(channel, MessageUtils.bye());
    }

    private void badCaseOfMessage(SocketChannel sc) throws Exception {
        if (!incomingSockets.contains(sc)) {
            sendMessage(sc, MessageUtils.message(serverName, "Not in this time baby! Your request is bad..."));
            clientStop(sc);
        } else {
            clientStop(sc);
        }
    }

    private void processErrorMessage(SocketChannel sc, ByteBuffer buffer) {
        for (Map.Entry<String, SocketChannel> pair : userTable.entrySet()) {
            if (pair.getValue().equals(sc)) {
                ArrayList<String> list = MessageProcessor.parseBytesToMessages(buffer.array());
                System.out.println("Error message from client: " + pair.getKey());
                for (String s : list) {
                    System.out.println(s);
                }
                break;
            }
        }
    }

    private void processToRoomMessage(ByteBuffer buffer) throws Exception {
        ArrayList<String> messages = MessageProcessor.parseBytesToMessages(buffer.array());
        String uName = messages.get(0);
        System.out.println(uName + " sent new message to room ...");
     /*   for (String s : messages) {
            System.out.println(s);
        } */
        for (Map.Entry<String, SocketChannel> pair : userTable.entrySet()) {
            if (!pair.getKey().equals(uName)) {
                this.sendMessage(pair.getValue(), buffer.array());
            }
        }
   //     this.sendMessageToAll(buffer.array());
    }

    private void processHelloMessage(SocketChannel sc, ByteBuffer buffer) throws Exception {
        String uName = MessageProcessor.getClientNameFromHelloMessage(buffer.array());
        if (this.userTable.containsKey(uName)) {
            System.out.println("Someone tries to connect via used name!" + " " + uName);
            sendMessage(sc, MessageUtils.error("Another person has this name, please try another one."));
            sendMessage(sc, MessageUtils.bye());
            incomingSockets.remove(sc);
            try {
                if (sc != null) {
                    sc.close();
                }
            } catch (Exception e) {
                throw new RuntimeException("Can't close socket");
            }
        } else {
            System.out.println("We have new user: " + uName);
            sendMessageToAll(MessageUtils.message(serverName, uName + " connected to server"));
            sendMessage(sc, MessageUtils.message(serverName, "Welcome to room: " + address.getPort()));
            userTable.put(uName, sc);
            incomingSockets.remove(sc);
        }
    }

    private void sendMessageToAll(byte[] message) {
        for (Map.Entry<String, SocketChannel> pair : userTable.entrySet()) {
            sendMessage(pair.getValue(), message);
        }
    }

    private void sendMessage(SocketChannel socket, byte[] message) {
        try {
            if (socket.isConnected()) {
                ByteBuffer bf = ByteBuffer.wrap(message);
                int res = socket.write(bf);
                System.out.println("Sent to clients byte: " + res);
            }
        } catch (Exception e) {

        }
    }

    private boolean getMessageFromSocket(SocketChannel socket, ByteBuffer stream) {
        try {
            if (socket == null || stream == null) {
                throw new RuntimeException("Bad input channel or stream");
            } else {
                int readBytes = socket.read(stream);
                if (readBytes == -1) {
                    clientStop(socket);
                    throw new RuntimeException("User is offline");
                }
                return true;
            }

        } catch (Exception e) {
            return false;
        }
    }

    private void clientStop(SocketChannel socket) throws Exception {
        for (Map.Entry<String, SocketChannel> pair : userTable.entrySet()) {
            if (pair.getValue().equals(socket)) {
                sendMessage(socket, MessageUtils.bye());
                socket.close();
                userTable.remove(pair.getKey());
                sendMessageToAll(MessageUtils.message(serverName, pair.getKey() + " is offline!"));
                System.out.println(pair.getKey() + " is offline!");
                return;
            }
        }
        if (incomingSockets.contains(socket)) {
            incomingSockets.remove(socket);
        } else
            throw new RuntimeException("No such client in user table!");
    }

    private void run() {
        try {
            selector = Selector.open();
            for (; ; ) {

                if (in.ready()) {
                    interpretConsole();
                }
                if (selector.selectNow() == 0) {
                    continue;
                }
                Set<SelectionKey> keys =  selector.selectedKeys();
                for (SelectionKey key : keys) {
                    workOutClient(key);
                }
                keys.clear();
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public static void main(String[] args) {
        new ServerManager().run();
    }
}
