package vacworld;

import vacworld.agent.Action;
import vacworld.agent.Agent;
import vacworld.agent.Percept;
import vacworld.actions.VacPercept;
import vacworld.actions.SuckDirt;
import vacworld.actions.GoForward;
import vacworld.actions.TurnLeft;
import vacworld.actions.TurnRight;
import vacworld.actions.ShutOff;

import java.util.HashSet;
import java.util.Set;

public class VacAgent extends Agent {

    private VacPercept currentPercept;  // Store the current percept
    private final boolean allClean = false;   // Track if all dirt is cleaned
    private int row = 1;                // Track the current row
    private int col = 1;                // Track the current column
    private int direction = 0;          // Track direction agent is facing N = 0, E = 1, S = 2, W = 3
    private boolean isTurning = false;
    private boolean isSecondTurn = false;
    private boolean isThirdTurn = false;
    private Set<String> visited;        // Keep track of visited locations

    // Constructor
    public VacAgent() {
        visited = new HashSet<>();
    }

    // Method to process the environment's percepts
    @Override
    public void see(Percept percept) {
        if (percept instanceof VacPercept) {
            currentPercept = (VacPercept) percept;
        }
    }

    // Method to decide the next action
    @Override
    public Action selectAction() {
        // Ensure the agent has seen something before selecting an action
        if (currentPercept == null) {
            return new GoForward();  // Default action if no percept is available
        }

        // Track the current position
        String position = currentPercept.getX() + "," + currentPercept.getY();
        visited.add(position);  // Mark this position as visited

        // initial position logic
        if (position.equals("1,1")) {
            if (direction == 0) {
                direction = 1;
                return new TurnRight();
            }

            if (direction == 1) {
                if (!currentPercept.seeObstacle()) {
                    return new GoForward();
                } else {
                    direction = 2;
                    return new TurnRight();
                }
            }

            if (currentPercept.seeObstacle()) {
                return new ShutOff();
            }
            row = 2;
            return new GoForward();
        }

        // When agent is facing down
        if (direction == 2) {

            // moving in-between rows
            if (isTurning) {

                // 3.0 Face downwards in previous box
                if (isThirdTurn) {
                    if (isSecondTurn) {
                        isTurning = false;
                        isSecondTurn = false;
                        isThirdTurn = false;
                        return new GoForward();
                    } else {
                        isSecondTurn = true;
                        if (row % 2 == 0) {
                            return new TurnLeft();
                        } else {
                            return new TurnRight();
                        }
                    }
                }

                // 1.0 Go Forward if no obstacle
                if (!currentPercept.seeObstacle()) {
                    // if no more turns, turn turning off
                    if (!isSecondTurn) {
                        isTurning = false;
                    } else {
                        isSecondTurn = false;
                        isThirdTurn = true;
                    }
                    return new GoForward();
                } else {
                    // 2.0 Turn and check previous box
                    isSecondTurn = true;
                    if (row % 2 == 0) {
                        return new TurnRight();
                    } else {
                        return new TurnLeft();
                    }
                }
            }

            // turning agent to traverse columns in current row
            if (row % 2 == 0) { // move left on even rows
                direction = 3;
                return new TurnRight();
            } else {
                direction = 1;
                return new TurnLeft();
            }
        }

        // Priority 1: Check if there's dirt on the current square
        if (currentPercept.seeDirt()) {
            return new SuckDirt();  // Suck dirt if present
        }

        // Handle zigzag movement logic (row traversal)
        isTurning = false;
        if (direction == 1) { // moving right
            // Moving left to right on odd rows
            if (col < 6 && !currentPercept.seeObstacle()) {
                col++;
                return new GoForward();
            } else {
                // Reached end of the row or encountered an obstacle
                row++;  // Move to the next row downwards
                direction = 2;
                isTurning = true;
                return new TurnRight();  // Turn to face left
            }
        }

        if (direction == 3) { // moving left
            // Moving right to left on even rows
            if (col > 0 && !currentPercept.seeObstacle()) {
                col--;
                return new GoForward();
            } else {
                // Reached end of the row or encountered an obstacle
                row++;  // Move to the next row downwards
                direction = 2;
                isTurning = true;
                return new TurnLeft();  // Turn to face right
            }

        }

        return new GoForward();
    }

    // Return a unique identifier for the agent
    @Override
    public String getId() {
        return "MyVacuumAgent";
    }
}