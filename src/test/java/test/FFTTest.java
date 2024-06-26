/*
*      _______                       _____   _____ _____  
*     |__   __|                     |  __ \ / ____|  __ \ 
*        | | __ _ _ __ ___  ___  ___| |  | | (___ | |__) |
*        | |/ _` | '__/ __|/ _ \/ __| |  | |\___ \|  ___/ 
*        | | (_| | |  \__ \ (_) \__ \ |__| |____) | |     
*        |_|\__,_|_|  |___/\___/|___/_____/|_____/|_|     
*                                                         
* -------------------------------------------------------------
*
* TarsosDSP is developed by Joren Six at IPEM, University Ghent
*  
* -------------------------------------------------------------
*
*  Info: http://0110.be/tag/TarsosDSP
*  Github: https://github.com/JorenSix/TarsosDSP
*  Releases: http://0110.be/releases/TarsosDSP/
*  
*  TarsosDSP includes modified source code by various authors,
*  for credits and info, see README.
* 
*/


package test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import dsp.AudioDispatcher;
import dsp.AudioEvent;
import dsp.AudioProcessor;
import dsp.io.TarsosDSPAudioFloatConverter;
import dsp.io.TarsosDSPAudioFormat;
import dsp.io.jvm.JVMAudioInputStream;
import dsp.pitch.PitchDetectionResult;
import dsp.pitch.Yin;
import dsp.util.fft.FFT;
import org.junit.jupiter.api.Test;
import javax.sound.sampled.AudioInputStream;

public class FFTTest {

	
	@Test
	public void testForwardAndBackwardsFFT() {
		final double sampleRate = 44100.0;
		final double f0 = 440.0;
		final double amplitudeF0 = 0.5;
		final int  audioBufferSize = 1024;
		final int numberOfAudioSamples = 2 * audioBufferSize * 44; // about two seconds
		final float[]  floatBuffer = TestUtilities.audioBufferSine(sampleRate,f0,numberOfAudioSamples, amplitudeF0);

	      final TarsosDSPAudioFormat
            format = new TarsosDSPAudioFormat((float) sampleRate, 16, 1, true, false);
	      final TarsosDSPAudioFloatConverter
            converter =  TarsosDSPAudioFloatConverter.getConverter(format);
	      final byte[] byteBuffer = new byte[floatBuffer.length * format.getFrameSize()];

	      converter.toByteArray(floatBuffer, byteBuffer);
	      final ByteArrayInputStream bais = new ByteArrayInputStream(byteBuffer);
          final AudioInputStream inputStream = new AudioInputStream(bais, JVMAudioInputStream.toAudioFormat(format),floatBuffer.length);
          JVMAudioInputStream stream = new JVMAudioInputStream(inputStream);
		  final Yin y = new Yin((float) sampleRate,audioBufferSize);

          final AudioDispatcher dispatcher = new AudioDispatcher(stream, audioBufferSize, 0);
          dispatcher.addAudioProcessor(new AudioProcessor() {
			private FFT fft = new FFT(512);
			@Override
			public void processingFinished() {
			}
			
	
			@Override
			public boolean process(AudioEvent audioEvent) {
				float[] audioFloatBuffer = audioEvent.getFloatBuffer();
				fft.forwardTransform(audioFloatBuffer);
				fft.backwardsTransform(audioFloatBuffer);
				PitchDetectionResult r = y.getPitch(audioFloatBuffer);
				assertTrue(r.isPitched());
				assertEquals(f0,(double) r.getPitch(), 0.04, "Expected around 440Hz");
				return true;
			}
		});
        dispatcher.run();
	}
}
