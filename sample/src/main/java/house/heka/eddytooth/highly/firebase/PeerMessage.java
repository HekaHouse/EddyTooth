package house.heka.eddytooth.highly.firebase;



/**
 * Created by aron on 7/31/16.
 */

public class PeerMessage {
    public String iv;
    public String from;
    public long posted;
    public String encMessage;
    public String ephemeralPubKey;
    public boolean isLocal = false;
    public boolean seen = false;
    public String decMessage;
    public String eid;
    public String encryptedWithPubKey;

    public PeerMessage() {

    }

    //public PeerMessage(String from, long posted, EncryptedShare share, String ephemeral) {
    public PeerMessage(String from, long posted, String ephemeral) {
        this.from = from;
        this.posted = posted;
//        this.encMessage = share.encryptedStr;
//        this.iv = share.iv;
        this.ephemeralPubKey = ephemeral;
    }
    public PeerMessage(String from, String message) {
        this.from = from;
        this.decMessage = message;
        this.posted = System.currentTimeMillis();
        this.isLocal = true;
    }
}
