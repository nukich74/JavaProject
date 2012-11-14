package ru.fizteh.fivt.orlovNikita.format.MyPack;

import ru.fizteh.fivt.orlovNikita.format.StandartPack.FormatterException;
import ru.fizteh.fivt.orlovNikita.format.StandartPack.StringFormatterExtension;

import java.util.TreeSet;

class StringFormatter implements ru.fizteh.fivt.orlovNikita.format.StandartPack.StringFormatter {

    TreeSet<StringFormatterExtension> formatterExtensions;

    StringFormatter() {
        formatterExtensions = new TreeSet<StringFormatterExtension>();
    }

    public void insertExtention(StringFormatterExtension extension) {
        if (extension == null) {
            throw new FormatterException("Can't insert null pointer extention");
        }
        formatterExtensions.add(extension);
    }

    @Override
    public String format(String format, Object... args) throws FormatterException {
        StringBuilder buffer = new StringBuilder();
        this.format(buffer, format, args);
        return buffer.toString();
    }

    @Override
    public void format(StringBuilder buffer, String format, Object... args) throws FormatterException {
        parseString(buffer, format, args);
    }

    private StringBuilder processObject(String format, Object... args) {
        String pattern;
        StringBuilder res = null;
        int patternStartIndex = 0;
        if (!format.matches("[0-9][0-9]*(\\..*)*(:.+)?")) {
            throw new FormatterException("Bad format of replacing pattern");
        } else {
            while ((patternStartIndex < format.length()) && (format.charAt(patternStartIndex) != ':')) {
                patternStartIndex++;
            }
            pattern = format.substring(patternStartIndex + 1, format.length());
            String[] fieldArray = format.substring(0, patternStartIndex - 1).split("\\.");
            if (!fieldArray[0].matches("[0-9][0-9]*")) {
                throw new FormatterException("Argument object is not number");
            }

            Object object = args[Integer.valueOf(fieldArray[0])];
            try {
                for (int i = 1; i < fieldArray.length; i++) {
                    object = object.getClass().getDeclaredField(fieldArray[i]).get(object);
                }
            } catch (Exception e) {
                throw new FormatterException("No such field in class: " + object.getClass().getName());
            }
            if (pattern != null) {
                boolean haveExtention = false;
                for (StringFormatterExtension extension : formatterExtensions) {
                    if (extension.supports(object.getClass())) {
                        res = new StringBuilder();
                        extension.format(res, object, pattern);
                        haveExtention = true;
                    }
                }
                if (!haveExtention || res == null) {
                    throw new FormatterException("Don't have extention");
                }
            } else {
                res.append(object.toString());
            }
        }
        return res;
    }


    private StringBuilder parseString(StringBuilder builder, String input, Object... args) {
        try {
            for (int i = 0; i - 1 < input.length(); i++) {
                if (input.charAt(i) == '{') {
                    if (input.charAt(i + 1) == '{') {
                        builder.append("{");
                        i++;
                    } else {
                        String arg = "";
                        while (input.charAt(i) != '}') {
                            arg += input.charAt(i);
                            i++;
                        }
                        builder.append(processObject(arg, args));
                    }
                } else if (input.charAt(i) == '}') {
                    if (input.charAt(i + 1) == '}') {
                        builder.append("}");
                        i++;
                    } else {
                        throw new FormatterException("Bad format pattern");
                    }
                }
            }
        } catch (Exception e) {
            throw new FormatterException(e.getMessage());
        }

        return builder;
    }
}