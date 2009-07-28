package ch.idsia.scenarios.test;

/**
 * Created by IntelliJ IDEA.
 * User: koutnij
 * Date: Jul 27, 2009
 * Time: 4:34:37 PM
 * To change this template use File | Settings | File Templates.
 */
public class EvaluateJLink {


    /** returns {in, rec, out} array. Just to make math and java codes fully independent. */
    public static int[] getDimension() {
        return new int[]{getInputSize()*getInputSize()*2+3, 6, 6};
    }

    /** returns length of an edge of the input window square*/
    public static int getInputSize() {
        return 7;
    }

    public double evaluateLargeSRN (double[][] inputs, double[][] recurrent, double[][] output) {
        // 98 * 6
        // 6*6
        // 6*6
        return 0;
    }

}
