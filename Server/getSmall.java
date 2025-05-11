import tage.ai.behaviortrees.BTAction;
import tage.ai.behaviortrees.BTStatus;

public class getSmall extends BTAction {
    private NPC npc;
    private NPCcontroller npcc;

    public getSmall(NPCcontroller controller, NPC c) {
        super();
        npc = c;
        npcc = controller;
    }

     @Override
    protected BTStatus update(float elapsedTime) {
        if (npcc.getNearFlag()) {
            // Don't shrink if player is nearby
            return BTStatus.BH_FAILURE;
        }

        System.out.println("[getSmall] Making NPC small");
        npc.setSmall();
        return BTStatus.BH_SUCCESS;
        
    }
}
