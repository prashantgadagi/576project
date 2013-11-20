import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * 		
 * @author Prashant
 *	Self-explanatory method. Used to pause a video
 */
public class QueryPauseButtonEvent extends MouseAdapter {
	
	public QueryPauseButtonEvent() {}
	
	@SuppressWarnings("deprecation")
	@Override
	public synchronized void mouseClicked(MouseEvent arg0) {
		if(!UI.queryVideoPaused) {
			UI.queryAudioThread.suspend();
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			UI.queryVideoThread.suspend();
			UI.queryVideoPaused = true;
		}
	}
}
