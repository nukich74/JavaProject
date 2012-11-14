package ru.fizteh.fivt.orlovNikita.format.MyPack;

import ru.fizteh.fivt.orlovNikita.format.StandartPack.FormatterException;
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
        if (o == null) {
            throw new FormatterException("Got null object");
        } else if (!this.supports(o.getClass())) {
            throw new FormatterException("Don't support class: " + o.getClass().getName());
        } else {
            buffer.append((new Formatter()).format(pattern, o).toString());
        }
    }
}
