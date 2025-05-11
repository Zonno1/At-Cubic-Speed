import tage.ai.behaviortrees.BTAction;
import tage.ai.behaviortrees.BTStatus;

public class getBig extends BTAction {
    private NPC npc;

    public getBig(NPC c) {
        super();
        npc = c;
    }

    @Override
    protected BTStatus update(float elapsedTime) {
        if (npc.getSize() < 4.0f) {  // assume big = 1.0f
            System.out.println("[getBig] Making NPC big");
            npc.setBig();
            return BTStatus.BH_SUCCESS;
        } else {
            return BTStatus.BH_FAILURE; // already big
        }
    }
}
