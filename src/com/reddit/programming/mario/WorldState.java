package com.reddit.programming.mario;

import java.util.HashMap;
import java.util.Vector;

public final class WorldState
{
	public byte[][] map;
	public int[] heightmap;
	public int MapX, MapY;

	// List of currently known enemies; maintained sorted by x coordinate
	public Vector<SpriteState> enemies;

	WorldState pred = null;
	HashMap<WSHashKey, WorldState> succ; // successor map

	// hash key comparator
	private class WSHashKey {
		static final int MOD_NONE = 0;
		static final int MOD_REMOVETILE = 1;
		static final int MOD_STOMP = 2;
		public int modType = MOD_NONE;
		public int modTile = 0;
		public SpriteState modEnemy = null;

		WSHashKey() { modType = MOD_NONE; }
		WSHashKey(int _modTile) {
			modType = MOD_REMOVETILE;
			modTile = _modTile;
		}

		@Override
		public int hashCode() {
			switch(modType) {
				case MOD_NONE: return 0;
				case MOD_REMOVETILE: return 1 + modTile*4;
				case MOD_STOMP: return 2 + modEnemy.hashCode()*4;
			}
			return -1;
		}

		@Override
		public boolean equals(Object _o) {
			WSHashKey o = (WSHashKey)_o;
			if(o.modType != modType) return false;
			if(o.modTile != modTile) return false;
			if(o.modEnemy != modEnemy) return false;
			return true;
		}
	}
	
	public WorldState(byte[][] _map, float[] marioPosition, float[] enemyPosition) {
		map = _map;
		MapX = (int)marioPosition[0]/16 - 11;
		MapY = (int)marioPosition[1]/16 - 11;
		succ = new HashMap<WSHashKey, WorldState>();
		enemies = new Vector<SpriteState>();
		syncEnemies(enemyPosition);
		buildHeightMap();
	}

	WorldState() {}

	public WorldState clone() {
		WorldState w = new WorldState();
		w.map = map; w.MapX = MapX; w.MapY = MapY;
		w.heightmap = heightmap;
		w.succ = new HashMap<WSHashKey, WorldState>();
		w.enemies = enemies; // share enemies vector by default
		return w;
	}

	// nondestructive step
	public WorldState step() {
		WSHashKey h = new WSHashKey();
		WorldState s = succ.get(h);
		if(s == null) {
			s = clone();
			s.enemies = (Vector<SpriteState>)enemies.clone();
			s.stepEnemies();
			succ.put(h, s);
		}
		return s;
	}

	// destructive update
	public void update(byte[][] _map, float[] marioPosition, float[] enemyPosition) {
		map = _map;
		MapX = (int)marioPosition[0]/16 - 11;
		MapY = (int)marioPosition[1]/16 - 11;
		buildHeightMap();
		stepEnemies();
		syncEnemies(enemyPosition);
		succ.clear();
	}

	void buildHeightMap() {
		heightmap = new int[22];
		//System.out.printf("heightmap: ");
		for(int i=0;i<22;i++) {
			int j;
			for(j=21;j>=0;j--) // find the first block from the bottom
				if(map[j][i] != 0) break;
			if(j < 0) {
				heightmap[i] = 22;
			} else {
				for(;j>=0;j--)
					if(map[j][i] == 0 || map[j][i] == 1) break; // 1 is mario, 0 is blank
				heightmap[i] = j+1;
			}
			//System.out.printf("%02d ", heightmap[i]);
		}
		//System.out.printf("\n");
	}

	//////////////////////////////////////////////
	// destructive operations
	void _removeTile(WSHashKey h, int x, int y) {
		byte[][] newmap = new byte[22][22];
		for(int j=0;j<22;j++)
			for(int i=0;i<22;i++)
				newmap[j][i] = map[j][i];
		newmap[y][x] = 0;
		map = newmap;
	}

	private static class EnemyObservation implements Comparable<EnemyObservation> {
		int type;
		float x, y;

		EnemyObservation(int type, float x, float y) { this.type=type; this.x=x; this.y=y; }
		public int compareTo(EnemyObservation b) {
			return x<b.x ? -1 : x>b.x ? 1 : 0;
		}
	}

