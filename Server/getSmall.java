import tage.ai.behaviortrees.BTAction;

public class getSmall extends BTAction {
    private NPCcontroller ctrl;

    public getSmall(NPCcontroller controller) {
        super();
        this.ctrl = controller;
    }

    @Override
    public void run() {
        ctrl.getPosition().normalize(); // example small action
        // or set size small:
        ctrl.getNPC().setSize(0.5);
        setSucceeded();
    }
}