package at.falb.games.alcatraz.api.logic;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class YamlHandler {
    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static final List<ServerCfg> serverCfgList = new ArrayList<>();

    private YamlHandler() {
    }

    public static ServerCfg readYaml(String serverName) throws IOException {
        InputStream in = YamlHandler.class.getClassLoader().getResourceAsStream("server.json");
        assert in != null;
        final InputStreamReader reader = new InputStreamReader(in, StandardCharsets.UTF_8);
        final BufferedReader buffer = new BufferedReader(reader);
        String text = buffer.lines().collect(Collectors.joining("\n"));
        buffer.close();
        reader.close();
        in.close();

        final TypeReference<List<ServerCfg>> mapTypeReference = new TypeReference<>() {
        };
        final List<ServerCfg> serverCfgList = objectMapper.readValue(text, mapTypeReference);
        int index = serverCfgList.indexOf(new ServerCfg(serverName));
        final ServerCfg serverCfg = serverCfgList.get(index);
        serverCfgList.remove(index);
        YamlHandler.serverCfgList.addAll(serverCfgList);
        return serverCfg;
    }

    // TODO: This will be removed with the new implementation in develop
    public static List<ServerCfg> getServerCfgList() {
        return serverCfgList;
    }
}
