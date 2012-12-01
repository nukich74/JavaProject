package ru.fizteh.fivt.bind.myPack;

import ru.fizteh.fivt.bind.test.Permissions;
import ru.fizteh.fivt.bind.test.User;
import ru.fizteh.fivt.bind.test.UserName;
import ru.fizteh.fivt.bind.test.UserType;

/**
 * Package: ru.fizteh.fivt.bind.myPack
 * User: acer
 * Date: 27.11.12
 * Time: 17:42
 */
public class Main {
    public static void main(String[] args) throws Exception {
        XmlBinder<User> binder = new XmlBinder<User>(User.class);
        Permissions permissions = new Permissions();
        permissions.setQuota(100500);
        User user = new User(1, UserType.USER, new UserName("first", "last"), permissions);
        byte[] bytes = binder.serialize(user);
        System.out.println(bytes.toString());
    }
}
