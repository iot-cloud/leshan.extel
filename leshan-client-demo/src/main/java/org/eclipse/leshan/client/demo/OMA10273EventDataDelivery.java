package org.eclipse.leshan.client.demo;

import org.eclipse.leshan.client.resource.BaseInstanceEnabler;
import org.eclipse.leshan.core.node.LwM2mResource;
import org.eclipse.leshan.core.node.LwM2mSingleResource;
import org.eclipse.leshan.core.node.ObjectLink;
import org.eclipse.leshan.core.request.WriteRequest;
import org.eclipse.leshan.core.response.ReadResponse;
import org.eclipse.leshan.core.response.WriteResponse;
import org.eclipse.leshan.util.NamedThreadFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class OMA10273EventDataDelivery extends BaseInstanceEnabler {
        private static final Logger LOG = LoggerFactory.getLogger(OMA10273EventDataDelivery.class);
        private static final int NAME = 0;
        private static final int EVENT_DATA_LINKS = 1;
        private static final int LATEST_EVENTLOG = 2;
        private static final int SCHEDULE = 3;
        private final ScheduledExecutorService scheduler;


    public OMA10273EventDataDelivery() {
        this.scheduler = Executors.newSingleThreadScheduledExecutor(new NamedThreadFactory("OMA10273EventDataDelivery"));
        scheduler.scheduleAtFixedRate(new Runnable() {

            @Override
            public void run() {
                adjustEventData();
            }
        }, 0, 30, TimeUnit.SECONDS);

    }

    private final Random rng = new Random();
    private double currentTemp = 20d;
    private synchronized void adjustEventData() {
        float delta = (rng.nextInt(20) - 10) / 10f;
        currentTemp += delta;
        ObjectLink changedResource = adjustEventDataLink(currentTemp);
        if (changedResource != null) {
            //fireResourcesChange(LATEST_EVENTLOG, changedResource);
            fireResourcesChange(LATEST_EVENTLOG);
        } else {
            fireResourcesChange(LATEST_EVENTLOG);
        }
    }

    private synchronized ObjectLink adjustEventDataLink(double currentTemp) {
        //Map<Integer, ObjectLink> myMap = new ObjectArray<>();

        WriteRequest writeReq = new WriteRequest(
                WriteRequest.Mode.UPDATE,
                9, 0,
                LwM2mSingleResource.newObjectLinkResource(
                        13,
                        new ObjectLink(5566, 7788)
                )
                //, LwM2mMultipleResource.newObjectLinkResource()
        );

        return new ObjectLink();
    }

    @Override
    public ReadResponse read(int resourceId) {
        LOG.info("Read on Device Resource " + resourceId);
        switch (resourceId) {
            case NAME:
                return ReadResponse.success(resourceId, getName());
            case LATEST_EVENTLOG:
                return ReadResponse.success(resourceId, getLatestEventlog());
            case EVENT_DATA_LINKS:
                return ReadResponse.success(resourceId, getEventDataLink());
            case SCHEDULE:
                return ReadResponse.success(resourceId, getSchedule());
            default:
                return super.read(resourceId);
        }
    }

    private String getName() {
        return "Name";
    }
    private ObjectLink getSchedule() {
        return new ObjectLink();
    }
    private ObjectLink getEventDataLink() {
        return new ObjectLink();
    }
    private byte[] getLatestEventlog() {
        String eventLogBuffer = "8319281a01831a5a983fa0193840861903f31901c50c18391908ae190566";
        return eventLogBuffer.getBytes();
    }



    @Override
    public WriteResponse write(int resourceId, LwM2mResource value) {
        LOG.info("Write on Device Resource " + resourceId);
        switch (resourceId) {
            case EVENT_DATA_LINKS:
                setEventDataLink((ObjectLink) value.getValue());
                fireResourcesChange(resourceId);
                return WriteResponse.success();
            case LATEST_EVENTLOG:
                setSchedule((ObjectLink) value.getValue());
                fireResourcesChange(resourceId);
                return WriteResponse.success();
            default:
                return super.write(resourceId, value);
        }
    }

    private ObjectLink eventDataLink;
    public void setEventDataLink(ObjectLink value) {
        eventDataLink = value;
    }


    private ObjectLink schedule;
    public void setSchedule(ObjectLink value) {
        schedule = value;
    }
}
