package ru.fizteh.fivt.orlovNikita;

import java.nio.ByteBuffer;
import java.util.ArrayList;

/**
 * Package: ru.fizteh.fivt.chat
 * User: acer
 * Date: 06.12.12
 * Time: 0:49
 */
public final class MessageProcessor {

    public static String getClientNameFromHelloMessage(byte[] bytes) throws Exception {
        try {
            ArrayList<String> array = new ArrayList<String>();
            ByteBuffer buffer = ByteBuffer.wrap(bytes);
            buffer.get(); // еще кол-во вроде
            int len = buffer.get();
            for (int i = 0; i < len; i++) {
                int messageLen = buffer.getInt();
                byte[] mes = new byte[messageLen];
                buffer.get(mes);
                array.add(new String(mes));
            }
            if (array.size() != 1) {
                throw new RuntimeException("Incorrect hello message");
            } else {
                return array.get(0);
            }
        } catch (Exception e) {
            throw new RuntimeException("Error while getting client name: " + e.getMessage());
        }
    }

    public static ArrayList<String> parseBytesToMessages(byte[] input) {
        ArrayList<String> res = new ArrayList<String>();
        ByteBuffer buffer = ByteBuffer.wrap(input);
        buffer.get();
        int len = buffer.get();
        for (int i = 0; i < len; i++) {
            int messageLen = buffer.getInt();
            byte[] mes = new byte[messageLen];
            buffer.get(mes);
            res.add(new String(mes));
        }
        return res;
    }

}
