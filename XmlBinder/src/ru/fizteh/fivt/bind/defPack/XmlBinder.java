package ru.fizteh.fivt.bind.defPack;

import ru.fizteh.fivt.bind.myPack.XmlBinderException;

import javax.xml.stream.XMLStreamException;

/**
 * Наследники класса должны быть потокобезопасными.
 *
 * @author Dmitriy Komanov (dkomanov@ya.ru)
 */
public abstract class XmlBinder<T> {

    private final Class<T> clazz;

    protected XmlBinder(Class<T> clazz) {
        this.clazz = clazz;
    }

    protected final Class<T> getClazz() {
        return clazz;
    }

    public abstract byte[] serialize(T value) throws Exception;

    public abstract T deserialize(byte[] bytes) throws XMLStreamException, XmlBinderException;
}