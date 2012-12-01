package ru.fizteh.fivt.orlovNikita.bind;

import org.junit.Test;
import ru.fizteh.fivt.bind.test.Permissions;
import ru.fizteh.fivt.bind.test.User;
import ru.fizteh.fivt.bind.test.UserName;
import ru.fizteh.fivt.bind.test.UserType;

/**
 * Package: ru.fizteh.fivt.bind.myPack
 * User: acer
 * Date: 27.11.12
 * Time: 17:13
 */
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
    public void testExceptionsSerialize() throws Exception {

    }

    @Test
    public void testExceptionsDeserialize() throws Exception {

    }
}
