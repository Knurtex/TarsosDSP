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


import static dsp.pitch.Goertzel.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.ByteArrayInputStream;
import dsp.AudioDispatcher;
import dsp.io.TarsosDSPAudioFloatConverter;
import dsp.io.TarsosDSPAudioFormat;
import dsp.io.jvm.JVMAudioInputStream;
import dsp.pitch.DTMF;
import dsp.pitch.Goertzel;
import org.junit.jupiter.api.Test;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

public class GoertzelTest {
	
	/**
	 * Generate a buffer with one sine wave.
	 * 
	 * @param f0
	 *            the frequency of the sine wave;
	 * @param size
	 *            the size of the buffer in samples.
	 * @return a buffer (float array) with audio information for the sine wave.
	 */
	public static float[] testAudioBufferSine(final double f0, int size) {
		final double sampleRate = 44100.0;
		final double amplitudeF0 = 1.0;
		final float[] buffer = new float[size];
		for (int sample = 0; sample < buffer.length; sample++) {
			final double time = sample / sampleRate;
			buffer[sample] = (float) (amplitudeF0 * Math.sin(2 * Math.PI * f0
					* time));
		}
		return buffer;
	}

	/**
	 * Append float buffers to form one big float buffer.
	 * 
	 * @param floatBuffers
	 *            The float buffers to append.
	 * @return An appended float buffer with all the information in the array of
	 *         buffers.
	 */
	public static float[] appendBuffers(final float[]...floatBuffers){
		int size = 0;
		for(int i = 0 ; i < floatBuffers.length ; i ++){
			size += floatBuffers[i].length;
		}
		final float[] floatBuffer = new float[size];
		int index = 0;
		for(int i = 0 ; i < floatBuffers.length; i ++){			
			for(int j = 0 ; j < floatBuffers[i].length ; j++){
				floatBuffer[index] = floatBuffers[i][j];
				index++;
			}
		}
		return floatBuffer;
	}
	
	
	/**
	 * Test detection of a simple sine wave (one frequency).
	 * @throws LineUnavailableException
	 * @throws UnsupportedAudioFileException
	 */
	@Test
	public void testDetection() throws LineUnavailableException, UnsupportedAudioFileException{
		
		final float[][] floatSinBuffers = {testAudioBufferSine(6000,10240),testAudioBufferSine(2000,10240),testAudioBufferSine(4000,10240)};
		final float[] floatBuffer = appendBuffers(floatSinBuffers);
		final TarsosDSPAudioFormat format = new TarsosDSPAudioFormat(44100, 16, 1, true, false);
		final TarsosDSPAudioFloatConverter converter = TarsosDSPAudioFloatConverter.getConverter(format);
		final byte[] byteBuffer = new byte[floatBuffer.length * format.getFrameSize()];
		assertEquals(2, format.getFrameSize(),"Specified 16 bits so framesize should be 2.");
		converter.toByteArray(floatBuffer, byteBuffer);
		final ByteArrayInputStream bais = new ByteArrayInputStream(byteBuffer);
		final AudioInputStream inputStream = new AudioInputStream(bais, JVMAudioInputStream.toAudioFormat(format),floatBuffer.length);
		JVMAudioInputStream stream = new JVMAudioInputStream(inputStream);
		final AudioDispatcher dispatcher = new AudioDispatcher(stream, 1024, 0);
		
		double[] frequencies = {6000,3000,5000,5800,6500};
		
        dispatcher.addAudioProcessor(new Goertzel(44100,1024,frequencies,new FrequenciesDetectedHandler() {
			@Override
			public void handleDetectedFrequencies(double time,final double[] frequencies, final double[] powers, final double[] allFrequencies, final double allPowers[]) {
				assertEquals((int)frequencies[0],6000,"Should only detect 6000 Hz");
			}
		}));
        //dispatcher.addAudioProcessor(new BlockingAudioPlayer(format,1024, 0));
        dispatcher.run();
	}
	
	/**
	 * Test detection of multiple frequencies.
	 * @throws LineUnavailableException
	 * @throws UnsupportedAudioFileException
	 */
	@Test
	public void testDTMF() throws LineUnavailableException, UnsupportedAudioFileException{
		
		final float[][] floatSinBuffers = {
        DTMF.generateDTMFTone('1'),DTMF.generateDTMFTone('2'),DTMF.generateDTMFTone('3'),DTMF.generateDTMFTone('4'),DTMF.generateDTMFTone('5'),DTMF.generateDTMFTone('6'),DTMF.generateDTMFTone('7'),DTMF.generateDTMFTone('8'),DTMF.generateDTMFTone('9')};
		final float[] floatBuffer = appendBuffers(floatSinBuffers);
		final int stepSize = 512;
		final AudioFormat format = new AudioFormat(44100, 16, 1, true, false);
		final TarsosDSPAudioFloatConverter converter = TarsosDSPAudioFloatConverter.getConverter(JVMAudioInputStream.toTarsosDSPFormat(format));
		final byte[] byteBuffer = new byte[floatBuffer.length * format.getFrameSize()];
		assertEquals(2, format.getFrameSize(),"Specified 16 bits so framesize should be 2.");
		converter.toByteArray(floatBuffer, byteBuffer);
		final ByteArrayInputStream bais = new ByteArrayInputStream(byteBuffer);
		final AudioInputStream inputStream = new AudioInputStream(bais, format,floatBuffer.length);
		JVMAudioInputStream stream = new JVMAudioInputStream(inputStream);
		final AudioDispatcher dispatcher = new AudioDispatcher(stream, stepSize, 0);
		final StringBuilder data = new StringBuilder();
		dispatcher.addAudioProcessor(new Goertzel(44100, stepSize,
				DTMF.DTMF_FREQUENCIES, new FrequenciesDetectedHandler() {
					
					@Override
					public void handleDetectedFrequencies(double time,
							final double[] frequencies, final double[] powers, final double[] allFrequencies, final double allPowers[]) {

						assertEquals(frequencies.length, powers.length,"Number of frequencies should be the same as the number of powers.");
						if (frequencies.length == 2) {
							int rowIndex = -1;
							int colIndex = -1;
							for (int i = 0; i < 4; i++) {
								if (frequencies[0] == DTMF.DTMF_FREQUENCIES[i] || frequencies[1] == DTMF.DTMF_FREQUENCIES[i])
									rowIndex = i;
							}
							for (int i = 4; i < DTMF.DTMF_FREQUENCIES.length; i++) {
								if (frequencies[0] == DTMF.DTMF_FREQUENCIES[i] || frequencies[1] == DTMF.DTMF_FREQUENCIES[i])
									colIndex = i-4;
							}
							if(rowIndex>=0 && colIndex>=0){
								char character = DTMF.DTMF_CHARACTERS[rowIndex][colIndex];
								if(data.length()==0 || character != data.charAt(data.length()-1)){
									data.append(character);
								}
							}
						}
					}
				}));
		//dispatcher.addAudioProcessor(new BlockingAudioPlayer(format, stepSize, 0));
		dispatcher.run();
		assertEquals("123456789", data.toString(),"Decoded string should be 123456789");
		assertEquals(9, data.length(),"Length should be 9");
		
	}

}
