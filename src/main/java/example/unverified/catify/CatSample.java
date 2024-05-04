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

package example.unverified.catify;

import static dsp.pitch.PitchProcessor.*;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import dsp.AudioDispatcher;
import dsp.AudioEvent;
import dsp.io.jvm.AudioDispatcherFactory;
import dsp.pitch.PitchDetectionHandler;
import dsp.pitch.PitchDetectionResult;
import dsp.pitch.PitchProcessor;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;









class CatSample{
	double duration;
	ArrayList<Float> pitches;
	float avgPitch = 0;
	AudioFormat format ;
	File file;
	public CatSample(File file){
		this.file=file;
		pitches = new ArrayList<Float>();
		try {
			format = AudioSystem.getAudioFileFormat(file).getFormat();
			duration = AudioSystem.getAudioFileFormat(file).getFrameLength()/format.getFrameRate();
			AudioDispatcher dispatcher = AudioDispatcherFactory.fromFile(file,1024, 0);
			dispatcher.addAudioProcessor(new PitchProcessor(
          PitchEstimationAlgorithm.FFT_YIN, format.getSampleRate(), 1024, new PitchDetectionHandler() {
				@Override
				public void handlePitch(PitchDetectionResult pitchDetectionResult, AudioEvent audioEvent) {
					if(pitchDetectionResult.isPitched())
					pitches.add(pitchDetectionResult.getPitch());
				}
			}));
			dispatcher.run();
			for(Float pitch:pitches){
				avgPitch+=pitch;
			}
			avgPitch = avgPitch/pitches.size();
			
		} catch (UnsupportedAudioFileException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public double getDuration(){
		return duration;
	}
	
	public float getAvgPitch(){
		return avgPitch;
	}
	
	public String toString(){
		return ""+avgPitch;
	}

	public double getSampleRate() {
		return format.getSampleRate();
	}

	public File getFile() {
		return file;
	}

	public AudioFormat getFormat() {
		return format;
	}
}
