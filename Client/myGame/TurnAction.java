// TurnAction.java
package myGame;

import tage.*;
import tage.input.action.AbstractInputAction;
import net.java.games.input.Event;
import org.joml.*;
import tage.physics.PhysicsObject;

public class TurnAction extends AbstractInputAction {
    private MyGame game;
    private float turnRate;

    public TurnAction(MyGame g, float rate) {
        game = g;
        turnRate = rate; // degrees per second
    }

    @Override
    public void performAction(float time, Event e) {
        GameObject avatar = game.getAvatar();
        PhysicsObject phys = avatar.getPhysicsObject();
        if (phys == null) return;

        float angleRad = (float)java.lang.Math.toRadians(turnRate * time);
        Matrix4f oldRot = new Matrix4f(avatar.getLocalRotation());
        Matrix4f rot = new Matrix4f().rotateY(angleRad);
        Matrix4f newRot = oldRot.mul(rot);

        avatar.setLocalRotation(newRot);

        // update the physics object's rotation too
        Matrix4f trans = new Matrix4f(avatar.getLocalTranslation());
        Matrix4f fullTransform = new Matrix4f(newRot).mul(trans);
        phys.setTransform(game.toDoubleArray(fullTransform.get(game.vals)));
    }
}

/* 
package myGame;

import tage.input.action.AbstractInputAction;
import tage.physics.PhysicsObject;
import tage.physics.JBullet.JBulletPhysicsObject;
import com.bulletphysics.dynamics.RigidBody;
import javax.vecmath.Vector3f;
import net.java.games.input.Event;
import com.bulletphysics.collision.dispatch.CollisionObject;

public class TurnAction extends AbstractInputAction {
    private MyGame      game;
    private float       turnRate;

    public TurnAction(MyGame g, float rate) {
        game     = g;
        turnRate = rate;   // radians per second
    }
    @Override
    public void performAction(float t, Event e) {
        // fetch the physics body
 PhysicsObject pObj = game.getAvatar().getPhysicsObject();
   JBulletPhysicsObject jbObj = (JBulletPhysicsObject)pObj;
   RigidBody           rb    = jbObj.getRigidBody();
        // apply a torque around the world‑up (Y) axis
        // positive Y → turn right, negative → turn left
        rb.applyTorqueImpulse(new Vector3f(0, turnRate * t, 0));
    }
}
*/