package objects;

import stega.FFT;
import utilities.Complex;
import utilities.NumOp;
import constants.Constants;

/*
 * A class extending the Machine abstract class, for decoding the payload
 */
public class DecodingMachine extends Machine {

	public DecodingMachine() {
	}
	
	/***
	 * decodes a byte of the payload from the DecodingMachine's Wav object
	 * @return
	 */
	public byte decode() {

		//if you're on the first channel, read more samples
		if (super.working_channel == 0) {
			super.channel_data = wav_in.readChannels(numSamples);
		}
		
		
		//a buffer for the frame to be decoded
		short[] test = new short[Constants.frame_size_samples];
		
		//the decoded byte
		byte returnedByte = 0;
		
		//loop through a byte's worth of encoded frames, pulling out the encoded bits
		for (int j = 0; j < 8; j++) {
			//track how many of the augmented frequencies indicate a 1 for this bit
			int count = 0;
			
			//copy the next frame of samples from the current channel into the decoding frame
			System.arraycopy(channel_data[super.working_channel], j*Constants.frame_size_samples, test, 0, Constants.frame_size_samples);
			
			//convert the shorts to complex numbers, and run an FFT
			Complex[] testComplexes = NumOp.convertToComplex(test);
			Complex[] transforms = FFT.fft(testComplexes);
			
			//loop through the selected frequencies, decoding the bit
			for (int i = 0; i < frequencies.length; i++) {
				//get the power of the frequency
				double tMag = transforms[frequencies[i]].abs();
				
				//check if the power is less than the decoding threshold
				if (tMag < Constants.bit_threshold) {//implies amplitude changed
					//if it is, that's evidence that the bit is a 1, so increment the tracker
					count++;
				}
			}
			
			//a byte to store the decoded bit
			byte temp = 0;
			
			//check if the count matched the number of frequencies encoded
			if (count >= frequencies.length) {
				//if it does, record the bit as a 1
				temp = 1;
			}

			//insert the bit into the decoded byte
			returnedByte |= temp << (7 - j);
		}

		//update the current channel
		super.working_channel++;
		super.working_channel %= wav_in.numChannels;
		
		//return the decoded byte
		return returnedByte;
	}
	
	/***
	 * finishes the processing of the Wav object -
	 * closes the Wav object
	 */
	public void finishAll() {
		wav_in.close();
	}
}
