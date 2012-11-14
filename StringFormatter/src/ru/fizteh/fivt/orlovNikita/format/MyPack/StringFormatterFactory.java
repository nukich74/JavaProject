package ru.fizteh.fivt.orlovNikita.format.MyPack;

import ru.fizteh.fivt.orlovNikita.format.StandartPack.FormatterException;

public class StringFormatterFactory implements ru.fizteh.fivt.orlovNikita.format.StandartPack.StringFormatterFactory {
    @Override
    public StringFormatter create(String... extensionClassNames) throws FormatterException {
        StringFormatter formatter = new StringFormatter();

        return formatter;
    }
}
