/*
 * Copyright (c) 2022. Drapuria
 */

package net.drapuria.framework.database.credentials;

import lombok.Getter;
import net.drapuria.framework.configuration.yaml.annotation.ConfigurationElement;

@ConfigurationElement
@Getter
public class SQLCredentials {


    private String host;
    private String port;

    private String username;
    private String password;
    private String database;

    public SQLCredentials() {
        this("", "", "", "", "");
    }

    public SQLCredentials(String host, String port, String username, String password, String database) {
        this.host = host;
        this.port = port;
        this.username = username;
        this.password = password;
        this.database = database;
    }
}
