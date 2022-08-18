package cn.serendipityr.EndMinecraftPlusV2.VersionControl.OldVersion.ForgeProtocol;

import cn.serendipityr.EndMinecraftPlusV2.Tools.LogUtil;
import com.google.gson.Gson;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class MCForgeMOTD {
    public Map<String, String> pingGetModsList(String ip, int port, int version) {
        Map<String, String> modList = new HashMap<String, String>();
        Socket socket = new Socket();
        try {
            socket.connect(new InetSocketAddress(ip, port));
            if(socket.isConnected()) {
                DataOutputStream out = new DataOutputStream(socket.getOutputStream());

                ByteArrayOutputStream packet = new ByteArrayOutputStream();
                DataOutputStream packetOut = new DataOutputStream(packet);

                packetOut.writeByte(0); // handshake packet id
                writeVarInt(packetOut, version); // version
                writeVarInt(packetOut, ip.length()); // host length
                packetOut.writeBytes(ip); // host
                packetOut.writeShort(port); // port
                writeVarInt(packetOut, 1); // next to status

                writeVarInt(out, packet.size()); // packet length
                out.write(packet.toByteArray()); // handshake packet

                out.writeByte(1); // packet length
                out.writeByte(0); // status packet id

                out.flush();

                DataInputStream in = new DataInputStream(socket.getInputStream());
                readVarInt(in); // packet length
                int packetID = readVarInt(in);
                int dataLength = readVarInt(in);

                if (packetID == 0 && dataLength > 0) { // Response
                    byte[] responseData = new byte[dataLength];
                    in.readFully(responseData);
                    Response response = new Gson().fromJson(new String(responseData), Response.class);
                    if (response.modinfo != null) {
                        for (Response.ModInfo.ModID modid : response.modinfo.modList) {
                            modList.put(modid.modid, modid.version);
                        }
                    }
                }
            }
        } catch (Exception e) {

            LogUtil.doLog(1, "获取服务器上的Forge Mods时发生错误。详细信息: " + e.getMessage(), null);

            try {
                if (socket.isConnected())
                    socket.close();
            } catch (IOException ignored) {}
        }
        return modList;
    }

    /**
     * @author thinkofdeath
     * See: https://gist.github.com/thinkofdeath/e975ddee04e9c87faf22
     */
    public int readVarInt(DataInputStream in) throws IOException {
        int i = 0;
        int j = 0;
        while (true) {
            int k = in.readByte();

            i |= (k & 0x7F) << j++ * 7;

            if (j > 5)
                throw new RuntimeException("VarInt too big");

            if ((k & 0x80) != 128)
                break;
        }

        return i;
    }

    /**
     * @throws IOException
     * @author thinkofdeath
     * See: https://gist.github.com/thinkofdeath/e975ddee04e9c87faf22
     */
    public void writeVarInt(DataOutputStream out, int paramInt) throws IOException {
        while (true) {
            if ((paramInt & 0xFFFFFF80) == 0) {
                out.writeByte(paramInt);
                return;
            }

            out.writeByte(paramInt & 0x7F | 0x80);
            paramInt >>>= 7;
        }
    }

    class Response {
        public Object description;
        public Players players;
        public Version version;
        public ModInfo modinfo;

        class Description {
            public String text;
            public String translate;

            public String getDescription() {
                return text != null ? text : translate;
            }
        }

        class Players {
            public int max;
            public int online;
        }

        class Version {
            public String name;
            public int protocol;
        }

        class ModInfo {
            public String type;
            public ModID[] modList;

            class ModID {
                public String modid;
                public String version;
            }
        }
    }
}
