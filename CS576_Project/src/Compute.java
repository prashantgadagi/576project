import java.awt.Color;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;



public class Compute {
	
	static void getHIntensityList(ArrayList<Integer[]> hIntensityList, String filePath)
	{
		InputStream is;
		File file = new File(filePath);
		long len = file.length();
		long frameLength = Constants.WIDTH * Constants.HEIGHT * 3;
		byte[] bytes = new byte[(int) frameLength];
		int totalRead = 0, i = 0, x = 0, y = 0, r = 0, g = 0, b = 0;
		
		try {		
			is = new FileInputStream(file);
			
			while(totalRead < len) {
				int numRead = 0;
				int offset = 0;
				while (offset < bytes.length && (numRead = is.read(bytes, offset, bytes.length - offset)) >= 0) {
					offset += numRead;
					totalRead += numRead;
				}

				Integer[] hIntensity = new Integer[Constants.QUANTIZATION_FACTOR];
				for(i = 0; i < Constants.QUANTIZATION_FACTOR; i++) {
					hIntensity[i] = 0;
				}
				
				int index = 0;		
				for (y = 0; y < Constants.HEIGHT; y++) {
					for (x = 0; x < Constants.WIDTH; x++) {
						r = bytes[index];
						g = bytes[index + Constants.HEIGHT * Constants.WIDTH];
						b = bytes[index + Constants.HEIGHT * Constants.WIDTH * 2];
						index++;
						//hIntensity[(r & 0xff)]++;	 
						
						int[] hsv = new int[3];
						rgbTohsv((r & 0xff), (g & 0xff), (b & 0xff), hsv);
						
						int[] yuv = new int[3];
						rgbToyuv((r & 0xff), (g & 0xff), (b & 0xff), yuv);
						
						if(hsv[0] >= 0)
							hIntensity[hsv[0]]++;
						//System.out.println(yuv[0]);
						
					}
				}
				hIntensityList.add(hIntensity);
			}

			is.close();
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} 
	}
	

	static void rgbTohsv( int red, int green, int blue, int hsv[] )
	{
        double max, min, r, g, b, h, s, v;

        r = red / 255.0;
        g = green / 255.0;
        b = blue / 255.0;
        h = 0;

        // find the maximum
        max = Math.max( Math.max( r, g ), b );

        // find the minimum
        min = Math.min( Math.min( r, g ), b );

        v = max;
        if( max != 0.0 )
            s = (max-min)/max;
        else
            s = 0.0;

//      Note: In theory, when saturation is 0, then
//            hue is undefined, but for practical purposes
//            we will leave the hue as it was.

    	if( s == 0.0 ) {
            h = -1;
    	}
    	else {
            double delta = (max-min);

            if( r == max )
                h = (g-b)/delta;
            else if( g == max )
                h = 2.0 + (b-r)/delta;
            else if( b == max )
                h = 4.0 + (r-g)/delta;
            
            h *= 60.0;
            while( h<0.0 )
            	h += 360.0;
        }

        hsv[0] = (int) h;
        hsv[1] = (int) (s * 255.0);
        hsv[2] = (int) (v * 255.0);
	}

	static void rgbToyuv(int red, int green, int blue, int yuv[] ) 
	{
		yuv[0] = (int)((0.299 * red) + (0.587 * green) + (0.114 * blue));
		yuv[1] = (int)((-0.147 * red) + (-0.289 * green) + (0.436 * blue));
		yuv[2] = (int)((0.615 * red) + (-0.515 * green) + (-0.100 * blue));
	}
}
