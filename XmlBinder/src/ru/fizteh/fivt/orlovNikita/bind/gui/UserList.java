package ru.fizteh.fivt.orlovNikita.bind.gui;

import ru.fizteh.fivt.bind.test.User;
import ru.fizteh.fivt.orlovNikita.bind.XmlBinder;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;

/**
 * Package: ru.fizteh.fivt.orlovNikita.bind
 * User: acer
 * Date: 02.12.12
 * Time: 18:40
 */
public class UserList extends JFrame {
    XmlBinder<User> binder = new XmlBinder<User>(User.class);
    File workFile = null;

    public UserList() {
        super("List user");
        this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        this.setPreferredSize(new Dimension(350, 350));
        this.setLocationRelativeTo(null);
        this.setResizable(true);
        this.setJMenuBar(createJMenuBar());
        this.pack();
        createTable();
    }

    private void createTable() {
    }


    private JMenuBar createJMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        JMenu fileMenu = new JMenu("File");
        fileMenu.add(new AbstractAction("Open") {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
                int result = fileChooser.showOpenDialog(UserList.this);
                if (result == JFileChooser.APPROVE_OPTION) {
                    workFile = fileChooser.getSelectedFile();
                }
            }
        });
        fileMenu.add(new AbstractAction("Save") {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (workFile == null) {
                    JOptionPane.showMessageDialog(new JFrame(), "No opened file", "Error", JOptionPane.ERROR_MESSAGE);
                } else {
                    PrintWriter writer = null;
                    try {
                        writer = new PrintWriter(workFile);
                        //TODO получить данные с таблицы и сохранить их

                    } catch (FileNotFoundException e1) {
                        if (writer != null) {
                             writer.close();
                        }
                    }
                }
            }
        });
        fileMenu.add(new AbstractAction("Save as ...") {
            @Override
            public void actionPerformed(ActionEvent e) {
                //TODO add new dialog to save as new file, to get new
            }
        });
        menuBar.add(fileMenu);
        return menuBar;
    }


    public static void main(String[] args) {
        UserList list = new UserList();
        list.setVisible(true);
    }
}
