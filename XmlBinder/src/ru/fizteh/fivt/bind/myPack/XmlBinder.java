package ru.fizteh.fivt.bind.myPack;

import java.lang.annotation.Annotation;

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
        Class valueClass = value.getClass();
        Annotation[] annArray = valueClass.getAnnotations();
        for (int i = 0; i < annArray.length; i++) {

        }
        return new byte[0];
    }

    @Override
    public Object deserialize(byte[] bytes) {
        return null;
    }
}
