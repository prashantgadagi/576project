import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class Compute {

	static void getParametersList(
			ArrayList<MatchParameters> frameParametersList, String filePath) {
		InputStream is;
		File file = new File(filePath);
		long len = file.length();
		long frameLength = Constants.WIDTH * Constants.HEIGHT * 3;
		byte[] bytes = new byte[(int) frameLength];
		int totalRead = 0, x = 0, y = 0, r = 0, g = 0, b = 0;

		try {
			is = new FileInputStream(file);

			while (totalRead < len) {
				int numRead = 0;
				int offset = 0;
				while (offset < bytes.length
						&& (numRead = is.read(bytes, offset, bytes.length
								- offset)) >= 0) {
					offset += numRead;
					totalRead += numRead;
				}

				MatchParameters matchParameters = new MatchParameters();

				int index = 0;
				for (y = 0; y < Constants.HEIGHT; y++) {
					for (x = 0; x < Constants.WIDTH; x++) {
						r = bytes[index] & 0xff;
						g = bytes[index + Constants.HEIGHT * Constants.WIDTH] & 0xff;
						b = bytes[index + Constants.HEIGHT * Constants.WIDTH
								* 2] & 0xff;

						index++;

						// Getting the HSV values and saving the H
						int[] hsv = new int[3];
						rgbTohsv(r, g, b, hsv);
						if (hsv[0] >= 0)
							matchParameters.h[hsv[0]]++;

						// Getting the YUV values and saving the Y
						int[] yuv = new int[3];
						rgbToyuv(r, g, b, yuv);
						matchParameters.y[yuv[0]]++;

						// System.out.println(yuv[0]);

					}
				}
				frameParametersList.add(matchParameters);
			}

			is.close();
			if(Constants.motion) {
				System.out.println("No of frames:"+frameParametersList.size());
				List<Double> motionVectorList = Compute.getMotionVectors(filePath);
				for(int i = 0; i < frameParametersList.size() - 1; i++) {
					frameParametersList.get(i).motion = motionVectorList.get(i);
					//System.out.println(motionVectorList.get(i));
				}
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	static void rgbTohsv(int red, int green, int blue, int hsv[]) {
		double max, min, r, g, b, h, s, v;

		r = red / 255.0;
		g = green / 255.0;
		b = blue / 255.0;
		h = 0;

		max = Math.max(Math.max(r, g), b);
		min = Math.min(Math.min(r, g), b);

		v = max;
		if (max != 0.0)
			s = (max - min) / max;
		else
			s = 0.0;

		// Note: In theory, when saturation is 0, then
		// hue is undefined, but for practical purposes
		// we will leave the hue as it was.

		if (s == 0.0) {
			h = -1;
		} else {
			double delta = (max - min);

			if (r == max)
				h = (g - b) / delta;
			else if (g == max)
				h = 2.0 + (b - r) / delta;
			else if (b == max)
				h = 4.0 + (r - g) / delta;

			h *= 60.0;
			while (h < 0.0)
				h += 360.0;
		}

		hsv[0] = (int) h;
		hsv[1] = (int) (s * 255.0);
		hsv[2] = (int) (v * 255.0);
	}

	static void rgbToyuv(int red, int green, int blue, int yuv[]) {
		yuv[0] = (int) ((0.299 * red) + (0.587 * green) + (0.114 * blue));
		yuv[1] = (int) ((-0.147 * red) + (-0.289 * green) + (0.436 * blue));
		yuv[2] = (int) ((0.615 * red) + (-0.515 * green) + (-0.100 * blue));
	}

	public static List<Double> getMotionVectors(String filePath) throws FileNotFoundException {
		List<int[][]> frameList = getFrameListAsIntArray(filePath);
		List<Double> motionVectorList = new ArrayList<Double>();
		for(int i = 0; i < frameList.size() - 1; i++) {
			double mv = computeMotionVector(frameList.get(i), frameList.get(i+1));
			motionVectorList.add(mv);
		}
		return motionVectorList;
	}
	
	/**
	 * 
	 * @param sourceImage
	 * @param referenceImage
	 * @return
	 */
	private static double computeMotionVector(int[][] sourceImage, int[][] referenceImage) {
		int n = (Constants.WIDTH / Constants.MB_SIZE)*(Constants.HEIGHT / Constants.MB_SIZE);
		double[] motionVectors = new double[n];
		n = 0;
		
		for(int j = 0; j < Constants.HEIGHT; j += Constants.MB_SIZE) {
			for(int i = 0; i < Constants.WIDTH; i += Constants.MB_SIZE) {
				int startX = (i - Constants.P) < 0 ? 0 : (i - Constants.P);
				int startY = (j - Constants.P) < 0 ? 0 : (j - Constants.P);
				int endX = (i + Constants.MB_SIZE + Constants.P) > Constants.WIDTH  ? (Constants.WIDTH - Constants.MB_SIZE) : (i + Constants.P);
				int endY = (j + Constants.MB_SIZE + Constants.P) > Constants.HEIGHT  ? (Constants.HEIGHT - Constants.MB_SIZE) : (j + Constants.P);
				
				double[] SAD = new double[(endX-startX) * (endY-startY)];
				int c = 0;
				
				for (int y = startY; y < endY; y++) {
					for (int x = startX; x < endX; x++) {
						SAD[c++] = sumOfAbsouluteDifferences(i, j, x, y, sourceImage, referenceImage);
					}
				}
				
				double minSAD = SAD[0];
				//int minIndex = 0, minIndexX = 0, minIndexY = 0;
				for (int x = 0; x < c; x++) {
					if(SAD[x] < minSAD) {
						minSAD = SAD[x];
						//minIndex = x;
					}
				}
				
				//minIndexX = minIndex / (endY - startY);
				//minIndexY = minIndex - ((endX - startX)*minIndexX) - 1;
				
				motionVectors[n++] = minSAD;
			}
		}
		
		double avg = 0;
		for (int i = 0; i < n; i++) {
			avg += motionVectors[i];
		}
		
		return (avg/n);
	}

	/**
	 * Return the Sum of Absolute Differences of the search area
	 * @param i
	 * @param j
	 * @param x
	 * @param y
	 * @param sourceImage
	 * @param referenceImage
	 * @return
	 */
	private static double sumOfAbsouluteDifferences(int i, int j, int x, int y, int[][] sourceImage, int[][] referenceImage) {
		double sad = 0;
		for (int a = 0; a < Constants.MB_SIZE; a++) {
			for (int b = 0; b < Constants.MB_SIZE; b++) {
				sad += Math.abs((sourceImage[i+a][j+b] - referenceImage[x+a][y+b]));
			}
		}
		return sad;
	}

	/**
	 * 
	 * @param filePath
	 * @return List<Float[][]>
	 * The frame list as a 2-dimensional Int array of H values per pixel  
	 */
	public static List<int[][]> getFrameListAsIntArray(String filePath) {
		List<int[][]> frameList = new ArrayList<int[][]>();
		
		InputStream is;
		File file = new File(filePath);
		long len = file.length();
		long frameLength = Constants.WIDTH * Constants.HEIGHT * 3;
		byte[] bytes = new byte[(int) frameLength];
		int totalRead = 0, x = 0, y = 0, r = 0, g = 0, b = 0;

		try {
			is = new FileInputStream(file);

			while (totalRead < len) {
				int[][] frame = new int[Constants.WIDTH][Constants.HEIGHT];
				int numRead = 0;
				int offset = 0;
				while (offset < bytes.length
						&& (numRead = is.read(bytes, offset, bytes.length
								- offset)) >= 0) {
					offset += numRead;
					totalRead += numRead;
				}

				int index = 0;
				for (y = 0; y < Constants.HEIGHT; y++) {
					for (x = 0; x < Constants.WIDTH; x++) {
						r = bytes[index] & 0xff;
						g = bytes[index + Constants.HEIGHT * Constants.WIDTH] & 0xff;
						b = bytes[index + Constants.HEIGHT * Constants.WIDTH
								* 2] & 0xff;
						index++;
						frame[x][y] = rgbTohsv(r, g, b);
					}
				}
				frameList.add(frame);
			}
			is.close();

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return frameList;
	}
	
	static int rgbTohsv(int red, int green, int blue) {
		double max, min, r, g, b, h, s;

		r = red / 255.0;
		g = green / 255.0;
		b = blue / 255.0;
		h = 0;

		max = Math.max(Math.max(r, g), b);
		min = Math.min(Math.min(r, g), b);

		if (max != 0.0)
			s = (max - min) / max;
		else
			s = 0.0;

		// Note: In theory, when saturation is 0, then
		// hue is undefined, but for practical purposes
		// we will leave the hue as it was.

		if (s == 0.0) {
			h = -1;
		} else {
			double delta = (max - min);

			if (r == max)
				h = (g - b) / delta;
			else if (g == max)
				h = 2.0 + (b - r) / delta;
			else if (b == max)
				h = 4.0 + (r - g) / delta;

			h *= 60.0;
			while (h < 0.0)
				h += 360.0;
		}

		return (int) h;
	}
}
