package cn.serendipityr.EndMinecraftPlusV2.VersionControl;

import com.github.steveice10.mc.protocol.data.message.Message;
import com.github.steveice10.mc.protocol.packet.ingame.client.ClientChatPacket;
import com.github.steveice10.packetlib.ProxyInfo;
import com.github.steveice10.packetlib.Session;
import com.github.steveice10.packetlib.tcp.TcpSessionFactory;

import java.util.List;

public class VersionSupport578 {
    public TcpSessionFactory createTcpSessionFactory(ProxyInfo proxyInfo) {
        return new TcpSessionFactory(proxyInfo);
    }

    public static boolean clickVerifiesHandle(Message message, Session session, List<String> ClickVerifiesDetect) {
        boolean needClick = false;

        if (message.getStyle().getClickEvent() != null) {
            for (String clickVerifiesDetect:ClickVerifiesDetect) {
                if (message.toString().contains(clickVerifiesDetect)) {
                    needClick = true;
                    break;
                }
            }
        }

        if (needClick) {
            session.send(new ClientChatPacket(message.getStyle().getClickEvent().getValue()));
            return true;
        }

        if (message.getExtra() != null && !message.getExtra().isEmpty()) {
            for (Message extraMessage:message.getExtra()) {
                clickVerifiesHandle(extraMessage, session, ClickVerifiesDetect);
            }
        }

        return false;
    }
}
