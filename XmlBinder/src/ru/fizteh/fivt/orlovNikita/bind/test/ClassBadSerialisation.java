package ru.fizteh.fivt.orlovNikita.bind.test;

public class ClassBadSerialisation {
    private ClassBadSerialisation pointer;
    public ClassBadSerialisation() {
        pointer = this;
    }
}
