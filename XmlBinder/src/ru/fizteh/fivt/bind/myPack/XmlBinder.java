package ru.fizteh.fivt.bind.myPack;

import ru.fizteh.fivt.bind.defPack.AsXmlAttribute;
import ru.fizteh.fivt.bind.defPack.AsXmlCdata;
import ru.fizteh.fivt.bind.defPack.BindingType;
import ru.fizteh.fivt.bind.defPack.MembersToBind;
import sun.misc.Unsafe;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import java.io.StringWriter;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.IdentityHashMap;
import java.util.Map;

/**
 * Package: ru.fizteh.fivt.bind.myPack
 * User: acer
 */
public class XmlBinder<T> extends ru.fizteh.fivt.bind.defPack.XmlBinder {
    private Unsafe unsafe;
    private ClassUtils classUtil;

    public XmlBinder(Class<T> clazz) {
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
    public byte[] serialize(Object value) {
        if (value == null || !value.getClass().equals(getClazz())) {
            throw new RuntimeException("Can't serialise because value isn't correct");
        } else {
            IdentityHashMap<Object, Integer> usedLinks = new IdentityHashMap<Object, Integer>();
            StringWriter strWriter = new StringWriter();
            XMLStreamWriter streamWriter = null;
            try {
                streamWriter = XMLOutputFactory.newInstance().createXMLStreamWriter(strWriter);
            } catch (XMLStreamException e) {
                System.err.print("Can't create streamWriter. Error\n");
                return null;
            }
            serialize(value, streamWriter, usedLinks);
            return strWriter.toString().getBytes();
        }
    }

    public static boolean isWrapperOrPrimitive(Class<?> clazz) {
        return clazz.isPrimitive() || clazz.equals(Boolean.class) ||
               clazz.equals(Byte.class) || clazz.equals(Character.class) ||
               clazz.equals(Short.class) || clazz.equals(Integer.class) ||
               clazz.equals(Long.class) || clazz.equals(Float.class) ||
               clazz.equals(Double.class) || clazz.isEnum() || clazz.equals(String.class);
    }

    private void serialize(Object value, XMLStreamWriter streamWriter, IdentityHashMap<Object, Integer> usedLinks) {
        try {
            if (value == null) {
                return;
            }
            if (usedLinks.containsKey(value)) {
                throw new RuntimeException("Can't serialize because of the links!");
            } else {
                usedLinks.put(value, 1);
            }
            if (isWrapperOrPrimitive(value.getClass())) {
                streamWriter.writeCharacters(value.toString());
            } else {
                streamWriter.writeStartElement(value.getClass().getName());
                BindingType type = value.getClass().getAnnotation(BindingType.class);
                if ((type == null) || (type.value().equals(MembersToBind.FIELDS))) {
                    for (Map.Entry<String, Field> entry : classUtil.getFieldsTable(value.getClass()).entrySet()) {
                        Object newValue = entry.getValue().get(value);
                        //Забыл добавить учет примитива
                        if (newValue == null) {
                            continue;
                        }
                        if (isWrapperOrPrimitive(newValue.getClass())) {
                            if (entry.getValue().getAnnotation(AsXmlCdata.class) != null) {
                                streamWriter.writeStartElement(entry.getKey());
                                streamWriter.writeCData(newValue.toString());
                                streamWriter.writeEndElement();
                            } else if (entry.getValue().getAnnotation(AsXmlAttribute.class) != null) {
                                //TODO Не обязательное задание
                            } else {
                                streamWriter.writeStartElement(entry.getKey());
                                serialize(newValue, streamWriter, usedLinks);
                                streamWriter.writeEndElement();
                            }
                        } else {
                            streamWriter.writeStartElement(entry.getKey());
                            serialize(newValue, streamWriter, usedLinks);
                            streamWriter.writeEndElement();
                        }
                    }
                } else {
                    for (Map.Entry<String, Method[]> entry : classUtil.getMethodTable(value.getClass()).entrySet()) {
                        Object newValue = entry.getValue()[1].invoke(value);
                        if (newValue == null) {
                            continue;
                        }
                        if (isWrapperOrPrimitive(newValue.getClass())) {
                            if (entry.getValue()[1].getAnnotation(AsXmlCdata.class) != null) {
                                streamWriter.writeStartElement(entry.getKey());
                                streamWriter.writeCData(newValue.toString());
                                streamWriter.writeEndElement();
                            } else if (entry.getValue()[1].getAnnotation(AsXmlAttribute.class) != null) {
                                //TODO Не обязательное задание
                            } else {
                                streamWriter.writeStartElement(entry.getKey());
                                serialize(newValue, streamWriter, usedLinks);
                                streamWriter.writeEndElement();
                            }
                        } else {
                            streamWriter.writeStartElement(entry.getKey());
                            serialize(newValue, streamWriter, usedLinks);
                            streamWriter.writeEndElement();
                        }
                    }
                }

                streamWriter.writeEndElement();
            }
        } catch (Exception e) {
            throw new RuntimeException("Error while serializing object: " + value.getClass().getCanonicalName());
        }

    }


    @Override
    public Object deserialize(byte[] bytes) {
        return null;
    }
}
