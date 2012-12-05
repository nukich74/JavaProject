package ru.fizteh.fivt.orlovNikita.proxy;

import java.io.IOException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Collections;
import java.util.HashSet;

/**
 * Package: ru.fizteh.fivt.orlovNikita.proxy
 * User: acer
 * Date: 04.12.12
 * Time: 17:37
 */
public class LoggingProxyFactory implements ru.fizteh.fivt.proxy.LoggingProxyFactory {

    private StringBuilder builder;
    private HashSet<Method> methods;

    LoggingProxyFactory() {
        methods = new HashSet<Method>();       ///!!!
        builder = new StringBuilder();
    }


    public class InvocationHandler implements java.lang.reflect.InvocationHandler {
        private Object invokeObject;

        InvocationHandler(Object target) {
            invokeObject = target;
        }

        public boolean isWrapperOrPrimitive(Class<?> clazz) {
            return clazz.isPrimitive() || clazz.equals(Boolean.class) ||
                    clazz.equals(Byte.class) || clazz.equals(Character.class) ||
                    clazz.equals(Short.class) || clazz.equals(Integer.class) ||
                    clazz.equals(Long.class) || clazz.equals(Float.class) ||
                    clazz.equals(Double.class) || clazz.isEnum();
        }

        public StringBuilder buildArrayStringValue(StringBuilder builder, Object array) {
            if (isWrapperOrPrimitive(array.getClass())) {
                builder.append(array);
            } else if (array.getClass().isArray()) {
                builder.append(((Object[]) array).length);
                builder.append("{");
                for (int i = 0; i < ((Object[]) array).length; i++) {
                    if (i != 0) {
                        builder.append(", ");
                    }
                    Object tarray = ((Object[]) array)[i];
                    builder = buildArrayStringValue(builder, tarray);
                }
                builder.append("}");
            } else {
                builder.append(array.toString());
            }
            return builder;
        }

        void appendObject(Object o) throws Exception {
            if (o.equals(null)) {
                builder.append("null");
            }
            if (isWrapperOrPrimitive(o.getClass())) {
                builder.append(o.toString());
            } else if (o.getClass().isArray()) {
                builder = buildArrayStringValue(builder, o);
            } else if (o.getClass().equals(String.class)) {
                String value = (String) o;
                builder.append("\"");
                for (int j = 0; j < value.length(); j++) {
                        switch (value.charAt(j)) {
                            case '\t':
                                builder.append("\\t");
                                break;
                            case '\r':
                                builder.append("\\r");
                                break;
                            case '\n':
                                builder.append("\\n");
                                break;
                            default:
                                builder.append(value.charAt(j));
                                break;
                        }
                }
                builder.append("\"");
            }

        }

        @Override
        public Object invoke(Object proxy, Method method,
                             Object[] args) throws Throwable {
            if ((proxy == null) || (method == null) || ((args == null) && (method.getParameterTypes().length != 0))) {
                throw new RuntimeException("Some args are null");
            } else {
                builder.append("\n");

                if (methods.contains(method)) {
                    builder.append(method.getName()).append("(");
                    if (args == null) {
                        builder.append(proxy.getClass().getSimpleName()).append(".").append(method.getName());
                    } else
                        for (int i = 0; i < args.length; i++) {
                            if (i != 0) {
                                builder.append(", ");
                            }
                            try {
                                appendObject(args[i]);
                            } catch (IOException e) {
                                throw new RuntimeException("Can't append object in args!");
                            }
                        }
                    builder.append(")");
                    try {
                        Object ret = method.invoke(invokeObject, args);
                        if (!(method.getReturnType().equals(void.class))) {
                            builder.append("-->").append(ret.toString());
                        }
                        return ret;
                    } catch (Exception e) {
                        builder.append("threw ").append(e.getClass().toString()).append(" : ").append(e.getMessage());
                        //throw new RuntimeException("Exception when invoke!");
                    }
                } else {
                    throw new RuntimeException("");
                }
            }
            return null;
        }
    }

    @Override
    public Object createProxy(Object target, Appendable writer, Class... interfaces) {
        builder = (StringBuilder) writer;
        for (Class val : interfaces) {
            if (!val.isInterface()) {
                throw new RuntimeException("value is not interface");
            } else {
                Collections.addAll(methods, val.getDeclaredMethods());
            }
        }

        return Proxy.newProxyInstance(target.getClass().getClassLoader(),
                interfaces, new InvocationHandler(target));

    }

    String getLog() {
        return builder.toString();
    }
}
