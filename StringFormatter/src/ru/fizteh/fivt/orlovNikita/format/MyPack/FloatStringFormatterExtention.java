package ru.fizteh.fivt.orlovNikita.format.MyPack;

import ru.fizteh.fivt.orlovNikita.format.StandartPack.FormatterException;
import ru.fizteh.fivt.orlovNikita.format.StandartPack.StringFormatterExtension;

import java.util.Formatter;

public class FloatStringFormatterExtention extends StringFormatterExtension {

    protected FloatStringFormatterExtention() {
        super(Float.class);
    }

    @Override
    public void format(StringBuilder buffer, Object o, String pattern) {
        try {
            if (((o == null) || (buffer == null)) || (pattern == null)) {
                throw new FormatterException("Got null object");
            }
            if (!this.supports(o.getClass())) {
                throw new FormatterException("Don't support this class: " + o.getClass().getName());
            } else {
                buffer.append((new Formatter()).format("%" + pattern, o).toString());
            }
        } catch (Exception e) {
            throw new FormatterException("Error while casting pattern {" + pattern + "} to object");
        }
    }
}
