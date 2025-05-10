package myGame;

import tage.GameObject;
import tage.ObjShape;
import tage.TextureImage;
import org.joml.Vector3f;

import java.util.UUID;

import org.joml.Matrix4f;

public class GhostNPC extends GameObject 
{
    public GhostNPC(UUID id, ObjShape shape, TextureImage tex, Vector3f p) {
        super(GameObject.root(), shape, tex);
        setLocalTranslation(new Matrix4f().translation(p));
    }

    public void setSize(float s) {
       setLocalScale(new Matrix4f().scaling(s));
     }

}
