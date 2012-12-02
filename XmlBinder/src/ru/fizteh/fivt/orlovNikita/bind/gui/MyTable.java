package ru.fizteh.fivt.orlovNikita.bind.gui;

import ru.fizteh.fivt.bind.test.User;

import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;
import java.util.ArrayList;

/**
 * Package: ru.fizteh.fivt.orlovNikita.bind.gui
 * User: acer
 * Date: 02.12.12
 * Time: 22:17
 */
public class MyTable implements TableModel {

    private ArrayList<User> userList;

    MyTable(ArrayList<User> list) {
        userList = list;
    }



    @Override
    public int getRowCount() {
        return 0;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public int getColumnCount() {
        return 0;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public String getColumnName(int columnIndex) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return true;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void addTableModelListener(TableModelListener l) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void removeTableModelListener(TableModelListener l) {
        //To change body of implemented methods use File | Settings | File Templates.
    }
}
