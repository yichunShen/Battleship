
/*
 * Authors: Raymond Li, David Tuck
 * Date created: 2018-05-30
 * Description: The artificial intelligence used to select where to fire
 */
import java.util.Random;
import java.util.Scanner;

public class AI {
	private Random rand = new Random();
	private Scanner sc = new Scanner(System.in);

	/**
	 * An enum for which mode the AI is in.
	 * 
	 * @type Hunt -> Parity is on, looking in entire grid for the highest PD after a
	 *       miss.
	 * 
	 * @type TARGET -> Parity is off, looking around a specific square after a hit.
	 */
	private enum Mode {
		HUNT, TARGET
	}

	// Start with hunt mode
	Mode mode = Mode.HUNT;

	// Constructor
	public AI() {

		// Generate Probability Density Distributed Graph for both grids
		generatePDDG(Battleship.enemyGrid);
		generatePDDG(Battleship.homeGrid);

		// Place ships on home grid
		try {
			placeShips(Battleship.homeGrid, Battleship.shipLengths);
		} catch (Exception e) {
		}
	}

	/**
	 * Generates where to place ships
	 * 
	 * @param grid
	 *            The grid to place ships on
	 * @param shipLengths
	 *            The lengths of ships to place
	 */
	public void placeShips(Square[][] grid, int[] shipLengths) {
		System.out.println("Select the mode. \n1-PDM\n2-Random");

		int mode = 0;

		try {
			mode = sc.nextInt();
		} catch (Exception e) {
			System.err.println("Input integer.");
		}
		int[][] rotationModifiers = new int[2][4];
		// [0][i]=y
		// [1][i]=x
		rotationModifiers[0][0] = 1;
		rotationModifiers[1][0] = 0;

		rotationModifiers[0][1] = -1;
		rotationModifiers[1][1] = 0;

		rotationModifiers[0][2] = 0;
		rotationModifiers[1][2] = 1;

		rotationModifiers[0][3] = 0;
		rotationModifiers[1][3] = -1;
		if (mode == 1) {// PDDG placement

		} else if (mode == 2) {// random ship placement
			for (int i = 0; i < shipLengths.length; i++) {// loop for number of ships
				boolean correct = false;
				int count = 0;

				int y = rand.nextInt(grid.length - 1);// random x coordinate. Start of ship
				int x = rand.nextInt(grid[0].length - 1);// random y coordinate. Start of ship

				do {
					correct = false;
					if (count > 4) {
						System.out.println("Over");
						y = rand.nextInt(grid.length - 1);// random y coordinate. Start of ship
						x = rand.nextInt(grid[0].length - 1);// random x coordinate. Start of ship
					}
					for (int a = 0; a < 4; a++) {
						int rotation = rand.nextInt(4);// random int to represent the orientation of the ship

						if (checkValidShipPosition(y, x, (y + (shipLengths[i] * rotationModifiers[0][rotation])),
								(x + (shipLengths[i] * rotationModifiers[1][rotation])), rotation, grid)) {
							correct = true;
							count = 0;
							System.out.println(i);
							System.out.println("Y:" + y + "  X:" + x + "  endY:"
									+ (y + ((shipLengths[i] - 1) * rotationModifiers[0][rotation])) + "  endX:"
									+ (x + ((shipLengths[i] - 1) * rotationModifiers[1][rotation])));
							Battleship.homeShips[i] = new Ship(grid, grid[y][x],
									grid[y + ((shipLengths[i] - 1) * rotationModifiers[0][rotation])][x
											+ ((shipLengths[i] - 1) * rotationModifiers[1][rotation])]);
							break;
						}
						count++;
					}

				} while (correct == false);
			}
		}
	}

	/**
	 * Check to see if a ship placement is in a valid position
	 * 
	 * @param X
	 *            the starting x coordinate that is always within the grid
	 * @param Y
	 *            the starting y coordinate that is always within the grid
	 * @param endX
	 *            the ending x coordinate for the ship that might be out of the
	 *            board
	 * @param endY
	 *            the ending y coordinate for the ship that might be out of the
	 *            board
	 * @param rotation
	 *            the orientation of the ship from x,y to endX,endY. 0=down 1=up
	 *            2=right 3=left
	 * @return If true, the ship is inside the grid and is not overlapping any other
	 *         ship. If false, the ships placement is either outside of the board or
	 *         is overlapping another ship
	 */
	public boolean checkValidShipPosition(int Y, int X, int endY, int endX, int rotation, Square[][] grid) {

		// first we must check to see if the end values are within the board size
		if (endX >= grid.length || endX < 0 || endY >= grid[0].length || endY < 0)
			return false;
		int sign = -1;
		if (rotation % 2 == 0)
			sign = 1;
		for (int i = 0; i < Math.abs(endX - X); i++)
			for (int j = 0; j < Math.abs(endY - Y); j++)
				if (Battleship.homeGrid[Y + (sign * j)][X + (sign * i)].shipType != 0) {
					System.out.println("overlace");
					return false;
				}

		return true;
	}

