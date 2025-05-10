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

    // Broadcast NPC updates
    public void sendNPCinfo() {
        try {
            UUID id = npcCtrl.getNpcID();
            Vector3f pos = npcCtrl.getPosition();
            double size = npcCtrl.getSize();
            String msg = String.format(
                "npcinfo,%s,%.2f,%.2f,%.2f,%.2f",
                id.toString(), pos.x, pos.y, pos.z, size
            );
            sendPacketToAll(msg);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Send creation to a specific client
    public void sendNPCstart(UUID clientID) {
        try {
            Vector3f pos = npcCtrl.getPosition();
            String msg = String.format(
                "createNPC,%s,%.2f,%.2f,%.2f",
                clientID.toString(), pos.x, pos.y, pos.z
            );
            sendPacketToAll(msg);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void processPacket(Object o, InetAddress senderIP, int port) {
        String str = (String)o;
        String[] tokens = str.split(",");
        if (tokens[0].equals("needNPC")) {
            sendNPCstart(UUID.fromString(tokens[1]));
        } else if (tokens[0].equals("isnear")) {
            npcCtrl.setNearFlag(true);
        }
    }
}
