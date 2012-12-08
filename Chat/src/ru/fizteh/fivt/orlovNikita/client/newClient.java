package ru.fizteh.fivt.orlovNikita.client;

import ru.fizteh.fivt.chat.MessageUtils;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.*;

public class newClient {

    TreeMap<String, Integer> servers = new TreeMap<String, Integer>();
    Scanner in;
    String listeningServerName;
    String userName;
    int curServerNumberId;
    boolean connected;
    ArrayList<SocketChannel> channels;
    ArrayList<Selector> selectors;

    public newClient(String userName) {
        in = new Scanner(System.in);
        this.userName = userName;
        selectors = new ArrayList<Selector>();
        channels = new ArrayList<SocketChannel>();
        connected = false;
        curServerNumberId = -1;
    }

    public void launchClient() {
        try {
            for (; ; ) {
                if (in.hasNextLine()) {
                    interpretConsole();
                }
                if (connected.t) {
                    int num = selectors.get(curServerNumberId.t).selectNow();
                    if (num == 0) {
                        continue;
                    }
                    handlerServer(servers, listenServerName, curServerNumberId, connected,
                            channels, selectors);
                }
            }
        } catch (Exception e) {
            IOUtils.printErrorAndExit(e.getMessage());
        }
    }

    public void closeChannel(SocketChannel sc) {
        try {
            if (sc != null) {
                sc.close();
            }
        } catch (Exception e) {
            IOUtils.printErrorAndExit("Bad closing: " + e.getMessage());
        }
    }

    public void closeSelector(Selector selector) {
        try {
            if (selector != null) {
                selector.close();
            }
        } catch (Exception e) {
            IOUtils.printErrorAndExit("Bad closing: " + e.getMessage());
        }
    }

    public void connect() {
        // при вызове команды сonnect клиент переходит в чат-комнату нового сервера
        try {
            if (st.hasMoreTokens()) {
                String cur = st.nextToken();
                int pos = cur.indexOf(":");
                if (pos != -1) {
                    String host = cur.substring(0, pos);
                    String portNumber = cur.substring(pos + 1, cur.length());
                    if (servers.containsKey(host)) {
                        System.out.println("You are already have connection to this server");
                    } else {
                        int port = Integer.parseInt(portNumber);
                        try {
                            channels.add(SocketChannel.open());
                            // создаем новый канал для нового сервера
                            selectors.add(Selector.open());
                            // создаем новый selector для нового сервера
                            channels.get(channels.size() - 1).
                                    connect(new InetSocketAddress(host, port));
                            channels.get(channels.size() - 1).
                                    configureBlocking(false);
                            // делаем канал неблокирующим для использования Selector
                            channels.get(channels.size() - 1).register
                                    (selectors.get(selectors.size() - 1), SelectionKey.OP_READ);
                            servers.put(host, channels.size() - 1);
                            curServer.t = host;
                            connected.t = true;
                            curServerNumber.t = selectors.size() - 1;
                            sendMessage(channels.get(curServerNumber.t),
                                    MessageUtils.hello(nick));
                        } catch (Exception e) {
                            IOUtils.printErrorAndExit(e.getMessage());
                        }
                    }
                } else {
                    IOUtils.printErrorAndExit("Usage: /connect host:port");
                }
            } else {
                IOUtils.printErrorAndExit("Usage: /connect host port");
            }
        } catch (Exception e) {
            IOUtils.printErrorAndExit("Bad connecting: " + e.getMessage());
        }
    }



