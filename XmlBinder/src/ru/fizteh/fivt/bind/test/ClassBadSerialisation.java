package ru.fizteh.fivt.bind.test;

/**
 * Package: ru.fizteh.fivt.bind.test
 * User: acer
 * Date: 01.12.12
 * Time: 17:33
 */

public class ClassBadSerialisation {
    private ClassBadSerialisation pointer;
    public ClassBadSerialisation() {
        pointer = this;
    }
}
