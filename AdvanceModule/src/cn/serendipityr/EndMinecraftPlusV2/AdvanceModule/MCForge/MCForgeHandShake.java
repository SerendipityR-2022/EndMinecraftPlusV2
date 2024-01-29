package cn.serendipityr.EndMinecraftPlusV2.AdvanceModule.MCForge;

import org.spacehq.packetlib.packet.Packet;

public abstract class MCForgeHandShake {
    protected MCForge forge;

    public MCForgeHandShake(MCForge forge) {
        this.forge = forge;
    }

    public abstract void handle(Packet recvPacket);
    public abstract String getFMLVersion();
}
