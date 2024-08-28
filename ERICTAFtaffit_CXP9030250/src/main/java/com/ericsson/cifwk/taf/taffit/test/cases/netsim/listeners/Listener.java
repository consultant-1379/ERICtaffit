package com.ericsson.cifwk.taf.taffit.test.cases.netsim.listeners;

import com.ericsson.cifwk.taf.taffit.test.cases.netsim.NetsimInformation;
import org.testng.ISuite;
import org.testng.ISuiteListener;

public class Listener implements ISuiteListener {

    @Override
    public void onStart(ISuite suite) {
        new NetsimInformation().setUp();
    }

    @Override
    public void onFinish(ISuite suite) {

    }
}
