import java.net.*;
import java.io.*;

public class MazewarHandlerThread extends Thread {

	// Create a blocking queue

	public MazewarHandlerThread(Socket socket) {
		super("MazewarHandlerThread");
		this.socket = socket;

		// Need to output this onto the console
		//Mazewar.consolePrintLn("Created new Thread to handle client");
		System.out.println("Created new Thread to handle client");
	}

	public void run()
	{
		// read packet
		// process
		// enqueue
		// dequeue packets
	}

}
