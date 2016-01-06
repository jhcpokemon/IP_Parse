package activity;

import layout.MyFrame;

import javax.swing.*;
import java.io.File;
import java.io.IOException;

public class MainActivity {
    private static MyFrame frame;

    public static void main(String[] args) throws IOException {
        try {
            UIManager.setLookAndFeel(
                    UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException | InstantiationException | UnsupportedLookAndFeelException | IllegalAccessException e) {
            e.printStackTrace();
        }
        frame = new MyFrame();
    }
}
