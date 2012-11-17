package ru.fizteh.fivt.orlovNikita.format.MyPack;

import ru.fizteh.fivt.orlovNikita.format.StandartPack.FormatterException;
import ru.fizteh.fivt.orlovNikita.format.StandartPack.StringFormatterExtension;

import java.math.BigInteger;
import java.util.Formatter;

public class BigIntegerStringFormatterExtention extends StringFormatterExtension {

    protected BigIntegerStringFormatterExtention() {
        super(BigInteger.class);
    }

    @Override
    public void format(StringBuilder buffer, Object o, String pattern) {
        try {
            if (((o == null) || (buffer == null)) || (pattern == null)) {
                throw new FormatterException("Got null object");
            } else if (!this.supports(o.getClass())) {
                throw new FormatterException("Don't support class: " + o.getClass().getName());
            } else {
                buffer.append((new Formatter()).format("%" + pattern, o).toString());
            }
        } catch (Exception e) {
            throw new FormatterException("Error while casting pattern {" + pattern + "} to object");
        }
    }
}
