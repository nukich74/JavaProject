package ru.fizteh.fivt.orlovNikita.bind.gui;

import ru.fizteh.fivt.bind.test.Permissions;
import ru.fizteh.fivt.bind.test.User;
import ru.fizteh.fivt.bind.test.UserName;
import ru.fizteh.fivt.bind.test.UserType;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;

public class MyTable extends AbstractTableModel {

    ArrayList<User> userList;

    String columnNames[] = {"ID", "firstName", "lastName", "userType", "root", "quota"};

    MyTable(ArrayList<User> list) {
        userList = list;
    }

    @Override
    public int getRowCount() {
        return userList.size();
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    @Override
    public String getColumnName(int columnIndex) {
        return columnNames[columnIndex];
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        switch (columnIndex) {
            case 0:
                return int.class;
            case 1:
                return String.class;
            case 2:
                return String.class;
            case 3:
                return UserType.class;
            case 4:
                return int.class;
            case 5:
                return boolean.class;
        }
        return null;
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return true;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        switch (columnIndex) {
            case 0:
                return userList.get(rowIndex).getId();
            case 1:
                return userList.get(rowIndex).getName().getFirstName();
            case 2:
                return userList.get(rowIndex).getName().getLastName();
            case 3:
                return userList.get(rowIndex).getUserType();
            case 4:
                return userList.get(rowIndex).getPermissions().isRoot();
            case 5:
                return userList.get(rowIndex).getPermissions().getQuota();
        }
        return null;
    }

    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        User user = userList.get(rowIndex);
        User newUser;
        switch (columnIndex) {
            case 0:
                newUser = new User((Integer) aValue, user.getUserType(), user.getName(), user.getPermissions());
                break;
            case 1:
                newUser = new User(user.getId(), user.getUserType(),
                        new UserName((String) aValue, user.getName().getLastName()), user.getPermissions());
                break;
            case 2:
                newUser = new User(user.getId(),
                        user.getUserType(), new UserName(user.getName().getFirstName(), (String) aValue), user.getPermissions());
                break;
            case 3:
                newUser = new User(user.getId(), (UserType) aValue, user.getName(), user.getPermissions());
                break;
            case 4:
                Permissions permissions = new Permissions();
                permissions.setRoot((Boolean) aValue);
                newUser = new User(user.getId(), user.getUserType(), user.getName(), permissions);
                break;
            case 5:
                permissions = new Permissions();
                permissions.setQuota((Integer) aValue);
                newUser = new User(user.getId(), user.getUserType(), user.getName(), permissions);
                break;
        }
        userList.set(rowIndex, user);
    }

    public ArrayList<User> getData() {
        return userList;
    }

}
