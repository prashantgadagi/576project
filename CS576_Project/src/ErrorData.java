public class ErrorData {
        public float hError;
        public float yError;
        public float mError;
        public int startIndex;
        public int videoIndex;
        
        public ErrorData() {
                hError = 0;
                yError = 0;
                mError = 0;
                startIndex = 0;
                videoIndex = 0;
        }
        
        public ErrorData(float h, float y, float m, int si, int vi) {
                hError = h;
                yError = y;
                mError = m;
                startIndex = si;
                videoIndex = vi;
        }
        
        public void Copy(ErrorData ed) {
                hError = ed.hError;
                yError = ed.yError;
                mError = ed.mError;
                startIndex = ed.startIndex;
                videoIndex = ed.videoIndex;
        }
        
}