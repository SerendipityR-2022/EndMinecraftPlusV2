package cn.serendipityr.EndMinecraftPlusV2.VersionControl;

import com.github.steveice10.mc.protocol.packet.ingame.clientbound.ClientboundSystemChatPacket;
import com.github.steveice10.mc.protocol.packet.ingame.serverbound.ServerboundChatPacket;
import com.github.steveice10.packetlib.Session;
import com.github.steveice10.packetlib.packet.Packet;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class VersionSupport760 {
    public static List<String> clickVerifiesHandle(Packet packet, Session session, List<String> ClickVerifiesDetect, Component Message) {
        ClientboundSystemChatPacket chatPacket = (ClientboundSystemChatPacket) packet;

        List<String> result = new ArrayList<>();
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
            session.send(new ServerboundChatPacket(Objects.requireNonNull(message.style().clickEvent()).value(), Instant.now().toEpochMilli(), 0, new byte[0], false, new ArrayList<>(), null));
            result.add("1");
            result.add(simpleMsg);
            result.add(Objects.requireNonNull(message.style().clickEvent()).value());
            return result;
        }

        if (!message.children().isEmpty()) {
            for (Component extraMessage:message.children()) {
                clickVerifiesHandle(null, session, ClickVerifiesDetect, extraMessage);
            }
        }

        result.add("0");
        result.add(simpleMsg);
        return result;
    }

    public static void sendChatPacket(Session session, String msg) {
        session.send(new ServerboundChatPacket(msg, Instant.now().toEpochMilli(), 0L, new byte[0], false, new ArrayList<>(), null));
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
