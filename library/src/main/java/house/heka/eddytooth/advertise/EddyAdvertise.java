package house.heka.eddytooth.advertise;

import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.AdvertiseCallback;
import android.bluetooth.le.AdvertiseData;
import android.bluetooth.le.AdvertiseSettings;
import android.bluetooth.le.BluetoothLeAdvertiser;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.ParcelUuid;
import android.util.Log;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.ByteBuffer;

import house.heka.eddytooth.beacon.UriBeacon;


public class EddyAdvertise {

    private static final byte FRAME_TYPE_UID = 0x00;
    public static final byte FRAME_TYPE_URL = 0x10;

    private static final int REQUEST_ENABLE_BLUETOOTH = 1;

    // The Eddystone Service UUID, 0xFEAA. See https://github.com/google/eddystone
    private static final ParcelUuid SERVICE_UUID =
            ParcelUuid.fromString("0000FEAA-0000-1000-8000-00805F9B34FB");

    private static final String TAG = "EddyAdvertise";

    private static final String DEFAULT_NAMESPACE = "17237217237217237217";
    private static final String DEFAULT_INSTANCE = "172372172372";

    private final AdvertiseCallback advertiseCallback;

    private BluetoothLeAdvertiser adv;

    private int txPowerLevel = AdvertiseSettings.ADVERTISE_TX_POWER_MEDIUM;
    private int advertiseMode = AdvertiseSettings.ADVERTISE_MODE_BALANCED;

    private String uid_namespace;
    private String uid_instance;
    private boolean isStarted = false;

    public static byte[] getDefaultNamespace() {
        return toFullByteArray(DEFAULT_NAMESPACE);
    }

    public static byte[] getDefaultInstance() {
        return toFullByteArray(DEFAULT_INSTANCE);
    }

    public static void processFailureCallback(int errorCode) {
        switch (errorCode) {
            case AdvertiseCallback.ADVERTISE_FAILED_DATA_TOO_LARGE:
                Log.e(TAG,"ADVERTISE_FAILED_DATA_TOO_LARGE");
                break;
            case AdvertiseCallback.ADVERTISE_FAILED_TOO_MANY_ADVERTISERS:
                Log.e(TAG,"ADVERTISE_FAILED_TOO_MANY_ADVERTISERS");
                break;
            case AdvertiseCallback.ADVERTISE_FAILED_ALREADY_STARTED:
                Log.e(TAG,"ADVERTISE_FAILED_ALREADY_STARTED");
                break;
            case AdvertiseCallback.ADVERTISE_FAILED_INTERNAL_ERROR:
                Log.e(TAG,"ADVERTISE_FAILED_INTERNAL_ERROR");
                break;
            case AdvertiseCallback.ADVERTISE_FAILED_FEATURE_UNSUPPORTED:
                Log.e(TAG,"ADVERTISE_FAILED_FEATURE_UNSUPPORTED");
                break;
            default:
                Log.e(TAG,"startAdvertising failed with unknown error " + errorCode);
                break;
        }
    }

    public EddyAdvertise(Activity a, AdvertiseCallback ac) {
        setUid_namespace(DEFAULT_NAMESPACE);
        setUid_instance(DEFAULT_INSTANCE);
        advertiseCallback = createAdvertiseCallback();
        init(a);
    }

    public EddyAdvertise(Activity a, String namespace, String instance) {
        setUid_namespace(namespace);
        setUid_instance(instance);
        advertiseCallback = createAdvertiseCallback();
        init(a);
    }

    public EddyAdvertise(Activity a, String namespace, String instance, AdvertiseCallback advertCallback) {
        setUid_namespace(namespace);
        setUid_instance(instance);
        advertiseCallback = advertCallback;
        init(a);
    }


    public boolean isStarted() {
        return isStarted;
    }

    //keep context local to avoid leaks
    public void startAdvertising(Context c) {
        Log.i(TAG, "Starting ADV");
        if (!isValidHex(getUid_namespace(), 10)) {
            Log.e(TAG, "not 10-byte hex");
            return;
        }
        if (!isValidHex(getUid_instance(), 6)) {
            Log.e(TAG, "not 6-byte hex");
            return;
        }

        AdvertiseSettings advertiseSettings = getAdvertiseSettings();

        AdvertiseData advertiseData = null;

        try {
            advertiseData = getAdvertiseData();
        } catch (IOException e) {
            Log.e(TAG, e.toString());
            Toast.makeText(c, "failed to build advertising data", Toast.LENGTH_SHORT).show();
            return;
        }

        adv.startAdvertising(advertiseSettings, advertiseData, advertiseCallback);
        isStarted = true;
    }

