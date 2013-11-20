import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;



public class OfflineProcess {
		
	static PrintWriter fileWriter;
	
	public static void main(String[] args) {
		
		int i = 0;
		
		// Opening the HIntensity file to write
		try {
			fileWriter = new PrintWriter(Constants.HINTENSITY_FILE, "UTF-8");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		
		// Reading all the Input files and extracting the HIntensity and writing to file
		for(i = 0; i < Constants.FILE_NAMES.length; i++) {
			ArrayList<Integer[]> hIntensityList = new ArrayList<Integer[]>();
			String filePath = Constants.BASE_PATH + Constants.FILE_NAMES[i] + Constants.VIDEO_FILE_EXTENSION;
			String fileName = Constants.FILE_NAMES[i];
			
			// Getting the hIntensity List
			Compute.getHIntensityList(hIntensityList, filePath);
			
			// Writing to file
			writeHIntensityToFile(hIntensityList, fileName, fileWriter);
		}
		
		// Closing the HIntensity file
		fileWriter.close();
	}
	
	
	static void writeHIntensityToFile(ArrayList<Integer[]> hIntensityList, String fileName, PrintWriter fileWriter) {
		int i = 0, j = 0;
		
		// Writing the fileName and the number of lines as the 1st line
		fileWriter.println(fileName + "," + hIntensityList.size());
		
		// Writing the Hintensity values - one line per frame
		for(i = 0; i < hIntensityList.size(); i++) {
			for(j = 0; j < Constants.QUANTIZATION_FACTOR; j++) {
				if(j < Constants.QUANTIZATION_FACTOR - 1)
					fileWriter.print(hIntensityList.get(i)[j] + ",");
				else
					fileWriter.print(hIntensityList.get(i)[j]);
			}
			fileWriter.println();
		}
	}

}
