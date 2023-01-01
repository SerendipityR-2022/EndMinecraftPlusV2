package cn.serendipityr.EndMinecraftPlusV2.VersionControl;

import org.spacehq.mc.protocol.data.game.ClientRequest;
import org.spacehq.mc.protocol.packet.ingame.client.ClientKeepAlivePacket;
import org.spacehq.mc.protocol.packet.ingame.client.ClientRequestPacket;
import org.spacehq.mc.protocol.packet.ingame.server.ServerKeepAlivePacket;
import org.spacehq.packetlib.Session;

public class VersionSupport107 {
    public static void sendRespawnPacket(Session session) {
        ClientRequestPacket clientRequestPacket = new ClientRequestPacket(ClientRequest.RESPAWN);

        session.send(clientRequestPacket);
    }

    public static void sendKeepAlivePacket(Session session, ServerKeepAlivePacket packet) {
        ClientKeepAlivePacket clientKeepAlivePacket = new ClientKeepAlivePacket(packet.getPingId());

        session.send(clientKeepAlivePacket);
    }
}