	/**
	 * Generates the initial probability density distributed graph for the given
	 * number of ships for each square in a given grid. Runs only at the beginning
	 * of the game.
	 * 
	 * @param grid
	 *            The grid for which to calculate the initial PDDG
	 */
	public void generatePDDG(Square[][] grid) {
		int[] distanceX, distanceY;
		for (int i = 0; i < grid.length; i++)
			for (int j = 0; j < grid[i].length; j++)
				for (int k : Battleship.shipLengths) {
					distanceX = new int[] { i, grid.length - i - 1 };
					distanceY = new int[] { j, grid[i].length - j - 1 };
					grid[j][i].huntPDx += generatePD(k, distanceX);
					grid[j][i].huntPDy += generatePD(k, distanceY);
					grid[j][i].combinehuntPDXY();
				}
	}

	/**
	 * Generates the initial probability density for the given number of ships for a
	 * component (horizontal or vertical) of a given square. Runs only at the
	 * beginning of the game.
	 * 
	 * @param shipLength
	 *            The length of a ship to calculate the PD for
	 * @param distance
	 *            The distances of a square to the edges of the grid (0 for a square
	 *            on the edge)
	 * @return The PD of a component (horizontal or vertical) of a square
	 */
	public int generatePD(int shipLength, int[] distance) {
		int pD = shipLength; // Probability density of a component (horizontal or vertical) of a square
		for (int l = 0; l < distance.length; l++)
			if (distance[l] < shipLength - 1)
				pD -= (shipLength - 1 - distance[l]);
		return pD;
	}

	/**
	 * Selects a square to fire at after updating the probability density for a
	 * given grid and ship lengths after a given lastShot
	 * 
	 * @param mode
	 *            The mode in which the AI is in (determined by hit or miss)
	 * 
	 * @param lastShot
	 *            The lastShot which was fired
	 * @param grid
	 *            The grid for which to calculate
	 * @param shipLengths
	 *            The lengths of ships still in play
	 */
	public Square aim(Mode mode, Square lastShot, Square[][] grid, int[] shipLengths) {

		// If lastShot was a hit, set aim mode to target, update hit PD and target
		// lastShot
		if (mode == Mode.TARGET) {
			for (int i = 0; i < shipLengths.length; i++)
				updateHitPD(grid, lastShot, shipLengths[i]);
			return target(grid, lastShot);
		}

		// If ship was sunk, check for side-by-side ships and target those
		else if (lastShot.status == SquareTypes.SUNK) {
			for (int i = 0; i < grid.length; i++)
				for (int j = 0; j < grid[0].length; j++)
					if (lastShot.status == SquareTypes.HIT)
						return aim(mode, grid[i][j], grid, shipLengths);
			mode = Mode.HUNT;
			return hunt(grid);
		}

		// If lastShot was a miss, update miss PD and hunt for a target
		else {
			for (int i = 0; i < shipLengths.length; i++)
				updateMissPD(grid, lastShot, shipLengths[i]);
			return hunt(grid);
		}
	}

	/**
	 * Updates the probability density for a ship for a missed square in a grid
	 * 
	 * @param grid
	 *            The grid in which the square is located
	 * @param lastShot
	 *            The square for which to calculate
	 * @param shipLength
	 *            The specific ship length for which to calculate
	 */
	public void updateMissPD(Square[][] grid, Square lastShot, int shipLength) {

		int[] bounds = getBounds(lastShot, grid);

		// Going up
		if (lastShot.y - bounds[0] >= shipLength) // No bounds
			for (int i = 0; i < shipLength; i++)
				grid[i][lastShot.x].huntPDy -= i;
		else // With bounds
			for (int i = bounds[0]; i < lastShot.y; i++)
				grid[i][lastShot.x].huntPDy -= lastShot.y - i;

		// Going down
		if (bounds[1] - lastShot.y >= shipLength) // No bounds
			for (int i = 0; i < shipLength; i++)
				grid[i][lastShot.x].huntPDy -= i;
		else // With bounds
			for (int i = bounds[1]; i > lastShot.y; i--)
				grid[i][lastShot.x].huntPDy -= lastShot.y - i;

		// Going left
		if (lastShot.x - bounds[2] >= shipLength) // No bounds
			for (int i = 0; i < shipLength; i++)
				grid[lastShot.y][i].huntPDx -= i;
		else // With bounds
			for (int i = bounds[2]; i < lastShot.x; i++)
				grid[lastShot.y][i].huntPDx -= lastShot.x - i;

		// Going right
		if (bounds[3] - lastShot.x >= shipLength) // No bounds
			for (int i = 0; i < shipLength; i++)
				grid[lastShot.y][i].huntPDx -= i;
		else // With bounds
			for (int i = bounds[3]; i > lastShot.x; i--)
				grid[lastShot.y][i].huntPDx -= lastShot.x - i;

		// Recombine an updated probability density distributed graph for hunt mode
		for (int i = 0; i < grid.length; i++)
			for (int j = 0; j < grid[i].length; j++)
				grid[i][j].combinehuntPDXY();
	}

