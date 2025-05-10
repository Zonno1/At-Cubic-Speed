import tage.ai.behaviortrees.BTAction;
import tage.ai.behaviortrees.BTStatus;

public class getBig extends BTAction {
    private NPCcontroller ctrl;

    public getBig(NPCcontroller controller) {
        super();
        this.ctrl = controller;
    }

    @Override
    public BTStatus update(float elapsedTime) {
        // set NPC size big
        ctrl.getNPC().setSize(2.0);
        return BTStatus.BH_SUCCESS;
    }
}
