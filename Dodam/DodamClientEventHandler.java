import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.util.StringTokenizer;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;

import kr.ac.konkuk.ccslab.cm.event.CMDataEvent;
import kr.ac.konkuk.ccslab.cm.event.CMDummyEvent;
import kr.ac.konkuk.ccslab.cm.event.CMEvent;
import kr.ac.konkuk.ccslab.cm.event.CMEventHandler;
import kr.ac.konkuk.ccslab.cm.info.CMInfo;
import kr.ac.konkuk.ccslab.cm.stub.CMClientStub;


public class DodamClientEventHandler implements CMEventHandler {
	private DodamClient m_dodamClient;
	//private CMClientStub m_clientStub;
	DodamStudy dodamStudy;
	
	public DodamClientEventHandler(CMClientStub stub, DodamClient dodam)
	{
		m_dodamClient = dodam;
		//m_clientStub = stub;
		
	}
	
	public class MActionListener implements ActionListener {
		public void actionPerformed(ActionEvent e)
		{
			JButton button = (JButton) e.getSource();
			if(button.getText().equals("send"))
			{
				// login to the default cm server
				dodamStudy = new DodamStudy();
			}

			//m_inTextField.requestFocus();
		}
	}
	
	public class DodamTimer extends Thread {
		
		JLabel DTime;
		boolean dodamtime = true;
		
		public DodamTimer(JLabel DTime) {
			this.DTime = DTime;
			
		}
		
