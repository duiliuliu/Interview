
package java.util.concurrent.locks;

import java.util.concurrent.TimeUnit;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import sun.misc.Unsafe;

/**
 * Acquire: while (!tryAcquire(arg)) { <em>enqueue thread if it is not already
 * queued</em>; <em>possibly block current thread</em>; }
 *
 * Release: if (tryRelease(arg)) <em>unblock the first queued thread</em>;
 * </pre>
 *
 * (Shared mode is similar but may involve cascading signals.)
 *
 * 
 * <h3>Usage Examples</h3>
 *
 * <p>
 * Here is a non-reentrant mutual exclusion lock class that uses the value zero
 * to represent the unlocked state, and one to represent the locked state. While
 * a non-reentrant lock does not strictly require recording of the current owner
 * thread, this class does so anyway to make usage easier to monitor. It also
 * supports conditions and exposes one of the instrumentation methods:
 *
 * <pre>
 * {
 *     &#64;code
 *     class Mutex implements Lock, java.io.Serializable {
 *
 *         // Our internal helper class
 *         private static class Sync extends AbstractQueuedSynchronizer {
 *             // Reports whether in locked state
 *             protected boolean isHeldExclusively() {
 *                 return getState() == 1;
 *             }
 *
 *             // Acquires the lock if state is zero
 *             public boolean tryAcquire(int acquires) {
 *                 assert acquires == 1; // Otherwise unused
 *                 if (compareAndSetState(0, 1)) {
 *                     setExclusiveOwnerThread(Thread.currentThread());
 *                     return true;
 *                 }
 *                 return false;
 *             }
 *
 *             // Releases the lock by setting state to zero
 *             protected boolean tryRelease(int releases) {
 *                 assert releases == 1; // Otherwise unused
 *                 if (getState() == 0)
 *                     throw new IllegalMonitorStateException();
 *                 setExclusiveOwnerThread(null);
 *                 setState(0);
 *                 return true;
 *             }
 *
 *             // Provides a Condition
 *             Condition newCondition() {
 *                 return new ConditionObject();
 *             }
 *
 *             // Deserializes properly
 *             private void readObject(ObjectInputStream s) throws IOException, ClassNotFoundException {
 *                 s.defaultReadObject();
 *                 setState(0); // reset to unlocked state
 *             }
 *         }
 *
 *         // The sync object does all the hard work. We just forward to it.
 *         private final Sync sync = new Sync();
 *
 *         public void lock() {
 *             sync.acquire(1);
 *         }
 * 
 *         public boolean tryLock() {
 *             return sync.tryAcquire(1);
 *         }
 * 
 *         public void unlock() {
 *             sync.release(1);
 *         }
 * 
 *         public Condition newCondition() {
 *             return sync.newCondition();
 *         }
 * 
 *         public boolean isLocked() {
 *             return sync.isHeldExclusively();
 *         }
 * 
 *         public boolean hasQueuedThreads() {
 *             return sync.hasQueuedThreads();
 *         }
 * 
 *         public void lockInterruptibly() throws InterruptedException {
 *             sync.acquireInterruptibly(1);
 *         }
 * 
 *         public boolean tryLock(long timeout, TimeUnit unit) throws InterruptedException {
 *             return sync.tryAcquireNanos(1, unit.toNanos(timeout));
 *         }
 *     }
 * }
 * </pre>
 *
 * <p>
 * Here is a latch class that is like a
 * {@link java.util.concurrent.CountDownLatch CountDownLatch} except that it
 * only requires a single {@code signal} to fire. Because a latch is
 * non-exclusive, it uses the {@code shared} acquire and release methods.
 *
 * <pre>
 * {
 *     &#64;code
 *     class BooleanLatch {
 *
 *         private static class Sync extends AbstractQueuedSynchronizer {
 *             boolean isSignalled() {
 *                 return getState() != 0;
 *             }
 *
 *             protected int tryAcquireShared(int ignore) {
 *                 return isSignalled() ? 1 : -1;
 *             }
 *
 *             protected boolean tryReleaseShared(int ignore) {
 *                 setState(1);
 *                 return true;
 *             }
 *         }
 *
 *         private final Sync sync = new Sync();
 * 
 *         public boolean isSignalled() {
 *             return sync.isSignalled();
 *         }
 * 
 *         public void signal() {
 *             sync.releaseShared(1);
 *         }
 * 
 *         public void await() throws InterruptedException {
 *             sync.acquireSharedInterruptibly(1);
 *         }
 *     }
 * }
 * </pre>
 *
 * @since 1.5
 * @author Doug Lea
 */
public abstract class AbstractQueuedSynchronizer extends AbstractOwnableSynchronizer implements java.io.Serializable {

    // 序列话版本号
    private static final long serialVersionUID = 7373984972572414691L;

    /**
     * 构造器
     */
    protected AbstractQueuedSynchronizer() {
    }

    /**
     * 内部类 每个被阻塞的线程会封装为一个node节点，放入队列。 每个节点包含了一个thread引用，并且每个节点都存在一个状态
     */
    static final class Node {
        /** 
         * 模式，有共享模式与独占模式
         * share 共享模式 允许多个线程获取同一个锁而且可能获取成功 ， 比如读取文件的时候
         * exclusive 独占 一个锁只能被一个线程所持有 ， 比如写文件的时候不允许有其他的线程对文件更新
         */
        static final Node SHARED = new Node();
        
        static final Node EXCLUSIVE = null;

        /** 
         * canceled 值为1， 表示线程已取消      
         *      当该线程等待超时或者被中断，需要从同步队列中取消等待，则该线程被置1，即被取消（这里该线程在取消之前是等待状态）。节点进入了取消状态则不再变化
         * signal 值为-1， 表示当前节点的后继节点需要运行，也就是unpack
         *      后继的节点处于等待状态，当前节点的线程如果释放了同步状态或者被取消（当前节点状态置为-1），将会通知后继节点，使后继节点的线程得以运行
         * condition 值为-2， 表示当前线程正在等待condition， 在condition队列中
         *      节点处于等待队列中，节点线程等待在Condition上，当其他线程对Condition调用了signal()方法后，该节点从等待队列中转移到同步队列中，加入到对同步状态的获取中
         * propagate 值为-3， 表示当前场景下的后续acquireShare能够得以执行
         *      表示下一次的共享状态会被无条件的传播下去
         * 值为0， 表示当前节点在sync队列中，等待获取锁
        */
        static final int CANCELLED = 1;
        static final int SIGNAL = -1;
        static final int CONDITION = -2;
        static final int PROPAGATE = -3;

        // 节点状态
        volatile int waitStatus;

        // 前驱结点
        volatile Node prev;

        // 后继节点
        volatile Node next;

        // 节点中的线程
        volatile Thread thread;

        // 下一个等待者  ？？？ 与后继节点有什么区别
        // 等待节点的后继节点。如果当前节点是共享的，那么这个字段是一个SHARED常量，也就是说节点类型（独占和共享）和等待队列中的后继节点共用一个字段。
        // （注：比如说当前节点A是共享的，那么它的这个字段是shared，也就是说在这个等待队列中，A节点的后继节点也是shared。如果A节点不是共享的，那么它的nextWaiter就不是一个SHARED常量，即是独占的。）
        Node nextWaiter;

        // 节点是否在共享模式下等待
        final boolean isShared() {
            return nextWaiter == SHARED;
        }

        /**
         * 获取前驱结点，若前驱结点为空，抛出异常
         *
         * @return the predecessor of this node
         */
        final Node predecessor() throws NullPointerException {
            Node p = prev;
            if (p == null)
                throw new NullPointerException();
            else
                return p;
        }

        // 无参构造器
        Node() { // Used to establish initial head or SHARED marker
        }

        Node(Thread thread, Node mode) { // Used by addWaiter
            this.nextWaiter = mode;
            this.thread = thread;
        }

        Node(Thread thread, int waitStatus) { // Used by Condition
            this.waitStatus = waitStatus;
            this.thread = thread;
        }
    }

    // 头节点
    private transient volatile Node head;

    // 尾节点
    private transient volatile Node tail;

    // 状态
    private volatile int state;

    /**
     * 获取状态
     * 
     * @return current state value
     */
    protected final int getState() {
        return state;
    }

