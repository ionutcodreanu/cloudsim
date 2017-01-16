package org.cloudbus.cloudsim.failure;

import java.util.ArrayList;
import java.util.List;
import org.cloudbus.cloudsim.Cloudlet;
import org.cloudbus.cloudsim.Datacenter;
import org.cloudbus.cloudsim.DatacenterBroker;
import org.cloudbus.cloudsim.Host;
import org.cloudbus.cloudsim.Log;
import org.cloudbus.cloudsim.Vm;
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
  protected void processOtherEvent(SimEvent event) {
    switch (event.getTag()) {
      case CloudSimTags.FAILURE_GENERATOR_HOST_FAILURE:
        Host host = this.getRandomHost();
        int selectedDatacenterId = host.getDatacenter().getId();
        Log.printConcatLine("Fail host at time: ", CloudSim.clock(), " Datacenter id: ",
            selectedDatacenterId);
        sendNow(selectedDatacenterId, CloudSimTags.FAILURE_GENERATOR_DATACENTER_HOST_FAILURE, host);
        break;
      case CloudSimTags.FAILURE_GENERATOR_VM_FAILURE:

        Vm selectedVmToFail = this.getRandomVm();
        Host hostForVM = selectedVmToFail.getHost();
        Datacenter datacenterForVm = hostForVM.getDatacenter();
        sendNow(
            datacenterForVm.getId(),
            CloudSimTags.FAILURE_GENERATOR_DATACENTER_VM_FAILURE,
            selectedVmToFail
        );
        break;
      case CloudSimTags.FAILURE_GENERATOR_CLOUDLET_FAILURE:
        Cloudlet selectedCloudletToFail = this.getRandomRunningCloudlet();
        Vm vmForCloudlet = this.getVmList().get(selectedCloudletToFail.getVmId());
        Host hostForCloudlet = vmForCloudlet.getHost();
        Datacenter datacenterForCloudlet = hostForCloudlet.getDatacenter();
        sendNow(
            datacenterForCloudlet.getId(),
            CloudSimTags.FAILURE_GENERATOR_DATACENTER_CLOUDLET_FAILURE,
            selectedCloudletToFail
        );
        break;
    }
  }

  private Vm getRandomVm() {
    Cloudlet cloudlet = this.getRandomRunningCloudlet();
    List<Vm> vmList = this.getVmList();
    return vmList.get(cloudlet.getVmId());
  }

  private Host getRandomHost() {
    Vm selectedVm = this.getRandomVm();
    return selectedVm.getHost();
  }

  private Cloudlet getRandomRunningCloudlet() {
    List<Cloudlet> runningCloudlets = getRunningCloudlets();

    int runningCloudletsListSize = runningCloudlets.size();
    ContinuousDistribution distribution = new UniformDistr(0, runningCloudletsListSize);
    Double selectedCloudlet = distribution.sample();
    return runningCloudlets.get(selectedCloudlet.intValue());
  }

  private List<Cloudlet> getRunningCloudlets() {
    List<Cloudlet> cloudletList = this.getCloudletSubmittedList();
    List<Cloudlet> runningCloudlets = new ArrayList<>();
    for (Cloudlet cloudlet : cloudletList) {
      if (cloudlet.getStatus() == Cloudlet.INEXEC) {
        runningCloudlets.add(cloudlet);
      }
    }
    return runningCloudlets;
  }
}
