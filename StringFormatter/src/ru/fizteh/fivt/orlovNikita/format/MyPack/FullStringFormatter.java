package ru.fizteh.fivt.orlovNikita.format.MyPack;

import ru.fizteh.fivt.orlovNikita.format.StandartPack.FormatterException;
import ru.fizteh.fivt.orlovNikita.format.StandartPack.StringFormatter;

import java.util.TreeSet;

public class FullStringFormatter implements StringFormatter {

    TreeSet<Class> classCollection;

    @Override
    public String format(String format, Object... args) throws FormatterException {
        StringBuilder buffer = new StringBuilder();
        this.format(buffer, format, args);
        return buffer.toString();
    }

    @Override
    public void format(StringBuilder buffer, String format, Object... args) throws FormatterException {
        buffer = new StringBuilder();
    }

    public void parseObjectArgs(Object... args) {
        classCollection = new TreeSet<Class>();
        for (Object o : args) {
            classCollection.add(o.getClass());
        }
    }
}
