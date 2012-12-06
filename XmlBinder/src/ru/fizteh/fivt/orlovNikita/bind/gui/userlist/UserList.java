package ru.fizteh.fivt.orlovNikita.bind.gui.userlist;

import ru.fizteh.fivt.bind.test.User;
import ru.fizteh.fivt.orlovNikita.bind.XmlBinder;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Scanner;

public class UserList extends JFrame {
    public XmlBinder<User> binder;
    public File workFile = null;
    private ArrayList<User> allUsers;
    private JTable table;
    private MyTable helpTable;

    public UserList() {
        super("List user");
        binder = new XmlBinder<User>(User.class);
        this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        this.setPreferredSize(new Dimension(350, 350));
        this.setLocationRelativeTo(null);
        this.setResizable(true);
        this.setLayout(new BorderLayout());
        this.setJMenuBar(createJMenuBar());
        this.pack();
    }

    private void createTable(File file) {
        try {

            helpTable = new MyTable(allUsers);
            table = new JTable(helpTable);
            table.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
            table.setColumnSelectionAllowed(false);
            table.setAutoCreateRowSorter(true);
            JButton button = new JButton("Delete row");
            button.setSize(10, 10);
            button.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    int viewRow = table.getSelectedRow();
                    if (viewRow < 0) {
                        JOptionPane.showMessageDialog(new JFrame(), "Choose row to delete", "Error", JOptionPane.ERROR_MESSAGE);
                    } else {
                        helpTable.removeRow(viewRow);
                    }
                }
            });

            JButton buttonInsert = new JButton("Add row");
            button.setSize(10, 10);
            buttonInsert.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    int viewRow = table.getSelectedRow();
                    if (viewRow < 0) {
                        helpTable.addRow(0);
                    } else {
                        helpTable.addRow(viewRow);
                    }
                }
            });

            this.getContentPane().add(table, BorderLayout.PAGE_START);
            this.getContentPane().add(new JScrollPane(table));

            this.getContentPane().add(button, BorderLayout.LINE_END);
            this.getContentPane().add(buttonInsert, BorderLayout.LINE_START);
            this.validate();
            helpTable.fireTableDataChanged();
            this.repaint();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(new JFrame(), "Error", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private JMenuBar createJMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        JMenu fileMenu = new JMenu("File");
        fileMenu.add(new AbstractAction("Open") {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    JFileChooser fileChooser = new JFileChooser();
                    fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
                    int result = fileChooser.showOpenDialog(UserList.this);
                    if (result == JFileChooser.APPROVE_OPTION) {
                        workFile = fileChooser.getSelectedFile();
                    }
                    if (result == JFileChooser.ERROR) {
                        JOptionPane.showMessageDialog(new JFrame(), "Can't open or deserialize file", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                    if (!workFile.exists() || workFile.isDirectory()) {
                        JOptionPane.showMessageDialog(new JFrame(), "Can't open or deserialize file", "Error", JOptionPane.ERROR_MESSAGE);
                    } else {
                        allUsers = getUserList(workFile);
                        createTable(workFile);
                    }
                } catch (Exception ex) {

                }
                UserList.this.repaint();
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
        }

        );
        fileMenu.add(new AbstractAction("Save as ...") {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileChooser = new JFileChooser();
                if (fileChooser.showSaveDialog(UserList.this) == JFileChooser.APPROVE_OPTION) {
                    writeNewTableToFile(fileChooser.getSelectedFile());
                }
            }
        }

        );
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
                writer.print("<users>");
                for (User user : getAllUsersFromTable()) {
                    writer.print(new String(binder.serialize(user)));
                }
                writer.print("</users>");
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

    public ArrayList<User> getAllUsersFromTable() {
        return helpTable.getData();
    }

    private ArrayList<User> getUserList(File file) {
        Scanner scanner = null;
        ArrayList users = new ArrayList();
        try {
            scanner = new Scanner(file);
            StringBuilder builder = new StringBuilder();
            while (scanner.hasNextLine()) {
                builder.append(scanner.nextLine());
            }
            String res = builder.toString().replace("<users>", "").replace("</users>", "");
            String[] spl = res.split("(?=<user>)");
            for (int i = 1; i < spl.length; i++) {
                String temp = spl[i];
                System.out.println(temp);
                System.out.println(temp.length());
                users.add(binder.deserialize(temp.getBytes()));
            }
        } catch (Exception e) {
            scanner.close();
            return null;
        }
        return users;
    }
}
