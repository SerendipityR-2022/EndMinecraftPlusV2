package cn.serendipityr.EndMinecraftPlusV2.MultipleVersion.VersionSupport.P1_16_X;

import cn.serendipityr.EndMinecraftPlusV2.AdvanceModule.ACProtocol.AnotherStarAntiCheat;
import cn.serendipityr.EndMinecraftPlusV2.AdvanceModule.ACProtocol.AntiCheat3;
import cn.serendipityr.EndMinecraftPlusV2.MultipleVersion.Packet.PacketManager;
import cn.serendipityr.EndMinecraftPlusV2.MultipleVersion.UniverseMethods;
import cn.serendipityr.EndMinecraftPlusV2.Tools.ConfigUtil;
import cn.serendipityr.EndMinecraftPlusV2.Tools.LogUtil;
import cn.serendipityr.EndMinecraftPlusV2.Tools.OtherUtils;
import com.github.steveice10.mc.protocol.data.game.ClientRequest;
import com.github.steveice10.mc.protocol.data.game.entity.player.HandPreference;
import com.github.steveice10.mc.protocol.data.game.setting.ChatVisibility;
import com.github.steveice10.mc.protocol.data.game.setting.SkinPart;
import com.github.steveice10.mc.protocol.packet.ingame.client.*;
import com.github.steveice10.mc.protocol.packet.ingame.client.player.ClientPlayerChangeHeldItemPacket;
import com.github.steveice10.mc.protocol.packet.ingame.client.player.ClientPlayerMovementPacket;
import com.github.steveice10.mc.protocol.packet.ingame.client.player.ClientPlayerPositionRotationPacket;
import com.github.steveice10.mc.protocol.packet.ingame.client.world.ClientTeleportConfirmPacket;
import com.github.steveice10.mc.protocol.packet.ingame.server.ServerChatPacket;
import com.github.steveice10.mc.protocol.packet.ingame.server.ServerJoinGamePacket;
import com.github.steveice10.mc.protocol.packet.ingame.server.ServerKeepAlivePacket;
import com.github.steveice10.mc.protocol.packet.ingame.server.ServerPluginMessagePacket;
import com.github.steveice10.mc.protocol.packet.ingame.server.entity.player.ServerPlayerHealthPacket;
import com.github.steveice10.mc.protocol.packet.ingame.server.entity.player.ServerPlayerPositionRotationPacket;
import com.github.steveice10.packetlib.Client;
import com.github.steveice10.packetlib.Session;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.TranslatableComponent;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

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
        ServerPlayerPositionRotationPacket packet = (ServerPlayerPositionRotationPacket) recvPacket;
        ClientTeleportConfirmPacket teleportConfirmPacket = new ClientTeleportConfirmPacket(packet.getTeleportId());
        session.send(teleportConfirmPacket);
        sendPositionRotationPacket(session, packet.getX(), packet.getY(), packet.getZ(), packet.getYaw(), packet.getYaw());
        sendClientPlayerMovementPacket(session, true);
    }

    @Override
    public boolean checkServerChatPacket(Object packet) {
        return packet instanceof ServerChatPacket;
    }

    @Override
    public void handleServerChatPacket(Object client, Object recvPacket, String username) {
        ServerChatPacket chatPacket = (ServerChatPacket) recvPacket;
        Component message = chatPacket.getMessage();
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
        if (ConfigUtil.KeepAlive) {
            sendClientKeepAlivePacket(session, serverKeepAlivePacket.getPingId());
        }    }

    @Override
    public boolean checkServerPlayerHealthPacket(Object packet) {
        return packet instanceof ServerPlayerHealthPacket;
    }

    @Override
    public void handleServerPlayerHealthPacket(Object client, Object recvPacket, String username) {
        Session session = ((Client) client).getSession();
        ServerPlayerHealthPacket serverPlayerHealthPacket = (ServerPlayerHealthPacket) recvPacket;
        double health = serverPlayerHealthPacket.getHealth();
        if (health <= 0) {
            sendRespawnPacket(session);
            LogUtil.doLog(0, "[" + username + "] " + "假人于服务器中死亡，已重生。", "BotAttack");
        }
    }

    @Override
    public void handleOtherPacket(Object packet) {
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
        ClientTabCompletePacket clientTabCompletePacket = new ClientTabCompletePacket(1, cmd);
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
    }

    @Override
    public Object getMessageFromPacket(Object packet) {
        ServerChatPacket chatPacket = (ServerChatPacket) packet;
        return chatPacket.getMessage();
    }

    @Override
    public boolean hasMessageClickEvent(Object message) {
        Component msg = (Component) message;
        return msg.style().clickEvent() != null;
    }

    @Override
    public String getMessageText(Object message) {
        Component msg = (Component) message;
        return convertComponentToPlainText(msg);
    }

    public static String convertComponentToPlainText(Component component) {
        StringBuilder sb = new StringBuilder();
        if (component instanceof TextComponent) {
            sb.append(((TextComponent) component).content());
        } else if (component instanceof TranslatableComponent) {
            TranslatableComponent translatable = (TranslatableComponent) component;
            List<Component> args = translatable.args();
            // 将参数转换为纯文本
            List<String> argsText = args.stream()
                    .map(PacketHandler::convertComponentToPlainText)
                    .collect(Collectors.toList());
            // 附加子参数
            if (argsText.size() >= 2) {
                sb.append("[").append(argsText.get(0)).append("] ").append(argsText.get(1));
                // 如果有更多的参数，你可能需要根据实际情况处理
            }
        }
        // 处理子组件
        List<Component> children = component.children();
        for (Component child : children) {
            sb.append(convertComponentToPlainText(child));
        }
        return sb.toString();
    }

    @Override
    public void handleMessageExtra(cn.serendipityr.EndMinecraftPlusV2.MultipleVersion.Packet.PacketHandler packetHandler, Object message, Object client, String username) {
        Component msg = (Component) message;
        for (Component extra : msg.children()) {
            PacketManager.clickVerifiesHandle(packetHandler, client, extra, username);
        }
    }

    @Override
    public String getClickValue(Object message) {
        Component msg = (Component) message;
        return Objects.requireNonNull(msg.style().clickEvent()).value();
    }

    @Override
    public boolean hasMessageExtra(Object message) {
        Component msg = (Component) message;
        return !msg.children().isEmpty();
    }

    private void sendClientPluginMessagePacket(Session session, String channel, byte[] data) {
        ClientPluginMessagePacket clientPluginMessagePacket = new ClientPluginMessagePacket(channel, data);
        session.send(clientPluginMessagePacket);
    }

    private void sendClientSettingPacket(Session session, String locale) {
        ClientSettingsPacket clientSettingsPacket = new ClientSettingsPacket(locale, 8, ChatVisibility.FULL, true, Arrays.asList(SkinPart.values()), HandPreference.LEFT_HAND);
        session.send(clientSettingsPacket);
    }

    private void sendClientChangeHeldItemPacket(Session session, int slot) {
        ClientPlayerChangeHeldItemPacket clientPlayerChangeHeldItemPacket = new ClientPlayerChangeHeldItemPacket(slot);
        session.send(clientPlayerChangeHeldItemPacket);
    }

    private void sendPositionRotationPacket(Session session, double x, double y, double z, float yaw, float pitch) {
        ClientPlayerPositionRotationPacket clientPlayerPositionRotationPacket = new ClientPlayerPositionRotationPacket(true, x, y, z, yaw, pitch);
        session.send(clientPlayerPositionRotationPacket);
    }

    private void sendClientPlayerMovementPacket(Session session, boolean onGround) {
        ClientPlayerMovementPacket clientPlayerMovementPacket = new ClientPlayerMovementPacket(onGround);
        session.send(clientPlayerMovementPacket);
    }

    private void sendClientKeepAlivePacket(Session session, long pingId) {
        ClientKeepAlivePacket packet = new ClientKeepAlivePacket(pingId);
        session.send(packet);
    }

    private void sendRespawnPacket(Session session) {
        ClientRequestPacket clientRequestPacket = new ClientRequestPacket(ClientRequest.RESPAWN);
        session.send(clientRequestPacket);
    }
}

