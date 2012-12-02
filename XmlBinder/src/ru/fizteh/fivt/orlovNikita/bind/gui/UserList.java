package ru.fizteh.fivt.orlovNikita.bind.gui;

import ru.fizteh.fivt.bind.test.User;
import ru.fizteh.fivt.orlovNikita.bind.XmlBinder;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.PrintWriter;
import java.util.ArrayList;

/**
 * Package: ru.fizteh.fivt.orlovNikita.bind
 * User: acer
 * Date: 02.12.12
 * Time: 18:40
 */
public class UserList extends JFrame {
    public XmlBinder<User> binder;
    public File workFile = null;
    private ArrayList<User> allUsers;

    public UserList() {
        super("List user");
        binder = new XmlBinder<User>(User.class);
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
                    writeNewTableToFile(workFile);
                }
            }
        });
        fileMenu.add(new AbstractAction("Save as ...") {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileChooser = new JFileChooser();
                if (fileChooser.showSaveDialog(UserList.this) == JFileChooser.APPROVE_OPTION) {
                    writeNewTableToFile(fileChooser.getSelectedFile());
                }
            }
        });
        menuBar.add(fileMenu);
        return menuBar;
    }

    private void writeNewTableToFile(File file) {
        if (file == null) {
            return;
        } else {
            PrintWriter writer = null;
            try {
                writer = new PrintWriter(file);
                writer.print("<users>".getBytes());
                for (User user : getAllUsers()) {
                    writer.print(binder.serialize(user));
                }
                writer.print("</users>".getBytes());
            } catch (Exception e) {
                JOptionPane.showMessageDialog(new JFrame(), "Error while trying to save file", "Error", JOptionPane.ERROR_MESSAGE);
            } finally {
                if (writer != null) {
                    writer.close();
                }
            }
        }
    }


    public static void main(String[] args) {
        UserList list = new UserList();
        list.setVisible(true);
    }

    public ArrayList<User> getAllUsers() {
        //TODO get all users from talble;
        return allUsers;
    }
}
