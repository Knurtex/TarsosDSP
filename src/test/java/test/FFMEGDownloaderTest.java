package test;


import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import dsp.util.FFMPEGDownloader;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

public class FFMEGDownloaderTest {

    @Test
    @Disabled
    public void testFFMPEGDownload() {
        FFMPEGDownloader d = new FFMPEGDownloader();
        assertTrue(new File(d.ffmpegBinary()).exists());
        assertTrue(new File(d.ffmpegBinary()).length() > 1000);
    }
}
