package foreignCheckers;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;

/**
 * 保存棋盘状态的类，保存了所有棋子的状态及评估值	
 */
public class ChessState {
	static Point[][] points = ChessBoard.points;
	Chess[] red;
	Chess[] white;
	static Chess preRed[];
	static Chess preWhite[];
	private Chess redKiller;  //刚吃过子的红子
	private Chess whiteKiller; //刚吃过子的白子
	private static final int[] w = new int[]{8,6,-6,8,-8,-4,4}; //评估函数的系数
	int depth;   //搜索时所在的深度
	int value;   //权值
	
	public ChessState(Chess[] red,Chess[] white,boolean isFirst) {
		//棋手走完后第一次创建新状态时要把初始状态保存起来
		if(isFirst){
			preRed = red;
			preWhite = white;
		}
		//生成新状态时要创建新的Chess数组，不然保存的只是原来状态的引用
		Chess[] red1 = new Chess[12];
		Chess[] white1 =new Chess[12];
		for(int i=0;i<12;i++){
			red1[i] = new Chess();
			red1[i].setId(red[i].getId());
			red1[i].setColor(red[i].getColor());
			red1[i].setLocation(red[i].getLocation());
			red1[i].setVisible(red[i].isVisible());
			red1[i].setKing(red[i].isKing());
			
			white1[i] = new Chess();
			white1[i].setId(white[i].getId());
			white1[i].setColor(white[i].getColor());
			white1[i].setLocation(white[i].getLocation());
			white1[i].setVisible(white[i].isVisible());
			white1[i].setKing(white[i].isKing());
		}
		this.red = red1;
		this.white = white1;
	}

	/**
	 * 估值函数
	 */
	public int getValue(){
		int redNumber = getRedNumber();
		int whiteNumber = getWhiteNumber();
		int redKingNumber = getRedKingNumber();
		int whiteKingNumber = getWhiteKingNumber();
		int redBeingKillerNumber = getRedBeingKilledNumber();
		int whiteBeingKillerNuumber = getWhiteBeingKilledNumber();
		if (redNumber+redKingNumber==0) {
			return -99999;
		}
		if (whiteNumber+whiteKingNumber==0) {
			return 99999;
		}
		//估算值
		int ww = w[0] + w[1]*redNumber + w[2]*whiteNumber + w[3]*redKingNumber + w[4]*whiteKingNumber 
				+ w[5]*redBeingKillerNumber + w[6]*whiteBeingKillerNuumber;
		return ww;
	}
	
	//红方下一步所有可能状态
	public ArrayList<ChessState> nextStatesOfRed(){
		ArrayList<ChessState> list = new ArrayList<>();
		for(int i=0;i<12;i++){//首先寻找吃子的状态
			if(!red[i].isVisible())
				continue;
			list.addAll(nextEatingStates(red[i],this));
		}
		if(list.size()==0) //没有吃子的情况再找其他状态
			for(int i=0;i<12;i++){
				if(!red[i].isVisible())
					continue;
				list.addAll(nextStates(red[i],this));
			}
		return list;
	}

	//白方下一步所有可能状态
	public ArrayList<ChessState> nextStatesOfWhite(){
		ArrayList<ChessState> list = new ArrayList<>();
		for(int i=0;i<12;i++){
			if(!white[i].isVisible())
				continue;
			list.addAll(nextEatingStates(white[i],this));
		}
		if(list.size()==0)
			for(int i=0;i<12;i++){
				if(!white[i].isVisible())
					continue;
				list.addAll(nextStates(white[i],this));
			}
		return list;
	}
	
	
	
