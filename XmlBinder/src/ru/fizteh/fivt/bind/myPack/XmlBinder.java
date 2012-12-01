package ru.fizteh.fivt.bind.myPack;

import ru.fizteh.fivt.bind.defPack.AsXmlCdata;
import ru.fizteh.fivt.bind.defPack.BindingType;
import ru.fizteh.fivt.bind.defPack.MembersToBind;
import sun.misc.Unsafe;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamWriter;
import java.io.StringWriter;
import java.lang.reflect.Field;
import java.util.IdentityHashMap;
import java.util.Map;

/**
 * Package: ru.fizteh.fivt.bind.myPack
 * User: acer
 */
public class XmlBinder<T> extends ru.fizteh.fivt.bind.defPack.XmlBinder {
    private Unsafe unsafe;
    private ClassUtils classUtil;

    protected XmlBinder(Class clazz) {
        super(clazz);
        classUtil = new ClassUtils(clazz);
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
            IdentityHashMap<Object, Integer> usedLinks = new IdentityHashMap<Object, Integer>();
            StringWriter strWriter = new StringWriter();
            XMLStreamWriter streamWriter = XMLOutputFactory.newInstance().createXMLStreamWriter(strWriter);
            streamWriter.writeStartElement(value.getClass().getName());
            serialize(value, streamWriter, usedLinks);
            streamWriter.writeEndElement();
            return strWriter.toString().getBytes();
        }
    }

    public static boolean isWrapperOrPrimitive(Class<?> clazz) {
        return clazz.equals(Boolean.class) || clazz.equals(Integer.class) ||
                clazz.equals(Character.class) || clazz.equals(Byte.class) ||
                clazz.equals(Short.class) || clazz.equals(Double.class) ||
                clazz.equals(Long.class) || clazz.equals(Float.class);
    }

    private void serialize(Object value, XMLStreamWriter streamWriter, IdentityHashMap<Object, Integer> usedLinks) {
        try {
            if (value == null || usedLinks.containsKey(value)) {
                return;
            } else {
                BindingType type = ((BindingType) value.getClass().getAnnotation(BindingType.class));
                if (isWrapperOrPrimitive(value.getClass())) {
                    streamWriter.writeCharacters(value.toString());
                } else if ((type.equals(MembersToBind.FIELDS)) || (type == null)) {
                    for (Map.Entry<String, Field> entry : classFieldMap.get(value.getClass()).entrySet()) {
                        Object newValue = entry.getValue().get(value);
                        if (isWrapperOrPrimitive(newValue.getClass())) {
                            if (newValue.getClass().getAnnotation(AsXmlCdata.class) != null) {
                                streamWriter.writeStartElement(entry.getKey());
                                streamWriter.writeCData(newValue.toString());
                                streamWriter.writeEndElement();
                            } /*TODO else if (newValue.getClass().getAnnotation(AsXmlAttribute.class) != null) {
                                streamWriter.writeStartElement(entry.getValue().getName());
                                streamWriter.writeAttribute(entry.getKey(), newValue.toString());
                                serialize(newValue, streamWriter, usedLinks);
                            } */ else {
                                streamWriter.writeStartElement(entry.getKey());
                                serialize(newValue, streamWriter, usedLinks);
                                streamWriter.writeEndElement();
                            }
                        } else {

                        }
                    }
                } else {

                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Error while serializing object: " + value.getClass().getCanonicalName());
        }

    }

    @Override
    public Object deserialize(byte[] bytes) throws XmlBinderException {
        return null;
    }
}
