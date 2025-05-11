package myGame;

import tage.*;
import tage.input.action.AbstractInputAction;
import net.java.games.input.Event;
import org.joml.*;
import tage.physics.PhysicsObject;

public class MoveAction extends AbstractInputAction {
    private MyGame game;
    private ProtocolClient protClient;
    private float speed;

    public MoveAction(MyGame g, ProtocolClient p, float s) {
        game = g;
        protClient = p;
        speed = s;
    }

    @Override
    public void performAction(float time, Event e) {
        GameObject av = game.getAvatar();
        PhysicsObject avPhys = av.getPhysicsObject();
        if (avPhys == null) return;

        // Get the avatar's forward direction
        Vector4f fwd = new Vector4f(0, 0, 1, 0).mul(av.getWorldRotation()).normalize();
        float[] velocity = new float[] {
            fwd.x() * speed,
            fwd.y() * speed,
            fwd.z() * speed
        };

        avPhys.setLinearVelocity(velocity);

        // Still notify others via networking
        protClient.sendMoveMessage(av.getWorldLocation());
    }
}


























/*
package myGame;
import tage.*;
import tage.input.action.AbstractInputAction;
import tage.physics.PhysicsObject;
import tage.physics.JBullet.JBulletPhysicsObject;
import net.java.games.input.Event;
import javax.vecmath.Vector3f;
import org.joml.Vector4f;
import com.bulletphysics.collision.dispatch.CollisionObject;
import com.bulletphysics.dynamics.RigidBody;

public class MoveAction extends AbstractInputAction {
    private MyGame game;
    private ProtocolClient protClient;
    private float speed;

    public MoveAction(MyGame g, ProtocolClient p, float s) {
        game       = g;
        protClient = p;
        speed      = s;
    }

    @Override
    public void performAction(float time, Event e) {
		System.out.println("W was pressed");
        GameObject av = game.getAvatar();

        // build a world‑space impulse vector
        Vector4f fwd = new Vector4f(0, 0, 1, 0)
            .mul(av.getWorldRotation())
            .mul(speed * time);

        // grab the JBullet rigid body
		PhysicsObject pObj = av.getPhysicsObject();
		JBulletPhysicsObject jbObj = (JBulletPhysicsObject)pObj;
		// use the wrapper’s getter
		RigidBody rb = jbObj.getRigidBody();

        // apply a forward impulse
        rb.applyCentralImpulse(new Vector3f(fwd.x(), fwd.y(), fwd.z()));

        // network‑sync your new position (graphics node will be updated in update())
        protClient.sendMoveMessage(av.getWorldLocation());
    }
}
*/