    /**
     * 设置状态
     * 
     * @param newState the new state value
     */
    protected final void setState(int newState) {
        state = newState;
    }

    /**
     * Atomically sets synchronization state to the given updated value if the
     * current state value equals the expected value. This operation has memory
     * semantics of a {@code volatile} read and write.
     *
     * @param expect the expected value
     * @param update the new value
     * @return {@code true} if successful. False return indicates that the actual
     *         value was not equal to the expected value.
     */
    protected final boolean compareAndSetState(int expect, int update) {
        // See below for intrinsics setup to support this
        return unsafe.compareAndSwapInt(this, stateOffset, expect, update);
    }

    // Queuing utilities

    /**
     * The number of nanoseconds for which it is faster to spin rather than to use
     * timed park. A rough estimate suffices to improve responsiveness with very
     * short timeouts.
     */
    static final long spinForTimeoutThreshold = 1000L;

    // 将节点插入队列
    private Node enq(final Node node) {
        for (;;) { // 无限循环，确保节点能够插入队列
            Node t = tail;
            if (t == null) { // 尾节点为空，即未初始化
                if (compareAndSetHead(new Node())) // 头节点为空，设置头节点为新生的节点
                    tail = head; // 头节点和尾节点都指向同一个新生的节点
            } else { // 未尾节点不为空，即已被初始化。尾部插入节点
                node.prev = t;
                if (compareAndSetTail(t, node)) {
                    t.next = node;
                    return t;
                }
            }
        }
    }

    // 添加等待者
    private Node addWaiter(Node mode) {
        // 新生成一个节点，默认为独占模式
        Node node = new Node(Thread.currentThread(), mode);
        // 保存尾节点
        Node pred = tail;
        if (pred != null) { // 尾节点不为空，即已经被初始化，将node尾部添加
            node.prev = pred;
            if (compareAndSetTail(pred, node)) {
                pred.next = node;
                return node;
            }
        }
        enq(node); // 尾结点为空(即还没有被初始化过)，或者是compareAndSetTail操作失败，则入队列
        return node;
    }

    /**
     * Sets head of queue to be node, thus dequeuing. Called only by acquire
     * methods. Also nulls out unused fields for sake of GC and to suppress
     * unnecessary signals and traversals.
     *
     * @param node the node
     */
    private void setHead(Node node) {
        head = node;
        node.thread = null;
        node.prev = null;
    }

    /**
     * Wakes up node's successor, if one exists.
     * 释放后继节点
     */
    private void unparkSuccessor(Node node) {
        /*
         * 获取node节点的等待状态
         */
        int ws = node.waitStatus;
        if (ws < 0)  // 状态值小于0，为SIGNAL -1 或 CONDITION -2 或 PROPAGATE -3
            compareAndSetWaitStatus(node, ws, 0); // 比较并且设置节点等待状态，设置为0

        /*
         * 线程停止在后继器中，通常是下一个节点。但如果被取消或明显无效，则从尾部向前移动，以找到实际未取消的继任者。
         */
        Node s = node.next; // 获取node的下一节点
        if (s == null || s.waitStatus > 0) { // 下一个节点为空或者下一个节点状态大于0，即为canceled
            s = null;
            for (Node t = tail; t != null && t != node; t = t.prev) // 从尾部向前遍历
                if (t.waitStatus <= 0)  // 找到节点状态小于等于0等节点， 保存节点为s，(找到后并没有跳出循环，是要找到最前面的那个)
                    s = t;
        }
        if (s != null)  // 该节点不为空，释放许可
            LockSupport.unpark(s.thread);
    }

    /**
     * Release action for shared mode -- signals successor and ensures propagation.
     * (Note: For exclusive mode, release just amounts to calling unparkSuccessor of
     * head if it needs signal.)
     */
    private void doReleaseShared() {
        /*
         * Ensure that a release propagates, even if there are other in-progress
         * acquires/releases. This proceeds in the usual way of trying to
         * unparkSuccessor of head if it needs signal. But if it does not, status is set
         * to PROPAGATE to ensure that upon release, propagation continues.
         * Additionally, we must loop in case a new node is added while we are doing
         * this. Also, unlike other uses of unparkSuccessor, we need to know if CAS to
         * reset status fails, if so rechecking.
         */
        for (;;) {
            Node h = head;
            if (h != null && h != tail) {
                int ws = h.waitStatus;
                if (ws == Node.SIGNAL) {
                    if (!compareAndSetWaitStatus(h, Node.SIGNAL, 0))
                        continue; // loop to recheck cases
                    unparkSuccessor(h);
                } else if (ws == 0 && !compareAndSetWaitStatus(h, 0, Node.PROPAGATE))
                    continue; // loop on failed CAS
            }
            if (h == head) // loop if head changed
                break;
        }
    }

    /**
     * Sets head of queue, and checks if successor may be waiting in shared mode, if
     * so propagating if either propagate > 0 or PROPAGATE status was set.
     *
     * @param node      the node
     * @param propagate the return value from a tryAcquireShared
     */
    private void setHeadAndPropagate(Node node, int propagate) {
        Node h = head; // Record old head for check below
        setHead(node);
        /*
         * Try to signal next queued node if: Propagation was indicated by caller, or
         * was recorded (as h.waitStatus either before or after setHead) by a previous
         * operation (note: this uses sign-check of waitStatus because PROPAGATE status
         * may transition to SIGNAL.) and The next node is waiting in shared mode, or we
         * don't know, because it appears null
         *
         * The conservatism in both of these checks may cause unnecessary wake-ups, but
         * only when there are multiple racing acquires/releases, so most need signals
         * now or soon anyway.
         */
        if (propagate > 0 || h == null || h.waitStatus < 0 || (h = head) == null || h.waitStatus < 0) {
            Node s = node.next;
            if (s == null || s.isShared())
                doReleaseShared();
        }
    }

    // Utilities for various versions of acquire

    /**
     * Cancels an ongoing attempt to acquire.
     * 取消继续获取资源
     */
    private void cancelAcquire(Node node) {
        // Ignore if node doesn't exist
        if (node == null) // node为空，返回
            return;

        node.thread = null; // 设置node节点的thread为空

        // 获取前驱结点
        Node pred = node.prev;
        while (pred.waitStatus > 0) // 找到node前驱结点中第一个状态小于0的结点，即不为CANCELLED状态的结点
            node.prev = pred = pred.prev;

        // 获取后继节点
        Node predNext = pred.next;

        // Can use unconditional write instead of CAS here.
        // After this atomic step, other Nodes can skip past us.
        // Before, we are free of interference from other threads.
        // 设置node节点的状态为canceled
        node.waitStatus = Node.CANCELLED;

        // If we are the tail, remove ourselves.
        if (node == tail && compareAndSetTail(node, pred)) { // node节点为尾节点，则设置尾节点为pre节点
            compareAndSetNext(pred, predNext, null);    // 比较并设置pre节点的next为null
        } else { // node不为尾节点 或者 比较设置不成功
            // If successor needs signal, try to set pred's next-link
            // so it will get one. Otherwise wake it up to propagate.
            int ws;
            // pre不为头节点， 且 pre节点的状态为signal或者pre节点状态小于等于0  并且比较并设置等待状态为siganl成功，并且pre节点的线程不为空
            if (pred != head && ((ws = pred.waitStatus) == Node.SIGNAL
                    || (ws <= 0 && compareAndSetWaitStatus(pred, ws, Node.SIGNAL))) && pred.thread != null) {
                Node next = node.next;
                if (next != null && next.waitStatus <= 0) // 后继节点不为空且状态小于等于0
                    compareAndSetNext(pred, predNext, next); // 比较并且设置pred.next= next
            } else {
                unparkSuccessor(node); // 释放node的前一个结点 ???
            }

            node.next = node; // help GC
        }
    }

