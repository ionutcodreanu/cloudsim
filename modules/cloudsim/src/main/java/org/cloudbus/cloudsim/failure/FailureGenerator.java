package org.cloudbus.cloudsim.failure;

import org.cloudbus.cloudsim.DatacenterBroker;
import org.cloudbus.cloudsim.Log;
import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.core.CloudSimTags;
import org.cloudbus.cloudsim.core.SimEntity;
import org.cloudbus.cloudsim.core.SimEvent;
import org.cloudbus.cloudsim.distributions.ContinuousDistribution;

import java.util.List;
import org.cloudbus.cloudsim.distributions.UniformDistr;

/**
 * Created by ydoc on 11/27/2016.
 */
public class FailureGenerator extends SimEntity {

  private final ContinuousDistribution failureDistribution;
  private int dataCenterBrokerEntityId;
  private double lastEventTime = 0;

  public FailureGenerator(String name) {
    super(name);
    this.failureDistribution = new UniformDistr(0, 1.5);
  }

  @Override
  public void startEntity() {
    Log.printConcatLine(getName(), " is starting...");
    List<SimEntity> entitiesList = CloudSim.getEntityList();
    for (SimEntity entity : entitiesList) {
      if (entity instanceof DatacenterBroker) {
        this.dataCenterBrokerEntityId = entity.getId();
      }
    }
    schedule(this.getId(), (double) 50, CloudSimTags.FAILURE_GENERATOR_INTERNAL_EVENT);
    schedule(this.getId(), (double) 100, CloudSimTags.FAILURE_GENERATOR_INTERNAL_EVENT);
    schedule(this.dataCenterBrokerEntityId, (double) 70, CloudSimTags.FAILURE_GENERATOR_CLOUDLET_FAILURE);
    schedule(this.dataCenterBrokerEntityId, (double) 120, CloudSimTags.FAILURE_GENERATOR_VM_FAILURE);
    schedule(this.getId(), (double) 250, CloudSimTags.FAILURE_GENERATOR_INTERNAL_EVENT);
    schedule(this.getId(), (double) 300, CloudSimTags.FAILURE_GENERATOR_INTERNAL_EVENT);
  }

  @Override
  public void processEvent(SimEvent ev) {
    switch (ev.getTag()) {
      case CloudSimTags.FAILURE_GENERATOR_INTERNAL_EVENT:
        Log.printConcatLine("Test internal: ", CloudSim.clock());
        sendNow(this.dataCenterBrokerEntityId, CloudSimTags.FAILURE_GENERATOR_HOST_FAILURE);
        break;
    }
  }

  @Override
  public void shutdownEntity() {
  }

  @Override
  public void run() {
    super.run();
//    double time = CloudSim.clock();
//    double sample = this.failureDistribution.sample();
//    if (sample > 1) {
//      schedule(this.getId(), (double) time + sample, CloudSimTags.FAILURE_GENERATOR_INTERNAL_EVENT);
//      Log.printConcatLine("test", time, ",", sample);
//    }

  }
}
