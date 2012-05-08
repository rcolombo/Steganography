package constants;

/*
 * A class housing constants used throughout the project
 */
public class Constants {
	
	//the filepath for the Lame encoder
	public static final String lame_filepath = "/net/course/cs4500wc/bin/lame";
	
	//the minimum frequency to read a 0 when decoding
	public static final int bit_threshold = 20000;
	
	//the length of a frame in which a bit is encoded/decoded
	public static final int frame_size_samples = 64;
	
	//the samples per second in a WAV file
	public static final int samples_per_second = 44100;
	
	//the target frequency for manipulation
	private static final int frequency_test = 16000;
	
	//the index of the FFT closest to the target frequency
	public static final int frequency_factor_test = frequency_factor(frequency_test);

	
	/*
	 * Methods for converting to/from frequency and FFT index
	 */
	
	/***
	 * returns an FFT index for the given frequency
	 * @param frequency
	 * @return
	 */
	private static int frequency_factor (int frequency) {
		return frequency*Constants.frame_size_samples/Constants.samples_per_second;
	}
	
	/***
	 * returns the frequency of the given FFT index
	 * @param frequency_factor
	 * @return
	 */
	public static int threshold_factor (int frequency_factor) {
		return frequency_factor*Constants.samples_per_second/Constants.frame_size_samples;
	}
}
