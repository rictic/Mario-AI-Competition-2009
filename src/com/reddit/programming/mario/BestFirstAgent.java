package com.reddit.programming.mario;

import ch.idsia.ai.agents.Agent;
import ch.idsia.ai.agents.RegisterableAgent;
import ch.idsia.mario.engine.sprites.Mario;
import ch.idsia.mario.environments.Environment;
import ch.idsia.mario.engine.GlobalOptions;

// if someone wants to make a multithreaded version, go nuts with the BlockingPriorityQueue
import java.util.PriorityQueue;
import java.util.Comparator;

//Based on ForwardAgent

public class BestFirstAgent extends RegisterableAgent implements Agent
{
  private boolean[] action;
  private int jumpCounter = 0;
  protected int[] marioPosition = null;
  protected Sensors sensors = new Sensors();

  MarioState ms;
  float pred_x, pred_y;

  public BestFirstAgent()
  {
    super("BestFirstAgent");
    action = new boolean[Environment.numberOfButtons];
    reset();
  }

  @Override
  public void reset()
  {
    // disable enemies for the time being
    GlobalOptions.pauseWorld = true;
  }

  private float cost(MarioState s, MarioState initial)
  {
    if(s.dead)
      return 1e30f; // dunno how to make an infinity in java
    // TODO: how far right can mario go from here holding down speed+right?
    return (initial.x + 11*16 - s.x)/9.71f;
  }

  private int searchForAction(MarioState initialState, byte[][] map, int MapX, int MapY)
  {
    Comparator<MarioState> msComparator = new MarioStateComparator();
    PriorityQueue<MarioState> pq = new PriorityQueue<MarioState>(20, msComparator);
    int a,n;
    // add initial set
    for(a=0;a<16;a++) {
      MarioState ms = initialState.next(a, map, MapX, MapY);
      ms.root_action = a;
      ms.cost = cost(ms, initialState);
      pq.add(ms);
    }

    for(n=0;n<10000;n++) {
      MarioState next = pq.remove();
      for(a=0;a<16;a++) {
        MarioState ms = next.next(a, map, MapX, MapY);
        if(ms.dead) continue;
        ms.cost = cost(ms, initialState);
        if(ms.cost <= 0) {
          System.out.printf("search terminated after %d iterations; best root_action=%d cost=%f\n", 
              n, ms.root_action, ms.cost);
          return ms.root_action;
        }
        pq.add(ms);
      }
    }

    MarioState bestfound = pq.remove();
    System.out.printf("giving up on search; best root_action=%d cost=%f\n", 
        bestfound.root_action, bestfound.cost);
    // return best so far
    return bestfound.root_action;
  }

  @Override
  public boolean[] getAction(Environment observation)
  {
    sensors.updateReadings(observation);
    marioPosition = sensors.getMarioPosition();
    float[] mpos = observation.getMarioFloatPos();
    if(ms == null) {
      // assume one frame of falling before we get an observation :(
      ms = new MarioState(mpos[0], mpos[1], 0.0f, 3.0f);
    } else {
      System.out.println(String.format("mario x,y=(%5.1f,%5.1f)", mpos[0], mpos[1]));
      if(mpos[0] != pred_x || mpos[1] != pred_y) {
        System.out.println("mario state mismatch; aborting");
        System.exit(1);
      }
    }

    if ((GlobalOptions.FPS != GlobalOptions.InfiniteFPS) && GlobalOptions.GameVeiwerOn)
      System.out.println(sensors);

    // quantize mario's position to get the map origin
    int mX = (int)mpos[0]/16 - 11;
    int mY = (int)mpos[1]/16 - 11;
    int next_action = searchForAction(ms, sensors.levelScene, mX,mY);
    ms = ms.next(next_action, sensors.levelScene, mX,mY);
    pred_x = ms.x;
    pred_y = ms.y;
    System.out.println(String.format("action: %d; predicted x,y=(%5.1f,%5.1f) xa,ya=(%5.1f,%5.1f)",
          next_action, ms.x, ms.y, ms.xa, ms.ya));

    action[Mario.KEY_SPEED] = (next_action&1)!=0;
    action[Mario.KEY_RIGHT] = (next_action&2)!=0;
    action[Mario.KEY_JUMP] = (next_action&4)!=0;
    action[Mario.KEY_LEFT] = (next_action&8)!=0;

    return action;
  }

}