	// this function is terrible and slow, but it only needs to be done once per real frame.
	public void syncEnemies(float[] enemyObs) {
		// when we get a new observation, sort the observation by x and filter
		// through the list, using the nearest enemy of the same type and comparing
		// predicted states with actual

		EnemyObservation[] obs = new EnemyObservation[enemyObs.length/3];
		for(int i=0;i<enemyObs.length;i+=3)
			obs[i/3] = new EnemyObservation((int)enemyObs[i], enemyObs[i+1], enemyObs[i+2]);

		// TODO: make this one pass, left-to-right, sorted ascending x
//		Arrays.sort(obs);

		// merge enemy observations into our internal enemy array
		Vector<SpriteState> newenemies = new Vector<SpriteState>(obs.length+2);
		for(EnemyObservation eobs : obs) {
			SpriteState closest = null;
			float closestdist=Float.POSITIVE_INFINITY;
			for(SpriteState s : enemies) {
				if(s.type != eobs.type)
					continue;
				float ex = s.x - eobs.x;
				float ey = s.y - eobs.y;
				float dist = ex*ex + ey*ey;
				if(closest == null || dist < closestdist) {
					closest = s;
					closestdist = dist;
				}
			}
			if(closest==null || closestdist > 16) { // allow a slop of 4 pixels (4^2 = 16)
				// assume new enemy
				//System.out.printf("new enemy @%f,%f type %d\n",
				//		eobs.x, eobs.y, eobs.type);
				closest = SpriteState.newEnemy(eobs.x, eobs.y, eobs.type);
			} else {
				if(closestdist != 0) {
					//System.out.printf("enemy t=%d sync problem: %f,%f -> %f,%f\n",
					//		eobs.type, closest.x, closest.y, eobs.x, eobs.y);
					closest.x = eobs.x;
					closest.y = eobs.y;
				}
				enemies.remove(closest);
			}
			if(closest != null)
				newenemies.add(closest);
		}
//		for(SpriteState s : enemies) {
//			//System.out.printf("enemy t=%d @%f,%f disappeared?\n",
//			//		s.type, s.x, s.y);
//		}
		enemies = newenemies;
	}

	public void stepEnemies() {
		for(int i=0;i<enemies.size();i++) {
			SpriteState e = enemies.get(i).clone();
			boolean keep = e.move(this);
			if(keep) {
				enemies.set(i, e);
			} else {
				enemies.remove(i);
				i--;
			}
		}
	}

	// interact with mario after everyone does their move step
	// destructively updates MarioState, but non-destructively returns updated
	// WorldState
	public WorldState interact(MarioState ms) {
		WorldState ws = this;
		for(SpriteState e : enemies) {
			// TODO: if it's a shell or fireball, then skip it
			if(e.type >= SpriteState.KIND_SHELL)
				continue;
			ws = e.collideCheck(ws, ms);
		}
		// TODO: now do the shells
		// TODO: now do the fireballs
		return ws;
	}

	//////////////////////////////////////////////
	// functional operations
	WorldState removeTile(int x, int y) {
		x -= MapX; y -= MapY;
		if(x < 0 || x >= 22 || y < 0 || y >= 22)
			return this;

		WSHashKey h = new WSHashKey(x*22+y);
		WorldState s = succ.get(h);
		if(s == null) {
			s = clone();
			s._removeTile(h,x,y);
			succ.put(h, s);
		}
		return s;
	}

	final byte getBlock(int x, int y) {
		// move x,y world coordinates to the 22x22 reference frame
		x -= MapX;
		y -= MapY;
		if(x < 0 || x >= 22 || y < 0 || y >= 22)
			return 0;

		return map[y][x];
	}

	final boolean isBlocking(int x, int y, float xa, float ya) {
		byte block = getBlock(x,y);

		if(block == 1) return false; // mario; ignore
		if(block == 34) return false; // coin
		if(block == -11) return ya > 0; // platform
		return block != 0;
	}

	final WorldState stomp(SpriteState e, MarioState ms) {
		// destructively modify mario
		ms.stomp(e);
		// clone us, and clone e, and splice e in the array
		WorldState ws = clone();
		ws.enemies = (Vector)enemies.clone();
		ws.enemies.set(ws.enemies.indexOf(e), e.stomp(this));
		return ws;
	}

	final WorldState bump(int x, int y, boolean big) {
		//System.out.printf("bumping tile @%d,%d = %d\n", x,y,getBlock(x,y));
		//if(big) {
		//	return removeTile(x,y);
		//}
		return this;
	}

}

