package cn.serendipityr.EndMinecraftPlusV2.VersionControl;

import com.github.steveice10.mc.protocol.MinecraftProtocol;
import com.github.steveice10.mc.protocol.packet.ingame.client.ClientChatPacket;
import com.github.steveice10.mc.protocol.packet.ingame.server.ServerChatPacket;
import com.github.steveice10.packetlib.ProxyInfo;
import com.github.steveice10.packetlib.Session;
import com.github.steveice10.packetlib.tcp.TcpClientSession;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;

import java.util.*;

public class VersionSupport754 {
    public static Session getSession(String ip, Integer port, String username, ProxyInfo proxyInfo) {
        return new TcpClientSession(ip, port, new MinecraftProtocol(username), proxyInfo);
    }

    public static Map<String, String> clickVerifiesHandle(ServerChatPacket packet, Session session, List<String> ClickVerifiesDetect, Component Message) {
        Map<String, String> result = new HashMap<>();
        boolean needClick = false;
        Component message;

        if (Message != null) {
            message = Message;
        } else {
            message = packet.getMessage();
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
            session.send(new ClientChatPacket(msg));
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
        return result;
    }
}
