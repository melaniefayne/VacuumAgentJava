package vacworld.actions;

import vacworld.agent.Percept;
import vacworld.agent.Agent;

/** A percept in the vacuum cleaning world. */
public class VacPercept extends Percept {

	private final boolean dirt;
	private final boolean obstacle;
	private final boolean bump;
	private final int agentX;  // Store agent's X position
	private final int agentY;  // Store agent's Y position

	/** Construct a vacuum world percept. If the agent is in a square that
	 has dirt, then create a percept that sees dirt. If the agent is
	 directly in front of and facing an obstacle, then the agent can
	 see the obstacle. If the agent moved into an obstacle on the
	 previous turn the agent will feel a bump. */
	public VacPercept(VacuumState state, Agent agent) {

		super(state, agent);

		// Get the agent's current position
		agentX = state.getAgentX();
		agentY = state.getAgentY();
		int dir = state.getAgentDir();

		// Determine dirt
		dirt = state.hasDirt(agentX, agentY);

		// Determine obstacle
		int viewX = agentX + Direction.DELTA_X[dir];
		int viewY = agentY + Direction.DELTA_Y[dir];
		obstacle = state.hasObstacle(viewX, viewY);

		// Determine bump
		bump = state.bumped();
	}

	// Getter for agent's X position
	public int getX() {
		return agentX;
	}

	// Getter for agent's Y position
	public int getY() {
		return agentY;
	}

	/** Returns true if the percept reflects that the agent is over dirt. */
	public boolean seeDirt() {
		return dirt;
	}

	/** Returns true if the percept reflects that the square immediately
	 in front of the agent contains an obstacle. */
	public boolean seeObstacle() {
		return obstacle;
	}

	/** Returns true if the percept reflects that the agent bumped into
	 an obstacle as a result of its most recent action. */
	public boolean feelBump() {
		return bump;
	}

	@Override
	public String toString() {
		StringBuilder pString = new StringBuilder(5);
		if (dirt) pString.append("DIRT ");
		if (obstacle) pString.append("OBSTACLE ");
		if (bump) pString.append("BUMP");
		return pString.toString();
	}
}
