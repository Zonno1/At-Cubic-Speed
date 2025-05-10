import tage.ai.behaviortrees.BTCondition;

public class AvatarNear extends BTCondition {
    private NPCcontroller npcc;

    public AvatarNear(NPCcontroller npcController) {
        super(false);
        this.npcc = npcController;
    }

    @Override
    public boolean check() {
        return npcc.getNearFlag();
    }
}