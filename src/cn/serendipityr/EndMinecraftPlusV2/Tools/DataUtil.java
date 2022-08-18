package cn.serendipityr.EndMinecraftPlusV2.Tools;

import cc.summermc.bukkitYaml.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DataUtil {
    public static List<String> botRegPasswords = new ArrayList<>();
    public static HashMap<String,String> botRegPasswordsMap = new HashMap<>();
    public static File dataFile = new File("data.yml");

    public static void loadData() {
        YamlConfiguration data = YamlConfiguration.loadConfiguration(dataFile);
        botRegPasswords = data.getStringList("Data");

        for (String PwdData:botRegPasswords) {
            String[] aPwdData = PwdData.split("@");
            botRegPasswordsMap.put(aPwdData[0], aPwdData[1]);
        }
    }

    public static void updateData(String name, String pwd) {
        YamlConfiguration data = YamlConfiguration.loadConfiguration(dataFile);
        List<String> datas = data.getStringList("Data");

        String aPwdData = name + "@" + pwd;
        datas.add(aPwdData);
        data.set("Data", datas);
        botRegPasswords.add(aPwdData);
        botRegPasswordsMap.put(name, pwd);

        try {
            data.save(dataFile);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
