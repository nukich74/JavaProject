package ru.fizteh.fivt.bind.myPack;

import ru.fizteh.fivt.bind.defPack.MembersToBind;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;
import java.io.ByteArrayInputStream;
import java.util.Scanner;

/**
 * Package: ru.fizteh.fivt.bind.myPack
 * User: acer
 * Date: 27.11.12
 * Time: 17:08
 */
public class XmlBinder extends ru.fizteh.fivt.bind.defPack.XmlBinder {

    private MembersToBind bind;
    protected XmlBinder(Class clazz) {
        ////Почему intelligi требует, чтобы я его создал?
        super(clazz);
        bind = null;
    }

    @Override
    public byte[] serialize(Object value) {
        bind = ((ru.fizteh.fivt.bind.defPack.BindingType)
                (value.getClass().getAnnotation(ru.fizteh.fivt.bind.defPack.BindingType.class))).value();

        String xmlInterpret;
        switch (bind) {
            case FIELDS:
                xmlInterpret = fieldSerialize(value);
                break;
            case GETTERS_AND_SETTERS:
                xmlInterpret = methodSerialize(value);
                break;
        }

        return new byte[0];
    }

    @Override
    public Object deserialize(byte[] bytes) throws XmlBinderException {
        Scanner scanner = new Scanner(new ByteArrayInputStream(bytes));
        XMLInputFactory inputFactory = XMLInputFactory.newInstance();
        try {
            XMLEventReader eventReader = inputFactory.createXMLEventReader(new ByteArrayInputStream(bytes));

        } catch (Exception e) {
            throw new XmlBinderException(e);
        }
        return null;
    }

    private String methodSerialize(Object value) {
        XMLOutputFactory outputFactory = XMLOutputFactory.newFactory();

        return outputFactory.toString();
    }

    private String fieldSerialize(Object value) {
        String result = "";

        return result;
    }

}
