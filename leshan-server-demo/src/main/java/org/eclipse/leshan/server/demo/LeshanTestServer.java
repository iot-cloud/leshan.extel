package org.eclipse.leshan.server.demo;

import org.eclipse.leshan.core.model.ObjectLoader;
import org.eclipse.leshan.core.model.ObjectModel;
import org.eclipse.leshan.server.model.LwM2mModelProvider;
import org.eclipse.leshan.server.model.StaticModelProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.List;

public class LeshanTestServer {
    private static final Logger LOG = LoggerFactory.getLogger(LeshanServerDemo.class);

    private final static String[] modelPaths = new String[] { "5.xml",
            "31024.xml",

            "10241.xml", "10242.xml", "10243.xml", "10244.xml", "10245.xml", "10246.xml", "10247.xml",
            "10248.xml", "10249.xml", "10250.xml", "10262.xml", "10263.xml",
            "10266.xml", "10267.xml", "10268.xml", "10269.xml", "10270.xml", "10271.xml",
            "Extel0001.xml", "22001.xml",

            "2048.xml", "2049.xml", "2050.xml", "2051.xml", "2052.xml", "2053.xml", "2054.xml",
            "2055.xml", "2056.xml", "2057.xml",

            "3200.xml", "3201.xml", "3202.xml", "3203.xml", "3300.xml", "3301.xml", "3302.xml",
            "3303.xml", "3304.xml", "3305.xml", "3306.xml", "3308.xml", "3310.xml", "3311.xml",
            "3312.xml", "3313.xml", "3314.xml", "3315.xml", "3316.xml", "3317.xml", "3318.xml",
            "3319.xml", "3320.xml", "3321.xml", "3322.xml", "3323.xml", "3324.xml", "3325.xml",
            "3326.xml", "3327.xml", "3328.xml", "3329.xml", "3330.xml", "3331.xml", "3332.xml",
            "3333.xml", "3334.xml", "3335.xml", "3336.xml", "3337.xml", "3338.xml", "3339.xml",
            "3340.xml", "3341.xml", "3342.xml", "3343.xml", "3344.xml", "3345.xml", "3346.xml",
            "3347.xml", "3348.xml",

            "Communication_Characteristics-V1_0.xml",

            "LWM2M_Lock_and_Wipe-V1_0.xml", "LWM2M_Cellular_connectivity-v1_0.xml",
            "LWM2M_APN_connection_profile-v1_0.xml", "LWM2M_WLAN_connectivity4-v1_0.xml",
            "LWM2M_Bearer_selection-v1_0.xml", "LWM2M_Portfolio-v1_0.xml", "LWM2M_DevCapMgmt-v1_0.xml",
            "LWM2M_Software_Component-v1_0.xml", "LWM2M_Software_Management-v1_0.xml",

            "Non-Access_Stratum_NAS_configuration-V1_0.xml" };

    public static void main(String[] args) {
        // Define model provider
        List<ObjectModel> models = ObjectLoader.loadDefault();
        models.addAll(ObjectLoader.loadDdfResources("/models/", modelPaths));

        //if (modelsFolderPath != null) {
        //    models.addAll(ObjectLoader.loadObjectsFromDir(new File(modelsFolderPath)));
        //}

        LwM2mModelProvider modelProvider = new StaticModelProvider(models);

    }
}