	//吃子的所有下一步状态
	private ArrayList<ChessState> nextEatingStates(Chess chess,ChessState st){
		ArrayList<ChessState> list = new ArrayList<>();
		Point pp = chess.getLocation();
		int x = pp.x/60 +1;
		int y = pp.y/60 +1;
		Chess red[] = st.red;
		Chess white[] = st.white;
		
		if(chess.getColor().equals("red")){
			if(Util.eat(chess,st)){ //可以吃子，必须吃。返回的状态只是吃子的情况
				if(chess.isVisible()){	
					for(int j=0;j<12;j++){//检查四个角
						if(x-2>0&&y-2>0&&chess.isKing() && white[j].isVisible()&&white[j].getLocation().equals(points[x-1][y-1])&&hasChess(points[x-2][y-2]).equals("none"))
						{
							ChessState state = new ChessState(red ,white,false);
							state.getRed(points[x][y]).setLocation(points[x-2][y-2]);
							state.redKiller = state.getRed(points[x-2][y-2]);
							state.getWhite(points[x-1][y-1]).setVisible(false);
							list.add(state);
						}
						if(y+2<9&&x-2>0&&white[j].getLocation().equals(points[x-1][y+1])&&white[j].isVisible()&&hasChess(points[x-2][y+2]).equals("none"))
						{
							ChessState state = new ChessState(red ,white,false);
							state.getRed(points[x][y]).setLocation(points[x-2][y+2]);
							state.redKiller = state.getRed(points[x-2][y+2]);
							state.getWhite(points[x-1][y+1]).setVisible(false);
							list.add(state);
						}
						if(x+2<9&&y-2>0&&chess.isKing() && white[j].isVisible()&&white[j].getLocation().equals(points[x+1][y-1])&&hasChess(points[x+2][y-2]).equals("none"))
						{
							ChessState state = new ChessState(red ,white,false);
							state.getRed(points[x][y]).setLocation(points[x+2][y-2]);
							state.redKiller = state.getRed(points[x+2][y-2]);
							state.getWhite(points[x+1][y-1]).setVisible(false);
							list.add(state);
						}
						if(x+2<9&&y+2<9&&white[j].getLocation().equals(points[x+1][y+1])&&white[j].isVisible()&&hasChess(points[x+2][y+2]).equals("none"))
						{
							ChessState state = new ChessState(red ,white,false);
							state.getRed(points[x][y]).setLocation(points[x+2][y+2]);
							state.redKiller = state.getRed(points[x+2][y+2]);
							state.getWhite(points[x+1][y+1]).setVisible(false);
							list.add(state);
						}
					}
				}	
			}
			ArrayList<ChessState> temp = new ArrayList<>();
			for(int i=0;i<list.size();i++){
				temp.add(null);
			}
			Collections.copy(temp, list);
			ArrayList<ChessState> nextLevelList = new ArrayList<>();
			for(Iterator<ChessState> it = temp.iterator();it.hasNext();){
				ChessState s = (ChessState)it.next();
				if(Util.eat(s.redKiller, s)){
					nextLevelList.addAll(nextEatingStates(s.redKiller,s));
				}
			}
			if(nextLevelList.size()>0)//下一层还有就返回下一层
				return nextLevelList;
			if(list.size()>0)
				return list;
		}
		else{ //white
			if(Util.eat(chess,st)){ //可以吃子，必须吃。返回的状态只是吃子的情况
				if(chess.isVisible()){	
					for(int j=0;j<12;j++){//四个角
						if(x-2>0&&y-2>0 && red[j].isVisible()&&red[j].getLocation().equals(points[x-1][y-1])&&hasChess(points[x-2][y-2]).equals("none"))
						{
							ChessState state = new ChessState(red ,white,false);
							state.getWhite(points[x][y]).setLocation(points[x-2][y-2]);
							state.whiteKiller = state.getWhite(points[x-2][y-2]);
							state.getRed(points[x-1][y-1]).setVisible(false);  
							list.add(state);
						}
						if(y+2<9&&x-2>0&&chess.isKing()&&red[j].getLocation().equals(points[x-1][y+1])&&red[j].isVisible()&&hasChess(points[x-2][y+2]).equals("none"))
						{
							ChessState state = new ChessState(red ,white,false);
							state.getWhite(points[x][y]).setLocation(points[x-2][y+2]);
							state.whiteKiller = state.getWhite(points[x-2][y+2]);
							state.getRed(points[x-1][y+1]).setVisible(false);
							list.add(state);
						}
						if(x+2<9&&y-2>0 && red[j].isVisible()&&red[j].getLocation().equals(points[x+1][y-1])&&hasChess(points[x+2][y-2]).equals("none"))
						{
							ChessState state = new ChessState(red ,white,false);
							state.getWhite(points[x][y]).setLocation(points[x+2][y-2]);
							state.whiteKiller = state.getWhite(points[x+2][y-2]);
							state.getRed(points[x+1][y-1]).setVisible(false);
							list.add(state);
						}
						if(x+2<9&&y+2<9&&chess.isKing()&&red[j].getLocation().equals(points[x+1][y+1])&&red[j].isVisible()&&hasChess(points[x+2][y+2]).equals("none"))
						{
							ChessState state = new ChessState(red ,white,false);
							state.getWhite(points[x][y]).setLocation(points[x+2][y+2]);
							state.whiteKiller = state.getWhite(points[x+2][y+2]);
							state.getRed(points[x+1][y+1]).setVisible(false);
							list.add(state);
						}
					}
				}	
			}

			ArrayList<ChessState> temp = new ArrayList<>();
			for(int i=0;i<list.size();i++){
				temp.add(null);
			}
			Collections.copy(temp, list);
			ArrayList<ChessState> nextLevelList = new ArrayList<>();
			for(Iterator<ChessState> it = temp.iterator();it.hasNext();){
				ChessState s = (ChessState)it.next();
				if(Util.eat(s.whiteKiller, s)){
					nextLevelList.addAll(nextEatingStates(s.whiteKiller,s));
				}
			}
			
			if(nextLevelList.size()>0)
				return nextLevelList;
			if(list.size()>0)
				return list;
		}
		
		return list;
	}
	