    /**
     * 检查并更新未能获取的节点的状态。如果线程应该阻塞，则返回true。这是所有采集回路中的主要信号控制。需要PRD=＝NODE.PREV。
     */
    private static boolean shouldParkAfterFailedAcquire(Node pred, Node node) {
        // 获取前驱结点的状态
        int ws = pred.waitStatus;
        if (ws == Node.SIGNAL) // 状态为signal，为-1
            /*
             * This node has already set status asking a release to signal it, so it can
             * safely park.
             * 可以进行pack操作
             */
            return true;
        if (ws > 0) { // 状态为canceled，为1
            /*
             * Predecessor was cancelled. Skip over predecessors and indicate retry.
             */
            do {
                node.prev = pred = pred.prev;
            } while (pred.waitStatus > 0);  // 找到pred结点前面最近的一个状态不为CANCELLED的结点
            pred.next = node;   // 赋值pred结点的next域
        } else {    // 为PROPAGATE -3 或者是0 表示无状态,(为CONDITION -2时，表示此节点在condition queue中) 
            /*
             * waitStatus must be 0 or PROPAGATE. Indicate that we need a signal, but don't
             * park yet. Caller will need to retry to make sure it cannot acquire before
             * parking.
             * 比较并设置前驱结点的状态为SIGNAL
             */
            compareAndSetWaitStatus(pred, ws, Node.SIGNAL);
        }
        // 不能进行park操作
        return false;
    }

    /**
     * Convenience method to interrupt current thread.
     */
    static void selfInterrupt() {
        Thread.currentThread().interrupt();
    }

    /**
     * Convenience method to park and then check if interrupted
     * 进行park操作并且返回该线程是否被中断
     * 首先执行park操作，即禁用当前线程，然后返回该线程是否已经被中断
     */
    private final boolean parkAndCheckInterrupt() {
        // 在许可可用之前禁用当前线程，并且设置了blocker
        LockSupport.park(this);
        return Thread.interrupted();    // 当前线程是否已被中断，并清除中断标记位
    }

    /**
     * acquireQueued逻辑
     * 1. 判断当前节点的前驱是否为head，并且是否成功获取资源
     * 2. 若满足1，则设置节点为head，之后判断是否finally，然后返回
     * 3. 若不满足1，则判断是否park当前线程，是否需要park当前线程是通过 判断节点的前去节点的状态是否为signal。若是则park当前线程，否则不进行park
     */

    // sync队列中的结点在独占且忽略中断的模式下获取(资源)
    // 首先获取当前节点的前驱节点，如果前驱节点是头结点并且能够获取(资源)，代表该当前节点能够占有锁，设置头结点为当前节点，返回。
    // 否则，调用shouldParkAfterFailedAcquire和parkAndCheckInterrupt函数
    final boolean acquireQueued(final Node node, int arg) {
        boolean failed = true; // 标志
        try {
            // 中断标志
            boolean interrupted = false;
            for (;;) { // 无限循环
                final Node p = node.predecessor();  // 获取node节点的前驱结点
                if (p == head && tryAcquire(arg)) { // 前驱结点为头节点并且成功获得锁， 表明只有头节点之后的第一个节点才有可能获取锁，队列特性
                    setHead(node);  // 设置头节点
                    p.next = null; // help GC
                    failed = false; // 设置标志
                    return interrupted;
                }
                if (shouldParkAfterFailedAcquire(p, node) && parkAndCheckInterrupt())
                    interrupted = true;
            }
        } finally {
            if (failed)
                cancelAcquire(node);
        }
    }

    /**
     * Acquires in exclusive interruptible mode.
     * 
     * @param arg the acquire argument
     */
    private void doAcquireInterruptibly(int arg) throws InterruptedException {
        final Node node = addWaiter(Node.EXCLUSIVE);
        boolean failed = true;
        try {
            for (;;) {
                final Node p = node.predecessor();
                if (p == head && tryAcquire(arg)) {
                    setHead(node);
                    p.next = null; // help GC
                    failed = false;
                    return;
                }
                if (shouldParkAfterFailedAcquire(p, node) && parkAndCheckInterrupt())
                    throw new InterruptedException();
            }
        } finally {
            if (failed)
                cancelAcquire(node);
        }
    }

    /**
     * Acquires in exclusive timed mode.
     *
     * @param arg          the acquire argument
     * @param nanosTimeout max wait time
     * @return {@code true} if acquired
     */
    private boolean doAcquireNanos(int arg, long nanosTimeout) throws InterruptedException {
        if (nanosTimeout <= 0L)
            return false;
        final long deadline = System.nanoTime() + nanosTimeout;
        final Node node = addWaiter(Node.EXCLUSIVE);
        boolean failed = true;
        try {
            for (;;) {
                final Node p = node.predecessor();
                if (p == head && tryAcquire(arg)) {
                    setHead(node);
                    p.next = null; // help GC
                    failed = false;
                    return true;
                }
                nanosTimeout = deadline - System.nanoTime();
                if (nanosTimeout <= 0L)
                    return false;
                if (shouldParkAfterFailedAcquire(p, node) && nanosTimeout > spinForTimeoutThreshold)
                    LockSupport.parkNanos(this, nanosTimeout);
                if (Thread.interrupted())
                    throw new InterruptedException();
            }
        } finally {
            if (failed)
                cancelAcquire(node);
        }
    }

    /**
     * Acquires in shared uninterruptible mode.
     * 
     * @param arg the acquire argument
     */
    private void doAcquireShared(int arg) {
        final Node node = addWaiter(Node.SHARED);
        boolean failed = true;
        try {
            boolean interrupted = false;
            for (;;) {
                final Node p = node.predecessor();
                if (p == head) {
                    int r = tryAcquireShared(arg);
                    if (r >= 0) {
                        setHeadAndPropagate(node, r);
                        p.next = null; // help GC
                        if (interrupted)
                            selfInterrupt();
                        failed = false;
                        return;
                    }
                }
                if (shouldParkAfterFailedAcquire(p, node) && parkAndCheckInterrupt())
                    interrupted = true;
            }
        } finally {
            if (failed)
                cancelAcquire(node);
        }
    }

    /**
     * Acquires in shared interruptible mode.
     * 
     * @param arg the acquire argument
     */
    private void doAcquireSharedInterruptibly(int arg) throws InterruptedException {
        final Node node = addWaiter(Node.SHARED);
        boolean failed = true;
        try {
            for (;;) {
                final Node p = node.predecessor();
                if (p == head) {
                    int r = tryAcquireShared(arg);
                    if (r >= 0) {
                        setHeadAndPropagate(node, r);
                        p.next = null; // help GC
                        failed = false;
                        return;
                    }
                }
                if (shouldParkAfterFailedAcquire(p, node) && parkAndCheckInterrupt())
                    throw new InterruptedException();
            }
        } finally {
            if (failed)
                cancelAcquire(node);
        }
    }

    /**
     * Acquires in shared timed mode.
     *
     * @param arg          the acquire argument
     * @param nanosTimeout max wait time
     * @return {@code true} if acquired
     */
    private boolean doAcquireSharedNanos(int arg, long nanosTimeout) throws InterruptedException {
        if (nanosTimeout <= 0L)
            return false;
        final long deadline = System.nanoTime() + nanosTimeout;
        final Node node = addWaiter(Node.SHARED);
        boolean failed = true;
        try {
            for (;;) {
                final Node p = node.predecessor();
                if (p == head) {
                    int r = tryAcquireShared(arg);
                    if (r >= 0) {
                        setHeadAndPropagate(node, r);
                        p.next = null; // help GC
                        failed = false;
                        return true;
                    }
                }
                nanosTimeout = deadline - System.nanoTime();
                if (nanosTimeout <= 0L)
                    return false;
                if (shouldParkAfterFailedAcquire(p, node) && nanosTimeout > spinForTimeoutThreshold)
                    LockSupport.parkNanos(this, nanosTimeout);
                if (Thread.interrupted())
                    throw new InterruptedException();
            }
        } finally {
            if (failed)
                cancelAcquire(node);
        }
    }

    // Main exported methods

    /**
     * Attempts to acquire in exclusive mode. This method should query if the state
     * of the object permits it to be acquired in the exclusive mode, and if so to
     * acquire it.
     *
     * <p>
     * This method is always invoked by the thread performing acquire. If this
     * method reports failure, the acquire method may queue the thread, if it is not
     * already queued, until it is signalled by a release from some other thread.
     * This can be used to implement method {@link Lock#tryLock()}.
     *
     * <p>
     * The default implementation throws {@link UnsupportedOperationException}.
     *
     * @param arg the acquire argument. This value is always the one passed to an
     *            acquire method, or is the value saved on entry to a condition
     *            wait. The value is otherwise uninterpreted and can represent
     *            anything you like.
     * @return {@code true} if successful. Upon success, this object has been
     *         acquired.
     * @throws IllegalMonitorStateException  if acquiring would place this
     *                                       synchronizer in an illegal state. This
     *                                       exception must be thrown in a
     *                                       consistent fashion for synchronization
     *                                       to work correctly.
     * @throws UnsupportedOperationException if exclusive mode is not supported
     */
    protected boolean tryAcquire(int arg) {
        throw new UnsupportedOperationException();
    }

