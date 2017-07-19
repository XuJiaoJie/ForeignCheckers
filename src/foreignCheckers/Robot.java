package foreignCheckers;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Iterator;

import javax.swing.JOptionPane;
import javax.swing.JPanel;

/**
 * Robot类用带alpha beta剪枝的极大极小值算法来搜索状态并走棋
 * 它是一个独立的线程，一直在监听棋手是否已经走过棋，如果是，就开始进行搜索并走棋
 */
public class Robot extends Thread{
	private static int DEPTH = 3; //搜索深度.默认是简单的水平
	private static final int MAX = 99999;
	private static final int MIN = -99999;
	private ChessState state;
	private static ArrayList<ChessState> NextStates; //当前状态所有可能的走法
	private JPanel chessBoard;
	private Util util = new Util();
	public static boolean eaten = false;
	
	public Robot(){
		start(); //启动robot线程
	}

	public void setState(ChessState state){
		this.state = state;
	}
	
	public void run(){
		while(true){
			synchronized(this){
				try{
					//此时是人在走棋，等待
					wait();
				}
				catch(InterruptedException ie){
					throw new RuntimeException(ie);
				}	
			}
			try{
				//稍等一会，以免走棋太快
				Thread.sleep(100);
			}
			catch(Exception e){
				e.printStackTrace();
			}
			//等待结束，该robot走了，开始搜索
			alphaBetaSearch(state);
		}
	}


	private void alphaBetaSearch(ChessState state){
		
		state.depth = 0;
		NextStates = null;

		//因为此处是电脑走棋，所以要取所有状态的最大值,maxValue()会把第一层的状态保存到NextStates里
		int value = maxValue(state,MIN,MAX);

		//如果电脑无棋可走,那你就赢了
		if(NextStates==null){ 
			JOptionPane.showMessageDialog(null, "你赢了！！！");	
			Util.win = true;
			return ;
		}

		//在NextStates里面寻找最佳的走法,通过比较权值
		ChessState s = null;
		ChessState nextState = null;
		for(Iterator<ChessState> it = NextStates.iterator();it.hasNext();){
			s = (ChessState)it.next();
			if(s.value==value){
				nextState = s;
				break;
			}
		}

		//电脑没棋走了，上面的判断还是有必要的，不然NextStates.iterator()一句会有异常
		if(nextState==null){
			JOptionPane.showMessageDialog(null, "你赢了！！！");
			Util.win = true;
			return ;
		}
	
		ArrayList<ChessState> steps = findPath(new ChessState(ChessState.preRed,ChessState.preWhite,false),nextState);
		System.out.println("findPath  after");
		ChessState st = null;
		for(Iterator<ChessState> it = steps.iterator();it.hasNext();){
			System.out.println("Iterator  repaint()");
			st = (ChessState)it.next();
			ChessBoard.red = st.red;
			ChessBoard.white = st.white;
			chessBoard.repaint();
			util.play();
			checkOver(st); //检查是否结束了
			try{
				Thread.sleep(200); //两步之间间隔一会
			}
			catch(InterruptedException e){
				e.printStackTrace();
			}
		}
		ChessBoard.turn = true;
		ChessBoard.chessState = st;
		System.out.println("ChessBoard.turn   "+ChessBoard.turn);
	}
	
	
	private int maxValue(ChessState state,int a,int b){
		//如果到了最底层，返回评价函数得到的权值
		if(state.depth==DEPTH) 
			return state.getValue();
		//先把state的value设为最小，因为后面要找一个最大值	
		state.value = MIN;
		//取最大值时对应红子的状态
		ArrayList<ChessState> list = state.nextStatesOfRed();

		//保存第一层的状态，走棋的时候用
		if(state.depth==0)
			NextStates = list;

		ChessState s = null;
		int minValue = MIN;
		for(Iterator<ChessState> it = list.iterator();it.hasNext();){
			s = (ChessState)it.next();
			s.depth = state.depth + 1; //深度加一

			int temp = minValue(s,a,b);//下一层取最小值
			if(minValue < temp)
				minValue = temp;

			//minValue是下一层状态里最小的，它与state的value相比较，取大的
			state.value = state.value>minValue ? state.value : minValue;

			//state的value比beta值大，此时没必要再找下去，可以剪枝了
			if(state.value>=b)
				return state.value;

			//如果state的value比alpha值大，更新alpha值
			a = a>state.value ? a : state.value;
		}
		return state.value;
	}


