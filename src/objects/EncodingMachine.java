package objects;

import java.io.IOException;


import stega.FFT;
import utilities.Complex;
import utilities.NumOp;
import constants.Constants;

/*
 * A class extending the Machine abstract class, for encoding the payload
 */
public class EncodingMachine extends Machine {
	
	//the Wav object for the encoded audio
	protected Wav wav_out;
	
	public EncodingMachine() {
	}
	
	/***
	 * sets the EncodingMachine's Wav object for encoding
	 * @param wav
	 */
	public void setWavOut(Wav wav) {
		this.wav_out = wav;
	}
	
	/***
	 * given a byte of the payload, encodes it in the Wav object
	 * @param byte_in
	 */
	public void encode(byte byte_in) {

		//if you're on the first channel, read more samples
		if (super.working_channel == 0) {
			channel_data = wav_in.readChannels(numSamples);
		}
		
		//set up an array of frames, one for each bit in the payload byte
		short[][] wavBuffers = new short[8][Constants.frame_size_samples];
		
		//loop through the frames, encoding the payload byte
		for (int j = 0; j < 8; j++) {
			//get the individual bit
			int encodeBit = (int)((byte_in << j >> 7) & (byte)(1));

			//copy the next frame of samples from the current channel into the j-th frame
			System.arraycopy(channel_data[super.working_channel], j*Constants.frame_size_samples, wavBuffers[j], 0, Constants.frame_size_samples);

			//convert the shorts to complex numbers, and run an FFT
			Complex[] wavVals = NumOp.convertToComplex(wavBuffers[j]);
			Complex[] transforms = FFT.fft(wavVals);

			//loop through the selected frequencies, encoding the bit
			for (int i = 0; i < frequencies.length; i++) {
				if (encodeBit == 1) {
					//if the bit is a 1, drop the power of the frequency to 0
					transforms[frequencies[i]] = transforms[frequencies[i]].times(0);
					transforms[transforms.length - frequencies[i]] =
						transforms[transforms.length - frequencies[i]].times(0);
				} 
				else {
					//if the bit is a 0, get the power of the frequency
					double amp = transforms[frequencies[i]].abs();
					
					//and make sure it's at least twice the threshold for decoding a 0
					if(amp < 2*Constants.bit_threshold) {
						double level = Constants.bit_threshold*2/(amp + 1);
						transforms[frequencies[i]] = transforms[frequencies[i]].times(level);
						transforms[transforms.length - frequencies[i]] =
							transforms[transforms.length - frequencies[i]].times(level);
					}
				}
			}
			
			//run an inverse FFT, and convert the Complex numbers back to shorts
			wavVals = FFT.ifft(transforms);
			wavBuffers[j] = NumOp.convertToReal(wavVals);
			
			//copy the new sample data back into the channel
			System.arraycopy(wavBuffers[j], 0, channel_data[super.working_channel], j*Constants.frame_size_samples, Constants.frame_size_samples);
		}
		
		//update the current channel
		super.working_channel++;
		super.working_channel %= wav_in.numChannels;
		
		//if you're back to the first channel, write the updated channels back
		if (super.working_channel == 0) {
			wav_out.writeChannels(channel_data);
		}
	}
	
	/***
	 * skips the next frame of data, for spacing between encodings
	 */
	public void skipThisByte() {
		super.skipThisByte();
		if (super.working_channel == 0) {
			wav_out.writeChannels(channel_data);
		}
	}
	
	/***
	 * finishes the processing of the Wav object -
	 * flushes the updated channels, writes out the rest of the file, and closes the stream
	 */
	public void finishAll() {
		if(super.working_channel != 0) {
			wav_out.writeChannels(channel_data);
		}
		
		byte[] b = new byte[1024];
		
		try {
			int t = wav_in.readData(b);
			while (t > 0) {
				wav_out.write(b, t);
				t = wav_in.readData(b);
			}
			
			wav_in.close();
			wav_out.close();
			
		} catch (IOException e) {
			e.printStackTrace();
		} 
	}
}
