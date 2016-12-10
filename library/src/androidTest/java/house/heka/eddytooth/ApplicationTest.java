package house.heka.eddytooth;

import android.app.Application;
import android.test.ApplicationTestCase;

import org.junit.Test;
import org.libsodium.jni.keys.KeyPair;
import org.libsodium.jni.keys.SigningKey;

import house.heka.secure.util.HekaSalt;

import static org.junit.Assert.assertTrue;
import static org.libsodium.jni.NaCl.sodium;

/**
 * <a href="http://d.android.com/tools/testing/testing_android.html">Testing Fundamentals</a>
 */
public class ApplicationTest extends ApplicationTestCase<Application> {
    public ApplicationTest() {
        super(Application.class);
    }
    
}