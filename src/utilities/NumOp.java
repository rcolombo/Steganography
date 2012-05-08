package utilities;



/*
 * A class for converting numbers from one type to another
 */
public class NumOp {
	
	/***
	 * converts the given array of shorts into an array of Complex numbers
	 * @param shorts
	 * @return
	 */
	public static Complex[] convertToComplex(short[] shorts) {
		int len = shorts.length;
		Complex[] returns = new Complex[len];

		for (int i = 0; i < len; i++) {
			Complex temp = new Complex(shorts[i], 0);
			returns[i] = temp;
		}

		return returns;
	}

	/***
	 * converts the given array of Complex numbers into an array of shorts
	 * @param c
	 * @return
	 */
	public static short[] convertToReal(Complex[] c) {
		int len = c.length;
		short[] returns = new short[len];

		for (int i = 0; i < len; i++) {
			returns[i] = (short)c[i].re();
		}

		return returns;
	}
}