    public void startAdvertising(Uri uri, Context c) {
        if (adv == null) return;

        AdvertiseSettings advertiseSettings = getAdvertiseUriSettings();


        AdvertiseData advertiseData = null;

        try {
            advertiseData = getAdvertiseUriData(uri);
        } catch (IOException | URISyntaxException e) {
            Log.e(TAG, e.toString());
            Toast.makeText(c, "failed to build advertising data", Toast.LENGTH_SHORT).show();

        }


        adv.startAdvertising(advertiseSettings, advertiseData, advertiseCallback);
        isStarted = true;
    }

    public void stopAdvertising() {
        Log.i(TAG, "Stopping ADV");
        adv.stopAdvertising(advertiseCallback);
        isStarted = false;
    }


    public String getUid_namespace() {
        return uid_namespace;
    }

    //namespace must be 20 characters [0-9A-F]
    public void setUid_namespace(String _namespace) {
        if (_namespace.length() < 20) {
            Log.e(TAG, "padding namespace to reach 20 characters");
            while (_namespace.length() < 20) {
                _namespace = _namespace + "0";
            }
        }
        if (_namespace.matches(".*?[^0-9A-F].*") || _namespace.length() > 20) {
            Log.e(TAG, "namespace must be 20 [0-9A-F]");
            _namespace = _namespace.replaceAll("[^0-9A-F]", "0").substring(0, 20);
        }
        if (!isValidHex(_namespace, 10)) {
            Log.e(TAG, "not 10-byte hex");
        } else {
            this.uid_namespace = _namespace;
        }
        Log.d(TAG,"namespace set:"+uid_namespace);
    }

    public String getUid_instance() {
        return uid_instance;
    }


    //instance must be 12 characters [0-9A-F]
    public void setUid_instance(String _instance) {
        if (_instance.length() < 12) {
            Log.e(TAG, "padding instance to reach 12 characters");
            while (_instance.length() < 12) {
                _instance = _instance + "0";
            }
        }
        if (_instance.matches(".*?[^0-9A-F].*") || _instance.length() > 12) {
            Log.e(TAG, "instance must be 12 [0-9A-F]");
            _instance = _instance.replaceAll("[^0-9A-F]", "0").substring(0, 12);
        }
        if (!isValidHex(_instance, 6)) {
            Log.e(TAG, "not 6-byte hex");
        } else {
            this.uid_instance = _instance;
        }
        Log.d(TAG,"instance set:"+uid_instance);
    }

    public int getTxPowerLevel() {
        return txPowerLevel;
    }

    // default AdvertiseSettings.ADVERTISE_TX_POWER_HIGH
    public void setTxPowerLevel(int txPowerLevel) {
        this.txPowerLevel = txPowerLevel;
    }

    public int getAdvertiseMode() {
        return advertiseMode;
    }

    // default AdvertiseSettings.ADVERTISE_MODE_LOW_LATENCY
    public void setAdvertiseMode(int advertiseMode) {
        this.advertiseMode = advertiseMode;
    }


    private void init(Activity a) {
        BluetoothManager manager = (BluetoothManager) a.getSystemService(
                Context.BLUETOOTH_SERVICE);
        BluetoothAdapter btAdapter = manager.getAdapter();
        if (btAdapter == null) {
            showFinishingAlertDialog("Bluetooth Error", "Bluetooth not detected on device",a);
        } else if (!btAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            a.startActivityForResult(enableBtIntent, REQUEST_ENABLE_BLUETOOTH);
        } else if (!btAdapter.isMultipleAdvertisementSupported()) {
            showFinishingAlertDialog("Not supported", "BLE advertising not supported on this device",a);
        } else {
            adv = btAdapter.getBluetoothLeAdvertiser();
        }
    }