    /**
     * Attempts to set the state to reflect a release in exclusive mode.
     *
     * <p>
     * This method is always invoked by the thread performing release.
     *
     * <p>
     * The default implementation throws {@link UnsupportedOperationException}.
     *
     * @param arg the release argument. This value is always the one passed to a
     *            release method, or the current state value upon entry to a
     *            condition wait. The value is otherwise uninterpreted and can
     *            represent anything you like.
     * @return {@code true} if this object is now in a fully released state, so that
     *         any waiting threads may attempt to acquire; and {@code false}
     *         otherwise.
     * @throws IllegalMonitorStateException  if releasing would place this
     *                                       synchronizer in an illegal state. This
     *                                       exception must be thrown in a
     *                                       consistent fashion for synchronization
     *                                       to work correctly.
     * @throws UnsupportedOperationException if exclusive mode is not supported
     */
    protected boolean tryRelease(int arg) {
        throw new UnsupportedOperationException();
    }

    /**
     * Attempts to acquire in shared mode. This method should query if the state of
     * the object permits it to be acquired in the shared mode, and if so to acquire
     * it.
     *
     * <p>
     * This method is always invoked by the thread performing acquire. If this
     * method reports failure, the acquire method may queue the thread, if it is not
     * already queued, until it is signalled by a release from some other thread.
     *
     * <p>
     * The default implementation throws {@link UnsupportedOperationException}.
     *
     * @param arg the acquire argument. This value is always the one passed to an
     *            acquire method, or is the value saved on entry to a condition
     *            wait. The value is otherwise uninterpreted and can represent
     *            anything you like.
     * @return a negative value on failure; zero if acquisition in shared mode
     *         succeeded but no subsequent shared-mode acquire can succeed; and a
     *         positive value if acquisition in shared mode succeeded and subsequent
     *         shared-mode acquires might also succeed, in which case a subsequent
     *         waiting thread must check availability. (Support for three different
     *         return values enables this method to be used in contexts where
     *         acquires only sometimes act exclusively.) Upon success, this object
     *         has been acquired.
     * @throws IllegalMonitorStateException  if acquiring would place this
     *                                       synchronizer in an illegal state. This
     *                                       exception must be thrown in a
     *                                       consistent fashion for synchronization
     *                                       to work correctly.
     * @throws UnsupportedOperationException if shared mode is not supported
     */
    protected int tryAcquireShared(int arg) {
        throw new UnsupportedOperationException();
    }

    /**
     * Attempts to set the state to reflect a release in shared mode.
     *
     * <p>
     * This method is always invoked by the thread performing release.
     *
     * <p>
     * The default implementation throws {@link UnsupportedOperationException}.
     *
     * @param arg the release argument. This value is always the one passed to a
     *            release method, or the current state value upon entry to a
     *            condition wait. The value is otherwise uninterpreted and can
     *            represent anything you like.
     * @return {@code true} if this release of shared mode may permit a waiting
     *         acquire (shared or exclusive) to succeed; and {@code false} otherwise
     * @throws IllegalMonitorStateException  if releasing would place this
     *                                       synchronizer in an illegal state. This
     *                                       exception must be thrown in a
     *                                       consistent fashion for synchronization
     *                                       to work correctly.
     * @throws UnsupportedOperationException if shared mode is not supported
     */
    protected boolean tryReleaseShared(int arg) {
        throw new UnsupportedOperationException();
    }

    /**
     * Returns {@code true} if synchronization is held exclusively with respect to
     * the current (calling) thread. This method is invoked upon each call to a
     * non-waiting {@link ConditionObject} method. (Waiting methods instead invoke
     * {@link #release}.)
     *
     * <p>
     * The default implementation throws {@link UnsupportedOperationException}. This
     * method is invoked internally only within {@link ConditionObject} methods, so
     * need not be defined if conditions are not used.
     *
     * @return {@code true} if synchronization is held exclusively; {@code false}
     *         otherwise
     * @throws UnsupportedOperationException if conditions are not supported
     */
    protected boolean isHeldExclusively() {
        throw new UnsupportedOperationException();
    }

    /**
     * 核心函数
     * 该函数以独占模式获取(资源)，忽略中断，即在线程acquire过程中，中断是无效的。
     * 
     * 流程： 
     *  1. 首先调用tryAcquire函数，调用此方法的线程会试图在独占模式下获取对象状态。此方法应该查询是否允许它在独占模式下获取对象状态，
     * 如果允许，则获取它。 在AbstractQueuedSynchronizer的源码中会默认抛出一个异常，即需要子类去重写此函数完成自己的逻辑。
     *  2. 若tryAcquire失败，则调用addWaiter函数，addWaiter函数的作用是将当前线程封装为node节点放入到syncQueue。
     *  3. 调用acquireQueue函数，此函数功能是 syncQUeue中的节点不断尝试获取资源，若成功则返回true；否则返回false
     */
    public final void acquire(int arg) {
        if (!tryAcquire(arg) && acquireQueued(addWaiter(Node.EXCLUSIVE), arg))
            selfInterrupt();
    }

    /**
     * Acquires in exclusive mode, aborting if interrupted. Implemented by first
     * checking interrupt status, then invoking at least once {@link #tryAcquire},
     * returning on success. Otherwise the thread is queued, possibly repeatedly
     * blocking and unblocking, invoking {@link #tryAcquire} until success or the
     * thread is interrupted. This method can be used to implement method
     * {@link Lock#lockInterruptibly}.
     *
     * @param arg the acquire argument. This value is conveyed to
     *            {@link #tryAcquire} but is otherwise uninterpreted and can
     *            represent anything you like.
     * @throws InterruptedException if the current thread is interrupted
     */
    public final void acquireInterruptibly(int arg) throws InterruptedException {
        if (Thread.interrupted())
            throw new InterruptedException();
        if (!tryAcquire(arg))
            doAcquireInterruptibly(arg);
    }

    /**
     * Attempts to acquire in exclusive mode, aborting if interrupted, and failing
     * if the given timeout elapses. Implemented by first checking interrupt status,
     * then invoking at least once {@link #tryAcquire}, returning on success.
     * Otherwise, the thread is queued, possibly repeatedly blocking and unblocking,
     * invoking {@link #tryAcquire} until success or the thread is interrupted or
     * the timeout elapses. This method can be used to implement method
     * {@link Lock#tryLock(long, TimeUnit)}.
     *
     * @param arg          the acquire argument. This value is conveyed to
     *                     {@link #tryAcquire} but is otherwise uninterpreted and
     *                     can represent anything you like.
     * @param nanosTimeout the maximum number of nanoseconds to wait
     * @return {@code true} if acquired; {@code false} if timed out
     * @throws InterruptedException if the current thread is interrupted
     */
    public final boolean tryAcquireNanos(int arg, long nanosTimeout) throws InterruptedException {
        if (Thread.interrupted())
            throw new InterruptedException();
        return tryAcquire(arg) || doAcquireNanos(arg, nanosTimeout);
    }

    /**
     * 以独占模式释放对象
     */
    public final boolean release(int arg) {
        if (tryRelease(arg)) {  // 释放成功
            Node h = head;
            if (h != null && h.waitStatus != 0) //  头节点不为空且头节点的状态不为0
                unparkSuccessor(h); // 受访头节点的后继节点
            return true;
        }
        return false;
    }

    /**
     * Acquires in shared mode, ignoring interrupts. Implemented by first invoking
     * at least once {@link #tryAcquireShared}, returning on success. Otherwise the
     * thread is queued, possibly repeatedly blocking and unblocking, invoking
     * {@link #tryAcquireShared} until success.
     *
     * @param arg the acquire argument. This value is conveyed to
     *            {@link #tryAcquireShared} but is otherwise uninterpreted and can
     *            represent anything you like.
     */
    public final void acquireShared(int arg) {
        if (tryAcquireShared(arg) < 0)
            doAcquireShared(arg);
    }

