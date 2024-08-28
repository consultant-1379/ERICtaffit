package com.ericsson.cifwk.taf.tools.cli;

import com.ericsson.cifwk.taf.configuration.TafDataHandler;
import org.junit.Rule;
import org.junit.rules.ExpectedException;
import org.testng.annotations.Test;

import static java.lang.String.format;

public class CliCommandHelperTimeoutTest {

    private CLICommandHelper cli = new CLICommandHelper(TafDataHandler.findHost().withHostname("netsim").get());
    private static final int TIMEOUT = 2;
    private static final String NON_EXISTING_MATCH_PATTERN = "non-existing-match-pattern";

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void executeThrowHumanReadableTimeoutExceptionMessageForExitCode() {
        thrown.expect(TimeoutException.class);
        thrown.expectMessage(format("Unable to retrieve command exit code. Possibly command was not completed in %s seconds", TIMEOUT));

        cli.execute("vi texttest.txt", TIMEOUT);
    }

    @Test
    public void expectShouldThrowDefaultMatchMessage() {
        thrown.expect(TimeoutException.class);
        thrown.expectMessage(format("Timeout trying to match '%s'", NON_EXISTING_MATCH_PATTERN));

        cli.write("ls");
        cli.expect(NON_EXISTING_MATCH_PATTERN);
    }

}