    // Pops an AlertDialog that quits the app on OK.
    private void showFinishingAlertDialog(String title, String message, final Activity a) {
        new AlertDialog.Builder(a)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        a.finish();
                    }
                }).show();
    }


    private AdvertiseCallback createAdvertiseCallback() {
        return new AdvertiseCallback() {
            @Override
            public void onStartFailure(int errorCode) {
                processFailureCallback(errorCode);
            }
        };
    }



    private boolean isValidHex(String s, int len) {
        return !(s == null || s.isEmpty()) && s.length() == len*2 && s.matches("[0-9A-F]+");
    }

    private AdvertiseData getAdvertiseData() throws IOException {
        byte[] serviceData = null;

        serviceData = buildServiceData();

        return new AdvertiseData.Builder()
                .addServiceData(SERVICE_UUID, serviceData)
                .addServiceUuid(SERVICE_UUID)
                .setIncludeTxPowerLevel(false)
                .setIncludeDeviceName(false)
                .build();
    }

    private AdvertiseData getAdvertiseUriData(Uri uri) throws IOException, URISyntaxException {
        return new AdvertiseData.Builder()
                .setIncludeDeviceName(false)
                .setIncludeTxPowerLevel(false)
                .addServiceUuid(SERVICE_UUID)
                .addServiceData(SERVICE_UUID, buildDataPacket(uri))
                .build();
    }


    private AdvertiseSettings getAdvertiseSettings() {
        return new AdvertiseSettings.Builder()
                .setAdvertiseMode(advertiseMode)
                .setTxPowerLevel(txPowerLevel)
                .setConnectable(true)
                .build();
    }

    private AdvertiseSettings getAdvertiseUriSettings() {
        return new AdvertiseSettings.Builder()
                .setAdvertiseMode(advertiseMode)
                .setTxPowerLevel(txPowerLevel)
                .setConnectable(false)
                .setTimeout(0)
                .build();
    }

    private byte[] buildServiceData() throws IOException {
        byte txPower = txPowerLevelToByteValue();
        byte[] namespaceBytes = toByteArray(getUid_namespace());

        Log.d(TAG,new String(namespaceBytes));

        byte[] instanceBytes = toByteArray(getUid_instance());
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        os.write(new byte[]{FRAME_TYPE_UID, txPower});
        os.write(namespaceBytes);
        os.write(instanceBytes);
        return os.toByteArray();
    }

    private byte[] buildDataPacket(Uri uri) throws URISyntaxException {
        //We cannot read the TX power from Android. This uses a sane default.
        UriBeacon model = new UriBeacon.Builder()
                .uriString(uri.toString())
                .build();
        byte[] uriBytes = model.getUriBytes();
        //Flags + Power + URI
        ByteBuffer buffer = ByteBuffer.allocateDirect(1 + 1 + uriBytes.length);
        buffer.put(FRAME_TYPE_URL);
        buffer.put(model.getTxPowerLevel()); // This is cheating…we don't really know it
        buffer.put(uriBytes);

        return byteBufferToArray(buffer);
    }

    private static byte[] byteBufferToArray(ByteBuffer bb) {
        byte[] bytes = new byte[bb.position()];
        bb.rewind();
        bb.get(bytes, 0, bytes.length);
        return bytes;
    }

    private byte[] toByteArray(String hexString) {
        // hexString guaranteed valid.
        int len = hexString.length();
        byte[] bytes = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            bytes[i / 2] = (byte) ((Character.digit(hexString.charAt(i), 16) << 4)
                    + Character.digit(hexString.charAt(i + 1), 16));
        }
        return bytes;
    }


    private static byte[] toFullByteArray(String hexString) {
        // hexString guaranteed valid.
        int len = hexString.length();
        byte[] bytes = new byte[(len / 2) + 2];
        bytes[0] = 0x00; //Frame type
        bytes[1] = 0x00; //TX Power
        for (int i = 0; i < len; i += 2) {
            bytes[(i+2) / 2] = (byte) ((Character.digit(hexString.charAt(i), 16) << 4)
                    + Character.digit(hexString.charAt(i + 1), 16));
        }
        return bytes;
    }

    // Converts the current Tx power level value to the byte value for that power
    // in dBm at 0 meters.
    //
    // Note that this will vary by device and the values are only roughly accurate.
    // The measurements were taken with a Nexus 6.
    private byte txPowerLevelToByteValue() {
        switch (txPowerLevel) {
            case AdvertiseSettings.ADVERTISE_TX_POWER_HIGH:
                return (byte) -16;
            case AdvertiseSettings.ADVERTISE_TX_POWER_MEDIUM:
                return (byte) -26;
            case AdvertiseSettings.ADVERTISE_TX_POWER_LOW:
                return (byte) -35;
            default:
                return (byte) -59;
        }
    }

}

