package org.eclipse.leshan.client.demo;

import org.eclipse.leshan.client.resource.BaseInstanceEnabler;
import org.eclipse.leshan.core.node.LwM2mResource;
import org.eclipse.leshan.core.node.ObjectLink;
import org.eclipse.leshan.core.response.ReadResponse;
import org.eclipse.leshan.core.response.WriteResponse;
import org.eclipse.leshan.util.NamedThreadFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class OMA10272IntervalDataDelivery extends BaseInstanceEnabler {
    private static final Logger LOG = LoggerFactory.getLogger(OMA10273EventDataDelivery.class);
    private static final int NAME = 0;
    private static final int INTERVAL_DATA_LINKS = 1;
    private static final int LATEST_PAYLOAD = 2;
    private static final int SCHEDULE = 3;
    private final ScheduledExecutorService scheduler;


    public OMA10272IntervalDataDelivery() {
        this.scheduler = Executors.newSingleThreadScheduledExecutor(new NamedThreadFactory("OMA10272IntervalDataDelivery"));
        scheduler.scheduleAtFixedRate(new Runnable() {

            @Override
            public void run() {
                adjustIntervalData();
            }
        }, 0, 30, TimeUnit.SECONDS);
    }


    private synchronized ObjectLink adjustIntervalDataLink(double currentTemp) {
        return new ObjectLink();
    }

    private final Random rng = new Random();
    private double currentTemp = 20d;
    private synchronized void adjustIntervalData() {
        float delta = (rng.nextInt(20) - 10) / 10f;
        currentTemp += delta;
        ObjectLink changedResource = adjustIntervalDataLink(currentTemp);
        if (changedResource != null) {
            //fireResourcesChange(INTERVAL_DATA_LINKS, changedResource);
            fireResourcesChange(LATEST_PAYLOAD);
        } else {
            fireResourcesChange(LATEST_PAYLOAD);
        }
    }



    private String getName() {
        return "Name";
    }
    public ObjectLink getSchedule() {
        return new ObjectLink();
    }
    public ObjectLink getIntervalDataLink() {
        return new ObjectLink();
    }
    public byte[] getLatestPayload() {
        String eventLogBuffer = "8319281a01831a5a983fa0193840861903f31901c50c18391908ae190566";
        return eventLogBuffer.getBytes();
    }

    @Override
    public ReadResponse read(int resourceId) {
        LOG.info("Read on Device Resource " + resourceId);
        switch (resourceId) {
            case NAME:
                return ReadResponse.success(resourceId, getName());
            case LATEST_PAYLOAD:
                return ReadResponse.success(resourceId, getLatestPayload());
            case INTERVAL_DATA_LINKS:
                return ReadResponse.success(resourceId, getIntervalDataLink());
            case SCHEDULE:
                return ReadResponse.success(resourceId, getSchedule());
            default:
                return super.read(resourceId);
        }
    }




    private ObjectLink intervalDataLink;
    public void setIntervalDataLink(ObjectLink value) {
        intervalDataLink = value;
    }
    private ObjectLink schedule;
    public void setSchedule(ObjectLink value) {
        schedule = value;
    }

    @Override
    public WriteResponse write(int resourceId, LwM2mResource value) {
        LOG.info("Write on Device Resource " + resourceId);
        switch (resourceId) {
            case INTERVAL_DATA_LINKS:
                setIntervalDataLink((ObjectLink) value.getValue());
                fireResourcesChange(resourceId);
                return WriteResponse.success();
            case LATEST_PAYLOAD:
                setSchedule((ObjectLink) value.getValue());
                fireResourcesChange(resourceId);
                return WriteResponse.success();
            default:
                return super.write(resourceId, value);
        }
    }


}
