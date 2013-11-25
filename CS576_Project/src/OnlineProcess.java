import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;


public class OnlineProcess extends Thread{
	
	public String file = "";

	OnlineProcess(String file) {
		this.file = file;
	}
	
	@SuppressWarnings("unchecked")
	public void run() {
		ArrayList<MatchParameters> queryParametersList = new ArrayList<MatchParameters>();
		ArrayList<ArrayList<MatchParameters>> dbParametersList = new ArrayList<ArrayList<MatchParameters>>();
		ArrayList<ArrayList<ErrorData>> errorPercentageList = new ArrayList<ArrayList<ErrorData>>();
		ArrayList<ErrorData> rankList = new ArrayList<ErrorData>();
		
		// Reading the Parameters file and keeping in memory
		getHIntensitiesFromFile(dbParametersList, Constants.PARAMETERS_FILE);
		
		// Getting the hIntensity List of query video
		Compute.getParametersList(queryParametersList, file);
		
		// Compare query and db
		compare(dbParametersList, queryParametersList, errorPercentageList, rankList);
		
		/*for(int i = 0; i < errorPercentageList.size(); i++) { 
			for(int j = 0; j < errorPercentageList.get(i).size(); j++) {
				System.out.println("H: " + errorPercentageList.get(i).get(j).hError
									+ "\tY: " + errorPercentageList.get(i).get(j).yError
									+ "\tMotion: " + errorPercentageList.get(i).get(j).mError 
									+ "\tStartIndex: " + errorPercentageList.get(i).get(j).startIndex);
			}
			System.out.println("============================");
		}*/
		
		UI.model.removeAllElements();
		sort(rankList);
		for(int i = 0; i < Constants.FILE_NAMES.length; i++) {
			float avg = (2 * rankList.get(i).hError + rankList.get(i).yError) / 2;
			System.out.println("Video: " + Constants.FILE_NAMES[rankList.get(i).videoIndex]
					+ " \tMError: " + rankList.get(i).mError
					+ " \tHError: " + rankList.get(i).hError
					+ " \tYError: " + rankList.get(i).yError 
					+ " \tIndex: " + rankList.get(i).startIndex
					+ " \tAvg: " +  avg);
			//UI.videoFileNames[i] = Constants.FILE_NAMES[rankList.get(i).videoIndex] + "( starts at : "+ rankList.get(i).startIndex+" frame";
			UI.model.addElement(Constants.FILE_NAMES[rankList.get(i).videoIndex] + " - (starts at frame: "+ rankList.get(i).startIndex+" )");
			UI.videoFileValues[i] = Constants.BASE_PATH+Constants.FILE_NAMES[rankList.get(i).videoIndex]+Constants.VIDEO_FILE_EXTENSION;
			UI.audioFileValues[i] = Constants.BASE_PATH+Constants.FILE_NAMES[rankList.get(i).videoIndex]+Constants.AUDIO_FILE_EXTENSION;
		}
		
	}	
	
	static void sort(ArrayList<ErrorData> rankList) {
		if(Constants.motion) {
			for(int i = 0; i < rankList.size(); i++) {
				for(int j = 0; j < rankList.size(); j++) {
					if(rankList.get(i).mError < rankList.get(j).mError) {
						ErrorData edTemp = new ErrorData(rankList.get(i).hError, 
											rankList.get(i).yError, rankList.get(i).mError, rankList.get(i).startIndex, rankList.get(i).videoIndex);
						rankList.get(i).Copy(rankList.get(j));
						rankList.get(j).Copy(edTemp);
					}
				}
			}
		}
		else {
			for(int i = 0; i < rankList.size(); i++) {
				for(int j = 0; j < rankList.size(); j++) {
					if(rankList.get(i).hError < rankList.get(j).hError) {
						ErrorData edTemp = new ErrorData(rankList.get(i).hError, 
											rankList.get(i).yError, rankList.get(i).mError, rankList.get(i).startIndex, rankList.get(i).videoIndex);
						rankList.get(i).Copy(rankList.get(j));
						rankList.get(j).Copy(edTemp);
					}
				}
			}
		}
	}
	
