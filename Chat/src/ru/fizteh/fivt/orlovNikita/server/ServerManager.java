package ru.fizteh.fivt.orlovNikita.server;

import ru.fizteh.fivt.chat.MessageUtils;
import ru.fizteh.fivt.orlovNikita.MessageProcessor;

import java.io.BufferedInputStream;
import java.net.InetAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.*;

public class ServerManager {
    private Selector selector;
    private ServerSocketChannel socketChannel;
    private Scanner in;
    private static final String serverName = "<server>";
    private InetAddress address = null;
    private HashSet<SocketChannel> incomingSockets;
    private HashMap<String, SocketChannel> userTable;

    public ServerManager() {
        in = new Scanner(new BufferedInputStream(System.in));
    }


    private void interpretConsole() {
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

    void workOutClient(SelectionKey key) throws Exception {
        if (key.isAcceptable()) {
            SocketChannel channel = socketChannel.accept();
            if (channel == null) {
                throw new RuntimeException("Error while accepting");
            } else {
                channel.configureBlocking(false);
                channel.register(selector, SelectionKey.OP_READ);
                incomingSockets.add(channel);
            }
        } else if (key.isReadable()) {
            SocketChannel sc = (SocketChannel) key.channel();
            ByteBuffer buffer = ByteBuffer.allocate(256);
            if (getMessageFromSocket(sc, buffer)) {
                if (buffer.array()[0] == 1) {
                    String uName = MessageProcessor.getClientName(buffer.array());
                    if (this.userTable.containsKey(uName)) {
                        sendMessage(sc, MessageUtils.error("Another person has this name, please try another one."));
                        sendMessage(sc, MessageUtils.bye());
                        try {
                            if (sc != null) {
                                sc.close();
                            }
                        } catch (Exception e) {
                            throw new RuntimeException("Can't close socket");
                        }
                    } else {
                        System.out.println("We have new user: " + uName);
                        sendMessageToAll(uName + "connected to server", serverName);
                    }
                }
            }
        }
    }

    private void sendMessageToAll(String s, String from) {
        Iterator iter = userTable.entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry<String, SocketChannel> pair = (Map.Entry<String, SocketChannel>) iter.next();
            sendMessage(pair.getValue(), MessageUtils.message(from, s));
        }
    }

    private void sendMessage(SocketChannel socket, byte[] message) {
        try {
            if (socket.isConnected()) {
                ByteBuffer bf = ByteBuffer.wrap(message);
                socket.write(bf);
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
                return true;
            }

        } catch (Exception e) {
            return false;
        }
    }


    private void run() {
        try {
            selector = Selector.open();
            for (; ; ) {
                int available = selector.selectNow();
                if (in.hasNextLine()) {
                    interpretConsole();
                }
                for (SelectionKey key : selector.selectedKeys()) {
                    workOutClient(key);
                }
            }
        } catch (Exception e) {

        }
    }


    public static void main(String[] args) {
        new ServerManager().run();
    }
}
