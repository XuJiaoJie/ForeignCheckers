package foreignCheckers;

import java.awt.Point;

public class Chess {
	//Chess类保存了单个棋子的所有属性，包括位置，颜色，是否可见，是否为王
	private int id;
	private String color;
	private boolean king = false;
	private boolean selected = false;
	private Point location;
	private boolean visible = true;
	private boolean counted = false;
	
	public Chess(){
	}
	
	public Chess(String color, int id) {
		this.color = color;
		this.id = id;
	}

	public void setLocation(Point p){
		setLocation(p.x, p.y);
	}
	
	public void setLocation(int x, int y) {
		location = new Point(x, y);
		if (color.equals("red") && y/60 == 7) {
			king = true;
		}
		if (color.equals("white") && y/60 == 0) {
			king = true;
		}
	}
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getColor() {
		return color;
	}

	public void setColor(String color) {
		this.color = color;
	}

	public boolean isKing() {
		return king;
	}

	public void setKing(boolean king) {
		this.king = king;
	}

	public boolean isSelected() {
		return selected;
	}

	public void setSelected(boolean selected) {
		this.selected = selected;
	}

	public Point getLocation() {
		return location;
	}


	public boolean isVisible() {
		return visible;
	}

	public void setVisible(boolean visible) {
		this.visible = visible;
	}

	public boolean isCounted() {
		return counted;
	}

	public void setCounted(boolean counted) {
		this.counted = counted;
	}

	public boolean equals(Object object) {
		if (!(object instanceof Chess)) {
			return false;
		}
		Chess chess = (Chess)object;
		if(this.getColor().equals(chess.getColor())&&this.id == chess.id){
			return true;
		}
		return false;
	}
	
}