	//不吃子的所有下一步状态
	private ArrayList<ChessState> nextStates(Chess chess,ChessState st){
		ArrayList<ChessState> list = new ArrayList<>();
		Point pp = chess.getLocation();
	
		Point southEast = Util.SouthEast(pp);
		Point southWest = Util.SouthWest(pp);
		Point northWest = Util.NorthWest(pp);
		Point northEast = Util.NorthEast(pp);

		Chess red[] = st.red;
		Chess white[] = st.white;

		if(chess.getColor().equals("red")){

			if(southEast!=null && hasChess(southEast).equals("none")){
				ChessState state = new ChessState(red ,white,false);
				state.getRed(pp).setLocation(southEast);
				list.add(state);
			}
			if(southWest!=null && hasChess(southWest).equals("none")){
				ChessState state = new ChessState(red ,white,false);
				state.getRed(pp).setLocation(southWest);
				list.add(state);
			}
			if(northEast!=null && hasChess(northEast).equals("none") && chess.isKing()){
				ChessState state = new ChessState(red ,white,false);
				state.getRed(pp).setLocation(northEast);
				list.add(state);
			}
			if(northWest!=null && hasChess(northWest).equals("none") && chess.isKing()){
				ChessState state = new ChessState(red ,white,false);
				state.getRed(pp).setLocation(northWest);                                              
				list.add(state);
			}
			if(southEast!=null){
				Point ssEast = Util.SouthEast(southEast);
				if(ssEast!=null && hasChess(southEast).equals("white") && hasChess(ssEast).equals("none")){
					ChessState state = new ChessState(red,white,false);
					state.getRed(pp).setLocation(ssEast);
					state.getWhite(southEast).setVisible(false);
					list.add(state);
				}
			}
			if(southWest!=null){
				Point ssWest = Util.SouthWest(southWest);
				if(ssWest!=null && hasChess(southWest).equals("white") && hasChess(ssWest).equals("none")){
					ChessState state = new ChessState(red,white,false);
					state.getRed(pp).setLocation(ssWest);
					state.getWhite(southWest).setVisible(false);
					list.add(state);
				}
			}
			if(northWest!=null){
				Point nnWest = Util.NorthWest(northWest);
				if(chess.isKing() && nnWest!=null && hasChess(northWest).equals("white") && hasChess(nnWest).equals("none")){
					ChessState state = new ChessState(red,white,false);
					state.getRed(pp).setLocation(nnWest);
					state.getWhite(northWest).setVisible(false);
					list.add(state);
				}
			}
			if(northEast!=null){
				Point nnEast =Util.NorthEast(northEast);
				if(chess.isKing() && nnEast!=null && hasChess(northEast).equals("white") && hasChess(nnEast).equals("none")){
					ChessState state = new ChessState(red,white,false);
					state.getRed(pp).setLocation(nnEast);
					state.getWhite(northEast).setVisible(false);
					list.add(state);
				}
			}
			return list;	
		}
		else{ //white
		
			if(southEast!=null && chess.isKing() && hasChess(southEast).equals("none")){
				ChessState state = new ChessState(red ,white,false);
				state.getWhite(pp).setLocation(southEast);
				list.add(state);
			}
			if(southWest!=null && chess.isKing() && hasChess(southWest).equals("none")){
				ChessState state = new ChessState(red ,white,false);
				state.getWhite(pp).setLocation(southWest);
				list.add(state);
			}
			if(northEast!=null && hasChess(northEast).equals("none")){
				ChessState state = new ChessState(red ,white,false);
				state.getWhite(pp).setLocation(northEast);
				list.add(state);
			}
			if(northWest!=null && hasChess(northWest).equals("none")){
				ChessState state = new ChessState(red ,white,false);
				state.getWhite(pp).setLocation(northWest);
				list.add(state);
			}
			if(southEast!=null){
				Point ssEast = Util.SouthEast(southEast);
				if(chess.isKing() && ssEast!=null && hasChess(southEast).equals("red") && hasChess(ssEast).equals("none")){
					ChessState state = new ChessState(red,white,false);
					state.getWhite(pp).setLocation(ssEast);
					state.getRed(southEast).setVisible(false);
					list.add(state);
				}
			}
			if(southWest!=null){
				Point ssWest = Util.SouthWest(southWest);
				if(chess.isKing() && ssWest!=null && hasChess(southWest).equals("red") && hasChess(ssWest).equals("none")){
					ChessState state = new ChessState(red,white,false);
					state.getWhite(pp).setLocation(ssWest);
					state.getRed(southWest).setVisible(false);
					list.add(state);
				}
			}
			if(northWest!=null){
				Point nnWest = Util.NorthWest(northWest);
				if(nnWest!=null && hasChess(northWest).equals("red") && hasChess(nnWest).equals("none")){
					ChessState state = new ChessState(red,white,false);
					state.getWhite(pp).setLocation(nnWest);
					state.getRed(northWest).setVisible(false);
					list.add(state);
				}
			}
			if(northEast!=null){
				Point nnEast = Util.NorthEast(northEast);
				if(nnEast!=null && hasChess(northEast).equals("red") && hasChess(nnEast).equals("none")){
					ChessState state = new ChessState(red,white,false);
					state.getWhite(pp).setLocation(nnEast);
					state.getRed(northEast).setVisible(false);
					list.add(state);
				}	
			}
			return list;
		}
	}
	
