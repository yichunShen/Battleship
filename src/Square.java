
/*
 * Authors: Raymond Li, David Tuck
 * Date created: 2018-05-30
 * Description: Battleship squares
 */
import java.io.Serializable;

public class Square implements Serializable {

	private static final long serialVersionUID = -4896377842037028342L;

	public int graphArray[] = new int[Battleship.shipLengths.length];
	public int totalSquareValue;
	public SquareTypes status;
	public int x, y;
	public int shipType; // #0 for no ship, #n for n-length ship

	/**
	 * sets contains to EMPTY
	 */
	public Square(int x, int y) {
		status = SquareTypes.UNKNOWN;
	}

	public int[] returnGraphArray() {
		return graphArray;
	}
}