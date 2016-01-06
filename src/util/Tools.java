package util;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.util.ArrayList;

public class Tools {
    private static StringBuilder sb = new StringBuilder();
    private static Writer writer;

    public static void setWriter(Writer writer) {
        Tools.writer = writer;
    }

    public static void appendText(JTextField field, String word) {
        field.setText(field.getText() + word);
        writeToFile(field);
    }

    public static ArrayList<String> parse(String data) {
        ArrayList<String> list = new ArrayList<>();
        list.add(data.substring(0, 4));
        list.add(data.substring(4, 5));
        list.add(data.substring(5, 6));
        list.add(data.substring(6, 8));
        list.add(data.substring(8, 12));
        list.add(data.substring(12, 16));
        list.add(data.substring(16, 20));
        list.add(data.substring(20, 22));
        list.add(data.substring(22, 24));
        list.add(data.substring(24, 28));
        list.add(data.substring(28, 36));
        list.add(data.substring(36, 44));
        //// TODO: 01/04/16 判断首部长度
        if (Integer.parseInt(data.substring(5, 6)) > 5) {
            list.add(data.substring(44, data.length()));
        }
        return list;
    }

    public static String getData(File file) {
        BufferedReader reader = null;
        String line;
        String data = null;
        try {
            reader = new BufferedReader(new FileReader(file));
            while ((line = reader.readLine()) != null) {
                sb.append(line.substring(7, line.length()));
            }
            data = sb.toString().replaceAll(" ", "");
            data = data.substring(24, data.length());
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) try {
                reader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return data;
    }

    public static String hexToBinary(String hex) {
        return Integer.toBinaryString(Integer.parseInt(hex, 16));
    }

    public static String hexToDec(String hex) {
        return String.valueOf(Long.parseLong(hex, 16));
    }

    public static String binaryToDec(String binary) {
        return String.valueOf(Integer.parseInt(binary, 2));
    }

    public static String getIP(String hex) {
        return hexToDec(hex.substring(0, 2)) + "." +
                hexToDec(hex.substring(2, 4)) + "." +
                hexToDec(hex.substring(4, 6)) + "." +
                hexToDec(hex.substring(6, 8));
    }

    private static void writeToFile(JTextField field) {
        System.out.println(field.getText());
        try {
            writer.write(field.getText() + "\n");
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String decToHex(String dec) {
        long num = Long.parseLong(dec,10);
        return Long.toHexString(num);
    }
}
