package org.cloudbus.cloudsim.failure;

import org.cloudbus.cloudsim.*;
import org.cloudbus.cloudsim.core.CloudSimTags;
import org.cloudbus.cloudsim.core.SimEvent;
import org.cloudbus.cloudsim.distributions.ContinuousDistribution;
import org.cloudbus.cloudsim.distributions.UniformDistr;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by ydoc on 11/28/2016.
 */
public class DatacenterWithFailure extends Datacenter {

    private List<Host> hostsThatFailed;

    public DatacenterWithFailure(String name, DatacenterCharacteristics characteristics, VmAllocationPolicy vmAllocationPolicy, List<Storage> storageList, double schedulingInterval) throws Exception {
        super(name, characteristics, vmAllocationPolicy, storageList, schedulingInterval);
        this.hostsThatFailed = new LinkedList<Host>();
    }

    @Override
    protected void processOtherEvent(SimEvent ev) {
        switch (ev.getTag()) {
            case CloudSimTags.FAILURE_GENERATOR_SEND_HOST_FAILURE:
                processRemoveHostFromList();
                break;
        }
    }

    private void processRemoveHostFromList() {
        List<Host> hosts = this.getHostList();
        int noOfHosts = hosts.size();

        ContinuousDistribution distribution = new UniformDistr(0, noOfHosts - 1);
        Double hostToBeDeleted = distribution.sample();
        Host failedHost = hosts.get(hostToBeDeleted.intValue());
        this.addFailedHost(failedHost);
//        ContinuousDistribution vmDistr = new UniformDistr(0, failedHost.getVmList().size() - 1);

        hosts.remove(hostToBeDeleted.intValue());
    }

    private void addFailedHost(Host failedHost) {
        this.hostsThatFailed.add(failedHost);
    }
}
