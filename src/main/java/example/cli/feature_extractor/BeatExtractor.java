package example.cli.feature_extractor;

import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.File;
import java.io.IOException;
import dsp.AudioDispatcher;
import dsp.beatroot.BeatRootOnsetEventHandler;
import dsp.io.jvm.AudioDispatcherFactory;
import dsp.onsets.ComplexOnsetDetector;
import dsp.onsets.OnsetHandler;

class BeatExtractor implements FeatureExtractorApp, OnsetHandler {

    @Override
    public String name() {
        return "beat";
    }

    @Override
    public String description() {
        String descr = "\tCalculates onsets using a complex domain onset detector. " +
                "\n\tThe output is a semicolon separated list of a timestamp, and a salliance. ";
        descr += "\n\n\tinput.wav\t\ta readable wav file.";
        descr += "";
        return descr;
    }

    @Override
    public String synopsis() {
        String helpString = "input.wav";
        return helpString;
    }

    @Override
    public boolean run(String... args) throws UnsupportedAudioFileException, IOException {
        String inputFile = args[1];
        File audioFile = new File(inputFile);
        int size = 512;
        int overlap = 256;
        int sampleRate = 16000;
        AudioDispatcher
            dispatcher = AudioDispatcherFactory.fromPipe(audioFile.getAbsolutePath(), sampleRate, size, overlap);

        ComplexOnsetDetector detector = new ComplexOnsetDetector(size);
        BeatRootOnsetEventHandler handler = new BeatRootOnsetEventHandler();
        detector.setHandler(handler);

        dispatcher.addAudioProcessor(detector);
        dispatcher.run();

        handler.trackBeats(this);

        return true;
    }

    @Override
    public void handleOnset(double time, double salience) {
        System.out.println(time);
    }
}
