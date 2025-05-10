
package myGame;

import tage.GameObject;
import tage.ObjShape;
import tage.TextureImage;
import tage.rml.Vector3f;

import java.util.UUID;

public class GhostAvatar extends GameObject {
    UUID uuid;

    public GhostAvatar(UUID id, ObjShape s, TextureImage t, org.joml.Vector3f p) {
        super(GameObject.root(), s, t);
        uuid = id;
        setPosition(p);
    }

    public UUID getID() { return uuid; }

public void setPosition(org.joml.Vector3f p) {
    setLocalLocation(new org.joml.Vector3f(p));  // Safe defensive copy (optional)
}

    public org.joml.Vector3f getPosition() {
        return getWorldLocation();
    }
}