	private int minValue(ChessState state,int a,int b){
		if(state.depth==DEPTH)
			return state.getValue();
		state.value = MAX;
		ArrayList<ChessState> list = state.nextStatesOfWhite();
		ChessState s = null;
		int maxValue = MAX;
		for(Iterator<ChessState> it = list.iterator();it.hasNext();){
			s = (ChessState)it.next();
			s.depth = state.depth + 1;

			int temp = maxValue(s,a,b);
			if(maxValue > temp)
				maxValue = temp;
			state.value = state.value<maxValue ? state.value : maxValue;
			if(state.value<=a)
				return state.value;
			b = b<state.value ? b : state.value;
		}
		return state.value;
	}
	
	/**
	 * 寻找当前状态和下一状态之间的路径，因为会有连续吃子的情况，
	 * 该方法返回的是一个数组，保存了所有的中间状态和最终状态
	 */
	private ArrayList<ChessState> findPath(ChessState from, ChessState to){
		System.out.println("findPath");
		ArrayList<ChessState> list = new ArrayList<>();
		Chess oriRed[] = from.red;
		Chess oriwhite[] = from.white;
		Chess currentwhite[] = to.white;
		Chess currentRed[] = to.red;
		Chess movedRed = null;

		boolean eaten = false;
		for(int i=0;i<12;i++){
			if(!oriRed[i].getLocation().equals(currentRed[i].getLocation()))
				movedRed = oriRed[i];
			if(oriwhite[i].isVisible()&& !currentwhite[i].isVisible())
				eaten = true;
		}
		Point start = movedRed.getLocation();

		if(eaten)
			while(!from.equals(to)){
				if(Util.NorthWest(start)!=null && Util.NorthWest( Util.NorthWest(start))!=null && Util.hasChess(from, 
						Util.NorthWest(start)).equals("white") && Util.hasChess(to,Util.NorthWest(start)).equals("none") )
				{
					System.out.println("NorthWest");
					from.getWhite( Util.NorthWest(start)).setVisible(false);
					from.getRed(start).setLocation(Util.NorthWest( Util.NorthWest(start)));
					list.add(from.copy());
					start = Util.NorthWest( Util.NorthWest(start));
				}
				else if(Util.NorthEast(start)!=null && Util.NorthEast( Util.NorthEast(start))!=null && Util.hasChess(from, 
						Util.NorthEast(start)).equals("white") && Util.hasChess(to,Util.NorthEast(start)).equals("none") )
				{
					System.out.println("NorthEast");
					from.getWhite( Util.NorthEast(start)).setVisible(false);
					from.getRed(start).setLocation(Util.NorthEast( Util.NorthEast(start)));
					list.add(from.copy());
					start = Util.NorthEast( Util.NorthEast(start));
				}
				else if(Util.SouthWest(start)!=null && Util.SouthWest( Util.SouthWest(start))!=null && Util.hasChess(from, 
						Util.SouthWest(start)).equals("white") && Util.hasChess(to,Util.SouthWest(start)).equals("none") )
				{
					System.out.println("SouthWest");
					from.getWhite(Util.SouthWest(start)).setVisible(false);
					from.getRed(start).setLocation(Util.SouthWest( Util.SouthWest(start)));
					list.add(from.copy());
					start = Util.SouthWest( Util.SouthWest(start));
					System.out.println("SouthWest  add");
				}
				else if(Util.SouthEast(start)!=null && Util.SouthEast( Util.SouthEast(start))!=null && Util.hasChess(from, 
						Util.SouthEast(start)).equals("white") && Util.hasChess(to,Util.SouthEast(start)).equals("none") )
				{
					System.out.println("SouthEast");
					from.getWhite( Util.SouthEast(start)).setVisible(false);
					from.getRed(start).setLocation(Util.SouthEast( Util.SouthEast(start)));
					list.add(from.copy());
					start = Util.SouthEast( Util.SouthEast(start));
				}
			}

		else
			list.add(to);
		return list;
	}
	

	//这个方法是为robot走棋之后刷新棋盘用的
	public void setBorad(JPanel jp){
		chessBoard = jp;
	}
	
	//设置难度
	public void setLevel(int index){
		if(index==1)
			DEPTH = 3;
		else if(index==2)
			DEPTH = 6;
		else if(index==3)
			DEPTH = 9;
	}

	//检查是否一方没子了
	private void checkOver(ChessState state){
		Chess[] red = state.red;
		Chess[] white = state.white;
		for(int i=0;i<12;i++){
			if(red[i].isVisible())
				break;
			if(i==11){
				JOptionPane.showMessageDialog(null, "你赢了！！！");
				return ;
			}
		}
		for(int i=0;i<12;i++){
			if(white[i].isVisible())
				break;
			if(i==11){
				JOptionPane.showMessageDialog(null, "你输了！！！");
				return ;
			}
		}
	}
}
	
	
	
	
