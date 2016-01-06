package view;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;

public class MyTextField extends JTextField{
    public static Font font = new Font("Microsoft Yahei",Font.PLAIN,32);
    public MyTextField() {
        init();
    }

    public MyTextField(String def){
        super(def);
        init();
    }

    public void init(){
        setEditable(false);
        setFont(font);
    }
}
