import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;

public class SliderMotionListener extends MouseAdapter {	
	@Override
	public void mouseReleased(MouseEvent arg0) {
		if(UI.play != null) {
			int frameLength = Constants.WIDTH * Constants.HEIGHT * 3;
			long pos = frameLength * UI.slider.getValue();
			
			//int audioPos = (int) ((UI.audioThread.playSound.frameLength * 2 ) * ((1.0 * UI.slider.getValue()) / UI.slider.getMaximum()));
			int audioPos = (int) (PlaySound.EXTERNAL_BUFFER_SIZE * UI.slider.getValue());
			try {
				UI.videoThread.fileAccess.seek(pos);
				UI.videoThread.totalRead = pos;
				
				UI.audioThread.playSound.audioInputStream.reset();
				UI.audioThread.playSound.dataLine.flush();
				//UI.audioThread.playSound.dataLine.stop();
				//UI.audioThread.playSound.dataLine.start();
				UI.audioThread.playSound.audioInputStream.skip(audioPos);				
			} catch (IOException e) {
				System.out.println(e.getMessage());
			}
			
			UI.play.mouseClicked(null);
		}
	}
	
	@Override
	public void mousePressed(MouseEvent arg0) {
		UI.pause.mouseClicked(null);
	}
}
