package cn.serendipityr.EndMinecraftPlusV2.VersionControl.NewVersion.ACProtocol;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;
import io.netty.buffer.ByteBufOutputStream;
import io.netty.buffer.Unpooled;
import com.github.steveice10.opennbt.NBTIO;
import com.github.steveice10.opennbt.tag.builtin.ByteArrayTag;
import com.github.steveice10.opennbt.tag.builtin.CompoundTag;
import com.github.steveice10.opennbt.tag.builtin.ListTag;

import javax.crypto.Cipher;
import java.io.*;
import java.math.BigInteger;
import java.security.KeyFactory;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.spec.RSAPrivateKeySpec;
import java.security.spec.RSAPublicKeySpec;
import java.util.HashSet;

public class AnotherStarAntiCheat {
    private final RSAPublicKeySpec clientPublicKey;
    private final RSAPrivateKeySpec clientPrivateKey;
    private final RSAPrivateKeySpec serverPrivateKey;
    private final Cipher clientPublicCipher;
    private final Cipher clientPrivateCipher;

    {
        clientPublicKey = new RSAPublicKeySpec(new BigInteger("110765265706288445432931740098429930486184776709780238438557625017629729661573053311960037088088056476891441153774532896215697933861615265976216025080531157954939381061122847093245480153835410088489980899310444547515616362801564379991216339336084947840837937083577860481298666622413144703510357744423856873247"), new BigInteger("65537"));
        clientPrivateKey = new RSAPrivateKeySpec(new BigInteger("127165929499203230494093636558638013965252017663799535492473366241186172657381802456786953683177089298103209968185180374096740166047543803456852621212768600619629127825926162262624471403179175000577485553838478368190967564483813134073944752700839742123715548482599351441718070230200126591331603170595424433351"), new BigInteger("8120442115967552979504430611683477858989268564673406717365778685618263462946775764555188689810276923151226539464042905009305546407509816095746345114598417659887966619863710400187548253486545871530930302536230539029867970428580758154100440676071461522806034959078299053007522099777875429363283152166104624633"));
        serverPrivateKey = new RSAPrivateKeySpec(new BigInteger("110765265706288445432931740098429930486184776709780238438557625017629729661573053311960037088088056476891441153774532896215697933861615265976216025080531157954939381061122847093245480153835410088489980899310444547515616362801564379991216339336084947840837937083577860481298666622413144703510357744423856873247"), new BigInteger("46811199235043884723986609175064677734346396089701745030024727102450381043328026268845951862745851965156510759358732282931568208403881136178696846768321267356928789780189985031058525539943424151785807761491334305713351706700232920994479762308513198807509163912459260953727448797718901389753582140608347129153"));

        try {
            (clientPublicCipher = Cipher.getInstance("RSA")).init(1, KeyFactory.getInstance("RSA").generatePublic(clientPublicKey));
            (clientPrivateCipher = Cipher.getInstance("RSA")).init(2, KeyFactory.getInstance("RSA").generatePrivate(clientPrivateKey));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void ctsEncode(ByteBuf buf, byte[][] md5s) {
        try {
            CompoundTag nbt = new CompoundTag("");
            ListTag strList = new ListTag("md5s", ByteArrayTag.class);
            for (final byte[] md5 : md5s) {
                strList.add(new ByteArrayTag("", md5));
            }
            nbt.put(strList);
            NBTIO.writeTag((OutputStream) new DataOutputStream(new ByteBufOutputStream(buf)), nbt);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private byte[] stcDecode(ByteBuf buf) {
        try {
            CompoundTag nbt = (CompoundTag) NBTIO.readTag((InputStream) new DataInputStream(new ByteBufInputStream(buf)));
            return ((ByteArrayTag) nbt.get("salt")).getValue();    
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public byte[] encodeCPacket(String[] md5s, String salt) {
        try {
            HashSet<byte[]> rsaMd5s = new HashSet<byte[]>();
            for (String md5 : md5s) {
                rsaMd5s.add(clientPublicCipher.doFinal(md5(md5 + salt).getBytes()));
            }

            ByteBuf buf = Unpooled.buffer();
            buf.writeByte(1); // packet id
            ctsEncode(buf, rsaMd5s.toArray(new byte[0][]));
            return buf.array();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public String decodeSPacket(byte[] data) {
        try {
            ByteBuf buf = Unpooled.copiedBuffer(data);
            buf.readByte(); // packet id
            return new String(clientPrivateCipher.doFinal(stcDecode(buf)));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public String md5(String str) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(str.getBytes());
            byte[] digest = md.digest();
            return String.format("%0" + (digest.length << 1) + "x", new BigInteger(1, digest));
        } catch (NoSuchAlgorithmException e) {
            return null;
        }
    }
}
