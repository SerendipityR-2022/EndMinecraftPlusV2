package cn.serendipityr.EndMinecraftPlusV2.VersionControl.NewVersion.ForgeProtocol;

import com.github.steveice10.mc.protocol.packet.login.client.LoginPluginResponsePacket;
import com.github.steveice10.mc.protocol.packet.login.clientbound.ClientboundCustomQueryPacket;
import com.github.steveice10.mc.protocol.packet.login.server.LoginPluginRequestPacket;
import com.github.steveice10.mc.protocol.packet.login.serverbound.ServerboundCustomQueryPacket;
import com.github.steveice10.packetlib.io.buffer.ByteBufferNetInput;
import com.github.steveice10.packetlib.io.stream.StreamNetOutput;
import com.github.steveice10.packetlib.packet.Packet;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MCForgeHandShakeV2 extends MCForgeHandShake {
    private final int Packet_S2CModList = 1;
    private final int Packet_C2SModListReply = 2;
    private final int Packet_S2CRegistry = 3;
    private final int Packet_S2CConfigData = 4;
    private final int Packet_C2SAcknowledge = 99;

    public MCForgeHandShakeV2(MCForge forge) {
        super(forge);
    }

    public void handle(Packet recvPacket) {
        LoginPluginRequestPacket packet = (LoginPluginRequestPacket) recvPacket;
        if (!packet.getChannel().equals("fml:loginwrapper")) return;

        try {
            LoginWrapper loginWrapper = new LoginWrapper().fromBytes(packet.getData());
            String targetNetworkReceiver = loginWrapper.getTargetNetworkReceiver();
            ByteBufferNetInput in = new ByteBufferNetInput(ByteBuffer.wrap(loginWrapper.getPayload()));

            int packetID = in.readByte();
            switch (packetID) {
                case Packet_S2CModList: {
                    // recv: S2CModList
                    final List<String> mods = new ArrayList<>();
                    int len = in.readVarInt();
                    for (int x = 0; x < len; x++)
                        mods.add(in.readString());

                    final Map<String, String> channels = new HashMap<>();
                    len = in.readVarInt();
                    for (int x = 0; x < len; x++)
                        channels.put(in.readString(), in.readString());

                    final List<String> registries = new ArrayList<>();
                    len = in.readVarInt();
                    for (int x = 0; x < len; x++)
                        registries.add(in.readString());
                    // send: C2SModListReply
                    ByteArrayOutputStream buf = new ByteArrayOutputStream();
                    StreamNetOutput out = new StreamNetOutput(buf);

                    out.writeByte(Packet_C2SModListReply);

                    out.writeVarInt(mods.size());
                    mods.forEach(m -> {
                        try {
                            out.writeString(m);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    });

                    out.writeVarInt(channels.size());
                    channels.forEach((k, v) -> {
                        try {
                            out.writeString(k);
                            out.writeString(v);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    });

                    // TODO: Fill with known hashes, which requires keeping a file cache (FMLHandshakeMessages.java)
                    out.writeVarInt(0); // empty map
                    /*
                    out.writeVarInt(registries.size());
                    registries.forEach(r -> {
                        try {
                            out.writeString(r);
                            out.writeString("");
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    });
                    */

                    reply(packet.getMessageId(), targetNetworkReceiver, buf.toByteArray());
                    break;
                }
                case Packet_S2CRegistry:
                case Packet_S2CConfigData: {
                    // recv: S2CRegistry
                    // send: C2SAcknowledge
                    ByteArrayOutputStream buf = new ByteArrayOutputStream();
                    StreamNetOutput out = new StreamNetOutput(buf);

                    out.writeByte(Packet_C2SAcknowledge);

                    reply(packet.getMessageId(), targetNetworkReceiver, buf.toByteArray());
                    break;
                }// recv: S2CConfigData
            }
        } catch (Exception ex) {
            forge.session.disconnect("Failure to handshake", ex);
        }
    }

    public void newHandle(Packet recvPacket) {
        ClientboundCustomQueryPacket packet = (ClientboundCustomQueryPacket) recvPacket;
        if (!packet.getChannel().equals("fml:loginwrapper")) return;

        try {
            LoginWrapper loginWrapper = new LoginWrapper().fromBytes(packet.getData());
            String targetNetworkReceiver = loginWrapper.getTargetNetworkReceiver();
            ByteBufferNetInput in = new ByteBufferNetInput(ByteBuffer.wrap(loginWrapper.getPayload()));

            int packetID = in.readByte();
            switch (packetID) {
                case Packet_S2CModList: {
                    // recv: S2CModList
                    final List<String> mods = new ArrayList<>();
                    int len = in.readVarInt();
                    for (int x = 0; x < len; x++)
                        mods.add(in.readString());

                    final Map<String, String> channels = new HashMap<>();
                    len = in.readVarInt();
                    for (int x = 0; x < len; x++)
                        channels.put(in.readString(), in.readString());

                    final List<String> registries = new ArrayList<>();
                    len = in.readVarInt();
                    for (int x = 0; x < len; x++)
                        registries.add(in.readString());
                    // send: C2SModListReply
                    ByteArrayOutputStream buf = new ByteArrayOutputStream();
                    StreamNetOutput out = new StreamNetOutput(buf);

                    out.writeByte(Packet_C2SModListReply);

                    out.writeVarInt(mods.size());
                    mods.forEach(m -> {
                        try {
                            out.writeString(m);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    });

                    out.writeVarInt(channels.size());
                    channels.forEach((k, v) -> {
                        try {
                            out.writeString(k);
                            out.writeString(v);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    });

                    // TODO: Fill with known hashes, which requires keeping a file cache (FMLHandshakeMessages.java)
                    out.writeVarInt(0); // empty map
                    /*
                    out.writeVarInt(registries.size());
                    registries.forEach(r -> {
                        try {
                            out.writeString(r);
                            out.writeString("");
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    });
                    */

                    newReply(packet.getMessageId(), targetNetworkReceiver, buf.toByteArray());
                    break;
                }
                case Packet_S2CRegistry:
                case Packet_S2CConfigData: {
                    // recv: S2CRegistry
                    // send: C2SAcknowledge
                    ByteArrayOutputStream buf = new ByteArrayOutputStream();
                    StreamNetOutput out = new StreamNetOutput(buf);

                    out.writeByte(Packet_C2SAcknowledge);

                    newReply(packet.getMessageId(), targetNetworkReceiver, buf.toByteArray());
                    break;
                }// recv: S2CConfigData
            }
        } catch (Exception ex) {
            forge.session.disconnect("Failure to handshake", ex);
        }
    }

    public String getFMLVersion() {
        return "FML2";
    }

    private void reply(int id, String targetNetworkReceiver, byte[] payload) throws IOException {
        forge.session.send(new LoginPluginResponsePacket(id, new LoginWrapper(targetNetworkReceiver, payload).toBytes()));
    }

    private void newReply(int id, String targetNetworkReceiver, byte[] payload) throws IOException {
        forge.session.send(new ServerboundCustomQueryPacket(id, new LoginWrapper(targetNetworkReceiver, payload).toBytes()));
    }

    static class LoginWrapper {
        private String targetNetworkReceiver;
        private byte[] payload;

        public LoginWrapper() {}
        public LoginWrapper(String targetNetworkReceiver, byte[] payload) {
            this.targetNetworkReceiver = targetNetworkReceiver;
            this.payload = payload;
        }

        public LoginWrapper fromBytes(byte[] bytes) throws IOException {
            ByteBufferNetInput in = new ByteBufferNetInput(ByteBuffer.wrap(bytes));
            this.targetNetworkReceiver = in.readString();
            this.payload = in.readBytes(in.readVarInt());
            return this;
        }

        public byte[] toBytes() throws IOException {
            ByteArrayOutputStream buf = new ByteArrayOutputStream();
            StreamNetOutput pb = new StreamNetOutput(buf);
            pb.writeString(targetNetworkReceiver);
            pb.writeVarInt(payload.length);
            pb.writeBytes(payload);

            return buf.toByteArray();
        }

        public String getTargetNetworkReceiver() {
            return this.targetNetworkReceiver;
        }

        public byte[] getPayload() {
            return this.payload;
        }
    }
}
