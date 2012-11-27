package ru.fizteh.fivt.bind.myPack;

/**
 * Package: ru.fizteh.fivt.bind.myPack
 * User: acer
 * Date: 27.11.12
 * Time: 17:08
 */
public class XmlBinder extends ru.fizteh.fivt.bind.defPack.XmlBinder {
    ////Почему intelligi требует, чтобы я его создал?
    protected XmlBinder(Class clazz) {
        super(clazz);
    }

    @Override
    public byte[] serialize(Object value) {
        return new byte[0];
    }

    @Override
    public Object deserialize(byte[] bytes) {
        return null;
    }
}
