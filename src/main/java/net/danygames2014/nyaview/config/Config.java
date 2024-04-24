package net.danygames2014.nyaview.config;

import blue.endless.jankson.Jankson;
import blue.endless.jankson.JsonObject;
import blue.endless.jankson.api.SyntaxError;
import net.danygames2014.nyaview.mapping.MappingType;
import net.danygames2014.nyaview.mapping.Mappings;
import net.danygames2014.nyaview.util.Logger;
import net.fabricmc.mappingio.format.MappingFormat;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import static net.danygames2014.nyaview.NyaView.config;

public class Config {
    public static Jankson jankson = Jankson.builder().build();
    public static File configFile = new File("config.json");

    public static boolean readConfig() {
        try {
            JsonObject configJson = jankson.load(configFile);
            config = jankson.fromJson(configJson, MappingsConfig.class);

            return true;
        } catch (FileNotFoundException e) {
            Logger.warn("Config file doesn't exist, creating a new one", e);
            writeConfig();
        } catch (IOException e) {
            Logger.error("Unable to load the config file", e);
        } catch (SyntaxError e) {
            Logger.error("Config file malformed", e);
        }

        return false;
    }

    public static boolean writeConfig() {
        return writeConfig(false);
    }

    public static boolean writeConfig(boolean reset) {
        Logger.info("Saving config to ");
        try {
            if (!configFile.exists()) {
                configFile.createNewFile();
            }

            if (config == null || reset) {
                config = new MappingsConfig();
                config.mappings.add(new Mappings("example", "Example Mapping", MappingType.INTERMEDIARY, "example.tiny", MappingFormat.TINY_2_FILE));
            }

            String result = jankson.toJson(config).toJson(true, true);

            FileOutputStream out = new FileOutputStream(configFile, false);
            out.write(result.getBytes());
            out.flush();
            out.close();

            return true;
        } catch (IOException e) {
            Logger.error("Failed to save config", e);
        }
        return false;
    }
}
