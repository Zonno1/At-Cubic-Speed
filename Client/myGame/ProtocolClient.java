package myGame;

import java.awt.Color;
import java.io.IOException;
import java.net.InetAddress;
import java.util.Iterator;
import java.util.UUID;
import java.util.Vector;
import org.joml.*;

import tage.*;
import tage.networking.client.GameConnectionClient;
import tage.rml.Vector3f;

public class ProtocolClient extends GameConnectionClient
{
	private MyGame game;
	private GhostManager ghostManager;
	private UUID id;
	private GhostNPC ghostNPC;
	
	public ProtocolClient(InetAddress remoteAddr, int remotePort, ProtocolType protocolType, MyGame game) throws IOException 
	{	super(remoteAddr, remotePort, protocolType);
		this.game = game;
		this.id = UUID.randomUUID();
		ghostManager = game.getGhostManager();
	}
	
	public UUID getID() { return id; }
	
	@Override
	protected void processPacket(Object message)
	{	String strMessage = (String)message;
		System.out.println("message received -->" + strMessage);
		String[] messageTokens = strMessage.split(",");
		
		// Game specific protocol to handle the message
		if(messageTokens.length > 0)
		{
			// Handle JOIN message
			// Format: (join,success) or (join,failure)
			if(messageTokens[0].compareTo("join") == 0)
			{	if(messageTokens[1].compareTo("success") == 0)
				{	System.out.println("join success confirmed");
					game.setIsConnected(true);
					sendCreateMessage(game.getPlayerPosition());

					try {
            sendPacket("needNPC," + id.toString());
            System.out.println("Requested NPC from server");
        } catch (IOException e) {
            e.printStackTrace();
        }
				}
				if(messageTokens[1].compareTo("failure") == 0)
				{	System.out.println("join failure confirmed");
					game.setIsConnected(false);
			}	}
			
			// Handle BYE message
			// Format: (bye,remoteId)
			if(messageTokens[0].compareTo("bye") == 0)
			{	// remove ghost avatar with id = remoteId
				// Parse out the id into a UUID
				UUID ghostID = UUID.fromString(messageTokens[1]);
				ghostManager.removeGhostAvatar(ghostID);
			}
			
			// Handle CREATE message
			// Format: (create,remoteId,x,y,z)
			// AND
			// Handle DETAILS_FOR message
			// Format: (dsfr,remoteId,x,y,z)
			if (messageTokens[0].compareTo("create") == 0 || (messageTokens[0].compareTo("dsfr") == 0))
			{	// create a new ghost avatar
				// Parse out the id into a UUID
				UUID ghostID = UUID.fromString(messageTokens[1]);
				
				// Parse out the position into a org.joml.Vector3f
				org.joml.Vector3f ghostPosition = new org.joml.Vector3f(
					Float.parseFloat(messageTokens[2]),
					Float.parseFloat(messageTokens[3]),
					Float.parseFloat(messageTokens[4]));

				try
				{	ghostManager.createGhostAvatar(ghostID, ghostPosition);
				}	catch (IOException e)
				{	System.out.println("error creating ghost avatar or npc");
				}
			}
			
			// Handle WANTS_DETAILS message
			// Format: (wsds,remoteId)
			if (messageTokens[0].compareTo("wsds") == 0)
			{
				// Send the local client's avatar's information
				// Parse out the id into a UUID
				UUID ghostID = UUID.fromString(messageTokens[1]);
				sendDetailsForMessage(ghostID, game.getPlayerPosition());
			}
			
			// Handle MOVE message
			// Format: (move,remoteId,x,y,z)
			if (messageTokens[0].compareTo("move") == 0)
			{
				// move a ghost avatar
				// Parse out the id into a UUID
				UUID ghostID = UUID.fromString(messageTokens[1]);
				
				// Parse out the position into a org.joml.Vector3f
				org.joml.Vector3f ghostPosition = new org.joml.Vector3f(
					Float.parseFloat(messageTokens[2]),
					Float.parseFloat(messageTokens[3]),
					Float.parseFloat(messageTokens[4]));
				
				ghostManager.updateGhostAvatar(ghostID, ghostPosition);
			}

			//npc conditions
			if (messageTokens[0].compareTo("createNPC") == 0)
			{  // create a new ghost NPC
				// Parse out the position
				org.joml.Vector3f ghostPosition = new org.joml.Vector3f(
				Float.parseFloat(messageTokens[1]),
				Float.parseFloat(messageTokens[2]),
				Float.parseFloat(messageTokens[3]));
				try{createGhostNPC(ghostPosition);} catch (IOException e) { System.out.println("error creating NPC"); } // error creating ghost avatar
			} 

			if (messageTokens[0].equals("npcinfo")) {
			org.joml.Vector3f newPos = new org.joml.Vector3f(
				Float.parseFloat(messageTokens[1]),
				Float.parseFloat(messageTokens[2]),
				Float.parseFloat(messageTokens[3]));
			double gsize = Double.parseDouble(messageTokens[4]);
			updateGhostNPC(newPos, gsize);
			System.out.println("[ProtocolClient] Updated NPC position and size to " + newPos + ", " + gsize);
			}
			/*
			if (messageTokens[0].equals("mnpc")) {
				org.joml.Vector3f newPos = new org.joml.Vector3f(
				Float.parseFloat(messageTokens[1]),
				Float.parseFloat(messageTokens[2]),
				Float.parseFloat(messageTokens[3]));
    			if (ghostNPC != null) {
					ghostNPC.setPosition(newPos);
					System.out.println("[ProtocolClient] Moved ghostNPC to " + newPos);
				}
			}*/

			if (messageTokens[0].equals("isnear")) {
				// Parse NPC position and proximity threshold
				float npcX = Float.parseFloat(messageTokens[1]);
				float npcY = Float.parseFloat(messageTokens[2]);
				float npcZ = Float.parseFloat(messageTokens[3]);
				double criteria = Double.parseDouble(messageTokens[4]);

				// Get player position
				org.joml.Vector3f playerPos = game.getPlayerPosition();
				org.joml.Vector3f npcPos = new org.joml.Vector3f(npcX, npcY, npcZ);

				// Calculate Euclidean distance
				double dist = playerPos.distance(npcPos);

				// If within proximity, reply to server
				if (dist < criteria) {
					System.out.println("[ProtocolClient] NPC is nearby! Distance: " + dist);
					sendNearReply(); // reply to server
				} else {
					System.out.println("[ProtocolClient] NPC is too far. Distance: " + dist);
				}
			}
		}
	}

	
	// The initial message from the game client requesting to join the 
	// server. localId is a unique identifier for the client. Recommend 
	// a random UUID.
	// Message Format: (join,localId)
	
