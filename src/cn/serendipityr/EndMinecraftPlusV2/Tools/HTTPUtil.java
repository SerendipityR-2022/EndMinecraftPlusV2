package cn.serendipityr.EndMinecraftPlusV2.Tools;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

public class HTTPUtil {
    public static String sendGet(String url) {
        StringBuilder result = new StringBuilder();
        BufferedReader in = null;
        try {
            URL realUrl = new URL(url);
            URLConnection connection = realUrl.openConnection();
            connection.setRequestProperty("accept", "*/*");
            connection.setRequestProperty("connection", "Keep-Alive");
            connection.setRequestProperty("user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
            connection.connect();

            in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String line;
            while ((line = in.readLine()) != null) {
                result.append(line);
            }
        } catch (Exception e) {
            LogUtil.doLog(1, "HTTP请求出错! 详细信息: " + e.getMessage(), null);
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (Exception e) {
                LogUtil.doLog(1, "IO异常! 详细信息: " + e.getMessage(), null);
            }
        }
        return result.toString();
    }
}
