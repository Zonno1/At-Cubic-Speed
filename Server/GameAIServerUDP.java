import java.io.IOException;
import java.net.InetAddress;
import java.util.UUID;

import tage.networking.server.GameConnectionServer;
import tage.networking.server.IClientInfo;
import tage.rml.Vector3f;

public class GameAIServerUDP extends GameConnectionServer<UUID> {
    private NPCcontroller npcCtrl;

    public GameAIServerUDP(int localPort, NPCcontroller npc) {
        super(localPort, ProtocolType.UDP);
        this.npcCtrl = npc;
    }

    // Request clients to check proximity
    public void sendCheckForAvatarNear() {
        try {
            String message = "isnr";
            Vector3f pos = npcCtrl.getPosition();
            message += "," + pos.getX() + "," + pos.getY() + "," + pos.getZ();
            message += "," + npcCtrl.getSize();
            sendPacketToAll(message);
        } catch (IOException e) {
            System.err.println("Could not send isnr message");
            e.printStackTrace();
        }
    }

    // Broadcast NPC info periodically
    public void sendNPCinfo() {
        try {
            UUID id = npcCtrl.getNpcID();
            Vector3f pos = npcCtrl.getPosition();
            double size = npcCtrl.getSize();
            String msg = String.format(
                "npcinfo,%s,%.2f,%.2f,%.2f,%.2f",
                id.toString(), pos.getX(), pos.getY(), pos.getZ(), size
            );
            sendPacketToAll(msg);
        } catch (IOException e) {
            System.err.println("Failed to broadcast NPC info");
            e.printStackTrace();
        }
    }

    // Send initial create message to specific client
    public void sendNPCstart(UUID clientID) {
        try {
            Vector3f pos = npcCtrl.getPosition();
            String msg = String.format(
                "createNPC,%s,%.2f,%.2f,%.2f",
                clientID.toString(), pos.getX(), pos.getY(), pos.getZ()
            );
            sendPacketToAll(msg);
        } catch (IOException e) {
            System.err.println("Failed to send createNPC to " + clientID);
            e.printStackTrace();
        }
    }

    @Override
    public void processPacket(Object o, InetAddress senderIP, int port) {
        String str = (String)o;
        System.out.println("Server received --> " + str);
        String[] tokens = str.split(",");

        if (tokens[0].equals("needNPC")) {
            UUID clientID = UUID.fromString(tokens[1]);
            System.out.println("Handling needNPC for " + clientID);
            sendNPCstart(clientID);
        }
        else if (tokens[0].equals("isnear")) {
            UUID clientID = UUID.fromString(tokens[1]);
            npcCtrl.setNearFlag(true);
        }
    }
}
