package cn.serendipityr.EndMinecraftPlusV2.AdvanceModule.MCForge;

import org.spacehq.packetlib.io.NetInput;

import java.io.IOException;

public class MCForgeUtils {
    public static int readVarShort(NetInput in) throws IOException {
        int low = in.readUnsignedShort();
        int high = 0;
        if ((low & 0x8000) != 0) {
            low = low & 0x7FFF;
            high = in.readUnsignedByte();
        }
        return ((high & 0xFF) << 15) | low;
    }

    public static UnknownPacket createUnknownPacket() {
        try {
            return UnknownPacket.class.newInstance();
        } catch (Exception e) {
            return null;
        }
    }
}
