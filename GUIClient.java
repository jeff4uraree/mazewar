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

import java.awt.event.KeyListener;
import java.awt.event.KeyEvent;

import java.io.*;
import java.net.*;

/**
 * An implementation of {@link LocalClient} that is controlled by the keyboard
 * of the computer on which the game is being run.  
 * @author Geoffrey Washburn &lt;<a href="mailto:geoffw@cis.upenn.edu">geoffw@cis.upenn.edu</a>&gt;
 * @version $Id: GUIClient.java 343 2004-01-24 03:43:45Z geoffw $
 */

public class GUIClient extends LocalClient implements KeyListener {

        ObjectOutputStream outStream = null;

        /**
         * Create a GUI controlled {@link LocalClient}.  
         */
        public GUIClient(String name, ObjectOutputStream outStream) {
                super(name);
                this.outStream = outStream;
        }
        
        /**
         * Handle a key press.
         * @param e The {@link KeyEvent} that occurred.
         */
        public void keyPressed(KeyEvent e) {
                // If the user pressed Q, invoke the cleanup code and quit. 
                if((e.getKeyChar() == 'q') || (e.getKeyChar() == 'Q')) {
                        Mazewar.quit();
                // Up-arrow moves forward.
                } else if(e.getKeyCode() == KeyEvent.VK_UP) {
                        //forward();
                        this.sendPacket(MazewarPacket.MAZEWAR_EVENT, MazewarPacket.MOVE_FORWARD);
                // Down-arrow moves backward.
                } else if(e.getKeyCode() == KeyEvent.VK_DOWN) {
                        //backup();
                        this.sendPacket(MazewarPacket.MAZEWAR_EVENT, MazewarPacket.MOVE_BACKWARD);
                // Left-arrow turns left.
                } else if(e.getKeyCode() == KeyEvent.VK_LEFT) {
                        //turnLeft();
                        this.sendPacket(MazewarPacket.MAZEWAR_EVENT, MazewarPacket.TURN_LEFT);
                // Right-arrow turns right.
                } else if(e.getKeyCode() == KeyEvent.VK_RIGHT) {
                        //turnRight();
                        this.sendPacket(MazewarPacket.MAZEWAR_EVENT, MazewarPacket.TURN_RIGHT);
                // Spacebar fires.
                } else if(e.getKeyCode() == KeyEvent.VK_SPACE) {
                        //fire();
                        this.sendPacket(MazewarPacket.MAZEWAR_EVENT, MazewarPacket.FIRE);
                }
        }
        
        /**
         * Handle a key release. Not needed by {@link GUIClient}.
         * @param e The {@link KeyEvent} that occurred.
         */
        public void keyReleased(KeyEvent e) {
        }
        
        /**
         * Handle a key being typed. Not needed by {@link GUIClient}.
         * @param e The {@link KeyEvent} that occurred.
         */
        public void keyTyped(KeyEvent e) {
        }

        public void sendPacket(int type, int event)
        {
                MazewarPacket packet = new MazewarPacket();
                packet.type = type;
                packet.username = this.getName();
                packet.event = event;

                System.out.println("Sending packet to" + packet.username + " , for event " + type + "to move " + event);

                try
                {
                        this.outStream.writeObject(packet);
                }
                catch (Exception e)
                {
                        System.err.println("ERROR: Error sending key commands.");
                        System.exit(1);
                }
        }

}