package ru.fizteh.fivt.orlovNikita.proxy;

import org.junit.Test;

import java.util.HashMap;

/**
 * Package: ru.fizteh.fivt.orlovNikita.proxy
 * User: acer
 * Date: 04.12.12
 * Time: 19:34
 */
public class LoggingProxyFactoryTest {
    @Test
    public void test1() {
        StringBuilder builder = new StringBuilder();
        Object invoker = new LoggingProxyFactory().createProxy(new HashMap <Integer[], String>().getClass(), builder);

    }

}
