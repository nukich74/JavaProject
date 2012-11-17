/**
 * Created with IntelliJ IDEA.
 * User: acer
 * Date: 13.11.12
 * Time: 20:32
 * To change this template use File | Settings | File Templates.
 */
public abstract class A {
    protected final Class<?> clazz;

    protected A(Class<?> clazz) {
        System.out.println("using A constructor");
        this.clazz = clazz;
    }

    public boolean supports(Class<?> clazz) {
        return this.clazz.equals(clazz);
    }

    public abstract void format(StringBuilder buffer, Object o, String pattern);
}
