import java.util.ArrayList;  
import java.util.List;  
  
/**
     * @author wilsonact
     */
    public static class DataNode {
        private String nodeName; // Sample name
        private double[] dimensioin; // The dimension of the sample point
        private double kDistance; // k-distance
        private List<DataNode> kNeighbor = new ArrayList<>();// k-field
        private double distance; // Euclidean distance to a given point
        private double reachDensity;// Reachable density
        private double reachDis;// Reach the distance

        private double lof;// Local outlier

        public DataNode() { }

        DataNode(String nodeName, double[] dimensioin) {
            this.nodeName = nodeName;
            this.dimensioin = dimensioin;
        }

        String getNodeName() {
            return nodeName;
        }

        public void setNodeName(String nodeName) {
            this.nodeName = nodeName;
        }

        double[] getDimensioin() {
            return dimensioin;
        }

        public void setDimensioin(double[] dimensioin) {
            this.dimensioin = dimensioin;
        }

        double getkDistance() {
            return kDistance;
        }

        void setkDistance(double kDistance) {
            this.kDistance = kDistance;
        }

        List<DataNode> getkNeighbor() {
            return kNeighbor;
        }

        public void setkNeighbor(List<DataNode> kNeighbor) {
            this.kNeighbor = kNeighbor;
        }

        double getDistance() {
            return distance;
        }

        void setDistance(double distance) {
            this.distance = distance;
        }

        double getReachDensity() {
            return reachDensity;
        }

        void setReachDensity(double reachDensity) {
            this.reachDensity = reachDensity;
        }

        double getReachDis() {
            return reachDis;
        }

        void setReachDis(double reachDis) {
            this.reachDis = reachDis;
        }

        double getLof() {
            return lof;
        }

        void setLof(double lof) {
            this.lof = lof;
        }
    }

