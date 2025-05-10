import tage.ai.behaviortrees.BTCondition;

public class OneSecPassed extends BTCondition {
    private NPCcontroller ctrl;
    private long startTime;

    public OneSecPassed(NPCcontroller controller) {
        super(false);
        this.ctrl = controller;
        this.startTime = System.nanoTime();
    }

    @Override
    public boolean check() {
        long now = System.nanoTime();
        float secs = (now - startTime) / 1_000_000_000.0f;
        return secs >= 1.0f;
    }
}