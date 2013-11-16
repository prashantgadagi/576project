import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;


public class PauseButtonEvent extends MouseAdapter {
	
	public PauseButtonEvent() {}
	
	@SuppressWarnings("deprecation")
	@Override
	public synchronized void mouseClicked(MouseEvent arg0) {
		if(!UI.videoPaused) {
			UI.audioThread.suspend();
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			UI.videoThread.suspend();
			UI.videoPaused = true;
		}
	}
}
