package cn.serendipityr.EndMinecraftPlusV2.VersionControl.OldVersion.ForgeProtocol;

import org.spacehq.mc.protocol.packet.ingame.client.ClientPluginMessagePacket;
import org.spacehq.mc.protocol.packet.ingame.server.ServerPluginMessagePacket;
import org.spacehq.packetlib.Session;
import org.spacehq.packetlib.io.stream.StreamNetOutput;
import org.spacehq.packetlib.packet.Packet;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class MCForgeHandShakeV1 extends MCForgeHandShake {
    public MCForgeHandShakeV1(MCForge forge) {
        super(forge);
    }

    public void handle(Packet recvPacket) {
        ServerPluginMessagePacket packet = (ServerPluginMessagePacket) recvPacket;
        Session session = forge.session;

        byte[] data = packet.getData();
        int packetID = data[0];

        switch (packetID) {
        case 0: // Hello
            sendPluginMessage(session, packet.getChannel(), new byte[] { 0x01, 0x02 });

            // ModList
            ByteArrayOutputStream buf = new ByteArrayOutputStream();
            StreamNetOutput out = new StreamNetOutput(buf);
            try {
                out.writeVarInt(2);
                out.writeByte(forge.modList.size());
                forge.modList.forEach((k, v) -> {
                    try {
                        out.writeString(k);
                        out.writeString(v);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
            } catch (IOException e) {
                e.printStackTrace();
            }
            sendPluginMessage(session, packet.getChannel(), buf.toByteArray());
            break;
        case 2: // ModList
            sendPluginMessage(session, packet.getChannel(), new byte[] { -0x1, 0x02 }); // ACK(WAITING SERVER DATA)
            break;
        case 3: // RegistryData
            sendPluginMessage(session, packet.getChannel(), new byte[] { -0x1, 0x03 }); // ACK(WAITING SERVER COMPLETE)
            break;
        case -1: // HandshakeAck
            int ackID = data[1];
            switch (ackID) {
            case 2: // WAITING CACK
                sendPluginMessage(session, packet.getChannel(), new byte[] { -0x1, 0x04 }); // PENDING COMPLETE
                break;
            case 3: // COMPLETE
                sendPluginMessage(session, packet.getChannel(), new byte[] { -0x1, 0x05 }); // COMPLETE
                break;
            default:
            }
        default:
        }
    }

    public String getFMLVersion() {
        return "FML";
    }

    private void sendPluginMessage(Session session, String channel, byte[] data) {
        session.send(new ClientPluginMessagePacket(channel, data));
    }
}
