
public class ErrorData {
	public float hError;
	public float yError;
	public int startIndex;
	public int videoIndex;
	
	public ErrorData() {
		hError = 0;
		yError = 0;
		startIndex = 0;
		videoIndex = 0;
	}
	
	public ErrorData(float h, float y, int si, int vi) {
		hError = h;
		yError = y;
		startIndex = si;
		videoIndex = vi;
	}
	
	public void Copy(ErrorData ed) {
		hError = ed.hError;
		yError = ed.yError;
		startIndex = ed.startIndex;
		videoIndex = ed.videoIndex;
	}
	
}
