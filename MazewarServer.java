import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.*;

public class MazewarServer {



	/**
	 * @param args
	 */
	public static void main(String[] args) throws IOException {

		// TODO: 
		// Set up server 
		// enqueue (might be moved to another thread)
		// dequeue and broadcast (might be moved to another thread)

		ServerSocket serverSocket = null;
		Socket socket = null;
		final ConcurrentLinkedQueue<MazewarPacket> queue = new ConcurrentLinkedQueue<MazewarPacket>();
        boolean listening = true;	
		
		try
		{
			// Check if num of arg is valid
			if(args.length == 1) {
				serverSocket = new ServerSocket(Integer.parseInt(args[0]));
			} 
			else {
				System.err.println("ERROR: Invalid arguments!");
				System.exit(-1);
			}
		}
		catch (IOException e) {
			System.err.println("ERROR: Could not listen on port!");
			System.exit(-1);
		}

		System.out.println("Server is ready.");

		MazewarServerSideBroadcasterThread broadcaster = new MazewarServerSideBroadcasterThread(queue);
		broadcaster.start();

		MazewarTickerThread tickerThread = new MazewarTickerThread(socket, queue);
		tickerThread.start();
		
		while (listening) {
			socket = serverSocket.accept();
			broadcaster.addOutputStream(socket);
			MazewarHandlerThread handler = new MazewarHandlerThread(socket, queue);
			handler.start();
		}
		
		serverSocket.close();	

	}

}
