package vacworld;

import vacworld.agent.Action;
import vacworld.agent.Agent;
import vacworld.agent.Percept;
import vacworld.actions.SuckDirt;
import vacworld.actions.GoForward;
import vacworld.actions.TurnLeft;
import vacworld.actions.TurnRight;
import vacworld.actions.ShutOff;

public class VacAgent extends Agent {

    // Constructor
    public VacAgent() {
        // Initialization code, if needed
    }

    // Implement the see() method, matching the signature in Agent.java
    @Override
    public void see(Percept percept) {
        // Check if the percept is an instance of VacPercept
        if (percept != null) {
            Percept vacPercept = (Percept) percept;
            // Process VacPercept (e.g., checking for dirt or obstacles)
        } else {
            System.out.println("ERROR - Percept is not of type VacPercept");
        }
    }

    // Implement the selectAction() method
    @Override
    public Action selectAction() {
        // Logic to decide which action to take
        // Example: return new GoForward();
        return new GoForward();  // Placeholder action
    }

    // Implement the getId() method
    @Override
    public String getId() {
        return "MyVacuumAgent";  // Unique ID for your agent
    }
}
