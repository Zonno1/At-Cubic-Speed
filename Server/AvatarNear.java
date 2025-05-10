
import tage.ai.behaviortrees.BTCondition;


// Ensure this class extends BTCondition and implements the abstract check() method
public class AvatarNear extends BTCondition {
    private NPCcontroller npcc;

    public AvatarNear(NPCcontroller npcController) {
        super(false);                     // call BTCondition constructor
        this.npcc = npcController;
    }

    @Override
    public boolean check() {
        // return true when the avatar is near
        return npcc.getNearFlag();
    }
}

