import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;

public class SliderMotionListener extends MouseAdapter {
	int before;
	int after;
	
	@Override
	public void mouseReleased(MouseEvent arg0) {
		if(UI.play != null) {
			int frameLength = Constants.WIDTH * Constants.HEIGHT * 3;
			long pos = frameLength * UI.slider.getValue();
			
			try {
				UI.videoThread.fileAccess.seek(pos);
				UI.videoThread.totalRead = pos;
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			UI.play.mouseClicked(null);
		}
	}
	
	@Override
	public void mousePressed(MouseEvent arg0) {
		UI.pause.mouseClicked(null);
	}
}
