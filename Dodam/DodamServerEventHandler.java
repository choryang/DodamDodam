import kr.ac.konkuk.ccslab.cm.entity.CMUser;
import kr.ac.konkuk.ccslab.cm.event.CMDummyEvent;
import kr.ac.konkuk.ccslab.cm.event.CMEvent;
import kr.ac.konkuk.ccslab.cm.event.CMEventHandler;
import kr.ac.konkuk.ccslab.cm.event.CMSessionEvent;
import kr.ac.konkuk.ccslab.cm.info.CMInfo;
import kr.ac.konkuk.ccslab.cm.info.CMInteractionInfo;
import kr.ac.konkuk.ccslab.cm.stub.CMServerStub;

public class DodamServerEventHandler implements CMEventHandler {

   private CMServerStub m_serverStub;
   private int oneHourCount = 0;
   private int twoHourCount = 0;
   private int threeHourCount = 0;
   private int deadEventCount = 0;
   static int userNum;
   
   public DodamServerEventHandler(CMServerStub serverStub) 
   {
      m_serverStub = serverStub;
      userNum=0;
   }
   
   @Override
   public void processEvent(CMEvent cme) {
      // TODO Auto-generated method stub
      switch(cme.getType()) {
      case CMInfo.CM_SESSION_EVENT:
         processSessionEvent(cme);
         break;
      case CMInfo.CM_DUMMY_EVENT:
         processDummyEvent(cme);
         break;
      default:
         return;
      }
      
   }
   
   private void processSessionEvent(CMEvent cme) 
   {
      CMSessionEvent se = (CMSessionEvent) cme;
      //CMDummyEvent de=new CMDummyEvent(); 
      AutoStartCount as =new AutoStartCount(m_serverStub);
      Thread th=new Thread(as);
      switch(se.getID()) {
      case CMSessionEvent.LOGIN:
         System.out.println("["+se.getUserName()+"] requests login");
         userNum+=1;
         
         if(userNum>3) {
        	 /* 메세지는 가는데 4번째 유저를 제외한 그룹 유저들에게 간다는 단점이 있음....
        	 de.setHandlerSession("session1");
        	 de.setHandlerGroup("g1");
        	 de.setDummyInfo("LOGIN_FAILED;");
        	 m_serverStub.cast(de,"session1","g1");
        	 */
        	 return;
         }
         System.out.println("userNum:"+userNum);
         System.out.println("DODAM :: "+th.getState());
         if(userNum==1) {
        	 //*****자동시작 카운트 스레드 시작
        	 th.setDaemon(true);
        	 th.start();
        	 System.out.println("유저 한명! 자동시작 카운트 시작");
         }//if(userNum==1)
			
         if(userNum==3) {
        	 //as.threePeople=3;
        	 int i=0;
        	 System.out.println(th.getState());
        	 th.interrupt();
        	 while(!th.getState().equals(Thread.State.TERMINATED)) {
        		 th.interrupt();
        		 System.out.println("인터럽트중");
        		 if(i>20) {	break; }
        		 i++;
        	 }//while
         }//if(userNum==3)	
         break;
      default:
         return;
      }
   }
   
   // DummyEvent 처리 메소드
   private void processDummyEvent(CMEvent cme) {
     // dodam client event
      CMDummyEvent dce = (CMDummyEvent) cme;
      //System.out.println("session("+dce.getHandlerSession()+"), group("+dce.getHandlerGroup()+")");
      //System.out.println("dummy msg: "+dce.getDummyInfo());
      
      CMInteractionInfo interInfo = m_serverStub.getCMInfo().getInteractionInfo();
      CMUser myself = interInfo.getMyself();
      //CMServer myself = interInfo.getDefaultServerInfo();
      
      // dodam server event
      CMDummyEvent dse = new CMDummyEvent();
      dse.setHandlerSession(myself.getCurrentSession());
      dse.setHandlerGroup(myself.getCurrentGroup());
      //System.out.println("####session("+myself.getCurrentSessionName()+"), group("+myself.getCurrentGroupName()+")");
      
      
      // dodam server message
      String dsm = "";
      
      // dodam client message
      String dcm = dce.getDummyInfo();
      switch(dcm) {
      case "ONE_HOUR":
         if(++oneHourCount==1) {//첫번째로 이벤트를 받았을 때 보내준다
            dsm += "START;TREE_1H";
         }
         break;
      case "TWO_HOUR":
         if(++twoHourCount==1) {
           dsm += "START;TREE_2H";
         }
         break;
      case "THREE_HOUR":
         if(++threeHourCount==1) {
            dsm += "START;TREE_SUCCESS";
            userNum=0;
         }
         break;
      case "DEAD":
         if(++deadEventCount==1) {
        	 dsm += "END;TREE_DEAD";
        	 userNum=0;
         }
         
         break;
      }
      
      dse.setDummyInfo(dsm);
      
      m_serverStub.cast(dse, "session1", "g1");
      dse = null;
      return;
   }

}





class AutoStartCount implements Runnable{
	String user1="";
	String user2="";
	String user3="";
	String send="";
	private CMServerStub m_serverStub;
	int timer;
	CMInteractionInfo interInfo;
    CMUser myself;
    CMDummyEvent due=new CMDummyEvent();
	
	AutoStartCount(CMServerStub s){
		m_serverStub=s;
		this.timer=60;
		interInfo = m_serverStub.getCMInfo().getInteractionInfo();
		myself = interInfo.getMyself();
	}
	
	@Override
	public void run(){
		due.setHandlerSession("session1");
		due.setHandlerGroup("g1");
		System.out.println("timer :: "+timer);
		
		while(DodamServerEventHandler.userNum<3) {
			System.out.println(DodamServerEventHandler.userNum+"명이 되었다!");
			if(!(timer>0)) {
				System.out.println("리턴되어라");
				return;
			}
			
			timer--;
			System.out.println(timer);
			
			//LOGIN;타이머;접속자1;접속자2;접속자3;
			StringBuffer sb=new StringBuffer("LOGIN;");
			sb.append(timer+";");
			sb.append(this.user1+";");
			sb.append(this.user2+";");
			sb.append(this.user3+";");
			this.send=sb.toString();
			
			due.setDummyInfo(send);
			System.out.println("메세지 세팅끝");
			
			//m_serverStub.cast(due, myself.getCurrentSession(), myself.getCurrentGroup());
			m_serverStub.cast(due,"session1","g1");
			System.out.println(send+"를 전송했음.");
			try{
				Thread.sleep(1000);
			} 
			catch(InterruptedException e) {
				return;
			}
		}//while
		
		if(DodamServerEventHandler.userNum==3) {
			System.out.println(DodamServerEventHandler.userNum+"명이 되었다!");
		}
		
	}
}