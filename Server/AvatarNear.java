
import tage.ai.behaviortrees.BTCondition;
package mygame.ai;

import mygame.ai.NPCcontroller;

public class AvatarNear extends BTCondition {
    NPC npc;
    NPCcontroller npcc;
    GameAIServerUDP server;

public AvatarNear(NPCcontroller npcController) {
        this.npcc = npcController;
    }

    public boolean isAvatarNear() {
        // Use the new getter on NPCcontroller
        return npcc.getNearFlag();
    }
}
    