	//红子个数
	private int getRedNumber(){
		int count = 0;
		for(int i=0;i<12;i++){
			if(red[i].isVisible()&&!red[i].isKing()){
				count++;
			}
		}
		return count;
	}
	
	//白子个数
	private int getWhiteNumber() {
		int count = 0;
		for(int i=0;i<12;i++){
			if (white[i].isVisible()&&!white[i].isKing()) {
				count++;
			}
		}
		return count;
	}
	
	//红王个数
	private int getRedKingNumber(){
		int count = 0;
		for(int i=0;i<12;i++){
			if (red[i].isVisible()&&red[i].isKing()) {
				count++;
			}
		}
		return count;
	}
	
	//白王个数
	private int getWhiteKingNumber(){
		int count = 0;
		for(int i=0;i<12;i++){
			if (white[i].isVisible()&&white[i].isKing()) {
				count++;
			}
		}
		return count;
	}
	
	//红子将要被吃掉的个数
	private int getRedBeingKilledNumber(){
		for(int i=0;i<12;i++){
			red[i].setCounted(false);
		}
		int x=0,y=0,count=0;
		for(int i=0;i<12;i++){
			if (white[i].isVisible()) {
				for(int ii=1;ii<9;ii++){
					for(int jj=1;jj<9;jj++){
						if (points[ii][jj].equals(white[i].getLocation())) {
							x=ii;
							y=jj;
						}
					}
				}
				for(int j=0;j<12;j++){
					if (x-2>0&&y+2<9&&white[i].isKing()&&red[j].isVisible()&&red[j].getLocation()
							.equals(points[x-1][y+1])&&hasChess(points[x-2][y+2]).equals("none")&&!red[j].isCounted()) {
						count++;
						red[j].setCounted(true);
					}
					if(x-2>0&&y-2>0&&red[j].getLocation().equals(points[x-1][y-1])&&red[j].isVisible()&&hasChess(points[x-2][y-2]).equals("none")&& !red[j].isCounted())
					{
						count++;
						red[j].setCounted(true);
					}
					if(x+2<9&&y+2<9&&white[i].isKing() &&red[j].isVisible()&& red[j].getLocation().equals(points[x+1][y+1])&&y+2<9&&hasChess(points[x+2][y+2]).equals("none")&& !red[j].isCounted())
					{
						count++;
						red[j].setCounted(true);
					}
					if(x+2<9&&y-2>0&&red[j].getLocation().equals(points[x+1][y-1])&&red[j].isVisible()&&hasChess(points[x+2][y-2]).equals("none")&& !red[j].isCounted())
					{
						count++;
						red[j].setCounted(true);
					}
				}
			}
		}
		return count;
	}
	
