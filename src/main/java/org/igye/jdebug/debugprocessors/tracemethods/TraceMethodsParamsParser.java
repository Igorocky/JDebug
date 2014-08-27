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
}
