package ru.fizteh.fivt.orlovNikita.bind;

import org.junit.Test;
import ru.fizteh.fivt.bind.test.*;
import ru.fizteh.fivt.orlovNikita.bind.test.ClassBadSerialisation;

public class XmlBinderTest {

    @Test
    public void easyTest() {
        XmlBinder<User> binder = new XmlBinder<User>(User.class);
        Permissions permissions = new Permissions();
        permissions.setQuota(100500);
        User user = new User(1, UserType.USER, new UserName("first", "last"), permissions);
        User user1 = (User) binder.deserialize(binder.serialize(user));
        assert user != user1;
        assert user.equals(user1);
    }

    @Test
    public void test1Serialize1() throws Exception {
        XmlBinder<User> binder = new XmlBinder<User>(User.class);
        Permissions permissions = new Permissions();
        permissions.setQuota(10);
        User user = new User(1, UserType.USER, new UserName("first", "last"), permissions);
        User user1 = (User) binder.deserialize(binder.serialize(user));
        assert user.equals(user1);
    }

    @Test(expected = RuntimeException.class)
    public void testExceptionsDeserialize() throws Exception {
        XmlBinder<ClassBadSerialisation> binder = new XmlBinder<ClassBadSerialisation>(ClassBadSerialisation.class);
        binder.serialize(new ClassBadSerialisation());
    }
}
