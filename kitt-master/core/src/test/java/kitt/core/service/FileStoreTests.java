package kitt.core.service;

import org.apache.tika.mime.MimeType;
import org.apache.tika.mime.MimeTypeException;
import org.apache.tika.mime.MimeTypes;
import org.junit.Test;

/**
 * Created by joe on 2/8/15.
 */
public class FileStoreTests {
    @Test
    public void testMimeType(){
        try {
            MimeType type= MimeTypes.getDefaultMimeTypes().getRegisteredMimeType("image/png");
            System.out.println(type.getExtension());
        } catch (MimeTypeException e) {
            e.printStackTrace();
        }
    }
}
