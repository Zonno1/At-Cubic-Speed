package myGame;

import java.io.IOException;
import java.net.InetAddress;
import java.util.UUID;

import tage.*;
import tage.networking.client.GameConnectionClient;
import tage.rml.Vector3f;

public class ProtocolClient extends GameConnectionClient {
	private MyGame game;
	private GhostManager ghostManager;
	private UUID id;

	private GhostNPC ghostNPC;

	public ProtocolClient(InetAddress remoteAddr, int remotePort, ProtocolType protocolType, MyGame game)
			throws IOException {
		super(remoteAddr, remotePort, protocolType);
		this.game = game;
		this.id = UUID.randomUUID();
		ghostManager = game.getGhostManager();
	}

	public UUID getID() {
		return id;
	}

	@Override
	protected void processPacket(Object message) {
		String strMessage = (String) message;
		System.out.println("message received -->" + strMessage);
		String[] messageTokens = strMessage.split(",");

		if (messageTokens.length > 0) {
			switch (messageTokens[0]) {
				case "join":
					if (messageTokens[1].equals("success")) {
						System.out.println("join success confirmed");
						game.setIsConnected(true);
						sendCreateMessage(game.getPlayerPosition());

						if (messageTokens[0].equals("join") && messageTokens[1].equals("success")) {
							System.out.println("Join success confirmed");
							game.setIsConnected(true);
							sendCreateMessage(game.getPlayerPosition());

							// ✅ Send NPC request after successful join
							try {
								sendPacket("needNPC," + id.toString());
								System.out.println("Sent needNPC message with client ID: " + id);
							} catch (IOException e) {
								e.printStackTrace();
							}
						}

					} else {
						System.out.println("join failure confirmed");
						game.setIsConnected(false);
					}
					break;

				case "bye":
					UUID ghostID = UUID.fromString(messageTokens[1]);
					ghostManager.removeGhostAvatar(ghostID);
					break;

				case "create":
				case "dsfr":
					UUID ghostID2 = UUID.fromString(messageTokens[1]);
					org.joml.Vector3f ghostPosition = new org.joml.Vector3f(
							Float.parseFloat(messageTokens[2]),
							Float.parseFloat(messageTokens[3]),
							Float.parseFloat(messageTokens[4]));
					try {
						ghostManager.createGhostAvatar(ghostID2, ghostPosition);
					} catch (IOException e) {
						System.out.println("error creating ghost avatar");
					}
					break;

				case "wsds":
					UUID ghostID3 = UUID.fromString(messageTokens[1]);
					sendDetailsForMessage(ghostID3, game.getPlayerPosition());
					break;

				case "move":
					UUID ghostID4 = UUID.fromString(messageTokens[1]);
					org.joml.Vector3f movePosition = new org.joml.Vector3f(
							Float.parseFloat(messageTokens[2]),
							Float.parseFloat(messageTokens[3]),
							Float.parseFloat(messageTokens[4]));
					ghostManager.updateGhostAvatar(ghostID4, movePosition);
					break;

				case "createNPC": {
					org.joml.Vector3f npcPos = new org.joml.Vector3f(
							Float.parseFloat(messageTokens[1]),
							Float.parseFloat(messageTokens[2]),
							Float.parseFloat(messageTokens[3]));
					System.out.println("Processing NPC creation with position: x=" 
    + npcPos.x() + ", y=" + npcPos.y() + ", z=" + npcPos.z());
					try {
						createGhostNPC(npcPos);
					} catch (IOException e) {
						System.out.println("error creating ghost npc");
					}
					break;
				}

				case "npcinfo": {
					org.joml.Vector3f npcPos = new org.joml.Vector3f(
							Float.parseFloat(messageTokens[1]),
							Float.parseFloat(messageTokens[2]),
							Float.parseFloat(messageTokens[3]));
					System.out.println("Processing current NPC position: " + npcPos);
					boolean big = Boolean.parseBoolean(messageTokens[4]);
					updateGhostNPC(npcPos, big);
					break;
				}
			}
		}
	}

	public void sendJoinMessage() {
		try {
			sendPacket("join," + id.toString());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void sendByeMessage() {
		try {
			sendPacket("bye," + id.toString());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void sendCreateMessage(org.joml.Vector3f position) {
		try {
			String message = "create," + id.toString() + "," +
					position.x() + "," + position.y() + "," + position.z();
			sendPacket(message);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void sendDetailsForMessage(UUID remoteId, org.joml.Vector3f position) {
		try {
			String message = "dsfr," + remoteId.toString() + "," + id.toString() + "," +
					position.x() + "," + position.y() + "," + position.z();
			sendPacket(message);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void sendMoveMessage(org.joml.Vector3f position) {
		try {
			String message = "move," + id.toString() + "," +
					position.x() + "," + position.y() + "," + position.z();
			sendPacket(message);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// ---- Ghost NPC Section ----

	private void createGhostNPC(org.joml.Vector3f position) throws IOException {
		if (ghostNPC == null) {
        ghostNPC = new GhostNPC(UUID.randomUUID(), game.getNPCShape(), game.getNPCtexture(), position);
        //game.getEngine().getSceneGraph().addGameObject(ghostNPC);
        System.out.println("GhostNPC created and added to scene");
	}
}

	private void updateGhostNPC(org.joml.Vector3f position, boolean big) {
		if (ghostNPC == null) {
			try {
				System.out.println("GhostNPC updated");
				createGhostNPC(position);
				
			} catch (IOException e) {
				System.out.println("error creating npc");
			}
		}
		ghostNPC.setPosition(position);
		float size = big ? 1.5f : 0.5f;
		ghostNPC.setSize(size);
	}

	public void sendIsNear() {
    try {
        sendPacket("isnear," + id.toString());
        System.out.println("Sent isnear message to server");
    } catch (IOException e) {
        e.printStackTrace();
    }
}

public GhostNPC getGhostNPC() {
    return ghostNPC;
}


}
