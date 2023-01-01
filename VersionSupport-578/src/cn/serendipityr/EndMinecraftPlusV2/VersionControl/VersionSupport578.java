package cn.serendipityr.EndMinecraftPlusV2.VersionControl;

import com.github.steveice10.mc.protocol.data.message.Message;
import com.github.steveice10.mc.protocol.packet.ingame.client.ClientChatPacket;
import com.github.steveice10.mc.protocol.packet.ingame.server.ServerKeepAlivePacket;
import com.github.steveice10.packetlib.ProxyInfo;
import com.github.steveice10.packetlib.Session;
import com.github.steveice10.packetlib.tcp.TcpSessionFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class VersionSupport578 {
    public TcpSessionFactory createTcpSessionFactory(ProxyInfo proxyInfo) {
        return new TcpSessionFactory(proxyInfo);
    }

    public static Map<String, String> clickVerifiesHandle(Message message, Session session, List<String> ClickVerifiesDetect) {
        Map<String, String> result = new HashMap<>();
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
            String msg = message.getStyle().getClickEvent().getValue();
            session.send(new ClientChatPacket(msg));
            result.put("result", "true");
            result.put("msg", msg);
            return result;
        }

        if (message.getExtra() != null && !message.getExtra().isEmpty()) {
            for (Message extraMessage:message.getExtra()) {
                return clickVerifiesHandle(extraMessage, session, ClickVerifiesDetect);
            }
        }

        result.put("result", "false");
        return result;
    }
}
