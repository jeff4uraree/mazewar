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

	// List of event codes


	// List of error codes



	// Used to identify the type of the packet
	public int type;

	// Used to keep track of the ordering of packets
	public int sequenceNum;

	// Used by the client when they initially join the game
	public String clientName;

	// Used for reporting and diagnosing errors
	public int error_code;

	// The number code corresponding to a certain action
	public int event;

	

}
