package com.ericsson.cifwk.taf.taffit.test.operators.netsim;

import java.util.List;
import java.util.Map;

import com.ericsson.cifwk.taf.handlers.netsim.NetSimCommand;
import com.ericsson.cifwk.taf.handlers.netsim.NetSimResult;
import com.ericsson.cifwk.taf.handlers.netsim.domain.Simulation;
import com.ericsson.cifwk.taf.handlers.netsim.domain.NeGroup;
import com.ericsson.cifwk.taf.handlers.netsim.domain.NetworkElement;
import com.ericsson.cifwk.taf.handlers.netsim.domain.SimulationGroup;

public interface NetsimOperator {
    /**
     * Get all the simulation present ot Netsim
     * 
     * @return
     */
    List<Simulation> getAllSimulations();

    /**
     * Lists all the NEs present in a simulation
     * 
     * @param simulation
     * @return
     */
    NeGroup getNEsFromSimulation(String... simulation);

    /**
     * Starts a NE from the simulation
     * 
     * @param simulation
     * @param neName
     * @return
     */
    boolean startNe(String simulation, String neName);

    /**
     * Stops a NE from the simulation
     * 
     * @param simulation
     * @param neName
     * @return
     */
    boolean stopNe(String simulation, String neName);

    /**
     * Stop all NEs on this simulation
     */
    boolean stopAllNEs(String simulation);

    /**
     * Gets all the started NEs from a simulation
     * @param simName
     * @return
     */
    List<NetworkElement> getSimulationsStartedNEs(String simName);

    /**
     * Start all NEs on this simulation
     * @param simulation
     * @return
     */
    boolean startAllNEs(String simulation);

    /**
     * Execute multiple netsim commands on a NE of the simulation
     * 
     * @param simulation
     * @param neName
     * @param netSimCommands
     * @return
     */
    NetSimResult executeCommandsOnNE(String simulation, String neName, NetSimCommand... netSimCommands);

    /**
     * Execute multiple netsim commands on all NEs in a simulation
     * 
     * @param simulation
     * @param netSimCommands
     * @return
     */
    Map<NetworkElement, NetSimResult> executeCommandsOnSimulation(String simulation, NetSimCommand... netSimCommands);

    /**
     * @param simulation
     * @param neName
     * @return
     */
    NetworkElement getSingleNEsFromSimulation(String simulation, String neName);

    /**
     * @param simulation
     * @param neGroup
     * @param netSimCommands
     * @return
     */
    Map<NetworkElement, NetSimResult> executeCommandsOnNEGroup(String simulation, NeGroup neGroup, NetSimCommand... netSimCommands);

    /**
     * @return
     */
    List<Simulation> getSimulations();

    /**
     * @param simulations
     * @return
     */
    SimulationGroup getSimulations(String... simulations);

    /**
     * @param netSimCommands
     * @return
     */
    NetSimResult executeCommandsWithSession(NetSimCommand... netSimCommands);

    /**
     * @param timeout
     * @param simulation
     * @param netSimCommands
     * @return
     */
    NetSimResult executeCommandsWithSession(int timeout, NetSimCommand... netSimCommands);

    /**
     * @param commandList
     * @return
     */
    NetSimResult executeCommandsWithSession(List<NetSimCommand> commandList);

    /**
     * @param timeout
     * @param commandList
     * @return
     */
    NetSimResult executeCommandsWithSession(int timeout, List<NetSimCommand> commandList);

    /**
     * @param netSimCommands
     * @return
     */
    NetSimResult executeCommandsWithContext(NetSimCommand... netSimCommands);

    /**
     * @param simulation
     * @param neName
     * @return
     */
    boolean isStarted(String simulation, String neName);

    void closeSession();

    /**
     * @return
     */
    List<NetworkElement> getAllNEs();

    /**
     * @return
     */
    NeGroup getAllStartedNEs();

    /**
     * @return
     */
    boolean isNetSimRunning();

}
