package foreignCheckers;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JOptionPane;
import javax.swing.JPanel;

/**
 * 棋盘界面操作类
 */
public class ChessBoard extends JPanel implements MouseListener{

	private static final long serialVersionUID = 1L;
	private static final Toolkit toolkit = Toolkit.getDefaultToolkit();
	private static final Image RedImage = toolkit.getImage("res/red.png");
	private static final Image WhiteImage = toolkit.getImage("res/white.png");
	private static final Image RedKingImage = toolkit.getImage("res/redKing.png");
	private static final Image WhiteKingImage = toolkit.getImage("res/whiteKing.png");
	private static final String[] bgStrings = new String[]{"res/bg.jpg","res/bg1.jpg","res/bg2.jpg","res/bg3.jpg"};
	private int bgIndex = 0;
	private Image bg = toolkit.getImage(bgStrings[0]);
	private Image bg1 = toolkit.getImage(bgStrings[1]);
	private Image bg2= toolkit.getImage(bgStrings[2]);
	private Image bg3 = toolkit.getImage(bgStrings[3]);
	
	
	private Chess selectedChess;
	private Robot robot;
	private Chess monster;
	private Util util = new Util();
	
	public static ChessState chessState;
	public static Chess[] white = new Chess[12];
	public static Chess[] red = new Chess[12];
	public static Point[][] points = new Point[9][9];
	public static boolean turn = true;
	
	public ChessBoard(Robot robot) {
		try {
			init();
		} catch (Exception e) {
			e.printStackTrace();
		}
//		this.setSize(496,516);
		this.addMouseListener(this);
		this.robot = robot;
	}
	

    //初始化Point数组，用来存放棋盘上64个位置
	public void init(){
		for(int i=1;i<9;i++){
			for(int j=1;j<9;j++){
				points[i][j]=new Point();
				points[i][j].x = 60*(i-1);
				points[i][j].y = 60*(j-1);
			}
		}
		for(int i=0;i<12;i++){
			red[i] = new Chess("red",i);
			white[i] = new Chess("white",i);
		}
		for(int i=0;i<4;i++){
			red[i].setLocation(points[2*i+2][1]);
		}
		for(int i=4;i<8;i++){
			red[i].setLocation(points[2*(i-4)+1][2]);
		}
		for(int i=8;i<12;i++){
			red[i].setLocation(points[2*(i-8)+2][3]);
		}
		for(int i=0;i<4;i++){
			white[i].setLocation(points[2*i+1][6]);
		}
		for(int i=4;i<8;i++){
			white[i].setLocation(points[2*(i-4)+2][7]);
		}
		for(int i=8;i<12;i++){
			white[i].setLocation(points[2*(i-8)+1][8]);
		}
		turn = true;
	}

	//画棋盘及棋子
	@Override
	public void paint(Graphics g) {
		g.drawImage(bg, 0, 0,480,480, null);
		g.drawImage(bg1, 0, 0,480,480, null);
		g.drawImage(bg2, 0, 0,480,480, null);
		g.drawImage(bg3, 0, 0,480,480, null);
		if (bgIndex == 0) {
			g.drawImage(bg, 0, 0,480,480, null);
		}else if (bgIndex == 1) {
			g.drawImage(bg1, 0, 0,480,480, null);
		}else if (bgIndex ==2) {
			g.drawImage(bg2, 0, 0,480,480, null);
		}else if (bgIndex == 3) {
			g.drawImage(bg3, 0, 0,480,480, null);
		}
		for(int i=0;i<12;i++){
			if (red[i].isVisible()) {
				Point point = red[i].getLocation();
				if (red[i].isKing()) {
					g.drawImage(RedKingImage, point.x, point.y,60,60,null);
				}else {
					g.drawImage(RedImage, point.x, point.y,60,60, null);
				}
			}
			if (white[i].isVisible()) {
				Point point = white[i].getLocation();
				if (white[i].isKing()) {
					if (white[i].isSelected()) {
						g.drawImage(WhiteKingImage, point.x, point.y, 60,60,new Color(139,69,19), null);
					}else {
						g.drawImage(WhiteKingImage, point.x, point.y, 60,60,null);
					}
				}else {
					if (white[i].isSelected()) {
						g.drawImage(WhiteImage, point.x, point.y, 60,60,new Color(139,69,19), null);
					}else {
						g.drawImage(WhiteImage, point.x, point.y, 60,60,null);
					}
				}
			}
		}
	}
	
