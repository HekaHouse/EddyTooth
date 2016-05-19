package house.heka.eddytooth.advertise;

/**
 * An interface to support Eddystone broadcasts via EddyAdvertise
 */
public interface IAdvertise {
    void setAdvertiseMode(int advertiseMode);

    void setTxPowerLevel(int txPowerLevel);

    void setInstance(String newInstance);

    void stopAdvert();

    void startAdvert();
}