    /**
     * Acquires in shared mode, aborting if interrupted. Implemented by first
     * checking interrupt status, then invoking at least once
     * {@link #tryAcquireShared}, returning on success. Otherwise the thread is
     * queued, possibly repeatedly blocking and unblocking, invoking
     * {@link #tryAcquireShared} until success or the thread is interrupted.
     * 
     * @param arg the acquire argument. This value is conveyed to
     *            {@link #tryAcquireShared} but is otherwise uninterpreted and can
     *            represent anything you like.
     * @throws InterruptedException if the current thread is interrupted
     */
    public final void acquireSharedInterruptibly(int arg) throws InterruptedException {
        if (Thread.interrupted())
            throw new InterruptedException();
        if (tryAcquireShared(arg) < 0)
            doAcquireSharedInterruptibly(arg);
    }

    /**
     * Attempts to acquire in shared mode, aborting if interrupted, and failing if
     * the given timeout elapses. Implemented by first checking interrupt status,
     * then invoking at least once {@link #tryAcquireShared}, returning on success.
     * Otherwise, the thread is queued, possibly repeatedly blocking and unblocking,
     * invoking {@link #tryAcquireShared} until success or the thread is interrupted
     * or the timeout elapses.
     *
     * @param arg          the acquire argument. This value is conveyed to
     *                     {@link #tryAcquireShared} but is otherwise uninterpreted
     *                     and can represent anything you like.
     * @param nanosTimeout the maximum number of nanoseconds to wait
     * @return {@code true} if acquired; {@code false} if timed out
     * @throws InterruptedException if the current thread is interrupted
     */
    public final boolean tryAcquireSharedNanos(int arg, long nanosTimeout) throws InterruptedException {
        if (Thread.interrupted())
            throw new InterruptedException();
        return tryAcquireShared(arg) >= 0 || doAcquireSharedNanos(arg, nanosTimeout);
    }

    /**
     * Releases in shared mode. Implemented by unblocking one or more threads if
     * {@link #tryReleaseShared} returns true.
     *
     * @param arg the release argument. This value is conveyed to
     *            {@link #tryReleaseShared} but is otherwise uninterpreted and can
     *            represent anything you like.
     * @return the value returned from {@link #tryReleaseShared}
     */
    public final boolean releaseShared(int arg) {
        if (tryReleaseShared(arg)) {
            doReleaseShared();
            return true;
        }
        return false;
    }

    // Queue inspection methods

    /**
     * Queries whether any threads are waiting to acquire. Note that because
     * cancellations due to interrupts and timeouts may occur at any time, a
     * {@code true} return does not guarantee that any other thread will ever
     * acquire.
     *
     * <p>
     * In this implementation, this operation returns in constant time.
     *
     * @return {@code true} if there may be other threads waiting to acquire
     */
    public final boolean hasQueuedThreads() {
        return head != tail;
    }

    /**
     * Queries whether any threads have ever contended to acquire this synchronizer;
     * that is if an acquire method has ever blocked.
     *
     * <p>
     * In this implementation, this operation returns in constant time.
     *
     * @return {@code true} if there has ever been contention
     */
    public final boolean hasContended() {
        return head != null;
    }

    /**
     * Returns the first (longest-waiting) thread in the queue, or {@code null} if
     * no threads are currently queued.
     *
     * <p>
     * In this implementation, this operation normally returns in constant time, but
     * may iterate upon contention if other threads are concurrently modifying the
     * queue.
     *
     * @return the first (longest-waiting) thread in the queue, or {@code null} if
     *         no threads are currently queued
     */
    public final Thread getFirstQueuedThread() {
        // handle only fast path, else relay
        return (head == tail) ? null : fullGetFirstQueuedThread();
    }

    /**
     * Version of getFirstQueuedThread called when fastpath fails
     */
    private Thread fullGetFirstQueuedThread() {
        /*
         * The first node is normally head.next. Try to get its thread field, ensuring
         * consistent reads: If thread field is nulled out or s.prev is no longer head,
         * then some other thread(s) concurrently performed setHead in between some of
         * our reads. We try this twice before resorting to traversal.
         */
        Node h, s;
        Thread st;
        if (((h = head) != null && (s = h.next) != null && s.prev == head && (st = s.thread) != null)
                || ((h = head) != null && (s = h.next) != null && s.prev == head && (st = s.thread) != null))
            return st;

        /*
         * Head's next field might not have been set yet, or may have been unset after
         * setHead. So we must check to see if tail is actually first node. If not, we
         * continue on, safely traversing from tail back to head to find first,
         * guaranteeing termination.
         */

        Node t = tail;
        Thread firstThread = null;
        while (t != null && t != head) {
            Thread tt = t.thread;
            if (tt != null)
                firstThread = tt;
            t = t.prev;
        }
        return firstThread;
    }

    /**
     * Returns true if the given thread is currently queued.
     *
     * <p>
     * This implementation traverses the queue to determine presence of the given
     * thread.
     *
     * @param thread the thread
     * @return {@code true} if the given thread is on the queue
     * @throws NullPointerException if the thread is null
     */
    public final boolean isQueued(Thread thread) {
        if (thread == null)
            throw new NullPointerException();
        for (Node p = tail; p != null; p = p.prev)
            if (p.thread == thread)
                return true;
        return false;
    }

    /**
     * Returns {@code true} if the apparent first queued thread, if one exists, is
     * waiting in exclusive mode. If this method returns {@code true}, and the
     * current thread is attempting to acquire in shared mode (that is, this method
     * is invoked from {@link #tryAcquireShared}) then it is guaranteed that the
     * current thread is not the first queued thread. Used only as a heuristic in
     * ReentrantReadWriteLock.
     */
    final boolean apparentlyFirstQueuedIsExclusive() {
        Node h, s;
        return (h = head) != null && (s = h.next) != null && !s.isShared() && s.thread != null;
    }

    /**
     * Queries whether any threads have been waiting to acquire longer than the
     * current thread.
     *
     * <p>
     * An invocation of this method is equivalent to (but may be more efficient
     * than):
     * 
     * <pre>
     *  {@code
     * getFirstQueuedThread() != Thread.currentThread() &&
     * hasQueuedThreads()}
     * </pre>
     *
     * <p>
     * Note that because cancellations due to interrupts and timeouts may occur at
     * any time, a {@code true} return does not guarantee that some other thread
     * will acquire before the current thread. Likewise, it is possible for another
     * thread to win a race to enqueue after this method has returned {@code false},
     * due to the queue being empty.
     *
     * <p>
     * This method is designed to be used by a fair synchronizer to avoid
     * <a href="AbstractQueuedSynchronizer#barging">barging</a>. Such a
     * synchronizer's {@link #tryAcquire} method should return {@code false}, and
     * its {@link #tryAcquireShared} method should return a negative value, if this
     * method returns {@code true} (unless this is a reentrant acquire). For
     * example, the {@code
     * tryAcquire} method for a fair, reentrant, exclusive mode synchronizer might
     * look like this:
     *
     * <pre>
     *  {@code
     * protected boolean tryAcquire(int arg) {
     *   if (isHeldExclusively()) {
     *     // A reentrant acquire; increment hold count
     *     return true;
     *   } else if (hasQueuedPredecessors()) {
     *     return false;
     *   } else {
     *     // try to acquire normally
     *   }
     * }}
     * </pre>
     *
     * @return {@code true} if there is a queued thread preceding the current
     *         thread, and {@code false} if the current thread is at the head of the
     *         queue or the queue is empty
     * @since 1.7
     */
    public final boolean hasQueuedPredecessors() {
        // The correctness of this depends on head being initialized
        // before tail and on head.next being accurate if the current
        // thread is first in queue.
        Node t = tail; // Read fields in reverse initialization order
        Node h = head;
        Node s;
        return h != t && ((s = h.next) == null || s.thread != Thread.currentThread());
    }

    // Instrumentation and monitoring methods

