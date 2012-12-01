package ru.fizteh.fivt.bind.myPack;

import ru.fizteh.fivt.bind.defPack.BindingType;
import ru.fizteh.fivt.bind.defPack.MembersToBind;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;

/**
 * Package: ru.fizteh.fivt.bind.myPack
 * User: acer
 * Date: 30.11.12
 * Time: 23:53
 */
public class ClassUtils {
    Class clazz;
    HashMap<Class, HashMap<String, Field>> fieldMap;
    HashMap<Class, HashMap<String, Method[]>> methodMap;
    HashMap<String, Constructor> constructorMap;
    HashSet<Class> readyClasses;

    IdentityHashMap<Object, Integer> linkMap;

    ClassUtils(Class clzz) {
        this.clazz = clzz;
        readyClasses = new HashSet<Class>();
        constructorMap = new HashMap<String, Constructor>();
        fieldMap = new HashMap<Class, HashMap<String, Field>>();
        methodMap = new HashMap<Class, HashMap<String, Method[]>>();

        this.classPrepare(this.clazz);
    }

    private Constructor getConstructor(Class clazz) {
        try {
            Constructor constructor = clazz.getConstructor();
            constructor.setAccessible(true);
            return constructor;
        } catch (NoSuchMethodException e) {
            return null;
        }
    }

    private void classPrepare(Class clzz) {
        constructorMap.put(clzz.getName(), this.getConstructor(clzz));
        if (!readyClasses.contains(clzz)) {
            boolean fieldBind = false;
            BindingType type = (BindingType) clzz.getAnnotation(BindingType.class);
            if (type == null || type.value().equals(MembersToBind.FIELDS)) {
                fieldBind = true;
            }

            if (fieldBind) {
                ArrayList<Field> fields = getFieldsArray(clzz);
                HashMap<String, Field> tempMap = new HashMap<String, Field>();
                for (Field field : fields) {
                    field.setAccessible(true);
                    tempMap.put(field.getName(), field);
                }
                fieldMap.put(clzz, tempMap);
                for (Field field : fields) {
                    classPrepare(field.getType());
                }
            } else {
                ArrayList<Method[]> methods = getMethodArray(clzz);
                HashMap<String, Method[]> tempMap = new HashMap<String, Method[]>();
                for (Method[] pair: methods) {
                    tempMap.put(pair[0].getName().replaceFirst("set", ""), pair);
                }
                methodMap.put(clzz, tempMap);
                for (Method[] pair: methods) {
                    classPrepare(pair[0].getReturnType());
                }
            }

        } else {
            return;
        }
    }

    private ArrayList<Field> getFieldsArray(Class clazz) {
        ArrayList<Field> arrayList = new ArrayList<Field>();
        while (clazz != null) {
            arrayList.addAll(Arrays.asList(clazz.getFields()));
            clazz.getSuperclass();
        }
        return arrayList;
    }

    private ArrayList<Method[]> getMethodArray(Class clazz) {
        ArrayList<Method[]> arrayList = new ArrayList<Method[]>();
        Method[] methods = clazz.getMethods();
        for (Method method : methods) {
            if (method.getName().matches("set.+")) {
                if (method.getParameterTypes().length != 1 || method.getReturnType().equals(void.class)) {
                    continue;
                }
            }
            try {
                Method getMethod = clazz.getMethod(method.getName().replaceFirst("set", "get"));
                if (getMethod.getReturnType().equals(method.getParameterTypes()[0])) {
                    arrayList.add(new Method[]{method, getMethod});
                    arrayList.get(arrayList.size() - 1)[0].setAccessible(true);
                    arrayList.get(arrayList.size() - 1)[1].setAccessible(true);
                    continue;
                }
                Method isMethod = clazz.getMethod(method.getName().replaceFirst("set", "is"));
                if ((isMethod.getReturnType().equals(method.getTypeParameters()[0])) &&
                        (isMethod.getReturnType().equals(Boolean.class)) &&
                        (method.getParameterTypes()[0].equals(Boolean.class))) {
                    arrayList.add(new Method[]{method, isMethod});
                    arrayList.get(arrayList.size() - 1)[0].setAccessible(true);
                    arrayList.get(arrayList.size() - 1)[1].setAccessible(true);
                    continue;
                }
                throw new NoSuchMethodException();
            } catch (NoSuchMethodException e) {
                //Can't understand what to write here, because I prevent any exceptions, but I really need to catch (NSME e)
                //But don't need to announce it, this occasion is normal;
            }
        }

        return arrayList;
    }

}
