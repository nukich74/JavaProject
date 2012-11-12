package ru.fizteh.fivt.orlovNikita.format.MyPack;

import ru.fizteh.fivt.orlovNikita.format.StandartPack.StringFormatterExtension;

import java.util.Formatter;

/**
 * Created with IntelliJ IDEA.
 * User: acer
 * Date: 10.11.12
 * Time: 17:57
 * To change this template use File | Settings | File Templates.
 */
public class BigIntegerStringFormatterExtention extends StringFormatterExtension {

    protected BigIntegerStringFormatterExtention(Class<?> clazz) {
        super(clazz);
    }

    @Override
    public void format(StringBuilder buffer, Object o, String pattern) {
        buffer = new StringBuilder();
        if (pattern == null || pattern.equals("")) {
            buffer.append(o.toString());
        } else {
            buffer.append((new Formatter()).format(pattern, o));
        }
    }
}
