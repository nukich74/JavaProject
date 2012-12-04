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

    @Override
    public Object createProxy(Object target, Appendable writer, Class... interfaces) {
        return Proxy.newProxyInstance(((Class) target).getClassLoader(),
                new Class[]{(Class) target},
                new InvocationHandler() {
                    public Object invoke(Object proxy, Method method,
                                         Object[] args) throws Throwable {
                        if ((proxy == null) || (method == null) || (args == null)) {
                            throw new RuntimeException("Some args are null");
                        } else {
                            Object ret = method.invoke(proxy, args);
                            StringBuilder builder = new StringBuilder();

                            return ret;
                        }
                    }
                });
    }
}
}
