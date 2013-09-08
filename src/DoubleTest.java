import org.apache.hadoop.io.DoubleWritable;


public class DoubleTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		DoubleWritable d1 = new DoubleWritable();
		d1.set(21474836470.0);
		DoubleWritable d2 = new DoubleWritable();
		d2.set((double)3213243432431.0);
		double d = d1.get() + d2.get();
		DoubleWritable d3 = new DoubleWritable();
		d3.set(d);
		System.out.println(d);
	}
}