	static void getHIntensitiesFromFile(ArrayList<ArrayList<MatchParameters>> dbParametersList, String filePath) {
		BufferedReader br;
		String line = "";
		int i = 0, j = 0; 
		
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
						
						
						//Getting motion value
						if(Constants.motion) {
							matchParameters.motion = Double.parseDouble(splitData[splitData.length - 1]);
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
	
	static void compare(ArrayList<ArrayList<MatchParameters>> dbParametersList, ArrayList<MatchParameters> queryParametersList, 
			ArrayList<ArrayList<ErrorData>> errorPercentageList, ArrayList<ErrorData> rankList) {
		
		int dbFilesIndex = 0, dbFramesIndex = 0, queryFramesIndex = 0, hIndex = 0, yIndex = 0;
		
		// Initilizing the rankList
		for(int i = 0; i < Constants.FILE_NAMES.length; i++) {
			ErrorData ed = new ErrorData();
			ed.mError = Integer.MAX_VALUE;
			ed.hError = Integer.MAX_VALUE;
			ed.yError = Integer.MAX_VALUE;
			rankList.add(ed);
		}
		
		// Iterating through the db video files
		for(dbFilesIndex = 0; dbFilesIndex < dbParametersList.size(); dbFilesIndex++) {
			
			ArrayList<ErrorData> fileErrorData = new ArrayList<ErrorData>(); 
			// Iterating through the frames of the db video file
			for(dbFramesIndex = 0; dbFramesIndex < dbParametersList.get(dbFilesIndex).size() - queryParametersList.size() + 1; dbFramesIndex++) {
				
				float hQueryWindowError = 0, yQueryWindowError = 0, mQueryWindowError = 0;
				ErrorData queryWindowErrorData = new ErrorData();
				
				// Iterating through the query frames
				for(queryFramesIndex = 0; queryFramesIndex < queryParametersList.size(); queryFramesIndex++) {
					
					float hFrameError = 0, yFrameError = 0, motionError = 0;
		
					// Iterating through the H parameter
					for(hIndex = 0; hIndex < Constants.H_QUANTIZATION_FACTOR; hIndex++) {
						// Comparing the corresponding frame in query with the DB
						hFrameError += (Math.abs(dbParametersList.get(dbFilesIndex).get(dbFramesIndex + queryFramesIndex).h[hIndex]
													- queryParametersList.get(queryFramesIndex).h[hIndex]) * 1.0 / (Constants.WIDTH * Constants.HEIGHT));
					}
					//hFrameError = (hFrameError / (2 * Constants.WIDTH * Constants.HEIGHT));
					
					// Adding to the query window error
					hQueryWindowError += hFrameError;
					
					// Iterating through the Y parameter
					for(yIndex = 0; yIndex < Constants.Y_QUANTIZATION_FACTOR; yIndex++) {	 
						// Comparing the corresponding frame in query with the DB
						yFrameError += (Math.abs(dbParametersList.get(dbFilesIndex).get(dbFramesIndex + queryFramesIndex).y[yIndex]
								- queryParametersList.get(queryFramesIndex).y[yIndex]) * 1.0 / (Constants.WIDTH * Constants.HEIGHT));
					}
					//yFrameError = (yFrameError / (2 * Constants.WIDTH * Constants.HEIGHT));
					
					// Adding to the query window error
					yQueryWindowError += yFrameError;

					//Comparing motion error
					if(Constants.motion) {
						if(queryFramesIndex < queryParametersList.size()  - 1) {
							motionError = (float) Math.abs(dbParametersList.get(dbFilesIndex).get(dbFramesIndex + queryFramesIndex).motion
									- queryParametersList.get(queryFramesIndex).motion);
						}
						mQueryWindowError += motionError;
					}
				}
				
				queryWindowErrorData.hError = (hQueryWindowError / queryParametersList.size()) * 100;
				queryWindowErrorData.yError = (yQueryWindowError / queryParametersList.size()) * 100;
				if(Constants.motion) {
					queryWindowErrorData.mError = (mQueryWindowError / (queryParametersList.size()-1));
				}
				queryWindowErrorData.startIndex = dbFramesIndex;
				queryWindowErrorData.videoIndex = dbFilesIndex;
				fileErrorData.add(queryWindowErrorData);
				
				if(Constants.motion) {
					if(rankList.get(dbFilesIndex).mError > queryWindowErrorData.mError) {
						rankList.get(dbFilesIndex).Copy(queryWindowErrorData);
					}
				}
				else {
					if(rankList.get(dbFilesIndex).hError > queryWindowErrorData.hError) {
						rankList.get(dbFilesIndex).Copy(queryWindowErrorData);
					}
				}
				
			}
			errorPercentageList.add(fileErrorData);
		}
	}
	
	public static float klDivergence(MatchParameters p1, MatchParameters p2, int flag) {
	      float klDiv = 0;

	      if( flag == 1) {
		      for (int i = 0; i < p1.h.length; ++i) {
		        if (p1.h[i] == 0) { continue; }
		        if (p2.h[i] == 0) { continue; } // Limin
	
		        klDiv += (p1.h[i]*1.0 / (Constants.HEIGHT * Constants.WIDTH)) * Math.log( (p1.h[i]*1.0 / (Constants.HEIGHT * Constants.WIDTH)) / (p2.h[i]*1.0 / (Constants.HEIGHT * Constants.WIDTH)) );
		      }
	      } else {
		      for (int i = 0; i < p1.y.length; ++i) {
		        if (p1.y[i] == 0) { continue; }
		        if (p2.y[i] == 0) { continue; } // Limin
	
		        klDiv += (p1.y[i] * 1.0 / (Constants.HEIGHT * Constants.WIDTH)) * Math.log( (p1.y[i] * 1.0 / (Constants.HEIGHT * Constants.WIDTH)) / (p2.y[i] * 1.0 / (Constants.HEIGHT * Constants.WIDTH)) );
	      }
	      }

	      return (float) (klDiv / Math.log(2)); // moved this division out of the loop -DM
	    }
}
