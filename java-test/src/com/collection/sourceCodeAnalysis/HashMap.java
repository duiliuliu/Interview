package java.util;

import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;

public class HashMap<K, V> extends AbstractMap<K, V> implements Map<K, V>, Cloneable, Serializable {

    private static final long serialVersionUID = 362498820763181265L;

    /**
     * 初始化容量为16,1左移四位即1*2^4 = 16
     */
    static final int DEFAULT_INITIAL_CAPACITY = 1 << 4; // aka 16

    /**
     * 最大容量，2^30
     */
    static final int MAXIMUM_CAPACITY = 1 << 30;

    /**
     * 负载因子
     */
    static final float DEFAULT_LOAD_FACTOR = 0.75f;

    /**
     * 由链表转换成树的阈值，当阈值为大于8时，使用红黑树树结构存储，链表结构依然保持
     */
    static final int TREEIFY_THRESHOLD = 8;

    /**
     * 由树转化为链表的阈值，当执行resize操作时，桶中的bin的数量小于6时，使用链表代替树结构
     */
    static final int UNTREEIFY_THRESHOLD = 6;

    /**
     * 当桶中的bin被树化时最小的hash表容量(如果没有达到，进行扩容操作)
     */
    static final int MIN_TREEIFY_CAPACITY = 64;

    /**
     * 节点，存储key，Value
     */
    static class Node<K, V> implements Map.Entry<K, V> {
        final int hash;
        final K key;
        V value;
        Node<K, V> next;

        Node(int hash, K key, V value, Node<K, V> next) {
            this.hash = hash;
            this.key = key;
            this.value = value;
            this.next = next;
        }

        public final K getKey() {
            return key;
        }

        public final V getValue() {
            return value;
        }

        public final String toString() {
            return key + "=" + value;
        }

        public final int hashCode() {
            return Objects.hashCode(key) ^ Objects.hashCode(value);
        }

        public final V setValue(V newValue) {
            V oldValue = value;
            value = newValue;
            return oldValue;
        }

        public final boolean equals(Object o) {
            if (o == this)
                return true;
            if (o instanceof Map.Entry) {
                Map.Entry<?, ?> e = (Map.Entry<?, ?>) o;
                if (Objects.equals(key, e.getKey()) && Objects.equals(value, e.getValue()))
                    return true;
            }
            return false;
        }
    }

    /* ---------------- Static utilities -------------- */

    /**
     * hash方法
     * key 的 hash值的计算是通过hashCode()的高16位异或低16位实现的：(h = k.hashCode()) ^ (h >>> 16)
     * 主要是从速度、功效、质量来考虑的，这么做可以在数组table的length比较小的时候
     * 也能保证考虑到高低Bit都参与到Hash的计算中，同时不会有太大的开销
     */
    static final int hash(Object key) {
        int h;
        return (key == null) ? 0 : (h = key.hashCode()) ^ (h >>> 16);
    }

    /**
     * Returns x's Class if it is of the form "class C implements Comparable<C>",
     * else null.
     */
    static Class<?> comparableClassFor(Object x) {
        if (x instanceof Comparable) {
            Class<?> c;
            Type[] ts, as;
            Type t;
            ParameterizedType p;
            if ((c = x.getClass()) == String.class) // bypass checks
                return c;
            if ((ts = c.getGenericInterfaces()) != null) {
                for (int i = 0; i < ts.length; ++i) {
                    if (((t = ts[i]) instanceof ParameterizedType)
                            && ((p = (ParameterizedType) t).getRawType() == Comparable.class)
                            && (as = p.getActualTypeArguments()) != null && as.length == 1 && as[0] == c) // type arg is
                                                                                                          // c
                        return c;
                }
            }
        }
        return null;
    }

    /**
     * Returns k.compareTo(x) if x matches kc (k's screened comparable class), else
     * 0.
     */
    @SuppressWarnings({ "rawtypes", "unchecked" }) // for cast to Comparable
    static int compareComparables(Class<?> kc, Object k, Object x) {
        return (x == null || x.getClass() != kc ? 0 : ((Comparable) k).compareTo(x));
    }

    /**
     * 检测参数是否为2的n次幂，且不能为负数，不能超过最大容量
     * 中间过程的目的是使n的二进制低位数全部变为1，比如：10,11变为11；100,101,110,111变为111. 然后+1即为2的n次幂
     * 返回的值是大于等于initialCapacity 的最小2的幂数值
     */
    static final int tableSizeFor(int cap) {
        int n = cap - 1;
        n |= n >>> 1; //现将n无符号右移1位，并将结果与右移前的n做按位或操作，结果赋给n；
        n |= n >>> 2;
        n |= n >>> 4;
        n |= n >>> 8;
        n |= n >>> 16;
        return (n < 0) ? 1 : (n >= MAXIMUM_CAPACITY) ? MAXIMUM_CAPACITY : n + 1;
    }

    /* ---------------- Fields -------------- */

