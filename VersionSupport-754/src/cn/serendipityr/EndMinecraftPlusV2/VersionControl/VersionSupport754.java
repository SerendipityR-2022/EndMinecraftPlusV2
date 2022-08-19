package cn.serendipityr.EndMinecraftPlusV2.VersionControl;

import com.github.steveice10.mc.protocol.MinecraftProtocol;
import com.github.steveice10.mc.protocol.packet.ingame.client.ClientChatPacket;
import com.github.steveice10.mc.protocol.packet.ingame.server.ServerChatPacket;
import com.github.steveice10.packetlib.ProxyInfo;
import com.github.steveice10.packetlib.Session;
import com.github.steveice10.packetlib.tcp.TcpClientSession;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class VersionSupport754 {
    public static Session getSession(String ip, Integer port, String username, ProxyInfo proxyInfo) {
        return new TcpClientSession(ip, port, new MinecraftProtocol(username), proxyInfo);
    }

    public static List<String> clickVerifiesHandle(ServerChatPacket packet, Session session, List<String> ClickVerifiesDetect, Component Message) {
        List<String> result = new ArrayList<>();
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
            session.send(new ClientChatPacket(Objects.requireNonNull(message.style().clickEvent()).value()));
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
}
