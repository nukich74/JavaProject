package ru.fizteh.fivt.orlovNikita.proxy;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * Package: ru.fizteh.fivt.orlovNikita.proxy
 * User: acer
 * Date: 04.12.12
 * Time: 19:34
 */
public class LoggingProxyFactoryTest {
    @Test
    public void test1() {
        ArrayList<Integer[][]> map = new ArrayList<Integer[][]>();
        StringBuilder builder = new StringBuilder();
        LoggingProxyFactory invoker = new LoggingProxyFactory();
        List proxy = (List) invoker.createProxy(map, builder, List.class);
        proxy.add(new Integer[][]{{1, 2}, {4, 5}});
        proxy.add(new Integer[][]{{1, 2}, {8, 9}});
        proxy.add(new Integer[][]{{3, 2}, {5, 6}});
        proxy.indexOf(new Integer[][]{{1, 2}, {0, 9}});
        proxy.get(0);
        proxy.get(123);
        System.out.println(invoker.getLog());
    }

    @Test
    public void test2() {
        ArrayList<String> map = new ArrayList<String>();
        StringBuilder builder = new StringBuilder();
        LoggingProxyFactory factory = new LoggingProxyFactory();
        List proxy = (List) factory.createProxy(map, builder, List.class);
        proxy.add("asd\n");
        proxy.add("\fasdasd\t\n\\");
        proxy.indexOf("asd\n");
        proxy.clear();
        System.out.println(factory.getLog());
    }


}
