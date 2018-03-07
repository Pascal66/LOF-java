import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;  
  
/**
 * Outlier Analysis
 *
 * @author Wilson
 *         Algorithm: Density Based Local Outlier Detection (lof Algorithm)
 *         Input: Sample Set D, Positive Integer K (Used to Calculate Kth Distance)
 *         Output: Local outliers of sample points
 *         Process:
 *         1) Calculate the Euclidean distance between each object and other objects
 *         2) Sort the Euclidean distances and calculate the kth distance and Kth field
 *         3) Calculate the reachable density of each object
 *         4) Calculate the local outlier factor of each object
 *         5) Sort the local outliers of each point and output it.
 **/
public class OutlierNodeDetect {

    private int INT_K = 4;//Positive integer K

    public void setK(int int_k) {
        this.INT_K = int_k;
    }

    // 1. Find the Euclidean distance between the given point and the other points
    // 2. Sort the Euclidean distance, find the top 5 points, and note the k distance
    // 3. Calculate the reachable density of each point
    // 4. Calculate the local outlier points for each point
    // 5. The local outlier points for each point are sorted and output.
    private List<DataNode> getOutlierNode(List<DataNode> allNodes) {

        List<DataNode> kdAndKnList = getKDAndKN(allNodes);
        calReachDis(kdAndKnList);
        calReachDensity(kdAndKnList);
        calLof(kdAndKnList);
        //Descending order
        Collections.sort(kdAndKnList, new LofComparator());

        return kdAndKnList;
    }

    /**
     * Calculate the local outlier point for each point
     *
     * @param kdAndKnList
     */
    private void calLof(List<DataNode> kdAndKnList) {
        for (DataNode node : kdAndKnList) {
            List<DataNode> tempNodes = node.getkNeighbor();
            double sum = 0.0;
            for (DataNode tempNode : tempNodes) {
                double rd = getRD(tempNode.getNodeName(), kdAndKnList);
                sum = rd / node.getReachDensity() + sum;
            }
            sum = sum / (double) INT_K;
            node.setLof(sum);
        }
    }

    /**
     * Calculate the reachable distance for each point
     *
     * @param kdAndKnList
     */
    private void calReachDensity(List<DataNode> kdAndKnList) {
        for (DataNode node : kdAndKnList) {
            List<DataNode> tempNodes = node.getkNeighbor();
            double sum = 0.0;
            double rd = 0.0;
            for (DataNode tempNode : tempNodes) {
                sum = tempNode.getReachDis() + sum;
            }
            rd = (double) INT_K / sum;
            node.setReachDensity(rd);
        }
    }

    /**
     * Calculate the reachable density of each point,reachdis(p,o)=max{ k-distance(o),d(p,o)}
     *
     * @param kdAndKnList
     */
    private void calReachDis(List<DataNode> kdAndKnList) {
        for (DataNode node : kdAndKnList) {
            List<DataNode> tempNodes = node.getkNeighbor();
            for (DataNode tempNode : tempNodes) {
                //获取tempNode点的k-距离
                double kDis = getKDis(tempNode.getNodeName(), kdAndKnList);
                //reachdis(p,o)=max{ k-distance(o),d(p,o)}
                if (kDis < tempNode.getDistance()) {
                    tempNode.setReachDis(tempNode.getDistance());
                } else {
                    tempNode.setReachDis(kDis);
                }
            }
        }
    }

    /**
     * Get the k-distance of a point（kDistance）
     *
     * @param nodeName
     * @param nodeList
     * @return
     */
    private double getKDis(String nodeName, List<DataNode> nodeList) {
        double kDis = 0;
        for (DataNode node : nodeList) {
            if (nodeName.trim().equals(node.getNodeName().trim())) {
                kDis = node.getkDistance();
                break;
            }
        }
        return kDis;
    }

    /**
     * Get a point of reachable distance
     *
     * @param nodeName
     * @param nodeList
     * @return
     */
    private double getRD(String nodeName, List<DataNode> nodeList) {
        double kDis = 0;
        for (DataNode node : nodeList) {
            if (nodeName.trim().equals(node.getNodeName().trim())) {
                kDis = node.getReachDensity();
                break;
            }
        }
        return kDis;
    }

    /**
     * Calculate the Euclidean distance between the NodeA and other NodeBs at a given point and find the first 5 NodeBs of the NodeA point and record the kNeighbor variable to NodeA.
     * Find the k distance of NodeA at the same time and record it in the k-distance (kDistance) variable of NodeA.
     * Processing steps are as follows:
     * 1 Calculate Euclidean distance between NodeA and NodeB at a given point, and record it in the distance variable of NodeB.
     * 2 the distances in all NodeB points are sorted in ascending order.
     * 3 Find the Euclidean distance from the first 5 digits of NodeB and record it in NodeA's kNeighbor variable.
     * 4 Find the 5th distance of the NodeB point and record it in the kDistance variable of the NodeA point.
     *
     * @param allNodes
     * @return List<Node>
     */
    private List<DataNode> getKDAndKN(List<DataNode> allNodes) {
        List<DataNode> kdAndKnList = new ArrayList<DataNode>();
        for (int i = 0; i < allNodes.size(); i++) {
            List<DataNode> tempNodeList = new ArrayList<DataNode>();
            DataNode nodeA = new DataNode(allNodes.get(i).getNodeName(), allNodes
                    .get(i).getDimensioin());
            //Find the Euclidean distance between a given point NodeA and other points NodeB
            //and record it in the distance variable at NodeB.
            for (int j = 0; j < allNodes.size(); j++) {
                DataNode nodeB = new DataNode(allNodes.get(j).getNodeName(), allNodes
                        .get(j).getDimensioin());
                //Calculate Euclidean distance between NodeA and NodeB
                double tempDis = getDis(nodeA, nodeB);
                nodeB.setDistance(tempDis);
                tempNodeList.add(nodeB);
            }

            //2, The Euclidean distances in all NodeB points are sorted in ascending order.
            Collections.sort(tempNodeList, new DistComparator());
            for (int k = 1; k < INT_K; k++) {
                //3, Find the first five Euclidean distance points of the NodeB point and record it in the kNeighbor NodeA variable.
                nodeA.getkNeighbor().add(tempNodeList.get(k));
                if (k == INT_K - 1) {
                    //4, Find the 5th distance of the NodeB point and record it in the kDistance variable of the NodeA point.
                    nodeA.setkDistance(tempNodeList.get(k).getDistance());
                }
            }
            kdAndKnList.add(nodeA);
        }
        return kdAndKnList;
    }

