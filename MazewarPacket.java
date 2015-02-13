import java.io.Serializable;
 /**
 * MazewarPacket
 * ============
 * 
 * Packet format of the packets exchanged between the Broker and the Client
 * 
 * The design of the packet consists of various fields
 * - Packet Type
 * - sequence number
 * - client name for initialization
 * - the action that client character will take
 * - and potentially any errorcode
 *
 *
 */

public class MazewarPacket implements Serializable {

	// List of packet types
	public static final int MAZEWAR_NULL    = 0;
	public static final int MAZEWAR_CONNECT = 101;
	public static final int MAZEWAR_EVENT = 102;
	public static final int FIND_REMOTE_CLIENTS = 103;
	public static final int START_GAME = 104;
	public static final int MAZEWAR_COUNT = 105;
	public static final int FIRE_TICKER = 106;

	// List of event codes
    public static final int MOVE_FORWARD = 0;
    public static final int MOVE_BACKWARD = 1;
    public static final int TURN_LEFT = 2;
    public static final int TURN_RIGHT = 3;
    public static final int FIRE = 4;

	// List of error codes



	// Used to identify the type of the packet
	public int type;

	// Used to keep track of the ordering of packets
	public int sequenceNum;

	// Used by the client when they initially join the game
	public String username;

	// Used for reporting and diagnosing errors
	public int error_code;

	// The number code corresponding to a certain action
	public int event;

	// The number of clients in the game
	public int num;

	

}
