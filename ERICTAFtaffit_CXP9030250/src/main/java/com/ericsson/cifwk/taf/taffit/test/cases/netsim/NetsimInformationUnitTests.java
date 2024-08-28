package com.ericsson.cifwk.taf.taffit.test.cases.netsim;

import org.junit.Test;

import static com.ericsson.cifwk.taf.taffit.test.cases.netsim.NetsimInformation.showSimnes;
import static org.assertj.core.api.Assertions.assertThat;

public class NetsimInformationUnitTests {

    @Test
    public void parseElementsTest() {
        String toParse =
                " 'server_00055_WCDMA_M-MGw_C1214-V1lim@netsim' for WCDMA M-MGw C1214-V1lim\n" +
                        "=================================================================\n" +
                        "    NE                       Address          Simulation/Commands \n" +
                        "\n" +
                        "'server_00054_WCDMA_RNC_V53303-tst@netsim' for WCDMA RNC V53303-tst\n" +
                        "=================================================================\n" +
                        "    NE                       Address          Simulation/Commands \n" +
                        "    RNCV533030002            192.168.101.200  /netsim/netsimdir/RNC-15B-UPGIND-V2x2-RNC100\n" +
                        "    RNCV533030001            192.168.101.199  /netsim/netsimdir/RNC-15B-UPGIND-V2x2-RNC100\n" +
                        "\n" +
                        "'server_00053_BB_ML_6691-1-1@netsim' for BB ML 6691-1-1\n" +
                        "=================================================================\n" +
                        "    NE                       Address          Simulation/Commands \n" +
                        "    CORE22ML6691-002         192.168.102.26 161 private,public,trap v1+v2+v3 .128.0.0.193.1.192.168.102.26 authPrivSHA1DES ericsson ericsson hmac_sha cbc_des admin_user ericsson no_value hmac_md5 none authPrivMD5DES ericsson ericsson hmac_md5 cbc_des oper_user ericsson ericsson hmac_md5 none view_user ericsson ericsson hmac_md5 none control_user ericsson ericsson hmac_md5 none authNoPrivMD5None ericsson no_value hmac_md5 none authNoPrivSHA1None ericsson ericsson hmac_sha none noAuthNoPriv ericsson ericsson none none  /netsim/netsimdir/ML6691-17Ax2-CORE22\n" +
                        "    CORE22ML6691-001         192.168.102.25 161 private,public,trap v1+v2+v3 .128.0.0.193.1.192.168.102.25 authPrivSHA1DES ericsson ericsson hmac_sha cbc_des admin_user ericsson no_value hmac_md5 none authPrivMD5DES ericsson ericsson hmac_md5 cbc_des oper_user ericsson ericsson hmac_md5 none view_user ericsson ericsson hmac_md5 none control_user ericsson ericsson hmac_md5 none authNoPrivMD5None ericsson no_value hmac_md5 none authNoPrivSHA1None ericsson ericsson hmac_sha none noAuthNoPriv ericsson ericsson none none  /netsim/netsimdir/ML6691-17Ax2-CORE22\n" +
                        "\n" +
                        "'server_00052_LTE_ERBS_Z9334-UPGINDV1_-IPv6@netsim' for LTE ERBS Z9334-UPGINDV1\n" +
                        "=================================================================\n" +
                        "    NE                       Address          Simulation/Commands \n" +
                        "    LTE95ERBS00002           2001:1b70:82a1:103::64:120 /netsim/netsimdir/LTEZ9334-G-UPGIND-V1-LTE95\n" +
                        "\n" +
                        "'server_00051_LTE_ERBS_Z9334-UPGINDV1@netsim' for LTE ERBS Z9334-UPGINDV1\n" +
                        "=================================================================\n" +
                        "    NE                       Address          Simulation/Commands \n" +
                        "    LTE95ERBS00001           192.168.101.57   /netsim/netsimdir/LTEZ9334-G-UPGIND-V1-LTE95\n" +
                        "\n" +
                        "'server_00050_LTE_MSRBS-V2_16B-V13@netsim' for LTE MSRBS-V2 16B-V13\n" +
                        "=================================================================\n" +
                        "    NE                       Address          Simulation/Commands \n" +
                        "    LTE06dg2ERBS00020        192.168.100.200 161 public v3+v2+v1 .128.0.0.193.1.192.168.100.200 mediation authpass privpass none none  [TLS] /netsim/netsimdir/LTE16B-V13x40-1.8K-DG2-FDD-LTE06\n" +
                        "\n" +
                        "'server_00049_LTE_MSRBS-V2_17-Q3-V2@netsim' for LTE MSRBS-V2 17-Q3-V2\n" +
                        "=================================================================\n" +
                        "    NE                       Address          Simulation/Commands \n" +
                        "    LTE04dg2ERBS00012        192.168.100.120 161 public v3+v2+v1 .128.0.0.193.1.192.168.100.120 mediation authpass privpass none none  [TLS] /netsim/netsimdir/LTE17-Q3-V2x40-1.8K-DG2-FDD-LTE04\n" +
                        "    LTE04dg2ERBS00011        192.168.100.119 161 public v3+v2+v1 .128.0.0.193.1.192.168.100.119 mediation authpass privpass none none  [TLS] /netsim/netsimdir/LTE17-Q3-V2x40-1.8K-DG2-FDD-LTE04\n" +
                        "\n" +
                        "'server_00048_LTE_MSRBS-V2_17A-V10@netsim' for LTE MSRBS-V2 17A-V10\n" +
                        "=================================================================\n" +
                        "    NE                       Address          Simulation/Commands \n" +
                        "    LTE01dg2ERBS00017        192.168.100.17 161 public v3+v2+v1 .128.0.0.193.1.192.168.100.17 mediation authpass privpass none none  [TLS] /netsim/netsimdir/LTE17A-V10x40-1.8K-DG2-FDD-LTE01\n" +
                        "    LTE01dg2ERBS00016        192.168.100.16 161 public v3+v2+v1 .128.0.0.193.1.192.168.100.16 mediation authpass privpass none none  [TLS] /netsim/netsimdir/LTE17A-V10x40-1.8K-DG2-FDD-LTE01\n" +
                        "\n" +
                        "'server_00047_LTE_MSRBS-V2_17A-V10@netsim' for LTE MSRBS-V2 17A-V10\n" +
                        "=================================================================\n" +
                        "    NE                       Address          Simulation/Commands \n" +
                        "    LTE01dg2ERBS00015        192.168.100.15 161 public v3+v2+v1 .128.0.0.193.1.192.168.100.15 mediation authpass privpass none none  [TLS] /netsim/netsimdir/LTE17A-V10x40-1.8K-DG2-FDD-LTE01\n" +
                        "    LTE01dg2ERBS00014        192.168.100.14 161 public v3+v2+v1 .128.0.0.193.1.192.168.100.14 mediation authpass privpass none none  [TLS] /netsim/netsimdir/LTE17A-V10x40-1.8K-DG2-FDD-LTE01\n" +
                        "    LTE01dg2ERBS00013        192.168.100.13 161 public v3+v2+v1 .128.0.0.193.1.192.168.100.13 mediation authpass privpass none none  [TLS] /netsim/netsimdir/LTE17A-V10x40-1.8K-DG2-FDD-LTE01\n" +
                        "    LTE01dg2ERBS00002        192.168.100.2 161 public v3+v2+v1 .128.0.0.193.1.192.168.100.2 mediation authpass privpass none none  [TLS] /netsim/netsimdir/LTE17A-V10x40-1.8K-DG2-FDD-LTE01\n" +
                        "\n" +
                        "'server_00046_LTE_ERBS_H1351-lim@netsim' for LTE ERBS H1351-lim\n" +
                        "=================================================================\n" +
                        "    NE                       Address          Simulation/Commands \n" +
                        "    LTE05ERBS00010           192.168.100.154  /netsim/netsimdir/LTEH1351-limx40-1.8K-FDD-LTE05\n" +
                        "    LTE05ERBS00009           192.168.100.153  /netsim/netsimdir/LTEH1351-limx40-1.8K-FDD-LTE05\n" +
                        "    LTE05ERBS00008           192.168.100.152  /netsim/netsimdir/LTEH1351-limx40-1.8K-FDD-LTE05\n" +
                        "    LTE05ERBS00005           192.168.100.149  /netsim/netsimdir/LTEH1351-limx40-1.8K-FDD-LTE05\n" +
                        "    LTE05ERBS00004           192.168.100.148  /netsim/netsimdir/LTEH1351-limx40-1.8K-FDD-LTE05\n" +
                        "    LTE05ERBS00003           192.168.100.147  /netsim/netsimdir/LTEH1351-limx40-1.8K-FDD-LTE05\n" +
                        "    LTE05ERBS00002           192.168.100.146  /netsim/netsimdir/LTEH1351-limx40-1.8K-FDD-LTE05\n" +
                        "    LTE05ERBS00001           192.168.100.145  /netsim/netsimdir/LTEH1351-limx40-1.8K-FDD-LTE05\n" +
                        "\n" +
                        "'server_00045_LTE_ERBS_H1160-V2lim@netsim' for LTE ERBS H1160-V2lim\n" +
                        "=================================================================\n" +
                        "    NE                       Address          Simulation/Commands \n" +
                        "    LTE02ERBS00036           192.168.100.72   /netsim/netsimdir/LTEH1160-V2limx40-1.8K-FDD-LTE02\n" +
                        "    LTE02ERBS00019           192.168.100.55   /netsim/netsimdir/LTEH1160-V2limx40-1.8K-FDD-LTE02\n" +
                        "\n" +
                        "'server_00044_LTE_ERBS_H1160-V2lim@netsim' for LTE ERBS H1160-V2lim\n" +
                        "=================================================================\n" +
                        "    NE                       Address          Simulation/Commands \n" +
                        "    LTE02ERBS00018           192.168.100.54   /netsim/netsimdir/LTEH1160-V2limx40-1.8K-FDD-LTE02\n" +
                        "    LTE02ERBS00017           192.168.100.53   /netsim/netsimdir/LTEH1160-V2limx40-1.8K-FDD-LTE02\n" +
                        "    LTE02ERBS00015           192.168.100.51   /netsim/netsimdir/LTEH1160-V2limx40-1.8K-FDD-LTE02\n" +
                        "    LTE02ERBS00014           192.168.100.50   /netsim/netsimdir/LTEH1160-V2limx40-1.8K-FDD-LTE02\n" +
                        "    LTE02ERBS00013           192.168.100.49   /netsim/netsimdir/LTEH1160-V2limx40-1.8K-FDD-LTE02\n" +
                        "    LTE02ERBS00012           192.168.100.48   /netsim/netsimdir/LTEH1160-V2limx40-1.8K-FDD-LTE02\n" +
                        "    LTE02ERBS00011           192.168.100.47   /netsim/netsimdir/LTEH1160-V2limx40-1.8K-FDD-LTE02\n" +
                        "    LTE02ERBS00010           192.168.100.46   /netsim/netsimdir/LTEH1160-V2limx40-1.8K-FDD-LTE02\n" +
                        "    LTE02ERBS00002           192.168.100.38   /netsim/netsimdir/LTEH1160-V2limx40-1.8K-FDD-LTE02\n" +
                        "    LTE02ERBS00001           192.168.100.37   /netsim/netsimdir/LTEH1160-V2limx40-1.8K-FDD-LTE02\n" +
                        "\n" +
                        "'server_00043_WPP_SGSN_16A-CP03-WPP-V1@netsim' for WPP SGSN 16A-CP03-WPP-V1\n" +
                        "=================================================================\n" +
                        "    NE                       Address          Simulation/Commands \n" +
                        "    SGSN-16A-CP03-V102       192.168.101.227 25161 public v3+v2+v1 .128.0.0.193.1.192.168.101.227 SGSN-16A-CP03-V102 authpass privpass hmac_md5 cbc_des  /netsim/netsimdir/CORE-ST-4.5K-SGSN-16A-CP03-V1x4\n" +
                        "    SGSN-16A-CP03-V101       192.168.101.226 25161 public v3+v2+v1 .128.0.0.193.1.192.168.101.226 SGSN-16A-CP03-V101 authpass privpass hmac_md5 cbc_des  /netsim/netsimdir/CORE-ST-4.5K-SGSN-16A-CP03-V1x4\n" +
                        "\n" +
                        "'server_00042_WPP_SGSN_16A-CP02-WPP-V3@netsim' for WPP SGSN 16A-CP02-WPP-V3\n" +
                        "=================================================================\n" +
                        "    NE                       Address          Simulation/Commands \n" +
                        "    SGSN-16A-CP02-V301       192.168.101.229 25161 public v3+v2+v1 .128.0.0.193.1.192.168.101.229 SGSN-16A-CP02-V301 authpass privpass hmac_md5 cbc_des  /netsim/netsimdir/CORE-ST-4.5K-SGSN-16A-CP02-V3x4\n" +
                        "    SGSN-16A-CP02-V304       2001:1b70:82a1:103::64:130 25161 public v3+v2+v1 .128.0.0.193.2.32.1.27.112.130.161.1.3.0.0.0.0.0.100.1.48 SGSN-16A-CP02-V304 authpass privpass hmac_md5 cbc_des  /netsim/netsimdir/CORE-ST-4.5K-SGSN-16A-CP02-V3x4\n" +
                        "    SGSN-16A-CP02-V303       192.168.101.231 25161 public v3+v2+v1 .128.0.0.193.1.192.168.101.231 SGSN-16A-CP02-V303 authpass privpass hmac_md5 cbc_des  /netsim/netsimdir/CORE-ST-4.5K-SGSN-16A-CP02-V3x4\n" +
                        "    SGSN-16A-CP02-V302       192.168.101.230 25161 public v3+v2+v1 .128.0.0.193.1.192.168.101.230 SGSN-16A-CP02-V302 authpass privpass hmac_md5 cbc_des  /netsim/netsimdir/CORE-ST-4.5K-SGSN-16A-CP02-V3x4\n" +
                        "\n" +
                        "'server_00041_WPP_SGSN_16A-CP01-WPP-V1@netsim' for WPP SGSN 16A-CP01-WPP-V1\n" +
                        "=================================================================\n" +
                        "    NE                       Address          Simulation/Commands \n" +
                        "    SGSN-16A-CP01-V104       2001:1b70:82a1:103::64:131 25161 public v3+v2+v1 .128.0.0.193.2.32.1.27.112.130.161.1.3.0.0.0.0.0.100.1.49 SGSN-16A-CP01-V104 authpass privpass hmac_md5 cbc_des  /netsim/netsimdir/CORE-ST-4.5K-SGSN-16A-CP01-V1x4\n" +
                        "    SGSN-16A-CP01-V103       192.168.101.234 25161 public v3+v2+v1 .128.0.0.193.1.192.168.101.234 SGSN-16A-CP01-V103 authpass privpass hmac_md5 cbc_des  /netsim/netsimdir/CORE-ST-4.5K-SGSN-16A-CP01-V1x4\n" +
                        "    SGSN-16A-CP01-V102       192.168.101.233 25161 public v3+v2+v1 .128.0.0.193.1.192.168.101.233 SGSN-16A-CP01-V102 authpass privpass hmac_md5 cbc_des  /netsim/netsimdir/CORE-ST-4.5K-SGSN-16A-CP01-V1x4\n" +
                        "    SGSN-16A-CP01-V101       192.168.101.232 25161 public v3+v2+v1 .128.0.0.193.1.192.168.101.232 SGSN-16A-CP01-V101 authpass privpass hmac_md5 cbc_des  /netsim/netsimdir/CORE-ST-4.5K-SGSN-16A-CP01-V1x4\n" +
                        "\n" +
                        "'server_00040_WCDMA_M-MGw_C1278-lim@netsim' for WCDMA M-MGw C1278-lim\n" +
                        "=================================================================\n" +
                        "    NE                       Address          Simulation/Commands \n" +
                        "    K3C127801                192.168.101.209  /netsim/netsimdir/CORE-3K-ST-MGw-C1278-17Bx2\n" +
                        "\n" +
                        "'server_00039_WCDMA_M-MGw_C1203-V1lim@netsim' for WCDMA M-MGw C1203-V1lim\n" +
                        "=================================================================\n" +
                        "    NE                       Address          Simulation/Commands \n" +
                        "    K3C120301                192.168.101.207  /netsim/netsimdir/CORE-3K-ST-MGw-C1203-16Ax2\n" +
                        "END\n";
        String command = showSimnes;
        String[] res = NetsimInformation.parseElements(toParse, command);
        assertThat(res.length).isEqualTo(47);
    }
}
