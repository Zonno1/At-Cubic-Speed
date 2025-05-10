package mygame.ai;

import java.util.Random;

import tage.ai.behaviortrees.BTCompositeType;
import tage.ai.behaviortrees.BTSequence;
import tage.ai.behaviortrees.BehaviorTree;
import java.util.UUID;
import org.joml.Vector3f;
import mygame.behaviortrees.OneSecPassed;
import mygame.behaviortrees.getSmall;
import mygame.behaviortrees.getBig;

public class NPCcontroller
{
    private NPC npc;
    Random rn = new Random();
    BehaviorTree bt = new BehaviorTree(BTCompositeType.SELECTOR);
    boolean nearFlag = false;
    long thinkStartTime, tickStartTime;
    GameAIServerUDP server;
    double criteria = 2.0;

    private UUID npcID;
    private Vector3f position;
    private double size;

    // Tick & think timestamps
    private long lastThinkUpdateTime;
    private long lastTickUpdateTime;



    public void updateNPCs()
    {
        npc.updateLocation();
    }

    public void start (GameAIServerUDP s)
    {
        thinkStartTime = System.nanoTime();
        tickStartTime = System.nanoTime();
        lastThinkUpdateTime = thinkStartTime;
        lastTickUpdateTime = tickStartTime;
        server =s;
        setupNPCs();
        setupBehaviorTree();
        npcLoop();

    }

    public void setupNPCs()
    {
        npc = new NPC();
        npc.randomizeLocation(rn.nextInt(40), rn.nextInt(40));
    }

    public void npcLoop()
    {
        while (true)
        {
            long currentTime = System.nanoTime();
            float elapsedThinkMilliSecs =(currentTime-lastThinkUpdateTime)/(100000.0f);
            float elapsedTickMilliSecs = (currentTime-lastTickUpdateTime)/(1000000.0);

            if (elapsedTickMilliSecs >= 25.0f)
            { 
                lastTickUpdateTime = currentTime;
                npc.updateLocation();
                server.sendNPCinfo();
            }
            if (elapsedThinkMilliSecs >= 250.0f)
            {
                lastThinkUpdateTime = currentTime;
                bt.update(elapsedThinkMilliSecs);
            }
            Thread.yield();
        }
    }

    public void setupBehaviorTree()
    {
        bt.insertAtRoot(new BTSequence(10));
        bt.insertAtRoot(new BTSequence(20));
        bt.insert(10, new OneSecPassed(this,npc,false));
        bt.insert(10, new getSmall(npc));
        bt.insert(20,new AvatarNear(server,this,npc,false));
        bt.insert(20, new getBig(npc));
        
    }

    public boolean getNearFlag() {
        return this.nearFlag;
    }
    public void setNearFlag(boolean flag) {
        this.nearFlag = flag;
    }

    public UUID getNpcID() { return npcID; }
    public Vector3f getPosition() { return position; }
    public double getSize() { return size; }
}