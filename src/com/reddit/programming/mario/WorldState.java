package com.reddit.programming.mario;

public class WorldState
{
	public byte[][] map;
	public int MapX, MapY;

	WorldState(byte[][] _map, float[] marioPosition) {
		map = _map;
		MapX = (int)marioPosition[0]/16 - 11;
		MapY = (int)marioPosition[1]/16 - 11;
	}
}

