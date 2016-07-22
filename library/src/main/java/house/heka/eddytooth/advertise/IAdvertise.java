package house.heka.eddytooth.advertise;

import android.net.Uri;

/**
 * An interface to support Eddystone broadcasts via EddyAdvertise
 */
public interface IAdvertise {

    /*
    * AdvertiseMode options
    *
    * AdvertiseSettings.ADVERTISE_MODE_BALANCED
    * AdvertiseSettings.ADVERTISE_MODE_LOW_POWER
    * AdvertiseSettings.ADVERTISE_MODE_LOW_LATENCY
    * */
    void setAdvertiseMode(int advertiseMode);

    /*
    * TxPowerLevel options
    *
    * AdvertiseSettings.ADVERTISE_TX_POWER_HIGH
    * AdvertiseSettings.ADVERTISE_TX_POWER_MEDIUM
    * AdvertiseSettings.ADVERTISE_TX_POWER_LOW
    * AdvertiseSettings.ADVERTISE_TX_POWER_ULTRA_LOW
    * */
    void setTxPowerLevel(int txPowerLevel);

    void setInstance(String newInstance);

    void setNamespace(String newNamespace);

    void stopAdvert();

    void startAdvert();

    void startAdvert(Uri uri);

    boolean isAdvertising();

    String getNamespace();
}