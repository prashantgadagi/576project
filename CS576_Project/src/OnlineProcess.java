import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;


public class OnlineProcess {
	
	static String QUERY_FILE = "C:\\Multimedia\\all_files\\query1.rgb";

	public static void main(String[] args) {

		ArrayList<MatchParameters> queryParametersList = new ArrayList<MatchParameters>();
		ArrayList<ArrayList<MatchParameters>> dbParametersList = new ArrayList<ArrayList<MatchParameters>>();
		ArrayList<ArrayList<ErrorData>> errorPercentageList = new ArrayList<ArrayList<ErrorData>>();
		
		// Reading the Parameters file and keeping in memory
		getHIntensitiesFromFile(dbParametersList, Constants.PARAMETERS_FILE);
		
		// Getting the hIntensity List of query video
		Compute.getParametersList(queryParametersList, QUERY_FILE);
		
		// Compare query and db
		compare(dbParametersList, queryParametersList, errorPercentageList);
		
		for(int i = 0; i < errorPercentageList.size(); i++) { 
			for(int j = 0; j < errorPercentageList.get(i).size(); j++) {
				System.out.println("H: " + errorPercentageList.get(i).get(j).hError
									+ "\tY: " + errorPercentageList.get(i).get(j).yError 
									+ "\tStartIndex: " + errorPercentageList.get(i).get(j).startIndex);
			}
			System.out.println("============================");
		}
	}
	
	static void getHIntensitiesFromFile(ArrayList<ArrayList<MatchParameters>> dbParametersList, String filePath) {
		BufferedReader br;
		String line = "";
		int i = 0, j = 0, k = 0; 
		
		try {		
			br = new BufferedReader(new FileReader(filePath));
			line = br.readLine();
			while(line != null) {
				String[] splitHeading = line.split(",");
				
				if(splitHeading.length == 2) {
					ArrayList<MatchParameters> videoParametersList = new ArrayList<MatchParameters>(); 
					for(i = 0; i < Integer.parseInt(splitHeading[1]); i++) {
						line = br.readLine();
						String[] splitData = line.split(",");
				
						MatchParameters matchParameters = new MatchParameters();
						
						// Getting the H values
						for(j = 0; j < Constants.H_QUANTIZATION_FACTOR; j++) {
							matchParameters.h[j] = Integer.parseInt(splitData[j]);
						}
						
						// Getting the Y values
						for(j = 0; j < Constants.Y_QUANTIZATION_FACTOR; j++) {
							matchParameters.y[j] = Integer.parseInt(splitData[Constants.H_QUANTIZATION_FACTOR + j]);
						}
						videoParametersList.add(matchParameters);
					}
					dbParametersList.add(videoParametersList);
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
	
	static void compare(ArrayList<ArrayList<MatchParameters>> dbParametersList, ArrayList<MatchParameters> queryParametersList, ArrayList<ArrayList<ErrorData>> errorPercentageList) {
		
		int dbFilesIndex = 0, dbFramesIndex = 0, queryFramesIndex = 0, hIndex = 0, yIndex = 0;
		
		// Iterating through the db video files
		for(dbFilesIndex = 0; dbFilesIndex < dbParametersList.size(); dbFilesIndex++) {
			
			ArrayList<ErrorData> fileErrorData = new ArrayList<ErrorData>(); 
			// Iterating through the frames of the db video file
			for(dbFramesIndex = 0; dbFramesIndex < dbParametersList.get(dbFilesIndex).size() - queryParametersList.size() + 1; dbFramesIndex++) {
				
				float hQueryWindowError = 0, yQueryWindowError = 0;
				ErrorData queryWindowErrorData = new ErrorData();
				
				// Iterating through the query frames
				for(queryFramesIndex = 0; queryFramesIndex < queryParametersList.size(); queryFramesIndex++) {
					
					float hFrameError = 0, yFrameError = 0;
		
					// Iterating through the H parameter
					for(hIndex = 0; hIndex < Constants.H_QUANTIZATION_FACTOR; hIndex++) {
						// Comparing the corresponding frame in query with the DB
						hFrameError += Math.abs((dbParametersList.get(dbFilesIndex).get(dbFramesIndex + queryFramesIndex).h[hIndex]
													- queryParametersList.get(queryFramesIndex).h[hIndex]));
					}
					hFrameError = (hFrameError / (2 * Constants.WIDTH * Constants.HEIGHT));
					
					// Adding to the query window error
					hQueryWindowError += hFrameError;
					
					// Iterating through the Y parameter
					for(yIndex = 0; yIndex < Constants.Y_QUANTIZATION_FACTOR; yIndex++) {	 
						// Comparing the corresponding frame in query with the DB
						yFrameError += Math.abs(dbParametersList.get(dbFilesIndex).get(dbFramesIndex + queryFramesIndex).y[yIndex]
								- queryParametersList.get(queryFramesIndex).y[yIndex]);
					}
					yFrameError = (yFrameError / (2 * Constants.WIDTH * Constants.HEIGHT));
					
					if(yFrameError > 1) {
						//System.out.println("Yframe: " + yFrameError);
					}
					
					// Adding to the query window error
					yQueryWindowError += yFrameError;
				}
				
				queryWindowErrorData.hError = (hQueryWindowError / queryParametersList.size()) * 100;
				queryWindowErrorData.yError = (yQueryWindowError / queryParametersList.size()) * 100;
				queryWindowErrorData.startIndex = dbFramesIndex;
				fileErrorData.add(queryWindowErrorData);
			}
			errorPercentageList.add(fileErrorData);
		}
	}
}
