package house.heka.secure.util;

import android.widget.TextView;

import org.libsodium.jni.SodiumConstants;
import org.libsodium.jni.crypto.Random;
import org.libsodium.jni.keys.KeyPair;
import org.libsodium.jni.keys.SigningKey;
import org.libsodium.jni.keys.VerifyKey;

import static org.libsodium.jni.NaCl.sodium;

/**
 * Created by aron2 on 12/9/2016.
 */

public class HekaSalt {


    /**
     * Generates all sodium keys with a byte[] as seed
     */
    public void generate(KeyPair encryptionKeyPair, SigningKey signingKey) {
        byte[] seed = new Random().randomBytes(SodiumConstants.SECRETKEY_BYTES);
        generateEncryptionKeyPair(encryptionKeyPair, seed);
        generateSigningKeyPair(signingKey, seed);
    }

    /**
     * Generate Encryption Key Pair
     *
     * @param seed as the seed we generated on generate()
     */
    private void generateEncryptionKeyPair(KeyPair encryptionKeyPair, byte[] seed) {
        encryptionKeyPair = new KeyPair(seed);
//        byte[] encryptionPublicKey = encryptionKeyPair.getPublicKey().toBytes();
//        byte[] encryptionPrivateKey = encryptionKeyPair.getPrivateKey().toBytes();
    }

    /**
     * Generate Sign Key Pair
     *
     * @param seed as the seed we generated on generate()
     */
    private void generateSigningKeyPair(SigningKey signingKey, byte[] seed) {
        signingKey = new SigningKey(seed);
        VerifyKey verifyKey = signingKey.getVerifyKey();
        byte[] verifyKeyArray = verifyKey.toBytes();
        byte[] signingKeyArray = signingKey.toBytes();
    }

    public static byte[] getSharedSecret(byte[] mysecret, byte[] yourpublic) {
        byte[] result = new byte[mysecret.length];
        sodium().crypto_scalarmult(result, mysecret, yourpublic);
        return result;
    }

    public static String convertToHex(byte[] data) {
        StringBuilder buf = new StringBuilder();
        for (byte b : data) {
            int halfbyte = (b >>> 4) & 0x0F;
            int two_halfs = 0;
            do {
                buf.append((0 <= halfbyte) && (halfbyte <= 9) ? (char) ('0' + halfbyte) : (char) ('a' + (halfbyte - 10)));
                halfbyte = b & 0x0F;
            } while (two_halfs++ < 1);
        }
        return buf.toString();
    }
}
