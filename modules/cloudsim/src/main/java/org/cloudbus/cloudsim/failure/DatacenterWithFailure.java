package org.cloudbus.cloudsim.failure;

import org.cloudbus.cloudsim.*;
import org.cloudbus.cloudsim.core.CloudSimTags;
import org.cloudbus.cloudsim.core.SimEvent;
import org.cloudbus.cloudsim.distributions.ContinuousDistribution;
import org.cloudbus.cloudsim.distributions.UniformDistr;

import java.util.LinkedList;
import java.util.List;
import org.cloudbus.cloudsim.lists.VmList;

/**
 * Created by ydoc on 11/28/2016.
 */
public class DatacenterWithFailure extends Datacenter {

  private List<Host> hostsThatFailed;

  public DatacenterWithFailure(String name, DatacenterCharacteristics characteristics,
      VmAllocationPolicy vmAllocationPolicy, List<Storage> storageList, double schedulingInterval)
      throws Exception {
    super(name, characteristics, vmAllocationPolicy, storageList, schedulingInterval);
    this.hostsThatFailed = new LinkedList<Host>();
  }

  @Override
  protected void processOtherEvent(SimEvent ev) {
    switch (ev.getTag()) {
      case CloudSimTags.FAILURE_GENERATOR_SEND_HOST_FAILURE:
        processRemoveHostFromList();
        break;
      case CloudSimTags.HOST_DESTROY:
        List<Host> hosts = this.getHostList();
        Host host = (Host) ev.getData();
        host.setFailed(true);
        this.addFailedHost(host);
//        hosts.remove(host);
        Log.printConcatLine("Host removed");
        break;
    }
  }

  private void processRemoveHostFromList() {
    List<Host> hosts = this.getHostList();
    int noOfHosts = hosts.size();
    if (noOfHosts > 0) {
      Host failedHost = this.getFirstAvailableHost(hosts);
      if (failedHost == null) {
        Log.printConcatLine("No host found for removal");
        return;
      }
      for (Vm vm : failedHost.getVmList()) {
        sendNow(this.getId(), CloudSimTags.VM_DESTROY, vm);
      }
      sendNow(this.getId(), CloudSimTags.HOST_DESTROY, failedHost);
    } else {
      Log.printConcatLine("No host found for removal");
    }
  }

  private Host getFirstAvailableHost(List<Host> hosts) {
    for (Host host : hosts) {
      if (!host.isFailed()) {
        return host;
      }
    }
    return null;
  }

  private void addFailedHost(Host failedHost) {
    this.hostsThatFailed.add(failedHost);
  }
}
