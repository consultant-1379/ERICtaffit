package com.ericsson.cifwk.taf.taffit.test.cases.netsim;

import com.ericsson.cifwk.taf.annotations.TestId;
import com.ericsson.cifwk.taf.data.DataHandler;
import com.ericsson.cifwk.taf.data.Host;
import com.ericsson.cifwk.taf.handlers.netsim.domain.NeGroup;
import com.ericsson.cifwk.taf.handlers.netsim.domain.NetworkElement;
import com.ericsson.cifwk.taf.taffit.test.operators.netsim.NetsimOperatorImpl;
import com.ericsson.cifwk.taf.tools.cli.CLI;
import com.ericsson.cifwk.taf.tools.cli.Shell;
import com.ericsson.cifwk.taf.tools.cli.Terminal;
import com.google.common.base.Preconditions;
import org.apache.commons.lang3.ArrayUtils;
import org.assertj.core.util.VisibleForTesting;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

public class NetsimInformation {

    NetsimOperatorImpl netsimOperator = new NetsimOperatorImpl();

    private static Host host = DataHandler.getHostByName("netsim");

    static String SIMULATION_FOR_START_STOP_ALL;
    static int TOTAL_NO_OF_OPEN_SIMULATIONS;
    static int TOTAL_NO_OF_STARTED_NES;
    static String ALT_SIM_NAME;
    static int TOTAL_NO_OF_NES;

    private static final String openCommand = "echo -e ' .open ";
    private static final String netsimPipeCommand = " ' | /netsim/inst/netsim_pipe";
    private static final String showSimnesCommand = ".show simnes" + netsimPipeCommand;
    static final String showSimnes = ".show simnes";
    private static final String showSimulations = ".show simulations";
    private static final String showSimulationsCommand = "echo -e ' " + showSimulations + netsimPipeCommand;
    private static final String showNumStartedNEs = ".show numstartednes";
    private static final String showNumStartedNEsCommand = "echo -e '" + showNumStartedNEs + netsimPipeCommand;

    private static String[] simsWith4NEs = new String[2];
    private static Map<String, String[]> simsWith4NEsAndNodes = new HashMap<>();
    private static String[] simsWith2NEs = new String[2];

    private static Map<String, String[]> simsWith2NEsAndNodes = new HashMap<>();

    static Map<String, String[]> getSimsWith4NEsAndNodes() {
        return simsWith4NEsAndNodes;
    }

    private static String[] getSimsWith4NEs() {
        return simsWith4NEs;
    }

    static Map<String, String[]> getSimsWith2NEsAndNodes() {
        return simsWith2NEsAndNodes;
    }

    public void setUp() {
        populateDataSources();
        populateVariables();
    }

    @Test
    @TestId(id = "TAF_CLI_Tool_Func_025_2")
    public void getNetsimInfoTest() throws Exception {
        setUp();
        CLI cli = new CLI(host);
        String sim = getSimsWith4NEs()[0];
        String getNEsFromSimCommand = "echo -e ' .open " + sim + "\n" +
                ".show simnes \n" +
                " ' | /netsim/inst/netsim_pipe";
        Shell shell = cli.executeCommand(Terminal.XTERM, getNEsFromSimCommand);
        String result = shell.read();
        assertThat(parseElements(result, showSimnes).length).isEqualTo(4);
        shell.disconnect();
        cli.close();
    }

