package io.agora.mainClass;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MainFrame extends JFrame {

    private DetailsPanel detailsPanel;
    private final JTextArea textArea = new JTextArea();
    private JTextField field = new JTextField();
    private String message = "";
    public MainFrame(String title){
        super(title);

        //Set layout manager
        setLayout(new BorderLayout());

        //Create Swing Component
        JButton button  = new JButton("Send Message");
        //Add Swing components to content pane
        Container c = getContentPane();

        c.add(textArea, BorderLayout.CENTER);
        c.add(field, BorderLayout.SOUTH);
        c.add(button, BorderLayout.LINE_END);
//        c.add(detailsPanel , BorderLayout.WEST);

        //Add behaviour
        button.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                message = field.getText();
                System.out.println("MESSAGE: " + message);
                textArea.append("\n" + message);
//                field.setText("");
            }
        });

    }

    public void addMessage(String input){
        textArea.append(input);
    }
    public String getMessage(){
        return message;
    }
    public void clearMessage(){
        message = "";
    }
    public void clear(){
        textArea.setText("");
    }

    public int askUserInt (String input){
        String str1 = JOptionPane.showInputDialog(input);

        return Integer.parseInt(str1);

    }

    public String askUserString (String input){
        String str1 = JOptionPane.showInputDialog(input);

        return str1;

    }
}
