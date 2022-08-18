package cn.serendipityr.EndMinecraftPlusV2.VersionControl.OldVersion.AttackUtils.Methods;

import org.spacehq.mc.protocol.packet.ingame.client.ClientSettingsPacket;
import org.spacehq.mc.protocol.packet.ingame.client.ClientTabCompletePacket;
import org.spacehq.mc.protocol.packet.ingame.client.player.ClientPlayerPositionRotationPacket;
import org.spacehq.mc.protocol.packet.ingame.server.entity.player.ServerPlayerPositionRotationPacket;
import org.spacehq.packetlib.Session;
import org.spacehq.packetlib.packet.Packet;

import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;

public class MultiVersionPacket {
    public static void sendTabPacket(Session session, String text) {
        try {
            Class<?> cls = ClientTabCompletePacket.class;
            Constructor<?> constructor = cls.getDeclaredConstructor();
            constructor.setAccessible(true);
            ClientTabCompletePacket packet = (ClientTabCompletePacket) constructor.newInstance();
            Field field = cls.getDeclaredField("text");
            field.setAccessible(true);
            field.set(packet, text);
            session.send(packet);
        } catch (Exception ignored) {}
    }

    public static void sendPosPacket(Session session, double x, double y, double z, float yaw, float pitch) {
        try {
            Class<?> cls = ClientPlayerPositionRotationPacket.class;
            Constructor<?> constructor;
            ClientPlayerPositionRotationPacket packet;
            try {
                constructor = cls.getConstructor(boolean.class, double.class, double.class, double.class, float.class, float.class);
                packet = (ClientPlayerPositionRotationPacket) constructor.newInstance(true, x, y, z, yaw, pitch);
            } catch (NoSuchMethodException ex) {
                constructor = cls.getConstructor(boolean.class, double.class, double.class, double.class, double.class, float.class, float.class);
                packet = (ClientPlayerPositionRotationPacket) constructor.newInstance(true, x, y - 1.62, y , z, yaw, pitch);
            }
            session.send(packet);
        } catch (Exception ignored) {}
    }

    public static void sendClientSettingPacket(Session session, String locale) {
        try {
            Class<?> cls = ClientSettingsPacket.class;
            Constructor<?> constructor;
            ClientSettingsPacket packet;
            try {
                Class<?> parm1Class = Class.forName("org.spacehq.mc.protocol.data.game.setting.ChatVisibility");
                Class<?> parm2Class = Class.forName("[Lorg.spacehq.mc.protocol.data.game.setting.SkinPart;");
                Class<?> parm3Class = Class.forName("org.spacehq.mc.protocol.data.game.entity.player.Hand");

                Class<?> skinClass = Class.forName("org.spacehq.mc.protocol.data.game.setting.SkinPart");
                Object[] arrSkin = (Object[]) Array.newInstance(skinClass, 1);
                Array.set(arrSkin, 0, skinClass.getEnumConstants()[0]);

                constructor = cls.getConstructor(String.class, int.class, parm1Class, boolean.class, parm2Class, parm3Class);
                packet = (ClientSettingsPacket) constructor.newInstance(locale, 10, parm1Class.getEnumConstants()[0], true, arrSkin, parm3Class.getEnumConstants()[0]);
            } catch (NoSuchMethodException ex) {
                Class<?> parm1Class = Class.forName("org.spacehq.mc.protocol.packet.ingame.client.ClientSettingsPacket.ChatVisibility");
                Class<?> parm2Class = Class.forName("org.spacehq.mc.protocol.packet.ingame.client.ClientSettingsPacket.Difficulty");

                constructor = cls.getConstructor(String.class, int.class, parm1Class, boolean.class, parm2Class, boolean.class);
                packet = (ClientSettingsPacket) constructor.newInstance(locale, 10, parm1Class.getEnumConstants()[0], true, parm2Class.getEnumConstants()[0], true);
            }
            session.send(packet);
        } catch (Exception ignored) {}
    }

    public static void sendClientPlayerChangeHeldItemPacket(Session session, int slot) {
        try {
            Class<?> cls = Class.forName("org.spacehq.mc.protocol.packet.ingame.client.player.ClientPlayerChangeHeldItemPacket");
            Constructor<?> constructor = cls.getConstructor(int.class);
            Packet packet = (Packet) constructor.newInstance(slot);
            session.send(packet);
        } catch (Exception ignored) {}
    }

    public static void sendClientTeleportConfirmPacket(Session session, int id) {
        try {
            Class<?> cls = Class.forName("org.spacehq.mc.protocol.packet.ingame.client.world.ClientTeleportConfirmPacket");
            Constructor<?> constructor = cls.getConstructor(int.class);
            Packet packet = (Packet) constructor.newInstance(id);
            session.send(packet);
        } catch (Exception ignored) {}
    }

    public static void sendClientTeleportConfirmPacket(Session session, ServerPlayerPositionRotationPacket packet) {
        try {
            sendClientTeleportConfirmPacket(session, (int) ServerPlayerPositionRotationPacket.class.getMethod("getTeleportId").invoke(packet));
        } catch (Exception ignored) {}
    }
}
