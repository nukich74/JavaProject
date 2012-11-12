package ru.fizteh.fivt.orlovNikita.format.MyPack;

import ru.fizteh.fivt.orlovNikita.format.StandartPack.FormatterException;
import ru.fizteh.fivt.orlovNikita.format.StandartPack.StringFormatter;
import ru.fizteh.fivt.orlovNikita.format.StandartPack.StringFormatterFactory;

public class MyStringFormatterFactory implements StringFormatterFactory {
    @Override
    public StringFormatter create(String... extensionClassNames) throws FormatterException {
        FullStringFormatter formatter = new FullStringFormatter();

        return formatter;
    }
}
