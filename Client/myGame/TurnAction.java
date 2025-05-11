package myGame;

import tage.*;
import tage.input.action.AbstractInputAction;
import net.java.games.input.Event;
import org.joml.*;

public class TurnAction extends AbstractInputAction {
	private MyGame game;
	private float direction; // 1 for right turn, -1 for left turn

	public TurnAction(MyGame g, float dir) {
		game = g;
		direction = dir;
	}

	@Override
	public void performAction(float time, Event e) {
		float keyValue = e.getValue();
		if (java.lang.Math.abs(keyValue) < 0.2f) return; // deadzone

		GameObject av = game.getAvatar();
		Matrix4f oldRotation = new Matrix4f(av.getWorldRotation());
		Vector4f oldUp = new Vector4f(0f, 1f, 0f, 0f).mul(oldRotation);
		Vector3f up = new Vector3f(oldUp.x(), oldUp.y(), oldUp.z());

		float angle = -0.01f * keyValue * direction; // multiply by direction flag
		Matrix4f rotAroundUp = new Matrix4f().rotation(angle, up);
		Matrix4f newRotation = new Matrix4f(oldRotation).mul(rotAroundUp);
		av.setLocalRotation(newRotation);
	}
}



