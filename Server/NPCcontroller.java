
import java.util.Random;
import java.util.UUID;
import tage.ai.behaviortrees.BTCompositeType;
import tage.ai.behaviortrees.BTSequence;
import tage.ai.behaviortrees.BehaviorTree;
import tage.rml.Vector3f;

public class NPCcontroller {
    private NPC npc;
    private Random rn = new Random();
    private BehaviorTree bt;
    private boolean nearFlag = false;
    private long lastThinkUpdateTime;
    private long lastTickUpdateTime;
    private GameAIServerUDP server;

    public NPCcontroller() {
        // Initialize behavior tree with selector root
        bt = new BehaviorTree(BTCompositeType.SELECTOR);
    }

    public void start(GameAIServerUDP s) {
        server = s;
        // Initialize timing
        long now = System.nanoTime();
        lastThinkUpdateTime = now;
        lastTickUpdateTime  = now;
        // Create NPC and behavior tree tasks
        setupNPCs();
        setupBehaviorTree();
        // Begin main loop
        npcLoop();
    }

    private void setupNPCs() {
        npc = new NPC();
        npc.randomizeLocation(rn.nextInt(40), rn.nextInt(40));
    }

    private void setupBehaviorTree() {
        // Two parallel sequences under root selector
        bt.insertAtRoot(new BTSequence(10));
        bt.insertAtRoot(new BTSequence(20));
        // Tasks for first sequence
        bt.insert(10, new OneSecPassed(this));
        bt.insert(10, new getSmall(this));
        // Tasks for second sequence
        bt.insert(20, new AvatarNear(this));
        bt.insert(20, new getBig(this));
    }

    private void npcLoop() {
        while (true) {
            long current = System.nanoTime();
            float elapsedTickMs   = (current - lastTickUpdateTime) / 1_000_000.0f;
            float elapsedThinkMs  = (current - lastThinkUpdateTime) / 1_000_000.0f;

            if (elapsedTickMs >= 25.0f) {
                lastTickUpdateTime = current;
                npc.updateLocation();
                server.sendNPCinfo();
            }
            if (elapsedThinkMs >= 250.0f) {
                lastThinkUpdateTime = current;
                bt.update(elapsedThinkMs);
            }
            Thread.yield();
        }
    }

    // Near-flag accessors
    public boolean getNearFlag() { return nearFlag; }
    public void setNearFlag(boolean flag) { this.nearFlag = flag; }

    // Expose NPC info
    public UUID getNpcID() { return npc.getId(); }
    public Vector3f getPosition() { return npc.getPosition(); }
    public double getSize() { return npc.getSize(); }
}
