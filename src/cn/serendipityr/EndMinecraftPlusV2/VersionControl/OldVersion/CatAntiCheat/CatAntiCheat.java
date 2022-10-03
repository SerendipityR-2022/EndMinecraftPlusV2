package cn.serendipityr.EndMinecraftPlusV2.VersionControl.OldVersion.CatAntiCheat;

import cn.serendipityr.EndMinecraftPlusV2.Tools.ConfigUtil;
import cn.serendipityr.EndMinecraftPlusV2.Tools.LogUtil;
import org.spacehq.mc.protocol.packet.ingame.client.ClientPluginMessagePacket;
import org.spacehq.packetlib.Session;
import org.spacehq.packetlib.io.stream.StreamNetOutput;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.zip.GZIPOutputStream;

public class CatAntiCheat {
    public static HashMap<Session, Byte> saltMap = new HashMap<>();

    public static void packetHandle(Session session, String channel, byte[] data, String username) throws IOException {
        ByteArrayOutputStream buf = new ByteArrayOutputStream();
        StreamNetOutput out = new StreamNetOutput(buf);
        byte receivedPacketID = data[0];

        byte sendPacketID;
        byte clientVersion;

        switch (receivedPacketID) {
            case 0:
                // Hello packet reply
                byte salt = data[1];
                saltMap.put(session, salt);
                sendPacketID = 4;
                clientVersion = 2;

                out.writeBytes(new byte[]{sendPacketID, 0, clientVersion, salt});

                // LogUtil.doLog(0,"发送PluginMessage: " + "CatAntiCheat" + " | " + Arrays.toString(buf.toByteArray()), "CatAntiCheat");
                session.send(new ClientPluginMessagePacket("CatAntiCheat", buf.toByteArray()));
                break;
            case 1:
                // File Check packet reply
                sendPacketID = 5;

                File loadedFiles = ConfigUtil.CACLoadedMods;
                List<String> fileHashList = getFileHashList(loadedFiles);

                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

                try {
                    GZIPOutputStream gzipOutputStream = new GZIPOutputStream(outputStream);
                    gzipOutputStream.write(saltMap.get(session));

                    BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(gzipOutputStream, StandardCharsets.UTF_8));
                    for (int i = 0; i < fileHashList.size(); i++) {
                        if (i > 0) writer.newLine();
                        writer.write(fileHashList.get(i));
                    }
                    writer.flush();
                    writer.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

                byte[] gzipData = outputStream.toByteArray();

                out.writeByte(sendPacketID);
                out.writeShort(gzipData.length);
                out.writeBytes(gzipData);
                out.writeInt(Arrays.hashCode(gzipData));

                // LogUtil.doLog(0,"发送PluginMessage: " + "CatAntiCheat" + " | " + Arrays.toString(buf.toByteArray()), "CatAntiCheat");
                LogUtil.doLog(0, "[PacketHandler] [" + username + "] 已发送FileCheck数据。", "CatAntiCheat");
                session.send(new ClientPluginMessagePacket("CatAntiCheat", buf.toByteArray()));
                break;
            case 2:
                // Class Check packet reply
                sendPacketID = 6;

                List<String> foundClassList = ConfigUtil.CACLoadedClass;

                out.writeByte(sendPacketID);
                out.writeShort(foundClassList.size());

                for (String s:foundClassList) {
                    writeUTF8String(out, s);
                }

                out.writeByte(saltMap.get(session));

                // LogUtil.doLog(0,"发送PluginMessage: " + "CatAntiCheat" + " | " + Arrays.toString(buf.toByteArray()), "CatAntiCheat");
                LogUtil.doLog(0, "[PacketHandler] [" + username + "] 已发送ClassCheck数据。", "CatAntiCheat");
                session.send(new ClientPluginMessagePacket("CatAntiCheat", buf.toByteArray()));
                // break;
            case 3:
                // Screenshot packet reply
                sendPacketID = 8;
                File imageFile = ConfigUtil.CACCustomScreenShot;
                byte[] imageData = getImageData(imageFile);

                out.writeByte(sendPacketID);

                ByteArrayInputStream in = new ByteArrayInputStream(imageData);

                try {
                    byte[] networkData = new byte[32763];
                    int size;
                    while ((size = in.read(networkData)) >= 0) {
                        ByteArrayOutputStream incomingData = new ByteArrayOutputStream();
                        StreamNetOutput streamNetOutput = new StreamNetOutput(incomingData);

                        streamNetOutput.writeByte(sendPacketID);
                        streamNetOutput.writeBoolean(in.available() == 0);

                        if (networkData.length == size) {
                            streamNetOutput.writeBytes(networkData);
                        } else {
                            streamNetOutput.writeBytes(Arrays.copyOf(networkData, size));
                        }

                        session.send(new ClientPluginMessagePacket("CatAntiCheat", incomingData.toByteArray()));
                        // LogUtil.doLog(0,"发送PluginMessage: " + "CatAntiCheat" + " | " + Arrays.toString(incomingData.toByteArray()), "CatAntiCheat");
                    }

                    LogUtil.doLog(0, "[PacketHandler] [" + username + "] 已发送ImageData数据。", "CatAntiCheat");
                } catch (IOException e) {
                    LogUtil.doLog(1, "[PacketHandler] [" + username + "] 发送ImageData数据时发生错误。", "CatAntiCheat");
                }
                break;
            case 9:
                // Data Check packet reply
                sendPacketID = 10;
                boolean isLighting = false;
                boolean isTransparentTexture = false;

                out.writeByte(sendPacketID);
                out.writeBoolean(isLighting);
                out.writeBoolean(isTransparentTexture);

                // LogUtil.doLog(0,"发送PluginMessage: " + "CatAntiCheat" + " | " + Arrays.toString(buf.toByteArray()), "CatAntiCheat");
                LogUtil.doLog(0, "[PacketHandler] [" + username + "] 已发送VanillaCheck数据。", "CatAntiCheat");
                session.send(new ClientPluginMessagePacket("CatAntiCheat", buf.toByteArray()));
                break;
        }
    }

