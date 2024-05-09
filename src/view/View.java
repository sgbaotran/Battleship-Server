package view;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import control.Control;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;

/**
 * The View class represents the graphical user interface for the Battleship
 * game.
 * It provides components for user input and displays game information.
 */
public class View extends JFrame {
    private JButton startButton;
    private JButton resultButton;
    private JButton endButton;
    private JTextField portTextField;
    private JTextArea messageBox;
    private JTextField dimensionTextField;
    private JPanel panel;

    /**
     * Constructs the View object and initializes the user interface components.
     */
    public View() {
        setTitle("Battleship");
        setLayout(new FlowLayout(FlowLayout.LEADING, 0, 0));
        setPanel();
        setSize(new Dimension(540, 400));
        setResizable(false);
        setVisible(true);
    }

    /**
     * Sets up the main panel of the user interface.
     */
    public void setPanel() {
        panel = new JPanel(new FlowLayout());
        panel.setPreferredSize(new Dimension(540, 400));
        panel.setBackground(new Color(158, 159, 165));
        setImage();
        setInputComponents();
        add(panel);
    }

    private ImageIcon imageIcon;

    /**
     * Sets the image icon to display on the user interface.
     */
    public void setImage() {
        try {
            imageIcon = new ImageIcon(getClass().getResource("../images/server.png"));
            panel.add(new JLabel(imageIcon));
        } catch (Exception ex) {
            System.out.println("Image not found");
        }
    }

    /**
     * Initializes and adds input components to the user interface.
     */
    public void setInputComponents() {
        portTextField = new JTextField(5);
        dimensionTextField = new JTextField(2);
        dimensionTextField.setText("1");
        messageBox = new JTextArea(8, 42);

        JScrollPane messageScrollPane = new JScrollPane(messageBox);

        startButton = new JButton("Start");
        resultButton = new JButton("Results");
        endButton = new JButton("End");

        panel.add(new JLabel("Port:"));
        panel.add(portTextField);
        panel.add(new JLabel("Dimension:"));
        panel.add(dimensionTextField);
        panel.add(startButton);
        panel.add(resultButton);
        panel.add(endButton);
        panel.add(messageScrollPane);
    }

    /**
     * Gets the "Start" button from the user interface.
     */
    public JButton getstartButton() {
        return startButton;
    }

    /**
     * Gets the "Results" button from the user interface.
     */
    public JButton getResetButton() {
        return resultButton;
    }

    /**
     * Gets the "End" button from the user interface.
     */
    public JButton getendButton() {
        return endButton;
    }

    /**
     * Gets the value entered in the port text field.
     */
    public String getPortNumber() {
        return portTextField.getText();
    }

    /**
     * Gets the value entered in the dimension text field.
     * Returns 0 if the value cannot be parsed as an integer.
     */
    public int getDimension() {
        try {
            return Integer.parseInt(dimensionTextField.getText());
        } catch (Exception ex) {
            return 0;
        }
    }

    /**
     * Adds an ActionListener to the buttons for handling user actions.
     */
    public void addController(Control control) {
        startButton.addActionListener(control);
        resultButton.addActionListener(control);
        endButton.addActionListener(control);
    }

    /**
     * Displays an error message dialog box.
     */
    public void showError() {
        JOptionPane.showMessageDialog(null, "Cannot open socket, check your port!!!", "Error",
                JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * Appends a message to the message box.
     */
    public void appendMessageBox(String text) {
        messageBox.setText(text);
    }

    /**
     * Disposes of the current frame.
     */
    public void disposeMenu() {
        this.dispose();
    }

    /**
     * Displays a game over dialog box with the winner's name.
     */
    public void showGameOver(int winner) {
        String winnerName = winner == 1 ? "FIRST PLAYER" : "SECOND PLAYER";
        JOptionPane.showMessageDialog(null, "WINNER IS " + winnerName, "Game Over",
                JOptionPane.INFORMATION_MESSAGE);
    }
}
