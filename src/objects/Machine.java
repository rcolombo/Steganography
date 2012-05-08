package objects;

import constants.Constants;


/*
 * An abstract class to represent a sample-processing machine
 */
public abstract class Machine {
	//the number of channels in the Wav file
	public int numOfChannels;
	
	//the Wav object housing the file being read
	protected Wav wav_in;
	//the frequencies for use in encoding/decoding
	protected int[] frequencies;
	//the current channel to be processed in the WAV file
	protected int working_channel;
	
	//the number of samples to encode a byte
	protected final int numSamples = Constants.frame_size_samples * 8;
	//an array of arrays of samples, one for each channel in the WAV file
	protected short[][] channel_data = null;
	
	//initializes a new Machine instance
	protected Machine() {
		this.working_channel = 0;
	}
	
	/***
	 * sets the Wav object to be read from
	 * @param wav
	 */
	public void setWavIn(Wav wav) {
		this.wav_in = wav;
		this.numOfChannels = wav.numChannels;
	}
	
	/***
	 * sets the frequencies to be analyzed
	 * @param frequencies
	 */
	public void setWorkingFrequencies(int[] frequencies) {
		this.frequencies = frequencies;
	}
	
	/***
	 * skips the next frame of data, for spacing between encodings
	 */
	public void skipThisByte() {
		if (working_channel == 0) {
			channel_data = wav_in.readChannels(numSamples);
		}
		working_channel++;
		working_channel %= wav_in.numChannels;
	}
	
	/***
	 * skips the given number of frames, for spacing between encodings
	 * @param num
	 */
	public void skipBytes(int num) {
		for(int i = 0; i < num; i++) {
			this.skipThisByte();
		}
	}
	
	/***
	 * abstract function, to finish the processing of the Wav file being read
	 */
	public abstract void finishAll();

}
