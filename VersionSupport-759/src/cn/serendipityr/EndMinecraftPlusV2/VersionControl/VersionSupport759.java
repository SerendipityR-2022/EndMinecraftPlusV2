package cn.serendipityr.EndMinecraftPlusV2.VersionControl;

import com.github.steveice10.mc.protocol.packet.ingame.clientbound.ClientboundSystemChatPacket;
import com.github.steveice10.mc.protocol.packet.ingame.serverbound.ServerboundChatPacket;
import com.github.steveice10.packetlib.Session;
import com.github.steveice10.packetlib.packet.Packet;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;

import java.time.Instant;
import java.util.*;

public class VersionSupport759 {
    public static Map<String, String> clickVerifiesHandle(Packet packet, Session session, List<String> ClickVerifiesDetect, Component Message) {
        ClientboundSystemChatPacket chatPacket = (ClientboundSystemChatPacket) packet;

        Map<String, String> result = new HashMap<>();
        boolean needClick = false;
        Component message;

        if (Message != null) {
            message = Message;
        } else {
            message = chatPacket.getContent();
        }

        String simpleMsg = PlainTextComponentSerializer.plainText().serialize(message);

        if (message.style().clickEvent() != null) {
            for (String clickVerifiesDetect:ClickVerifiesDetect) {
                if (simpleMsg.contains(clickVerifiesDetect)) {
                    needClick = true;
                    break;
                }
            }
        }

        if (needClick) {
            String msg = Objects.requireNonNull(message.style().clickEvent()).value();
            session.send(new ServerboundChatPacket(msg, Instant.now().toEpochMilli(), 0, new byte[0], false));
            result.put("result", "true");
            result.put("msg", msg);
            return result;
        }

        if (!message.children().isEmpty()) {
            for (Component extraMessage:message.children()) {
                return clickVerifiesHandle(null, session, ClickVerifiesDetect, extraMessage);
            }
        }

        result.put("result", "false");
        result.put("msg", simpleMsg);
        return result;
    }

    public static void sendChatPacket(Session session, String msg) {
        session.send(new ServerboundChatPacket(msg, Instant.now().toEpochMilli(), 0L, new byte[0], false));
    }

    public static boolean checkServerChatPacket(Packet packet) {
        try {
            Class.forName("com.github.steveice10.mc.protocol.packet.ingame.clientbound.ClientboundSystemChatPacket");
        } catch (ClassNotFoundException e) {
            return false;
        }

        return packet instanceof ClientboundSystemChatPacket;
    }
}
