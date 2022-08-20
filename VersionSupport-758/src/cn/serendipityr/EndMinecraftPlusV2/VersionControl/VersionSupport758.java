package cn.serendipityr.EndMinecraftPlusV2.VersionControl;

import com.github.steveice10.mc.protocol.packet.ingame.clientbound.ClientboundChatPacket;
import com.github.steveice10.mc.protocol.packet.ingame.clientbound.entity.player.ClientboundPlayerPositionPacket;
import com.github.steveice10.mc.protocol.packet.ingame.serverbound.ServerboundChatPacket;
import com.github.steveice10.mc.protocol.packet.ingame.serverbound.ServerboundClientInformationPacket;
import com.github.steveice10.mc.protocol.packet.ingame.serverbound.ServerboundCommandSuggestionPacket;
import com.github.steveice10.mc.protocol.packet.ingame.serverbound.player.ServerboundMovePlayerPosRotPacket;
import com.github.steveice10.packetlib.Session;
import com.github.steveice10.packetlib.packet.Packet;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;

import java.io.File;
import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class VersionSupport758 {
    public static List<String> clickVerifiesHandle(Packet packet, Session session, List<String> ClickVerifiesDetect, Component Message) {
        ClientboundChatPacket chatPacket = (ClientboundChatPacket) packet;

        List<String> result = new ArrayList<>();
        boolean needClick = false;
        Component message;

        if (Message != null) {
            message = Message;
        } else {
            message = chatPacket.getMessage();
        }

        String simpleMsg = PlainTextComponentSerializer.plainText().serialize(message);

        if (message.style().clickEvent() != null) {
            for (String clickVerifiesDetect:ClickVerifiesDetect) {
                if (simpleMsg.contains(clickVerifiesDetect)) {
                    needClick = true;
                    break;
                }
            }
        }

        if (needClick) {
            session.send(new ServerboundChatPacket(Objects.requireNonNull(message.style().clickEvent()).value()));
            result.add("1");
            result.add(simpleMsg);
            result.add(Objects.requireNonNull(message.style().clickEvent()).value());
            return result;
        }

        if (!message.children().isEmpty()) {
            for (Component extraMessage:message.children()) {
                clickVerifiesHandle(null, session, ClickVerifiesDetect, extraMessage);
            }
        }

        result.add("0");
        result.add(simpleMsg);
        return result;
    }

    public static void sendClientSettingPacket(Session session, String locale) {
        try {
            Class<?> cls = ServerboundClientInformationPacket.class;
            Constructor<?> constructor;
            ServerboundClientInformationPacket packet;
            try {
                Class<?> parm1Class = Class.forName("com.github.steveice10.mc.protocol.data.game.setting.ChatVisibility");
                Class<?> parm2Class = Class.forName("com.github.steveice10.mc.protocol.data.game.setting.SkinPart;");
                Class<?> parm3Class = Class.forName("com.github.steveice10.mc.protocol.data.game.entity.player.Hand");

                Class<?> skinClass = Class.forName("com.github.steveice10.mc.protocol.data.game.setting.SkinPart");
                Object[] arrSkin = (Object[]) Array.newInstance(skinClass, 1);
                Array.set(arrSkin, 0, skinClass.getEnumConstants()[0]);

                constructor = cls.getConstructor(String.class, int.class, parm1Class, boolean.class, parm2Class, parm3Class);
                packet = (ServerboundClientInformationPacket) constructor.newInstance(locale, 10, parm1Class.getEnumConstants()[0], true, arrSkin, parm3Class.getEnumConstants()[0]);
            } catch (NoSuchMethodException ex) {
                Class<?> parm1Class = Class.forName("com.github.steveice10.mc.protocol.packet.ingame.serverbound.ServerboundClientInformationPacket.chatVisibility");
                Class<?> parm2Class = Class.forName("com.github.steveice10.mc.protocol.packet.ingame.serverbound.ServerboundChangeDifficultyPacket.difficulty");

                constructor = cls.getConstructor(String.class, int.class, parm1Class, boolean.class, parm2Class, boolean.class);
                packet = (ServerboundClientInformationPacket) constructor.newInstance(locale, 10, parm1Class.getEnumConstants()[0], true, parm2Class.getEnumConstants()[0], true);
            }
            session.send(packet);
        } catch (Exception ignored) {}
    }

    public static void sendClientPlayerChangeHeldItemPacket(Session session, int slot) {
        try {
            Class<?> cls = Class.forName("com.github.steveice10.mc.protocol.packet.ingame.serverbound.player.ServerboundSetCarriedItemPacket");
            Constructor<?> constructor = cls.getConstructor(int.class);
            Packet packet = (Packet) constructor.newInstance(slot);
            session.send(packet);
        } catch (Exception ignored) {}
    }

    public static void sendClientTeleportConfirmPacket(Session session, int id) {
        try {
            Class<?> cls = Class.forName("com.github.steveice10.mc.protocol.packet.ingame.serverbound.level.ServerboundAcceptTeleportationPacket");
            Constructor<?> constructor = cls.getConstructor(int.class);
            Packet packet = (Packet) constructor.newInstance(id);
            session.send(packet);
        } catch (Exception ignored) {}
    }

    public static void sendClientTeleportConfirmPacket(Session session, ClientboundPlayerPositionPacket packet) {
        try {
            sendClientTeleportConfirmPacket(session, (int) ClientboundPlayerPositionPacket.class.getMethod("getTeleportId").invoke(packet));
        } catch (Exception ignored) {}
    }

    public static void sendPosPacket(Session session, double x, double y, double z, float yaw, float pitch) {
        try {
            Class<?> cls = ServerboundMovePlayerPosRotPacket.class;
            Constructor<?> constructor;
            ServerboundMovePlayerPosRotPacket packet;
            try {
                constructor = cls.getConstructor(boolean.class, double.class, double.class, double.class, float.class, float.class);
                packet = (ServerboundMovePlayerPosRotPacket) constructor.newInstance(true, x, y, z, yaw, pitch);
            } catch (NoSuchMethodException ex) {
                constructor = cls.getConstructor(boolean.class, double.class, double.class, double.class, double.class, float.class, float.class);
                packet = (ServerboundMovePlayerPosRotPacket) constructor.newInstance(true, x, y - 1.62, y , z, yaw, pitch);
            }
            session.send(packet);
        } catch (Exception ignored) {}
    }

    public static void sendChatPacket(Session session, String msg) {
        session.send(new ServerboundChatPacket(msg));
    }

    public static void sendTabPacket(Session session, String text) {
        try {
            Class<?> cls = ServerboundCommandSuggestionPacket.class;
            Constructor<?> constructor = cls.getDeclaredConstructor();
            constructor.setAccessible(true);
            ServerboundCommandSuggestionPacket packet = (ServerboundCommandSuggestionPacket) constructor.newInstance();
            Field field = cls.getDeclaredField("text");
            field.setAccessible(true);
            field.set(packet, text);
            session.send(packet);
        } catch (Exception ignored) {}
    }

    public static boolean checkServerChatPacket(Packet packet) {
        try {
            Class.forName("com.github.steveice10.mc.protocol.packet.ingame.clientbound.ClientboundChatPacket");
        } catch (ClassNotFoundException e) {
            return false;
        }

        return packet instanceof ClientboundChatPacket;
    }
}
