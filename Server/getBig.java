import tage.ai.behaviortrees.BTAction;

public class getBig extends BTAction {
    private NPCcontroller ctrl;

    public getBig(NPCcontroller controller) {
        super();
        this.ctrl = controller;
    }

    @Override
    public void run() {
        // set size big:
        ctrl.getNPC().setSize(2.0);
        setSucceeded();
    }
}