	//白子将要被吃掉的个数
	public int getWhiteBeingKilledNumber(){
		for(int i=0;i<12;i++){
			white[i].setCounted(false);
		}
		int x = 0, y = 0, count = 0;
		for(int i=0;i<12;i++){
			if(red[i].isVisible()){	
				for(int ii=1;ii<9;ii++)
					for(int jj=1;jj<9;jj++){
						if(points[ii][jj] .equals(red[i].getLocation())){
							x = ii;
							y = jj;
						}		
					}
				for(int j=0;j<12;j++){//四个角
					if(x-2>0&&y-2>0&&red[i].isKing() && white[j].isVisible()&&white[j].getLocation().equals(points[x-1][y-1])&&hasChess(points[x-2][y-2]).equals("none")&&!white[j].isCounted())
					{
						count++;
						white[j].setCounted(true); 
					}
					if(x-2>0&&y+2<9&&white[j].getLocation().equals(points[x-1][y+1])&&white[j].isVisible()&&hasChess(points[x-2][y+2]).equals("none")&&!white[j].isCounted())
					{
						count++;
						white[j].setCounted(true); 
					}
					if(x+2<9&&y-2>0&&red[i].isKing() && white[j].isVisible()&&white[j].getLocation().equals(points[x+1][y-1])&&hasChess(points[x+2][y-2]).equals("none")&&!white[j].isCounted())
					{
						count++;
						white[j].setCounted(true); 
					}
					if(x+2<9&&y+2<9&&white[j].getLocation().equals(points[x+1][y+1])&&white[j].isVisible()&&hasChess(points[x+2][y+2]).equals("none")&&!white[j].isCounted())
					{
						count++;
						white[j].setCounted(true); 
					}
				}
			}	
		}
		return count;
	}
	//这个点有没有子
	private String hasChess(Point pp){
		if(pp==null)
			return null;

		for(int i=0;i<12;i++){
			if(red[i].isVisible()&&red[i].getLocation().equals(pp))
				return "red";
			if(white[i].isVisible()&&white[i].getLocation().equals(pp))
				return "white";
		}
		return "none";
	}
	
	//返回p这个点的白子
	public Chess getWhite(Point p){
		for(int i=0;i<12;i++)
			if(white[i].isVisible() && white[i].getLocation().equals(p))
				return white[i];
		return null;
	}
	
	//返回p这个点的红子
	public Chess getRed(Point p){
		for(int i=0;i<12;i++)
			if(red[i].isVisible() && red[i].getLocation().equals(p))
				return red[i];
		return null;
	}
	
	
	public ChessState copy(){
		ChessState st = new ChessState(red,white,false);
		return st;
		
	}
	

	public boolean equals(Object o){
		if(!(o instanceof ChessState))
			return false;
		ChessState s = (ChessState)o;
		Chess redO[] = s.red;
		Chess white0[] = s.white;
		for(int i=0;i<12;i++){
			if(!(red[i].getLocation().equals(redO[i].getLocation()) && 
					red[i].isVisible()==redO[i].isVisible() && red[i].isKing()==redO[i].isKing()))
				return false;
			if(!(white[i].getLocation().equals(white0[i].getLocation()) && 
					white[i].isVisible()==white0[i].isVisible() && white[i].isKing()==white0[i].isKing()))
				return false;
		}
		return true;
	}
	
	
	
	
	
	
	
	
	
}
