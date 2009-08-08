package ch.idsia.utils;

public class ArrayUtils {
  public static String toString(Object[] a) {
    StringBuilder sb= new StringBuilder();
    sb.append("[");
    for (int i = 0; i < a.length; i++){
      sb.append(String.valueOf(a[i]));
      if (i < a.length-1)
        sb.append(",");
    }
      
    sb.append("]");
    return sb.toString();
  }
  
  /* I realize cutting and pasting is a sin, but so is using Java.
   * If anyone knows of a better solution for this, feel free to change it.
   * */
  public static String toString(double[] a) {
    StringBuilder sb= new StringBuilder();
    sb.append("[");
    for (int i = 0; i < a.length; i++){
      sb.append(String.valueOf(a[i]));
      if (i < a.length-1)
        sb.append(",");
    }
      
    sb.append("]");
    return sb.toString();
  }
  
  public static String toString(float[] a) {
    StringBuilder sb= new StringBuilder();
    sb.append("[");
    for (int i = 0; i < a.length; i++){
      sb.append(String.valueOf(a[i]));
      if (i < a.length-1)
        sb.append(",");
    }
      
    sb.append("]");
    return sb.toString();
  }
  
}
