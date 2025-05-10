package myGame;

import java.awt.Color;
import java.io.IOException;
import java.net.InetAddress;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;
import java.util.Vector;

import org.joml.Matrix4f;
import org.joml.Vector3f; // JOML Vector3f

import tage.*;
import tage.rml.*; // TAGE Vector3f (conflicts with org.joml.Vector3f)

public class GhostManager
{
	private MyGame game;
	private Vector<GhostAvatar> ghostAvatars = new Vector<GhostAvatar>();

	public GhostManager(VariableFrameRateGame vfrg)
	{
		game = (MyGame)vfrg;
	}

	public void createGhostAvatar(UUID id, Vector3f position) throws IOException
	{
		System.out.println("adding ghost with ID --> " + id);
		ObjShape s = game.getGhostShape();
		TextureImage t = game.getGhostTexture();
		GhostAvatar newAvatar = new GhostAvatar(id, s, t, position);  // JOML Vector3f
		org.joml.Matrix4f initialScale = new org.joml.Matrix4f().scaling(0.25f);
		newAvatar.setLocalScale(initialScale);
		ghostAvatars.add(newAvatar);
	}

	public void removeGhostAvatar(UUID id)
	{
		GhostAvatar ghostAvatar = findAvatar(id);
		if (ghostAvatar != null)
		{
			game.getEngine().getSceneGraph().removeGameObject(ghostAvatar);
			ghostAvatars.remove(ghostAvatar);
		}
		else
		{
			System.out.println("tried to remove, but unable to find ghost in list");
		}
	}

	private GhostAvatar findAvatar(UUID id)
	{
		Iterator<GhostAvatar> it = ghostAvatars.iterator();
		while (it.hasNext())
		{
			GhostAvatar ghostAvatar = it.next();
			if (ghostAvatar.getID().compareTo(id) == 0)
			{
				return ghostAvatar;
			}
		}
		return null;
	}

	public void updateGhostAvatar(UUID id, Vector3f position) // JOML
	{
		GhostAvatar ghostAvatar = findAvatar(id);
		if (ghostAvatar != null)
		{
			ghostAvatar.setPosition(position);
		}
		else
		{
			System.out.println("tried to update ghost avatar position, but unable to find ghost in list");
		}
	}

	private Map<UUID, GhostNPC> ghostNPCs = new HashMap<>();

	public void createGhostNPC(UUID id, Vector3f pos) // JOML
	{
		GhostNPC npc = new GhostNPC(id, game.getNPCshape(), game.getNPCtexture(), pos); // constructor handles conversion
		ghostNPCs.put(id, npc);
	}

	public void updateGhostNPC(UUID id, Vector3f pos, double size) // JOML
	{
		GhostNPC npc = ghostNPCs.get(id);
		if (npc != null) {
			npc.setLocalTranslation(new org.joml.Matrix4f().translation(pos));
			npc.setSize((float)size);
		}
	}
}
