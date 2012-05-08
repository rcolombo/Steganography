package stega;

import java.io.*;
import java.nio.ByteOrder;
import java.util.Random;

import objects.DecodingMachine;
import objects.EncodingMachine;
import objects.Wav;
import utilities.ByteOp;
import constants.Constants;

//The main class for encoding/decoding messages
public class Steganographic {

	public Steganographic() {
	}

	/***
	 * The main function for the program
	 * @param args
	 */
	public static void main(String[] args) {
		String mode = args[0];
		File payload = null;
		String resultMsg = "Message processed";

		// the frequencies where data will be encoded
		int[] frequencies = {Constants.frequency_factor_test};

		try {			
			//convert the given carrier file into the canonical WAV form
			String new_path = args[1].replace(".", "-") + ".wav";  
			Convert.convert(args[1], new_path, true);
			
			//create a Wav object using the new carrier file
			Wav carrier = Wav.newObject(new_path);
			
			Random frame_randomizer = new Random(1713); //for randomizing encoded frames, shared by encode/decode
			
			if (mode.equals("--encode")) {
				//create the input stream for the message to encode
				payload = new File(args[2]);
				FileInputStream msg = new FileInputStream(payload);

				long payload_size = payload.length();
				
				//select the intervals (# of frames) between encoded frames
				//  while keeping track of the total amount of frames spanned
				int[] frame_intervals = new int[(int)payload_size];
				long frames_spanned = 0;
				for (int i = 0; i < payload_size ; i++) {
					frame_intervals[i] = 5 + frame_randomizer.nextInt(10);
					frames_spanned += frame_intervals[i];
				}
				
				//check size, including room needed for encoding size
				if (sizeChecking(frames_spanned + (4 * 6* carrier.numChannels), carrier)) {
					//this Wav object has same header as carrier
					//  but writes to the specified location
					Wav wav_out = carrier.cloneForWrite(args[3]);
					wav_out.prepareDataToWrite();
					
					//initialize our encoding machine
					EncodingMachine machine = new EncodingMachine();
					machine.setWavIn(carrier);
					machine.setWavOut(wav_out);
					machine.setWorkingFrequencies(frequencies);
					
					//encode the size first
					encodeSize(machine, (int)payload_size);
										
					int frames_since_encode = 0;
					int bytes_encoded = 0;
					
					//loop through bytes of the carrier, encode a byte of the payload
					//  when we have skipped the necessary amount of frames
					while (bytes_encoded < payload_size) {
						if (frames_since_encode == frame_intervals[bytes_encoded]) {
							machine.encode((byte)msg.read());
							frames_since_encode = 0;
							bytes_encoded++;
						} else {
							machine.skipThisByte();
						}
						
						frames_since_encode++;
						
					}
					
					//finish up
					machine.finishAll();
				}
				else {
					throw new RuntimeException("Carrier not large enough for payload");
				}
			}
			else if(mode.equals("--decode")){
				// opens a stream to the output file
				FileOutputStream outStr = new FileOutputStream(new File(args[2]));
				int frames_since_decode = 0;

				// Initialize our decoding machine
				DecodingMachine machine = new DecodingMachine();
				machine.setWavIn(carrier);
				machine.setWorkingFrequencies(frequencies);
				
				// Ensures that the size of t is at most 1% of the carrier
				// protects against errors in encoding/decoding size
				int size_msg = Math.min(carrier.dataSize / 100, decodeSize(machine));
				
				
				if(size_msg < 0) {
					throw new RuntimeException("Payload size not properly decoded");
				}
				
				// Select the intervals (# of frames) between encoded frames
				int[] frame_intervals = new int[size_msg];
				for (int i = 0; i < size_msg; i++) {
					frame_intervals[i] = 5 + frame_randomizer.nextInt(10);
				}

				int frames_decoded = 0;
				
				// loop until we've decoded every frame
				while(frames_decoded < size_msg) {

					if (frames_since_decode == frame_intervals[frames_decoded]) {
						// Write a byte of decoded data
						outStr.write(machine.decode());
						frames_decoded++;
						frames_since_decode = 0;
					} else {
						machine.skipThisByte();
					}

					frames_since_decode++;
				}
				
				//finish up
				outStr.flush();
				outStr.close();
				machine.finishAll();
			}
			
			//delete the WAV file created for the conversion
			File wav = new File(new_path);
			wav.delete();
		}
		catch(Exception e) {
			resultMsg = e.getMessage();
		}

		finally {
			System.out.println(resultMsg);
		}

	}
	
	
	/***
	 * Encodes the size of the message into audio file
	 * @param m
	 * @param size
	 */
	private static void encodeSize(EncodingMachine m, int size) {
		byte[] bytes = ByteOp.IntToBytes(size);
		
		m.skipBytes(m.numOfChannels*6);

		for(int i = 0; i < bytes.length; i++) {
			m.skipBytes(m.numOfChannels*6);
			m.encode(bytes[i]);
		}
	}
	
	/***
	 * Decodes the size of the message from the audio file
	 * @param m
	 * @return
	 */
	private static int decodeSize(DecodingMachine m) {
		int size = -1;
		byte[] decoded = new byte[4];

		m.skipBytes(m.numOfChannels*6);
		
		for(int i = 0; i < decoded.length; i++) {
			m.skipBytes(m.numOfChannels*6);
			decoded[i] = m.decode();
		}
		
		size = ByteOp.bytesToInt(decoded, ByteOrder.LITTLE_ENDIAN);
		return size;
	}
	
	
    /***
     * Ensures that the carrier file is large enough to hold
     * the size of the message
     * @param size_msg
     * @param carrier
     * @return
     */
	public static boolean sizeChecking(long size_msg, Wav carrier) {
		
		//number of bytes of carrier needed for 1 byte of payload
		int carrierBytesPerPayloadByte = Constants.frame_size_samples*8*carrier.numChannels;
		
		boolean re = false;
		if(size_msg*carrierBytesPerPayloadByte <= carrier.dataSize) {
			re = true;
		}
		return re;
	}
	
	
	/***
	 * Function for getting the next frame index to encode/decode
	 * @param seed
	 * @return
	 */
	public static short getFrequencyIndex(long seed){
		Random myRandom = new Random(seed);
		double mult = myRandom.nextInt(11);
		return (short)(mult + 1);
	}


}