import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JPanel;

public class PlayButtonEvent extends MouseAdapter {
	String audio;
	String video;
	boolean isPaused;
	JPanel panel;
	
	public PlayButtonEvent() {}
	public PlayButtonEvent(JPanel panel, String audio, String video) {
		this.audio = audio;
		this.video = video;
		this.panel = panel;
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public void mouseClicked(MouseEvent arg0) {
		if(null == this.audio || null == this.video) {
			this.audio = UI.queryAudio;
			this.video = UI.queryVideo;
		}
		this.panel.repaint();
		if(!UI.videoPaused) {
			PlayAudio playAudio = new PlayAudio(this.audio);
			PlayRGBVideo playVideo = new PlayRGBVideo(this.video, this.panel);
			
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
