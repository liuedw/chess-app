package demo;

import javax.swing.*;

public class App {


    public static void main(String[] args) {
        JFrame frame = new JFrame("Chess");
        BoardGUI board = new BoardGUI(frame);

        frame.add(board.getGUI());
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        frame.pack();
        frame.setMinimumSize(frame.getSize());
        frame.setVisible(true);
    }
}