    // 存储元素（位桶--Node<K,V>节点）的数组，总是2的幂次倍
    transient Node<K, V>[] table;
    // 由hashMap 中 Node<K,V>节点构成的 set集合
    transient Set<Map.Entry<K, V>> entrySet;
    // 存放元素（键-值对）的个数，注意这个不等于数组的长度
    transient int size;
    // 每次扩容和更改map结构的计数器，fail-fast机制
    transient int modCount;
    // 临界值 当实际大小size(容量*填充因子)超过临界值时，会进行扩容
    int threshold;
    // 记录 hashMap 装载因子
    final float loadFactor;

    /* ---------------- Public operations -------------- */

    /**
     * 构造一个HashMap 使用指定的初始容量和加载因子
     *
     * @param initialCapacity the initial capacity
     * @param loadFactor      the load factor
     * @throws IllegalArgumentException if the initial capacity is negative or the
     *                                  load factor is nonpositive
     */
    public HashMap(int initialCapacity, float loadFactor) {
        if (initialCapacity < 0)
            throw new IllegalArgumentException("Illegal initial capacity: " + initialCapacity);
        if (initialCapacity > MAXIMUM_CAPACITY)
            initialCapacity = MAXIMUM_CAPACITY;
        if (loadFactor <= 0 || Float.isNaN(loadFactor))
            throw new IllegalArgumentException("Illegal load factor: " + loadFactor);
        this.loadFactor = loadFactor;
        this.threshold = tableSizeFor(initialCapacity);
    }

    /**
     *  默认加载因子构造
     */
    public HashMap(int initialCapacity) {
        this(initialCapacity, DEFAULT_LOAD_FACTOR);
    }

    /**
     * 默认容量、默认加载因子构造
     */
    public HashMap() {
        this.loadFactor = DEFAULT_LOAD_FACTOR; // all other fields defaulted
    }

    /**
     * 传入一个map构造
     *
     * @throws NullPointerException if the specified map is null
     */
    public HashMap(Map<? extends K, ? extends V> m) {
        this.loadFactor = DEFAULT_LOAD_FACTOR;
        putMapEntries(m, false);
    }

    /**
     * Implements Map.putAll and Map constructor
     * 将m中的所有值存入本hashmap实例中
     */
    final void putMapEntries(Map<? extends K, ? extends V> m, boolean evict) {
        int s = m.size();
        if (s > 0) {
            // 判断table是否已经初始化
            if (table == null) { // pre-size
                // 根据待插入的map的size计算打算创建的hashmap容量
                float ft = ((float) s / loadFactor) + 1.0F;
                int t = ((ft < (float) MAXIMUM_CAPACITY) ? (int) ft : MAXIMUM_CAPACITY);
                // threshold打算创建的hashmap存储容量
                if (t > threshold)
                    threshold = tableSizeFor(t);
            } else if (s > threshold)   // 判断待插入的map大小是否大于threshold，若大于则先进行扩容
                resize();
            for (Map.Entry<? extends K, ? extends V> e : m.entrySet()) {
                K key = e.getKey();
                V value = e.getValue();
                // 实际也是调用　putVal　函数进行元素的插入
                putVal(hash(key), key, value, false, evict);
            }
        }
    }

    /** 
     * @return the number of key-value mappings in this map
     */
    public int size() {
        return size;
    }

    /** 
     * @return <tt>true</tt> if this map contains no key-value mappings
     */
    public boolean isEmpty() {
        return size == 0;
    }

    /**
     *  get操作，hash计算key的hash值，然后通过hash值获取节点
     */
    public V get(Object key) {
        Node<K, V> e;
        return (e = getNode(hash(key), key)) == null ? null : e.value;
    }

    /**
     * Implements Map.get and related methods
     * 
     */
    final Node<K, V> getNode(int hash, Object key) {
        Node<K, V>[] tab; // Node对象数组
        Node<K, V> first, e; // 通过hash找到的第一个节点，可能链接了好长一串
        int n;
        K k;
        // table 为node数组，此时将table赋值给局部变量tab
        // (n - 1) & hash 使用按位与运算代替取模运算
        // tab 不为null 且 长度大于0 且 通过hash找到的节点node不为null
        if ((tab = table) != null && (n = tab.length) > 0 && (first = tab[(n - 1) & hash]) != null) {
            // 与first相比较，比较hash值与key值 ， key两种情况比较 == 或 equals
            if (first.hash == hash && // always check first node
                    ((k = first.key) == key || (key != null && key.equals(k))))
                return first;
            // 判断是否链接其他节点
            if ((e = first.next) != null) {
                // 判断是否为树，如果为树，则使用getTreeNode方法查找，否则遍历链表进行查找
                if (first instanceof TreeNode)
                    return ((TreeNode<K, V>) first).getTreeNode(hash, key);
                do {
                    if (e.hash == hash && ((k = e.key) == key || (key != null && key.equals(k))))
                        return e;
                } while ((e = e.next) != null);
            }
        }
        return null;
    }

    /**
     * 判断是否包含key，使用getNode查找
     */
    public boolean containsKey(Object key) {
        return getNode(hash(key), key) != null;
    }

