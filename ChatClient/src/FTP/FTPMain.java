/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package FTP;

/**
 *
 * @author mike
 *
 */
import org.apache.commons.net.ftp.FTPFile;

import javax.swing.*;
import chatclient.*;
import chatclient.clientGUI;
import javax.swing.border.LineBorder;
import javax.swing.filechooser.FileSystemView;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;

public class FTPMain implements ActionListener {

    //init--------------------------------
    public FTPFile[] file;
    public static String FTP = "172.20.10.2";
    public String username = "Userx";
    public String password = "123456";
    //init--------------------------------

    private JFrame frame;
    private JTable table;
    private JScrollPane scrollPane;
    public static Ftp_by_apache ftp;
    public clientGUI gui;

    public static Ftp_by_apache getFtp() {
        return ftp;
    }

    public FTPMain(clientGUI gui) {
        this.gui = gui;
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    ftp = new Ftp_by_apache(FTP, username, password);
                    file = ftp.getAllFile();
                    initialize();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * Initialize the contents of the frame.
     */
    private void initialize() {
        frame = new JFrame();
        //frame.setIconImage(Toolkit.getDefaultToolkit().getImage(Main.class.getResource("/com/sun/java/swing/plaf/windows/icons/UpFolder.gif")));
        frame.setTitle("FTP");
        frame.setBounds(100, 100, 470, 534);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.getContentPane().setLayout(null);
        frame.setVisible(true);
        //upload button--------------------------------------------------
        JButton upload = new JButton("Upload");
        upload.setFont(new Font("??", Font.PLAIN, 12));
        upload.setBackground(UIManager.getColor("Button.highlight"));
        upload.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                //upload button trigger------------------------------------
                System.out.println("Uploading?????");
                int result = 0;
                File file = null;
                String path = null;
                JFileChooser fileChooser = new JFileChooser();
                FileSystemView fsv = FileSystemView.getFileSystemView();
                System.out.println(fsv.getHomeDirectory());                //get home directory
                fileChooser.setCurrentDirectory(fsv.getHomeDirectory());
                fileChooser.setDialogTitle("Please select file...");
                fileChooser.setApproveButtonText("Enter");
                fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
                result = fileChooser.showOpenDialog(null);
                if (JFileChooser.APPROVE_OPTION == result) {
                    path = fileChooser.getSelectedFile().getPath();
                    System.out.println("path: " + path);
                    try {
                        ftp.upload(path);
                    } catch (Exception e1) {
                        // TODO Auto-generated catch block
                        e1.printStackTrace();
                    } finally {

                        //ftp.close_connection();
                    }
                }
            }
        });
        upload.setBounds(195, 15, 82, 23);
        frame.getContentPane().add(upload);

        //refresh button
        JButton refresh = new JButton("Refresh");
        refresh.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                System.out.println("Refreshing?????");
                frame.getContentPane().remove(scrollPane);
                frame.validate();
                frame.repaint();
                file = ftp.getAllFile();
                showTable();
                System.out.println("Refresh success!");
            }
        });
        refresh.setFont(new Font("??", Font.PLAIN, 12));
        refresh.setBackground(UIManager.getColor("Button.highlight"));
        refresh.setBounds(312, 15, 82, 23);
        frame.getContentPane().add(refresh);
        //refresh button--------------------------------------------------

        //show information(FTP username)-----------------------------------------------
        JLabel lblNewLabel = new JLabel("FTP ip:");
        lblNewLabel.setBounds(32, 10, 54, 15);
        frame.getContentPane().add(lblNewLabel);

        JLabel lblNewLabel_1 = new JLabel("Name:");
        lblNewLabel_1.setBounds(32, 35, 54, 15);
        frame.getContentPane().add(lblNewLabel_1);

        JLabel address = new JLabel(FTP);
        address.setBounds(110, 10, 75, 15);
        frame.getContentPane().add(address);

        JLabel name = new JLabel(username);
        name.setBounds(110, 35, 82, 15);
        frame.getContentPane().add(name);
        //show information-----------------------------------------------

        showTable();
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.addWindowListener(new WindowAdapter(){
            public void windowClosing(WindowEvent we){
                ftp.close_connection();
                gui.fileRunning = false;
                //System.exit(0);
            }
        });
    }

    private void showTable() {
        //table data init  read files in ftp

        String[][] data1 = new String[file.length][4];
        for (int row = 0; row < file.length; row++) {

            data1[row][0] = file[row].getName();
            if (file[row].isDirectory()) {
                data1[row][1] = "Folder";
            } else if (file[row].isFile()) {
                String[] geshi = file[row].getName().split("\\.");
                data1[row][1] = geshi[1];
            }
            data1[row][2] = file[row].getSize() + "";
            data1[row][3] = "Download";
        }

        //table name-----------------------------------------------------
        String[] columnNames = {"file", "type", "size(kb)", ""};
        DefaultTableModel model = new DefaultTableModel();
        model.setDataVector(data1, columnNames);

        //scroll bar--------------------------------------------------------
        scrollPane = new JScrollPane();
        scrollPane.setBounds(32, 73, 420, 384);
        frame.getContentPane().add(scrollPane);
        //scroll bar-----------------------------------------------------

        //table function------------------------------------------------------
        table = new JTable(model);
        scrollPane.setViewportView(table);

        table.setColumnSelectionAllowed(true);
        table.setCellSelectionEnabled(true);
        table.setFont(new Font("????", Font.PLAIN, 12));
        table.setBorder(new LineBorder(new Color(0, 0, 0)));
        //table.setToolTipText("\u53EF\u4EE5\u70B9\u51FB\u4E0B\u8F7D");

        //table button init(button in last row)--------------------
        Gui buttonsColumn = new Gui(table, 3);

    }

    @Override
    public void actionPerformed(ActionEvent arg0) {
        // TODO Auto-generated method stub

    }
}
