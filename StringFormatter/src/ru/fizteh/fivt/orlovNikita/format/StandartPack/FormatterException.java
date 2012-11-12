package ru.fizteh.fivt.orlovNikita.format.StandartPack;

public class FormatterException extends RuntimeException {

    public FormatterException() {
        super();
    }

    public FormatterException(String message) {
        super(message);
    }

    public FormatterException(String message, Throwable cause) {
        super(message, cause);
    }

    public FormatterException(Throwable cause) {
        super(cause);
    }

    public FormatterException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
