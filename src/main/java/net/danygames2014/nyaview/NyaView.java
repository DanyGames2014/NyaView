package net.danygames2014.nyaview;

import net.danygames2014.nyaview.config.Config;
import net.danygames2014.nyaview.config.MappingsConfig;
import net.danygames2014.nyaview.mapping.MappingLoader;
import net.danygames2014.nyaview.mapping.Mappings;

import java.util.ArrayList;
import java.util.Scanner;

import static net.danygames2014.nyaview.util.Search.*;


public class NyaView {
    public static MappingsConfig config;
    //    public static final Logger LOGGER = LoggerFactory.getLogger("NyaView");
    public static MappingLoader loader = new MappingLoader();
    public static boolean run = true;

    public static void main(String[] args) {
//        config = new MappingsConfig();
//        config.mappings.add(new Mappings("babric", "Babric", MappingType.INTERMEDIARY, "babric.tiny", MappingFormat.TINY_2_FILE));
//        config.mappings.add(new Mappings("ornithe", "Ornithe", MappingType.INTERMEDIARY, "ornithe.tiny", MappingFormat.TINY_2_FILE));
//        config.mappings.add(new Mappings("mcp-client", "MCP Client", MappingType.MCP_CLIENT, "mcp-client.tiny", MappingFormat.TINY_2_FILE));
//        config.mappings.add(new Mappings("mcp-server", "MCP Server", MappingType.MCP_SERVER, "mcp-server.tiny", MappingFormat.TINY_2_FILE));
//        config.mappings.add(new Mappings("biny", "BINY", MappingType.BABRIC, "biny", MappingFormat.ENIGMA_DIR));
//        config.mappings.add(new Mappings("bin", "BIN", MappingType.BABRIC, "bin", MappingFormat.ENIGMA_DIR));
//        config.mappings.add(new Mappings("nya", "NYA", MappingType.BABRIC, "nya", MappingFormat.ENIGMA_DIR));
//        config.mappings.add(new Mappings("bin-bh", "BIN-BH", MappingType.BABRIC, "bin-bh", MappingFormat.ENIGMA_DIR));
//        config.mappings.add(new Mappings("feather", "Feather", MappingType.BABRIC, "feather", MappingFormat.ENIGMA_DIR));
//        Config.writeConfig();
        Config.readConfig();

        for (Mappings item : config.mappings) {
            loader.load(item);
        }

        Scanner sc = new Scanner(System.in);
        while (run) {
            String input = sc.nextLine();
            SearchParameters parameters = parseSearchQuery(input);
            if (parameters == null) {
                System.err.println("Invalid Search Query");
                continue;
            }

            System.out.println(parameters);

            ArrayList<String> result = search(parameters);
            if(result.isEmpty()){
                System.err.println("No Results");
            }else{
                for (var item : result) {
                    System.out.println(item);
                }
            }
        }
    }
}
