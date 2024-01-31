package cn.serendipityr.EndMinecraftPlusV2.MultipleVersion.VersionSupport.P1_8_X;

import cn.serendipityr.EndMinecraftPlusV2.AdvanceModule.ACProtocol.AnotherStarAntiCheat;
import cn.serendipityr.EndMinecraftPlusV2.AdvanceModule.ACProtocol.AntiCheat3;
import cn.serendipityr.EndMinecraftPlusV2.AdvanceModule.CatAntiCheat.CatAntiCheat;
import cn.serendipityr.EndMinecraftPlusV2.MultipleVersion.Packet.PacketManager;
import cn.serendipityr.EndMinecraftPlusV2.MultipleVersion.UniverseMethods;
import cn.serendipityr.EndMinecraftPlusV2.Tools.ConfigUtil;
import cn.serendipityr.EndMinecraftPlusV2.Tools.LogUtil;
import cn.serendipityr.EndMinecraftPlusV2.Tools.OtherUtils;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.spacehq.mc.protocol.data.game.ItemStack;
import org.spacehq.mc.protocol.data.game.values.ClientRequest;
import org.spacehq.mc.protocol.data.game.values.setting.ChatVisibility;
import org.spacehq.mc.protocol.data.game.values.setting.SkinPart;
import org.spacehq.mc.protocol.data.message.Message;
import org.spacehq.mc.protocol.packet.ingame.client.*;
import org.spacehq.mc.protocol.packet.ingame.client.player.ClientChangeHeldItemPacket;
import org.spacehq.mc.protocol.packet.ingame.client.player.ClientPlayerMovementPacket;
import org.spacehq.mc.protocol.packet.ingame.client.player.ClientPlayerPositionRotationPacket;
import org.spacehq.mc.protocol.packet.ingame.client.window.ClientConfirmTransactionPacket;
import org.spacehq.mc.protocol.packet.ingame.server.ServerChatPacket;
import org.spacehq.mc.protocol.packet.ingame.server.ServerJoinGamePacket;
import org.spacehq.mc.protocol.packet.ingame.server.ServerKeepAlivePacket;
import org.spacehq.mc.protocol.packet.ingame.server.ServerPluginMessagePacket;
import org.spacehq.mc.protocol.packet.ingame.server.entity.player.ServerPlayerPositionRotationPacket;
import org.spacehq.mc.protocol.packet.ingame.server.entity.player.ServerUpdateHealthPacket;
import org.spacehq.opennbt.NBTIO;
import org.spacehq.opennbt.tag.builtin.CompoundTag;
import org.spacehq.opennbt.tag.builtin.ListTag;
import org.spacehq.opennbt.tag.builtin.StringTag;
import org.spacehq.opennbt.tag.builtin.Tag;
import org.spacehq.packetlib.Client;
import org.spacehq.packetlib.Session;
import org.spacehq.packetlib.io.stream.StreamNetOutput;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PacketHandler implements cn.serendipityr.EndMinecraftPlusV2.MultipleVersion.Packet.PacketHandler {
    @Override
    public boolean checkServerPluginMessagePacket(Object packet) {
        return packet instanceof ServerPluginMessagePacket;
    }

    @Override
    public void handleServerPluginMessagePacket(Object client, Object recvPacket, String username) {
        ServerPluginMessagePacket packet = (ServerPluginMessagePacket) recvPacket;
        Session session = ((Client) client).getSession();

        switch (packet.getChannel()) {
            case "AntiCheat3.4.3":
                AntiCheat3 ac3 = new AntiCheat3();
                String code = ac3.uncompress(packet.getData());
                byte[] checkData = ac3.getCheckData("AntiCheat3.jar", code, new String[]{"44f6bc86a41fa0555784c255e3174260"});
                sendClientPluginMessagePacket(session, "AntiCheat3.4.3", checkData);
                break;
            case "anotherstaranticheat":
                AnotherStarAntiCheat asac = new AnotherStarAntiCheat();
                String salt = asac.decodeSPacket(packet.getData());
                byte[] data = asac.encodeCPacket(new String[]{"4863f8708f0c24517bb5d108d45f3e15"}, salt);
                sendClientPluginMessagePacket(session, "anotherstaranticheat", data);
                break;
            case "VexView":
                if (new String(packet.getData()).equals("GET:Verification")) {
                    sendClientPluginMessagePacket(session, "VexView", "Verification:1.8.10".getBytes());
                }
                break;
            case "MAC|Check":
                if (ConfigUtil.RandomMAC) {
                    byte[] MACAddress = UniverseMethods.getRandomMAC();
                    LogUtil.doLog(0, "[" + username + "] 已发送随机MACAddress数据。(" + Arrays.toString(MACAddress) + ")", "MACCheck");
                    sendClientPluginMessagePacket(session, packet.getChannel(), MACAddress);
                }
                break;
            case "CatAntiCheat":
            case "catanticheat:data":
                if (ConfigUtil.CatAntiCheat) {
                    CatAntiCheat.packetHandle(session, packet.getData(), username);
                }
                break;
            default:
                // 收到插件消息
        }
    }

    @Override
    public boolean checkServerJoinGamePacket(Object packet) {
        return packet instanceof ServerJoinGamePacket;
    }

    @Override
    public void handleServerJoinGamePacket(Object client, Object recvPacket, String username) {
        Session session = ((Client) client).getSession();
        session.setFlag("join", true);
        LogUtil.doLog(0, "[假人加入服务器] [" + username + "]", "BotAttack");

        sendClientSettingPacket(session, "zh_CN");
        sendClientChangeHeldItemPacket(session, 1);
    }

    @Override
    public boolean checkServerPlayerPositionRotationPacket(Object packet) {
        return packet instanceof ServerPlayerPositionRotationPacket;
    }

    @Override
    public void handleServerPlayerPositionRotationPacket(Object client, Object recvPacket, String username) {
        Session session = ((Client) client).getSession();
        sendClientPlayerMovementPacket(session, true);
    }

    @Override
    public boolean checkServerChatPacket(Object packet) {
        return packet instanceof ServerChatPacket;
    }

    @Override
    public void handleServerChatPacket(Object client, Object recvPacket, String username) {
        ServerChatPacket chatPacket = (ServerChatPacket) recvPacket;
        Message message = chatPacket.getMessage();
        if (ConfigUtil.ShowServerMessages && !getMessageText(message).equals("")) {
            LogUtil.doLog(0, "[服务端返回信息] [" + username + "] " + getMessageText(message), "BotAttack");
        }
    }

    @Override
    public boolean checkServerKeepAlivePacket(Object packet) {
        return packet instanceof ServerKeepAlivePacket;
    }

    @Override
    public void handleServerKeepAlivePacket(Object client, Object recvPacket, String username) {
        Session session = ((Client) client).getSession();
        ServerKeepAlivePacket serverKeepAlivePacket = (ServerKeepAlivePacket) recvPacket;
        sendClientKeepAlivePacket(session, serverKeepAlivePacket.getPingId());
    }

    @Override
    public boolean checkServerPlayerHealthPacket(Object packet) {
        return packet instanceof ServerUpdateHealthPacket;
    }

    @Override
    public void handleServerPlayerHealthPacket(Object client, Object recvPacket, String username) {
        Session session = ((Client) client).getSession();
        ServerUpdateHealthPacket serverUpdateHealthPacket = (ServerUpdateHealthPacket) recvPacket;
        double health = serverUpdateHealthPacket.getHealth();
        if (health <= 0) {
            sendRespawnPacket(session);
            LogUtil.doLog(0, "[" + username + "] " + "假人于服务器中死亡，已重生。", "BotAttack");
        }
    }

    @Override
    public void handleOtherPacket(Object packet) {
        String packetName = packet.getClass().getSimpleName();

        if (packetName.contains("ServerEntity")
                || packetName.contains("ServerPlaySound")
                || packetName.contains("ServerUpdateTime")
                || packetName.contains("ServerMultiChunkData")
                || packetName.contains("ServerUpdateTileEntity")
                || packetName.contains("ServerSpawnMob")
                || packetName.contains("ServerChunkData")
                || packetName.contains("ServerPlayEffect")
                || packetName.contains("ServerDestroyEntities")
                || packetName.contains("ServerWindowItems")
        ) {
            return;
        }

        LogUtil.doLog(0, "收到未处理的数据包: " + packetName, "DEBUG");
    }

    @Override
    public void sendChatPacket(Object client, String text) {
        Session session = ((Client) client).getSession();
        ClientChatPacket chatPacket = new ClientChatPacket(text);
        session.send(chatPacket);
    }

    @Override
    public void sendTabCompletePacket(Object client, String cmd) {
        Session session = ((Client) client).getSession();
        ClientTabCompletePacket clientTabCompletePacket = new ClientTabCompletePacket(cmd);
        session.send(clientTabCompletePacket);
    }

    @Override
    public void sendPositionPacketFromPacket(Object client, Object recvPacket, boolean random) {
        Session session = ((Client) client).getSession();
        ServerPlayerPositionRotationPacket packet = (ServerPlayerPositionRotationPacket) recvPacket;
        if (packet == null) {
            return;
        }
        double x = packet.getX() + (random ? OtherUtils.getRandomInt(-10, 10) : 0);
        double y = packet.getY() + (random ? OtherUtils.getRandomInt(-10, 10) : 0);
        double z = packet.getZ() + (random ? OtherUtils.getRandomInt(-10, 10) : 0);
        float yaw = packet.getYaw() + (random ? OtherUtils.getRandomFloat(0.00, 1.00) : 0);
        float pitch = packet.getPitch() + (random ? OtherUtils.getRandomFloat(0.00, 1.00) : 0);
        sendPositionRotationPacket(session, x, y, z, yaw, pitch);
    }

    @Override
    public void sendCrashBookPacket(Object client) {
        Session session = ((Client) client).getSession();

        try {
            ItemStack crashBook = getCrashBook();

            ByteArrayOutputStream buf = new ByteArrayOutputStream();
            StreamNetOutput out = new StreamNetOutput(buf);

            out.writeShort(crashBook.getId());
            out.writeByte(crashBook.getAmount());
            out.writeShort(crashBook.getData());

            NBTIO.writeTag(new DataOutputStream(buf), crashBook.getNBT());

            byte[] crashData = buf.toByteArray();

            sendClientPluginMessagePacket(session, "MC|BEdit", crashData);
            sendClientPluginMessagePacket(session, "MC|BSign", crashData);
        } catch (Exception ignored) {
        }
    }

    @Override
    public Object getMessageFromPacket(Object packet) {
        ServerChatPacket chatPacket = (ServerChatPacket) packet;
        return chatPacket.getMessage();
    }

    @Override
    public boolean hasMessageClickEvent(Object message) {
        Message msg = (Message) message;
        return msg.getStyle().getClickEvent() != null;
    }

    @Override
    public String getMessageText(Object message) {
        Message msg = (Message) message;
        return getTextFromJson(msg.toJson());
    }

    private static String getTextFromJson(JsonElement element) {
        StringBuilder textBuilder = new StringBuilder();
        if (element.isJsonObject()) {
            JsonObject obj = element.getAsJsonObject();

            boolean hasTranslate = obj.has("translate");
            String translate = hasTranslate ? obj.get("translate").getAsString() : "";
            if (hasTranslate && !translate.contains("chat.type")) {
                return translate;
            }
            if (obj.has("text")) {
                textBuilder.append(obj.get("text").getAsString());
            }
            if (obj.has("extra")) {
                for (JsonElement extraElement : obj.getAsJsonArray("extra")) {
                    textBuilder.append(getTextFromJson(extraElement));
                }
            }
            if (obj.has("with")) {
                // 记录 "with" 数组元素以便之后处理
                ArrayList<String> withElementsText = new ArrayList<>();
                for (JsonElement withElement : obj.getAsJsonArray("with")) {
                    withElementsText.add(getTextFromJson(withElement));
                }
                // 对 "with" 数组的处理，添加适当的分隔符
                if (!withElementsText.isEmpty()) {
                    // 添加第一个元素，并包围以方括号
                    textBuilder.append("[").append(withElementsText.get(0)).append("]");
                    // 如果有更多元素，这些应该跟随在后面，并用空格分隔
                    for (int i = 1; i < withElementsText.size(); i++) {
                        textBuilder.append(" ").append(withElementsText.get(i));
                    }
                }
            }
        } else if (element.isJsonArray()) {
            for (JsonElement arrElement : element.getAsJsonArray()) {
                textBuilder.append(getTextFromJson(arrElement));
            }
        }
        return textBuilder.toString();
    }

    @Override
    public void handleMessageExtra(cn.serendipityr.EndMinecraftPlusV2.MultipleVersion.Packet.PacketHandler packetHandler, Object message, Object client, String username) {
        Message msg = (Message) message;
        for (Message extra : msg.getExtra()) {
            PacketManager.clickVerifiesHandle(packetHandler, client, extra, username);
        }
    }

    @Override
    public String getClickValue(Object message) {
        Message msg = (Message) message;
        return msg.getStyle().getClickEvent().getValue();
    }

    @Override
    public boolean hasMessageExtra(Object message) {
        Message msg = (Message) message;
        return msg.getExtra() != null && !msg.getExtra().isEmpty();
    }

    private void sendClientPluginMessagePacket(Session session, String channel, byte[] data) {
        ClientPluginMessagePacket clientPluginMessagePacket = new ClientPluginMessagePacket(channel, data);
        session.send(clientPluginMessagePacket);
    }

    private void sendClientSettingPacket(Session session, String locale) {
        ClientSettingsPacket clientSettingsPacket = new ClientSettingsPacket(locale, 8, ChatVisibility.FULL, true, SkinPart.values());
        session.send(clientSettingsPacket);
    }

    private void sendClientChangeHeldItemPacket(Session session, int slot) {
        ClientChangeHeldItemPacket clientChangeHeldItemPacket = new ClientChangeHeldItemPacket(slot);
        session.send(clientChangeHeldItemPacket);
    }

    private void sendPositionRotationPacket(Session session, double x, double y, double z, float yaw, float pitch) {
        ClientPlayerPositionRotationPacket clientPlayerPositionRotationPacket = new ClientPlayerPositionRotationPacket(true, x, y, z, yaw, pitch);
        session.send(clientPlayerPositionRotationPacket);
    }

    private void sendClientPlayerMovementPacket(Session session, boolean onGround) {
        ClientPlayerMovementPacket clientPlayerMovementPacket = new ClientPlayerMovementPacket(onGround);
        session.send(clientPlayerMovementPacket);
    }

    private void sendClientKeepAlivePacket(Session session, int pingId) {
        ClientKeepAlivePacket packet = new ClientKeepAlivePacket(pingId);
        session.send(packet);
    }

    private void sendRespawnPacket(Session session) {
        ClientRequestPacket clientRequestPacket = new ClientRequestPacket(ClientRequest.RESPAWN);
        session.send(clientRequestPacket);
    }

    public static ItemStack getCrashBook() {
        ItemStack crashBook;
        CompoundTag nbtTag = new CompoundTag("crashBook");
        List<Tag> pageList = new ArrayList<>();

        // Plain Mode
        nbtTag.put(new StringTag("author", OtherUtils.getRandomString(20, 20)));
        nbtTag.put(new StringTag("title", OtherUtils.getRandomString(20, 20)));

        for (int a = 0; a < 35; a++) {
            pageList.add(new StringTag("", OtherUtils.getRandomString(600, 600)));
        }

        nbtTag.put(new ListTag("pages", pageList));
        crashBook = new ItemStack(386, 1, 0, nbtTag);

        return crashBook;
    }
}
