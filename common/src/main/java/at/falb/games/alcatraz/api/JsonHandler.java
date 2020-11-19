package at.falb.games.alcatraz.api;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class JsonHandler {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    private JsonHandler() {
    }

    public static ServerCfg readServerJson(String serverName) throws IOException {
        final TypeReference<List<ServerCfg>> mapTypeReference = new TypeReference<>() {
        };
        final List<ServerCfg> serverCfgList = jsonToObject("server.json", mapTypeReference);
        final Optional<ServerCfg> optionalServer = serverCfgList
                .stream()
                .filter(s -> s.getName().equals(serverName))
                .findAny();
        assert optionalServer.isPresent() : "This " + serverName + " is not known";

        ServerClientUtility.getServerCfgList().addAll(serverCfgList);
        return optionalServer.get();
    }

    public static ClientCfg readClientJson(String clientName, String serverName) throws IOException {
        final TypeReference<List<ClientCfg>> mapTypeReference = new TypeReference<>() {
        };
        final ServerCfg serverCfg = readServerJson(serverName);
        final List<ClientCfg> clientCfgList = jsonToObject("client.json", mapTypeReference);
        final Optional<ClientCfg> optionalClient = clientCfgList
                .stream()
                .filter(c -> c.getName().equals(clientName))
                .findAny();
        assert optionalClient.isPresent() : "This " + clientName + " is not known";

        ServerClientUtility.getClientCfgList().addAll(clientCfgList);
        final ClientCfg clientCfg = optionalClient.get();
        clientCfg.setServerCfg(serverCfg);
        return clientCfg;
    }

    private static <T> T jsonToObject(String fileName, TypeReference<T> mapTypeReference) throws IOException {
        InputStream in = JsonHandler.class.getClassLoader().getResourceAsStream(fileName);
        assert in != null;
        final InputStreamReader reader = new InputStreamReader(in, StandardCharsets.UTF_8);
        final BufferedReader buffer = new BufferedReader(reader);
        String text = buffer.lines().collect(Collectors.joining("\n"));
        buffer.close();
        reader.close();
        in.close();

        return objectMapper.readValue(text, mapTypeReference);
    }
}
