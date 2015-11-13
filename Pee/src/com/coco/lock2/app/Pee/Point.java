package com.coco.lock2.app.Pee;

public class Point {
	
	public Point()
	{
		x=0;
		y=0;
	}
	public Point(float x,float y)
	{
		this.x=x;
		this.y=y;
	}
	public float x,y;
	public float getX() {
		return x;
	}

	public void setX(float x) {
		this.x = x;
	}

	public float getY() {
		return y;
	}

	public void setY(float y) {
		this.y = y;
	}
	
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return "Point("+x+","+y+")";
	}
}
