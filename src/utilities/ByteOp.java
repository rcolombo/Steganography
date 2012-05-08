package utilities;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/*
 * A class for doing byte-level operations
 */
public class ByteOp {
	/***
	 * given a byte array, a starting point, and a size,
	 *   copies size bytes from the starting point in the byte array
	 *   into a new byte array
	 * @param from
	 * @param start
	 * @param size
	 * @return
	 */
	public static byte[] getBytes(byte[] from, int start,  int size) {
		byte[] result = null;
		if (from == null) {
		} else {
			result = new byte[size];
			for (int i = 0; i < size; i++) {
				result[i] = from[start + i];
			}
		}
		return result;
	}
	
	/***
	 * returns an int's worth of bytes from the given array
	 * @param from
	 * @param index
	 * @return
	 */
	public static byte[] getInt(byte[] from, int index) {
		return ByteOp.getBytes(from, index, 4);
	}

	/***
	 * returns a short's worth of bytes from the given array
	 * @param from
	 * @param index
	 * @return
	 */
	public static byte[] getShort(byte[] from, int index) {
		return ByteOp.getBytes(from, index, 2);
	}

	/***
	 * returns the first four bytes from the given byte array as an int
	 * @param bytes
	 * @param endian
	 * @return
	 */
	public static int bytesToInt(byte[] bytes, ByteOrder endian) {
		ByteBuffer bf = ByteBuffer.wrap(bytes);
		bf.order(endian);
		return bf.getInt();
	}
	
	/***
	 * returns the first eight bytes from the given byte array as a long
	 * @param bytes
	 * @param endian
	 * @return
	 */
	public static long bytesToLong(byte[] bytes, ByteOrder endian) {
		ByteBuffer bf = ByteBuffer.wrap(bytes);
		bf.order(endian);
		return bf.getLong();
	}

	/***
	 * returns the first two bytes from the given byte array as a short
	 * @param bytes
	 * @param endian
	 * @return
	 */
	public static short bytesToShort(byte[] bytes, ByteOrder endian) {
		ByteBuffer bf = ByteBuffer.wrap(bytes);
		bf.order(endian);
		return bf.getShort();
	}


	/***
	 * converts the given short into a two-byte array and returns it
	 * @param num
	 * @return
	 */
	public static byte[] shortToBytes(short num) {
		ByteBuffer bf = ByteBuffer.allocate(2);
		bf.order(ByteOrder.LITTLE_ENDIAN);
		bf.putShort(num);
		return bf.array();
	}
	
	/***
	 * converts the given int into a four-byte array and returns it
	 * @param num
	 * @return
	 */
	public static byte[] IntToBytes(int num) {
		ByteBuffer bf = ByteBuffer.allocate(4);
		bf.order(ByteOrder.LITTLE_ENDIAN);
		bf.putInt(num);
		return bf.array();
	}
	
	/***
	 * converts the given long into an eight-byte array and returns it
	 * @param num
	 * @return
	 */
	public static byte[] LongToBytes(long num) {
		ByteBuffer bf = ByteBuffer.allocate(8);
		bf.order(ByteOrder.LITTLE_ENDIAN);
		bf.putLong(num);
		return bf.array();
	}
}
