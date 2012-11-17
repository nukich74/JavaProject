package ru.fizteh.fivt.orlovNikita.format.MyPack;

import ru.fizteh.fivt.orlovNikita.format.StandartPack.FormatterException;
import ru.fizteh.fivt.orlovNikita.format.StandartPack.StringFormatterExtension;

public class StringFormatterFactory implements ru.fizteh.fivt.orlovNikita.format.StandartPack.StringFormatterFactory {
    @Override
    public StringFormatter create(String... extensionClassNames) throws FormatterException {
        StringFormatter formatter = new StringFormatter();
        for (String extentionName : extensionClassNames) {
            try {
                StringFormatterExtension extension = (StringFormatterExtension) Class.forName(extentionName).newInstance();
                formatter.insertExtention(extension);
            } catch (Exception e) {
                throw new FormatterException("Can't insert extention");
            }
        }
        return formatter;
    }
}
