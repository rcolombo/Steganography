package objects;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteOrder;

import utilities.ByteOp;

/*
 * A wrapper class for handling WAV files
 */
public class Wav {
	//the size of the buffer for reading/writing
	private final int buffer_size = 1024*1024;
	
	//the actual WAV file
	private File wavFile;
	
	//the header of the WAV file
	private byte[] header = new byte[44];
	
	//the input stream, for reading
	private BufferedInputStream data_read;
	//the output stream, for writing
	private BufferedOutputStream data_write;
	
	//the attributes of the header
	public int chunkID;
	public short numChannels;
	public int sampleRate;
	public short bitsPerSample;
	public int dataSize;
	
	/***
	 * given the path to a WAV file, returns a new Wav object
	 * @param file
	 * @return
	 */
	public static Wav newObject(String file) {
		Wav result = null;
		try {
			File f = new File(file);
			f.setReadOnly();
			result = new Wav(f);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return result;
	}

	/***
	 * private creator of a Wav object
	 * @param f
	 * @throws Exception
	 */
	private Wav (File f) throws Exception {
		if (f == null) {
		} else {
			wavFile = f;
			FileInputStream instr = new FileInputStream(f);
			BufferedInputStream binstr = new BufferedInputStream(instr, buffer_size);
			binstr.read(this.header);
			this.setHeader(this.header);
			this.data_read = binstr;
		}
	}
	
	/***
	 * given the header to a WAV file, sets the Wav header attributes
	 * @param header
	 */
	public void setHeader(byte[] header) {
		if (header.length != 44) {
		} else {
			byte[] Int = new byte[4];
			byte[] Short = new byte[2];

			Int = ByteOp.getInt(header, 0);
			this.chunkID = ByteOp.bytesToInt(Int, ByteOrder.LITTLE_ENDIAN);

			Short = ByteOp.getShort(header, 22);
			this.numChannels = ByteOp.bytesToShort(Short, ByteOrder.LITTLE_ENDIAN);

			Int = ByteOp.getInt(header, 24);
			this.sampleRate = ByteOp.bytesToInt(Int, ByteOrder.LITTLE_ENDIAN);

			Short = ByteOp.getShort(header, 34);
			this.bitsPerSample = ByteOp.bytesToShort(Short, ByteOrder.LITTLE_ENDIAN);

			Int = ByteOp.getInt(header, 40);
			this.dataSize = ByteOp.bytesToInt(Int, ByteOrder.LITTLE_ENDIAN);
		}
	}
	
	/***
	 * given a new file path, copies this Wav's WAV file to a new file at that location,
	 * and returns a new Wav object using the new file
	 * @param new_name
	 * @return
	 */
	public Wav cloneForWrite(String new_name) {
		try {
			File f = new File(new_name);
			f.createNewFile();
			f.setWritable(true);
			FileOutputStream outstr = new FileOutputStream(f);
			byte[] buff = new byte[44];
			System.arraycopy(this.header, 0, buff, 0, 44);
			outstr.write(buff);
			outstr.flush();
			outstr.close();
			return new Wav(f);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	/***
	 * reads from the Wav's input stream into the given byte array
	 * @param b
	 * @return
	 * @throws IOException
	 */
	public int readData(byte[] b) throws IOException {
		if (data_read == null) {
			return -1;
		} else {
			return data_read.read(b);
		}
	}

	/***
	 * opens an output stream for the Wav object, and writes the Wav's header
	 */
	public void prepareDataToWrite() {
		try {
			data_write = new BufferedOutputStream(new FileOutputStream(wavFile), buffer_size);
			data_write.write(header);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/***
	 * writes the given byte array to the Wav's output stream
	 * @param b
	 * @throws IOException
	 */
	public void write(byte[] b) throws IOException {
		if (data_write == null) {
		} else {
			data_write.write(b);
		}
	}
	
	/***
	 * writes the given number of bytes from the given byte array to the Wav's output stream
	 * @param b
	 * @param length
	 * @throws IOException
	 */
	public void write(byte[] b, int length) throws IOException {
		if (data_write == null) {
		} else {
			data_write.write(b, 0, length);
		}
	}
	
	/***
	 * flushes and closes the read/write streams
	 */
	public void close() {
		try {
			if(data_read == null) {
			} else {
				data_read.close();
			}
			if (data_write == null) {
			} else {
				data_write.flush();
				data_write.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/***
	 * reads samples (as shorts) from the input stream into the given array
	 * @param samples
	 * @return
	 */
	public int readSamples(short[] samples) {
		int sampleRead = 0;
		try {
			byte[] buff = new byte[samples.length*2];
			
			sampleRead = data_read.read(buff) / 2;
			
			for (int i = 0; i < samples.length; i++) {
				byte[] t = new byte[2];
				t[0] = buff[i*2];
				t[1] = buff[i*2+1];
				samples[i] = ByteOp.bytesToShort(t, ByteOrder.LITTLE_ENDIAN);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return sampleRead;
	}
	
	/***
	 * writes the given array of samples (as shorts) into the output stream
	 * @param samples
	 */
	public void writeSamples(short[] samples) {
		try {
			byte[] buff = new byte[samples.length*2];
			
			for (int i = 0; i < samples.length; i++) {
				byte[] temp = ByteOp.shortToBytes(samples[i]);
				buff[i*2] = temp[0];
				buff[i*2+1] = temp[1];
			}
			
			data_write.write(buff);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/***
	 * reads the given number of samples from each channel of the WAV file
	 *   into separate arrays within a short[][]
	 * @param samplePerChannel
	 * @return
	 */
	public short[][] readChannels(int samplePerChannel) {
		short[] data = new short[samplePerChannel*numChannels];
		int sampleRead = readSamples(data);
		short[][] result = new short[numChannels][samplePerChannel];
		
		for(int i = 0; i < samplePerChannel; i ++) {
			for(int j = 0; j < numChannels; j++) {
				result[j][i] = data[i*numChannels+j];
			}
		}
		
		return result;
	}
	
	/***
	 * writes the given array of each channels' samples (a short[][]) into the corresponding channels of the WAV file
	 * @param allChannels
	 */
	public void writeChannels(short[][] allChannels) {
		short[] data = new short[allChannels[0].length*allChannels.length];
		for (int i = 0; i < allChannels[0].length; i++) {
			for (int j = 0; j < allChannels.length; j++) {
				data[i*allChannels.length+j] = allChannels[j][i];
			}
		}
		writeSamples(data);
	}
}
