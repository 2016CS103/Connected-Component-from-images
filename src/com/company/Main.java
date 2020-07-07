package com.company;
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;


 public class Main {
     public static int a = 0;
    static class Pair {
        int x;
        int y;

        // Constructor
        public Pair(int x, int y)
        {
            this.x = x;
            this.y = y;
        }
    }




     private static int[][] convertToArrayLocation(BufferedImage inputImage) {


         WritableRaster wr = inputImage.getRaster() ;
         /*final byte[] pixels = ((DataBufferByte) inputImage.getRaster()

                 .getDataBuffer()).getData(); */// get pixel value as single array from buffered Image

         final int width = inputImage.getWidth(); //get image width value

         final int height = inputImage.getHeight(); //get image height value

         int[][] result = new int[height][width]; //Initialize the array with height and width
         int pixelvalue;

         for (int y=0, row = 0; y < inputImage.getHeight() ; y++) {
             for (int x = 0, col = 0; x < inputImage.getWidth(); x++) {
                 pixelvalue = wr.getSample(x, y, 0);
                 if (pixelvalue <= 80) {

                     pixelvalue = 0;

                 }
                 if (pixelvalue > 80){
                     pixelvalue = 1;
                 }
                 result[row][col] = pixelvalue;
                 col++;

                if (col == width) {

                 col = 0;

                 row++;

             }  }
         }
         //this loop allocates pixels value to two dimensional array

         /*for (int pixel = 0, row = 0, col = 0; pixel < pixels.length; pixel++) {

             int argb = 0;

             argb = (int) pixels[pixel];



             if (argb < 0) { //if pixel value is negative, change to positive //still weird to me

                 argb += 256;

             }



             result[row][col] = argb;

             col++;

             if (col == width) {

                 col = 0;

                 row++;

             }

         }*/

         return result; //return the result as two dimensional array

     } //!end of method!//





    public static void ConvertImage(String fileName) throws IOException {

         BufferedImage img = null;
        // String fileName = "apple.jpeg";

         try {
             //Read in new image file
             img = ImageIO.read(new File(fileName));
         }
         catch (IOException e){
             System.out.println("Error: "+e);
         }
         int h=img.getHeight();
         int w=img.getWidth();


         BufferedImage bufferedImage = new BufferedImage(w,h, BufferedImage.TYPE_INT_RGB);
         if (img == null) {
             System.out.println("No image loaded");
         }
         else {
             for(int i=0;i<w;i++)
             {
                 for(int j=0;j<h;j++)
                 {


                     //Get RGB Value
                     int val = img.getRGB(i, j);
                     //Convert to three separate channels
                     int r = (0x00ff0000 & val) >> 16;
                     int g = (0x0000ff00 & val) >> 8;
                     int b = (0x000000ff & val);
                     int m=(r+g+b);
                     //(255+255+255)/2 =283 middle of dark and light
                     if(m>=383)
                     {
                         // for light color it set white
                         bufferedImage.setRGB(i, j, Color.WHITE.getRGB());
                     }
                     else{
                         // for dark color it will set black
                         bufferedImage.setRGB(i, j, 0);
                     }
                 }
             }
         }


         File file = new File("E:\\MyImage.jpg");
         ImageIO.write(bufferedImage, "jpg", file);

     }






    public static void Calculate_cc(int r, int c, int[][] arr, int[][] C_arr) throws IOException {
        int X_Min = 500000, Y_Min = 500000, X_Max = 0, Y_Max = 0;
        int cc_h = 0, cc_w = 0;
        Stack<Pair> STACK = new Stack<Pair>();
        int j = 0;
        ArrayList<Pair> points = new ArrayList<Pair>();
        int[] x_diff = {-1, 0, 1, 1, 1, 0, -1, -1};
        int[] y_diff = {1, 1, 1, 0, -1, -1, -1, 0};
        int result = 0;

        for (int i = 0; i < 8; i++) {
            try {
                int new_x = r;
                int new_y = c;
                new_x = new_x + x_diff[i];
                new_y = new_y + y_diff[i];
                result = arr[new_x][new_y];
                if (result == 0){
                    Pair p1 = new Pair(new_x, new_y);
                    STACK.push(p1);
                    /*arr[r][c] = 2;
                    Pair e = new Pair(r, c);
                    points.add(e);*/
                }
            } catch (Exception e) {
                continue;
            }
        }
        arr[r][c] = 2;
        Pair g = new Pair(r, c);
        points.add(g);

        while(!STACK.empty()){
            Pair p2 = STACK.pop();
            if(arr[p2.x][p2.y] != 2) {
                for (int i = 0; i < 8; i++) {
                    try {
                        int new_x = p2.x;
                        int new_y = p2.y;
                        new_x = new_x + x_diff[i];
                        new_y = new_y + y_diff[i];
                        result = arr[new_x][new_y];
                        if (result == 0) {
                            Pair p1 = new Pair(new_x, new_y);
                            STACK.push(p1);
                        }

                    } catch (Exception e) {

                        continue;
                    }
                }
                arr[p2.x][p2.y] = 2;
                Pair e = new Pair(p2.x, p2.y);
                points.add(e);
            }

        }

        for(Pair pair : points){
            if(X_Min > pair.x){
                X_Min = pair.x;
            }
            if(Y_Min > pair.y){
                Y_Min = pair.y;
            }
            if(X_Max < pair.x){
                X_Max = pair.x;
            }
            if(Y_Max < pair.y){
                Y_Max = pair.y;
            }
        }

        cc_h = (X_Max - X_Min) + 1;
        cc_w = (Y_Max - Y_Min) + 1;
        int[][] CC_Array = new int[cc_h][cc_w];
        //Without overlapping
        for (int n = 0; n < CC_Array.length; n++) {
            for (int k = 0; k < CC_Array[n].length; k++) {
                CC_Array[n][k] = 1;
            }
        }
        for(Pair pair : points){
            int k = pair.x - X_Min;
            int l = pair.y - Y_Min;
            CC_Array[k][l] = 0;
        }

        //For overlapping
        /*for(int m = X_Min; m < cc_h + X_Min ; m++){
            for(int k = Y_Min; k < cc_w + Y_Min ; k++){
                //  CC_Array[m - X_Min][k - Y_Min] = C_arr[m][k];
                //  CC_Array[m - r][k - c] = C_arr[m][k];
                int x = m - X_Min;
                int y = k - Y_Min;
                CC_Array[x][y] = C_arr[m][k];
            }
        }*/
        System.out.println("Connected_Component");
        for (int n = 0; n < CC_Array.length; n++) {
            for (int k = 0; k < CC_Array[n].length; k++) {
                System.out.print(CC_Array[n][k] + "\t");
            }
            System.out.println();
        }

        try{
        BufferedImage img = new BufferedImage(cc_w, cc_h, BufferedImage.TYPE_INT_RGB);
        for (int i = 0; i < cc_h; i++) {
            for (int p = 0; p < cc_w; p++) {

                int rgb = CC_Array[i][p];
                if (rgb >= 1)// 0xFFFFFF )
                {
                    rgb = 0xFFFFFF;
                } else //if(rgb < 0)
                {
                    rgb = 0;
                }
                img.setRGB(p, i, rgb);

            }
        }
           // File outputfile= new File("E:\\connected");
            String Top, Bottom, left, Right;
            Top = Integer.toString(X_Min);
            Bottom = Integer.toString(X_Max);
            left = Integer.toString(Y_Min);
            Right = Integer.toString(Y_Max);
            String name = Integer.toString(a++);
            File output = new File("E:\\conn\\" + "T_"+Top+"_B_"+Bottom+"_L_"+left+"_R_"+Right +"_CC"+name+".jpg");
            ImageIO.write(img, "jpg", output);
        }
            catch (IOException e) {
                System.out.println(e);
            }
    }








    public static void main(String[] args) throws IOException {
	// write your code here
        /*int[][] array = new int[][]{
                new int[] { 0, 1, 1},
                new int[] { 1, 0, 1},
                new int[] { 1, 1, 1},
        };*/
        int[][] array = new int[][]{
        new int[] { 0, 0, 0, 1, 1, 1, 1, 1, 1, 1 },
        new int[] { 1, 1, 1, 0, 1, 1, 0, 0, 0, 0},
        new int[] { 0, 0, 1, 1, 1, 1, 1, 1, 0, 0},
        new int[] { 1, 1, 1, 1, 0, 0, 1, 1, 1, 1},
        new int[] { 1, 1, 1, 1, 0, 0, 1, 1, 1, 1},

        };

        int[][] Copy_Array = new int[][]{
                new int[] { 0, 0, 0, 1, 1, 1, 1, 1, 1, 1 },
                new int[] { 1, 1, 1, 0, 1, 1, 0, 0, 0, 0},
                new int[] { 0, 0, 1, 1, 1, 1, 1, 1, 0, 0},
                new int[] { 1, 1, 1, 1, 0, 0, 1, 1, 1, 1},
                new int[] { 1, 1, 1, 1, 0, 0, 1, 1, 1, 1},

        };
        /*for (int i = 0; i < array.length; i++) {
            for (int j = 0; j < array[i].length; j++) {
                if (array[i][j] == 0) {
                    Calculate_cc(i, j, array, Copy_Array);
                }
            }

        }*/

       // ConvertImage("E:\\apple.jpg");
        BufferedImage inputImage = ImageIO.read(new File("E:\\test.jpg")); //load the image from this current folder

        int[][] result = convertToArrayLocation(inputImage); //pass buffered image to the method and get back the result
        int[][] C_result = convertToArrayLocation(inputImage);

        for (int n = 0; n < result.length; n++) {
            for (int k = 0; k < result[n].length; k++) {
                if (result[n][k] == 0) {
                     Calculate_cc(n, k, result, C_result);
                }
             //   System.out.print(result[n][k] + " ");
            }
           // System.out.println();
        }
    }
}
