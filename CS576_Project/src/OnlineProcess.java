import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;


public class OnlineProcess {
	
	static String QUERY_FILE = "C:\\Multimedia\\all_files\\query3.rgb";

	public static void main(String[] args) {

		ArrayList<Integer[]> queryHIntensityList = new ArrayList<Integer[]>();
		ArrayList<ArrayList<Integer[]>> dbHIntensityList = new ArrayList<ArrayList<Integer[]>>();
		ArrayList<Integer> errorPercentageList = new ArrayList<Integer>();
		
		// Reading the hIntensity file and keeping in memory
		getHIntensitiesFromFile(dbHIntensityList, Constants.HINTENSITY_FILE);
		
		// Getting the hIntensity List of query video
		Compute.getHIntensityList(queryHIntensityList, QUERY_FILE);
		
		// Compare query and db
		compare(dbHIntensityList, queryHIntensityList);
		
	}
	
	static void getHIntensitiesFromFile(ArrayList<ArrayList<Integer[]>> dbHIntensityList, String filePath) {
		BufferedReader br;
		String line = "";
		int i = 0, j = 0; 
		
		try {		
			br = new BufferedReader(new FileReader(filePath));
			line = br.readLine();
			while(line != null) {
				String[] splitHeading = line.split(",");
				
				if(splitHeading.length == 2) {
					ArrayList<Integer[]> hIntensityList = new ArrayList<Integer[]>(); 
					for(i = 0; i < Integer.parseInt(splitHeading[1]); i++) {
						line = br.readLine();
						String[] splitData = line.split(",");
				
						Integer[] hIntensity = new Integer[Constants.QUANTIZATION_FACTOR];
						for(j = 0; j < Constants.QUANTIZATION_FACTOR; j++) {
							hIntensity[j] = Integer.parseInt(splitData[j]);
						}
						hIntensityList.add(hIntensity);
					}
					dbHIntensityList.add(hIntensityList);
				}
				
				line = br.readLine();
			}
			
			br.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} 
	}
	
	static void compare(ArrayList<ArrayList<Integer[]>>dbHIntensityList, ArrayList<Integer[]> queryHIntensityList) {
		
		int dbFilesIndex = 0, dbFramesIndex = 0, queryFramesIndex = 0, hIntensityIndex = 0;
		
		// Iterating through the db video files
		for(dbFilesIndex = 0; dbFilesIndex < dbHIntensityList.size(); dbFilesIndex++) {
			float errorPercentage = 100; 
			int bestMatchStartIndex = 0;
			
			// Iterating through the frames of the db video file
			for(dbFramesIndex = 0; dbFramesIndex < dbHIntensityList.get(dbFilesIndex).size() - queryHIntensityList.size(); dbFramesIndex++) {
				
				// Iterating through the query frames
				for(queryFramesIndex = 0; queryFramesIndex < queryHIntensityList.size(); queryFramesIndex++) {
					
					float error = 0;
					// Iterating through the HIntensites in each frame
					for(hIntensityIndex = 0; hIntensityIndex < Constants.QUANTIZATION_FACTOR; hIntensityIndex++) {
						 
						// Comparing the corresponding frame in query with the DB
						error += Math.abs((dbHIntensityList.get(dbFilesIndex).get(dbFramesIndex + queryFramesIndex)[hIntensityIndex]
													- queryHIntensityList.get(queryFramesIndex)[hIntensityIndex]) / Constants.QUANTIZATION_FACTOR);
						//System.out.println(dbHIntensityList.get(dbFilesIndex).get(dbFramesIndex + queryFramesIndex)[hIntensityIndex]
						//		+ " ============ " + queryHIntensityList.get(queryFramesIndex)[hIntensityIndex]);
					}
					error = (error / Constants.QUANTIZATION_FACTOR) * 100;
					
					// Checking if, with the current starting point the error is less
					if(error < errorPercentage) {
						bestMatchStartIndex = dbFramesIndex;
						errorPercentage = error;
					}
				}
			}
			System.out.println("Error%: " + errorPercentage + " Video: " + dbFilesIndex + " Best match Start Index: " + bestMatchStartIndex);
		}
	}
}
