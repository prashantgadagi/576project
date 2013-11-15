public class PlayVideo {
	public static void main(String[] args) {

		// get the command line parameters
		if (args.length < 2) {
			System.err.println("usage: java -jar PlayWaveFile.jar [filename]");
			return;
		}
		
		PlayAudio playAudio = new PlayAudio(args[0]);
		PlayRGBVideo playVideo = new PlayRGBVideo(args[1]);
		
		
		new Thread(playVideo).start();
		new Thread(playAudio).start();
	}
}
