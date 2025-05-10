import tage.ai.behaviortrees.BTAction;
import tage.ai.behaviortrees.BTStatus;

public class getSmall extends BTAction {
    private NPCcontroller ctrl;

    public getSmall(NPCcontroller controller) {
        super();
        this.ctrl = controller;
    }

    @Override
    public BTStatus update(float elapsedTime) {
        // set NPC size small
        ctrl.getNPC().setSize(0.5);
        return BTStatus.BH_SUCCESS;
    }
}