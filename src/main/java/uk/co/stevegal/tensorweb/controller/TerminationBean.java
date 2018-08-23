package uk.co.stevegal.tensorweb.controller;

import org.tensorflow.Session;

import javax.annotation.PreDestroy;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;

/**
 * Created by stevegal on 23/08/2018.
 */
public class TerminationBean {

  private BlockingQueue<Session> sessions;

  public TerminationBean(BlockingQueue<Session> sessions) {

    this.sessions = sessions;
  }

  @PreDestroy
  public void doDestroy(){
    List<Session> drainSessions = new ArrayList<>();
    this.sessions.drainTo(drainSessions);
    for(Session session: drainSessions) {
      session.close();
    }
  }

}
