/*
Copyright (C) 2004 Geoffrey Alan Washburn
   
This program is free software; you can redistribute it and/or
modify it under the terms of the GNU General Public License
as published by the Free Software Foundation; either version 2
of the License, or (at your option) any later version.
   
This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.
   
You should have received a copy of the GNU General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307,
USA.
*/
  
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JOptionPane;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import javax.swing.BorderFactory;
import java.io.Serializable;

// Updated java libraries Jan 26.
import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.*;

/**
 * The entry point and glue code for the game.  It also contains some helpful
 * global utility methods.
 * @author Geoffrey Washburn &lt;<a href="mailto:geoffw@cis.upenn.edu">geoffw@cis.upenn.edu</a>&gt;
 * @version $Id: Mazewar.java 371 2004-02-10 21:55:32Z geoffw $
 */

public class Mazewar extends JFrame {

        /**
         * The default width of the {@link Maze}.
         */
        private final int mazeWidth = 20;

        /**
         * The default height of the {@link Maze}.
         */
        private final int mazeHeight = 10;

        /**
         * The default random seed for the {@link Maze}.
         * All implementations of the same protocol must use 
         * the same seed value, or your mazes will be different.
         */
        //private final int mazeSeed = 42;
        private final int mazeSeed = 1989;
        /**
         * The {@link Maze} that the game uses.
         */
        private Maze maze = null;

        /**
         * The {@link GUIClient} for the game.
         */
        private GUIClient guiClient = null;

        /**
         * The panel that displays the {@link Maze}.
         */
        private OverheadMazePanel overheadPanel = null;

        /**
         * The table the displays the scores.
         */
        private JTable scoreTable = null;
        
        /** 
         * Create the textpane statically so that we can 
         * write to it globally using
         * the static consolePrint methods  
         */
        private static final JTextPane console = new JTextPane();
      
        /** 
         * Write a message to the console followed by a newline.
         * @param msg The {@link String} to print.
         */ 
        public static synchronized void consolePrintLn(String msg) {
                console.setText(console.getText()+msg+"\n");
        }
        
        /** 
         * Write a message to the console.
         * @param msg The {@link String} to print.
         */ 
        public static synchronized void consolePrint(String msg) {
                console.setText(console.getText()+msg);
        }
        
        /** 
         * Clear the console. 
         */
        public static synchronized void clearConsole() {
           console.setText("");
        }
        
        /**
         * Static method for performing cleanup before exiting the game.
         */
        public static void quit() {
                // Put any network clean-up code you might have here.
                // (inform other implementations on the network that you have 
                //  left, etc.)
                

                System.exit(0);
        }
       
	// create mazesocket
	private Socket mazeSocket = null;

