package org.eclipse.leshan.client.demo;

import org.eclipse.leshan.client.resource.BaseInstanceEnabler;
import org.eclipse.leshan.core.response.ExecuteResponse;
import org.eclipse.leshan.core.response.ReadResponse;
import org.eclipse.leshan.util.NamedThreadFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Extel0001TestDiagnostic  extends BaseInstanceEnabler {
    private static final Logger LOG = LoggerFactory.getLogger(OMA10273EventDataDelivery.class);
    private static final int BOOTSTRAP_REQUEST_TRIGGER = 0;
    private static final int LAST_BOOTSTRAPPED = 1;
    private final ScheduledExecutorService scheduler;

    public Extel0001TestDiagnostic() {
        this.scheduler = Executors.newSingleThreadScheduledExecutor(new NamedThreadFactory("Extel0001TestDiagnostic"));
        scheduler.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                //adjustBatteryLevelReadings();
            }
        }, 0, 30, TimeUnit.SECONDS);
    }

    Date getLastBootstrapped() {return (new Date()); }

    @Override
    public synchronized ReadResponse read(int resourceId) {
        switch (resourceId) {
            case LAST_BOOTSTRAPPED:
                return ReadResponse.success(resourceId, getLastBootstrapped());
            default:
                return super.read(resourceId);
        }
    }


    private void triggerBootstrap() {

    }

    @Override
    public synchronized ExecuteResponse execute(int resourceId, String params) {
        switch (resourceId) {
            case BOOTSTRAP_REQUEST_TRIGGER:
                triggerBootstrap();
                return ExecuteResponse.success();
            default:
                return super.execute(resourceId, params);
        }
    }


}