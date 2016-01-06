package layout;

import activity.NetWorkCapture;
import util.Tools;
import view.MyTextField;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MyFrame extends Frame {
    public final JButton open, parse, lib;
    public final MyTextField url, version, headLength, serviceType, length, identity, flag, offset, lifetime, protocol, verify, source, destination, optional, fill;
    private final JFileChooser fileChooser;
    private final JPanel headPanel, mainPanel;
    private File in;
    private ArrayList<String> result;
    private String data;
    private Writer out;
    private Map<String, String> map = new HashMap<>();

    public MyFrame() {
        headPanel = new JPanel();
        mainPanel = new JPanel();
        fileChooser = new JFileChooser();
        open = new JButton("打开");
        open.setFont(MyTextField.font);
        parse = new JButton("解析");
        parse.setFont(MyTextField.font);
        lib = new JButton("来自Jnetpcap");
        lib.setFont(MyTextField.font);
        url = new MyTextField();
        version = new MyTextField("版本:");
        headLength = new MyTextField("首部长度:");
        serviceType = new MyTextField("服务类型:");
        length = new MyTextField("总长度:");
        identity = new MyTextField("标识:");
        flag = new MyTextField("标志:");
        offset = new MyTextField("片偏移:");
        lifetime = new MyTextField("生存时间:");
        protocol = new MyTextField("协议类型:");
        verify = new MyTextField("首部校验和:");
        source = new MyTextField("源地址:");
        destination = new MyTextField("目的地址:");
        optional = new MyTextField("可选:");
        fill = new MyTextField("填充:");
        result = new ArrayList<>();
        init();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void init() {
        GridBagConstraints c = new GridBagConstraints();
        setTitle("IP 数据包解析");
        setSize(1200, 600);
        headPanel.setLayout(new GridBagLayout());
        c.fill = GridBagConstraints.BOTH;
        c.gridx = 0;
        c.gridy = 0;
        c.weightx = 10;
        headPanel.add(url, c);
        c.gridx = 1;
        c.weightx = 1;
        headPanel.add(open, c);
        c.gridx = 2;
        headPanel.add(parse, c);
        c.gridx = 3;
        headPanel.add(lib, c);
        setLayout(new GridBagLayout());
        c.gridx = 0;
        c.gridy = 0;
        add(headPanel, c);

        mainPanel.setLayout(new GridBagLayout());

        JPanel row0 = new JPanel(new GridBagLayout());
        c.gridx = 0;
        c.gridy = 0;
        c.weightx = 1;
        row0.add(version, c);
        c.gridx = 1;
        row0.add(headLength, c);
        c.gridx = 2;
        c.weightx = 2;
        row0.add(serviceType, c);
        c.gridx = 3;
        c.weightx = 4;
        row0.add(length, c);
        c.gridx = 0;
        mainPanel.add(row0, c);

        JPanel row1 = new JPanel(new GridBagLayout());
        c.gridx = 0;
        c.weightx = 4;
        row1.add(identity, c);
        c.gridx = 1;
        c.weightx = 1.5;
        row1.add(flag, c);
        c.gridx = 2;
        c.weightx = 3;
        row1.add(offset, c);
        c.gridx = 0;
        c.gridy = 1;
        mainPanel.add(row1, c);

        JPanel row2 = new JPanel(new GridBagLayout());
        c.gridy = 0;
        c.weightx = 2;
        row2.add(lifetime, c);
        c.gridx = 1;
        c.weightx = 2;
        row2.add(protocol, c);
        c.gridx = 2;
        c.weightx = 4;
        row2.add(verify, c);
        c.gridx = 0;
        c.gridy = 2;
        mainPanel.add(row2, c);

        JPanel row3 = new JPanel(new GridBagLayout());
        c.gridy = 3;
        row3.add(source, c);
        mainPanel.add(row3, c);

        JPanel row4 = new JPanel(new GridBagLayout());
        c.gridy = 4;
        row4.add(destination, c);
        mainPanel.add(row4, c);

        JPanel row5 = new JPanel(new GridBagLayout());
        c.gridy = 0;
        c.weightx = 3;
        row5.add(optional, c);
        c.gridx = 1;
        c.weightx = 1;
        row5.add(fill, c);
        c.gridx = 0;
        c.gridy = 5;
        mainPanel.add(row5, c);

        c.gridy = 1;

        add(mainPanel, c);

        open.addActionListener((event) -> {
            origin();
            int returnVal = fileChooser.showOpenDialog(this);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                open.setEnabled(false);
                in = fileChooser.getSelectedFile();
                url.setText(in.getAbsolutePath());
                data = Tools.getData(in);
            }
            try {
                out = new BufferedWriter(new FileWriter("./logfile", true));
                Tools.setWriter(out);
            } catch (IOException e) {
                e.printStackTrace();
            }
            parse.setEnabled(true);
        });

        parse.addActionListener(event -> {
            parse.setEnabled(false);
            if (data != null) {
                result = Tools.parse(data);
                display();
            } else {
                String err = "请先选择要解析的文件!!";
                try {
                    out = new BufferedWriter(new FileWriter("./logfile", true));
                    out.write(err + "\n");
                    out.close();
                } catch (IOException e) {
                    System.out.println("Stream closed");
                }
            }
            open.setEnabled(true);
        });

        lib.addActionListener(event -> {
            NetWorkCapture.main(null);
            origin();
            url.setText("");
            parse.setEnabled(false);
            map = NetWorkCapture.getMap();
            try {
                out = new BufferedWriter(new FileWriter("./logfile", true));
                Tools.setWriter(out);
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (map.get("err") == null) {
                displayLib();
            } else {
                Tools.appendText(this.url,map.get("err"));
            }
        });


        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });
    }

    public void display() {
        /**
         * 判断是否为IP数据报
         */
        String ip = result.get(0);
        if (!ip.equals("0800") && !(Integer.parseInt(ip) == 8)) {
            String err = "不是IP数据报";
            try {
                out.write(err + "\n");
                out.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
            System.out.println(err);
            return;
        }

        /**
         * 获取版本
         */
        String version = result.get(1);
        if (Integer.parseInt(version) == 4) {
            version = "IPv4";
        } else {
            version = "版本错误";
        }
        Tools.appendText(this.version, version);
        /**
         *获取首部长度
         */
        String headLength = result.get(2);
        headLength = String.valueOf(Integer.parseInt(headLength) * 4) + "字节";
        Tools.appendText(this.headLength, headLength);

        /**
         *获取服务类型
         */
        String serviceType = result.get(3);
        if (!(Integer.parseInt(serviceType) == 0)) {
            serviceType = Tools.hexToBinary(serviceType);
            String pre = serviceType.substring(0, serviceType.length() - 5);
            String type = serviceType.substring(serviceType.length() - 5, serviceType.length());
            serviceType = "优先级:" + pre + " 类型:" + type;
        }
        Tools.appendText(this.serviceType, serviceType);

        /**
         * 获取总长度
         */
        String length = result.get(4);
        length = Tools.hexToDec(length) + "字节";
        Tools.appendText(this.length, length);

        /**
         *获取标识
         */
        String identify = result.get(5);
        Tools.appendText(this.identity, identify);

        /**
         * 获取标志和片偏移
         */
        String mix = result.get(6);
        mix = Tools.hexToBinary(mix);
        String flag = mix.substring(0, 3);
        if (flag.substring(1, 2).equals("1")) {
            flag = "不能分片";
        } else {
            if (flag.substring(0, 1).equals("1")) {
                flag = "还有分片";
            } else {
                flag = "最后一个分片";
            }
        }
        String offset = Tools.binaryToDec(mix.substring(3, mix.length()));
        offset = String.valueOf(Integer.parseInt(offset) * 8) + "字节";
        Tools.appendText(this.flag, flag);
        Tools.appendText(this.offset, offset);

        /**
         *获取生存时间
         */
        String lifetime = result.get(7);
        lifetime = Tools.hexToDec(lifetime);
        Tools.appendText(this.lifetime, lifetime);

        /**
         *获取协议类型
         */
        String protocol = result.get(8);
        switch (Tools.hexToDec(protocol)) {
            case "1":
                protocol = "ICMP";
                break;
            case "6":
                protocol = "UDP";
                break;
            case "17":
                protocol = "TCP";
                break;
            default:
                protocol = "未知协议";
        }
        Tools.appendText(this.protocol, protocol);

        /**
         * 获取首部校验和
         */
        String verify = result.get(9);
        Tools.appendText(this.verify, verify);

        /**
         * 获取源地址
         */
        String source = result.get(10);
        source = Tools.getIP(source);
        Tools.appendText(this.source, source);

        /**
         * 获取目的地址
         */
        String destination = result.get(11);
        destination = Tools.getIP(destination);
        Tools.appendText(this.destination, destination);

        /**
         * 可选字段
         */
        String optional, fill;
        if (result.size() > 12) {
            mix = result.get(12);
            mix = new StringBuilder(mix).reverse().toString();
            Matcher matcher = Pattern.compile("00*").matcher(mix);
            int index = matcher.end();
            optional = new StringBuilder(mix.substring(index, mix.length())).reverse().toString();
            fill = "有" + index + "位填充";
        } else {
            optional = "无可选字段";
            fill = "无填充字段";
        }
        Tools.appendText(this.optional, optional);
        Tools.appendText(this.fill, fill);

        /**
         * 关闭Writer
         */
        try {
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void displayLib() {
        origin();
        Tools.appendText(version, map.get("version"));
        Tools.appendText(headLength, map.get("head length"));
        Tools.appendText(serviceType, map.get("TOS"));
        Tools.appendText(length, map.get("length"));
        Tools.appendText(identity, map.get("identification"));
        Tools.appendText(flag, map.get("flags"));
        Tools.appendText(offset, map.get("offset"));
        Tools.appendText(lifetime, map.get("ttl"));
        Tools.appendText(protocol, map.get("type"));
        Tools.appendText(verify, map.get("check"));
        Tools.appendText(source, map.get("source"));
        Tools.appendText(destination, map.get("des"));
        Tools.appendText(optional, "无");
        Tools.appendText(fill, "无");
    }

    public void origin() {
        version.setText("版本:");
        headLength.setText("首部长度:");
        serviceType.setText("服务类型:");
        length.setText("总长度:");
        identity.setText("标识:");
        flag.setText("标志:");
        offset.setText("片偏移:");
        lifetime.setText("生存时间:");
        protocol.setText("协议类型:");
        verify.setText("首部校验和:");
        source.setText("源地址:");
        destination.setText("目的地址:");
        optional.setText("可选:");
        fill.setText("填充:");
    }
}