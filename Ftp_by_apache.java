package com.ShuaiLiu;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;

import javax.swing.*;
import java.io.*;


public class Ftp_by_apache {


    FTPClient f = null;

    //constructor
    public Ftp_by_apache(String url, String username, String password) {
        f = new FTPClient();
        //get connection
        this.get_connection(url, username, password);


    }


    //method of connect to server
    public void get_connection(String url, String username, String password) {


        try {
            //connect to server，default port is 21
            f.connect(url);
            System.out.println("connect success!");

            //set up encoding
            f.setControlEncoding("GBK");

            //login
            boolean login = f.login(username, password);
            if (login)
                System.out.println("Login success!");
            else
                System.out.println("Login false！");

        } catch (IOException e) {

            e.printStackTrace();
        }


    }

    public void close_connection() {

        boolean logout = false;
        try {
            logout = f.logout();
        } catch (IOException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        if (logout) {
            System.out.println("Logout success!");
        } else {
            System.out.println("Logout false!");
        }

        if (f.isConnected())
            try {
                System.out.println("close the connect！");
                f.disconnect();
            } catch (IOException e) {

                e.printStackTrace();
            }

    }


    //get folder and file
    public FTPFile[] getAllFile() {


        FTPFile[] files = null;
        try {
            files = f.listFiles();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        for (FTPFile file : files) {

//            if(file.isDirectory())
//                System.out.println(file.getName()+"是文件夹");
//            if(file.isFile())
//                System.out.println(file.getName()+"是文件");
        }
        return files;

    }


    //generate InputStream for upload local file
    public void upload(String File_path) throws IOException {

        InputStream input = null;
        String[] File_name = null;
        try {
            input = new FileInputStream(File_path);
            File_name = File_path.split("\\\\");
            f.setFileType(FTP.BINARY_FILE_TYPE);    //default is ASCII. It lead to file broken. Change it to binary file.
            System.out.println(File_name[File_name.length - 1]);
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        //uploading file
        System.out.println(File_name[File_name.length - 1]);
        f.storeFile(File_name[File_name.length - 1], input);
        System.out.println("Uploading success!");
        JOptionPane.showMessageDialog(null, "Uploading success!\nPlease click Refresh");

        if (input != null)
            input.close();


    }


    //Download from_file_name is file name which download. to_path is downloading path
    public void download(String from_file_name, String to_path) throws IOException {


        OutputStream output = null;
        try {
            output = new FileOutputStream(to_path + from_file_name);
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        f.retrieveFile(from_file_name, output);
        if (output != null) {
            try {
                if (output != null)
                    output.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }


    }


}