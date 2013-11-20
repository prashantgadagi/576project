import java.awt.Container;
import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;


@SuppressWarnings("serial")
public class UI extends JFrame {
	String[] videoFileNames = { "Soccer 1", 
								"Soccer 2", 
								"Soccer 3", 
								"Soccer 4", 
								"Talk 1",
								"Talk 2",
								"Talk 3",
								"Talk 4",
								"Wreck 1",
								"Wreck 2",
								"Wreck 3",
								"Wreck 4"};
	
	final String[] audioFileValues = {"D:\\576project\\extracted\\all_audio_files\\soccer1.wav",
									  "D:\\576project\\extracted\\all_audio_files\\soccer2.wav",
									  "D:\\576project\\extracted\\all_audio_files\\soccer3.wav",
									  "D:\\576project\\extracted\\all_audio_files\\soccer4.wav",
									  "D:\\576project\\extracted\\all_audio_files\\talk1.wav",
									  "D:\\576project\\extracted\\all_audio_files\\talk2.wav",
									  "D:\\576project\\extracted\\all_audio_files\\talk3.wav",
									  "D:\\576project\\extracted\\all_audio_files\\talk4.wav",
									  "D:\\576project\\extracted\\all_audio_files\\wreck1.wav",
									  "D:\\576project\\extracted\\all_audio_files\\wreck2.wav",
									  "D:\\576project\\extracted\\all_audio_files\\wreck3.wav",
									  "D:\\576project\\extracted\\all_audio_files\\wreck4.wav"};
	
	final String[] videoFileValues = {"D:\\576project\\extracted\\soccer1\\soccer1.rgb",
									  "D:\\576project\\extracted\\soccer2\\soccer2.rgb",
									  "D:\\576project\\extracted\\soccer3\\soccer3.rgb",
									  "D:\\576project\\extracted\\soccer4\\soccer4.rgb",
									  "D:\\576project\\extracted\\talk1\\talk1.rgb",
									  "D:\\576project\\extracted\\talk2\\talk2.rgb",
									  "D:\\576project\\extracted\\talk3\\talk3.rgb",
									  "D:\\576project\\extracted\\talk4\\talk4.rgb",
									  "D:\\576project\\extracted\\wreck1\\wreck1.rgb",
									  "D:\\576project\\extracted\\wreck2\\wreck2.rgb",
									  "D:\\576project\\extracted\\wreck3\\wreck3.rgb",
									  "D:\\576project\\extracted\\wreck4\\wreck4.rgb"};
	public Container contentPane;
	public static JTextField textField;
	public JPanel origVideoPanel;
	public JPanel queryVideoPanel;
	public JButton queryVideoPlay;
	public JButton queryVideoPause;
	public JButton queryVideoStop;
	public JButton origVideoPlay;
	public JButton origVideoPause;
	public JButton origVideoStop;
	public static boolean videoPaused;
	public static boolean queryVideoPaused;
	public static PlayRGBVideo videoThread;
	public static PlayAudio audioThread;
	public static PlayRGBVideo queryVideoThread;
	public static PlayAudio queryAudioThread;
	private JScrollPane scrollPane;
	
	public static QueryPlayButtonEvent queryPlay;
	public static QueryPauseButtonEvent queryPause;
	public static QueryStopButtonEvent queryStop;
	
	public static PlayButtonEvent play;
	public static PauseButtonEvent pause;
	public static StopButtonEvent stop;
	
	public static String queryAudioFileName;
	public static String queryVideoFileName;
	public static String audioFileName;
	public static String videoFileName;
	

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		final UI frame = new UI();
		frame.setVisible(true);
	}

	/**
	 * Create the frame.
	 */
	public UI() {
		
		setResizable(false);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(150, 5, 1024, 700);
		this.contentPane = new JPanel();
		((JComponent) this.contentPane).setBorder(new EmptyBorder(5, 5, 5, 5));
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
				fc.setCurrentDirectory(new File("D:\\576project\\extracted\\query"));
		        int res = fc.showOpenDialog(null);
		        try {
		            if (res == JFileChooser.APPROVE_OPTION) {
		                File file = fc.getSelectedFile();
		                String queryVideo = file.getAbsolutePath();
		                UI.textField.setText(queryVideo);
		                String audioName = queryVideo.substring(queryVideo.indexOf("query\\query")+6,queryVideo.indexOf("query")+12);
		                String queryAudio = "D:\\576project\\extracted\\all_audio_files\\"+audioName+".wav";
		                
		                UI.queryAudioFileName = queryAudio;
		                UI.queryVideoFileName = queryVideo;
		                
		                if(UI.queryStop != null)
		                UI.queryStop.mouseClicked(null);
		                
		                if(UI.queryPlay == null) {
		                	UI.queryPlay = new QueryPlayButtonEvent(queryVideoPanel);
			                UI.queryPause = new QueryPauseButtonEvent();
			                UI.queryStop = new QueryStopButtonEvent(queryVideoPanel);
			                
			                queryVideoPlay.addMouseListener(UI.queryPlay);
							queryVideoPause.addMouseListener(UI.queryPause);
							queryVideoStop.addMouseListener(UI.queryStop);
		                }
		            }
		        } 
		        catch (Exception iOException) {
		        	iOException.printStackTrace();
		        }
			}
		});
		btnNewButton.setBounds(96, 43, 85, 23);
		this.contentPane.add(btnNewButton);
		
		this.origVideoPanel = new JPanel();
		this.origVideoPanel.setBounds(604, 330, 352, 288);
		this.contentPane.add(this.origVideoPanel);
		
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
		
		scrollPane = new JScrollPane();
		scrollPane.setBounds(604, 46, 352, 159);
		contentPane.add(scrollPane);
		
		@SuppressWarnings({ "rawtypes", "unchecked" })
		final JList list = new JList(videoFileNames);
		scrollPane.setViewportView(list);
		list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		list.addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent arg0) {
				if(!list.getValueIsAdjusting()) {
					UI.audioFileName = audioFileValues[list.getSelectedIndex()];
					UI.videoFileName = videoFileValues[list.getSelectedIndex()];
					
					if(UI.stop != null) {
		                UI.stop.mouseClicked(null);
					}
		                
					if (UI.play == null) {
						UI.play = new PlayButtonEvent(origVideoPanel);
						UI.pause = new PauseButtonEvent();
						UI.stop = new StopButtonEvent(origVideoPanel);

						origVideoPlay.addMouseListener(UI.play);
						origVideoPause.addMouseListener(UI.pause);
						origVideoStop.addMouseListener(UI.stop);
					}
				}
			}
		});
		
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
