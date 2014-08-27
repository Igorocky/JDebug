package org.igye.jdebug.debugprocessors.tracemethods;


import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;

@Parameters(commandDescription = "Traces methods entries")
public class TraceMethodsParamsParser {
    @Parameter(names = "-o", description = "Directory to store results to", required = true)
    private String dirToStoreResultsTo;

    @Parameter(names = "-ohp", description = "Will append host and port to the output directory name")
    private boolean appendHostPort = false;

    @Parameter(names = "-odt", description = "Will append date and time to the output directory name")
    private boolean appendDateTime = false;

    @Parameter(names = "-cm", description = "Class match modifier. " +
            "Matches are limited to exact matches of the given class pattern " +
            "and matches of patterns that begin or end with '*'; " +
            "for example, \"*.Foo\" or \"java.*\".")
    private String classMatch;

    @Parameter(names = "-ce", description = "Class exclude modifier. " +
            "Matches are limited to exact matches of the given class pattern " +
            "and matches of patterns that begin or end with '*'; " +
            "for example, \"*.Foo\" or \"java.*\". " +
            "Several patterns may be set comma separated.")
    private String classExclude;

    public String getDirToStoreResultsTo() {
        return dirToStoreResultsTo;
    }

    public boolean isAppendHostPort() {
        return appendHostPort;
    }

    public boolean isAppendDateTime() {
        return appendDateTime;
    }

    public String getClassMatch() {
        return classMatch;
    }

    public String getClassExclude() {
        return classExclude;
    }
}
