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
    private int movesMade = 0;          // Track the number of moves made

    // Method to process the environment's percepts
    @Override
    public void see(Percept percept) {
        // Store the current percept for future decision-making
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

        // Priority 1: Check if there's dirt on the current square
        if (currentPercept.seeDirt()) {
            return new SuckDirt();  // Suck dirt if present
        }

        // Priority 2: Check if there's an obstacle ahead
        if (currentPercept.seeObstacle()) {
            return new TurnLeft();  // Turn left if there's an obstacle ahead
        }

        // Priority 3: Otherwise, move forward
        return new GoForward();
    }

    // Return a unique identifier for the agent
    @Override
    public String getId() {
        return "MyVacuumAgent";
    }
}
