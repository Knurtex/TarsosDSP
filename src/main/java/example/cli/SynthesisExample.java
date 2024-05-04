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

package example.cli;

import dsp.AudioGenerator;
import dsp.effects.DelayEffect;
import dsp.effects.FlangerEffect;
import dsp.filters.LowPassFS;
import dsp.io.jvm.AudioPlayer;
import dsp.synthesis.AmplitudeLFO;
import dsp.synthesis.NoiseGenerator;
import dsp.synthesis.SineGenerator;
import example.TarsosDSPExampleStarter;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.LineUnavailableException;

/**
 * Shows how a synthesizer can be constructed using some simple ugen blocks.
 * @author Joren Six
 */
public class SynthesisExample extends TarsosDSPExampleStarter {

	@Override
	public String name() {
		return "synthesis";
	}

	@Override
	public void start(String... args) {
		try {
			SynthesisExample.main(args);
		} catch (LineUnavailableException e) {
			throw new RuntimeException(e);
		}
	}

	public String description(){
		return "Audio synthesis example";
	}

	public boolean hasGUI(){
		return false;
	}

	public static void main(String... args) throws LineUnavailableException{
		SharedCommandLineUtilities.printPrefix();
		System.out.println("TarsosDSP audio synthesis example.");

		AudioGenerator generator = new AudioGenerator(1024,0);
		generator.addAudioProcessor(new NoiseGenerator(0.2));
		generator.addAudioProcessor(new LowPassFS(1000,44100));
		generator.addAudioProcessor(new LowPassFS(1000,44100));
		generator.addAudioProcessor(new LowPassFS(1000,44100));
		generator.addAudioProcessor(new SineGenerator(0.05,220));
		generator.addAudioProcessor(new AmplitudeLFO(10,0.9));
		generator.addAudioProcessor(new SineGenerator(0.2,440));
		generator.addAudioProcessor(new SineGenerator(0.1,880));
		generator.addAudioProcessor(new DelayEffect(1.5, 0.4, 44100));
		generator.addAudioProcessor(new AmplitudeLFO());
		generator.addAudioProcessor(new NoiseGenerator(0.02));
		generator.addAudioProcessor(new SineGenerator(0.05,1760));
		generator.addAudioProcessor(new SineGenerator(0.01,2460));
		generator.addAudioProcessor(new AmplitudeLFO(0.1,0.7));
		generator.addAudioProcessor(new DelayEffect(0.757, 0.4, 44100));
		generator.addAudioProcessor(new FlangerEffect(0.1,0.2,44100,4));
		generator.addAudioProcessor(new AudioPlayer( new AudioFormat(44100, 16, 1, true, false)));
		generator.run();
	}
}