		public void run() { 
			
			int second = 0;
			int minute = 0;
			
			while(dodamtime) {
				if(second==10) {
					second = 0;
					minute++;
					switch(minute) {
					case 1:
						m_dodamClient.sendHour("ONE_HOUR");
						break;
					case 2:
						m_dodamClient.sendHour("TWO_HOUR");
						break;
					case 3:
						m_dodamClient.sendHour("THREE_HOUR");
						dodamtime = false;
						break;
					}
				}
				DTime.setText(Integer.toString(minute)+" : "+Integer.toString(second));
				second++;
				
				try {
					
					Thread.sleep(1000);//1초간격
					
				}catch(InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	


	}
	
	public class DodamStudy extends JFrame {
		
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		
		//ImageIcon image;
		JLabel labelImage;
		DodamTimer th; 
		//JLabel timerLabel;
		
		DodamStudy(){
			
			Container c = getContentPane();
			c.setLayout(new BorderLayout());
			dodamUserEventListener duel = new dodamUserEventListener();
			c.addMouseMotionListener(duel);
			c.addKeyListener(duel);
			JLabel dt = new JLabel();
			dt.setFont(new Font("Gothic", Font.BOLD, 120));
			dt.setHorizontalAlignment(JLabel.CENTER);
			dt.setVerticalAlignment(JLabel.CENTER);
			c.add(dt, BorderLayout.SOUTH);
			ImageIcon image = new ImageIcon("client-file-path/tree_seed.png");
			labelImage = new JLabel(image);
			labelImage.setHorizontalAlignment(JLabel.CENTER);
			labelImage.setVerticalAlignment(JLabel.CENTER);
			c.add(labelImage,BorderLayout.CENTER);
			
			th = new DodamTimer(dt);
			
			setExtendedState(JFrame.MAXIMIZED_BOTH); 
			setUndecorated(true); 
			//setSize(800,800);
			setVisible(true);
			
			c.requestFocus();
			
			th.start();
			
		}
		
		void FailTimer() {
			th.dodamtime = false;
		}
		
		
		void setImage(String ts) {
			ImageIcon image = new ImageIcon("client-file-path/"+ts+".png");
			labelImage.setIcon(image);
			//setVisible(true);
		}
		
		
	}
	
	public class dodamUserEventListener implements MouseMotionListener, KeyListener{

		// 시작하자마자 화면 안쪽 MouseMovedEvent 감지로 인한 도담도담 중지 방지용 카운터
		int eventCounter = 0;
		
		@Override
		public void keyPressed(KeyEvent e) {
			// 키보드 이벤트가 발생했을 때 이유는 모르겠지만 Pressed혼자는 감지를 못하고 Released랑 같이 있어야 감지를 하더라구요. 테스트용 파일도 같이 보내드릴게요.. cmd로라도 실행을..
			m_dodamClient.sendDeadEvent();
			dodamStudy.FailTimer();
		}
		@Override
		public void keyReleased(KeyEvent e) {
			m_dodamClient.sendDeadEvent();
			dodamStudy.FailTimer();
		}
		@Override
		public void mouseMoved(MouseEvent e) {
			// TODO Auto-generated method stub
			// 마우스 움직임이 발생했을 때 eventCounter가 3이 되었을 때 메시지보내도록
			if(++eventCounter==3) {
				m_dodamClient.sendDeadEvent();
				dodamStudy.FailTimer();
			}
			
		}

		@Override
		public void keyTyped(KeyEvent e) {}

		@Override
		public void mouseDragged(MouseEvent e) {}

		
		
	}

	
	
	@Override
	public void processEvent(CMEvent cme) {
		// TODO Auto-generated method stub
		//System.out.println("Client app receives CM event!!");
		switch(cme.getType())
		{
		case CMInfo.CM_DATA_EVENT:
			processDataEvent(cme);
			break;
		case CMInfo.CM_DUMMY_EVENT:
			processDummyEvent(cme);
			break;
		
		default:
			return;
		}
	}
	
	private void processDataEvent(CMEvent cme)
	{
		CMDataEvent de = (CMDataEvent) cme;
		
		switch(de.getID())
		{
		
		case CMDataEvent.NEW_USER:
			de.getUserName();
			m_dodamClient.getContentPane().add(new JLabel(de.getUserName()));
			JButton send = new JButton("send");
			MActionListener cmActionListener = new MActionListener();
			send.addActionListener(cmActionListener);
			m_dodamClient.getContentPane().add(send);
			m_dodamClient.setVisible(true);
			break;
		case CMDataEvent.INHABITANT:
			/*if(newUser != null) {
				m_dodamClient.getContentPane().add(new JLabel(newUser)); 
				m_dodamClient.getContentPane().add(new JLabel(de.getUserName())); 
				m_dodamClient.setVisible(true);
			}*/
			
			break;	
		case CMDataEvent.REMOVE_USER:
			System.out.println("["+de.getUserName()+"] leaves group("+de.getHandlerGroup()+") in session("
					+de.getHandlerSession()+").");
			break;
		default:
			return;                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                 
		}
	}
	
	private void processDummyEvent(CMEvent cme)
	{
		
		//dse
		CMDummyEvent dse = (CMDummyEvent) cme;
		//System.out.println("session("+dse.getHandlerSession()+"), group("+dse.getHandlerGroup()+")");
		//System.out.println("dummy msg: "+dse.getDummyInfo());
		
		String dcm = dse.getDummyInfo();
		String MessageType = null;
		StringTokenizer st = new StringTokenizer(dcm, ";" );
		MessageType = st.nextToken();
		System.out.println(MessageType);
		
		//로그인 메시지가 왔을 때
		if(MessageType.equals("LOGIN")) {
			System.out.println(st.nextToken());
		}
		//로그인 실패 메세지
		else if(MessageType.equals("LOGIN_FAILED")) {
			System.out.println("로그인 실패 :: 인원이 가득 찼습니다!");
		}
		//도담도담 공부 중
		else if(MessageType.equals("START")) {
			String TreeState = st.nextToken();
			
			switch(TreeState) {
			case "TREE_1H":
	        //나무 그림이랑 타이머 수정
				dodamStudy.setImage("tree_1h");
				System.out.println("#한시간");
				break;
			case "TREE_2H":
				dodamStudy.setImage("tree_2h");
				System.out.println("#두시간");
				break;
			case "TREE_SUCCESS":
				dodamStudy.setImage("tree_success");
				m_dodamClient.showSuccessDialog();
				System.out.println("#성공");
				break;
			}
		 }
		 //공부 실패
		 else {
		 //나무 죽은 그림 넣기
			 dodamStudy.setImage("tree_dead");
			 m_dodamClient.showFailDialog();
		 }
		return;
	}



}