    private void populateDataSources() {
        CLI cli = new CLI(host);
        Shell shell = cli.executeCommand(Terminal.XTERM, showSimulationsCommand);
        String simulationResults = shell.read();
        boolean simsWith4NEsIsFull;
        int elementsWith4NEsFilled = 0;
        boolean simsWith2NEsIsFull;
        int elementsWith2NEsFilled = 0;

        String[] sims = parseElements(simulationResults, showSimulations);
        for (String sim : sims) {
            if (elementsWith4NEsFilled < simsWith4NEs.length && getNECountFromSimulation(sim, cli) == 4) {
                simsWith4NEs[elementsWith4NEsFilled] = sim;
                NeGroup neGroup = netsimOperator.getNEsFromSimulation(sim);
                String[] neNames = new String[neGroup.size()];
                int neNamesLocation = 0;
                for(NetworkElement ne : neGroup.getNetworkElements()) {
                    neNames[neNamesLocation] = ne.getName();
                    neNamesLocation++;
                }
                simsWith4NEsAndNodes.put(sim, neNames);
                elementsWith4NEsFilled++;
            }
            if (elementsWith2NEsFilled < simsWith2NEs.length && getNECountFromSimulation(sim, cli) == 2) {
                simsWith2NEs[elementsWith2NEsFilled] = sim;
                NeGroup neGroup = netsimOperator.getNEsFromSimulation(sim);
                String[] neNames = new String[neGroup.size()];
                int neNamesLocation = 0;
                for(NetworkElement ne : neGroup.getNetworkElements()) {
                    neNames[neNamesLocation] = ne.getName();
                    neNamesLocation++;
                }
                simsWith2NEsAndNodes.put(sim, neNames);
                elementsWith2NEsFilled++;
            }
            simsWith4NEsIsFull = elementsWith4NEsFilled >= simsWith4NEs.length;
            simsWith2NEsIsFull = elementsWith2NEsFilled >= simsWith2NEs.length;
            if (simsWith4NEsIsFull && simsWith2NEsIsFull) {
                break;
            }
        }

        shell.disconnect();
        cli.close();
    }

    private void populateVariables() {
        CLI cli = new CLI(host);
        SIMULATION_FOR_START_STOP_ALL = simsWith2NEs[0];
        ALT_SIM_NAME = simsWith2NEs[1];
        TOTAL_NO_OF_OPEN_SIMULATIONS = getSimulationsCount(cli);
        TOTAL_NO_OF_NES = getAllNEs();
        TOTAL_NO_OF_STARTED_NES = getNumberStartedNEs(cli);
        cli.close();
    }

    private static int getNECountFromSimulation(String simulation, CLI cli) {
        Preconditions.checkNotNull(simulation, "'simulation' is undefined");
        String getNEsFromSimCommand = openCommand + simulation + " \n " + showSimnesCommand;
        Shell shell = cli.executeCommand(Terminal.XTERM, getNEsFromSimCommand);
        String res = shell.read();
        if (res.contains("Number of parameters shall be 1")) {
            return 0;
        }
        shell.disconnect();
        return parseElements(res, showSimnes).length;
    }

    private static int getNumberStartedNEs(CLI cli) {
        Shell shell = cli.executeCommand(Terminal.XTERM, showNumStartedNEsCommand);
        String output = shell.read();
        String[] parsedOutput = parseElements(output, showNumStartedNEs);
        int startedNEsSize = 0;
        for(String singleParsedOutput : parsedOutput) {
            try {
                startedNEsSize = Integer.parseInt(singleParsedOutput);
                break;
            } catch (NumberFormatException nfe) {}
        }
        shell.disconnect();
        return startedNEsSize;
    }

    private static int getSimulationsCount(CLI cli) {
        Shell shell = cli.executeCommand(Terminal.XTERM, showSimulationsCommand);
        String res = shell.read();
        shell.disconnect();
        return parseElements(res, showSimulations).length;
    }

    private int getAllNEs() {
        int neLength = netsimOperator.getAllNEs().size();
        return neLength;
    }

    @VisibleForTesting
    static String[] parseElements(String resultToParse, String command) {
        Preconditions.checkNotNull(resultToParse, "'resultToParse' is undefined");
        Preconditions.checkNotNull(command, "'command' is undefined");
        if (resultToParse.contains("There are no simnes"))
            return ArrayUtils.EMPTY_STRING_ARRAY;
        if (resultToParse.contains(command))
            resultToParse = resultToParse.substring(resultToParse.lastIndexOf(command) + command.length());
        if (resultToParse.contains("Default dest."))
            resultToParse = resultToParse.substring(resultToParse.lastIndexOf("Default dest.") + 15, resultToParse.lastIndexOf("OK"));

        String[] res = resultToParse.split("\r\n|\r|\n");
        List<String> results = new ArrayList<>();
        for (String str : res) {
            str = str.trim();
            if (str != null && filterString(str)) {
                if (str.contains(" ")) {
                    str = str.substring(0, str.indexOf(" "));
                    results.add(str);
                } else {
                    results.add(str);
                }
            }
        }
        String[] resultArray = results.toArray(new String[results.size()]);
        return resultArray;
    }

    private static boolean filterString(String str) {
        if (str.startsWith("'server") || str.startsWith(" 'server") || str.startsWith("==========") || str.contains("NE") || str.contains("END")) {
            return false;
        }
        return str.length()>1;
    }
}