    public void interpretConsole() {
        try {
            String query = in.nextLine();
            if (query.matches("/connect [a-z0-9]*\\:[0-9]{4}")) {
                connect(curServer, curServerNumber, st);
            } else if (query.equals("/disconnect")) {
                if (connected) {
                    disconnect(servers, curServer, curServerNumber, connected,
                            channels, selectors);
                } else {
                    System.out.println("You are not connected to any server!");
                }
            } else if (query.matches("/whereami")) {
                if (connected) {
                    System.out.println(curServerNumberId);
                } else {
                    System.out.println("You are not connected");
                }
            } else if (query.equals("/list")) {
                for (Map.Entry<String, Integer> pair: this.servers.entrySet()) {

                }
            } else if (query.matches("/use [0-9]*")) {
                    String host = st.nextToken();
                    if (servers.containsKey(host)) {
                        curServer = host;
                        curServerNumber.t = servers.get(curServer.t);
                    } else {
                        IOUtils.printErrorAndExit(host + ": there is no such server");
                    }
                }
            } else if (query.equals("/exit")) {
                Iterator it = servers.entrySet().iterator();
                while (it.hasNext()) {
                    Map.Entry pair = (Map.Entry) it.next();
                    sendMessage(channels.get((Integer) pair.getValue()),
                            MessageUtils.bye());
                    closeChannel(channels.get((Integer) pair.getValue()));
                    closeSelector(selectors.get((Integer) pair.getValue()));
                }
                servers.clear();
                channels.clear();
                selectors.clear();
                System.exit(0);
            } else {
                // отправка сообщения в чат
                if (connected.t) {
                    sendMessage(channels.get(curServerNumber.t),
                            MessageUtils.message(nick, query));
                } else {
                    System.out.println("You are not connected. You can't " +
                            "send messages.");
                }
            }
        } catch (Exception e) {
            IOUtils.printErrorAndExit(e.getMessage());
        }
    }

    public static void handlerServer(Map<String, Integer> servers,
                                     WrapperPrimitive<String> curServer, WrapperPrimitive<Integer> curServerNumber,
                                     WrapperPrimitive<Boolean> connected, List<SocketChannel> channels,
                                     List<Selector> selectors) {
        try {
            Set<SelectionKey> keys = selectors.get(curServerNumber.t).selectedKeys();
            Iterator iter = keys.iterator();
            while (iter.hasNext()) {
                SelectionKey key = (SelectionKey) iter.next();
                if ((key.readyOps() & SelectionKey.OP_READ) ==
                        SelectionKey.OP_READ) {
                    // в SocketChannel пришло новое сообщение
                    SocketChannel sc = (SocketChannel) key.channel();
                    ByteBuffer mes = ByteBuffer.allocate(512);
                    getMessage(sc, mes, curServer, curServerNumber, servers,
                            connected, channels, selectors);
                    byte[] message = mes.array();
                    if (message[0] == 2) {
                        // обычное сообщение
                        List<String> l = MessageUtils.dispatch(message);
                        StringBuilder sb = new StringBuilder(l.get(0) + ": ");
                        for (int i = 1; i < l.size(); ++i) {
                            sb.append(l.get(i));
                        }
                        System.out.println(sb.toString());
                    } else if (message[0] == 3) {
                        // bye от сервера
                        disconnect(servers, curServer, curServerNumber, connected,
                                channels, selectors);
                    } else if (message[0] == 127) {
                        // ошибка от сервера
                        List<String> l = MessageUtils.dispatch(message);
                        StringBuilder sb = new StringBuilder();
                        for (int i = 0; i < l.size(); ++i) {
                            sb.append(l.get(i));
                        }
                        System.out.println("Error: " + sb.toString());
                    }
                }
            }
            keys.clear();
        } catch (Exception e) {
            IOUtils.printErrorAndExit(e.getMessage());
        }
    }

    public static void sendMessage(SocketChannel sc, byte[] message) {
        try {
            if (sc != null) {
                ByteBuffer bf = ByteBuffer.wrap(message);
                sc.write(bf);
            } else {
                IOUtils.printErrorAndExit("Bad SocketChannel");
            }
        } catch (Exception e) {
            IOUtils.printErrorAndExit("Bad sending message!" + e.getMessage());
        }
    }

    public static void getMessage(SocketChannel sc, ByteBuffer message,
                                  WrapperPrimitive<String> curServer, WrapperPrimitive<Integer> curServerNumber,
                                  Map<String, Integer> servers, WrapperPrimitive<Boolean> connected,
                                  List<SocketChannel> channels, List<Selector> selectors) {
        try {
            int count = sc.read(message);
            if (count == -1) {
                // проверка на случай экстренного выхода сервера
                disconnect(servers, curServer, curServerNumber, connected,
                        channels, selectors);
            }
        } catch (Exception e) {
            IOUtils.printErrorAndExit("Bad geting message!" + e.getMessage());
        }
    }
}