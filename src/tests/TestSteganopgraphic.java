package tests;


import stega.Convert;
import java.io.*;

import junit.framework.TestCase;

import stega.Steganographic;


public class TestSteganopgraphic extends TestCase {
/*	
	public void testClearFiles() {
		String t = System.getProperty("user.dir");
		System.setProperty("user.dir", "src/tests/result_files");
		t = System.getProperty("user.dir");
	}
	*/
	
	public void testEncoding() {
		Convert.convert("src/tests/09.mp3", "original.wav", true);
		String[] cmd = {"--encode", "original.wav", "src/tests/msg.txt", "encoded.wav"};
		Steganographic.main(cmd);
		Convert.convert("encoded.wav", "encoded.mp3", false);
	}
		//String[] cmd2 = {"--encode", "Hewlett.wav", "README", "Outcome.wav"};
		//Steganographic.main(cmd2);

		
//	}

	public void testDecoding() {
		String[] cmd = {"--decode", "encoded.wav", "testoutput_wav.txt"};
		Steganographic.main(cmd);
		Convert.convert("encoded.mp3", "converted_encoded.wav", true);
		String[] cmd2 = {"--decode", "converted_encoded.wav", "testoutput_mp3.txt"};
		Steganographic.main(cmd2);
		
	}
	/*
	public void testAll(){
		Convert.convert("planet.mp3", "planet.wav");
		String[] cmd = {"--encode", "planet.wav", "README", "Outcome.wav"};
		Steganographic.main(cmd);
		Convert.convert("Outcome.wav", "Outcome.mp3");
		Convert.convert("Outcome.mp3", "Final.wav");
		String[] cmd2 = {"--decode", "Final.wav", "finaloutput"};
		Steganographic.main(cmd2);
	}*/
	
	/*
	public void testBytesToNumber() {
		int exp = -123241241;
		byte[] bytes = Steganographic.numberToBytes(exp);
		int act = (int)Steganographic.bytesToNumber(bytes, true);
		
		assertEquals(exp, act);
		
		byte[] bytes2 = {-1, -1, -1, -1};
		act = (int) Steganographic.bytesToNumber(bytes2, true);
		
		assertEquals(-1, act);
		
	}
	
	public void testFFT(){
		Convert.convert("testoutput.wav", "testoutput.mp3");
	}*/
	
}
