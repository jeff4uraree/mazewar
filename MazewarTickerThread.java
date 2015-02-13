import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.*;


public class MazewarTickerThread extends Thread {

	Socket socket = null;
	private ConcurrentLinkedQueue<MazewarPacket> queue = null;

	public MazewarTickerThread(Socket socket, ConcurrentLinkedQueue<MazewarPacket> queue)
	{
		super("MazewarTickerThread");
		this.socket = socket;
		this.queue = queue;
	}

	public void run()
	{
		
		while (true)
		{
			//Enqueue tick event
			MazewarPacket packetTicker = new MazewarPacket();
			packetTicker.type = MazewarPacket.FIRE_TICKER;
			queue.add(packetTicker);

			try
			{
				Thread.sleep(200);
			}
			catch (InterruptedException e) 
			{
			 	e.printStackTrace();
			}
		}
	}
} 