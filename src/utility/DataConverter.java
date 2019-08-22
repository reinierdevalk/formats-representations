package utility;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import representations.Tablature;
import representations.Transcription;
import de.uos.fmt.musitech.utility.math.Rational;

public class DataConverter {

	public DataConverter() {
		
	}


	/**
	 * Converts the given voice label into a List of voices. In the case of a CoD, the higher voice will be listed first.
	 * 
	 * @param voiceLabel
	 * @return
	 */
	// TESTED
	public static List<Integer> convertIntoListOfVoices(List<Double> voiceLabel) {
		List<Integer> listOfVoices = new ArrayList<Integer>();
		for (int i = 0; i < voiceLabel.size(); i++) {
			if (voiceLabel.get(i) == 1.0) {
				listOfVoices.add(i);
			}
		}	  
		return listOfVoices;
	}


	/**
	 * Converts the given List of voices into a voice label.
	 * 
	 * @param listOfVoices
	 * @return
	 */
	// TESTED
	public static List<Double> convertIntoVoiceLabel(List<Integer> listOfVoices) {
		// Create a voice label and initialise it with only 0.0s
		List<Double> voiceLabel = new ArrayList<Double>();
		for (int i = 0; i < Transcription.MAXIMUM_NUMBER_OF_VOICES; i++) {
			voiceLabel.add(0.0);
		}

		// For each predicted voice in predictedVoices: set the element representing that voice in
		// voiceLabel to 1.0
		for (int j = 0; j < listOfVoices.size(); j++) {
			int predictedVoice = listOfVoices.get(j);
			voiceLabel.set(predictedVoice, 1.0);
		}

		return voiceLabel;
	}


	/**
	 * Converts the given List of durations into a duration label.
	 * 
	 * @param listOfDurations
	 * @return
	 */
	// TESTED
	public static List<Double> convertIntoDurationLabel(List<Integer> listOfDurations) {
		// Create a duration label and initialise it with only 0.0s
		List<Double> durationLabel = new ArrayList<Double>();
		for (int i = 0; i < Transcription.DURATION_LABEL_SIZE; i++) {
			durationLabel.add(0.0);
		}

		// For each predicted duration in listOfDurations: set the element representing that 
		// duration in durationLabel to 1.0
		for (int j = 0; j < listOfDurations.size(); j++) {
			int predictedDur = listOfDurations.get(j);
			durationLabel.set(predictedDur - 1, 1.0);
		}

		return durationLabel;
	}


	/**
	 * Gets the voice assignment of the chord represented by the given voice labels.
	 * 
	 * @param chordVoiceLabels The List of voice labels to interpret
	 * @return
	 */
	// TESTED 
	public static List<Integer> getVoiceAssignment(List<List<Double>> chordVoiceLabels, 
		int highestNumberOfVoices) {
		Integer[] voiceAssignmentArray = new Integer[highestNumberOfVoices];

		// Initialise voiceAssignmentArray with -1s to cover any voices not assigned
		for (int i = 0; i < voiceAssignmentArray.length; i++) {
			voiceAssignmentArray[i] = -1;
		}
		// For each voiceLabel in voiceLabels: find the position(s) j where that voiceLabel contains a 1.0; this is the 
		// voice onset i is assigned to. Set voiceAssignmentArray accordingly.
		for (int i = 0; i < chordVoiceLabels.size(); i++) {
			int currentOnset = i;
			List<Double> currentVoiceLabel = chordVoiceLabels.get(i);
			for (int j = 0; j < currentVoiceLabel.size(); j++) {
				if (currentVoiceLabel.get(j) == 1.0) {
					int currentVoice = j;
					voiceAssignmentArray[currentVoice] = currentOnset; 
				}
			}
		}
		// Turn the voiceAssignmentArray into the List that is to be returned
		List<Integer> voiceAssignment = new ArrayList<Integer>();
		for (int i = 0; i < voiceAssignmentArray.length; i++) {
			voiceAssignment.add(voiceAssignmentArray[i]);
		}
		return voiceAssignment;
	}


	/**
	 * Gets the voice assignment of the chord represented by the List<List>>. Example: given a List with 
	 * elements [0], [1], and [2, 3] (i.e., a chord with voice 0 assigned to onset 0, voice 1 to onset 1, and 
	 * voice 2 and 3 assigned to onset 3, the method will return [0, 1, 2, 2, -1].
	 * 
	 * @param chordVoiceLabels The List of voice labels to interpret
	 * @return
	 */
	// TESTED; TODO not in use
	static List<Integer> getVoiceAssignmentFromListOfVoices(List<List<Integer>> chordVoices, int highestNumberOfVoices) {
		// Make voiceAssignment and set its default values to -1 (voice not active))
		List<Integer> voiceAssignment = new ArrayList<Integer>();
		for (int i = 0; i < highestNumberOfVoices; i++) {
			voiceAssignment.add(-1);
		}

		// Get the voices for each onset
		for (int i = 0; i < chordVoices.size(); i++) {
			List<Integer> onsetVoices = chordVoices.get(i);
			// For each voice, set the element in voiceAssignment with that number to i
			for (int j = 0; j < onsetVoices.size(); j++) {
				int currentVoice = onsetVoices.get(j);
				voiceAssignment.set(currentVoice, i);
			}
		}
		return voiceAssignment;
	}


