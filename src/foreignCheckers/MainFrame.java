package foreignCheckers;

import java.awt.AWTEvent;
import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.UIManager;





/**
 * 主界面,主要监听按钮
 */
public class MainFrame extends JFrame implements ActionListener{ 

	private static final long serialVersionUID = 1L;
	private JMenuBar mainMenu = new JMenuBar();
	private JMenu gameMenu = new JMenu("进入游戏");
	private JMenu levelMenu = new JMenu("游戏等级");
	private JMenu soundMenu = new JMenu("游戏音效");
	private JMenu changeBgMenu = new JMenu("棋盘背景");
	private JMenuItem itemStart = new JMenuItem("游戏开始");
	private JMenuItem itemRegular = new JMenuItem("游戏规则");
	private JMenuItem itemEasy = new JMenuItem("简单");
	private JMenuItem itemNormal = new JMenuItem("一般");
	private JMenuItem itemHard = new JMenuItem("困难");
	private JMenuItem itemSoundOn = new JMenuItem("开启");
	private JMenuItem itemSoundOff = new JMenuItem("关闭");
	private JMenuItem itemChangeBg = new JMenuItem("更换棋盘背景");
	private ImageIcon selectedIcon;
	ChessBoard chessBoard;
	Robot robot;
	
	public static void main(String[] args){
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
			new MainFrame();  
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
	public MainFrame() {
		enableEvents(AWTEvent.WINDOW_EVENT_MASK);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.pack();
		this.setBounds(450,170,486,530);
		this.setTitle("西洋跳棋");
		init();
	}

	private void init(){
		URL iconUrl = this.getClass().getResource("/selected.png");
		selectedIcon = new ImageIcon(iconUrl);
		robot = new Robot();
		chessBoard = new ChessBoard(robot);
		
		Container container = this.getContentPane();
		container.setLayout(new BorderLayout());
		gameMenu.setFont(new Font("Dialog", 0, 13));
		levelMenu.setFont(new Font("Dialog", 0, 13));
		soundMenu.setFont(new Font("Dialog", 0, 13));
		itemStart.setFont(new Font("Dialog", 0, 12));
		itemRegular.setFont(new Font("Dialog", 0, 12));
		itemEasy.setFont(new Font("Dialog", 0, 12));
		itemEasy.setIcon(selectedIcon);
		itemNormal.setFont(new Font("Dialog", 0, 12));
		itemHard.setFont(new Font("Dialog", 0, 12));
		itemSoundOn.setFont(new Font("Dialog", 0, 12));
		itemSoundOn.setIcon(selectedIcon);
		itemSoundOff.setFont(new Font("Dialog", 0, 12));
		itemChangeBg.setFont(new Font("Dialog", 0, 12));
		//组合菜单栏
		gameMenu.add(itemStart);
		gameMenu.add(itemRegular);
		levelMenu.add(itemEasy);
		levelMenu.add(itemNormal);
		levelMenu.add(itemHard);
		soundMenu.add(itemSoundOn);
		soundMenu.add(itemSoundOff);
		changeBgMenu.add(itemChangeBg);
		mainMenu.add(gameMenu);
		mainMenu.add(levelMenu);
		mainMenu.add(soundMenu);
		mainMenu.add(changeBgMenu);
		
//		mainMenu.setBounds(0, 0, 480, 40);
		this.setJMenuBar(mainMenu);
//		this.add(mainMenu);
//		chessBoard.setLocation(0,35);
//		chessBoard.setBounds(0,40,480,480);
//		container.add(chessBoard);
//		chessBoard.setBounds(0,20,480,500);
//		chessBoard.setVisible(false);
		this.add(chessBoard);
		
		//添加事件监听
		itemStart.addActionListener(this);
		itemRegular.addActionListener(this);
		itemEasy.addActionListener(this);
		itemNormal.addActionListener(this);
		itemHard.addActionListener(this);
		itemSoundOn.addActionListener(this);
		itemSoundOff.addActionListener(this);
		itemChangeBg.addActionListener(this);

    	setVisible(true);
    	setResizable(false);
	}
	
	
	@Override
	public void actionPerformed(ActionEvent e) {
		Object object = e.getSource();
		if (object == itemStart) {
			Util.win = false;
			chessBoard.init();
			chessBoard.setVisible(true);
			chessBoard.repaint();
		}else if (object == itemRegular) {
			String regular = "具体规则\n1）双方轮流行走。“未成王”的棋子只能向左上角或右上角且无人占据的格子斜走一格。\n2）吃子时，敌方的棋子必须是"
					+ "在己方棋子的左上角或右上角的格子，而且该敌方棋子的\n    对应的左上角或右上角必须没有棋子。\n3）当棋子到了底线，它就可以“成王”"
					+ "，可以向后移动。\n4）若一个棋子可以吃棋，它必须吃。棋子可以连吃。即是说，若一只棋子吃过敌方的棋\n    子后，若它新的位置亦可以"
					+ "吃敌方的另一些敌方棋子，它必须再吃，直到无法再吃为止。\n5）若一个玩家没法行走或所有棋子均被吃去便算输。\n6）此游戏为人机对战，"
					+ "游戏玩家为白子。";
			JOptionPane.showMessageDialog(null,regular,"游戏规则",JOptionPane.INFORMATION_MESSAGE); 
		}else if (object == itemEasy) {
			clearLevelIcon(itemEasy);
			robot.setLevel(1);
		}else if (object == itemNormal) {
			clearLevelIcon(itemNormal);
			robot.setLevel(2);
		}else if (object == itemHard) {
			clearLevelIcon(itemHard);
			robot.setLevel(3);
		}else if (object == itemSoundOn) {
			clearSoundIcon(itemSoundOn);
			Util.mute = true;
		}else if (object == itemSoundOff) {
			clearSoundIcon(itemSoundOff);
			Util.mute = false;
		}else if (object == itemChangeBg) {
			chessBoard.changeBg();
		}
		
	}
	
	private void clearLevelIcon(JMenuItem jMenuItem){
		itemEasy.setIcon(null);
		itemNormal.setIcon(null);
		itemHard.setIcon(null);
		jMenuItem.setIcon(selectedIcon);
	}
	
	private void clearSoundIcon(JMenuItem jMenuItem){
		itemSoundOn.setIcon(null);
		itemSoundOff.setIcon(null);
		jMenuItem.setIcon(selectedIcon);
	}
	
	
	
	
	

}