    /**
     * Returns an estimate of the number of threads waiting to acquire. The value is
     * only an estimate because the number of threads may change dynamically while
     * this method traverses internal data structures. This method is designed for
     * use in monitoring system state, not for synchronization control.
     *
     * @return the estimated number of threads waiting to acquire
     */
    public final int getQueueLength() {
        int n = 0;
        for (Node p = tail; p != null; p = p.prev) {
            if (p.thread != null)
                ++n;
        }
        return n;
    }

    /**
     * Returns a collection containing threads that may be waiting to acquire.
     * Because the actual set of threads may change dynamically while constructing
     * this result, the returned collection is only a best-effort estimate. The
     * elements of the returned collection are in no particular order. This method
     * is designed to facilitate construction of subclasses that provide more
     * extensive monitoring facilities.
     *
     * @return the collection of threads
     */
    public final Collection<Thread> getQueuedThreads() {
        ArrayList<Thread> list = new ArrayList<Thread>();
        for (Node p = tail; p != null; p = p.prev) {
            Thread t = p.thread;
            if (t != null)
                list.add(t);
        }
        return list;
    }

    /**
     * Returns a collection containing threads that may be waiting to acquire in
     * exclusive mode. This has the same properties as {@link #getQueuedThreads}
     * except that it only returns those threads waiting due to an exclusive
     * acquire.
     *
     * @return the collection of threads
     */
    public final Collection<Thread> getExclusiveQueuedThreads() {
        ArrayList<Thread> list = new ArrayList<Thread>();
        for (Node p = tail; p != null; p = p.prev) {
            if (!p.isShared()) {
                Thread t = p.thread;
                if (t != null)
                    list.add(t);
            }
        }
        return list;
    }

    /**
     * Returns a collection containing threads that may be waiting to acquire in
     * shared mode. This has the same properties as {@link #getQueuedThreads} except
     * that it only returns those threads waiting due to a shared acquire.
     *
     * @return the collection of threads
     */
    public final Collection<Thread> getSharedQueuedThreads() {
        ArrayList<Thread> list = new ArrayList<Thread>();
        for (Node p = tail; p != null; p = p.prev) {
            if (p.isShared()) {
                Thread t = p.thread;
                if (t != null)
                    list.add(t);
            }
        }
        return list;
    }

    /**
     * Returns a string identifying this synchronizer, as well as its state. The
     * state, in brackets, includes the String {@code "State ="} followed by the
     * current value of {@link #getState}, and either {@code "nonempty"} or
     * {@code "empty"} depending on whether the queue is empty.
     *
     * @return a string identifying this synchronizer, as well as its state
     */
    public String toString() {
        int s = getState();
        String q = hasQueuedThreads() ? "non" : "";
        return super.toString() + "[State = " + s + ", " + q + "empty queue]";
    }

    // Internal support methods for Conditions

    /**
     * Returns true if a node, always one that was initially placed on a condition
     * queue, is now waiting to reacquire on sync queue.
     * 
     * @param node the node
     * @return true if is reacquiring
     */
    final boolean isOnSyncQueue(Node node) {
        if (node.waitStatus == Node.CONDITION || node.prev == null)
            return false;
        if (node.next != null) // If has successor, it must be on queue
            return true;
        /*
         * node.prev can be non-null, but not yet on queue because the CAS to place it
         * on queue can fail. So we have to traverse from tail to make sure it actually
         * made it. It will always be near the tail in calls to this method, and unless
         * the CAS failed (which is unlikely), it will be there, so we hardly ever
         * traverse much.
         */
        return findNodeFromTail(node);
    }

    /**
     * Returns true if node is on sync queue by searching backwards from tail.
     * Called only when needed by isOnSyncQueue.
     * 
     * @return true if present
     */
    private boolean findNodeFromTail(Node node) {
        Node t = tail;
        for (;;) {
            if (t == node)
                return true;
            if (t == null)
                return false;
            t = t.prev;
        }
    }

    /**
     * Transfers a node from a condition queue onto sync queue. Returns true if
     * successful.
     * 
     * @param node the node
     * @return true if successfully transferred (else the node was cancelled before
     *         signal)
     */
    final boolean transferForSignal(Node node) {
        /*
         * If cannot change waitStatus, the node has been cancelled.
         */
        if (!compareAndSetWaitStatus(node, Node.CONDITION, 0))
            return false;

        /*
         * Splice onto queue and try to set waitStatus of predecessor to indicate that
         * thread is (probably) waiting. If cancelled or attempt to set waitStatus
         * fails, wake up to resync (in which case the waitStatus can be transiently and
         * harmlessly wrong).
         */
        Node p = enq(node);
        int ws = p.waitStatus;
        if (ws > 0 || !compareAndSetWaitStatus(p, ws, Node.SIGNAL))
            LockSupport.unpark(node.thread);
        return true;
    }

    /**
     * Transfers node, if necessary, to sync queue after a cancelled wait. Returns
     * true if thread was cancelled before being signalled.
     *
     * @param node the node
     * @return true if cancelled before the node was signalled
     */
    final boolean transferAfterCancelledWait(Node node) {
        if (compareAndSetWaitStatus(node, Node.CONDITION, 0)) {
            enq(node);
            return true;
        }
        /*
         * If we lost out to a signal(), then we can't proceed until it finishes its
         * enq(). Cancelling during an incomplete transfer is both rare and transient,
         * so just spin.
         */
        while (!isOnSyncQueue(node))
            Thread.yield();
        return false;
    }

    /**
     * Invokes release with current state value; returns saved state. Cancels node
     * and throws exception on failure.
     * 
     * @param node the condition node for this wait
     * @return previous sync state
     */
    final int fullyRelease(Node node) {
        boolean failed = true;
        try {
            int savedState = getState();
            if (release(savedState)) {
                failed = false;
                return savedState;
            } else {
                throw new IllegalMonitorStateException();
            }
        } finally {
            if (failed)
                node.waitStatus = Node.CANCELLED;
        }
    }

    // Instrumentation methods for conditions

    /**
     * Queries whether the given ConditionObject uses this synchronizer as its lock.
     *
     * @param condition the condition
     * @return {@code true} if owned
     * @throws NullPointerException if the condition is null
     */
    public final boolean owns(ConditionObject condition) {
        return condition.isOwnedBy(this);
    }

    /**
     * Queries whether any threads are waiting on the given condition associated
     * with this synchronizer. Note that because timeouts and interrupts may occur
     * at any time, a {@code true} return does not guarantee that a future
     * {@code signal} will awaken any threads. This method is designed primarily for
     * use in monitoring of the system state.
     *
     * @param condition the condition
     * @return {@code true} if there are any waiting threads
     * @throws IllegalMonitorStateException if exclusive synchronization is not held
     * @throws IllegalArgumentException     if the given condition is not associated
     *                                      with this synchronizer
     * @throws NullPointerException         if the condition is null
     */
    public final boolean hasWaiters(ConditionObject condition) {
        if (!owns(condition))
            throw new IllegalArgumentException("Not owner");
        return condition.hasWaiters();
    }

    /**
     * Returns an estimate of the number of threads waiting on the given condition
     * associated with this synchronizer. Note that because timeouts and interrupts
     * may occur at any time, the estimate serves only as an upper bound on the
     * actual number of waiters. This method is designed for use in monitoring of
     * the system state, not for synchronization control.
     *
     * @param condition the condition
     * @return the estimated number of waiting threads
     * @throws IllegalMonitorStateException if exclusive synchronization is not held
     * @throws IllegalArgumentException     if the given condition is not associated
     *                                      with this synchronizer
     * @throws NullPointerException         if the condition is null
     */
    public final int getWaitQueueLength(ConditionObject condition) {
        if (!owns(condition))
            throw new IllegalArgumentException("Not owner");
        return condition.getWaitQueueLength();
    }

    /**
     * Returns a collection containing those threads that may be waiting on the
     * given condition associated with this synchronizer. Because the actual set of
     * threads may change dynamically while constructing this result, the returned
     * collection is only a best-effort estimate. The elements of the returned
     * collection are in no particular order.
     *
     * @param condition the condition
     * @return the collection of threads
     * @throws IllegalMonitorStateException if exclusive synchronization is not held
     * @throws IllegalArgumentException     if the given condition is not associated
     *                                      with this synchronizer
     * @throws NullPointerException         if the condition is null
     */
    public final Collection<Thread> getWaitingThreads(ConditionObject condition) {
        if (!owns(condition))
            throw new IllegalArgumentException("Not owner");
        return condition.getWaitingThreads();
    }

