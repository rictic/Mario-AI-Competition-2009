package com.reddit.programming.mario;

import java.awt.Graphics;
import java.util.Vector;

import ch.idsia.mario.engine.LevelScene;

public class DebugPolyLineList
{
	private StaticMario mario = null;
	private Vector<DebugPolyLine> polyLines = new Vector<DebugPolyLine>(0);
	
	public final int MAXSIZE = 400;

	private boolean DrawFilled;
	
	public DebugPolyLineList(boolean drawFilled)
	{
		DrawFilled = drawFilled;
	}
	
	public DebugPolyLineList()
	{
		DrawFilled = false;
    }

	public int Size()
	{
		return polyLines.size();
	}
	
	public DebugPolyLine Pop()
	{
		if (Size() > 0)
		{
			return polyLines.remove(0);
		}
		else
		{
			return null;
		}
	}
	
	public void Push(DebugPolyLine polyLine)
	{
		if (Size() < MAXSIZE)
		{
			polyLines.add(polyLine);
		}
	}
	
	public void Clear()
	{
		polyLines.clear();
	}

	public void DrawAll(Graphics g, LevelScene w, int xcam, int ycam)
	{
		for (int i = polyLines.size() - 1; i >= 0; i--)
		{
			DebugPolyLine line = polyLines.elementAt(i);
			if (line != null)
			{
				g.setColor(line.color);
				if (i == 0)
				{
					drawThickPolyline(g, w, line.GetX(g), line.GetY(g), line.points.size(), 4, xcam, ycam);
				}
				else
				{
					g.drawPolyline(line.GetX(g), line.GetY(g), line.points.size());
				}
			}
		}
		
		polyLines.clear();
	}

	public void PushFront(DebugPolyLine line2)
	{
		polyLines.add(0, line2);
		while (Size() > MAXSIZE)
		{
			polyLines.removeElementAt(Size() - 1);
		}
	}

	private void drawThickPolyline(Graphics g, LevelScene w, int[] xArray, int[] yArray, int count, int thickness, int xcam, int ycam)
	{
		// The thick line is in fact a filled polygon
		int i = 0;
		for ( ; i < count - 1; i++)
		{
			int x1, x2, y1, y2;
			
			x1 = xArray[i];
			y1 = yArray[i];
			x2 = xArray[i+1];
			y2 = yArray[i+1];
			
			int dX = x2 - x1;
			int dY = y2 - y1;
			// line length
			double lineLength = Math.sqrt(dX * dX + dY * dY);
	
			double scale = (double) (thickness) / (2 * lineLength);
	
			// The x,y increments from an endpoint needed to create a rectangle...
			double ddx = -scale * (double) dY;
			double ddy = scale * (double) dX;
			ddx += (ddx > 0) ? 0.5 : -0.5;
			ddy += (ddy > 0) ? 0.5 : -0.5;
			int dx = (int) ddx;
			int dy = (int) ddy;
	
			// Now we can compute the corner points...
			int xPoints[] = new int[4];
			int yPoints[] = new int[4];
	
			xPoints[0] = x1 + dx;
			yPoints[0] = y1 + dy;
			xPoints[1] = x1 - dx;
			yPoints[1] = y1 - dy;
			xPoints[2] = x2 - dx;
			yPoints[2] = y2 - dy;
			xPoints[3] = x2 + dx;
			yPoints[3] = y2 + dy;

			if (DrawFilled)
			{
				g.fillPolygon(xPoints, yPoints, 4);
			}
			else
			{
				g.drawPolygon(xPoints, yPoints, 4);
			}
		}
        if (mario == null)
        {
            mario = new StaticMario(w, xArray[0], yArray[0], 50, 0);
            w.addSprite(mario);
        }
		else
        {
            mario.x = xArray[0];
            mario.y = yArray[0];
        }
	}
}
