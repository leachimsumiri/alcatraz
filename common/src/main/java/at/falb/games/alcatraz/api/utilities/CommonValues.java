package at.falb.games.alcatraz.api.utilities;

import java.net.URL;

public class CommonValues {
    public static final String JAVA_SECURITY_POLICY_KEY = "java.security.policy";
    public static final String JAVA_RMI_SERVER_HOSTNAME = "java.rmi.server.hostname";
    public static final URL RESOURCE = CommonValues.class.getClassLoader().getResource("rmi.policy");
}