    /**
     * Condition implementation for a {@link AbstractQueuedSynchronizer} serving as
     * the basis of a {@link Lock} implementation.
     *
     * <p>
     * Method documentation for this class describes mechanics, not behavioral
     * specifications from the point of view of Lock and Condition users. Exported
     * versions of this class will in general need to be accompanied by
     * documentation describing condition semantics that rely on those of the
     * associated {@code AbstractQueuedSynchronizer}.
     *
     * <p>
     * This class is Serializable, but all fields are transient, so deserialized
     * conditions have no waiters.
     */
    public class ConditionObject implements Condition, java.io.Serializable {
        private static final long serialVersionUID = 1173984872572414699L;
        /** First node of condition queue. */
        private transient Node firstWaiter;
        /** Last node of condition queue. */
        private transient Node lastWaiter;

        /**
         * Creates a new {@code ConditionObject} instance.
         */
        public ConditionObject() {
        }

        // Internal methods

        /**
         * Adds a new waiter to wait queue.
         * 
         * @return its new wait node
         */
        private Node addConditionWaiter() {
            Node t = lastWaiter;
            // If lastWaiter is cancelled, clean out.
            if (t != null && t.waitStatus != Node.CONDITION) {
                unlinkCancelledWaiters();
                t = lastWaiter;
            }
            Node node = new Node(Thread.currentThread(), Node.CONDITION);
            if (t == null)
                firstWaiter = node;
            else
                t.nextWaiter = node;
            lastWaiter = node;
            return node;
        }

        /**
         * Removes and transfers nodes until hit non-cancelled one or null. Split out
         * from signal in part to encourage compilers to inline the case of no waiters.
         * 
         * @param first (non-null) the first node on condition queue
         */
        private void doSignal(Node first) {
            do {
                if ((firstWaiter = first.nextWaiter) == null)
                    lastWaiter = null;
                first.nextWaiter = null;
            } while (!transferForSignal(first) && (first = firstWaiter) != null);
        }

        /**
         * Removes and transfers all nodes.
         * 
         * @param first (non-null) the first node on condition queue
         */
        private void doSignalAll(Node first) {
            lastWaiter = firstWaiter = null;
            do {
                Node next = first.nextWaiter;
                first.nextWaiter = null;
                transferForSignal(first);
                first = next;
            } while (first != null);
        }

        /**
         * Unlinks cancelled waiter nodes from condition queue. Called only while
         * holding lock. This is called when cancellation occurred during condition
         * wait, and upon insertion of a new waiter when lastWaiter is seen to have been
         * cancelled. This method is needed to avoid garbage retention in the absence of
         * signals. So even though it may require a full traversal, it comes into play
         * only when timeouts or cancellations occur in the absence of signals. It
         * traverses all nodes rather than stopping at a particular target to unlink all
         * pointers to garbage nodes without requiring many re-traversals during
         * cancellation storms.
         */
        private void unlinkCancelledWaiters() {
            Node t = firstWaiter;
            Node trail = null;
            while (t != null) {
                Node next = t.nextWaiter;
                if (t.waitStatus != Node.CONDITION) {
                    t.nextWaiter = null;
                    if (trail == null)
                        firstWaiter = next;
                    else
                        trail.nextWaiter = next;
                    if (next == null)
                        lastWaiter = trail;
                } else
                    trail = t;
                t = next;
            }
        }

        // public methods

        /**
         * Moves the longest-waiting thread, if one exists, from the wait queue for this
         * condition to the wait queue for the owning lock.
         *
         * @throws IllegalMonitorStateException if {@link #isHeldExclusively} returns
         *                                      {@code false}
         */
        public final void signal() {
            if (!isHeldExclusively())
                throw new IllegalMonitorStateException();
            Node first = firstWaiter;
            if (first != null)
                doSignal(first);
        }

        /**
         * Moves all threads from the wait queue for this condition to the wait queue
         * for the owning lock.
         *
         * @throws IllegalMonitorStateException if {@link #isHeldExclusively} returns
         *                                      {@code false}
         */
        public final void signalAll() {
            if (!isHeldExclusively())
                throw new IllegalMonitorStateException();
            Node first = firstWaiter;
            if (first != null)
                doSignalAll(first);
        }

        /**
         * Implements uninterruptible condition wait.
         * <ol>
         * <li>Save lock state returned by {@link #getState}.
         * <li>Invoke {@link #release} with saved state as argument, throwing
         * IllegalMonitorStateException if it fails.
         * <li>Block until signalled.
         * <li>Reacquire by invoking specialized version of {@link #acquire} with saved
         * state as argument.
         * </ol>
         */
        public final void awaitUninterruptibly() {
            Node node = addConditionWaiter();
            int savedState = fullyRelease(node);
            boolean interrupted = false;
            while (!isOnSyncQueue(node)) {
                LockSupport.park(this);
                if (Thread.interrupted())
                    interrupted = true;
            }
            if (acquireQueued(node, savedState) || interrupted)
                selfInterrupt();
        }

        /*
         * For interruptible waits, we need to track whether to throw
         * InterruptedException, if interrupted while blocked on condition, versus
         * reinterrupt current thread, if interrupted while blocked waiting to
         * re-acquire.
         */

        /** Mode meaning to reinterrupt on exit from wait */
        private static final int REINTERRUPT = 1;
        /** Mode meaning to throw InterruptedException on exit from wait */
        private static final int THROW_IE = -1;

        /**
         * Checks for interrupt, returning THROW_IE if interrupted before signalled,
         * REINTERRUPT if after signalled, or 0 if not interrupted.
         */
        private int checkInterruptWhileWaiting(Node node) {
            return Thread.interrupted() ? (transferAfterCancelledWait(node) ? THROW_IE : REINTERRUPT) : 0;
        }

        /**
         * Throws InterruptedException, reinterrupts current thread, or does nothing,
         * depending on mode.
         */
        private void reportInterruptAfterWait(int interruptMode) throws InterruptedException {
            if (interruptMode == THROW_IE)
                throw new InterruptedException();
            else if (interruptMode == REINTERRUPT)
                selfInterrupt();
        }

        /**
         * Implements interruptible condition wait.
         * <ol>
         * <li>If current thread is interrupted, throw InterruptedException.
         * <li>Save lock state returned by {@link #getState}.
         * <li>Invoke {@link #release} with saved state as argument, throwing
         * IllegalMonitorStateException if it fails.
         * <li>Block until signalled or interrupted.
         * <li>Reacquire by invoking specialized version of {@link #acquire} with saved
         * state as argument.
         * <li>If interrupted while blocked in step 4, throw InterruptedException.
         * </ol>
         */
        public final void await() throws InterruptedException {
            if (Thread.interrupted())
                throw new InterruptedException();
            Node node = addConditionWaiter();
            int savedState = fullyRelease(node);
            int interruptMode = 0;
            while (!isOnSyncQueue(node)) {
                LockSupport.park(this);
                if ((interruptMode = checkInterruptWhileWaiting(node)) != 0)
                    break;
            }
            if (acquireQueued(node, savedState) && interruptMode != THROW_IE)
                interruptMode = REINTERRUPT;
            if (node.nextWaiter != null) // clean up if cancelled
                unlinkCancelledWaiters();
            if (interruptMode != 0)
                reportInterruptAfterWait(interruptMode);
        }

        /**
         * Implements timed condition wait.
         * <ol>
         * <li>If current thread is interrupted, throw InterruptedException.
         * <li>Save lock state returned by {@link #getState}.
         * <li>Invoke {@link #release} with saved state as argument, throwing
         * IllegalMonitorStateException if it fails.
         * <li>Block until signalled, interrupted, or timed out.
         * <li>Reacquire by invoking specialized version of {@link #acquire} with saved
         * state as argument.
         * <li>If interrupted while blocked in step 4, throw InterruptedException.
         * </ol>
         */
        public final long awaitNanos(long nanosTimeout) throws InterruptedException {
            if (Thread.interrupted())
                throw new InterruptedException();
            Node node = addConditionWaiter();
            int savedState = fullyRelease(node);
            final long deadline = System.nanoTime() + nanosTimeout;
            int interruptMode = 0;
            while (!isOnSyncQueue(node)) {
                if (nanosTimeout <= 0L) {
                    transferAfterCancelledWait(node);
                    break;
                }
                if (nanosTimeout >= spinForTimeoutThreshold)
                    LockSupport.parkNanos(this, nanosTimeout);
                if ((interruptMode = checkInterruptWhileWaiting(node)) != 0)
                    break;
                nanosTimeout = deadline - System.nanoTime();
            }
            if (acquireQueued(node, savedState) && interruptMode != THROW_IE)
                interruptMode = REINTERRUPT;
            if (node.nextWaiter != null)
                unlinkCancelledWaiters();
            if (interruptMode != 0)
                reportInterruptAfterWait(interruptMode);
            return deadline - System.nanoTime();
        }