    public static List<String> getFileHashList(File filesDir) {
        List<String> fileHashList = new ArrayList<>();

        for (File file: Objects.requireNonNull(filesDir.listFiles())) {
            fileHashList.add(getFileHash(file));
        }

        return fileHashList;
    }

    private static String getFileHash(File file) {
        try {
            try (InputStream in = Files.newInputStream(file.toPath())) {
                return calcHash(in) + "\0" + file.getName();
            }
        } catch (IOException ignored) { }
        return null;
    }

    private static String calcHash(InputStream in) throws IOException {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA1");

            final byte[] buffer = new byte[4096];
            int read = in.read(buffer, 0, 4096);

            while (read > -1) {
                md.update(buffer, 0, read);
                read = in.read(buffer, 0, 4096);
            }

            byte[] digest = md.digest();
            return String.format("%0" + (digest.length << 1) + "x", new BigInteger(1, digest)).toUpperCase();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    public static void writeUTF8String(StreamNetOutput to, String string) throws IOException {
        byte[] utf8Bytes = string.getBytes(StandardCharsets.UTF_8);
        writeVarInt(to, utf8Bytes.length);
        to.writeBytes(utf8Bytes);
    }

    public static void writeVarInt(StreamNetOutput to, int toWrite) throws IOException {
        while((toWrite & -128) != 0) {
            to.writeByte(toWrite & 127 | 128);
            toWrite >>>= 7;
        }

        to.writeByte(toWrite);
    }

    public static byte[] getImageData(File imageFile) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {
            GZIPOutputStream gzipOutputStream = new GZIPOutputStream(out);
            BufferedImage bufferedImage = ImageIO.read(imageFile);
            ImageIO.write(bufferedImage, "png", gzipOutputStream);
            gzipOutputStream.flush();
            gzipOutputStream.close();
        } catch (Exception ignored) {}

        return out.toByteArray();
    }
}
