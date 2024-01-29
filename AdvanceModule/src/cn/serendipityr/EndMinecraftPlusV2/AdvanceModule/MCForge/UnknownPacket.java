package cn.serendipityr.EndMinecraftPlusV2.AdvanceModule.MCForge;

import org.spacehq.packetlib.io.NetInput;
import org.spacehq.packetlib.io.NetOutput;
import org.spacehq.packetlib.packet.Packet;

import java.io.IOException;

public class UnknownPacket implements Packet {
    public boolean isPriority() {
        return false;
    }

    public void read(NetInput in) throws IOException {
        in.readBytes(in.available());
    }

    public void write(NetOutput out) {
    }
}
