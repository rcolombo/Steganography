package stega;

import java.io.*;
import constants.Constants;
/*
 * A class for converting audio files to/from WAV and MP3
 */
public class Convert{
	
	/***
	 * given the path for an existing audio file, a destination path, and a boolean
	 * for whether it should be compressed/decompressed, converts the file 
	 * @param in
	 * @param out
	 * @param decode
	 */
	public static void convert(String in, String out, boolean decode) {
		Process p = null;
		String[] lame_cmd = {Constants.lame_filepath, "--silent", "", "-V2", in, out};
		System.out.println("Converting to " + out.substring(out.length()-3) + "...");
		if (decode) {
			lame_cmd[2] = "--decode"; 
		}
		try {
			p = new ProcessBuilder(lame_cmd).start();
			p.waitFor();
		} catch (Exception e) {
			System.out.println("Error using lame encoder");
			e.printStackTrace();
		}
	}
}