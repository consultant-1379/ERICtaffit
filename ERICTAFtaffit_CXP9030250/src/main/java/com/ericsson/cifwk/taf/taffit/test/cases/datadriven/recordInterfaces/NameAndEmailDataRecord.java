package com.ericsson.cifwk.taf.taffit.test.cases.datadriven.recordInterfaces;

import com.ericsson.cifwk.taf.datasource.DataRecord;

public interface NameAndEmailDataRecord extends DataRecord {

    String getName();

    String getEmail();
}
