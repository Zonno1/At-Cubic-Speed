import tage.ai.behaviortrees.BTCondition;

public class OneSecPassed extends BTCondition {
    private NPCcontroller npcCtrl;
    private NPC npc;
    private boolean negated;
    private long lastTriggerTime;

    public OneSecPassed(NPCcontroller ctrl, NPC n, boolean neg) {
        super(neg);
        this.npcCtrl = ctrl;
        this.npc = n;
        this.negated = neg;
        this.lastTriggerTime = System.nanoTime();
    }

    @Override
    protected boolean check() {
        long currentTime = System.nanoTime();
        float elapsedMilliseconds = (currentTime - lastTriggerTime) / 1_000_000.0f;

        if (elapsedMilliseconds >= 1000.0f) {
            lastTriggerTime = currentTime;
            System.out.println("[OneSecPassed] 1 second passed, returning true");
            return true;
        }

        return false;
    }
}
