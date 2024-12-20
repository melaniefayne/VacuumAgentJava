package vacworld.actions;

import vacworld.agent.State;
import java.io.*;
import java.util.Random;

/** Represents a state in the vacuum world. */
public class VacuumState extends State {

	/* Constants for the initial state of the agent. */
	protected static int INIT_X = 1;
	protected static int INIT_Y = 1;
	protected static int INIT_DIR = Direction.NORTH;

	/** Constant representing a clear location. */
	public static final int CLEAR = 0;

	/** Constant representing a dirty location. */
	public static final int DIRT = 1;

	/** Constant representing a location with an obstacle. */
	public static final int WALL = 2;

	/* Default map for initial state.
     Note the interior arrays are columns in the map, not rows */
	protected static int[][] defaultMap = 
	{{WALL, WALL, WALL, WALL, WALL, WALL, WALL},
		{WALL, CLEAR, CLEAR, CLEAR, WALL, WALL, WALL},
		{WALL, CLEAR, DIRT, CLEAR, CLEAR, WALL, WALL},
		{WALL, CLEAR, CLEAR, CLEAR, CLEAR, CLEAR, WALL},
		{WALL, CLEAR, DIRT, CLEAR, CLEAR, DIRT, WALL},
		{WALL, WALL, WALL, WALL, CLEAR, CLEAR, WALL},
		{WALL, WALL, WALL, WALL, WALL, WALL, WALL}};

	protected static double DIRT_CHANCE = 0.37;
	protected static double WALL_CHANCE = 0.22;

	/* Variables for the state of the agent. */
	protected int agentX;
	protected int agentY;
	protected int agentDir;
	protected boolean robotOff = false;

	/** True if the agent just bumped into something. */
	protected boolean bump = false;

	/** An array that contains the locations of objects in the world. */
	protected int[][] map;

	protected int numDirtyLocs = 0;

	/** Height of the map, including surrounding walls of the room. */
	protected int height;

	/** Width of the map, including surrounding walls of the room. */
	protected int width;

	static final int DEFAULT_HEIGHT = 7;
	static final int DEFAULT_WIDTH = 7;


	/** Return the default initial state for the vacuum world. */
	public static VacuumState getInitState() {

		VacuumState state;

		state = new VacuumState();
		state.width = defaultMap.length;
		state.height = defaultMap[0].length;
		state.map = new int[state.width][state.height];
		state.map = defaultMap;
		state.numDirtyLocs = state.countDirtyLocs();
		state.agentX = INIT_X;
		state.agentY = INIT_Y;
		state.agentDir = INIT_DIR;
		return state;
	}

	/** Return a random initial state for the vacuum world. The agent's
   initial position is always the same, but the dirt locations and
   other obstacles are random. Note, this may generate random
   states that are impossible for the agent to solve, i.e., dirt may
   be entirely surrounded by obstacles. */
	public static VacuumState getRandomState() {
	   Random rgen = new Random();
	   return getRandomState(rgen);
	}
   
	/** Return a random initial state for the vacuum world using a specific
	 * seed. The agent's initial position is always the same, but the dirt 
	 * locations and other obstacles are random. Note, this may generate random
	 * states that are impossible for the agent to solve, i.e., dirt may
	 * be entirely surrounded by obstacles. */
   public static VacuumState getRandomState(int randSeed) {
      Random rgen = new Random(randSeed);
      return getRandomState(rgen);
   }

	/** A helper class for the other getRandomState() methods. Accepts
	 * a random number generator and uses this to generate a random initial 
	 * state for the vacuum world. The agent's initial position is always the 
	 * same, but the dirt locations and other obstacles are random. Note, 
	 * this may generate random states that are impossible for the agent to 
	 * solve, i.e., dirt may be entirely surrounded by obstacles. */
	protected static VacuumState getRandomState(Random rgen) {

		VacuumState state;
		float roll;

		state = new VacuumState();
		state.numDirtyLocs = 0;
		state.height = DEFAULT_HEIGHT;
		state.width = DEFAULT_WIDTH;
		state.map = new int[state.width][state.height];
		for (int i=0; i < state.width; i++) {
			for (int j=0; j < state.height; j++) {
				if (i == 0 || i == (state.width - 1))
					state.map[i][j] = WALL;
				else {
					if (j == 0 || j == (state.height - 1))
						state.map[i][j] = WALL;
					else {
						roll = rgen.nextFloat();
						if ((i != INIT_X || j != INIT_Y) && roll <= WALL_CHANCE) {
							state.map[i][j] = WALL;
						}
						else if (roll <= DIRT_CHANCE) {
							state.map[i][j] = DIRT;
							state.numDirtyLocs++;
						}
						else
							state.map[i][j] = CLEAR;
					}
				}
			}
		}   
		state.agentX = INIT_X;
		state.agentY = INIT_Y;
		state.agentDir = INIT_DIR;
		return state;
	}

	/** Construct a new empty vacuum state. This should only be called
      internally, and should be followed by code that initializes the
      state. */
	protected VacuumState() {
		// do nothing
	}

	/** Construct a new vacuum state given an array that specifies the map. */
	public VacuumState(int[][] map) {

		width = map.length;
		height = map[0].length;
		this.map = map;
		numDirtyLocs = countDirtyLocs();
		agentX = INIT_X;
		agentY = INIT_Y;
		agentDir = INIT_DIR;
	}

