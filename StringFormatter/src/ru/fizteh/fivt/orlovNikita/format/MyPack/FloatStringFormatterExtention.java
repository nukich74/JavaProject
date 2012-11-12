package ru.fizteh.fivt.orlovNikita.format.MyPack;

import ru.fizteh.fivt.orlovNikita.format.StandartPack.StringFormatterExtension;

import java.util.Formatter;

public class FloatStringFormatterExtention extends StringFormatterExtension {

    protected FloatStringFormatterExtention(Class<?> clazz) {
        super(clazz);
    }

    @Override
    public void format(StringBuilder buffer, Object o, String pattern) {
        buffer = new StringBuilder();
        if (pattern == null || pattern.equals("")) {
            buffer.append((new Formatter()).format(pattern, o));
        } else {
            o.toString();
        }
    }
}
