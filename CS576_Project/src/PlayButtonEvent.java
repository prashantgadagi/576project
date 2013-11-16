import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JPanel;

public class PlayButtonEvent extends MouseAdapter {
	JPanel panel;
	
	public PlayButtonEvent() {}
	public PlayButtonEvent(JPanel panel) {
		this.panel = panel;
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public void mouseClicked(MouseEvent arg0) {
		this.panel.repaint();
		if(!UI.videoPaused) {
			PlayAudio playAudio = new PlayAudio("D:\\576project\\extracted\\all_audio_files\\wreck2.wav");
			PlayRGBVideo playVideo = new PlayRGBVideo("D:\\576project\\extracted\\wreck2\\wreck2.rgb", this.panel);
			
			UI.videoThread = new Thread(playVideo);
			UI.audioThread = new Thread(playAudio);
			
			try {
				UI.videoThread.join();
				UI.audioThread.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
			UI.videoThread.start();
			UI.audioThread.start();
		}
		else {
			UI.videoThread.resume();
			UI.audioThread.resume();
			UI.videoPaused = false;
		}
	}
}
