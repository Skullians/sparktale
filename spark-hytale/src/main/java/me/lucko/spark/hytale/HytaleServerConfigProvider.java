package me.lucko.spark.hytale;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.hypixel.hytale.server.core.universe.Universe;
import com.hypixel.hytale.server.core.universe.world.World;
import me.lucko.spark.bukkit.BukkitServerConfigProvider;
import me.lucko.spark.common.platform.serverconfig.ConfigParser;
import me.lucko.spark.common.platform.serverconfig.ExcludedConfigFilter;
import me.lucko.spark.common.platform.serverconfig.ServerConfigProvider;

import java.io.BufferedReader;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;


public class HytaleServerConfigProvider extends ServerConfigProvider {

    private static final Map<String, ConfigParser> FILES;
    private static final Collection<String> HIDDEN_PATHS;

    public HytaleServerConfigProvider() {
        super(FILES, HIDDEN_PATHS);
    }
    
    private static class JsonConfigParser implements ConfigParser {
        public static final JsonConfigParser INSTANCE = new JsonConfigParser();
        protected static final Gson GSON = new Gson();
        
        private static final Type MAP_TYPE = new TypeToken<Map<String, Object>>() {}.getType();

        @Override
        public JsonElement load(String file, ExcludedConfigFilter filter) throws IOException {
            Map<String, Object> values = this.parse(Paths.get(file));
            if (values == null) return null;
            
            return filter.apply(GSON.toJsonTree(values));
        }

        @Override
        public Map<String, Object> parse(BufferedReader reader) throws IOException {
            return GSON.fromJson(reader, MAP_TYPE);
        }
    }
    
    private static class WorldJsonConfigParser extends JsonConfigParser {

        @Override
        public JsonElement load(String folder, ExcludedConfigFilter filter) throws IOException {
            Path directory = Paths.get(folder);
            if (!Files.exists(directory)) return null;

            JsonObject root = new JsonObject();
            for (Map.Entry<String, Path> entry : getWorldFiles(directory).entrySet()) {
                String fileName = entry.getKey();
                Path path = entry.getValue();

                Map<String, Object> values = this.parse(path);
                if (values == null) {
                    continue;
                }

                // apply the filter individually to each nested file
                root.add(fileName, filter.apply(GSON.toJsonTree(values)));
            }
            
            return root;
        }
        
        private static Map<String, Path> getWorldFiles(Path worldDir) {
            Map<String, Path> files = new LinkedHashMap<>();
            for (Map.Entry<String, World> entry : Universe.get().getWorlds().entrySet()) {
                files.put(entry.getKey() + ".json", entry.getValue().getSavePath().resolve("config.json"));
            }
            
            return files;
        }
    }
    
    static {
        ImmutableMap.Builder<String, ConfigParser> files = ImmutableMap.<String, ConfigParser>builder()
                .put("config.json", JsonConfigParser.INSTANCE)
                .put("worlds/", WorldJsonConfigParser.INSTANCE);

        for (String config : getSystemPropertyList("spark.serverconfigs.extra")) {
            files.put(config, WorldJsonConfigParser.INSTANCE);
        }

        ImmutableSet.Builder<String> hiddenPaths = ImmutableSet.<String>builder()
                .addAll(BASE_HIDDEN_PATHS)
                .add("Password")
                .addAll(getSystemPropertyList("spark.serverconfigs.hiddenpaths"));

        FILES = files.build();
        HIDDEN_PATHS = hiddenPaths.build();
    }
}
