package org.cloudbus.cloudsim.failure;

import org.cloudbus.cloudsim.*;
import org.cloudbus.cloudsim.core.CloudSimTags;
import org.cloudbus.cloudsim.core.SimEvent;

import java.util.List;

/**
 * Created by ydoc on 11/28/2016.
 */
public class DatacenterWithFailure extends Datacenter {


  public DatacenterWithFailure(String name, DatacenterCharacteristics characteristics,
      VmAllocationPolicy vmAllocationPolicy, List<Storage> storageList, double schedulingInterval)
      throws Exception {
    super(name, characteristics, vmAllocationPolicy, storageList, schedulingInterval);
  }

  @Override
  protected void processOtherEvent(SimEvent event) {
    switch (event.getTag()) {
      case CloudSimTags.FAILURE_GENERATOR_DATACENTER_HOST_FAILURE:
        Host failedHost = (Host) event.getData();
        failHost(failedHost);
        break;
      case CloudSimTags.FAILURE_GENERATOR_DATACENTER_VM_FAILURE:
        Vm vmFailure = (Vm) event.getData();
        failCloudlets(vmFailure);
        sendNow(this.getId(), CloudSimTags.VM_DESTROY, vmFailure);
        break;
      case CloudSimTags.VM_DESTROY_FAIL_CLOUDLETS:
        Vm vm = (Vm) event.getData();
        failCloudlets(vm);
        break;
      case CloudSimTags.FAILURE_GENERATOR_DATACENTER_CLOUDLET_FAILURE:
        Cloudlet cloudlet = (Cloudlet) event.getData();
        Vm vm1 = getVmForCloudlet(cloudlet);
        CloudletScheduler cloudletScheduler = vm1.getCloudletScheduler();
        int cloudletId = cloudlet.getCloudletId();
        cloudletScheduler.cloudletCancel(cloudletId);
        break;
    }
  }

  private Vm getVmForCloudlet(Cloudlet cloudlet) {
    for (Vm vm : this.getVmList()) {
      if (vm.getId() == cloudlet.getVmId()) {
        return vm;
      }
    }
    return this.getVmList().get(0);
  }

  private void failCloudlets(Vm vm) {
    List<ResCloudlet> cloudletsInExecution = vm.getCloudletScheduler().getCloudletExecList();
    List<ResCloudlet> resumedCloudlets = vm.getCloudletScheduler().getCloudletPausedList();
    List<ResCloudlet> waitingCloudlets = vm.getCloudletScheduler().getCloudletWaitingList();
    cancelCloudlets(cloudletsInExecution);
    cancelCloudlets(resumedCloudlets);
    cancelCloudlets(waitingCloudlets);
  }

  private void cancelCloudlets(List<ResCloudlet> cloudletsList) {
    for (ResCloudlet cloudlet : cloudletsList) {
      cloudlet.setCloudletStatus(Cloudlet.FAILED);
    }
  }

  private void failHost(Host failedHost) {
    for (Vm vm : failedHost.getVmList()) {
      sendNow(this.getId(), CloudSimTags.VM_DESTROY_FAIL_CLOUDLETS, vm);
      sendNow(this.getId(), CloudSimTags.VM_DESTROY, vm);
    }
    failedHost.setFailed(true);
  }
}