	/** Return the agent's X position. */
	public int getAgentX() {
		return agentX;
	}

	/** Return the agent's Y position. */
	public int getAgentY() {
		return agentY;
	}

	/** Return the agent's orientation. Note that the constants in
      the Direction class are used to interpret the meaning of this
      result.*/
	public int getAgentDir() {
		return agentDir;
	}

	/** Return a string the represents the agent's orientation. */
	public String getAgentDirString() {
		return Direction.toString(agentDir);
	}

	/** Change the agent's X position. */
	public void setAgentX(int x) {
		agentX = x;
	}

	/** Change the agent's Y position. */
	public void setAgentY(int y) {
		agentY = y;
	}

	/** Change the agent's orientation. */
	public void setAgentDir(int dir) {
		agentDir = dir;
	}

	/** Set an indicator that the robot has shut down in the state. */
	public void setRobotOff(boolean off) {
		robotOff = off;
	}

	/** Set an indicator that the robot bumped into an obstacle 
      during its previous move. */
	public void setBump(boolean bump) {
		this.bump = bump;
	}

	/** Remove dirt from the specified location. */
	public void removeDirt(int x, int y) {

		if (map[x][y] == DIRT) {
			map[x][y] = CLEAR;
			numDirtyLocs--;
		}
	}

	/** Return true if the specified location has dirt in it. */
	public boolean hasDirt(int x, int y) {

		if (map[x][y] == DIRT)
			return true;
		else
			return false;
	}

	/** Return true if the specified location has an obstacle in it. */
	public boolean hasObstacle(int x, int y) {

		if (map[x][y] == WALL)
			return true;
		else
			return false;
	}

	/** Return true if the agent bumped into an obstacle on its previous
      move. */
	public boolean bumped() {

		return bump;
	}

	/** Return true if the location is within bounds of the state's map. */
	public boolean inBounds(int x, int y) {

		if (x >= 0 && x < width && y >= 0 && y < height)
			return true;
		else
			return false;
	}

	/** Return true if the robot is turned off in the state. */
	public boolean isRobotOff() {

		return robotOff;
	}

	/** Return the number of dirty locations in the state. */
	public int getNumDirtyLocs() {

		return numDirtyLocs;
	}

	/** Print an output of the state to a print stream. This output includes
      a map as well as information about the agent's location and the
      direction it is facing. On the map, "A" denotes the agent, "*"
      denotes dirt, and "X" denotes an obstacle. Depending on the 
      direction the agent is facing, it may also be displayed as "<", "V",
      or ">". */
	public void display(PrintStream output) {

		for (int j=0; j < width; j++)
			output.print("  " + j);
		output.println();

		output.print(" ");
		for (int j=0; j < width; j++)
			output.print("+--");
		output.println("+");

		/* To print to the screen properly, the loop in the Y
       direction must be the outer loop. */
		for (int i=0; i < height; i++) {
			output.print(i + "|");
			for (int j=0; j < width; j++) {
				if (i == agentY && j == agentX)
					output.print(getAgentCharacter());
				else if (hasDirt(j,i))
					output.print("*");
				else if (hasObstacle(j,i))
					output.print("X");
				else
					output.print(" ");
				output.print(" |");
			}
			output.println();

			output.print(" +");
			for (int j=0; j < width - 1; j++)
				output.print("--+");
			output.println("--+");

		}
		output.println("Location: (" + agentX + "," + agentY + ")  " +
				"Facing: " + getAgentDirString());
		output.println();

	}

	/** Print an output of the state to the screen. This output includes
      a map as well as information about the agent's location and the
      direction it is facing. On the map, "A" denotes the agent, "*"
      denotes dirt, and "X" denotes an obstacle. The user must press
      ENTER to continue. */
	public void display() {

		display(System.out);
	}


	/** A utility method used to count how many dirty squares are in the 
      state. */
	private int countDirtyLocs() {

		int dirtCount = 0;
		for (int dx=1; dx < width; dx++) {
			for (int dy=1; dy < height; dy++) {
				if (map[dx][dy] == DIRT)
					dirtCount++;
			}
		}
		return dirtCount;
	}

	/** A utility method used to determine what character should
      represent the agent. A different character is used for each
      direction the agent is facing. */
	private String getAgentCharacter() {

		switch (agentDir) {
		case Direction.NORTH:
			return "A";
		case Direction.EAST:
			return ">";
		case Direction.SOUTH:
			return "V";
		case Direction.WEST:
			return "<";
		}
		return "A";
	}

	// GUI Helpers *****
	public char[][] getGrid() {
		char[][] grid = new char[width][height];

		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
				if (i == agentX && j == agentY) {
					// Place the agent in the grid
					grid[i][j] = getAgentCharacter().charAt(0);
				} else if (hasDirt(i, j)) {
					// Place dirt in the grid
					grid[i][j] = '*';
				} else if (hasObstacle(i, j)) {
					// Place walls in the grid
					grid[i][j] = 'X';
				} else {
					// Empty space
					grid[i][j] = ' ';
				}
			}
		}
		return grid;
	}
}

