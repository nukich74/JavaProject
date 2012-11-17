package ru.fizteh.fivt.orlovNikita.Utils;

/**
 * Package: ru.fizteh.fivt.orlovNikita.Utils
 * User: acer
 * Date: 17.11.12
 * Time: 17:54
 */
public enum MessageType {
    HELLO(1),
    MESSAGE(2),
    BYE(3),
    ERROR(127);

    private final byte id;

    private MessageType(int id) {
        this.id = (byte) id;
    }

    public byte getId() {
        return id;
    }
}
