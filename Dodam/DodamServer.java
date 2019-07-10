//import kr.ac.konkuk.ccslab.cm.*;
import kr.ac.konkuk.ccslab.cm.stub.CMServerStub; 

public class DodamServer {
	private CMServerStub m_serverStub;  
	private DodamServerEventHandler m_eventHandler;    
	
	public DodamServer()  
	{   
		m_serverStub = new CMServerStub();   
		m_eventHandler = new DodamServerEventHandler(m_serverStub);  
	}   
	
	public CMServerStub getServerStub()  
	{   
		return m_serverStub;  
	}    
	
	public DodamServerEventHandler getServerEventHandler()  
	{   
		return m_eventHandler;  
	} 
	 
	 public static void main(String[] args) 
	 {   
		 DodamServer server = new DodamServer();   
		 CMServerStub cmStub = server.getServerStub();   
		 cmStub.setEventHandler(server.getServerEventHandler());   
		 cmStub.startCM();  
	} 
	 
	 
	 
}
