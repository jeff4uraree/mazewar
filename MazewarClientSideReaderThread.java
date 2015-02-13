import java.net.*;
import java.io.*;
import java.util.*;
import java.util.concurrent.*;

public class MazewarClientSideReaderThread extends Thread {

	private Socket socket = null;
	private ConcurrentLinkedQueue<MazewarPacket> queue = null;
	private String username;
	// Constructor classes
	public MazewarClientSideReaderThread(Socket socket, String username, ConcurrentLinkedQueue<MazewarPacket> queue)
	{
		super("MazewarClientSideReaderThread");
		this.socket = socket;
		this.queue = queue;
		this.username = username;
	}

	public void run () {
		try 
		{
			MazewarPacket packetFromServer;
			ObjectInputStream fromServer = new ObjectInputStream(socket.getInputStream());
			while ((packetFromServer = (MazewarPacket) fromServer.readObject()) != null) 
			{
				System.out.println("Adding packet to queue");
				queue.add(packetFromServer);
			}

		}
		catch (Exception e)
		{
			System.err.println("ClientSideReaderFailed!");
			System.exit(-1);
		}
	}

}
