package org.cloudbus.cloudsim.failure;

import org.cloudbus.cloudsim.DatacenterBroker;
import org.cloudbus.cloudsim.Log;
import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.core.CloudSimTags;
import org.cloudbus.cloudsim.core.SimEvent;
import org.cloudbus.cloudsim.distributions.ContinuousDistribution;
import org.cloudbus.cloudsim.distributions.UniformDistr;

/**
 * Created by ydoc on 12/5/2016.
 */
public class DatacenterBrokerWithFailure extends DatacenterBroker {

    public DatacenterBrokerWithFailure(String name) throws Exception {
        super(name);
    }

    @Override
    protected void processOtherEvent(SimEvent ev) {
        switch (ev.getTag()) {
            case CloudSimTags.FAILURE_GENERATOR_HOST_FAILURE:
                ContinuousDistribution distribution = new UniformDistr(0, getDatacenterIdsList().size());
                Double selectedDatacenter = distribution.sample();
                Log.printConcatLine("Fail host at time: ", CloudSim.clock(), " Datacenter id: ", selectedDatacenter.intValue());
                Integer dataCenterId = getDatacenterIdsList().get(selectedDatacenter.intValue());
                sendNow(dataCenterId, CloudSimTags.FAILURE_GENERATOR_SEND_HOST_FAILURE);
                break;
        }
    }
}