	public void sendJoinMessage()
	{	try 
		{	sendPacket(new String("join," + id.toString()));
		} catch (IOException e) 
		{	e.printStackTrace();
	}	}
	
	// Informs the server that the client is leaving the server. 
	// Message Format: (bye,localId)

	public void sendByeMessage()
	{	try 
		{	sendPacket(new String("bye," + id.toString()));
		} catch (IOException e) 
		{	e.printStackTrace();
	}	}
	
	// Informs the server of the client�s Avatar�s position. The server 
	// takes this message and forwards it to all other clients registered 
	// with the server.
	// Message Format: (create,localId,x,y,z) where x, y, and z represent the position

	public void sendCreateMessage(org.joml.Vector3f position)
	{	try 
		{	String message = new String("create," + id.toString());
			message += "," + position.x();
			message += "," + position.y();
			message += "," + position.z();
			
			sendPacket(message);
		} catch (IOException e) 
		{	e.printStackTrace();
	}	}
	
	// Informs the server of the local avatar's position. The server then 
	// forwards this message to the client with the ID value matching remoteId. 
	// This message is generated in response to receiving a WANTS_DETAILS message 
	// from the server.
	// Message Format: (dsfr,remoteId,localId,x,y,z) where x, y, and z represent the position.

	public void sendDetailsForMessage(UUID remoteId, org.joml.Vector3f position)
	{	try 
		{	String message = new String("dsfr," + remoteId.toString() + "," + id.toString());
			message += "," + position.x();
			message += "," + position.y();
			message += "," + position.z();
			
			sendPacket(message);
		} catch (IOException e) 
		{	e.printStackTrace();
	}	}
	
	// Informs the server that the local avatar has changed position.  
	// Message Format: (move,localId,x,y,z) where x, y, and z represent the position.

	public void sendMoveMessage(org.joml.Vector3f position)
	{	try 
		{	String message = new String("move," + id.toString());
			message += "," + position.x();
			message += "," + position.y();
			message += "," + position.z();
			
			sendPacket(message);
		} catch (IOException e) 
		{	e.printStackTrace();
	}	}

// ------------- GHOST NPC SECTION --------------
	private void createGhostNPC(org.joml.Vector3f position) throws IOException
	{ 
		if (ghostNPC == null){
		ghostNPC = new GhostNPC(0, game.getNPCshape(),
		game.getNPCtexture(), position);
		//game.getEngine().getSceneGraph().addGameObject(ghostNPC); 
		}
	}
	private void updateGhostNPC(org.joml.Vector3f position, double gsize)
	{ 
		boolean gs;
		if (ghostNPC == null)
		{ 
			try { createGhostNPC(position);} 
			catch (IOException e) { System.out.println("error creating npc"); }
		}
		ghostNPC.setPosition(position);
		if (gsize == 1.0) gs=false; else gs=true;
			ghostNPC.setSize(gs);
	}
	//might need find npc

	public void sendNearReply() {
    try {
        sendPacket("isnear," + id.toString());
    } catch (IOException e) {
        e.printStackTrace();
    }
}
}
