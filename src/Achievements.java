import java.awt.event.*;
import javax.swing.*;
import java.awt.*;

public class Achievements extends JPanel{
	JLabel backButton = new JLabel(new ImageIcon("theBackButton.png"));//button for returning to main menu
	JLabel buttonEffect = new JLabel(new ImageIcon("gMouse.png"));//effect for button 
	MouseListener backMouseEffect = new MouseListener() {//show effect when clicked or entered
		public void mouseClicked(MouseEvent e) {
			buttonEffect.setVisible(false);
		}

		public void mouseEntered(MouseEvent e) {
			buttonEffect.setVisible(true);
		}

		public void mouseExited(MouseEvent e) {
			buttonEffect.setVisible(false);
		}
		public void mousePressed(MouseEvent e) {}
		public void mouseReleased(MouseEvent e) {}
    };
	private final static int num=14;//number of achievements
	private static int accumExp = 0;//exp points
	private static JScrollPane scrollPane = new JScrollPane();//to fit all achievents in the window
	private static JPanel paneContent=new JPanel();//things in scrollpane
	private static JLabel[] achievementsLabels=new JLabel[num];//achievents
	private static JLabel getRewardButton = new JLabel(new ImageIcon("aGetReward.png"));//reward icon
	private static JLabel bgi = new JLabel(new ImageIcon("achievementBgi.jpg"));//background
	private static boolean[] accomplished=new boolean[num];// determine if achievements is unlocked
	private static final int[] achiNums = {100,1000,2000,100,1000,5000};//requirements list
	private static final int[] achiRews = {100,100,1000,2000,100,1000,2000,100,1000,5000,10,100,5000,2036};//reward list
	Insets bInsets;	//insets of window
	MouseListener getReward = new MouseListener(){//enables user to collect reward
		public void mouseClicked(MouseEvent e) {
			JOptionPane.showMessageDialog(null,//inform user that reward is collected
				    "Total Exp Collected: "+accumExp+".",
				    "Reward Collected",
				    JOptionPane.INFORMATION_MESSAGE,
				    new ImageIcon("gResponse.png"));
			system.getExp(accumExp);
			accumExp = 0;
		}
		public void mouseEntered(MouseEvent e) {
		}
		public void mouseExited(MouseEvent e) {
		}
		public void mousePressed(MouseEvent e) {
		}
		public void mouseReleased(MouseEvent e) {
		}
	};
	
	/**
	 * This method updates the current state of an achievement (locked or unlocked) as the game progresses
	 */
	public static void updateAchievement(){
		loadAchi();
		long now = System.currentTimeMillis();//current time
		system.userInfo[16] = Long.toString(now-system.recordTime);
		system.recordTime = now;
		System.out.println("Time Updated");
		String[] battleRecords = system.userInfo[17].split(" ");;
		int winNum = Integer.parseInt(battleRecords[1]);
		int battleNum = Integer.parseInt(battleRecords[0]);
		int losNum = battleNum-winNum;
		int missNum = Integer.parseInt(system.userInfo[14]);
		int[] the3Var = {winNum,losNum,missNum};//all necessary info
		long hourNum = Long.parseLong(system.userInfo[16])/3600000;
		boolean[] newAccomplished = new boolean[num];
		System.out.println("Calculations completed");
		newAccomplished[0] = (winNum>=1);System.out.println(newAccomplished[0]);
		for(int i =1;i<10;i++){
			newAccomplished[i]=(the3Var[(i-1)/3]>=achiNums[((i-1)/6)*3+i%3]);
			System.out.println(i+" "+newAccomplished[i]+" "+((i/6)*3+i%3));
		}
		System.out.println("checked 1-10");//inform user
		newAccomplished[10] = (battleNum>=10);
		newAccomplished[11] = (battleNum>=100);
		newAccomplished[12] = (battleNum>=5000);
		newAccomplished[13] = (hourNum >= 2036);
		System.out.println("all checked");//inform user
		String checkedString = "";
		for(int i=0;i<num;i++){
			checkedString+=" "+newAccomplished[i];
			if((!accomplished[i])&&newAccomplished[i]){
				achievementsLabels[i].setIcon(new ImageIcon("A"+i+".png"));
				accumExp+=achiRews[i];
			}//end if
		}//end for
		System.out.println("Achievement updated");
		system.userInfo[28] = checkedString.substring(1);
	}
	
	/**
	 * This method loads the achievement interface
	 */
	private static void loadAchi(){
		String[] achiString = system.userInfo[28].split(" ");
		for(int i =0;i<num;i++){
			accomplished[i] = Boolean.parseBoolean(achiString[i]);
			System.out.println(i+" "+accomplished[i]);
		}
		System.out.println("Achievement loaded");
		makeLabel();
	}
	
	public Achievements() {
		setSize(1300, 700);//size of window
		setLayout(null);
		bInsets=getInsets();
		getRewardButton.setBounds(bInsets.left+10,bInsets.top+110,120,160);
		getRewardButton.addMouseListener(getReward);
		add(getRewardButton);//adds getReward
		backButton.setBounds(bInsets.left + 10, bInsets.top + 10, 100, 60);
		backButton.addMouseListener(backMouseEffect);
		add(backButton);//adds back
		buttonEffect.setBounds(bInsets.left, bInsets.top, 120, 80);
		add(buttonEffect);//adds effects
		buttonEffect.setVisible(false);
		scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);//remove horizontal scrollbar
		scrollPane.setBounds(bInsets.left + 150, bInsets.top + 20, 1250, 650);
		scrollPane.getViewport().setOpaque(false);//make scrollPane transparent
		scrollPane.setOpaque(false);
		add(scrollPane);//adds scrollPane
		scrollPane.setVisible(true);
		bgi.setBounds(bInsets.left, bInsets.top, 1300, 700);
		add(bgi);//adds background
		setVisible(true);
	}
	
	/**
	 * This method creates the label that contains each achievement
	 */
	public static void makeLabel() {
		paneContent.setLayout(new BoxLayout(paneContent,BoxLayout.Y_AXIS));//display one label per line
		for(int i=0;i<num;i++) {//make labels
			if(accomplished[i]) {
				achievementsLabels[i]=new JLabel(new ImageIcon("A"+i+".png"));//if unlocked
			}else {
				achievementsLabels[i]=new JLabel(new ImageIcon("aLocked.png"));//if locked
			}
			paneContent.add(achievementsLabels[i]);
		}
		paneContent.setOpaque(false);//make background transparent
		scrollPane.add(paneContent);
		scrollPane.setViewportView(paneContent);//add to scrollPane
	}
}
