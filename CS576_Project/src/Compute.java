import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;


public class Compute {
	
	static void getParametersList(ArrayList<MatchParameters> frameParametersList, String filePath)
	{
		InputStream is;
		File file = new File(filePath);
		long len = file.length();
		long frameLength = Constants.WIDTH * Constants.HEIGHT * 3;
		byte[] bytes = new byte[(int) frameLength];
		int totalRead = 0, x = 0, y = 0, r = 0, g = 0, b = 0;
		
		try {		
			is = new FileInputStream(file);
			
			while(totalRead < len) {
				int numRead = 0;
				int offset = 0;
				while (offset < bytes.length && (numRead = is.read(bytes, offset, bytes.length - offset)) >= 0) {
					offset += numRead;
					totalRead += numRead;
				}

				MatchParameters matchParameters = new MatchParameters();
				
				int index = 0;		
				for (y = 0; y < Constants.HEIGHT; y++) {
					for (x = 0; x < Constants.WIDTH; x++) {
						r = bytes[index] & 0xff;
						g = bytes[index + Constants.HEIGHT * Constants.WIDTH] & 0xff;
						b = bytes[index + Constants.HEIGHT * Constants.WIDTH * 2] & 0xff;
						
						index++;
						
						// Getting the HSV values and saving the H
						int[] hsv = new int[3];
						rgbTohsv(r, g, b, hsv);
						if(hsv[0] >= 0)
							matchParameters.h[hsv[0]]++;
						else 
							matchParameters.h[Constants.H_QUANTIZATION_FACTOR - 1]++;
						
						// Getting the YUV values and saving the Y
						int[] yuv = new int[3];
						rgbToyuv(r, g, b, yuv);
						matchParameters.y[yuv[0]]++;
						
						//System.out.println(yuv[0]);
						
					}
				}
				frameParametersList.add(matchParameters);
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
		
		max = Math.max( Math.max( r, g ), b );
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
