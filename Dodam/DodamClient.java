//import kr.ac.konkuk.ccslab.cm.*;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

import kr.ac.konkuk.ccslab.cm.entity.CMUser;
import kr.ac.konkuk.ccslab.cm.event.CMDummyEvent;
import kr.ac.konkuk.ccslab.cm.event.CMSessionEvent;
import kr.ac.konkuk.ccslab.cm.info.CMInteractionInfo;
import kr.ac.konkuk.ccslab.cm.stub.CMClientStub; 


public class DodamClient extends JFrame {
	
	private static final long serialVersionUID = 1L;
	
	private CMClientStub m_clientStub;  
	private DodamClientEventHandler m_eventHandler; 
	private JTextField m_inTextField;
	private JButton m_loginButton;
	
	
	DodamClient(){
		
		m_clientStub = new CMClientStub();   
		m_eventHandler = new DodamClientEventHandler(m_clientStub, this);  
		
		//Server�� �����ϱ�(���� IP�� ��Ʈ��ȣ�� �׳� �޾ƿ��� conf����?
		boolean bRet = m_clientStub.startCM();//�̰� �ֿܼ� ����Ѵٰ� �ʹ� �����ɸ��µ�....?
		if(!bRet)
		{
			System.err.println("CM initialization error!");
			return;
		}
		
		MyKeyListener cmKeyListener = new MyKeyListener();
		MyActionListener cmActionListener = new MyActionListener();
		
		setTitle("���㵵��");
		setSize(300, 400);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLayout(new GridLayout(3,1));
		 
		add(new JLabel("UserName"));
		
		m_inTextField = new JTextField();
		m_inTextField.addKeyListener(cmKeyListener);
		add(m_inTextField);
		
		m_loginButton = new JButton("Login");
		m_loginButton.addActionListener(cmActionListener);
		add(m_loginButton);
		
		
		setVisible(true);
		
		
	}

	
	public CMClientStub getClientStub()  {   
		
		return m_clientStub;  
	
	}    
	
	
	public DodamClientEventHandler getClientEventHandler()  {   
		
		return m_eventHandler;  
	
	}    
	
	public void DodamLogin(String userName) {
		
		String strUserName = userName;
		String strPassword = "aaaa";// ��й�ȣ�� ��������ϱ� ���Ƿ� �ֱ�
		CMSessionEvent loginAckEvent = null;
		
		loginAckEvent = m_clientStub.syncLoginCM(strUserName, strPassword);
		
		if(loginAckEvent != null) {  // print login result  
			if(loginAckEvent.isValidUser() == 0)   
				System.err.println("This client fails authentication by the default server!");  
			
			else if(loginAckEvent.isValidUser() == -1)   
				System.err.println("This client is already in the login-user list!");  
			
			else   
				System.out.println("This client successfully logs in to the default server."); 
				getContentPane().removeAll();
				getContentPane().repaint();
				getContentPane().setLayout(new GridLayout(6,1));
				//getContentPane().add(new JLabel(strUserName)); 
				//setVisible(true);
			} 
		else  
			System.err.println("failed the login request!"); 
		
	
	}
	
	public void sendHour(String hour) {
		CMInteractionInfo interInfo = m_clientStub.getCMInfo().getInteractionInfo();
		CMUser myself = interInfo.getMyself();
		
		CMDummyEvent dhe = new CMDummyEvent();
		dhe.setHandlerSession(myself.getCurrentSession());
		dhe.setHandlerGroup(myself.getCurrentGroup());
		
		dhe.setDummyInfo(hour);
		m_clientStub.send(dhe, "SERVER");
		dhe = null;
	}
	
	public void sendDeadEvent() {
		CMInteractionInfo interInfo = m_clientStub.getCMInfo().getInteractionInfo();
		CMUser myself = interInfo.getMyself();
		
		CMDummyEvent dde = new CMDummyEvent();
		dde.setHandlerSession(myself.getCurrentSession());
		//System.out.println("####"+myself.getCurrentSession());
		dde.setHandlerGroup(myself.getCurrentGroup());
		//System.out.println("####"+myself.getCurrentGroup());
		// ����� �̺�Ʈ �߻����� ������ �׾����!!!!!!!!!!!!!!!!!!!!!!!!!
		dde.setDummyInfo("DEAD");
		m_clientStub.send(dde, "SERVER");
		dde = null;
	}
	
	// ������ ��� �˾� â
	public void showSuccessDialog() {
		String [] options = {"����"};
		int result = JOptionPane.showOptionDialog(null, "SUCCESS!!", "����", JOptionPane.YES_OPTION, JOptionPane.PLAIN_MESSAGE, null, options, options[0]);
		if(result==JOptionPane.YES_OPTION) {
			System.exit(0);
		}
	}
	// ������ ��� �˾� â
	public void showFailDialog() {
		String [] options = {"����"};
		int result = JOptionPane.showOptionDialog(null, "Fail...", "����", JOptionPane.YES_OPTION, JOptionPane.PLAIN_MESSAGE, null, options, options[0]);
		if(result==JOptionPane.YES_OPTION) {
			System.exit(0);
		}
	}

	public class MyKeyListener implements KeyListener {
		public void keyPressed(KeyEvent e)
		{
			int key = e.getKeyCode();
			if(key == KeyEvent.VK_ENTER)
			{
				JTextField input = (JTextField)e.getSource();
				String strText = input.getText();
				DodamLogin(strText);
				
				
			}
			else if(key == KeyEvent.VK_ALT)
			{
				
			}
		}
		
		public void keyReleased(KeyEvent e){}
		public void keyTyped(KeyEvent e){}
		
	}
	
	public class MyActionListener implements ActionListener {
		public void actionPerformed(ActionEvent e)
		{
			JButton button = (JButton) e.getSource();
			if(button.getText().equals("Login"))
			{
				// login to the default cm server
				DodamLogin(m_inTextField.getText());
			}

			//m_inTextField.requestFocus();
		}
	}
	
	
	public static void main(String[] args) {   
		
		DodamClient client = new DodamClient();   
		CMClientStub cmStub = client.getClientStub();   
		cmStub.setEventHandler(client.getClientEventHandler());   
		//cmStub.startCM();
		//client.DodamTest(cmStub);
		
	}
}
