package ru.fizteh.fivt.orlovNikita.proxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * Package: ru.fizteh.fivt.orlovNikita.proxy
 * User: acer
 * Date: 04.12.12
 * Time: 17:37
 */
public class LoggingProxyFactory implements ru.fizteh.fivt.proxy.LoggingProxyFactory {

    public static boolean isWrapperOrPrimitive(Class<?> clazz) {
        return clazz.isPrimitive() || clazz.equals(Boolean.class) ||
                clazz.equals(Byte.class) || clazz.equals(Character.class) ||
                clazz.equals(Short.class) || clazz.equals(Integer.class) ||
                clazz.equals(Long.class) || clazz.equals(Float.class) ||
                clazz.equals(Double.class) || clazz.isEnum();
    }


    @Override
    public Object createProxy(Object target, Appendable writer, Class... interfaces) {
        return Proxy.newProxyInstance(((Class) target).getClassLoader(),
                new Class[]{(Class) target},
                new InvocationHandler() {

                    public StringBuilder buildArrayStringValue(StringBuilder builder, Object... array) {
                        if (array.length == 1) {
                            if (isWrapperOrPrimitive(array.getClass())) {
                                builder.append(array);
                            } else {
                                buildArrayStringValue(builder, array[0]);
                            }
                        } else {
                            builder.append(array.length).append("{");
                            for (int i = 0; i < array.length; i++) {
                                if (i != 0) {
                                    builder.append(", ");
                                }
                                builder = buildArrayStringValue(builder, array[i]);
                            }
                            builder.append("}");
                        }
                        return builder;
                    }

                    @Override
                    public Object invoke(Object proxy, Method method,
                                         Object[] args) throws Throwable {
                        if ((proxy == null) || (method == null) || (args == null)) {
                            throw new RuntimeException("Some args are null");
                        } else {
                            Object ret = method.invoke(proxy, args);
                            StringBuilder builder = new StringBuilder();
                            builder.append(method.getName()).append("(");

                            for (int i = 0; i < builder.length(); i++) {
                                if (isWrapperOrPrimitive(args[i].getClass())) {
                                    builder.append(args[i].toString());
                                } else if (args[i].getClass().isArray()) {
                                    builder = buildArrayStringValue(builder, args[i]);
                                } else if (args[i].getClass().equals(String.class)) {
                                    String value = (String)args[i];
                                    builder.append("\"");
                                    for (int j = 0; j < value.length(); j++) {
                                        if (value.charAt(i) == '\\') {
                                            builder.append('\\');
                                        }
                                        builder.append(value.charAt(i));
                                    }
                                    builder.append("\"");
                                }
                            }
                            return ret;
                        }
                    }
                });
    }
}
