package ru.fizteh.fivt.bind.myPack;

import sun.misc.Unsafe;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamWriter;
import java.io.StringWriter;
import java.lang.reflect.Field;
import java.util.IdentityHashMap;

/**
 * Package: ru.fizteh.fivt.bind.myPack
 * User: acer
 */
public class XmlBinder<T> extends ru.fizteh.fivt.bind.defPack.XmlBinder {
    private Unsafe unsafe;

    protected XmlBinder(Class clazz) {
        super(clazz);

        try {
            Field field = Unsafe.class.getDeclaredField("theUnsafe");
            field.setAccessible(true);
            unsafe = Unsafe.class.cast(field.get(null));
        } catch (Exception e) {
            throw new RuntimeException("Can't create binder");
        }
    }

    @Override
    public byte[] serialize(Object value) throws Exception {
        if (value == null || !value.getClass().equals(getClazz())) {
            throw new RuntimeException("Can't serialise because value isn't correct");
        } else {
            IdentityHashMap<Object, Object> usedForSerialisation;
            StringWriter strWriter = new StringWriter();
            XMLStreamWriter streamWriter = XMLOutputFactory.newInstance().createXMLStreamWriter(strWriter);

            return strWriter.toString().getBytes();
        }
    }

    @Override
    public Object deserialize(byte[] bytes) throws XmlBinderException {
        return null;
    }

    private byte[] methodSerialize(Object value) throws Exception {
        return null;
    }

    private byte[] fullSerialize(Object value) {
        return null;
    }

}
