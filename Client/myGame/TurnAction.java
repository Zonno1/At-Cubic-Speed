// TurnAction.java
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