        /** 
         * The place where all the pieces are put together. 
         */
        public Mazewar(String hostname, String port) {
                super("ECE419 Mazewar");
                consolePrintLn("ECE419 Mazewar started!");

                ConcurrentLinkedQueue<MazewarPacket> queue = new ConcurrentLinkedQueue<MazewarPacket>();
                MazewarPacket packet = null; 
                ObjectOutputStream toServer = null;
                ArrayList<String> clientNames = new ArrayList<String>();
                boolean listen = true;

                // Create the maze
                maze = new MazeImpl(new Point(mazeWidth, mazeHeight), mazeSeed);
                assert(maze != null);
                
                // Have the ScoreTableModel listen to the maze to find
                // out how to adjust scores.
                ScoreTableModel scoreModel = new ScoreTableModel();
                assert(scoreModel != null);
                maze.addMazeListener(scoreModel);
                
                // Throw up a dialog to get the GUIClient name.
                String name = JOptionPane.showInputDialog("Enter your name");
                if((name == null) || (name.length() == 0)) {
                  Mazewar.quit();
                }
                


                // You may want to put your network initialization code somewhere in
                // here.

		// DEBUG CODE:
		//System.out.println("Hostname is:" + hostname);
		//System.out.println("port num is:" + port);
		/*******************************************************************************************/		     
		
		try
		{

			// Process information to be sent to clienthandler thread
			int portnum = Integer.parseInt(port);

			// Create new socket
			mazeSocket = new Socket(hostname, portnum);

            /* stream to write back to server */
            toServer = new ObjectOutputStream(mazeSocket.getOutputStream());

			// Call clienthandler thread which will listen and queue packets
			MazewarClientSideReaderThread reader = new MazewarClientSideReaderThread(mazeSocket, name, queue);
            reader.start();

            // Find and create all current remote players
            MazewarPacket packetFindRemoteClients = new MazewarPacket();
            packetFindRemoteClients.type = MazewarPacket.MAZEWAR_CONNECT;
            packetFindRemoteClients.username = name;
            toServer.writeObject(packetFindRemoteClients);

            // determine number of remote clients
            // Keep polling until number of clients that join is 4


            while(listen == true)
            {
                // Dequeue packets
                if ((packet = queue.poll()) != null)
                {
                    if (packet.type == MazewarPacket.START_GAME)
                    {
                        System.out.println("Game has started!");
                        listen = false;
                    }
                }

            }
            // Reset listen
            listen = true;
            System.out.println("All clients connected!");

		}catch (UnknownHostException e) {
			System.err.println("ERROR: Don't know where to connect!!");
			System.exit(1);
		}catch (IOException e) {
			System.err.println("ERROR: Couldn't get I/O for the connection.");
			System.exit(1);
		} 

                int counter = 0;
                // Get client names and create corresponding remote clients
                try 
                {
                    while (listen == true)
                    {
                        if ((packet = queue.poll()) != null)
                        {
                            if (packet.type == MazewarPacket.MAZEWAR_CONNECT)
                            {
                                System.out.println(packet.username);
                                clientNames.add(packet.username);
                                counter++;
                                System.out.println(counter);
                                if (counter == 4)
                                {
                                    listen = false;
                                }
                            }
                        }
                    }
                    System.out.println("Got all user names!");
                }
                catch (Exception e)
                {
                    System.err.println("ERROR: Error getting numclients.");
                    System.exit(1);
                }
                for (String usernames : clientNames)
                {
                    if (usernames.equals(name))
                    {
                        // Create the GUIClient and connect it to the KeyListener queue
                        System.out.println("local Player " + name + "connected!");
                        guiClient = new GUIClient(name, toServer);
                        maze.addClient(guiClient);
                        Direction d = guiClient.getOrientation();
                        int compass = d.getDirection(d);
                        System.out.println("local Direction: "+ compass);

                    }
                    else
                    {
                        RemoteClient remoteClient = new RemoteClient(usernames);
                        System.out.println("Remote Player " + usernames + "connected!");
                        maze.addClient(remoteClient);
                        Direction e = remoteClient.getOrientation();
                        int remotecompass = e.getDirection(e);
                        System.out.println("remote Direction:"+ remotecompass);
                    }
                }
                System.out.println("Got all clients");
                // I think we need to add the keylistener outside
                this.addKeyListener(guiClient);

                // Use braces to force constructors not to be called at the beginning of the
                // constructor.
                /*
                {
                        maze.addClient(new RobotClient("Norby"));
                        maze.addClient(new RobotClient("Robbie"));
                        maze.addClient(new RobotClient("Clango"));
                        maze.addClient(new RobotClient("Marvin"));
                }
                */

		// Added: Jan 22
		// Add constructor for remote clients
		//maze.addClient(new RemoteClient());
                
                // Create the panel that will display the maze.
                overheadPanel = new OverheadMazePanel(maze, guiClient);
                assert(overheadPanel != null);
                maze.addMazeListener(overheadPanel);
                
                // Don't allow editing the console from the GUI
                console.setEditable(false);
                console.setFocusable(false);
                console.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder()));
               
                // Allow the console to scroll by putting it in a scrollpane
                JScrollPane consoleScrollPane = new JScrollPane(console);
                assert(consoleScrollPane != null);
                consoleScrollPane.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Console"));
                
                // Create the score table
                scoreTable = new JTable(scoreModel);
                assert(scoreTable != null);
                scoreTable.setFocusable(false);
                scoreTable.setRowSelectionAllowed(false);

                // Allow the score table to scroll too.
                JScrollPane scoreScrollPane = new JScrollPane(scoreTable);
                assert(scoreScrollPane != null);
                scoreScrollPane.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Scores"));
                
                // Create the layout manager
                GridBagLayout layout = new GridBagLayout();
                GridBagConstraints c = new GridBagConstraints();
                getContentPane().setLayout(layout);
                
                // Define the constraints on the components.
                c.fill = GridBagConstraints.BOTH;
                c.weightx = 1.0;
                c.weighty = 3.0;
                c.gridwidth = GridBagConstraints.REMAINDER;
                layout.setConstraints(overheadPanel, c);
                c.gridwidth = GridBagConstraints.RELATIVE;
                c.weightx = 2.0;
                c.weighty = 1.0;
                layout.setConstraints(consoleScrollPane, c);
                c.gridwidth = GridBagConstraints.REMAINDER;
                c.weightx = 1.0;
                layout.setConstraints(scoreScrollPane, c);
                                
                // Add the components
                getContentPane().add(overheadPanel);
                getContentPane().add(consoleScrollPane);
                getContentPane().add(scoreScrollPane);
                
                // Pack everything neatly.
                pack();

                // Let the magic begin.
                setVisible(true);
                overheadPanel.repaint();
                this.requestFocusInWindow();

                // Listen for packets
                System.out.println("Dequeuing packets");
                while (true){
                    packet = queue.poll();
                    
                        if (packet != null)
                        {
                            System.out.println(packet.type);
                            if (packet.type == MazewarPacket.MAZEWAR_EVENT)
                            {
                                Client clientToMove = null;
                                Iterator iteratorClients = maze.getClients();
                                System.out.println("Got an event!");
                                while(iteratorClients.hasNext())
                                {
                                    Client client = (Client) iteratorClients.next();
                                    if (client.getName().equals(packet.username))
                                    {
                                        System.out.println("Select client " + packet.username + "to move");
                                        clientToMove = client;
                                    }
                                }

                                if (packet.event == MazewarPacket.MOVE_FORWARD)
                                {
                                    clientToMove.forward();
                                }
                                else if (packet.event == MazewarPacket.MOVE_BACKWARD)
                                {
                                    clientToMove.backup();
                                }
                                else if (packet.event == MazewarPacket.TURN_LEFT)
                                {
                                    clientToMove.turnLeft();
                                }
                                else if (packet.event == MazewarPacket.TURN_RIGHT)
                                {
                                    clientToMove.turnRight();
                                }
                                else if (packet.event == MazewarPacket.FIRE)
                                {
                                    clientToMove.fire();
                                }
                            }
                            else if (packet.type == MazewarPacket.FIRE_TICKER)
                            {
                                maze.fireTick();
                                //System.out.println("Fire tick!");
                            }
                        }
                    
                }

        }

        
        /**
         * Entry point for the game.  
         * @param args Command-line arguments.
         */
        public static void main(String args[]) {

		// TODO: 
		// Error checking the number of arguments
                /* Create the GUI */
                new Mazewar(args[0], args[1]);
        }
}
