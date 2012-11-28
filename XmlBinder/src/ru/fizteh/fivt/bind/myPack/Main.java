package ru.fizteh.fivt.bind.myPack;

import ru.fizteh.fivt.bind.defPack.BindingType;
import ru.fizteh.fivt.bind.defPack.MembersToBind;

/**
 * Package: ru.fizteh.fivt.bind.myPack
 * User: acer
 * Date: 27.11.12
 * Time: 17:42
 */
@BindingType(value = MembersToBind.FIELDS)
public class Main {
    Main() {
        System.out.println("Created new object of class Main");
    }

    public static void main(String[] args) {

    }
}