	@Override
	public void mousePressed(MouseEvent e) {
		if (Util.win) {
			return;
		}
		if (!turn) {
			JOptionPane.showMessageDialog(this, "亲，等等，机器人还没下");
			return;
		}
		int x=e.getX();
		int y=e.getY();
		//判断是否无路可走，已输
		if (null != chessState && chessState.nextStatesOfWhite().size()==0) {
			JOptionPane.showMessageDialog(null, "你输了！！！");
			return;
		}
		
		Point selectedPoint = getPoint(x, y);
		for(int i=0;i<12;i++){  //点到红子返回
			if (red[i].isVisible() && red[i].getLocation().equals(selectedPoint)) {
				return;
			}
		}
		for(int i=0;i<12;i++){
			white[i].setSelected(false);
		}
//		if (selectedChess!=null) {
//			selectedChess.setSelected(true);
//		}
		for(int i=0;i<12;i++){
			if (white[i].isVisible() && white[i].getLocation().equals(selectedPoint)) {
				selectedChess = white[i];
				white[i].setSelected(true);
				repaint();
			}
		}
		if (selectedChess != null) {
			Point formerPoint = selectedChess.getLocation();
			if (ifCanGo(selectedChess, formerPoint, selectedPoint)) {
				selectedChess.setLocation(selectedPoint);
				for(int i=1;i<9;i++){
					if (selectedChess.getColor().equals("white")&&selectedPoint.equals(points[i][1])) {
						selectedChess.setKing(true);
						selectedChess.setVisible(true);
					}
				}
				repaint();
				util.play();
				//刚刚吃子的白子还可以再吃，必须继续吃
				if (monster!=null&&monster.equals(selectedChess)&& Util.eat(monster, red,white)) {
					monster = null;
					return;
				}
				if (selectedChess.getColor().equals("white")) {
					ChessState state = new ChessState(red,white,true);
					synchronized(robot){
						//将robot的状态设置为当前状态，并通知它可以走棋了
						robot.setState(state);
						robot.setBorad(this);
						robot.notify();
					}
					selectedChess = null;
					turn = false;
				}
			}
		}
		
		
	}
	
	/**
	 * 判断是否可以走棋
	 */
	private boolean ifCanGo(Chess chess,Point former,Point now){
		System.out.println("ifCanGo");
		int fx = 0,fy = 0,nx = 0,ny = 0;
		monster = null;
		for(int i=1;i<9;i++){
			for(int j=1;j<9;j++){
				if(points[i][j].equals(former)){
					fx=i;
					fy=j;
				}
				if (points[i][j].equals(now)) {
					nx=i;
					ny=j;
				}
			}
		}
		if (chess.isKing()&&chess.getColor().equals("white")&&Math.abs(fx-nx)==1&&Math.abs(fy-ny)==1) {
			if (Util.eat(chess,red,white)) {
				JOptionPane.showMessageDialog(this, "必须吃掉对方的棋子");
			    return false;
			}
			return true;
		}
		if (chess.getColor().equals("white")) {
			if (Math.abs(fx-nx)==1&&fy-ny==1) {
				if (Util.eat(chess, red,white)) {
					JOptionPane.showMessageDialog(this, "必须吃掉对方的棋子");
				    return false;
				}
				System.out.println("通路");
				return true;
			}else if (Math.abs(fx-nx)==2 && Math.abs(fy-ny)==2) { //吃子
				for(int i=0;i<12;i++){
					if (fy-ny == 2) {
						if(nx-fx==2&&red[i].isVisible()&&fx<8&&red[i].getLocation().equals(points[fx+1][fy-1])){
							red[i].setVisible(false);
							monster = chess;
							return true;
						}
						if (nx-fx==-2&&red[i].isVisible()&&red[i].getLocation().equals(points[fx-1][fy-1])) {
							red[i].setVisible(false);
							monster = chess;
							return true;
						}
					}else if (chess.isKing()) {
						if(nx-fx==2 &&fx<8 &&red[i].isVisible()&& red[i].getLocation().equals(points[fx+1][fy-1])){
							red[i].setVisible(false);
							monster = chess;
							return true;
						}
						if(nx-fx==-2 &&red[i].isVisible()&&red[i].getLocation().equals(points[fx-1][fy-1])){
							red[i].setVisible(false);
							monster = chess;
							return true;
						}
						if(nx-fx==2 &&fx<8 &&red[i].isVisible()&& fy<8 && red[i].getLocation().equals(points[fx+1][fy+1])){
							red[i].setVisible(false);
							monster = chess;
							return true;
						}
						if(nx-fx==-2 &&fy<8 && red[i].isVisible()&& red[i].getLocation().equals(points[fx-1][fy+1])){
							red[i].setVisible(false);
							monster = chess;
							return true;
					    }
				   }
			    }
			}
		}
		return false;
	}
	
	
	
	
	/**
	 * 将坐标转化为对应的Point
	 */
	private Point getPoint(int x, int y){
		int i=1,j=1;
		while(x-i*60>=5 && i<8) i++;
		while(y-j*60>=5 && j<8) j++;
		return points[i][j];
	}
	
	/**
	 * 更换棋盘背景图
	 */
	public void changeBg(){
		if (bgIndex == 3) {
			bgIndex = 0;
		}else {
			bgIndex++;
		}
//		bg = toolkit.getImage(bgStrings[bgIndex]);
		repaint();
	}
	
	@Override
	public void mouseClicked(MouseEvent e) {
		// TODO Auto-generated method stub	
	}
	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub	
	}
	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub
	}
	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub
	}
}
