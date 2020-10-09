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
        SkipListNode prev;
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
        SkipListNode next;
    }

    public SkipList() {
        level = 1;
        length = 0;
        head = new SkipListNode();
        head.initLevels(MAX_LEVEL);
    }

    private int randomLevel() {
        int level = 1;
        while (Math.random() < P && level < MAX_LEVEL) {
            level++;
        }
        return level;
    }

    public SkipListNode add(String val, double score) {
        int[] rank = new int[MAX_LEVEL];
        SkipListNode[] update = new SkipListNode[MAX_LEVEL];
        SkipListNode x = head;

        for (int i = level - 1; i >= 0; i--) {
            rank[i] = i == level - 1 ? 0 : rank[i + 1];
            while (x.levels[i].next != null
                    && (x.levels[i].next.score < score
                    || (x.levels[i].next.score == score && val.compareTo(x.levels[i].next.val) > 0))) {

                rank[i] += x.levels[i].span;
                x = x.levels[i].next;
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
            x.levels[i].next = update[i].levels[i].next;
            update[i].levels[i].next = x;

            x.levels[i].span = update[i].levels[i].span - (rank[0] - rank[i]);
            update[i].levels[i].span = (rank[0] - rank[i]) + 1;
        }

        for (int i = curLevel; i < level; i++) {
            update[i].levels[i].span++;
        }

        x.prev = update[0] == head ? null : update[0];
        if (x.levels[0].next != null) {
            x.levels[0].next.prev = x;
        } else {
            tail = x;
        }

        length++;

        return x;
    }

    public boolean remove(String val, double score) {
        SkipListNode[] update = new SkipListNode[MAX_LEVEL];
        SkipListNode x = head;

        for (int i = level - 1; i >= 0; i--) {
            while (x.levels[i].next != null
                    && (x.levels[i].next.score < score
                    || (x.levels[i].next.score == score && val.compareTo(x.levels[i].next.val) > 0))) {

                x = x.levels[i].next;
            }

            update[i] = x;
        }

        x = x.levels[0].next;

        if (x != null && x.score == score && val.compareTo(x.val) == 0) {
            deleteNode(x, update);
            return true;
        }

        return false;
    }

    private void deleteNode(SkipListNode x, SkipListNode[] update) {
        for (int i = 0; i < level; i++) {
            if (update[i].levels[i].next == x) {
                update[i].levels[i].span += x.levels[i].span - 1;
                update[i].levels[i].next = x.levels[i].next;
            } else {
                update[i].levels[i].span--;
            }
        }

        if (x.levels[0].next != null) {
            x.levels[0].next.prev = x.prev;
        } else {
            tail = x.prev;
        }

        while (level > 1 && head.levels[level - 1].next == null) {
            head.levels[level - 1].span = 0;
            level--;
        }

        length--;
    }

}