    /**
     * Calculate Euclidean distance between a given point A and other points B.
     *       * Euclidean distance formula:
     * d=sqrt( ∑(xi1-xi2)^2 ) Here i = 1,2..n
     * xi1 represents the i-th coordinate of the first point, xi2 represents the i-th coordinate of the second point
     * n-dimensional Euclidean space is a set of points, each of its points can be expressed as(x(1),x(2),...x(n)),
     * Where x (i) (i = 1, 2 ... n) is a real number called the ith coordinate of x, two points x and y = (y (1), y n)) is defined as the above formula.
     *
     * @param A
     * @param B
     * @return
     */
    private double getDis(DataNode A, DataNode B) {
        double dis = 0.0;
        double[] dimA = A.getDimensioin();
        double[] dimB = B.getDimensioin();
        if (dimA.length == dimB.length) {
            for (int i = 0; i < dimA.length; i++) {
                double temp = Math.pow(dimA[i] - dimB[i], 2);
                dis = dis + temp;
            }
            dis = Math.pow(dis, 0.5);
        }
        return dis;
    }

    /**
     * Sort in ascending order
     */
    class DistComparator implements Comparator<DataNode> {
        public int compare(DataNode A, DataNode B) {
            //return A.getDistance() - B.getDistance() < 0 ? -1 : 1;
            if ((A.getDistance() - B.getDistance()) < 0)
                return -1;
            else if ((A.getDistance() - B.getDistance()) > 0)
                return 1;
            else return 0;
        }
    }

    /**
     * Descending order
     */
    class LofComparator implements Comparator<DataNode> {
        public int compare(DataNode A, DataNode B) {
            //return A.getLof() - B.getLof() < 0 ? 1 : -1;
            if ((A.getLof() - B.getLof()) < 0)
                return 1;
            else if ((A.getLof() - B.getLof()) > 0)
                return -1;
            else return 0;
        }
    }

    public static void main(String[] args) throws IOException {

       DecimalFormat df = new DecimalFormat("#.####");

        // Read all the files in the directory  bag
        File root = new File("C:/Users/uatrm990204/Desktop/LOF/javatest");
        File[] files = root.listFiles();

        //Read a file
        for (int i = 0; i < files.length; i++) {
            // print the name of each file, for the purpose that we can recognize the sample size
            // System.out.println(files[i]);

            BufferedReader br = new BufferedReader(new FileReader(files[i]));
            String str = br.readLine();
            // build a DataNode set
            ArrayList<DataNode> dpoints = new ArrayList<>();
            // walk through each line
            int r = 1;
            while ((str = br.readLine()) != null) {
//        		System.out.println(str);
                String[] strs = str.split(",");
                double[] eachRow = new double[strs.length];
                for (int j = 0; j < strs.length; j++) {

                    eachRow[j] = Double.parseDouble(strs[j]);
                }

                dpoints.add(new DataNode(String.valueOf(r), eachRow));
//    		    System.out.println(eachRow[j]);
                r++;
            }
//        	System.out.println(dpoints.size());

            Date datestart = new Date();
            List<DataNode> nodeList = null;
            OutlierNodeDetect lof = new OutlierNodeDetect();
            // bagging for k
            int[] k = {5, 6, 7, 9, 12, 15, 20, 25};
            for (int l = 0; l < k.length; l++) {
                Date datestart1 = new Date();
                lof.setK(k[l]);
                nodeList = lof.getOutlierNode(dpoints);
                Date dateend1 = new Date();
                long gap1 = dateend1.getTime() - datestart1.getTime();
                System.out.println(gap1);
                //  System.out.println("int_k" + k[l] +":" + gap1);
                //  System.out.println("The K of this loop:" +k[l] +"\n");

                //  for (DataNode node : nodeList) {
                //   System.out.println(node.getNodeName() + "  " + df.format(node.getLof()));
                // }
            }
            //Timing ends
            Date dateend = new Date();
            long gap = dateend.getTime() - datestart.getTime();
            // System.out.println(datestart.getTime());
            // System.out.println(dateend.getTime());
            System.out.println("total:" + gap);
            // print results
            for (DataNode node : nodeList) {
                System.out.println(node.getNodeName() + "  " + df.format(node.getLof()));
            }
        }
    }
}  
 
