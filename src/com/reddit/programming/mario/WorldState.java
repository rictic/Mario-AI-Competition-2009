package com.reddit.programming.mario;

import java.util.HashMap;

public class WorldState
{
	public byte[][] map;
	public int MapX, MapY;
	WorldState pred = null;
	HashMap<WSHashKey, WorldState> succ; // successor map

	// hash key comparator
	private class WSHashKey {
		static final int MOD_NONE = 0;
		static final int MOD_REMOVETILE = 1;
		static final int MOD_STOMP = 2;
		public int modType = MOD_NONE;
		public int modTile = 0;
		public int modFrame = 0;
		WSHashKey(int _modTile) {
			modType = MOD_REMOVETILE;
			modTile = modTile;
		}

		@Override
		public int hashCode() {
			switch(modType) {
				case MOD_NONE: return 0;
				case MOD_REMOVETILE: return 1 + modTile*4;
				case MOD_STOMP: return 2; // plus whichever thing we stomped?
			}
			return -1;
		}

		@Override
		public boolean equals(Object _o) {
			WSHashKey o = (WSHashKey)_o;
			if(o.modType != modType) return false;
			if(o.modTile != modTile) return false;
			if(o.modFrame != modFrame) return false;
			return true;
		}
	}
	
	// public int lastModifiedFrame - i don't think i need this now, but it
	// seemed like a good idea at some point in order to consolidate similar frames

	WorldState(byte[][] _map, float[] marioPosition) {
		map = _map;
		MapX = (int)marioPosition[0]/16 - 11;
		MapY = (int)marioPosition[1]/16 - 11;
		succ = new HashMap<WSHashKey, WorldState>();
	}

	WorldState(byte[][] _map, int _MapX, int _MapY) {
		map = _map; MapX = _MapX; MapY = _MapY;
		succ = new HashMap<WSHashKey, WorldState>();
	}

	WorldState _removeTile(WSHashKey h, int x, int y) {
		byte[][] newmap = new byte[22][22];
		for(int j=0;j<22;j++)
			for(int i=0;i<22;i++)
				newmap[j][i] = map[j][i];
		newmap[y][x] = 0;
		WorldState ws = new WorldState(newmap, MapX, MapY);
		succ.put(h, ws);
		return ws;
	}

	WorldState removeTile(int x, int y) {
		WSHashKey h = new WSHashKey(x*22+y);
		WorldState s = succ.get(h);
		if(s == null)
			return _removeTile(h,x,y);
		return s;
	}
}

