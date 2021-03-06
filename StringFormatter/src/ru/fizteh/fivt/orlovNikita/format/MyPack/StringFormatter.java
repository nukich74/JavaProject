package ru.fizteh.fivt.orlovNikita.format.MyPack;

import ru.fizteh.fivt.orlovNikita.format.StandartPack.FormatterException;
import ru.fizteh.fivt.orlovNikita.format.StandartPack.StringFormatterExtension;

import java.util.ArrayList;


class StringFormatter implements ru.fizteh.fivt.orlovNikita.format.StandartPack.StringFormatter {

    ArrayList<StringFormatterExtension> formatterExtensions;

    public StringFormatter() {
        formatterExtensions = new ArrayList<StringFormatterExtension>();
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
        StringBuilder res = new StringBuilder();
        int patternStartIndex = 0;


        if (!format.matches("[0-9][0-9]*(\\..*)*(:.+)?")) {
            throw new FormatterException("Bad format of replacing pattern");
        } else {
            while ((patternStartIndex < format.length()) && (format.charAt(patternStartIndex) != ':')) {
                patternStartIndex++;
            }
            if (patternStartIndex == format.length()) {
                pattern = null;
            } else {
                pattern = format.substring(patternStartIndex + 1, format.length());
            }

            String[] fieldArray = format.substring(0, patternStartIndex).split("\\.");
            if (!fieldArray[0].matches("[0-9][0-9]*")) {
                throw new FormatterException("Argument object is not number");
            }

            if (Integer.valueOf(fieldArray[0]) > args.length) {
                throw new FormatterException("Not enougth params or error in index in pattern");
            }

            Object object = args[Integer.valueOf(fieldArray[0])];
            try {
                for (int i = 1; i < fieldArray.length; i++) {
                    object = object.getClass().getDeclaredField(fieldArray[i]).get(object);
                }
            } catch (Exception e) {
                throw new FormatterException("Field is private or no such field in class: " + object.getClass().getName());
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
                if ((!haveExtention) || (res == null)) {
                    throw new FormatterException("Don't have extention");
                }
            } else {
                String s = object.toString();
                res.append(s);
            }
        }
        return res;
    }


    private StringBuilder parseString(StringBuilder builder, String input, Object... args) {
        try {
            for (int i = 0; i < input.length(); i++) {
                if (input.charAt(i) == '{') {
                    if (input.charAt(i + 1) == '{') {
                        builder.append("{");
                        i++;
                    } else {
                        String arg = "";
                        i++;
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
                } else {
                    builder.append(input.charAt(i));
                }
            }
        } catch (Exception e) {
            throw new FormatterException(e.getMessage());
        }

        return builder;
    }

    public boolean containsExtention(Class clazz) {
        for (StringFormatterExtension extension : formatterExtensions) {
            if (extension.supports(clazz)) {
                return true;
            }
        }
        return false;
    }

}