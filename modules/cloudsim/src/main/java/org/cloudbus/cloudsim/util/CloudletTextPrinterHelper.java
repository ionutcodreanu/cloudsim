package org.cloudbus.cloudsim.util;

import java.text.DecimalFormat;
import java.util.List;
import org.cloudbus.cloudsim.Cloudlet;
import org.cloudbus.cloudsim.Log;

/**
 * @author Ionut Codreanu <ionutcodreanu@outlook.com>
 */
public class CloudletTextPrinterHelper {

  private List<Cloudlet> cloudletList;
  private String title;
  private String indent = "    ";

  /**
   *
   * @param cloudletList
   * @param title
   */
  public CloudletTextPrinterHelper(List<Cloudlet> cloudletList, String title) {

    this.cloudletList = cloudletList;
    this.title = title;
  }

  public String getTable() {
    return this.printHeader() + this.printRows();
  }

  private String printHeader() {
    String result = "";
    result = System.lineSeparator();
    result += "========== OUTPUT: " + this.title + " ==========" + System.lineSeparator();
    result += "Cloudlet ID" + indent + "STATUS" + indent +
        "Data center ID" + indent + "VM ID" + indent + indent + "Time" + indent + "Start Time"
        + indent + "Finish Time" + System.lineSeparator();
    return result;

  }

  private String printRows() {
    String result = "";
    DecimalFormat dft = new DecimalFormat("###.##");
    for (Cloudlet cloudlet : this.cloudletList) {
      result += indent + cloudlet.getCloudletId() + indent + indent;

      String cloudletStatus = Cloudlet.getStatusString(cloudlet.getStatus());
      result += cloudletStatus;
      int resourceId = cloudlet.getResourceId();
      int vmId = cloudlet.getVmId();
      String execCPUTime = "";
      String execStartTime = "";
      String execFinishTime = "";
      if (cloudlet.getStatus() == Cloudlet.SUCCESS) {
        execCPUTime = dft.format(cloudlet.getActualCPUTime());
        execStartTime = dft.format(cloudlet.getExecStartTime());
        execFinishTime = dft.format(cloudlet.getFinishTime());
      } else {
        execCPUTime = "N/A";
        execStartTime = "N/A";
        execFinishTime = "N/A";
      }
      result +=
          indent + indent + resourceId + indent + indent + indent + vmId +
              indent + indent + indent + execCPUTime +
              indent + indent + execStartTime + indent + indent + indent
              + execFinishTime;

      result += System.lineSeparator();
    }

    return result;
  }
}
