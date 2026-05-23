public class HuffmanNode implements Comparable<HuffmanNode> {
    public final int value;
    public final long frequency;
    public final HuffmanNode left;
    public final HuffmanNode right;
    private final int minValue;

    public HuffmanNode(int value, long frequency) {
        this.value = value;
        this.frequency = frequency;
        this.left = null;
        this.right = null;
        this.minValue = value;
    }

    public HuffmanNode(HuffmanNode left, HuffmanNode right) {
        this.value = -1;
        this.frequency = left.frequency + right.frequency;
        this.left = left;
        this.right = right;
        this.minValue = Math.min(left.minValue, right.minValue);
    }

    public boolean isLeaf() {
        return left == null && right == null;
    }

    @Override
    public int compareTo(HuffmanNode other) {
        int byFrequency = Long.compare(this.frequency, other.frequency);
        if (byFrequency != 0) {
            return byFrequency;
        }
        return Integer.compare(this.minValue, other.minValue);
    }
}
