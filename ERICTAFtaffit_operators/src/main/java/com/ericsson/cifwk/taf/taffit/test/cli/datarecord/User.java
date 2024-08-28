package com.ericsson.cifwk.taf.taffit.test.cli.datarecord;

import com.ericsson.cifwk.taf.datasource.DataRecord;

public interface User extends DataRecord {
    String getUsername();
    String getPassword();
}
