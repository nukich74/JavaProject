package ru.fizteh.fivt.orlovNikita.bind.gui;

import ru.fizteh.fivt.bind.test.Permissions;
import ru.fizteh.fivt.bind.test.User;
import ru.fizteh.fivt.bind.test.UserName;
import ru.fizteh.fivt.bind.test.UserType;

import javax.swing.*;
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
        return String.class;
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return true;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        switch (columnIndex) {
            case 0:
                return String.valueOf(String.valueOf(userList.get(rowIndex).getId()));
            case 1:
                return userList.get(rowIndex).getName().getFirstName();
            case 2:
                return userList.get(rowIndex).getName().getLastName();
            case 3:
                return userList.get(rowIndex).getUserType().toString().toString();
            case 4:
                return String.valueOf(userList.get(rowIndex).getPermissions().isRoot());
            case 5:
                return String.valueOf(userList.get(rowIndex).getPermissions().getQuota());
        }
        return null;
    }

    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        User user = userList.get(rowIndex);
        User newUser = null;
        try {
            switch (columnIndex) {
                case 0:
                    newUser = new User(Integer.valueOf((String) aValue), user.getUserType(), user.getName(), user.getPermissions());
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
                    newUser = new User(user.getId(), UserType.valueOf((String) aValue), user.getName(), user.getPermissions());
                    break;
                case 4:
                    Permissions permissions = user.getPermissions();
                    if (((String) aValue).equals("false")) {
                        permissions.setRoot(false);
                    } else if (aValue.equals("true")) {
                        permissions.setRoot(true);
                    } else {
                        throw new RuntimeException("Bad type!!!");
                    }
                    newUser = new User(user.getId(), user.getUserType(), user.getName(), permissions);
                    break;
                case 5:
                    permissions = user.getPermissions();
                    permissions.setQuota(Integer.valueOf((String) aValue));
                    newUser = new User(user.getId(), user.getUserType(), user.getName(), permissions);
                    break;
            }
            userList.set(rowIndex, newUser);
            this.fireTableDataChanged();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(new JFrame(), "Bad value for position!", "Error", JOptionPane.ERROR_MESSAGE);
        }

    }

    public ArrayList<User> getData() {
        return userList;
    }

    public void removeRow(int row) {
        userList.remove(row);
        fireTableRowsDeleted(row, row);
    }
}
