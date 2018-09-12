package org.eclipse.leshan.client.demo;

import org.eclipse.leshan.client.resource.BaseInstanceEnabler;
import org.eclipse.leshan.core.node.LwM2mResource;
import org.eclipse.leshan.core.response.ExecuteResponse;
import org.eclipse.leshan.core.response.ReadResponse;
import org.eclipse.leshan.core.response.WriteResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;

public class OMA10268TemperatureReadings extends BaseInstanceEnabler {
    private static final Logger LOG = LoggerFactory.getLogger(OMA10268TemperatureReadings.class);
    private static final int INTERVAL_PERIOD = 6000;
    private static final int INTERVAL_START_OFFSET = 6001;
    private static final int INTERVAL_UTC_OFFSET = 6002;
    private static final int INTERVAL_COLLECTION_STARTTIME = 6003;
    private static final int OLDEST_RECORDED_INTERVAL = 6004;
    private static final int LAST_DELIVERED_INTERVAL = 6005;
    private static final int LATEST_RECORDED_INTERVAL = 6006;
    private static final int DELIVERY_MIDNIGHT_ALIGNED = 6007;
    private static final int INTERVAL_HISTORICAL_READ = 6008;
    private static final int INTERVAL_HISTORICAL_READ_PAYLOAD = 6009;
    private static final int INTERVAL_CHANGE_CONFIGURATION = 6010;
    private static final int START = 6026;
    private static final int STOP = 6027;
    private static final int STATUS = 6028;
    private static final int LATEST_PAYLOAD = 6029;


    public OMA10268TemperatureReadings() {
    }


    public int getIntervalPeriod() {return 0;}
    public int getIntervalStartOffset() {return 0;}
    public String getIntervalUTCOffset() {return "+10";}
    public boolean getDeliveryMidnightAligned() {return true;}
    public byte[] getNumberValuePerInterval() {return new byte[] {};}
    public Date getLatestRecordedInterval() { return new Date(); }
    public Date getIntervalCollectionStartTime() { return new Date(); }
    public Date getOldestRecordInterval() { return new Date(); }
    public Date getLastDeliveredInterval() { return new Date(); }
    public byte[] getIntervalHistoricalReadPayload() {return new byte[] {};}
    public int getStatus() {return 0;}

    @Override
    public ReadResponse read(int resourceId) {
        LOG.info("Read on Device Resource " + resourceId);
        switch (resourceId) {
            case INTERVAL_PERIOD: //6000 R Integer
                return ReadResponse.success(resourceId, getIntervalPeriod());
            case INTERVAL_START_OFFSET: //6001 R
                return ReadResponse.success(resourceId, getIntervalStartOffset());
            case INTERVAL_UTC_OFFSET: //6002 R String
                return ReadResponse.success(resourceId, getIntervalUTCOffset());
            case INTERVAL_COLLECTION_STARTTIME: //6003 R Time
                return ReadResponse.success(resourceId, getIntervalCollectionStartTime());
            case OLDEST_RECORDED_INTERVAL: //6004 R Time
                return ReadResponse.success(resourceId, getOldestRecordInterval());
            case LAST_DELIVERED_INTERVAL: //6005 RW Time
                return ReadResponse.success(resourceId, getLastDeliveredInterval());
            case LATEST_RECORDED_INTERVAL: //6006 R Time
                return ReadResponse.success(resourceId, getLatestRecordedInterval());
            case DELIVERY_MIDNIGHT_ALIGNED: // 6007 RW Boolean
                return ReadResponse.success(resourceId, getDeliveryMidnightAligned());
            case INTERVAL_HISTORICAL_READ_PAYLOAD: // 6009 R Qpaque
                return ReadResponse.success(resourceId, getIntervalHistoricalReadPayload());
            case STATUS: // 6028 R
                return ReadResponse.success(resourceId, getStatus());
            case LATEST_PAYLOAD: // 6029 R Qpaque
                return ReadResponse.success(resourceId, getNumberValuePerInterval());
            default:
                return super.read(resourceId);
        }
    }


    @Override
    public synchronized ExecuteResponse execute(int resourceId, String params) {
        LOG.info("Exec on Device Resource " + resourceId);
        switch (resourceId) {
            case INTERVAL_HISTORICAL_READ: // 6008 E
                return ExecuteResponse.success();
            case START: // 6026 E
                return ExecuteResponse.success();
            case STOP: // 6027 E
                return ExecuteResponse.success();
            case INTERVAL_CHANGE_CONFIGURATION: // 6010 E
                return ExecuteResponse.success();
            default:
                return super.execute(resourceId, params);
        }
    }

    private boolean deliveryMidnightAligned;
    public void setDeliveryMidnightAligned(boolean value) {
        deliveryMidnightAligned = value;
    }
    private Date latestRecordedInterval;
    public void setLatestRecordedInterval(Date value) {
        latestRecordedInterval = value;
    }

    @Override
    public WriteResponse write(int resourceId, LwM2mResource value) {
        LOG.info("Write on Device Resource " + resourceId);
        switch (resourceId) {
            case DELIVERY_MIDNIGHT_ALIGNED: // 6007 RW
                setDeliveryMidnightAligned( (boolean) value.getValue());
                fireResourcesChange(resourceId);
                return WriteResponse.success();
            case LATEST_RECORDED_INTERVAL: //6005 RW
                setLatestRecordedInterval( (Date) value.getValue());
                fireResourcesChange(resourceId);
                return WriteResponse.success();
            default:
                return super.write(resourceId, value);
        }
    }



}
