package org.eclipse.leshan.client.demo;

import org.eclipse.leshan.client.resource.BaseInstanceEnabler;
import org.eclipse.leshan.core.node.LwM2mResource;
import org.eclipse.leshan.core.response.ExecuteResponse;
import org.eclipse.leshan.core.response.ReadResponse;
import org.eclipse.leshan.core.response.WriteResponse;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

public class OMA0005FirmwareUpdate extends BaseInstanceEnabler {
    private final Logger logger = Logger.getLogger(this.getClass().getName());

    public static final int RESOURCE_ID_PACKAGE = 0;
    public static final int RESOURCE_ID_PACKAGE_URI = 1;
    public static final int RESOURCE_ID_UPDATE = 2;
    public static final int RESOURCE_ID_STATE = 3;
    public static final int RESOURCE_ID_UPDATE_RESULT = 5;
    public static final int RESOURCE_ID_PACKAGE_NAME = 6;
    public static final int RESOURCE_ID_PACKAGE_VERSION = 7;
    public static final int RESOURCE_ID_PROTOCOL_SUPPORT = 8;
    public static final int RESOURCE_ID_DELIVERY_METHOD = 9;

    public static final int STATE_IDLE = 0;
    public static final int STATE_DOWLOADING = 1;
    public static final int STATE_DOWNLOADED = 2;
    public static final int STATE_UPDATING = 3;

    public static final int UPDATE_RESULT_INITIAL = 0;
    public static final int UPDATE_RESULT_SUCCESS = 1;
    public static final int UPDATE_RESULT_NOT_ENOUGH_FLASH_MEMORY = 2;
    public static final int UPDATE_RESULT_OUT_OF_RAM = 3;
    public static final int UPDATE_RESULT_CONNECTION_LOST_DURING_DOWNLOAD = 4;
    public static final int UPDATE_RESULT_INTEGRETY_CHECK_FAILURE = 5;
    public static final int UPDATE_RESULT_UNSUPPORTED_PACKAGE_TYPE = 6;
    public static final int UPDATE_RESULT_INVALID_URI = 7;
    public static final int UPDATE_RESULT_FIRMWARE_UPDATE_FAILED = 8;
    public static final int UPDATE_RESULT_UNSUPPORTED_PROTOCOL = 9;

    private String packageUri;
    private int state = STATE_IDLE;
    private int updateResult = UPDATE_RESULT_INITIAL;
    private String packageName = "OneSecondUpdate";
    private String packageVersion = "1.1";

    // 0 - CoAP
    // 1 - CoAPS
    // 2 - HTTP
    // 3 - HTTPS
    private int protocolSupport = 2; //3;

    //	0 – Pull only
    //  1 – Push only
    //  2 – Both.
    private int deliveryMethod = 0;

    public OMA0005FirmwareUpdate() {
    }

    @Override
    public ReadResponse read(int resourceid) {
        logger.finest("Read on Resource " + resourceid);
        switch (resourceid) {
            case RESOURCE_ID_UPDATE_RESULT:
                return ReadResponse.success(resourceid, this.updateResult);
            case RESOURCE_ID_STATE:
                return ReadResponse.success(resourceid, this.state);
            case RESOURCE_ID_DELIVERY_METHOD:
                return ReadResponse.success(resourceid, this.deliveryMethod);
            case RESOURCE_ID_PROTOCOL_SUPPORT:
                return ReadResponse.success(resourceid, this.protocolSupport);
            case RESOURCE_ID_PACKAGE_NAME:
                return ReadResponse.success(resourceid, this.packageName);
            case RESOURCE_ID_PACKAGE_VERSION:
                return ReadResponse.success(resourceid, this.packageVersion);

            default:
                return super.read(resourceid);
        }
    }

    public boolean assertLink(String urlStr){

        try{
            URL url = new URL(urlStr);

            // we only support http
            if( !url.getProtocol().equalsIgnoreCase("https") )
                return false;

            // make sure we only accept packages from this IP
//            if( !url.getHost().equals("23.100.10.249"))
//                return false;

            //make "sure" it's a jar file
//            if( !url.getFile().toLowerCase().endsWith(".jar"))
//                return false;

        }catch (MalformedURLException e){

            return false;
        }

        return true;

    }


    @Override
    public WriteResponse write(int resourceid, LwM2mResource value) {
        logger.info("Write on Resource " + resourceid + " value: " + value.getValue().toString());
        switch (resourceid) {
            case RESOURCE_ID_PACKAGE:
                return WriteResponse.notFound();
            case RESOURCE_ID_PACKAGE_URI:

                String url = value.getValue().toString();

                if( !assertLink(url))
                    return WriteResponse.badRequest("URL validation failure");

                this.packageUri = url;

                startDownload();
                return WriteResponse.success();
            default:
                return WriteResponse.notFound();
        }
    }

