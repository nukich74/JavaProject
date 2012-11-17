/**
 * Created with IntelliJ IDEA.
 * User: acer
 * Date: 13.11.12
 * Time: 21:10
 * To change this template use File | Settings | File Templates.
 */
public class B extends A{
    protected B(Class<?> clazz) {
        super(clazz);
    }

    @Override
    public void format(StringBuilder buffer, Object o, String pattern) {
        System.out.println("formating from B");
    }
}
