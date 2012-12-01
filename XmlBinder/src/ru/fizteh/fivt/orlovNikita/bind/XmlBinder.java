package ru.fizteh.fivt.orlovNikita.bind;

import com.sun.xml.internal.messaging.saaj.util.ByteInputStream;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import ru.fizteh.fivt.bind.AsXmlAttribute;
import ru.fizteh.fivt.bind.AsXmlCdata;
import ru.fizteh.fivt.bind.BindingType;
import ru.fizteh.fivt.bind.MembersToBind;
import sun.misc.Unsafe;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import java.io.StringWriter;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Map;

/**
 * Package: ru.fizteh.fivt.bind.myPack
 * User: acer
 */
public class XmlBinder<T> extends ru.fizteh.fivt.bind.XmlBinder {
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
                String fileName = value.getClass().getSimpleName();
                fileName = String.valueOf(fileName.charAt(0)).toLowerCase() + fileName.substring(1);
                streamWriter.writeStartElement(fileName);
                serialize(value, streamWriter, usedLinks);
                streamWriter.writeEndElement();
            } catch (XMLStreamException e) {
                System.err.print("Can't create streamWriter. Error\n");
                return null;
            } finally {
                if (streamWriter != null) {
                    ///       streamWriter.close();
                }
            }
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

    private void chooseNextSerializationFieldState(Map.Entry<String, Field> entry, Object newValue, XMLStreamWriter streamWriter, IdentityHashMap<Object, Integer> usedLinks) {
        try {
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
        } catch (Exception e) {
            throw new RuntimeException("Error while serializing object: " + newValue.getClass().getCanonicalName());
        }
    }

    private void chooseNextSerializationMethodState(Map.Entry<String, Method[]> entry, Object newValue, XMLStreamWriter streamWriter, IdentityHashMap<Object, Integer> usedLinks) {
        try {
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
        } catch (Exception e) {
            throw new RuntimeException("Error while serializing object: " + newValue.getClass().getCanonicalName());
        }
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
                BindingType type = value.getClass().getAnnotation(BindingType.class);
                if ((type == null) || (type.value().equals(MembersToBind.FIELDS))) {
                    for (Map.Entry<String, Field> entry : classUtil.getFieldsTable(value.getClass()).entrySet()) {
                        Object newValue = entry.getValue().get(value);
                        if (newValue != null) {
                            chooseNextSerializationFieldState(entry, newValue, streamWriter, usedLinks);
                        }
                    }
                } else {
                    for (Map.Entry<String, Method[]> entry : classUtil.getMethodTable(value.getClass()).entrySet()) {
                        Object newValue = entry.getValue()[1].invoke(value);
                        if (newValue != null) {
                            chooseNextSerializationMethodState(entry, newValue, streamWriter, usedLinks);
                        }
                    }
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Error while serializing object: " + value.getClass().getCanonicalName());
        }

    }

    @Override
    public Object deserialize(byte[] bytes) {
        if (bytes == null || bytes.length == 0) {
            throw new RuntimeException("Can't deserialize!");
        } else {
            try {
                Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new ByteInputStream(bytes, bytes.length));
                if (!(String.valueOf(document.getDocumentElement().getTagName().charAt(0)).toUpperCase() + document.getDocumentElement().getTagName().substring(1)).
                        equals(this.getClazz().getSimpleName())) {
                    throw new RuntimeException("Can't deserialize this Xml, because of the not equal types");
                } else {
                    return (T) deserialize(document.getDocumentElement(), this.getClazz());
                }
            } catch (Exception e) {
                throw new RuntimeException("Can't deserialize!");
            }

        }
    }

    private Object deserialize(Element documentElement, Class clazz) {
        if (isWrapperOrPrimitive(clazz)) {
            if (clazz.isPrimitive() || clazz.isEnum()) {
                return invokeForPrimitive(documentElement.getTextContent(), clazz);
            } else
                if (clazz.equals(String.class)) {
                    return documentElement.getTextContent();
                }
                try {
                    return clazz.getDeclaredMethod("valueOf").invoke(documentElement.getTextContent());
                }  catch (Exception e) {
                    throw new RuntimeException("No method valueOf in wrapper of primitive");
                }
        }
        Object finalObj = null;
        try {
            finalObj = newInstance(clazz);
            BindingType type = (BindingType) clazz.getAnnotation(BindingType.class);
            NodeList nodeList = documentElement.getChildNodes();
            if (nodeList == null) {
                throw new RuntimeException("No such classes to deserialize");
            } else if ((type == null) || (type.value().equals(MembersToBind.FIELDS))) {
                HashMap<String, Field> fieldMap = classUtil.getFieldsTable(clazz);
                if (fieldMap == null) {
                    throw new RuntimeException("No structure for class");
                }
                for (int i = 0; i < nodeList.getLength(); i++) {
                    Field field = fieldMap.get(((Element) nodeList.item(i)).getTagName());
                    if ((field != null) && (nodeList.item(i).getNodeType() == (Node.ELEMENT_NODE))) {
                        field.set(finalObj, deserialize((Element) nodeList.item(i), field.getType()));
                    } else
                    if (!field.getType().isPrimitive()) {
                        field.set(finalObj, null);
                    }
                }
            } else {
                HashMap<String, Method[]> methodMap = classUtil.getMethodTable(clazz);
                if (methodMap == null) {
                    throw new RuntimeException("No structure for class");
                }
                for (int i = 0; i < nodeList.getLength(); i++) {
                    Method[] array = methodMap.get(((Element) nodeList.item(i)).getTagName());
                    if ((array != null) && (nodeList.item(i).getNodeType() == (Node.ELEMENT_NODE))) {
                        array[0].invoke(finalObj, deserialize((Element) nodeList.item(i), array[1].getReturnType()));
                    }
                }
            }
        } catch (Exception ignored) {
            throw new RuntimeException("Error while deserializing class: " + clazz.getSimpleName());
        } finally {
            return finalObj;
        }
    }

    private Object newInstance(Class clazz) throws Exception {
        Constructor constructor = classUtil.getConstructor(clazz);
        if (constructor == null) {
            return unsafe.allocateInstance(clazz);
        } else {
            return constructor.newInstance();
        }
    }

    private Object invokeForPrimitive(String textContent, Class clazz) {
        if (clazz.equals(boolean.class)) {
            return Boolean.valueOf(textContent);
        } else if (clazz.equals(float.class)) {
            return Float.valueOf(textContent);
        } else if (clazz.equals(double.class)) {
            return Double.valueOf(textContent);
        } else if (clazz.equals(int.class)) {
            return Integer.valueOf(textContent);
        } else if (clazz.equals(long.class)) {
            return Long.valueOf(textContent);
        } else if (clazz.isEnum()) {
            return Enum.valueOf(clazz, textContent);
        } else if (clazz.equals(short.class)) {
            return Short.valueOf(textContent);
        } else if (clazz.equals(byte.class)) {
            return Byte.valueOf(textContent);
        } else if (clazz.equals(char.class)) {
            if (textContent.length() != 1) {
                throw new RuntimeException("Bad Xml type!");
            } else
                return textContent.charAt(0);
        } else {
            return null;
        }
    }
}