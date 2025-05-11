package myGame;

import tage.*;
import tage.input.action.AbstractInputAction;
import net.java.games.input.Event;
import org.joml.*;

public class MoveAction extends AbstractInputAction
{
	private MyGame game;
	private GameObject av;
	private Vector3f oldPosition, newPosition;
	private Vector4f fwdDirection;
	private ProtocolClient protClient;
	float speed;

	public MoveAction(MyGame g, ProtocolClient p, float s)
	{	game = g;
		protClient = p;
		speed = s;
	}

	@Override
	public void performAction(float time, Event e)
	{	av = game.getAvatar();
		oldPosition = av.getWorldLocation();
		fwdDirection = new Vector4f(0f,0f,1f,1f);
		fwdDirection.mul(av.getWorldRotation());
		fwdDirection.mul(speed);
		newPosition = oldPosition.add(fwdDirection.x(), fwdDirection.y(), fwdDirection.z());
		av.setLocalLocation(newPosition);
		protClient.sendMoveMessage(av.getWorldLocation());
	}
}


