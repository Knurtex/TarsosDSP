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

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import dsp.AudioDispatcher;
import dsp.beatroot.Agent;
import dsp.beatroot.AgentList;
import dsp.beatroot.Event;
import dsp.beatroot.EventList;
import dsp.beatroot.Induction;
import dsp.io.jvm.AudioDispatcherFactory;
import dsp.onsets.ComplexOnsetDetector;
import dsp.onsets.OnsetHandler;
import org.junit.jupiter.api.Test;
import javax.sound.sampled.UnsupportedAudioFileException;

public class BeatRootTest {
	@Test
	public void testExpectedOnsets() throws UnsupportedAudioFileException, IOException{
		File audioFile = TestUtilities.onsetsAudioFile();
		String contents = TestUtilities.readFileFromJar("NR45_expected_onsets.txt");
		String[] onsetStrings = contents.split("\n");
		final double[] expectedOnsets = new double[onsetStrings.length*3];
		int i = 0;
		for(String onset : onsetStrings){
			expectedOnsets[i] = Double.parseDouble(onset);
			i++;
		}
		
		AudioDispatcher d = AudioDispatcherFactory.fromFile(audioFile, 2048, 2048-441);
		d.setZeroPadFirstBuffer(true);
		ComplexOnsetDetector b = new ComplexOnsetDetector(2048);
		b.setHandler(new OnsetHandler(){
			int i = 0;
			@Override
			public void handleOnset(double actualTime, double salience) {
				double expectedTime = expectedOnsets[i];
				assertEquals(expectedTime,actualTime,0.0001,"Onset time should be the expected value!");
				i++;
			}
		});		
		d.addAudioProcessor(b);
		d.run();
	}
	
	@Test
	public void testExpectedBeats() throws UnsupportedAudioFileException, IOException{
		File audioFile = TestUtilities.onsetsAudioFile();
		String contents = TestUtilities.readFileFromJar("NR45_expected_beats.txt");
		String[] beatsStrings = contents.split("\n");
		final double[] expectedBeats = new double[beatsStrings.length];
		int i = 0;
		for(String beat : beatsStrings){
			expectedBeats[i] = Double.parseDouble(beat);
			i++;
		}
		i = 0;
	
		/** beat data encoded as a list of Events */
		final EventList onsetList = new EventList();
		
		AudioDispatcher d = AudioDispatcherFactory.fromFile(audioFile, 2048, 2048-441);
		d.setZeroPadFirstBuffer(true);
		ComplexOnsetDetector b = new ComplexOnsetDetector(2048);
		b.setHandler(new OnsetHandler(){
			@Override
			public void handleOnset(double time, double salience) {
				double roundedTime = Math.round(time *100 )/100.0;
				Event e = newEvent(roundedTime,0);
				e.salience = salience;
				onsetList.add(e);
			}});
		d.addAudioProcessor(b);
		d.run();
		
		AgentList agents = null;
		// tempo not given; use tempo induction
		agents = Induction.beatInduction(onsetList);
		agents.beatTrack(onsetList, -1);
		Agent best = agents.bestAgent();
		if (best != null) {
			best.fillBeats(-1.0);
			EventList beats = best.events;
			Iterator<Event> eventIterator = beats.iterator();
			while(eventIterator.hasNext()){
				Event beat = eventIterator.next();
				double expectedTime = expectedBeats[i];
				double actualTime = beat.keyDown;

				assertEquals(expectedTime,actualTime,0.00001,"Beat time should be the expected value!");
				i++;
			}
			
		} else {
			System.err.println("No best agent");
		}
		
	}


	/** Creates a new Event object representing an onset or beat.
	 *  @param time The time of the beat in seconds
	 *  @param beatNum The index of the beat or onset.
	 *  @return The Event object representing the beat or onset.
	 */
	public static Event newEvent(double time, int beatNum) {
		return new Event(time,time, time, 56, 64, beatNum, 0, 1);
	} // newBeat()
}