    /**
     * put(key,value),实质上条用putVal
     */
    public V put(K key, V value) {
        return putVal(hash(key), key, value, false, true);
    }

    /**
     * Implements Map.put and related methods 
     * onlyIfAbsent在true的情况下不改变已有value值,evict(驱逐)在false的情况下table为创作模式.
     */
    final V putVal(int hash, K key, V value, boolean onlyIfAbsent, boolean evict) {
        Node<K, V>[] tab;
        Node<K, V> p;
        int n, i;
        // 如果tab为null 或 tab。length==0 进行扩容resize
        if ((tab = table) == null || (n = tab.length) == 0)
            n = (tab = resize()).length;
        // 通过 (n - 1) & hash 计算下标，当前下标无节点，则直接创建一个节点
        if ((p = tab[i = (n - 1) & hash]) == null)
            tab[i] = newNode(hash, key, value, null);
        else {
            Node<K, V> e;
            K k;
            // 当前传入节点的key与链表第一个节点的key一致时，替换value，返回旧value
            if (p.hash == hash && ((k = p.key) == key || (key != null && key.equals(k))))
                e = p;
            // 如果链表为数节点子类，则运用putTreeVal方法添加节点
            else if (p instanceof TreeNode)
                e = ((TreeNode<K, V>) p).putTreeVal(this, tab, hash, key, value);
            // 尾结点插入
            else {
                for (int binCount = 0;; ++binCount) {
                    if ((e = p.next) == null) {
                        p.next = newNode(hash, key, value, null);
                        if (binCount >= TREEIFY_THRESHOLD - 1) // -1 for 1st，转化树结构
                            treeifyBin(tab, hash);
                        break;
                    }
                    if (e.hash == hash && ((k = e.key) == key || (key != null && key.equals(k))))
                        break;
                    p = e;
                }
            }
            // key存在的话，替换掉value，返回旧value
            if (e != null) { // existing mapping for key
                V oldValue = e.value;
                if (!onlyIfAbsent || oldValue == null)
                    e.value = value;
                afterNodeAccess(e);
                return oldValue;
            }
        }
        // 结构性修改
        ++modCount;
        // 键值对数目超过阈值时，进行 resize 扩容
        if (++size > threshold)
            resize();
        // 插入后回调
        afterNodeInsertion(evict);  // 空函数，由用户根据需要覆盖
        return null;
    }

    final Node<K,V>[] resize() {
        Node<K,V>[] oldTab = table;
        int oldCap = (oldTab == null) ? 0 : oldTab.length;
        int oldThr = threshold;
        int newCap, newThr = 0;
        if (oldCap > 0) {
            if (oldCap >= MAXIMUM_CAPACITY) {
                threshold = Integer.MAX_VALUE;
                return oldTab;
            }
            else if ((newCap = oldCap << 1) < MAXIMUM_CAPACITY &&
                     oldCap >= DEFAULT_INITIAL_CAPACITY)
                newThr = oldThr << 1; // double threshold
        }
        else if (oldThr > 0) // initial capacity was placed in threshold
            newCap = oldThr;
        else {               // zero initial threshold signifies using defaults
            newCap = DEFAULT_INITIAL_CAPACITY;
            newThr = (int)(DEFAULT_LOAD_FACTOR * DEFAULT_INITIAL_CAPACITY);
        }
        if (newThr == 0) {
            float ft = (float)newCap * loadFactor;
            newThr = (newCap < MAXIMUM_CAPACITY && ft < (float)MAXIMUM_CAPACITY ?
                      (int)ft : Integer.MAX_VALUE);
        }
        threshold = newThr;
        @SuppressWarnings({"rawtypes","unchecked"})
            Node<K,V>[] newTab = (Node<K,V>[])new Node[newCap];
        table = newTab;
        if (oldTab != null) {
            for (int j = 0; j < oldCap; ++j) {
                Node<K,V> e;
                if ((e = oldTab[j]) != null) {
                    oldTab[j] = null;
                    if (e.next == null)
                        newTab[e.hash & (newCap - 1)] = e;
                    else if (e instanceof TreeNode)
                        ((TreeNode<K,V>)e).split(this, newTab, j, oldCap);
                    else { // preserve order
                        Node<K,V> loHead = null, loTail = null;
                        Node<K,V> hiHead = null, hiTail = null;
                        Node<K,V> next;
                        do {
                            next = e.next;
                            if ((e.hash & oldCap) == 0) {
                                if (loTail == null)
                                    loHead = e;
                                else
                                    loTail.next = e;
                                loTail = e;
                            }
                            else {
                                if (hiTail == null)
                                    hiHead = e;
                                else
                                    hiTail.next = e;
                                hiTail = e;
                            }
                        } while ((e = next) != null);
                        if (loTail != null) {
                            loTail.next = null;
                            newTab[j] = loHead;
                        }
                        if (hiTail != null) {
                            hiTail.next = null;
                            newTab[j + oldCap] = hiHead;
                        }
                    }
                }
            }
        }
        return newTab;
    }


}
