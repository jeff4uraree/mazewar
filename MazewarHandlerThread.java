import java.net.*;
import java.io.*;
import java.util.*;
import java.util.concurrent.*;

public class MazewarHandlerThread extends Thread {

	// Create a blocking queue
	private Socket socket = null;
	// Create local instance of queue
	private ConcurrentLinkedQueue<MazewarPacket> queue;

	public MazewarHandlerThread(Socket socket, ConcurrentLinkedQueue<MazewarPacket> queue) {
		super("MazewarHandlerThread");
		this.socket = socket;
		this.queue = queue;

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

		try
		{
			MazewarPacket packetFromClient;
			ObjectInputStream fromClient = new ObjectInputStream(socket.getInputStream());
			// Enqueue the packets 

			// Read Packet from client
			while (( packetFromClient = (MazewarPacket) fromClient.readObject()) != null) {				
				// Enqueue the packet
				System.out.println("Enqueue packet");
				queue.add(packetFromClient);
		
			}
			
			fromClient.close();
			socket.close();

		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}

	}

}
