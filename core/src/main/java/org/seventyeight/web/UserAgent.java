package org.seventyeight.web;

import java.util.regex.Pattern;

/**
 *
 * http://www.useragentstring.com/pages/useragentstring.php
 *
 * @author cwolfgang
 */
public class UserAgent {

    public enum Model {
        InternetExplorer,
        Firefox,
        Chrome,
        Unknown
    }

    public enum Platform {
        Windows("Windows", "Generic"),
        Windows95("Windows", "95"),
        Windows98("Windows", "98"),
        WindowsNT("Windows", "NT"),
        WindowsXP("Windows", "XP"),
        Windows2K("Windows", "2K"),
        WindowsVista("Windows", "Vista"),
        Windows7("Windows", "7"),
        Windows8("Windows", "8"),
        Windows81("Windows", "8.1"),

        Linux("Linux", "Generic"),
        RedHat("Linux", "Red Hat"),
        Fedora("Linux", "Fedora"),
        Ubuntu("Linux", "Ubuntu"),
        Debian("Linux", "Debian"),

        MacOs("Mac", "Mac OS X"),
        IPhone("IOS", "IPhone"),
        IPad("IOS", "IPad"),
        IPod("IOS", "IPod"),
        Android("Android", "Android"),
        Unknown("Unknown", "Unknown");

        private String flavor;
        private String name;

        private Platform(String name, String flavor) {
            this.name = name;
            this.flavor = flavor;
        }

    }

    public static final UserAgent unknownAgent = new UserAgent( Model.Unknown, "0", Platform.Unknown );

    private static final String IE = "MSIE";
    private static final String FIREFOX = "Firefox";

    private Model model;
    private String userAgentString;
    private String version;
    private Platform platform;

    private UserAgent(Model model, String version, Platform platform) {
        this.model = model;
        this.version = version;
        this.platform = platform;
    }

    public UserAgent() {

    }

    public Model getModel() {
        return model;
    }

    public String getVersion() {
        return version;
    }

    public Platform getPlatform() {
        return platform;
    }

    public static UserAgent getUserAgent(String str) {
        if(str == null || str.isEmpty()) {
            return unknownAgent;
        }

        UserAgent agent = new UserAgent();

        if(str.contains( IE )) {
            agent.model = Model.InternetExplorer;
            int vi = str.indexOf( IE ) + IE.length() + 1;
            agent.version = str.substring( vi, str.indexOf( ";", vi ) );
        } else if(str.contains( FIREFOX )) {
            agent.model = Model.Firefox;
            int vi = str.indexOf( FIREFOX + "/" ) + FIREFOX.length() + 1;
            if(vi < 0 ) {
                agent.version = "0";
            } else {
                agent.version = str.substring( vi, ( str.indexOf( " ", vi ) > 0 ? str.indexOf( " ", vi ) : str.length() ) );
            }
        }

        agent.platform = getPlatform( str );
        agent.userAgentString = str;

        return agent;
    }

    public static Platform getPlatform(String str) {
        if(str == null) {
            return Platform.Unknown;
        } else if(str.contains( "Windows 95" )) {
            return Platform.Windows95;
        } else if(str.contains( "Windows 98" )) {
            return Platform.Windows98;
        } else if(str.contains( "Windows NT 5.0" )) {
            return Platform.Windows2K;
        } else if(str.contains( "Windows NT 5.1" ) || str.contains( "Windows NT 5.2" ) || str.contains( "Windows XP" )) {
            return Platform.WindowsXP;
        } else if(str.contains( "Windows NT 6.0" )) {
            return Platform.WindowsVista;
        } else if(str.contains( "Windows NT 6.1" )) {
            return Platform.Windows7;
        } else if(str.contains( "Windows NT 6.2" )) {
            return Platform.Windows8;
        } else if(str.contains( "Windows NT 6.3" )) {
            return Platform.Windows81;
        } else if(str.contains( "Windows NT" )) {
            return Platform.WindowsNT;
        } else if(str.contains( "Windows" )) {
            return Platform.Windows;
        } else if(str.contains( "Linux" )) {
            if(str.contains( "Ubuntu" )) {
                return Platform.Ubuntu;
            } else if(str.contains( "Fedora" )) {
                return Platform.Fedora;
            } else if(str.contains( "Red Hat" )) {
                return Platform.RedHat;
            } else if(str.contains( "Debian" )) {
                return Platform.Debian;
            } else {
                return Platform.Linux;
            }
        } else if(str.contains( "Mac OS X" )) {
            return Platform.MacOs;
        } else if(str.contains( "IPod" )) {
            return Platform.IPod;
        } else if(str.contains( "IPad" )) {
            return Platform.IPad;
        } else if(str.contains( "IPhone" )) {
            return Platform.IPhone;
        } else if(str.contains( "Android" )) {
            return Platform.Android;
        } else {
            return Platform.Unknown;
        }
    }
}
