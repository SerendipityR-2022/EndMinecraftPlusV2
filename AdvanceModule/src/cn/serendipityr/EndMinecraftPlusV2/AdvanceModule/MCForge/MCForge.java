package cn.serendipityr.EndMinecraftPlusV2.AdvanceModule.MCForge;

import org.spacehq.mc.protocol.packet.ingame.client.ClientPluginMessagePacket;
import org.spacehq.mc.protocol.packet.ingame.server.ServerPluginMessagePacket;
import org.spacehq.packetlib.Session;
import org.spacehq.packetlib.event.session.*;

import java.lang.reflect.Field;
import java.util.Map;

public class MCForge {
    private final MCForgeHandShake handshake;

    public Map<String, String> modList;
    public Session session;

    public MCForge(Session session, Map<String, String> modList) {
        this.modList = modList;
        this.session = session;
        this.handshake = isAfterVersion1_13() ? new MCForgeHandShakeV2(this) : new MCForgeHandShakeV1(this);
    }

    public void init() {
        this.session.addListener(new SessionListener() {
            public void packetReceived(PacketReceivedEvent e) {
                if (e.getPacket() instanceof ServerPluginMessagePacket) {
                    handle(e.getPacket());
                } else if (e.getPacket().getClass().getSimpleName().equals("LoginPluginRequestPacket")) {
                    handshake.handle(e.getPacket());
                }
            }

            public void packetSent(PacketSentEvent e) {
            }

            public void connected(ConnectedEvent e) {
                modifyHost();
            }

            public void disconnecting(DisconnectingEvent e) {
            }

            public void disconnected(DisconnectedEvent e) {
            }
        });
    }

    public void handle(ServerPluginMessagePacket packet) {
        switch (packet.getChannel()) {
            case "FML|HS":
                this.handshake.handle(packet);
                break;
            case "REGISTER":
            case "minecraft:register": // 1.13
                this.session.send(new ClientPluginMessagePacket(packet.getChannel(), packet.getData()));
                break;
            case "MC|Brand":
            case "minecraft:brand": // 1.13
                this.session.send(new ClientPluginMessagePacket(packet.getChannel(), "fml,forge".getBytes()));
                break;
        }
    }

    public void modifyHost() {
        try {
            Class<?> cls = this.session.getClass().getSuperclass();

            Field field = cls.getDeclaredField("host");
            field.setAccessible(true);

            field.set(this.session, this.session.getHost() + "\0" + handshake.getFMLVersion() + "\0");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static boolean isVersion1710() {
        return (getProtocolVersion() == 5);
    }

    public static boolean isAfterVersion1_13() {
        return (getProtocolVersion() >= 393);
    }

    public static int getProtocolVersion() {
        try {
            Class<?> cls;
            try {
                cls = Class.forName("org.spacehq.mc.protocol.ProtocolConstants");
            } catch (ClassNotFoundException e) {
                cls = Class.forName("org.spacehq.mc.protocol.MinecraftConstants");
            }

            Field field = cls.getDeclaredField("PROTOCOL_VERSION");
            return field.getInt(null);
        } catch (Exception e) {
            return 0;
        }
    }
}
