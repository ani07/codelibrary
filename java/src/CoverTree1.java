import java.util.*;

public class CoverTree1 {

	static double dist(Node p1, Node p2) {
		double dx = p1.x - p2.x;
		double dy = p1.y - p2.y;
		return Math.sqrt(dx * dx + dy * dy);
	}

	static class Node {
		double x, y;
		Map<Integer, List<Node>> children = new HashMap<Integer, List<Node>>();
		int maxChildLevel = 0;

		public void addChild(Node node, int level) {
			if (!children.containsKey(level)) {
				children.put(level, new ArrayList<Node>());
			}
			children.get(level).add(node);
			maxChildLevel = Math.max(maxChildLevel, level);
		}

		public List<Node> getChildren(int level) {
			if (!children.containsKey(level))
				return new ArrayList<Node>();
			return children.get(level);
		}

		public Node(double x, double y) {
			this.x = x;
			this.y = y;
		}
	}

	static final int levels = 64;
	double[] layerRadius = new double[levels];

	public CoverTree1() {
		layerRadius[0] = 1 << 30;
		for (int i = 1; i < layerRadius.length; i++)
			layerRadius[i] = layerRadius[i - 1] / 2;
	}

	Node root;

	public void insert(Node p) {
		if (root == null)
			root = p;
		else
			insert(p, Arrays.asList(new Node[] { root }), 0);
	}

	boolean insert(Node p, List<Node> Qi, int level) {
		Node parent = null;
		List<Node> nQi = new ArrayList<Node>();
		for (Node q : Qi) {
			if (dist(p, q) <= layerRadius[level]) {
				nQi.add(q);
				parent = q;
			}
		}

		if (parent == null) // separation holds
			return true;

		for (Node q : Qi)
			for (Node ch : q.getChildren(level))
				if (dist(p, ch) <= layerRadius[level])
					nQi.add(ch);

		if (insert(p, nQi, level + 1))
			parent.addChild(p, level);

		return false;
	}

	double bestDist;
	Node bestNode;

	public Node findNearest(Node p) {
		bestDist = p != root ? dist(p, root) : Double.POSITIVE_INFINITY;
		bestNode = p != root ? root : null;
		findNearest(p, Arrays.asList(new Node[] { root }), 0);
		return bestNode;
	}

	void findNearest(Node p, List<Node> Qi, int level) {
		for (; !Qi.isEmpty(); level++) {
			List<Node> Q = new ArrayList<Node>();
			for (Node q : Qi) {
				Q.add(q);
				for (Node ch : q.getChildren(level)) {
					if (ch != p) {
						double dist = dist(p, ch);
						if (bestDist > dist) {
							bestDist = dist;
							bestNode = ch;
						}
					}
					Q.add(ch);
				}
			}

			Qi = new ArrayList<Node>();
			for (Node q : Q)
				if (q.maxChildLevel > level && dist(p, q) <= bestDist + layerRadius[level])
					Qi.add(q);
		}
	}

	// Usage example
	public static void main(String[] args) {
		CoverTree1 tree = new CoverTree1();
		tree.insert(new Node(1, 1));
		tree.insert(new Node(2, 2));
		Node p = tree.findNearest(new Node(1.6, 1.6));
		System.out.println(2 == p.x && 2 == p.y);
	}
}