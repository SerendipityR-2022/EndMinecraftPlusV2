package cn.serendipityr.EndMinecraftPlusV2.Tools;

import cc.summermc.bukkitYaml.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DataUtil {
    public static List<String> botRegPasswords;
    public static HashMap<String,String> botRegPasswordsMap = new HashMap<>();

    public static void loadData() {
        File dataFile = new File("data.yml");
        if (dataFile.exists()) {
            YamlConfiguration data = YamlConfiguration.loadConfiguration(dataFile);
            botRegPasswords = data.getStringList("Data");
            botRegPasswords.remove("");
        } else {
            botRegPasswords = new ArrayList<>();
        }

        if (botRegPasswords.size() < ConfigUtil.BotCount) {
            int count = ConfigUtil.BotCount - botRegPasswords.size();

            for (int i = 0; i < count; i++) {
                String newBotName = ConfigUtil.BotName.replace("$rnd", OtherUtils.getRandomString(3,5));
                String newBotPwd = OtherUtils.getRandomString(8,10);
                botRegPasswords.add(newBotName + "@" + newBotPwd);
            }
        }

        for (String PwdData:botRegPasswords) {
            try {
                String[] aPwdData = PwdData.split("@");
                botRegPasswordsMap.put(aPwdData[0], aPwdData[1]);
            } catch (Exception ignored) {}
        }

        updateData(botRegPasswords);
    }

    public static void updateData(List<String> dataList) {
        File dataFile = new File("data.yml");
        YamlConfiguration data = YamlConfiguration.loadConfiguration(dataFile);

        data.set("Data", dataList);

        try {
            data.save(dataFile);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
