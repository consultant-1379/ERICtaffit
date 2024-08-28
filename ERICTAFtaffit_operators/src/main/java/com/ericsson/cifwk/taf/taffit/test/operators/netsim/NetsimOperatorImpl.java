package com.ericsson.cifwk.taf.taffit.test.operators.netsim;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ericsson.cifwk.taf.data.DataHandler;
import com.ericsson.cifwk.taf.data.Host;
import com.ericsson.cifwk.taf.data.Ports;
import com.ericsson.cifwk.taf.handlers.netsim.NetSimCommandHandler;
import com.ericsson.cifwk.taf.handlers.netsim.NetSimContext;
import com.ericsson.cifwk.taf.handlers.netsim.NetSimSession;
import com.ericsson.cifwk.taf.handlers.netsim.NetSimResult;
import com.ericsson.cifwk.taf.handlers.netsim.NetSimCommand;
import com.ericsson.cifwk.taf.handlers.netsim.domain.NeGroup;
import com.ericsson.cifwk.taf.handlers.netsim.domain.NetworkElement;
import com.ericsson.cifwk.taf.handlers.netsim.domain.Simulation;
import com.ericsson.cifwk.taf.handlers.netsim.domain.SimulationGroup;
import com.google.inject.Singleton;

@Singleton
public class NetsimOperatorImpl implements NetsimOperator {

    Host host;

    NetSimCommandHandler service;
    NetSimContext contextService;
    NetSimSession sessionService;

    Logger log = LoggerFactory.getLogger(NetsimOperatorImpl.class);

    public NetsimOperatorImpl() {
        host = DataHandler.getHostByName("netsim");
        log.debug("Host IP {}", host.getIp());
        log.debug("Host port {}", host.getPort(Ports.SSH));
        initializeNetsimHandler(host);

    }

    public void initializeNetsimHandler(Host host) {
        service = NetSimCommandHandler.getInstance(host);
    }

    @Override
    public List<Simulation> getAllSimulations() {
        SimulationGroup simulationGroup = service.getAllSimulations();
        List<Simulation> simulations = simulationGroup.getSimulations();

        for (Simulation simulation : simulations) {
            log.info(simulation.getName());
        }
        return simulations;
    }

    @Override
    public SimulationGroup getSimulations(String... simulations) {
        return service.getSimulations(simulations);
    }

    @Override
    public NeGroup getNEsFromSimulation(String... simulations) {
        return service.getSimulationNEs(simulations);
    }

    @Override
    public List<NetworkElement> getAllNEs() {
        SimulationGroup simulationGroup = service.getAllSimulations();
        return simulationGroup.getAllNEs();
    }

    @Override
    public NeGroup getAllStartedNEs() {
        return service.getAllStartedNEs();
    }

    @Override
    public NetworkElement getSingleNEsFromSimulation(String simulation, String neName) {
        NeGroup neGroup = service.getSimulationNEs(simulation);
        return neGroup.get(neName);
    }

    @Override
    public boolean startNe(String simulation, String neName) {
        NeGroup neGroup = service.getSimulationNEs(simulation);
        NetworkElement ne = neGroup.get(neName);
        return ne.start();
    }

    @Override
    public boolean isStarted(String simulation, String neName) {
        NetworkElement ne;
        boolean isStarted;
        try{
            NeGroup neGroup = service.getSimulationNEs(simulation);
            ne = neGroup.get(neName);
            isStarted = ne.isStarted();
        }catch(NullPointerException e){
            log.info("NE does not exist");
            return false;
        }
        return isStarted;
    }

    @Override
    public boolean stopNe(String simulation, String neName) {
        log.debug("Simulation {}", simulation);
        log.debug("NE {}", neName);
        NeGroup neGroup = service.getSimulationNEs(simulation);
        log.debug("neGroup {}", neGroup.size());
        NetworkElement ne = neGroup.get(neName);
        return ne.stop();
    }

    @Override
    public boolean stopAllNEs(final String simulation) {
        Simulation sim = service.getSimulations(simulation).get(simulation);
        return sim.stopAllStartedNEs();
    }

    @Override
    public List<NetworkElement> getSimulationsStartedNEs(final String simulation) {
        Simulation sim = service.getSimulations(simulation).get(simulation);
        return sim.getStartedNEs();
    }

    @Override
    public boolean startAllNEs(final String simulation) {
        Simulation sim = service.getSimulations(simulation).get(simulation);
        return sim.startAllNEs();
    }

    @Override
    public NetSimResult executeCommandsOnNE(String simulation, String neName, NetSimCommand... netSimCommands) {
        NeGroup neGroup = service.getSimulationNEs(simulation);
        NetworkElement ne = neGroup.get(neName);
        return ne.exec(netSimCommands);
    }

    @Override
    public Map<NetworkElement, NetSimResult> executeCommandsOnNEGroup(String simulation, NeGroup neGroup, NetSimCommand... netSimCommands) {
        return neGroup.exec(netSimCommands);
    }

    @Override
    public Map<NetworkElement, NetSimResult> executeCommandsOnSimulation(String simulation, NetSimCommand... netSimCommands) {
        NeGroup neGroup = service.getSimulationNEs(simulation);
        return neGroup.exec(netSimCommands);
    }

    @Override
    public NetSimResult executeCommandsWithContext(NetSimCommand... netSimCommands) {
        contextService = NetSimCommandHandler.getContext(host);
        return contextService.exec(netSimCommands);
    }

    @Override
    public NetSimResult executeCommandsWithSession(NetSimCommand... netSimCommands) {
        sessionService = NetSimCommandHandler.getSession(host);
        return sessionService.exec(netSimCommands);
    }

    @Override
    public List<Simulation> getSimulations() {
        return service.getAllSimulations().getSimulations();
    }

    @Override
    public NetSimResult executeCommandsWithSession(List<NetSimCommand> commandList) {
        if(sessionService == null || sessionService.isClosed()){
            sessionService = NetSimCommandHandler.getSession(host);
        }
        return sessionService.exec(commandList);
    }

    @Override
    public NetSimResult executeCommandsWithSession(int timeout, List<NetSimCommand> commandList) {
        if(sessionService == null || sessionService.isClosed()){
            sessionService = NetSimCommandHandler.getSession(host);
        }
        return sessionService.exec(timeout, commandList);
    }

    @Override
    public NetSimResult executeCommandsWithSession(int timeout, NetSimCommand... netSimCommands) {
        if(sessionService == null || sessionService.isClosed()){
            sessionService = NetSimCommandHandler.getSession(host);
        }
        return sessionService.exec(timeout, netSimCommands);
    }

    @Override
    public void closeSession() {
        if(sessionService != null && !sessionService.isClosed()){
            sessionService.close();
        }
    }

    @Override
    public boolean isNetSimRunning() {
        contextService = NetSimCommandHandler.getContext(host);
        return contextService.isNetSimRunning();
    }
}
