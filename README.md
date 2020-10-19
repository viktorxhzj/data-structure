# Implementations of Advanced Data Structures

## 1 SkipList
SkipList由William Pugh在他的论文《Skip Lists: A Probabilistic Alternative to Balanced Trees》中提出，是一种可以用来代替平衡树的数据结构。它采取了概率平衡而不是严格强制的平衡，因此插入和删除的算法比平衡树的等效算法简单得多，速度也快得多。

### 1.1 空间开销
SkipList的底层是单项链表，区别在于每个链表节点有多层指向下个节点的指针，而链表的随机层数算法由常数p决定，因此，SkipList的额外空间开销与p相关。

根据随即层数算法，得出每个节点的平均层数为1/(1-p)，则SkipList的总体空间开销即为n/(1-p)。

### 1.2 时间复杂度
SkipList的查找时间复杂度为O(log(n))。

### 1.3 Java实现

```java
interface SkipList {
    void add(String val, double score);             // 添加元素
    boolean remove(String val, double score);       // 删除元素
    boolean contains(String val, double score);     // 判断是否存在元素
    int getRank(String val, double score);          // 获取排名
}
```