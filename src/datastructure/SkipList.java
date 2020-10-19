package datastructure;

/**
 * @author ViktorXH
 * @date 10-09-2020
 */
public class SkipList {
    private static final int MAX_LEVEL = 32;
    private static final double P = 0.25;

    SkipListNode head, tail;
    int length;
    int level;

    static class SkipListNode {
        String val;
        double score;
        SkipListNode backward;
        SkipListLevel[] levels;

        SkipListNode() {
        }

        SkipListNode(String val, double score) {
            this();
            this.val = val;
            this.score = score;
        }

        void initLevels(int level) {
            levels = new SkipListLevel[level];
            for (int i = 0; i < level; i++) {
                levels[i] = new SkipListLevel();
            }
        }
    }

    static class SkipListLevel {
        int span;
        SkipListNode forward;
    }

    public SkipList() {
        level = 1;
        length = 0;
        head = new SkipListNode();
        head.initLevels(MAX_LEVEL);
    }

    public void add(String val, double score) {
        int[] rank = new int[MAX_LEVEL];
        SkipListNode[] update = new SkipListNode[MAX_LEVEL];
        SkipListNode x = head;

        for (int i = level - 1; i >= 0; i--) {
            rank[i] = i == level - 1 ? 0 : rank[i + 1];
            while (x.levels[i].forward != null
                    && (x.levels[i].forward.score < score
                    || (x.levels[i].forward.score == score && val.compareTo(x.levels[i].forward.val) > 0))) {

                rank[i] += x.levels[i].span;
                x = x.levels[i].forward;
            }

            update[i] = x;
        }

        int curLevel = randomLevel();

        x = new SkipListNode(val, score);
        x.initLevels(curLevel);

        if (curLevel > level) {
            for (int i = level; i < curLevel; i++) {
                rank[i] = 0;
                update[i] = head;
                update[i].levels[i].span = length;
            }
            level = curLevel;
        }

        for (int i = 0; i < curLevel; i++) {
            x.levels[i].forward = update[i].levels[i].forward;
            update[i].levels[i].forward = x;

            x.levels[i].span = update[i].levels[i].span - (rank[0] - rank[i]);
            update[i].levels[i].span = (rank[0] - rank[i]) + 1;
        }

        for (int i = curLevel; i < level; i++) {
            update[i].levels[i].span++;
        }

        x.backward = update[0] == head ? null : update[0];
        if (x.levels[0].forward != null) {
            x.levels[0].forward.backward = x;
        } else {
            tail = x;
        }

        length++;
    }

    public boolean remove(String val, double score) {
        SkipListNode[] update = new SkipListNode[MAX_LEVEL];
        SkipListNode x = head;

        for (int i = level - 1; i >= 0; i--) {
            while (x.levels[i].forward != null
                    && (x.levels[i].forward.score < score
                    || (x.levels[i].forward.score == score && val.compareTo(x.levels[i].forward.val) > 0))) {

                x = x.levels[i].forward;
            }

            update[i] = x;
        }

        x = x.levels[0].forward;

        if (x != null && x.score == score && val.compareTo(x.val) == 0) {
            deleteNode(x, update);
            return true;
        }

        return false;
    }

    public int getRank(String val, double score) {
        int rank = 0;
        SkipListNode x = head;

        for (int i = level - 1; i >= 0; i--) {
            while (x.levels[i].forward != null
                    && (x.levels[i].forward.score < score
                    || (x.levels[i].forward.score == score && val.compareTo(x.levels[i].forward.val) >= 0))) {

                rank += x.levels[i].span;
                x = x.levels[i].forward;
            }

            if (x.val != null && x.val.equals(val)) {
                return rank;
            }
        }
        return 0;
    }

    public boolean contains(String val, double score) {
        SkipListNode x = head;

        for (int i = level - 1; i >= 0; i--) {
            while (x.levels[i].forward != null
                    && (x.levels[i].forward.score < score
                    || (x.levels[i].forward.score == score && val.compareTo(x.levels[i].forward.val) > 0))) {

                x = x.levels[i].forward;
            }
        }

        x = x.levels[0].forward;

        if (x != null && x.score == score && val.compareTo(x.val) == 0) {
            return true;
        }

        return false;
    }

    private void deleteNode(SkipListNode x, SkipListNode[] update) {
        for (int i = 0; i < level; i++) {
            if (update[i].levels[i].forward == x) {
                update[i].levels[i].span += x.levels[i].span - 1;
                update[i].levels[i].forward = x.levels[i].forward;
            } else {
                update[i].levels[i].span--;
            }
        }

        if (x.levels[0].forward != null) {
            x.levels[0].forward.backward = x.backward;
        } else {
            tail = x.backward;
        }

        while (level > 1 && head.levels[level - 1].forward == null) {
            head.levels[level - 1].span = 0;
            level--;
        }

        length--;
    }

    private int randomLevel() {
        int level = 1;
        while (Math.random() < P && level < MAX_LEVEL) {
            level++;
        }
        return level;
    }
}
