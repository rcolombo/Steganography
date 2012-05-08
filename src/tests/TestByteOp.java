package tests;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;

import utilities.ByteOp;
import junit.framework.TestCase;

public class TestByteOp extends TestCase {
	
	public void testBytesToNumber() {
		short act = 0;
		short exp = -1;
		
		byte[] bytes = new byte[2];
		bytes[0] = -1;
		bytes[1] = -1;
		
		act = ByteOp.bytesToShort(bytes, ByteOrder.LITTLE_ENDIAN);
		
		assertEquals(exp, act);
	}
	
	public void testByteBuffer () {
		byte[] bytes = new byte[2];
		bytes[0] = -1;
		bytes[1] = 0;
		ByteBuffer bf = ByteBuffer.wrap(bytes);
		bf.order(ByteOrder.LITTLE_ENDIAN);
		
		short act = (short) bf.getShort();
		short exp = 255;
		
		assertEquals(exp, act);
		
		byte[] a = new byte[2];
		a[0] = -1;
		a[1] = -1;
		bf = ByteBuffer.allocate(2);
		bf.order(ByteOrder.LITTLE_ENDIAN);
		bf.putShort((short)-1);
		bytes = bf.array();
		
		assertEquals(true, Arrays.equals(bytes, a));
	}

}