	/**
	 * Updates the probability density for a ship for a hit square in a grid
	 * 
	 * @param grid
	 *            The grid in which the square is located
	 * @param lastShot
	 *            The square for which to calculate
	 * @param shipLength
	 *            The specific ship length for which to calculate
	 */
	public void updateHitPD(Square[][] grid, Square lastShot, int shipLength) {
		int[] bounds = getBounds(lastShot, grid);

		// Going up
		for (int i = lastShot.y - shipLength; i < lastShot.y; i++)
			if (i > bounds[0])
				grid[i][lastShot.x].targetPDy += i;

		// Going down
		for (int i = lastShot.y + shipLength; i > lastShot.y; i--)
			if (i < bounds[1])
				grid[i][lastShot.x].targetPDy += (bounds[i] - i);

		// Going left
		for (int i = lastShot.x - shipLength; i < lastShot.x; i++)
			if (i > bounds[2])
				grid[lastShot.y][i].targetPDy += i;

		// Going right
		for (int i = lastShot.x + shipLength; i > lastShot.x; i--)
			if (i < bounds[3])
				grid[lastShot.y][i].targetPDy += (bounds[i] - i);

		// Recombine an updated probability density distributed graph for target mode
		for (int i = 0; i < grid.length; i++)
			for (int j = 0; j < grid[i].length; j++)
				grid[i][j].combinetargetPDXY();
	}

	/**
	 * Gets the boundaries for a decrement of probability density in all 4
	 * directions
	 * 
	 * @param lastShot
	 *            The lastShot that was fired
	 * @param grid
	 *            The grid for which to calculate boundaries
	 * @return The boundaries as an int array
	 */
	private int[] getBounds(Square lastShot, Square[][] grid) {

		// Boundaries set for decrementing total square values
		int[] bounds = new int[] { -1, -1, -1, -1 };

		// Going up
		for (int i = lastShot.y - 1; i >= 0; i--)
			if (grid[i][lastShot.x].status != SquareTypes.UNKNOWN) {
				bounds[0] = i;
				break;
			}

		// Going down
		for (int i = lastShot.y + 1; i < grid.length; i++)
			if (grid[i][lastShot.x].status != SquareTypes.UNKNOWN) {
				bounds[1] = i;
				break;
			}

		// Going left
		for (int i = lastShot.x - 1; i >= 0; i--)
			if (grid[lastShot.y][i].status != SquareTypes.UNKNOWN) {
				bounds[2] = i;
				break;
			}

		// Going right
		for (int i = lastShot.x + 1; i < grid.length; i++)
			if (grid[lastShot.y][i].status != SquareTypes.UNKNOWN) {
				bounds[3] = i;
				break;
			}
		return bounds;
	}

	/**
	 * Finds the highest likely location of a ship from the entire grid
	 * 
	 * @return The target to fire at
	 */
	public Square hunt(Square[][] grid) {
		int max = 0;
		Square hunt = grid[0][0];
		for (int i = 0; i < grid[0].length; i++)
			for (int j = 0; j < grid.length; j++)
				if ((i + j) % 2 == 0 && grid[i][j].status == SquareTypes.UNKNOWN && grid[i][j].totalSquarePD > max) {
					max = grid[i][j].totalSquarePD;
					hunt = grid[i][j];
				}
		return hunt;
	}

	/**
	 * Finds the highest likely location of a ship from a 3x3 cross of a grid
	 * 
	 * @return The target to fire at
	 */
	public Square target(Square[][] grid, Square lastShot) {
		int max = 0;
		Square target = lastShot;
		for (int i = 0; i < grid[0].length; i++)
			for (int j = 0; j < grid.length; j++)
				if (grid[i][j].status == SquareTypes.UNKNOWN && grid[i][j].totalSquarePD > max) {
					max = grid[i][j].totalSquarePD;
					target = grid[i][j];
				}
		return target;
	}

}