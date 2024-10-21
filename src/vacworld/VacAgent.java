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

public class VacAgent extends Agent {

    private VacPercept currentPercept;  // Store the current percept
    private boolean allClean = false;   // Track if all dirt is cleaned
    private int row = 1;                // Track the current row
    private int col = 1;                // Track the current column
    private int direction = 0;          // Track direction agent is facing
    // N = 0, E = 1, S = 2, W = 3
    private boolean isTurning = false;
    private boolean isSecondTurn = false;
    private boolean isThirdTurn = false;

    // Constructor
    public VacAgent() {
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

        if(allClean) return handleBackToStart();

        // 1.0 initial position logic
        String position = currentPercept.getX() + "," + currentPercept.getY();
        if (position.equals("1,1")) return handleFirstPos();

        // Moving from one row to the next
        if (direction == 2) {
            // moving down to next row
            if (isTurning) return handleTurning();

            // Turning to start traversing current row
            if (row % 2 == 0) { // right to left on even rows
                direction = 3;
                return new TurnRight();
            } else { // left to right on even rows
                direction = 1;
                return new TurnLeft();
            }
        }

        // Priority 1: Suck dirt if on current square
        if (currentPercept.seeDirt()) return new SuckDirt();

        // 2.0 Handle left to right movement
        if (direction == 1) {
            if (col < 6 && !currentPercept.seeObstacle()) {
                col++;
                return new GoForward();
            } else { // 4.0 Handle last column
                if (row == 5) {
                    allClean = true;
                    isTurning = false;
                    isSecondTurn = false;
                    return handleBackToStart();
                }
                // Reached end of the row or encountered an obstacle
                row++;  // Initiate move to the next row downwards
                direction = 2;
                isTurning = true;
                return new TurnRight();
            }
        }

        // 3.0 Handle right to left movement
        if (direction == 3) {
            if (col > 0 && !currentPercept.seeObstacle()) {
                col--;
                return new GoForward();
            } else {
                // Reached end of the row or encountered an obstacle
                row++;  // Initiate Move to the next row downwards
                direction = 2;
                isTurning = true;
                return new TurnLeft();  // Turn to face right
            }

        }

        // 5.0 Default action
        return new GoForward();
    }

    private Action handleTurning() {
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

    private Action handleFirstPos() {
        if (direction == 0) { // 1.0 Try move in right direction
            direction = 1;
            return new TurnRight();
        }

        if (direction == 1) {
            if (!currentPercept.seeObstacle()) {
                return new GoForward();
            } else { // 2.0 Try move to next row
                direction = 2;
                return new TurnRight();
            }
        }

        // 3.0  Shut down if surrounded by obstacles
        if (currentPercept.seeObstacle()) {
            return new ShutOff();
        }
        row = 2;
        return new GoForward();
    }
private boolean startedGoBack = false;
    private Action handleBackToStart() {
        if(!startedGoBack && direction != 3) {
            direction++;
            return new TurnRight();
        }

        startedGoBack = true;

        String position = currentPercept.getX() + "," + currentPercept.getY();
        if (position.equals("1,1")) return new ShutOff();

        if(isTurning){
            if (currentPercept.seeObstacle()) return new TurnLeft();
            return new GoForward();
        }

        if (!currentPercept.seeObstacle()) {
            return new GoForward();
        }else{
            isTurning = true;
            direction = 0;
            return new TurnRight();
        }
    }

    // Return a unique identifier for the agent
    @Override
    public String getId() {
        return "MyVacuumAgent";
    }
}