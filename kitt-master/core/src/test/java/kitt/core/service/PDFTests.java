package kitt.core.service;

import com.itextpdf.text.DocumentException;
import org.junit.Test;
import org.springframework.util.FileCopyUtils;

import java.io.File;
import java.io.IOException;

/**
 * Created by joe on 1/11/15.
 */
public class PDFTests {
    @Test
    public void testHtml() {
        try {
            File file=PDF.create("1. hello中文内容xxx \n 1. xx \n 1. bb");
            FileCopyUtils.copy(file, new File("a.pdf"));
        } catch (IOException e) {
            e.printStackTrace();
        } catch (DocumentException e) {
            e.printStackTrace();
        }
    }
}