        /**
         * Implements absolute timed condition wait.
         * <ol>
         * <li>If current thread is interrupted, throw InterruptedException.
         * <li>Save lock state returned by {@link #getState}.
         * <li>Invoke {@link #release} with saved state as argument, throwing
         * IllegalMonitorStateException if it fails.
         * <li>Block until signalled, interrupted, or timed out.
         * <li>Reacquire by invoking specialized version of {@link #acquire} with saved
         * state as argument.
         * <li>If interrupted while blocked in step 4, throw InterruptedException.
         * <li>If timed out while blocked in step 4, return false, else true.
         * </ol>
         */
        public final boolean awaitUntil(Date deadline) throws InterruptedException {
            long abstime = deadline.getTime();
            if (Thread.interrupted())
                throw new InterruptedException();
            Node node = addConditionWaiter();
            int savedState = fullyRelease(node);
            boolean timedout = false;
            int interruptMode = 0;
            while (!isOnSyncQueue(node)) {
                if (System.currentTimeMillis() > abstime) {
                    timedout = transferAfterCancelledWait(node);
                    break;
                }
                LockSupport.parkUntil(this, abstime);
                if ((interruptMode = checkInterruptWhileWaiting(node)) != 0)
                    break;
            }
            if (acquireQueued(node, savedState) && interruptMode != THROW_IE)
                interruptMode = REINTERRUPT;
            if (node.nextWaiter != null)
                unlinkCancelledWaiters();
            if (interruptMode != 0)
                reportInterruptAfterWait(interruptMode);
            return !timedout;
        }

        /**
         * Implements timed condition wait.
         * <ol>
         * <li>If current thread is interrupted, throw InterruptedException.
         * <li>Save lock state returned by {@link #getState}.
         * <li>Invoke {@link #release} with saved state as argument, throwing
         * IllegalMonitorStateException if it fails.
         * <li>Block until signalled, interrupted, or timed out.
         * <li>Reacquire by invoking specialized version of {@link #acquire} with saved
         * state as argument.
         * <li>If interrupted while blocked in step 4, throw InterruptedException.
         * <li>If timed out while blocked in step 4, return false, else true.
         * </ol>
         */
        public final boolean await(long time, TimeUnit unit) throws InterruptedException {
            long nanosTimeout = unit.toNanos(time);
            if (Thread.interrupted())
                throw new InterruptedException();
            Node node = addConditionWaiter();
            int savedState = fullyRelease(node);
            final long deadline = System.nanoTime() + nanosTimeout;
            boolean timedout = false;
            int interruptMode = 0;
            while (!isOnSyncQueue(node)) {
                if (nanosTimeout <= 0L) {
                    timedout = transferAfterCancelledWait(node);
                    break;
                }
                if (nanosTimeout >= spinForTimeoutThreshold)
                    LockSupport.parkNanos(this, nanosTimeout);
                if ((interruptMode = checkInterruptWhileWaiting(node)) != 0)
                    break;
                nanosTimeout = deadline - System.nanoTime();
            }
            if (acquireQueued(node, savedState) && interruptMode != THROW_IE)
                interruptMode = REINTERRUPT;
            if (node.nextWaiter != null)
                unlinkCancelledWaiters();
            if (interruptMode != 0)
                reportInterruptAfterWait(interruptMode);
            return !timedout;
        }

        // support for instrumentation

        /**
         * Returns true if this condition was created by the given synchronization
         * object.
         *
         * @return {@code true} if owned
         */
        final boolean isOwnedBy(AbstractQueuedSynchronizer sync) {
            return sync == AbstractQueuedSynchronizer.this;
        }

        /**
         * Queries whether any threads are waiting on this condition. Implements
         * {@link AbstractQueuedSynchronizer#hasWaiters(ConditionObject)}.
         *
         * @return {@code true} if there are any waiting threads
         * @throws IllegalMonitorStateException if {@link #isHeldExclusively} returns
         *                                      {@code false}
         */
        protected final boolean hasWaiters() {
            if (!isHeldExclusively())
                throw new IllegalMonitorStateException();
            for (Node w = firstWaiter; w != null; w = w.nextWaiter) {
                if (w.waitStatus == Node.CONDITION)
                    return true;
            }
            return false;
        }

        /**
         * Returns an estimate of the number of threads waiting on this condition.
         * Implements
         * {@link AbstractQueuedSynchronizer#getWaitQueueLength(ConditionObject)}.
         *
         * @return the estimated number of waiting threads
         * @throws IllegalMonitorStateException if {@link #isHeldExclusively} returns
         *                                      {@code false}
         */
        protected final int getWaitQueueLength() {
            if (!isHeldExclusively())
                throw new IllegalMonitorStateException();
            int n = 0;
            for (Node w = firstWaiter; w != null; w = w.nextWaiter) {
                if (w.waitStatus == Node.CONDITION)
                    ++n;
            }
            return n;
        }

        /**
         * Returns a collection containing those threads that may be waiting on this
         * Condition. Implements
         * {@link AbstractQueuedSynchronizer#getWaitingThreads(ConditionObject)}.
         *
         * @return the collection of threads
         * @throws IllegalMonitorStateException if {@link #isHeldExclusively} returns
         *                                      {@code false}
         */
        protected final Collection<Thread> getWaitingThreads() {
            if (!isHeldExclusively())
                throw new IllegalMonitorStateException();
            ArrayList<Thread> list = new ArrayList<Thread>();
            for (Node w = firstWaiter; w != null; w = w.nextWaiter) {
                if (w.waitStatus == Node.CONDITION) {
                    Thread t = w.thread;
                    if (t != null)
                        list.add(t);
                }
            }
            return list;
        }
    }

    
    // Unsafe类实例
    private static final Unsafe unsafe = Unsafe.getUnsafe();
    // state内存偏移地址
    private static final long stateOffset;
    // head内存偏移地址
    private static final long headOffset;
    // tail内存偏移地址
    private static final long tailOffset;
    private static final long waitStatusOffset;
    // next内存偏移地址
    private static final long nextOffset;

    // 静态初始化块
    static {
        try {
            stateOffset = unsafe.objectFieldOffset(AbstractQueuedSynchronizer.class.getDeclaredField("state"));
            headOffset = unsafe.objectFieldOffset(AbstractQueuedSynchronizer.class.getDeclaredField("head"));
            tailOffset = unsafe.objectFieldOffset(AbstractQueuedSynchronizer.class.getDeclaredField("tail"));
            waitStatusOffset = unsafe.objectFieldOffset(Node.class.getDeclaredField("waitStatus"));
            nextOffset = unsafe.objectFieldOffset(Node.class.getDeclaredField("next"));

        } catch (Exception ex) {
            throw new Error(ex);
        }
    }

    /**
     * CAS head field. Used only by enq.
     */
    private final boolean compareAndSetHead(Node update) {
        return unsafe.compareAndSwapObject(this, headOffset, null, update);
    }

    /**
     * CAS tail field. Used only by enq.
     */
    private final boolean compareAndSetTail(Node expect, Node update) {
        return unsafe.compareAndSwapObject(this, tailOffset, expect, update);
    }

    /**
     * CAS waitStatus field of a node.
     */
    private static final boolean compareAndSetWaitStatus(Node node, int expect, int update) {
        return unsafe.compareAndSwapInt(node, waitStatusOffset, expect, update);
    }

    /**
     * CAS next field of a node.
     */
    private static final boolean compareAndSetNext(Node node, Node expect, Node update) {
        return unsafe.compareAndSwapObject(node, nextOffset, expect, update);
    }
}
