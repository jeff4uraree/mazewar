import java.net.*;
import java.io.*;
import java.util.*;
import java.util.concurrent.*;

public class MazewarServerSideBroadcasterThread extends Thread {

	private ConcurrentLinkedQueue<MazewarPacket> queue = null;
	private ArrayList<ObjectOutputStream> outStreams = new ArrayList<ObjectOutputStream>();
    private HashMap<String, MazewarPacket> clientID = new HashMap<String, MazewarPacket>();
	//private ArrayList<String> clientID = new ArrayList<String>();

	// Constructor for Broadcaster Thread
	public MazewarServerSideBroadcasterThread(ConcurrentLinkedQueue<MazewarPacket> queue) {
		super("MazewarServerSideBroadcasterThread");
		this.queue = queue;
	}

	public void run() {
		boolean listening = true;
		MazewarPacket packet;
		int numplayers = 0;

		while(listening)
		{
			// Dequeue packet from client queue and broadcast it
			packet = queue.poll();
			if (packet != null){
				if (packet.type == MazewarPacket.MAZEWAR_CONNECT)
				{
					// Map the clientIDs into the hashmap
					clientID.put(packet.username, packet);
					System.out.println(packet.username + "Connecting!");
					numplayers++;
					System.out.println("Number of players is now " + numplayers);
					broadcast(packet);
					if (numplayers == 4)
					{
						// Create a new packet
						MazewarPacket packetRemoteClientsCount = new MazewarPacket();
						packetRemoteClientsCount.type = MazewarPacket.START_GAME;
						System.out.println("Broadcasting to clients to start game");
						broadcast(packetRemoteClientsCount);

						for (MazewarPacket packetPlayerNames: this.clientID.values())
						{
							broadcast(packetPlayerNames);
						}

					}
				}
				else
				{
					broadcast(packet);
				}

			}
		}
	}

	public void addOutputStream(Socket socket){
		try {
			outStreams.add(new ObjectOutputStream(socket.getOutputStream()));
		} catch (Exception e) {
			System.err.println("ERROR: problem adding outputstreams!");
			System.exit(-1);
		}
	}

	public void broadcast(MazewarPacket packet) {
		for (ObjectOutputStream iteratorStream: this.outStreams) {
			try 
			{
				iteratorStream.writeObject(packet);

			}
			catch (Exception e)
			{
				System.err.println("ERROR: broadcaster failed!");
				System.exit(-1);
			}
		}
	}

}