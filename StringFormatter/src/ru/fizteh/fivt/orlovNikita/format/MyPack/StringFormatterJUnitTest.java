package ru.fizteh.fivt.orlovNikita.format.MyPack;


import org.junit.Assert;
import org.junit.Test;
import ru.fizteh.fivt.orlovNikita.format.StandartPack.FormatterException;

import java.math.BigDecimal;
import java.math.BigInteger;

class FirstTestClass {
    public int field;
    public BigInteger bigIntegerField;

    FirstTestClass(int tField) {
        field = tField;
    }

    FirstTestClass(BigInteger tBigInteger) {
        bigIntegerField = tBigInteger;
    }
}

class SecondTestClass {
    public FirstTestClass field1;

    SecondTestClass(FirstTestClass clzz) {
        field1 = clzz;
    }
}

class ThirdTestClass {
    public SecondTestClass field2;

    ThirdTestClass(SecondTestClass clzz) {
        field2 = clzz;
    }
}

class FourthTestClass {
    public ThirdTestClass field3;

    FourthTestClass(ThirdTestClass tField) {
        field3 = tField;
    }
}

public class StringFormatterJUnitTest extends Assert {

    @Test
    public void testFactoryExtention() throws Exception {
        StringFormatterFactory factory = new StringFormatterFactory();
        StringFormatter formatter = factory.create("ru.fizteh.fivt.orlovNikita.format.MyPack.BigIntegerStringFormatterExtention",
                "ru.fizteh.fivt.orlovNikita.format.MyPack.FloatStringFormatterExtention");
        assertTrue(formatter.containsExtention(BigInteger.class));
        assertTrue(formatter.containsExtention(Float.class));
    }

    @Test
    public void testFactoryExtentionException() throws Exception {
        StringFormatterFactory factory = new StringFormatterFactory();
        StringFormatter formatter = factory.create("ru.fizteh.fivt.orlovNikita.format.MyPack.BigIntegerStringFormatterExtention",
                "ru.fizteh.fivt.orlovNikita.format.MyPack.FloatStringFormatterExtention");
        assertTrue(!formatter.containsExtention(BigDecimal.class));
    }

    @Test
    public void testStringFormatter() {
        StringFormatter formatter = new StringFormatter();
        assertTrue(formatter.format("Empty String").equals("Empty String"));
        assertTrue(formatter.format("Empty String and Int: {0}", 123).equals("Empty String and Int: 123"));
        assertTrue(formatter.format("{{Empty}}{0} String and Int: {{{0}, {1}, {0}}}", 123, 1234).equals("{Empty}123 String and Int: {123, 1234, 123}"));
        assertTrue(formatter.format("{{{0.field} {1.field1.field} {2.field2.field1.field}}}", new FirstTestClass(123),
                new SecondTestClass(new FirstTestClass(124)),
                new ThirdTestClass(new SecondTestClass(new FirstTestClass(125)))).equals("{123 124 125}"));
    }

    @Test
    public void testStringFormatterPattern() {
        StringFormatterFactory factory = new StringFormatterFactory();
        StringFormatter formatter = factory.create("ru.fizteh.fivt.orlovNikita.format.MyPack.BigIntegerStringFormatterExtention",
                "ru.fizteh.fivt.orlovNikita.format.MyPack.FloatStringFormatterExtention");
        assertTrue(formatter.format("{0:10f}", (float) 3.0 / 2).equals(String.format("%10f", (float) 3.0 / 2)));
        assertTrue(formatter.format("{0:.5f}", (float) 3.0 / 2).equals(String.format("%.5f", (float) 3.0 / 2)));
        assertTrue(formatter.format("{0:E}", (float) 3.0 / 2).equals(String.format("%E", (float) 3.0 / 2)));
        assertTrue(formatter.format("{0:o}", (new BigInteger("11111111111111111"))).equals(String.format("%o", (new BigInteger("11111111111111111")))));
        assertTrue(formatter.format("{0:0#30X}", (new BigInteger("11111111111111111"))).equals(String.format("%0#30X", (new BigInteger("11111111111111111")))));
        assertTrue(formatter.format("{0.field3.field2.field1.bigIntegerField:0#+20o}",
                new FourthTestClass(new ThirdTestClass(new SecondTestClass(new FirstTestClass(new BigInteger("125")))))).equals(String.format("%0#+20o", new BigInteger("125"))));
    }

    @Test(expected = FormatterException.class)
    public void testStringFormatterException() {
        try {
            StringFormatter formatter = new StringFormatter();
            formatter.format("{0.#.@:^}", 123);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            throw new FormatterException(e);
        }
    }

    @Test(expected = FormatterException.class)
    public void testStringFormatterException2() {
        try {
            StringFormatter formatter = new StringFormatter();
            formatter.format("Empty string {{}{}}");
        } catch (Exception e) {
            System.out.println(e.getMessage());
            throw new FormatterException(e);
        }
    }

}