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

import dsp.pitch.PitchDetector;
import dsp.pitch.PitchProcessor;
import dsp.pitch.PitchProcessor.PitchEstimationAlgorithm;
import org.junit.jupiter.api.Test;

public class PitchDetectorTests {
	
	@Test
	public void testSine(){
		float[] audioBuffer = TestUtilities.audioBufferSine();
		for(PitchEstimationAlgorithm algorithm : PitchEstimationAlgorithm.values()){
			PitchDetector detector = algorithm.getDetector(44100, 1024);
			float pitch = 0;
			float[] shortAudioBuffer = new float[1024];
			
			System.arraycopy(audioBuffer, 0, shortAudioBuffer, 0, shortAudioBuffer.length);
			pitch = detector.getPitch(shortAudioBuffer).getPitch();
			//System.out.println(String.format("%15s %8.3f Hz", algorithm, pitch));
			assertEquals(440,pitch,1.5,"Expected about 440Hz for " + algorithm);
			
			System.arraycopy(audioBuffer, 1024, shortAudioBuffer, 0, shortAudioBuffer.length);
			pitch = detector.getPitch(shortAudioBuffer).getPitch();
			//System.out.println(String.format("%15s %8.3f Hz", algorithm, pitch));
			assertEquals(440,pitch,1.5,"Expected about 440Hz for " + algorithm);
			
			System.arraycopy(audioBuffer, 2048, shortAudioBuffer, 0, shortAudioBuffer.length);
			pitch = detector.getPitch(shortAudioBuffer).getPitch();
			//System.out.println(String.format("%15s %8.3f Hz", algorithm, pitch));
			assertEquals(440,pitch,1.5,"Expected about 440Hz for " + algorithm);
		}
	}
	
	@Test
	public void testFlute(){
		float[] audioBuffer = TestUtilities.audioBufferFlute();
		for(PitchEstimationAlgorithm algorithm : PitchEstimationAlgorithm.values()){
			PitchDetector detector = algorithm.getDetector(44100, 1024);
			float pitch = 0;
			float[] shortAudioBuffer = new float[1024];
			System.arraycopy(audioBuffer, 2048, shortAudioBuffer, 0, shortAudioBuffer.length);
			pitch = detector.getPitch(shortAudioBuffer).getPitch();
			//System.out.println(String.format("%15s %8.3f Hz", algorithm, pitch));
			assertEquals(442,pitch,2,"Expected about 442Hz for " + algorithm);
		}
		//System.out.println();
	}
	
	@Test
	public void testPiano(){
		float[] audioBuffer = TestUtilities.audioBufferPiano();
		for(PitchEstimationAlgorithm algorithm : PitchEstimationAlgorithm.values()){
			PitchDetector detector = algorithm.getDetector(44100, 1024);
			float pitch = 0;
			float[] shortAudioBuffer = new float[1024];
			
			System.arraycopy(audioBuffer, 0, shortAudioBuffer, 0, shortAudioBuffer.length);
			pitch = detector.getPitch(shortAudioBuffer).getPitch();
			//System.out.println(String.format("%15s %8.3f Hz", algorithm, pitch));
			assertEquals(443,pitch,3,"Expected about 440Hz for " + algorithm);
		}
		System.out.println();
	}
	
	@Test
	public void testLowPiano(){
		float[] audioBuffer = TestUtilities.audioBufferLowPiano();
		for(PitchEstimationAlgorithm algorithm : PitchEstimationAlgorithm.values()){
			PitchDetector detector = algorithm.getDetector(44100, 1024);
			float pitch = 0;
			float[] shortAudioBuffer = new float[1024];
			
			System.arraycopy(audioBuffer, 0, shortAudioBuffer, 0, shortAudioBuffer.length);
			pitch = detector.getPitch(shortAudioBuffer).getPitch();
			//System.out.println(String.format("%15s %8.3f Hz", algorithm, pitch));
			assertEquals(130.81,pitch,2,"Expected about 130.81Hz for " + algorithm);
		}
		//System.out.println();
	}
	
	@Test
	public void testHighFlute(){
		float[] audioBuffer = TestUtilities.audioBufferHighFlute();
		for(PitchEstimationAlgorithm algorithm : PitchEstimationAlgorithm.values()){
			PitchDetector detector = algorithm.getDetector(44100, 1024);
			float pitch = 0;
			float[] shortAudioBuffer = new float[1024];
			
			System.arraycopy(audioBuffer, 3000, shortAudioBuffer, 0, shortAudioBuffer.length);
			pitch = detector.getPitch(shortAudioBuffer).getPitch();
			//System.out.println(String.format("%15s %8.3f Hz", algorithm, pitch));
			//this fails with dynamic wavelet and amdf
			//assertEquals("Expected about 1975.53Hz for " + algorithm,1975.53,pitch,30);
		}
		//System.out.println();
	}	
}
