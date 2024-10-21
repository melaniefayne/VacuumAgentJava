package vacworld;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class VacuumWorldGUI extends JFrame {
    private final JLabel[][] labels; // 2D array to hold the labels for each grid cell
    private final JPanel panel;
    private final JLabel perceptLabel; // Label to show the percept
    private final JLabel actionLabel;  // Label to show the action
    private final JButton nextButton;  // Button to go to the next step
    private ActionListener nextButtonListener; // Listener to handle "Next" button clicks
    private final JTextArea evaluationArea;  // To display the final score

    // Initialize the GUI as before
    public VacuumWorldGUI() {
        JFrame frame = new JFrame("Vacuum Cleaner Grid");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(600, 500);

        JPanel topPanel = new JPanel(new GridLayout(1, 2));  // Two columns: one for percept, one for action
        perceptLabel = new JLabel("Percept: ", SwingConstants.CENTER);
        actionLabel = new JLabel("Action: ", SwingConstants.CENTER);
        topPanel.add(perceptLabel);
        topPanel.add(actionLabel);

        panel = new JPanel();
        panel.setLayout(new GridLayout(7, 7));
        labels = new JLabel[7][7];  // Create a 7x7 grid of JLabels

        // Initialize the grid with empty labels
        for (int row = 0; row < 7; row++) {
            for (int col = 0; col < 7; col++) {
                labels[row][col] = new JLabel("", SwingConstants.CENTER);
                labels[row][col].setOpaque(true); // So we can set background color
                panel.add(labels[row][col]);
            }
        }

        // Create a panel for the "Next" button
        JPanel buttonPanel = new JPanel();
        nextButton = new JButton("Next");
        nextButton.setPreferredSize(new Dimension(500, 50));
        // Add the action listener to the "Next" button
        nextButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (nextButtonListener != null) {
                    nextButtonListener.actionPerformed(e);  // Call the next step in the simulation
                }
            }
        });
        buttonPanel.add(nextButton);

        // Add a JTextArea for the final score display
        evaluationArea = new JTextArea();
        evaluationArea.setEditable(false);  // Make it read-only
        evaluationArea.setFont(new Font("Monospaced", Font.PLAIN, 12));

        JScrollPane scrollPane = new JScrollPane(evaluationArea);  // Add scrolling for large evaluations

        frame.setLayout(new BorderLayout());
        frame.add(topPanel, BorderLayout.NORTH);  // Add percept/action labels to the top
        frame.add(panel, BorderLayout.CENTER);    // Add the grid to the center
        frame.add(buttonPanel, BorderLayout.SOUTH); // Add the button to the bottom
        frame.add(scrollPane, BorderLayout.EAST);  // Add the evaluation area on the right

        frame.setVisible(true);
    }

    // Method to update the grid based on the new state
    public void updateGrid(char[][] grid) {
        for (int row = 0; row < grid.length; row++) {
            for (int col = 0; col < grid[row].length; col++) {
                labels[row][col].setText(String.valueOf(grid[row][col]));

                // Update colors based on content of the grid
                switch (grid[row][col]) {
                    case 'X':  // Wall
                        labels[row][col].setBackground(Color.DARK_GRAY);
                        labels[row][col].setForeground(Color.WHITE);
                        break;
                    case '>':  // Agent facing East
                    case '<':  // Agent facing West
                    case 'A':  // Agent default facing North
                    case 'V':  // Agent facing South
                        labels[row][col].setBackground(Color.WHITE);
                        labels[row][col].setForeground(Color.BLACK);
                        break;
                    case '*':  // Dirt
                        labels[row][col].setBackground(Color.ORANGE);
                        labels[row][col].setForeground(Color.BLACK);
                        break;
                    case ' ':  // Empty space
                        labels[row][col].setBackground(Color.LIGHT_GRAY);
                        break;
                }
            }
        }

        // Repaint to apply updates
        panel.revalidate();
        panel.repaint();
    }

    // Method to update the percept and action labels
    public void updateAction(String action) {
        actionLabel.setText("Action: " + action);
    }

    public void updatePercept(String percept) {
        perceptLabel.setText("Percept: " + percept);
    }

    // Method to set the action listener for the "Next" button
    public void setNextButtonListener(ActionListener listener) {
        this.nextButtonListener = listener;
    }

    // Method to display the final evaluation
    public void displayFinalEvaluation(String evaluation) {
        evaluationArea.setText(evaluation);  // Set the text of the evaluation area
    }

    // Method to disable the "Next" button
    public void disableNextButton() {
        nextButton.setEnabled(false);  // Disable the button once the simulation is complete
    }
}
