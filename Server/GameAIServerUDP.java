package mygame.net;

import mygame.ai.NPCcontroller;

import java.io.IOException;
import java.net.InetAddress;
import java.util.UUID;

import tage.networking.server.GameConnectionServer;
import tage.networking.server.IClientInfo;
import tage.rml.Vector3f;

public class GameAIServerUDP extends GameConnectionServer<UUID> {
    NPCcontroller npcCtrl;
    public GameAIServerUDP(int localPort, NPCcontroller npc)
    {   super(localPort, ProtocolType.UDP);
        npcCtrl = npc;
    }

    public void sendCheckForAvatarNear()
    {   try
        {   String message = new String("isnr");
            message += "," + (npcCtrl.getNPC()).getX();
            message += "," + (npcCtrl.getNPC()).getY();
            message += "," + (npcCtrl.getNPC()).getZ();
            message += "," + (npcCtrl.getCriteria());
            sendPacketToAll(message);
        }
        catch (IOException e)
        { System.out.println("couldnt send msg"); e.printStackTrace(); }
    }
    
    public void sendNPCinfo() {
        // Broadcast NPC updates (position and size) every tick
        UUID npcID = npcCtrl.getNpcID();
        Vector3f pos = npcCtrl.getPosition();
        double size = npcCtrl.getSize();
        String info = String.format(
            "npcinfo,%s,%.2f,%.2f,%.2f,%.2f",
            npcID.toString(), pos.x, pos.y, pos.z, size);
        forwardPacketToAll(info, null);
    }

    public void sendNPCstart(UUID clientID) {
        // Notify a new client to create the NPC at its initial position
        UUID npcID = npcCtrl.getNpcID();
        Vector3f pos = npcCtrl.getPosition();
        String msg = String.format("createNPC,%s,%.2f,%.2f,%.2f",
            npcID.toString(), pos.x, pos.y, pos.z);
        forwardPacketTo(clientID, msg);
    }

    @Override
    public void processPacket(Object o, InetAddress senderIP, int port)
    {
        // Case where server receives request for NPCs
        // Received Message Format: (needNPC,id)
        String strMessage = (String)o;
		System.out.println("message received -->" + strMessage);
		String[] messageTokens = strMessage.split(",");

        if(messageTokens[0].compareTo("needNPC") == 0)
        {   System.out.println("server got a needNPC message");
            UUID clientID = UUID.fromString(messageTokens[1]);
            sendNPCstart(clientID);
        }
        // Case where server receives notice that an av is close to the npc
        // Received Message Format: (isnear,id)
        if(messageTokens[0].compareTo("isnear") == 0)
        {   UUID clientID = UUID.fromString(messageTokens[1]);
            handleNearTiming(clientID);
        }  
    }

    public void handleNearTiming(UUID clientID)
    {   
        npcCtrl.setNearFlag(true);
    }
    
    // ------------ SENDING NPC MESSAGES -----------------
    // Informs clients of the whereabouts of the NPCs.
    public void sendCreateNPCmsg(UUID clientID, String[] position)
    {   try
        {   System.out.println("server telling clients about an NPC");
            String message = new String("createNPC," + clientID.toString());
            message += "," + position[0];
            message += "," + position[1];
            message += "," + position[2];
            forwardPacketToAll(message, clientID);
        } catch (IOException e) { e.printStackTrace(); }
    }
}


