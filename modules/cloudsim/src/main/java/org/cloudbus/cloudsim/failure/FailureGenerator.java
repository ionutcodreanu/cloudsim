package org.cloudbus.cloudsim.failure;

import org.cloudbus.cloudsim.DatacenterBroker;
import org.cloudbus.cloudsim.Log;
import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.core.CloudSimTags;
import org.cloudbus.cloudsim.core.SimEntity;
import org.cloudbus.cloudsim.core.SimEvent;
import org.cloudbus.cloudsim.distributions.ContinuousDistribution;

import java.util.List;

/**
 * Created by ydoc on 11/27/2016.
 */
public class FailureGenerator extends SimEntity {

    private final ContinuousDistribution failureDistribution;
    private int dataCenterBrokerEntityId;

    public FailureGenerator(String name, ContinuousDistribution failureDistribution) {
        super(name);
        this.failureDistribution = failureDistribution;
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
        for (int timeForFailure = 10; timeForFailure <= 3300; timeForFailure = timeForFailure + 150) {
            schedule(this.getId(), (double) timeForFailure, CloudSimTags.FAILURE_GENERATOR_INTERNAL_EVENT);
        }
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
}
