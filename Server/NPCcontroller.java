import java.util.Random;
import java.util.UUID;
import tage.ai.behaviortrees.BTCompositeType;
import tage.ai.behaviortrees.BTSequence;
import tage.ai.behaviortrees.BehaviorTree;
import tage.rml.Vector3f;

public class NPCcontroller {
    // expose underlying NPC for actions
    public NPC getNPC() { return npc; }
    private NPC npc;
    private UUID npcID;
    private Random rn = new Random();
    private BehaviorTree bt;
    private boolean nearFlag = false;
    private long lastThinkUpdateTime;
    private long lastTickUpdateTime;
    private GameAIServerUDP server;

    public NPCcontroller() {
        bt = new BehaviorTree(BTCompositeType.SELECTOR);
    }

    public void start(GameAIServerUDP s) {
        server = s;
        long now = System.nanoTime();
        lastThinkUpdateTime = now;
        lastTickUpdateTime  = now;
        setupNPCs();
        setupBehaviorTree();
        npcLoop();
    }

    private void setupNPCs() {
        npc = new NPC();
        npcID = UUID.randomUUID();
        npc.randomizeLocation(rn.nextInt(40), rn.nextInt(40));
    }

    private void setupBehaviorTree() {
        bt.insertAtRoot(new BTSequence(10));
        bt.insertAtRoot(new BTSequence(20));
        bt.insert(10, new OneSecPassed(this));
        bt.insert(10, new getSmall(this));
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

    // Exposed for networking
    public UUID getNpcID() { return npcID; }
    public Vector3f getPosition() { return npc.getPosition(); }
    public double getSize()     { return npc.getSize(); }
    public boolean getNearFlag(){ return nearFlag; }
    public void setNearFlag(boolean flag) { this.nearFlag = flag; }
}