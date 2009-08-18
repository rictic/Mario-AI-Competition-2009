package com.reddit.programming.mario;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.util.Vector;

public class DebugPolyLine
{
	public Color color;
	public Vector<Point> points;
	
	public DebugPolyLine(Color col)
	{
		points = new Vector<Point>();
		color = col;
	}
	
	public void AddPoint(Point point)
	{
		points.add(point);
	}

	public void AddPoint(float x, float y) {
		points.add(new Point((int)x, (int)y));
	}

	public int[] GetX(Graphics g)
	{
		int[] x = new int[points.size()];
		for (int i = 0; i < points.size(); i++)
		{
			x[i] = points.get(i).x;
		}
		return x;
	}

	public int[] GetY(Graphics g)
	{
		int[] y = new int[points.size()];
		for (int i = 0; i < points.size(); i++)
		{
			y[i] = points.get(i).y;
		}
		return y;
	}
}
