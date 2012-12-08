package ru.fizteh.fivt.orlovNikita.proxy;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashMap;

/**
 * Package: ru.fizteh.fivt.orlovNikita.proxy
 * User: acer
 * Date: 04.12.12
 * Time: 17:37
 */
public class LoggingProxyFactory implements ru.fizteh.fivt.proxy.LoggingProxyFactory {

    private StringBuilder builder;
    private HashMap<Method, Class> methods;

    LoggingProxyFactory() {
        methods = new HashMap<Method, Class>();       ///!!!
        builder = new StringBuilder();
    }


    public class InvocationHandler implements java.lang.reflect.InvocationHandler {
        private Object invokeObject;
        private HashMap<Method, Class> methods;
        String extendedChar = "";

        InvocationHandler(Object target, HashMap<Method, Class> in) {
            methods = in;
            invokeObject = target;
        }

        public boolean isWrapperOrPrimitive(Class<?> clazz) {
            return clazz.isPrimitive() || clazz.equals(Boolean.class) ||
                    clazz.equals(Byte.class) || clazz.equals(Character.class) ||
                    clazz.equals(Short.class) || clazz.equals(Integer.class) ||
                    clazz.equals(Long.class) || clazz.equals(Float.class) ||
                    clazz.equals(Double.class) || clazz.isEnum();
        }

        StringBuilder buildStringValue(StringBuilder tempBuilder, String o) {
            String value = (String) o;
            tempBuilder.append("\"");
            for (int j = 0; j < value.length(); j++) {
                switch (value.charAt(j)) {
                    case '\t':
                        tempBuilder.append("\\t");
                        break;
                    case '\r':
                        tempBuilder.append("\\r");
                        break;
                    case '\n':
                        tempBuilder.append("\\n");
                        break;
                    case '\f':
                        tempBuilder.append("\\f");
                        break;
                    case '\'':
                        tempBuilder.append("\\'");
                    case '\"':
                        tempBuilder.append("\\\"");
                    case '\\':
                        tempBuilder.append("\\");
                    default:
                        tempBuilder.append(value.charAt(j));
                        break;
                }
            }
            tempBuilder.append("\"");
            return tempBuilder;
        }

        public StringBuilder buildArrayStringValue(StringBuilder builder, Object array) {
            if (isWrapperOrPrimitive(array.getClass())) {
                builder.append(array).append(extendedChar);
            } else if (array.getClass().equals(String.class)) {
                StringBuilder tbuilder = new StringBuilder();
                tbuilder = buildStringValue(tbuilder, (String)array);
                if (tbuilder.length() > 60) {
                    extendedChar = "\n  ";
                    builder.append("[a very long result of toString() method of " + array.getClass().getSimpleName() + " with huge amount of data]").
                            append(extendedChar);
                } else {
                    builder.append(tbuilder).append(extendedChar);
                }
            } else if (array.getClass().isArray()) {
                builder.append(((Object[]) array).length);
                builder.append("{");
                for (int i = 0; i < ((Object[]) array).length; i++) {
                    if (i != 0) {
                        builder.append(", ");
                    }
                    Object tarray = ((Object[]) array)[i];
                    builder = buildArrayStringValue(builder, tarray).append(extendedChar);
                }
                builder.append("}");
            } else {
                builder.append(array.toString()).append(extendedChar);
            }
            return builder;
        }

        void appendObject(Object o) throws Exception {
            if (o.equals(null)) {
                builder.append("null");
            }
            if (isWrapperOrPrimitive(o.getClass())) {
                if (o.toString().length() > 60) {
                    extendedChar = "\n  ";
                    builder.append("[a very long result of toString() method of " + o.getClass().getSimpleName() + " with huge amount of data]").append(extendedChar);
                } else {
                    builder.append(o.toString()).append(extendedChar);
                }
            } else if (o.getClass().isArray()) {
                StringBuilder tbuilder = new StringBuilder();
                tbuilder = buildArrayStringValue(tbuilder, o);
                if (tbuilder.length() > 60) {
                    extendedChar = "\n  ";
                    builder.append("[a very long result of toString() method of " + o.getClass().getSimpleName() + " with huge amount of data]").append(extendedChar);
                } else {
                    builder.append(tbuilder).append(extendedChar);
                }
            } else if (o.getClass().equals(String.class)) {
                StringBuilder tempBuilder = new StringBuilder();
                tempBuilder = buildStringValue(tempBuilder, (String) o);
                if (tempBuilder.length() > 60) {
                    extendedChar = "\n  ";
                    builder.append("[a very long result of toString() method of " + o.getClass().getSimpleName() + " with huge amount of data]").append(extendedChar);
                } else {
                    builder.append(tempBuilder.toString()).append(extendedChar);
                }
            }

        }

        @Override
        public Object invoke(Object proxy, Method method,
                             Object[] args) throws Throwable {
            extendedChar = "";
            if ((proxy == null) || (method == null) || ((args == null) && (method.getParameterTypes().length != 0))) {
                throw new RuntimeException("Some args are null");
            } else {
                builder.append("\n");

                if (methods.containsKey(method)) {
                    builder.append(this.methods.get(method).getSimpleName()).append(".").append(method.getName()).append("(");
                    if (args != null) {
                        for (int i = 0; i < args.length; i++) {
                            if (i != 0) {
                                builder.append(", ").append(extendedChar);
                            }
                            try {
                                appendObject(args[i]);
                            } catch (IOException e) {
                                throw new RuntimeException("Can't append object in args!");
                            }
                        }
                    }
                    builder.append(")").append(extendedChar);
                    try {
                        Object ret = method.invoke(invokeObject, args);
                        if (!(method.getReturnType().equals(void.class))) {
                            builder.append(" returned ").append(ret.toString()).append(extendedChar);
                        }
                        return ret;
                    } catch (Exception e) {
                        StringWriter sw = new StringWriter();
                        PrintWriter writer = new PrintWriter(sw);
                        e.printStackTrace(writer);
                        builder.append(" threw ").append(e.getClass().toString()).append(" : ").
                                append(e.getMessage()).append("\n").append(sw.toString());
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
                for (Method method : val.getDeclaredMethods()) {
                    this.methods.put(method, val);
                }
            }
        }

        return Proxy.newProxyInstance(target.getClass().getClassLoader(),
                interfaces, new InvocationHandler(target, methods));

    }

    String getLog() {
        return builder.toString();
    }
}
