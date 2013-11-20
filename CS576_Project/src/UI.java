import java.awt.EventQueue;
import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;


@SuppressWarnings("serial")
public class UI extends JFrame {

	public JPanel contentPane;
	public static JTextField textField;
	public JPanel origVideo;
	public JPanel queryVideoPanel;
	public JButton queryVideoPlay;
	public JButton queryVideoPause;
	public JButton queryVideoStop;
	public JButton origVideoPlay;
	public JButton origVideoPause;
	public JButton origVideoStop;
	public static boolean videoPaused;
	public static Thread videoThread;
	public static Thread audioThread;
	public static String queryAudio;
	public static String queryVideo;
	

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					final UI frame = new UI();
					frame.setVisible(true);
					String audio = "D:\\576project\\extracted\\all_audio_files\\wreck2.wav";
					String video = "D:\\576project\\extracted\\wreck2\\wreck2.rgb";
					
					frame.origVideoPlay.addMouseListener(new PlayButtonEvent(frame.origVideo, audio, video));
					frame.origVideoPause.addMouseListener(new PauseButtonEvent());
					frame.origVideoStop.addMouseListener(new StopButtonEvent(frame.origVideo));
					
					frame.queryVideoPlay.addMouseListener(new PlayButtonEvent(frame.queryVideoPanel, UI.queryAudio, UI.queryVideo));
					frame.queryVideoPause.addMouseListener(new PauseButtonEvent());
					frame.queryVideoStop.addMouseListener(new StopButtonEvent(frame.queryVideoPanel));
					
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public UI() {
		setResizable(false);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(150, 5, 1024, 700);
		this.contentPane = new JPanel();
		this.contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(this.contentPane);
		this.contentPane.setLayout(null);
		
		UI.textField = new JTextField();
		UI.textField.setBounds(191, 44, 257, 20);
		this.contentPane.add(UI.textField);
		UI.textField.setColumns(10);
		
		JButton btnNewButton = new JButton("Browse");
		btnNewButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				JFileChooser fc = new JFileChooser();
				fc.setCurrentDirectory(new File("D:\\576project\\extracted"));
		        int res = fc.showOpenDialog(null);
		        try {
		            if (res == JFileChooser.APPROVE_OPTION) {
		                File file = fc.getSelectedFile();
		                String path = file.getAbsolutePath();
		                UI.textField.setText(path);
		                UI.queryVideo = path;
		                String audioName = path.substring(path.indexOf("query\\query")+6,path.indexOf("query")+12);
		                UI.queryAudio = "D:\\576project\\extracted\\all_audio_files\\"+audioName+".wav";
		            }
		        } 
		        catch (Exception iOException) {
		        	iOException.printStackTrace();
		        }
			}
		});
		btnNewButton.setBounds(96, 43, 85, 23);
		this.contentPane.add(btnNewButton);
		
		this.origVideo = new JPanel();
		this.origVideo.setBounds(604, 330, 352, 288);
		this.contentPane.add(this.origVideo);
		
		this.queryVideoPanel = new JPanel();
		this.queryVideoPanel.setBounds(96, 330, 352, 288);
		this.contentPane.add(this.queryVideoPanel);
		
		JLabel lblQuery = new JLabel("Query");
		lblQuery.setFont(new Font("Tahoma", Font.BOLD | Font.ITALIC, 11));
		lblQuery.setBounds(96, 305, 46, 14);
		this.contentPane.add(lblQuery);
		
		JLabel lblVideo = new JLabel("Video");
		lblVideo.setFont(new Font("Tahoma", Font.BOLD | Font.ITALIC, 11));
		lblVideo.setBounds(604, 305, 46, 14);
		this.contentPane.add(lblVideo);
		
		@SuppressWarnings("rawtypes")
		JList list = new JList();
		list.setBounds(604, 46, 352, 159);
		this.contentPane.add(list);
		
		this.queryVideoPlay = new JButton("Play");
		this.queryVideoPlay.setBounds(96, 640, 89, 23);
		this.contentPane.add(this.queryVideoPlay);
		
		this.queryVideoPause = new JButton("Pause");
		this.queryVideoPause.setBounds(228, 640, 89, 23);
		this.contentPane.add(this.queryVideoPause);
		
		this.queryVideoStop = new JButton("Stop");
		this.queryVideoStop.setBounds(359, 640, 89, 23);
		this.contentPane.add(this.queryVideoStop);
		
		this.origVideoPlay = new JButton("Play");
		this.origVideoPlay.setBounds(604, 640, 89, 23);
		this.contentPane.add(this.origVideoPlay);
		
		this.origVideoPause = new JButton("Pause");
		this.origVideoPause.setBounds(736, 640, 89, 23);
		this.contentPane.add(this.origVideoPause);
		
		this.origVideoStop = new JButton("Stop");
		this.origVideoStop.setBounds(867, 640, 89, 23);
		this.contentPane.add(this.origVideoStop);
	}
}