    @Override
    public ExecuteResponse execute(int resourceid, String params) {
        logger.info("Execute on Resource " + resourceid);
        switch (resourceid) {
            case RESOURCE_ID_UPDATE:
                startFirmwareUpdate();
                return ExecuteResponse.success();
            default:
                return super.execute(resourceid, params);
        }
    }

    private void startDownload() {
        logger.log(Level.INFO, "Starting download.");

        try {
            setState(STATE_DOWLOADING);
            String workingDirName = System.getProperty("user.dir");

            File packagesDir = new File(workingDirName + File.separatorChar + "packages");

            //INFO: Write on Resource 1 value:
// https://testng2appiotmeas.blob.core.windows.net/firmwares/2efb025b-199a-45c5-86cb-adedc2019a73/Edison-client-1.1.jar?sv=2016-05-31&sr=c&sig=0l7VVPJmbHYFE4367cJEJcSU7nTwyDG4hqkf8EQRJZY%3D&st=2017-09-13T00:00:00Z&se=2017-09-15T00:00:00Z&sp=rw


            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd_hhmmss");

            String filename = "Edison-" + sdf.format(new Date()) + ".jar";

            File packageFile = new File(packagesDir, filename);

            //String filename = this.packageUri.substring(this.packageUri.lastIndexOf("/") + 1);

            //File packageFile = new File(packagesDir, filename);
            HttpGet request = new HttpGet(this.packageUri);

            HttpClient httpClient = HttpClientBuilder.create().build();
            HttpResponse response = httpClient.execute(request);

            int responseCode = response.getStatusLine().getStatusCode();
            logger.log(Level.FINEST, "[GatewaySDK] Response Code: " + responseCode);

            // TODO: Handle update result for
            // - Unsupported package type (6)
            // - Invalid URL (used below) (7)
            // - Unsupported protocol. (9)

            if (responseCode < 200 || responseCode > 299) {
                String reason = response.getStatusLine().getReasonPhrase();
                logger.log(Level.SEVERE,
                        "Could not download firmware update. Got response code " + responseCode + " " + reason);
                setState(STATE_IDLE);
                setUpdateResult(UPDATE_RESULT_INVALID_URI);
                return;
            }

            InputStream is = response.getEntity().getContent();
            FileUtils.copyInputStreamToFile(is, packageFile);

            // TODO: Handle update result for
            // - Not enough flash memory (2)
            // - Connection lost during download (Used below) (4)
            // - Integrity check failed (5)

            setState(STATE_DOWNLOADED);
            logger.log(Level.INFO, "Download complete.");
        } catch (IllegalArgumentException iae) {
            setState(STATE_IDLE);
            setUpdateResult(UPDATE_RESULT_INVALID_URI);
            logger.log(Level.INFO, "Download failed.");
        } catch (IOException e) {
            setState(STATE_IDLE);
            setUpdateResult(UPDATE_RESULT_CONNECTION_LOST_DURING_DOWNLOAD);
            logger.log(Level.INFO, "Download failed.");
        }
    }

    private void startFirmwareUpdate() {

        logger.log(Level.INFO, "Starting upgrade.");

        try {
            setState(STATE_UPDATING);

            setUpdateResult(UPDATE_RESULT_INITIAL);

            setUpdateResult(UPDATE_RESULT_SUCCESS);

            // TODO: Apply firmware upgrade, simulated with a 5 sec delay...

            //Thread.sleep(1000);
            String[] cmd = {"/bin/sh", "-c", "bin/delay_restart.sh"};

            logger.info("Running command: " + cmd);

            Process p = Runtime.getRuntime().exec(cmd);

            // TODO: Handle update result for
            // - Integrity check failed (5)
            // - Firmware update failed (used below) (8)


            setState(STATE_IDLE);
            logger.log(Level.INFO, "Upgrade complete.");

        } catch (Exception e) {
            setState(STATE_IDLE);
            setUpdateResult(UPDATE_RESULT_FIRMWARE_UPDATE_FAILED);
            logger.log(Level.INFO, "Upgrade failed.");
        }
    }

    public void setUpdateResult(int updateResult) {
        this.updateResult = updateResult;
        fireResourcesChange(RESOURCE_ID_UPDATE_RESULT);
        logger.log(Level.INFO, "Update result is " + updateResult);
    }

    public void setState(int state) {
        this.state = state;
        if (state == STATE_DOWNLOADED) {
            this.packageUri = "";
        }
        fireResourcesChange(RESOURCE_ID_STATE);
        logger.log(Level.INFO, "State is " + state);
    }
}
