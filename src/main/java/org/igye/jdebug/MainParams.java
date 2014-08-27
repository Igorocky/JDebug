package org.igye.jdebug;


import com.beust.jcommander.Parameter;

public class MainParams {
    @Parameter(names = "-h", description = "Remote host to attach to")
    private String host = "localhost";

    @Parameter(names = "-p", description = "Remote port to attach to", required = true)
    private Integer port;

    public String getHost() {
        return host;
    }

    public Integer getPort() {
        return port;
    }
}