	/**
	 * Gets the voice labels of the chord represented by the given voice assignment. Examples: 
	 * Given the voiceAssignment [0, 1, 1, 2, -1], the  method will return a List with
	 * as element 0: [1.0, 0.0, 0.0, 0.0, 0.0]
	 * as element 1: [0.0, 1.0, 1.0, 0.0, 0.0]
	 * as element 2: [0.0, 0.0, 0.0, 1.0, 0.0]
	 * 
	 * Given the voiceAssignment [0, -1, 1, -1, 2], the method will return a List with
	 * as element 0: [1.0, 0.0, 0.0, 0.0, 0.0]
	 * as element 1: [0.0, 0.0, 1.0, 0.0, 0.0]
	 * as element 2: [0.0, 0.0, 0.0, 0.0, 1.0]
	 * 
	 * @param voiceAssignment The voice assignment to interpret
	 * @return
	 */
	// TESTED
	public static List<List<Double>> getChordVoiceLabels(List<Integer> voiceAssignment) {

		List<List<Double>> voiceLabels = new ArrayList<List<Double>>();

		// Initialise an empty voice label
		List<Double> emptyVoiceLabel = new ArrayList<Double>();

		for (int i = 0; i < Transcription.MAXIMUM_NUMBER_OF_VOICES; i++) {
			emptyVoiceLabel.add(0.0);
		}
		// Create voice labels for each onset in the chord. The number of onsets is the highest number contained by
		// voiceAssignment + 1
		int numberOfOnsets = Collections.max(voiceAssignment) + 1;

		// For each onset i: find each index at which voiceAssignment contains i. These indices represent the voices
		// i is assigned to
		for (int i = 0; i < numberOfOnsets; i++) {
			// Make currentVoiceLabel and initialise it as emptyVoiceLabel
			List<Double> currentVoiceLabel = new ArrayList<Double>(emptyVoiceLabel);
			// For each voice j: if the value of j equals onset i, i is assigned to voice j. Thus, element j of 
			// currentVoiceLabel should be set to 1.0
			for (int j = 0; j < voiceAssignment.size(); j++) {
				int currentValue = voiceAssignment.get(j);
				if (currentValue == i) {
					currentVoiceLabel.set(j, 1.0);
				}
			}
			// Add currentVoiceLabel to voiceLabels
			voiceLabels.add(currentVoiceLabel);
		}
		return voiceLabels;
	}


	/**
	 * Gets the voice(s) assigned to each onset of the chord represented by the given voice labels. Since each 
	 * onset may contain a CoD, a List of Lists is returned (in which the element at position 0 represents the top
	 * voice(s)). 
	 *   
	 * @param chordVoiceLabels
	 * @return
	 */
	// TESTED
	public static List<List<Integer>> getVoicesInChord(List<List<Double>> chordVoiceLabels) {
		List<List<Integer>> voicesInChord = new ArrayList<List<Integer>>();
		for (int i = 0; i < chordVoiceLabels.size(); i++) {
			List<Double> currentVoiceLabel = chordVoiceLabels.get(i);
			List<Integer> currentNoteVoices = new ArrayList<Integer>();
			// Determine at which position(s) currentVoiceLabel contains a 1.0; this will be 
			// the voice(s) the current note is assigned to. Add these voices to currentNoteVoices. 
			for (int j = 0; j < currentVoiceLabel.size(); j++) {
				if (currentVoiceLabel.get(j) == 1.0) {
					currentNoteVoices.add(j);
				}
			}
			// Add the voice(s) assigned to the current onset to voicesInChord 
			voicesInChord.add(currentNoteVoices);
		}
		return voicesInChord;
	}


	/**
	 * Gets the duration(s) encoded by the given duration label. Returns a Rational[] containing <br>
	 *   as element 0: the duration encoded by the label, or if the label encodes a CoD, the duration
	 *                 of the longer CoDnote (or, if they have the same duration, of both CoDnotes); <br>
	 *   as element 1: the duration of the shorter CoDnote (only in case of a CoD with two different durations). 
	 *   
	 * @param durationLabel
	 * @return
	 */
	// TESTED
	public static Rational[] convertIntoDuration(List<Double> durationLabel) {
		int shorterDuration = durationLabel.indexOf(1.0) + 1;
		int longerDuration = durationLabel.lastIndexOf(1.0) + 1;
		if (shorterDuration == longerDuration) {
			Rational dur = new Rational(shorterDuration, Tablature.SMALLEST_RHYTHMIC_VALUE.getDenom());
			dur.reduce();
			return new Rational[]{dur};
		}
		else {
			Rational longerDur = new Rational(longerDuration, Tablature.SMALLEST_RHYTHMIC_VALUE.getDenom());
			longerDur.reduce();
			Rational shorterDur = new Rational(shorterDuration, Tablature.SMALLEST_RHYTHMIC_VALUE.getDenom());
			shorterDur.reduce();
			return new Rational[]{longerDur, shorterDur};
		}
	}


	/**
	 * Gets the integer encoding of the given duration. The integer encoding is the numerator
	 * of the Rational when it is expressed as a fraction of Transcription.DURATION_LABEL_SIZE
	 * (i.e., 32). Thus, the integer encoding of 1/32 = 1; that of 1/16 = 2; etc.
	 * NB: The integer encoding equals the index of the duration in the duration label + 1.   
	 * 
	 * @param dur
	 * @return
	 */
	// TESTED
	public static int getIntegerEncoding(Rational dur) {
		dur.reduce();
		int multiplier = Transcription.DURATION_LABEL_SIZE / dur.getDenom();
		return dur.getNumer() * multiplier;
	}

}
