package at.falb.games.alcatraz.api.utilities;

import java.net.URL;

public class CommonValues {
    public static final String javaSecurityPolicyKey = "java.security.policy";
    public static final String javaRmiServerHostname = "java.rmi.server.hostname";
    public static final URL RESOURCE = CommonValues.class.getClassLoader().getResource("rmi.policy");
}
