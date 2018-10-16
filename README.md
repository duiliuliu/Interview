[阅览](https://duiliuliu.github.io/Interview/)

> # 目录

- [项目经验](#项目经验)
- [操作系统](#操作系统)
- [计算机网络](#计算机网络)
- [数据结构&算法](#数据结构&算法)
- [数据库](#数据库)
- [设计模式](#设计模式)
- [java编程基础](#java编程基础)
- [java面向对象](#java面向对象)
- [JDBC](#JDBC)
- [java集合](#java集合)
- [javaIO](#javaIO)
- [javaNIO、AIO](#javaNIO、AIO)
- [java反射](#java反射)
- [java并发编程](#java并发编程)
- [JVM](#JVM)
- [分布式](#分布式)
- [MySql](#MySql)
- [MongoDB](#MongoDB)
- [Redis](#Redis)
- [ElasticSearch](#ElasticSearch)
- [Python](#Python)
- [JavaScript](#JavaScript)
- [Shell](#Shell)
- [Spring](#Spring)
- [面试题](#面试题)
- [其他](#其他)

> # 正文



## 项目经验

我此刻参与的项目吧。<br>
我在搜索引擎部分，正在做的搜索引擎的后台管理。前端使用 react,后端使用 springBoot,数据在 elasticsearch 中请求。服务器使用 node 做代理转发请求给 springboot。
业务中，对于搜索关键字及其推荐词、同义词，是可以通过管理平台自定义增改删的，其中热更新是一个难点。我们解决的方式是：开发一个 es 的插件，由 es 对 aws3 进行定时扫描，有新的更新记录，则进行由插件进行读取更新数据到 es 集群中。
我在其中做的是管理后台的自动化测试，以及修改相应测试出的 bug。
做了一个给客户展示搜索界面的 demo，前端+后端。
做了一个快速启动的 shell 脚本，主要内容是对整个部署在服务器中项目的各部分依次启动。先对插件的打包，并移动到 es 服务器插件目录下、初始化 es 数据、启动 springboot(docker)、启动代理。

- 可能遇到的问题

  - es 版本为 5.6.2
  - 用的是 es 提供的 java 包，请求为 restClient_searchAdmin.performRequest(method,url,Collections.<String,String>emptyMap(),entity)
  - es 建表语句
  - es 增删改改查


## 操作系统

#### 进程、线程

- 进程：进程是正在运行的程序的实例(an instance of a computer program that is being executed)

  ![进程模型](./images/进程.png)

- 线程

  ![进程模型](./images/线程.png)

所以呢，进程是资源分配的基本单位，而线程是运行与调度的基本单位。

进程有着自己独立的地址空间，是不能共享内存的，而线程之间可以。

线程间有着共享内存，通信很方便。

**进程间通信(IPC，Interprocess communication)**：

1.  管道
    <br>它可以看作是特殊的文件，对于它的读写也可以使用普通的 read、write、open 等函数，不过它是只存在与内存中的。 - 普通管道(PIPE):通常有两种限制,一是单工,只能单向传输;二是只能在父子或者兄弟进程间使用.

        - 流管道(s_pipe):去除了第一种限制,为半双工，可以双向传输.

            ![流管道模型](./images/普通管道.png)

        如图,数据流从父进程流向子进程。
        若要数据从子进程流向父进程，则关闭父进程的写端与子进程的读端，打开子进程的写端与父进程的读端，即可。

        - 命名管道(name_pipe):去除了第二种限制,可以在许多并不相关的进程之间进行通讯.
        有路径名与之相关联，它以一种特殊设备文件形式存在于文件系统中.

2.  消息队列

消息队列，是消息的链接表，存放在内核中。一个消息队列由一个标识符(即队列 ID)来标识。

    特点：
    1. 消息队列是面向记录的，其中的消息具有特定的格式以及特定的优先级。
    2. 消息队列独立于发送与接收进程。进程终止时，消息队列及其内容并不会被删除。
    3. 消息队列可以实现消息的随机查询,消息不一定要以先进先出的次序读取,也可以按消息的类型读取。

3. 信号量

信号量(semaphore)与已经介绍过的 IPC 结构不同，它是一个计数器。信号量用于实现进程间的互斥与同步，而不是用于存储进程间通信数据。

    特点
    1. 信号量用于进程间同步，若要在进程间传递数据需要结合共享内存。
    2. 信号量基于操作系统的 PV 操作，程序对信号量的操作都是原子操作。
    3. 每次对信号量的 PV 操作不仅限于对信号量值加 1 或减 1，而且可以加减任意正整数。
    4. 支持信号量组。

4. 共用内存

共享内存(Shared Memory)，指两个或多个进程共享一个给定的存储区。

    特点
    1. 共享内存是最快的一种 IPC，因为进程是直接对内存进行存取。
    2. 因为多个进程可以同时操作，所以需要进行同步。
    3. 信号量+共享内存通常结合在一起使用，信号量用来同步对共享内存的访问。

5. TCP

通过网络 socket 与其他进程进行数据交流

#### 死锁、饥饿

- 死锁(deadlock)

指的是两个或者两个以上的进程相互竞争系统资源，导致进程永久阻塞。

- 饥饿(starvation)

指的是等待时间已经影响到进程运行，此时成为饥饿现象。如果等待时间过长，导致进程使命已经没有意义时，称之为“饿死”。

#### 生产者消费者模型

- 问题的核心是： 1.要保证不让生产者在缓存还是满的时候仍然要向内写数据; 2.不让消费者试图从空的缓存中取出数据。

多个生产者进行生产，多个消费者进行消费，缓冲区作为双方的纽带(阻塞队列)
当生产者生产较多的产品，超过缓冲区的容量时，阻塞生产者进程
当缓冲区中无产品时，消费者进行阻塞

#### 例题

有关线程说法正确的是( A C )

A 线程是程序的多个顺序的流动态执行

B 线程有自己独立的地址空间

C 线程不能够独立执行，必须依存在应用程序中,由应用程序提供多个线程执行控制

D 线程是系统进行资源分配和调度的一个独立单位


## 计算机网络

#### osi 七层模型、TCP/IP 模型

- 应用层 HTTP、SMTP 、telnet、DHCP、PPTP
- 传输层 判断数据包的可靠性，错误重传机制，TCP/UDP 协议
  - TCP 协议 基于连接的
- 网络层 路由 ip 协议，标识各个网络节点
- 数据链路层 节点到节点数据包的传递,并校验数据包的安全性

![网络模型](./images/网络模型.png)

#### 网络传输

- 不可靠
  - 丢包、重复包
  - 出错
  - 乱序
- 不安全
  - 中间人攻击
  - 窃取
  - 篡改

#### TCP

TCP(Transmission Control Protocol 传输控制协议)是一种面向连接的、可靠的、基于字节流的传输层通信协议

    - 在数据正确性与合法性上，TCP用一个校验和函数来检验数据是否有错误，在发送和接收时都要计算校验和;同时可以使用md5认证对数据进行加密。
    - 在保证可靠性上，采用超时重传和捎带确认机制。
    - 在流量控制上，采用滑动窗口协议，协议中规定，对于窗口内未经确认的分组需要重传。
    - 在拥塞控制上，采用TCP拥塞控制算法(也称AIMD算法)。该算法主要包括三个主要部分：1)加性增、乘性减;2)慢启动;3)对超时事件做出反应。


    * TCP 是面向连接的传输层协议
    * 每一条 TCP 连接只能有两个端点(endpoint),每一条 TCP 连接只能是点对点的(一对一)
    * TCP 提供可靠交付的服务
    * TCP 提供全双工通信
    * 面向字节流

- 报头

  ![TCP报文头模型](./images/TCP报文头模型.png)

  我们来分析分析每部分的含义和作用

  - 源端口号/目的端口号: 各占两个字节，端口是传输层与应用层的服务接口
  - 32 位序号: 占 4 字节，连接中传送的数据流中的每一个字节都编上一个序号。序号字段的值则指的是本报文段所发送的数据的第一个字节的序号。
  - 确认序号: 占 4 字节,是期望收到对方的下一个报文段的数据的第一个字节的序号
  - 4 位首部长度: 表示该 tcp 报头有多少个 4 字节(32 个 bit)
  - 6 位保留: 占 6 位,保留为今后使用,但目前应置为 0
  - 6 位标志位：

    URG: 标识紧急指针是否有效

    ACK: 标识确认序号是否有效 。只有当 ACK=1 时确认号字段才有效.当 ACK=0 时,确认号无效

    PSH: 用来提示接收端应用程序立刻将数据从 tcp 缓冲区读走

    RST: 要求重新建立连接. 我们把含有 RST 标识的报文称为复位报文段

    SYN: 请求建立连接. 我们把含有 SYN 标识的报文称为同步报文段 。　同步 SYN = 1 表示这是一个连接请求或连接接受报文

    FIN: 通知对端, 本端即将关闭. 我们把含有 FIN 标识的报文称为结束报文段。FIN=1 表明此报文段的发送端的数据已发送完毕,并要求释放运输连接

  - 16 位窗口大小:
  - 16 位检验和: 由发送端填充, 检验形式有 CRC 校验等. 如果接收端校验不通过, 则认为数据有问题. 此处的校验和不光包含 TCP 首部, 也包含 TCP 数据部分.
  - 16 位紧急指针: 用来标识哪部分数据是紧急数据.
    选项和数据暂时忽略

- 三次握手

  客户端 CLOSE 状态，服务器 LISTEN(监听)状态<br>
  客户端向服务器主动发出连接请求, 服务器被动接受连接请求

  客户端 -- > 服务端 发送 SYN 包，请求建立连接<br>
  服务端 -- > 客户端 发送 SYN+ACK 包，确认收到连接请求，并回复响应<br>
  客户端 -- > 服务端 发送 ACK 包，确认回复，建立连接

  ![TCP三次握手模型](./images/TCP三次握手模型.gif)

  **为什么不用两次?**

        主要是为了防止已经失效的连接请求报文突然又传送到了服务器，从而产生错误。

        如果使用的是两次握手建立连接，那么假设：
            客户端发送的第一个请求在网络中滞留较长时间，由于客户端迟迟没有收到服务端的回应，重新发送请求报文，服务器收到后与客户端建立连接，传输数据，然后关闭连接。此时滞留的那一次请求因为网络通畅了，到达了服务器，这个报文本应是失效的，但是两次握手的机制将会让客户端与服务端再次建立连接，导致不必要的错误和资源浪费。

            如果采用的是三次握手，就算失效的报文传送过来，服务端就收到报文并回复了确认报文，但是客户端不会再次发送确认。由于服务端没有收到确认，就知道客户端没有连接。

  **为什么不用四次？**

        因为三次就足够了，四次就多余了。

- 四次挥手

  客户端和服务器都是处于 ESTABLISHED 状态<br>
  客户端主动断开连接，服务器被动断开连接

  客户端 -- > 服务端 发送 FIN=1(连接释放报文)，并且停止发送数据，进入 FIN-WAIT-1(终止等待 1)状态<br>
  服务端 -- > 客户端 发送确认报文 ACK，并带上自己的序列号 seq=v，进入了 CLOSE-WAIT(关闭等待)状态<br>
  客户端 收到服务器确认，进入 FIN-WAIT-2(终止等待 2)状态，等待服务器发送连接释放报文(在这之前还需要接受服务器发送的最终数据)<br>
  服务端 -- > 向客户端发送连接释放报文，FIN=1，确认序号为 v+1,服务器就进入了 LAST-ACK(最后确认)状态，等待客户端的确认。<br>
  客户端 -- > 服务端 发送 ACK 包，确认,断开连接<br>
  服务器 收到确认,断开连接

  ![TCP四次挥手模型](./images/TCP四次挥手模型.gif)

  **为什么最后客户端还要等待 2\*MSL 的时间呢?**

        MSL(Maximum Segment Lifetime)，TCP允许不同的实现可以设置不同的MSL值

        1. 保证客户端发送的最后一个ACK报文能够到达服务器，如果ACK报文丢失，服务器未收到确认，会再次发送，而客户端就能在这个2MSL时间段内收到重传的报文，接着给出回应报文，并且会重启2MSL计时器。

        2. 防止类似与"三次握手"中提到了的"已经失效的连接请求报文段"出现在本连接中。客户端发送完最后一个确认报文后，在这个2MSL时间中，就可以使本连接持续的时间内所产生的所有报文段都从网络中消失。这样新的连接中不会出现旧连接的请求报文。(简单讲，就是这段时间所有的报文段会慢慢消失，以至于其余的连接请求是不会有正确确认的)

  **为什么建立连接是三次握手，关闭连接确是四次挥手呢？**

        断开连接时，服务器收到对方的FIN包后，仅仅表示对方不在发送数据了，但是还能接收数据。所以己方还可能有数据发送给客户端，因此己方ACK与FIN包会分开发送，从而导致多了一次。

  **如果已经建立了连接, 但是客户端突发故障了怎么办?**

        TCP设有一个保活计时器。
        显然，如果客户端发生故障，服务器不能一直等下去，浪费资源。
        服务器每收到一次客户端的请求都会重置复位这个计时器，时间通常设置为2小时。两小时没有收到客户端请求，会发送一个探测报文段，以后每隔75分钟发送一次，连续10次后，客户端仍没反应，则认为客户端故障，关闭连接。

- 确认应答机制(ACK 机制)

  TCP 将每个字节的数据都进行了编号, 即为序列号

  ![序列号模型](./images/序列号模型.png)

  每一个 ACK 都带有对应的确认序列号, 意思是告诉发送者, 我已经收到了哪些数据; 下一次你要从哪里开始发.

  比如, 客户端向服务器发送了 1005 字节的数据, 服务器返回给客户端的确认序号是 1003, 那么说明服务器只收到了 1-1002 的数据.
  1003, 1004, 1005 都没收到.
  此时客户端就会从 1003 开始重发.

- 超时重传机制

  特定的时间间隔内，发生网络丢包(到客户端的数据包丢失或者客户端确认的 ACK 包丢失)，主机未收到另一端的确认应答，就会进行重发。

  这种情况下, 主机 B 会收到很多重复数据.
  那么 TCP 协议需要识别出哪些包是重复的, 并且把重复的丢弃.
  这时候利用前面提到的序列号, 就可以很容易做到去重.

  **超时时间如何确定?**

  TCP 为了保证任何环境下都能保持较高性能的通信, 因此会动态计算这个最大超时时间.

- 滑动窗口

- 流量控制

- 拥塞控制

- 延迟应答


## 数据结构&算法

> https://github.com/xiufengcheng/DATASTRUCTURE

#### 线性表

- 是 n(n≥0)个相同类型的数据元素构成的有限序列。

  ![线性表](./images/线性表.png)

  - 线性表除第一个和最后一个元素之外，每一格数据元素只有一个前驱和一个后继
  - 分有序线性表(顺序表)和无序线性表(链表)，有序线性表的元素按照值的递增顺序排列;无序线性表在元素的值与位置之间没有特殊的联系

- 顺序表

  顺序表：一组连续地址存储线性表的各个元素

  求址：
  每个元素占用 k 个存储单元，则
  LOC(ai)= LOC(a1)+(i-1)\*k (1≤i ≤n)
  LOC(ai+1)= LOC(ai)+k (1≤i ≤n)

  **注意**

        访问顺序表中元素的时间都相等,具有这一特点的称为 随机存取结构

- 链表

  链表：一组随机地址存储线性表各个元素，元素之间使用指针连接起来

  单链表：各元素只有一个指针域的链表(只含有一个指针域)

  头结点在链表中并不是必须的，仅仅是为了操作上的方便。

  双向链表: 有两个指针域的链表，一个指向前驱，另一个指向后继

        节点p既指向前驱，又指向后继。因此可随意在其上向前或向后移动，使得操作更加容易。

        操作上比单链表更加便利，但存储开销增大。

        双(向)链表是非循环的，有首尾节点，注意与双向循环链表区别开来

  循环链表(约瑟夫环)：

        尾部指针的next指向头指针,形成环状数据结构

        一般有两种形式的循环链表，即单向循环链表和双向循环链表。

        单向循环链表中，表尾结点的指针域不为空，回指第一个结点，整个链表形成一个环。

        在双向循环链表中，除了表尾结点的后继指针域回指第一个结点外，同时表头结点的前驱指针域回指表尾结点，这样在链表中构成了两个环。

#### 栈

可用顺序表或链表实现。特点：后进先出 (LIFO)

- 多栈共享邻接空间

  - 定义：即两个栈栈底位置为两端，两个栈顶在中间不断变化，由两边往中间延伸。动态变化(想象把两个花瓶口对口连起来)。
  - 目的：若多个栈同时使用，可能出现一个栈的空间被占满而其它栈空间还有大量剩余，因此采用这个方法。
  - 特点：设有 top1 和 top2 两个指针，top1 = -1 表示左栈为空，top2 >= StackSize 表示右栈为空; top1 + 1 >= top2 时表示栈满。

    ![两栈共享邻接空间](./images/两栈共享邻接空间.png)

#### 队列

- 先进先出
- 顺序循环队列

  - 初始状态为 front = rear = 0
  - 入队时，把元素插到 rear 指示位置，然后 rear++
  - 出队时，把 front 指示位置元素删除，然后 front++

  - **判断空**

    1. 少用一个存储单元

       插入时，判断下一元素是否为 front，如果是，则停止插入。

       判满条件：(rear+1) mod maxsize = front

       判空条件：rear == front

    2. 设置一个标志位

       即设置一个标志位 tag=0,当进队成功时 tag=1;当出队成功时 tag=0

       判满条件：(rear==front) && (tag==1)

       判空条件：(rear==front) && (tag==0)

    3. 设置计数器

       即设置一个计数器 count=0，当进队成功时 count++;当出队成功时 count--

       判满条件：(rear==front) && count>0\*\*

       判空条件：count == 0

#### 查找

#### 排序

- 快速排序

  快排是不稳定的排序，最好的情况对半分，时间复杂度为 nlogn
  最坏的情况选择最小或最大的元素作为切分元素，时间复杂度为 n^2

  快排是内部进行比较交换，所以不需要额外的空间，但是递归调用会占用栈空间

  ArrayList.sort()方法使用的是快排

  对快排的优化：

  1. 对于小数组或者切分为小数组时可以用插入排序，效率较高
  2. 可以选择三个数，从中取中值
  3. 对于重复值多的序列，可以三向切分，大于、等于、小于


    补充：
        快速排序通过一个切分元素将数组分为两个子数组，左子数组小于等于切分元素，右子数组大于等于切分元素。再将两个子数组排序就是整个数组排序了

        ```
        public class QuickSort<T extends Comparable<T>> exths Sort<T>{

            @override
            public void sort(T[] nums){
                shuffle(nums);
                sort(nums,0,nums.length-1);
            }

            public void sort(t[] nums, int l,int h){
                if(l == h){
                    return
                }
                int mid =partition(nums, l, h);
                sort(nums, l, mid+1);
                sort(nums, mid-1, h);
            }

            public void shuffle(T[] nums){
                List<Comparable> list = Arrays.asList(nums);
                Collections.shuffle(lsit);
                list.toArray(nums);
            }
        }
        ```

        取a[l]元素作为切分元素，定义i指针指向l，从左向右(i++)扫描数组，直到找到第一个大于等于他的元素，接着从右往左扫描，遇到第一个小于等于他的元素，交换这两个元素，不断重复这个过程，直到两个指针相遇。最后将切分元素a[l]与a[j]交换。这样保证了切分元素左面都是小于等于他的元素，右面都是大于等于他的元素。

        ```
        private int partition(T[] nums, int l, int h){
            T v = T[l];
            int i = l, j = h + 1;
            while(true){
                while(less(nums[++i], v) && i!=h);
                while(less(nums[--j], v) && j!=l);
                if (i>=j){
                    break;
                }
                swap(nums,i,j);
            }
            swap(nums,l,j);
            return j;
        }
        ```

    排序的话，快排是不稳定的排序，同样不稳定排序有 选择排序、希尔排序、堆排序

    Java中Arrays.sort()使用的是快排

- 堆排序

  堆可以看做是一颗树，节点在树中的高度可以被定义为从本节点到叶子节点的最长简单下降路径上边的数目；定义树的高度为树根的高度。我们将看到，堆结构上的一些基本操作的运行时间至多是与树的高度成正比，为 O(lgn)

  1. 先将初始数列建成一个大根堆，此堆为初始的无序区
  2. 将堆顶元素 R[1]与最后一个 R[n]交换，此事得到新的无序区(R1,R2,......Rn-1)和新的有序区(Rn)
  3. 由于交换后新的堆顶 R[1]可能违反堆的性质，因此需要对当前无序区(R1,R2,......Rn-1)调整为新堆，然后再次将 R[1]与无序区最后一个元素交换，得到新的无序区(R1,R2....Rn-2)和新的有序区(Rn-1,Rn)。不断重复此过程直到有序区的元素个数为 n-1，则整个排序过程完成

  初始大顶堆：从最后一个有子节点的开始网上调整最大堆 从下往上 从右往左
  调整堆： 堆顶元素 R[1]与最后一个元素 R[n]交换，交换后堆长度减一
  即每次调整都是从父节点、左孩子节点、右孩子节点三者中选择最大者跟父节点进行交换(交换之后可能造成被交换的孩子节点不满足堆的性质，因此每次交换之后要重新对被交换的孩子节点进行调整)。有了初始堆之后就可以进行排序了。 从上往下 从左往右

  堆排序 平均时间复杂度 nlog2(n) 空间复杂度 o(1)

  Java 中 PriorityQueue 实现依赖小根堆

  实现：

        首先初始化堆，从最后一个有子节点的开始网上调整最大堆

        ```
        private static void maxHeapify(int[] nums,int heapSize,int index){
            int left = getChildLeftIndex();
            int right = getChildRightIndex();
            int largest = index;

            if (left < index && data[index] < data[left])
                larget = left;
            if (right < index && data[largest] < data[right])
                largest = right;

            if (largest!=index){
                swap(data,index,largest);
                //得到最大值后可能需要交换，如果交换了，其子节点可能就不是最大堆了，需要重新调整
                maxHeapify(data, heapSize, largest);
            }
        }
        ```

        依次从下往上 从右往左，调整堆

        ```
        private static void buildMaxHeapify(int[] data) {
            int startIndex = getParentIndex(data.length - 1);
            for (int i = startIndex; i >= 0; i--) {
                maxHeapify(data, data.length, i);
            }
        }
        ```

        进行堆排序

        ```
        private static void heapSort(int[] data) {
            for (int i = data.length - 1; i > 0; i--) {
                ArrayMethod.swap(data, 0, i);
                maxHeapify(data, i, 0);
            }
        }
        ```

#### 搜索二叉树

#### 平衡二叉树

#### 红黑树

AVL 是高度平衡的树，插入删除都需要大量的左旋右旋进行重新平衡
而红黑树放弃了极致平衡以追求插入删除时的效率

红黑树的特点：

1.  节点非黑即红
2.  叶子节点为黑色
3.  到叶子节点的任意路径下的黑色节点个数一致
4.  红色节点的两个子节点都为黑色

#### b 树

#### 用两个栈实现一个队列或者俩个队列实现一个栈

1. 区别与联系

相同点：
(1)栈和队列都是控制访问点的线性表;
(2)栈和队列都是允许在端点处进行数据的插入和删除的数据结构;

不同点：
(1)栈遵循“后进先出(LIFO)”的原则，即只能在该线性表的一头进行数据的插入和删除，该位置称为“栈顶”，而另外一头称为“栈底”;根据该特性，实现栈时用顺序表比较好;
(2)队列遵循“先进先出(FIFO)”的原则，即只能在队列的尾部插入元素，头部删除元素。根据该特性，在实现队列时用链表比较好

2. 两站实现队列
   栈是后进先出的，所以两个栈 一个栈用来入栈，而将栈中的数据再次出栈存入另一个栈中，这样经过两次后进先出， 则实现了先进先出

3. 两队列实现栈
   始终保持一个队列为空，需要出队时，队列抛出 n-1 个数据到另一队列中，只留下一个 top1 数据，这样那个队列再次 pop,pop 的数据就是队首元素了。


## 数据库

#### 范式

- 1NF 一个属性不允许在分成多个属性，即每个属性具有 **原子性**
- 2NF **完全函数依赖**，第二范式的目标就是消除函数依赖关系中左边存在的冗余属性。例如，(学号，班级)->姓名，事实上，只需要学号就能决定姓名，因此班级是冗余的，应该去掉。
- 3NF **消除传递依赖** 数据库中的属性依赖仅能依赖于主属性，不存在于其他非主属性的关联。
- BCNF
  - 所有非主属性对每一个码都是完全函数依赖
  - 所有的主属性对于每一个不包含它的码，也是完全函数依赖
  - 没有任何属性完全函数依赖于非码的任意一个组合。
  - R 属于 3NF，不一定属于 BCNF，如果 R 属于 BCNF，一定属于 3NF。
  - 例：配件管理关系模式 WPE(WNO，PNO，ENO，QNT)分别表仓库号，配件号，职工号，数量。有以下条件
    a.一个仓库有多个职工。
    b.一个职工仅在一个仓库工作。
    c.每个仓库里一种型号的配件由专人负责，但一个人可以管理几种配件。
    d.同一种型号的配件可以分放在几个仓库中。

#### 特性

- A atomicity 原子性  
  整个事务中的所有操作，要么全部完成，要么全部不完成，不可能停滞在中间某个环节。事务在执行过程中发生错误，会被回滚(Rollback)到事务开始前的状态，就像这个事务从来没有执行过一样。
- C consistency 一致性
  一个事务可以封装状态改变(除非它是一个只读的)。事务必须始终保持系统处于一致的状态，不管在任何给定的时间并发事务有多少。
- I isolation 隔离性
  隔离状态执行事务，使它们好像是系统在给定时间内执行的唯一操作。如果有两个事务，运行在相同的时间内，执行相同的功能，事务的隔离性将确保每一事务在系统中认为只有该事务在使用系统。这种属性有时称为串行化，为了防止事务操作间的混淆，必须串行化或序列化请求，使得在同一时间仅有一个请求用于同一数据。
- D durability 持久性
  在事务完成以后，该事务对数据库所作的更改便持久的保存在数据库之中，并不会被回滚。

#### 隔离级别

事务的隔离级别有四种，隔离级别高的数据库的可靠性高，但并发量低，而隔离级别低的数据库可靠性低，但并发量高，系统开销小

|                 | 脏读(dirty read) | 不可重复读(unrepeatable read) | 幻读(phantom read) |
| --------------- | ---------------- | ----------------------------- | ------------------ |
| read uncommited | y                | y                             | y                  |
| read commited   | n                | y                             | y                  |
| repearable read | n                | n                             | n                  |
| serialable      | n                | n                             | n                  |

1.  READ UNCOMMITED 未提交读 **更新的瞬间加 行级共享锁**
    事务还没提交，而别的事务可以看到他其中修改的数据的后果，-->脏读(该事物出错而回滚，其他事物已读到改变的值，发生脏读)。
2.  READ COMMITED 提交读 **对被读取的数据加 行级共享锁 读完释放** **更新的瞬间加 行级排他锁**
    其他事务只能看到已经完成的事务的结果，正在执行的，是无法被其他事务看到的。这种级别会出现读取旧数据的现象。大多数数据库系统的默认隔离级别是 READ COMMITTED
    也容易发生丢失更新： 如果 A、B 同时获取资源 X，然后事务 A 先发起更新记录 X，那么事务 B 将等待事务 A 完成，然后获得记录 X 的排他锁，进行更改，这样事务 A 的更新就会丢失。

    |                           |     |                                          |
    | ------------------------- | --- | ---------------------------------------- |
    | 事务 A                    | --- | 事务 B                                   |
    | 读取 X=100(同时上共享锁)  | --- | 读取 X=100(同时上共享锁)                 |
    | 读取成功(释放共享锁)      | --- | 读取成功(释放共享锁)                     |
    | UPDATE X=X+100 (上排他锁) | --- |                                          |
    |                           | --- | UPDATING A(等待事务 A 释放对 X 的排他锁) |
    | 事务成功(释放排他锁)X=200 | --- |                                          |
    |                           | --- | UPDATE X=X+200(成功上排他锁)             |
    | (事务 A 的更新丢失)       | --- | 事务成功(释放排他锁)X=300<br>            |

3.  REPEATBLE COMMITED 可重复读 **对被读取的数据加 行级共享锁 事务结束释放** **更新的瞬间加 行级排他锁**
    事务结束才释放行级共享锁 ，这样保证了可重复读(既是其他的事务职能读取该数据，但是不能更新该数据)。　会导致幻读
4.  SERIALIZABLE 可串行化
    SERIALIZABLE 是最高的隔离级别，它通过强制事务串行执行(注意是串行)，避免了前面的幻读情况，由于他大量加上锁(加表锁)，导致大量的请求超时，因此性能会比较底下，再特别需要数据一致性且并发量不需要那么大的时候才可能考虑这个隔离级别

- 期间造的问题
  - 丢失更新
  - 脏读(未提交读)
  - 不可重复读 一个事务在自己没有更新数据库数据的情况，同一个查询操作执行两次或多次的结果应该是一致的;如果不一致，就说明为不可重复读。
  - 幻读 事务 A 读的时候读出了 15 条记录，事务 B 在事务 A 执行的过程中 增加 了 1 条，事务 A 再读的时候就变成了 16 条，这种情况就叫做幻影读。

#### 锁机制

- 共享锁 (S 锁)
  由读表操作加上的锁，加锁后其他用户只能获取该表或行的共享锁，不能获取排它锁，也就是说只能读不能写
- 排它锁 (X 锁)
  由写表操作加上的锁，加锁后其他用户不能获取该表或行的任何锁，典型是 mysql 事务</br>

根据锁的范围，可以分为

- 表锁
  给整张表加锁
- 行锁
  给行数据加锁

因此锁可以分为表级共享锁、行级共享锁、表级排它锁、行级排它锁。

#### 存储过程

对于我们常用的关系型数据库，操作数据库的语言一般是 SQL，SQL 在执行的时候需要先编译,然后执行.

而存储过程(Stored Procedure)是一组为了完成某种特定功能的*SQL 语句集*,经编译后存储在数据库中,用户通过存储过程的名字并给定参数(如果需要参数)来调用执行它。

- 创建存储过程

  ```
  create procedure sp_name
  @[参数名] [类型],@[参数名] [类型]
  as
  begin
  .........
  end
  以上格式还可以简写成：
  create proc sp_name
  @[参数名] [类型],@[参数名] [类型]
  as
  begin
  .........
  end
  /*注：“sp_name”为需要创建的存储过程的名字，该名字不可以以阿拉伯数字开头*/
  ```

- 调用存储过程

  基本语法：exec sp_name [参数名]

- 删除存储过程

  1.基本语法：
  drop procedure sp_name

  2.注意事项
  (1)不能在一个存储过程中删除另一个存储过程，只能调用另一个存储过程

- 其他常用命令

  1.show procedure status
  显示数据库中所有存储的存储过程基本信息，包括所属数据库，存储过程名称，创建时间等

  2.show create procedure sp_name
  显示某一个 mysql 存储过程的详细信息

  3、exec sp_helptext sp_name
  显示你这个 sp_name 这个对象创建文本

#### 索引

索引(index)是帮助数据库高效获取数据的数据结构。

在数据之外，数据库系统维护着满足特定查找算法的数据结构,这些数据结构以某种方式引用(指向)数据,可以在这些数据结构上实现高级查找算法，提高查询速度，这种数据结构，就是索引。

- 索引存储分类(MySql)

  - B-Tree 索引 最常见的索引类型，大部分引擎都支持 B 树索引。
  - Hash 索引
  - R-tree 索引(空间索引) 空间索引是 MyISAM 的一种特殊索引类型，主要用于地理空间数据类型。
  - Full-text(全文索引) 全文索引也是 MyISAM 的一种特殊索引类型，主要用于全文索引，InnoDB 从 MySQL5.6 版本提供对全文索引的支持。

- B-tree 索引类型

  - 普通索引

    最基本的索引类型，而且它没有唯一性之类的限制，可以通过以下几种方式创建：

    ```
    //创建索引
    create index index_name on table_name(col1,col2...)

    //修改表
    alter table_name add index index_name (col1,col2...)

    //创建表时指定索引
    create table table_name([...],index index_name (col1,col2...))
    ```

  - UNIQUE 索引

    唯一索引

    ```
    关键字为 UNIQUE INDEX

    //创建索引
    create UNIQUE INDEX index_name on table_name(col1,col2...)

    //修改表
    alter table_name add UNIQUE INDEX index_name (col1,col2...)

    //创建表时指定索引
    create table table_name([...],UNIQUE INDEX index_name (col1,col2...))
    ```

  - 主键索引

    主键是一种唯一性索引，但它必须指定为“PRIMARY KEY”,不允许 null

    ```
    // 1. 创建表时指定
    CREATE TABLE 表名( […], PRIMARY KEY (列的列表) );
    // 2. 修改表时加入
    ALTER TABLE 表名 ADD PRIMARY KEY (列的列表);
    ```

- 删除索引

  ```
  DROP INDEX index_name ON talbe_name

  ALTER TABLE table_name DROP INDEX index_name

  ALTER TABLE table_name DROP PRIMARY KEY
  ```

- 查看索引

  ```
  mysql> show index from table_name;
  ```

- 设置索引的规则

  1. 较频繁的作为查询条件的字段
  2. 唯一性太差的字段不适合单独做索引，即使频繁作为查询条件
  3. 更新非常频繁的字段不适合作为索引

- 索引的弊端

  索引是有代价的：索引文件本身要消耗存储空间，同时索引会加重插入、删除和修改记录时的负担，另外，MySQL 在运行时也要消耗资源维护索引，因此索引并不是越多越好。

- 处理重复记录的常用操作

  ```
  // 查找重复记录
  select * from table_name where column_name in (select column in table_name group by column having count(column) > 1)

  // 删除重复记录
  delete ...

  // 查找多个字段重复记录
  select * from table_name where column_name in (select column_1,column_2 in table_name group by column_1 column_2 having count(*) > 1)
  ```

#### SQL

- 数据定义：Create Table,Alter Table,Drop Table, Craete/Drop Index
- 数据操纵：Select ,insert,update,delete
- 数据控制：grant,revoke

- 内连接/外连接

  - 内连接是保证两个表中的数据要满足连接条件


## 设计模式

> https://www.cnblogs.com/geek6/p/3951677.html <br>
 > https://github.com/CyC2018/CS-Notes/blob/master/notes/%E8%AE%BE%E8%AE%A1%E6%A8%A1%E5%BC%8F.md

#### 设计模式的六大原则

- 总原则：开闭原则(Open Close Principle)

  开闭原则就是说对扩展开放，对修改关闭。

  在程序需要进行拓展的时候，不能去修改原有的代码，而是要扩展原有代码，实现一个热插拔的效果。

1. 单一职责原则

   不要存在多于一个导致类变更的原因，也就是说每个类应该实现单一的职责，如若不然，就应该把类拆分。

2. 里氏替换原则(Liskov Substitution Principle)

   任何基类可以出现的地方，子类一定可以出现。

   在继承关系中，父类的对象如果替换为子类的对象，他原来执行的行为依然保持不变。这样来说，java 的多态正是依赖于里氏替换原则的。

3. 依赖倒转原则(Dependence Inversion Principle)

   面向接口编程，依赖于抽象而不依赖于具体。写代码时用到具体类时，不与具体类交互，而与具体类的上层接口交互。

4. 接口隔离原则(Interface Segregation Principle)

   每个接口中不存在子类用不到却必须实现的方法，如果不然，就要将接口拆分。使用多个隔离的接口，比使用单个接口(多个接口方法集合到一个的接口)要好。

5. 迪米特法则(最少知道原则)(Demeter Principle)

   一个类对自己依赖的类知道的越少越好。也就是说无论被依赖的类多么复杂，都应该将逻辑封装在方法的内部，通过 public 方法提供给外部。这样当被依赖的类变化时，才能最小的影响该类。

6. 合成复用原则(Composite Reuse Principle)

   原则是尽量首先使用合成/聚合的方式，而不是使用继承。

#### 简单工厂模式

就是建立一个工厂类，对实现了同一接口的一些类进行实例的创建

-
![简单工厂模型](./images/简单工厂模型.png)

```
public interface Sender {
    public void Send();
}

public class MailSender implements Sender {
    @Override
    public void Send() {
        System.out.println("this is mailsender!");
    }
}

public class SmsSender implements Sender {

    @Override
    public void Send() {
        System.out.println("this is sms sender!");
    }
}

//1. 依据传入的字符串创建对象
//如果传入的字符串有误，不能正确创建对象
public class SendFactory {
    public Sender produce(String type) {
        if ("mail".equals(type)) {
            return new MailSender();
        } else if ("sms".equals(type)) {
            return new SmsSender();
        } else {
            System.out.println("请输入正确的类型!");
            return null;
        }
    }
}

//2. 多个工厂方法分别创建不同的对象
public class SendFactory {
    public Sender produceMail(){
        return new MailSender();
    }
    public Sender produceSms(){
        return new SmsSender();
    }
}

//3. 多个静态方法分别创建不同的对象
//相较于上一种，不需要实例化工厂类
public class SendFactory {
    public static Sender produceMail(){
        return new MailSender();
    }
    public static Sender produceSms(){
        return new SmsSender();
    }
}
```

总体来说，工厂模式适合：凡是出现了大量的产品需要创建，并且具有共同的接口时，可以通过工厂方法模式进行创建。

#### 工厂方法模式(Factory Method)

简单工厂模式有一个问题就是，类的创建依赖工厂类，也就是说，如果想要拓展程序，必须对工厂类进行修改，这违背了闭包原则.

所以，用到工厂方法模式，创建一个工厂接口和创建多个工厂实现类，这样一旦需要增加新的功能，直接增加新的工厂类就可以了，不需要修改之前的代码。

-
![工厂方法模式](./images/工厂方法模型.png)

```
public interface Sender {
    public void Send();
}

public class MailSender implements Sender {
    @Override
    public void Send() {
        System.out.println("this is mailsender!");
    }
}

public class SmsSender implements Sender {
    @Override
    public void Send() {
        System.out.println("this is sms sender!");
    }
}

// 工厂接口
public interface Provider {
    public Sender produce();
}

public class SendMailFactory implements Provider {
    @Override
    public Sender produce(){
        return new MailSender();
    }
}

public class SendSmsFactory implements Provider{

    @Override
    public Sender produce() {
        return new SmsSender();
    }
}

// 调用时

public class Main {

    public static void main(String[] args) {
        Provider provider = new SendMailFactory();
        Sender sender = provider.produce();
        sender.Send();
    }
}
```

对于增加改变的产品，只需实现 Provider 接口，做一个相应的工厂类就行了。拓展性较好。

#### 抽象工厂模式

(_-_) 等等哈，没抽象明白！

#### 单例模式

- 适用场景：
  1. 有些类对象的创建与销毁非常耗费资源，而且有经常用到
  2. 需要生成唯一序列话的环境，如部分窗口对象，关闭后打开我们需要保持在之前的界面，
  3. 方便资源互相通信的环境， 如一个 log 日志单例类，不同地方的的调用可以将日志按顺序记录到一个文件中。
- 优点：
  1. 实现了对唯一实例的访问的可控
  2. 对于一些频繁创建和销毁的对象来说可以提高系统的性能
- 缺点：
  1. 滥用单例会带来一些负面问题，如为了节省资源将数据库连接池对象设计为单例类，可能会导致共享连接池对象的程序太多，而出现连接池溢出
  2. 如果实例化的对象长时间不被利用，系统会认为该对象是垃圾而被回收，这可能会导致对象状态的丢失
- 实现
  1. 饿汉式 初始化时直接创建一个单例对象 初始启动时创建对象延迟启动，而且对象长时间不用占用一定空间，浪费<br>
  2. 懒汉式 需要的时候再去创建。
  - 线程不安全
  - 线程安全
    - synchronized 锁住方法
    - synchronized 锁部分代码块，缩小锁粒度
    - 内部类，利用类的加载一定线程安全的原因
    - 枚举类 该实现在多次序列化再进行反序列化之后，不会得到多个实例

饿汉式

    ```
    static instance = new SingleInstance();
    ```

懒汉式(线程不安全)

    ```
    static instance = null;
    ...
    getInstance(){
        if(instance==null)
            return new SingleInstance();
    }
    ```

懒汉式加锁 synchronized(低效)

    ```
    synchronized getInsance(){
        if (instance==null){
            return new SingleInstance();
        }
    }
    ```

懒汉式双重锁检查(volatile 防止指令重排)

    ```
    static volatile instance = null;
    ...
    getInstance(){
        if(instance==null){
            synchronized(SingleInstance.class){
                if (instance==null){
                    return new SingleInstance();
                }
            }
        }
    }
    ```

内部类(在方法 getInstance()调用时加载内部类，利用 classLoader 保证了同步)

    ```
    // 内部类
    private static class InnerSingleInstance {
        private staic final SingleInstance INTANCE = new SingleInstance();
    }
    private SingleInstance(){};
    public static SingleInstance getInstance(){
        return InnerSingleInstance.INSTANCE;
    }
    ```

枚举实现

该实现可以防止反射攻击。在其它实现中，通过 setAccessible() 方法可以将私有构造函数的访问级别设置为 public，然后调用构造函数从而实例化对象，如果要防止这种攻击，需要在构造函数中添加防止多次实例化的代码。该实现是由 JVM 保证只会实例化一次，因此不会出现上述的反射攻击。

    ```
    public enum Singleton {
        INSTANCE;
        private String objName;

        public String getObjName() {
            return objName;
        }

        public void setObjName(String objName) {
            this.objName = objName;
        }
    ```

#### 代理模式

- 1. 实现类与代理类 都继承同一个接口， 代理类的方法实现实际是调用实现类的该方法实现。 如：spring AOP 中的 jdk 动态代理

  ![代理模式1](./images/代理模式1.png)

* 2. 代理类继承实现类，覆盖其中的方法。 如：spring AOP 中的 cglib 代理

  ![代理模式2](./images/代理模式2.png)

JDK 动态代理只能对实现了接口的类生成代理对象;

cglib 可以对任意类生成代理对象，它的原理是对目标对象进行继承代理，如果目标对象被 final 修饰，那么该类无法被 cglib 代理。

#### 适配器模式

#### 职责链模式

- ![职责链模型](./images/职责链模型.png)

将能够处理同一类请求的对象连成一条链，使这些对象都有机会处理请求，所提交的请求沿着链传递，从而避免请求的发送者和接受者之间的耦合关系。链上的对象逐个判断是否有能力处理该请求，如果能则处理，如果不能，则传递给链上的下一个对象。直到有一个对象处理它为止。

场景：
打牌时，轮流出牌
接力赛跑
请假审批
公文审批

    ```
    // 共同抽象处理类
    public abstract class Handler {

        protected Handler successor ;

        public Handler (Handler successor ){
            this.successor  = successor ;
        }

        public abstract void handleRequest(Request request);
    }

    // 主任对应的处理类
    public class DirectorHandler extends Handler {

        private String name = "director";

        public DirectorHandler(Handler successor) {
            super(successor);
        }

        @Override
        public void handleRequest(Request request) {
            if (request.getlevel() == 1) {
                System.out.println("\t" + name + " handler the request: \n\t\t" + request.getMsg());
                return;
            }

            if (successor != null) {
                successor.handleRequest(request);
            }
        }
    }

    // 经理对应的处理类
    public class ManagerHandler extends Handler {

        private String name = "manager";

        public ManagerHandler(Handler successor) {
            super(successor);
        }

        @Override
        public void handleRequest(Request request) {
            if (request.getlevel() == 2) {
                System.out.println("\t" + name + " handler the request: \n\t\t" + request.getMsg());
                return;
            }

            if (successor != null) {
                successor.handleRequest(request);
            }
        }
    }

    // 请求类
    public class Request {
        private int level;
        private String msg;

        public Request(int level, String msg) {
            this.level = level;
            this.msg = msg;
        }

        public void setlevel(int level) {
            this.level = level;
        }

        public void setMsg(String msg) {
            this.msg = msg;
        }

        public int getlevel() {
            return this.level;
        }

        public String getMsg() {
            return this.msg;
        }
    }

    // 客户端
    public class Client {

        public static void main(String[] args) {
            Handler generalManagerHandler = new GeneralManagerHandler(null);
            Handler managerHandler = new ManagerHandler(generalManagerHandler);
            Handler directorHandler = new DirectorHandler(managerHandler);

            Request request1 = new Request(1,
                    "the request's level is 1 and request body is the person need one day to rest \n");
            directorHandler.handleRequest(request1);

            Request request2 = new Request(2,
                    "the request's level is 2 and request body is the person need ten day to rest \n");
            directorHandler.handleRequest(request2);
        }
    }

    // output:
        director handler the request:
                the request's level is 1 and request body is the person need one day to rest

        manager handler the request:
                the request's level is 2 and request body is the person need ten day to rest

    ```


## java编程基础

#### java 基础数据类型

- 内置数据类型

  八种基本类型。 六种数字类型(四个整形，两个浮点型)，一种字符型，一种布尔型

  一个字节八个位

  | 数据类型 | 大小(字节)                                 | 最小值           | 最大值                | 默认值 | 例子                                  | 备注                                                                                           |
  | -------- | ------------------------------------------ | ---------------- | --------------------- | ------ | ------------------------------------- | ---------------------------------------------------------------------------------------------- |
  | byte     | 1                                          | 最小值-128(-2^7) | 最大值 127(2^7)       | 0      | byte a = 100; <br> byte b = -50       | byte 类型用在大型数组中节约空间，主要代替整数，因为 byte 变量占用的空间只有 int 类型的四分之一 |
  | short    | 2                                          | -2^15 = -32768   | 2^15 = 32767          | 0      | short s = 1000; <br> short r = -20000 |
  | int      | 4                                          | -2^31            | 2^31                  | 0      | int a = 1                             | 一般地整型变量默认为 int 类型；                                                                |
  | long     | 8                                          | -2^63            | 2^64                  | 0L     | long a = 100000L                      | "L"理论上不分大小写，但是若写成"l"容易与数字"1"混淆，不容易分辩。所以最好大写                  |
  | float    | 4                                          |                  |                       | 0.0f   | float f1 = 234.5f                     | float 在储存大型浮点数组的时候可节省内存空间                                                   |
  | double   | 8                                          |                  |                       | 0.0d   | double d1 = 123.4                     | double 类型同样不能表示精确的值，如货币                                                        |
  | boolean  | 1/8,boolean 数据类型表示一位的信息         |                  |                       | false  | boolean one = true                    | 只有两个取值：true 和 false                                                                    |
  | char     | 2 char 类型是一个单一的 16 位 Unicode 字符 | \u0000（即为 0） | \uffff（即为 65,535） |        | char letter = 'A'                     |                                                                                                |

- 引用数据类型

#### hashSet 的遍历

1. 迭代遍历

```
    Set<String> set = new HashSet<String>();
    Iterator<String> it = set.iterator();
    while (it.hasNext()) {
        String str = it.next();
        System.out.println(str);
    }
```

2. for 循环遍历

```
for (String str : set) {
      System.out.println(str);
}
```

#### String 类为什么不可变

```
public final class String
    implements java.io.Serializable, Comparable<String>, CharSequence
{
    /** String本质是个char数组. 而且用final关键字修饰.*/
    private final char value[];
```

1. 字符串常量池的需要

字符串常量池是 Java 内存中一个特殊的存储区域，当创建一个 String 对象时，假如此字符串值在常量池中已经存在，则不会创建一个新的对象，而是引用已存在的对象。
因为 String 是不可变类，可靠。如果 String 可变，则某个对象的改变会引起其他对象的改变。

2. 允许 String 对象缓存 HashCode

java 中 String 对象的哈希码被频繁的使用，比如 hashMap 等容器中。
字符串不变保证了 hash 码的唯一性。如果可变，string 改变后将可能导致出现相同的 hashCode

3. 安全性

String 类被许多类库作为参数，例如，网络连接地址 url，文件路径 path，还有反射机制所需要的 String 参数等，假若 String 不是固定不变的，将会引起各种安全隐患

#### java 中 Byte 怎么转换为 String

- String 转化为 byte[]数组

```
String str = "abcd";
byte[] bs = str.getBytes();
```

- byte[]数组转化为 String 字符串

```
byte[] bs1 = {97,98,100};
String s = new String(bs1);
```

- 设置格式

```
byte[] srtbyte = {97,98,98};
String res = new String(srtbyte,"UTF-8");
```


## java面向对象

多态就是依赖运行期动态加载和动态链接这个特点实现的


## JDBC

> https://github.com/duiliuliu/Interview/tree/master/test/src/com/jdbc <br>
 > https://blog.csdn.net/limin0983/article/details/73500035

#### 概念

- JDBC (JAVA DATABASE CONNECTIVITY,JAVA 数据库连接)

  是一种用于执行 sql 语句的 java API，可以为多种关系数据库提供统一访问，它由一组用 java 语言编写的类和接口组成。

- JDBC API 支持两层和三层处理模型进行数据库访问，但一般的 JDBC 体系结构由两层组成：

  - JDBC API : 提供了应用程序对 JDBC 的管理连接
  - JDBC Driver API： 支持 JDBC 管理到驱动连接

    ![JDBC模型](./images/JDBC模型.png)

#### 常用接口

1. Driver 接口

   驱动，由数据库厂商提供，java 开发者只需使用即可

   不同的数据库有不同的驱动装载方法

   MySQL: Class.forName("com.mysql.jdbc.Driver")

   Oracle: Class.forName("oracle.jdbc.driver.OracleDriver")

2. DriverManager 接口

   DriverManager 是驱动的管理类，用它来连接数据库比 Driver 更为方便

3. Connection 接口

   Connection 接口是与特定数据库连接通信的对象。此接口有接触数据库的所有方法，连接对象便是通信上下文，即，与数据库中所有的通信都是 Connection 对象。

   通过 Driver 对象获取 Connection 对象：

   ```
   Connection connection = driver.connect(jdbcurl, user,password)
   ```

   通过 DriverManager 对象获取 Connection 对象：

   ```
   Connection connection = DriverManager.getConnection(url,user,password)
   ```

4. Statement 接口

   用于执行 sql 语句并返回生成结果的对象

   有三种 Statement 类：

   - Statement

     由 createStatement 创建，用于发送简单的 sql 语句(不带参数)

   - PreparedStatement

     继承自 Statement 接口，由 prepareStatement 创建，用于发送含有一个或多个参数的 sql 语句

     PreparedStatement 对象采用预编译，比 Statement 对象效率更高。并且可以防止 sql 注入

   - CallableStatement

     继承自 PreparedStatement，由方法 prearedCall()创建，用于调用存储过程

   常用的 Statement 方法：

   - execute(String sql) 运行语句，返回是否有结果集
   - executeQuery(String sql) 运行 select 语句，返回 ResultSet 结果集
   - executeUpdate(String sql) 运行 insert/delete/update 操作，返回更新的 行数。
   - addBatch(String sql) 把多条 sql 语句放到一个批处理中
   - executeBatch() 使用 executeBatch()执行批量 sql 语句(之前添加的)

5. ResultSet 接口

   查询结果集，提供了检索不同类型字段的方法


## java集合

#### Java 集合 Set/List

- ![java集合](./images/java集合.png)
- ArrayList
- LinkedList

#### Map

- HashMap

  HashMap 是基于哈希表的 map 接口的非同步实现
  简单说就是数组+链表构成
  在 HashMap 初始化的时候会初始化一个 Entry[]数组，用来存储 key、value 对，当需要存储一个 Entry 对象，会经过 hash 算法来确定数组中的位置，产生冲突后，从头部插入当前 entry 对象，原有 Entry 链串接其后。

  HashMap 允许 null 值 null 键，键具有唯一性，所以允许一个 null 键，而值就无所谓了。相比较 HashTable，HashTable 不允许 null 值 null 键，null 值与 null 键都会抛出 NullPointException。

  HashMap 的初始容量为 16，之后以当前容量的 2 倍进行扩充。其容量始终为 2 的 n 次幂，目的是为了提高 hash 算法确定位置时的效率，通常得到 hashcode 会对当前容量进行取模运算得到散列表中存储位置，而除法运算相对效率较低，容量设置为 2 的 n 次幂便可以通过位运算快速得到模，具体位运算为 hash 与 当前容量-1 进行与运算。
  而 HashTable 的初始容量为 11，之后的扩容变为原来的 2N+1,取模运算哪儿运用除法，效率低。

  HashMap 实现了 Iterable，HashTable 实现了 Iterable 与 Enumration(历史遗留问题)

  HashMap 不保证存储顺序，LinkedHashMap 保证存储顺序，其内部实现了双向链表以保证顺序

  HashMap 是线程不安全的，如果要线程安全，可以选择 HashTable、collections.synchronizedHashMap()包装或 concurrentHashMap,推荐 concurrentHashMap。因为内部实现为分段加锁，效率较高。

  构造函数
  HashMap()
  HashMap(int capacity)
  HashMap(int capacity, float loadFactory)
  HashMap(Map<? extends K, ?extends V> map)
  继承自 abstractMap 类，实现 Map 接口

  hash()
  在 get、put 计算下标时，先对 hashCode 进行 hash 操作，然后再通过 hash 值进一步操作.

  > 高 16bit 和低 16bit 异或了一下。设计者还解释到因为现在大多数的 hashCode 的分布已经很不错了，就算是发生了碰撞也用 O(logn)的 tree 去做了。仅仅异或一下，既减少了系统的开销，也不会造成因为高位没有参与下标的计算(table 长度比较小)时，引起的碰撞。

        ```
        static final int hash(Object key) {
            int h;
            return (key == null) ? 0 : (h = key.hashCode()) ^ (h >>> 16);
        }
        ```

  indexFor(int h, int length)
  目前的 table 长度 n 为 2 的次幂，所以计算下标的时候，可使用按位与&代替取模%：

        ```
        return h & (length-1);
        ```

  get()
  获取 key 对应的 value
  传入参数 key
  通过 key 的 hash 码计算出数组中的位置
  判断是否有值，有的话进行遍历(链表)
  判断值返回

        ```
        public V get(Object key) {
            if (key == null)
                return getForNullKey();

            int hash = hash(key);

            for (Entry<K,V> e = table[indexFor(hash, table.length)];
                e != null;
                e = e.next) {
                Object k;
                if (e.hash == hash && ((k = e.key) == key || key.equals(k)))
                    return e.value;
            }
            return null;
        }
        ```

  java8 中链表长度大于 8 时，则转化为红黑树存储

        ```
        public V get(Object key) {
            Node<K,V> e;
            return (e = getNode(hash(key), key)) == null ? null : e.value;
        }

        final Node<K,V> getNode(int hash, Object key) {
            Node<K,V>[] tab; Node<K,V> first, e; int n; K k;
            // table不为空 && table长度大于0 && table索引位置(根据hash值计算出)不为空
            if ((tab = table) != null && (n = tab.length) > 0 &&
                (first = tab[(n - 1) & hash]) != null) {
                if (first.hash == hash && // always check first node
                    ((k = first.key) == key || (key != null && key.equals(k))))
                    return first;	// first的key等于传入的key则返回first对象
                if ((e = first.next) != null) { // 向下遍历
                    if (first instanceof TreeNode)  // 判断是否为TreeNode
                        // 如果是红黑树节点，则调用红黑树的查找目标节点方法getTreeNode
                        return ((TreeNode<K,V>)first).getTreeNode(hash, key);
                    // 走到这代表节点为链表节点
                    do { // 向下遍历链表, 直至找到节点的key和传入的key相等时,返回该节点
                        if (e.hash == hash &&
                            ((k = e.key) == key || (key != null && key.equals(k))))
                            return e;
                    } while ((e = e.next) != null);
                }
            }
            return null;    // 找不到符合的返回空
        }
        ```

  put()
  校验 table 是否为空或者 length 为 0，是的话则调用 resize 进行初始化
  通过**hash 值计算索引位置**，将该索引位置的头节点赋值给 p 节点，如果该索引位置节点为空则使用传入的参数新增一个节点并放在该索引位置
  判断 p 节点的 key 和 hash 值是否跟传入的相等，如果相等, 则 p 节点即为要查找的目标节点，将 p 节点赋值给 e 节点
  如果 p 节点不是目标节点，则**判断 p 节点是否为 TreeNode**，如果是则调用红黑树的 putTreeVal 方法 查找目标节点
  走到这代表 p 节点为普通链表节点，则调用普通的链表方法进行查找，并定义变量 binCount 来统计该链表的节点数
  如果 p 的 next 节点为空时，则代表找不到目标节点，则新增一个节点并插入链表尾部，并校验节点数是否超过 8 个，如果超过则调用 treeifyBin 方法（见下文代码块 6）将链表节点转为红黑树节点
  如果遍历的 e 节点存在 hash 值和 key 值都与传入的相同，则 e 节点即为目标节点，跳出循环
  如果 e 节点不为空，则**代表目标节点存在**，使用传入的 value 覆盖该节点的 value，并返回**oldValue**
  如果插入节点后节点数超过阈值，则调用 resize 方法（见下文 resize 方法）进行扩容


## javaIO

> https://www.cnblogs.com/Evsward/archive/2017/12/04/io.html
 > http://www.importnew.com/28021.html

\*
![javaIO](./images/javaIO.png)

- 字节流和字符流

  io 默认都是直接操作字节的，多用于读取或书写二进制数据，这些类的基类为 InputStream 或 OutputStream。而字符流操作的是为了支持 Unicode 编码，用于字符国际化，一个字符占用两个字节，这些类的基类为 Reader 或 Writer。java 的 io 在 jdk1.1 以后添加了字符流的支持，为我们直接操作字符流提供了方便。

- IO

  根据数据的流向，流分为输入流和输出流，这里指的都是内存，内存输入为读，输出为写，I 读 O 写。

- java 的 IO 包大量应用了装饰者模式

- 流的分类

  - 按数据流的方向不同：输入流，输出流。

  - 按处理数据单位不同：字节流，字符流。

    (1) 字节流：数据流中最小的数据单元是字节。

    (2)字符流：数据流中最小的数据单元是字符， Java 中的字符是 Unicode 编码，一个字符占用两个字节。

  - 按功能不同：节点流，处理流。

    (1)程序用于直接操作目标设备所对应的类叫节点流。

    (2)程序通过一个间接流类去调用节点流类，以达到更加灵活方便地读写各种类型的数据，这个间接流类就是处理流。

* 节点流

  ![节点流模型](./images/节点流模型.png)

  |              | 处理类型      | 字符流                               | 字节流                                          | 说明                             |
  | ------------ | ------------- | ------------------------------------ | ----------------------------------------------- | -------------------------------- |
  | 文件         | File          | FileReader <br> FileWriter           | FileInputStream <br> fileOutputStream           | 对文件进行读写操作               |
  | 内存(数组)   | Memory Array  | CharArrayReader <br> CharArrayWriter | ByteArrayInputStream <br> ByteArrayOutputStream | 从/向内存数组读写数据            |
  | 内存(字符串) | Memory String | StringWriter <br> StringReader       | ---                                             | 从/向内存字符串读写数据          |
  | 管道         | Pipe          | PipedReader <br> PipedWriter         | PipedInputStream <br> PipedOutputStream         | 实现管道的输入和输出(进程间通信) |

- 处理流

  ![处理流模型](./images/处理流模型.png)

  |            | 处理类型                                   | 字符流                                    | 字节流                                        | 说明                                                                                                                                                                 |
  | ---------- | ------------------------------------------ | ----------------------------------------- | --------------------------------------------- | -------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
  | 缓冲区     | Buffering                                  | BufferedReader <br> BufferedWriter        | BufferedInputStream <br> BufferedOutputStream | 将流放在缓冲区内操作                                                                                                                                                 |
  | 过滤流     | Filtering                                  | FilterReader <br> FilterWriter            | FilterInputStream <br> FilterOutputStream     | 抽象类，作为“装饰器”的接口，其中，“装饰器”为其他输入输出字符字节类提供有用功能                                                                                       |
  | 转换流     | coverting between <br> bytes and character | InputStreamReader <br> OutputStreamWriter | ---                                           | 处理字符流的抽象类 <br> InputStreamReader 是字节流通向字符流的桥梁,它将字节流转换为字符流. <br> OutputStreamWriter 是字符流通向字节流的桥梁，它将字符流转换为字节流. |
  | 对象流     | object Serialization                       | ---                                       | ObjectInputStream <br> ObjectOutputStream     | 序列化                                                                                                                                                               |
  | 基本类型   | Data conversion                            | ---                                       | DataInputStream <br> DataOutputStream         | 可传输基本类型数据                                                                                                                                                   |
  | 行号推入流 | Counting                                   | LineNumberReader                          | ---                                           | 可得到一个携带行号的字符读入流                                                                                                                                       |
  | 推回输入流 | Peeking ahead                              | PushbackReader                            | PushbackInputStream                           | 可将输入流 push back 或 unread 一个字节                                                                                                                              |
  | 合并流     | Sequence                                   | ---                                       | SequenceInputStream                           | 可逻辑串联其他输入流                                                                                                                                                 |
  | 打印流     | Printing                                   | PrintWriter                               | PrintStream                                   | 包含 print 和 println 的输出流                                                                                                                                       |

  - 注意

    - 默认都是操作字节，所有操作字符的类都需要先经过转化流将字节流转为字符流再使用。
    - LineNumberInputStream 已过时，因为它是基于字节输入流的，而错误假定字节能充分表示字符，现已被 LineNumberReader 取代。
    - StringBufferInputStream 已过时，因为此类未能正确地将字符转换为字节，现已被 StringReader 取代。


    * java io 转化流的适配器模式

        我们知道在字符流处理类加入java io类库之前，所有的类都是面向字节流的，在jdk1.1以后，添加了字符流的支持，根据“开闭原则”，所以在不改变原有类的基础上，有了转化流：InputStreamReader和OutputStreamWriter，这两个类正是所谓的“适配器类”，InputStreamReader可以吧InputStream转换为Reader，而OutputStreamWriter可以将OutputStream转换为Writer。字节流和字符流都有各自独立的一整套继承层次结构，而通过适配器类，可以在不改变原有类的前提下有效将他们结合起来。

    * java io 装饰器模式的研究：

        Java I/O类库需要多种不同功能的组合，存在filter类的原因就是抽象类filter是所有装饰器类的基类，装饰器必须具有和它所装饰对象相同的接口。FilterInputStream和FilterOutputStream是用来提供装饰器类接口以控制特定输入流(InputStream)和输出流(OutputStream)的两个类，他们的名字并不是很直观，包括DataInput/OutputStream, BufferedInput/OutputStream,LineNumberInputStream, PushbackInputStream,PrintStream等，这些过滤流类在下面都会有详细介绍。FilterInputStream和FilterOutputStream分别自I/O类库中的基类InputStream和OutputStream派生而立，这两个类是装饰器的必要条件(以便能为所有正在被修饰的对象提供通用接口)。

- 源码

  - InputStream

    InputStream 是输入字节流部分，装饰器模式的顶层类，重要规定了输入字节流的公共方法

    ```
    int read()                              //从输入流读取数据的下一字节
    int read(byte[] b)                      //从输入流读取b.length的字节
    int read(byte[] b, int off, int len)    //从输入流读取字节到b[off]~b[len-off]
    int available()                         //返回能从输入流读取的字节数的估计
    void close()                            //关闭输入流并释放它所占取的系统资源
    long skip(long n)                       //跳过或丢弃输入流的n个字节
    boolean markSupported()                 //测试这个输入流是否支持mark()和reset()
    void mark(int readlimit)                //标记这个输入流的当前位置
    void reset()                            //最后一次调用这个输入流上的mark方法时的初始位置
    ```

    ```
    package java.io;

    public abstract class InputStream implements Closeable {

        private static final int SKIP_BUFFER_SIZE = 2048;  //用于skip方法，和skipBuffer相关

        private static byte[] skipBuffer;    // skipBuffer is initialized in skip(long), if needed.

        /**
        * 从输入流中读取下一个字节，
        * 正常返回0-255，到达文件的末尾返回-1
        * 在流中还有数据，但是没有读到时该方法会阻塞(block)
        * Java IO和New IO的区别就是阻塞流和非阻塞流
        * 抽象方法！不同的子类会有不同的实现！
        */
        public abstract int read() throws IOException;

        /**
        * 将流中的数据读入放在byte数组的第off个位置先后的len个位置中
        * 放回值为放入字节的个数。
        */
        public int read(byte b[], int off, int len) throws IOException {
            if (b == null) {                       //检查输入是否正常。一般情况下，检查输入是方法设计的第一步
                throw new NullPointerException();
            } else if (off < 0 || len < 0 || len > b.length - off) {
                throw new IndexOutOfBoundsException();
            } else if (len == 0) {
                return 0;
            }
            int c = read();                         //读取下一个字节
            if (c == -1) {    return -1;   }        //到达文件的末端返回-1
            b[off] = (byte)c;                       //放回的字节downcast
            int i = 1;                              //已经读取了一个字节
            try {                                   //int i 在循环外申明，扩大作用域
                for (; i < len ; i++) {             //最多读取len个字节，所以要循环len次
                    c = read();                     //每次循环从流中读取一个字节。由于read方法阻塞，所以read(byte[],int,int)也会阻塞
                    if (c == -1) {  break;  }       //到达末尾，返回-1
                    b[off + i] = (byte)c;           //读到就放入byte数组中
                }
            } catch (IOException ee) {     }
            return i;
        }

        public int read(byte b[]) throws IOException {
            return read(b, 0, b.length);
        }

        public long skip(long n) throws IOException {
            long remaining = n;                                  //方法内部使用的、表示要跳过的字节数目，

            int nr;
            if (skipBuffer == null)
                skipBuffer = new byte[SKIP_BUFFER_SIZE];         //初始化一个跳转的缓存
            byte[] localSkipBuffer = skipBuffer;                 //本地化的跳转缓存
            if (n <= 0) {    return 0;      }                    //检查输入参数
            while (remaining > 0) {                              //一共要跳过n个，每次跳过部分，循环
                nr = read(localSkipBuffer, 0, (int) Math.min(SKIP_BUFFER_SIZE, remaining));
                                                                //利用上面的read(byte[],int,int)方法，读取n个字节
                if (nr < 0) {  break;    }                       //读到流的末端，则返回
                remaining -= nr;                                 //没有完全读到需要的，则继续循环
            }
            return n - remaining;                                //返回时要么全部读完，要么因为到达文件末端，读取了部分
        }

        public int available() throws IOException {             //查询流中还有多少可以读取的字节
            return 0;
        }

        public void close() throws IOException {}               //关闭当前流、同时释放与此流相关的资源

        public synchronized void mark(int readlimit) {}         //在当前位置对流进行标记，必要的时候可以使用reset方法返回。

        public synchronized void reset() throws IOException {   //对mark过的流进行复位。只有当流支持mark时才可以使用此方法。
            throw new IOException("mark/reset not supported");
        }

        public boolean markSupported() {                        //查询是否支持mark.绝大部分不支持，因此提供默认实现，返回false。子类有需要可以覆盖。
            return false;
        }
    }
    ```

  - FilterInputStream

    是字节输入流部分装饰器模式的核心。是我们在装饰器模式中的 Decorator 对象，主要完成对其它流装饰的基本功能。


## javaNIO、AIO

> https://blog.csdn.net/u011109589/article/details/80333775


## java反射

#### 概念

- 反射机制

  反射是一种间接操作目标对象的机制。在程序运行时或者能够获取对象的信息，只要给定类的名字，就可以通过反射获取类的信息。

- 作用

  1. 在运行时判断任意一个对象所属的类
  2. 在运行时判断任意一个对象所具有的成员变量和方法
  3. 在运行时任意调用一个对象的方法
  4. 在运行时构造任意一个类的对象

- 优点

  动态编译：运行时确定类型，绑定对象。动态编译较大限度的发挥了 Java 的灵活性，体现了多态的应用，有利于降低类之间的耦合性。

- 缺点

  不利于封装

#### Class.forName 和 classloader 区别

Java 中 Class.forName 和 classloader 都可以对类进行加载。

不过 Class.forName()除了将.class 文件加载到 JVM 中之外，还会对类进行解释，_执行类中的 static 块_;而 classloader 只会做一件事，就是加载.class 文件到 JVM 中，不会执行 static 中的内容，只有在 newInstance 时才会去执行 static 块。

- 反射能够获取类的所有信息吗？


## java并发编程

#### 三个并发编程概念

- 原子性问题
  - 原子是世界上的最小单位，具有不可分割性
  - 一个操作是原子操作，那么我们称它具有原子性。

即一个操作或者多个操作 要么全部执行并且执行的过程不会被任何因素打断，要么就都不执行。

- 可见性问题

指当多个线程访问同一个变量时，一个线程修改了这个变量的值，其他线程能够立即看得到修改的值。

- 有序性问题

即程序执行的顺序按照代码的先后顺序执行。

#### 缓存一致性

解决缓存不一致的问题：

1. 通过在总线加 LOCK#锁的方式

   因为 CPU 和其他部件进行通信都是通过总线来进行的，如果对总线加 LOCK#锁的话，也就是说阻塞了其他 CPU 对其他部件访问(如内存)，从而使得只能有一个 CPU 能使用这个变量的内存。

2. 通过缓存一致性协议

   最出名的就是 Intel 的 MESI 协议，MESI 协议保证了每个缓存中使用的共享变量的副本是一致的。它核心的思想是：当 CPU 写数据时，如果发现操作的变量是共享变量，即在其他 CPU 中也存在该变量的副本，会发出信号通知其他 CPU 将该变量的缓存行置为无效状态，因此当其他 CPU 需要读取这个变量时，发现自己缓存中缓存该变量的缓存行是无效的，那么它就会从内存重新读取。

#### 锁相关概念

- 可重入锁

  如果锁具备可重入性，则称作为可重入锁。像 synchronized 和 ReentrantLock 都是可重入锁，可重入性在我看来实际上表明了锁的分配机制：基于线程的分配，而不是基于方法调用的分配。举个简单的例子，当一个线程执行到某个 synchronized 方法时，比如说 method1，而在 method1 中会调用另外一个 synchronized 方法 method2，此时线程不必重新去申请锁，而是可以直接执行方法 method2。

  ```
  public MyClass{
      public synchronized void method1(){
          method2();
      }
      public synchronized void method2(){

      }
  }
  ```

  机制：每个锁都关联一个请求计数器和一个占有他的线程，当请求计数器为 0 时，这个锁可以被认为是 unhled 的，当一个线程请求一个 unheld 的锁时，JVM 记录锁的拥有者，并把锁的请求计数加 1，如果同一个线程再次请求这个锁时，请求计数器就会增加，当该线程退出 syncronized 块时，计数器减 1，当计数器为 0 时，锁被释放。

- 可中断锁

  可中断锁：顾名思义，就是可以相应中断的锁。

  在 Java 中，synchronized 就不是可中断锁，而 Lock 是可中断锁。

  如果某一线程 A 正在执行锁中的代码，另一线程 B 正在等待获取该锁，可能由于等待时间过长，线程 B 不想等待了，想先处理其他事情，我们可以让它中断自己或者在别的线程中中断它，这种就是可中断锁。

- 公平锁

  公平锁即尽量以请求锁的顺序来获取锁。比如同是有多个线程在等待一个锁，当这个锁被释放时，等待时间最久的线程(最先请求的线程)会获得该所，这种就是公平锁。

  非公平锁即无法保证锁的获取是按照请求锁的顺序进行的。这样就可能导致某个或者一些线程永远获取不到锁。

  在 Java 中，synchronized 就是非公平锁，它无法保证等待的线程获取锁的顺序。

  而对于 ReentrantLock 和 ReentrantReadWriteLock，它默认情况下是非公平锁，但是可以设置为公平锁。

  ```
  ReentrantLock lock = new ReentrantLock(true); \\公平锁
  ```

* 读写锁

  读写锁将对一个资源(比如文件)的访问分成了 2 个锁，一个读锁和一个写锁。

  正因为有了读写锁，才使得多个线程之间的读操作不会发生冲突。

  ReadWriteLock 就是读写锁，它是一个接口，ReentrantReadWriteLock 实现了这个接口。

  可以通过 readLock()获取读锁，通过 writeLock()获取写锁

* 偏向锁

  java 偏向锁(Biased Locking)是 java6 引入的一项多线程优化.它通过消除资源无竞争 q 情况下的同步原语,进一步提高了程序的运行性能.

  偏向锁，顾名思义，它会偏向于第一个访问锁的线程，如果在接下来的运行过程中，该锁没有被其他的线程访问，则持有偏向锁的线程将永远不需要触发同步。

  如果在运行过程中，遇到了其他线程抢占锁，则持有偏向锁的线程会被挂起，JVM 会尝试消除它身上的偏向锁，将锁恢复到标准的轻量级锁。(偏向锁只能在单线程下起作用)

  因此 流程是这样的 偏向锁->轻量级锁->重量级锁

  偏向锁，简单的讲，就是在锁对象的对象头中有个 ThreaddId 字段，这个字段如果是空的，第一次获取锁的时候，就将自身的 ThreadId 写入到锁的 ThreadId 字段内，将锁头内的是否偏向锁的状态位置 1.这样下次获取锁的时候，直接检查 ThreadId 是否和自身线程 Id 一致，如果一致，则认为当前线程已经获取了锁，因此不需再次获取锁，略过了轻量级锁和重量级锁的加锁阶段。提高了效率。

- 乐观锁，悲观锁

  - 乐观锁

    总是认为不会产生兵法问题，每次*读取*数据的时候认为不会有其他线程对数据进行修改，因此不会上锁。

    但是在更新的时候会判断其他线程在这之前有没有对数据进行修改,一般会使用*版本控制机制或 CAS*实现。

    实现：

    - 版本控制机制

      一般是在数据表中加上一个数据版本号字段(version)，表示数据被修改的次数,当数据被修改时，version 值会加 1.

      当线程 A 要更新数据时，读取数据的同时也会读取 version，提交更新时对比 version，若 version 与数据库中 version 相等时进行更新，否则重试更新操作，知道更新成功。(类似锁的自旋)

    - CAS

      即 compare and swap 或者 compare and set,涉及到三个操作数:数据所在的内存值，预期值，新值。

      当需要更新时,判断内存值(公共)与旧的预期值(之前取的值)是否相等,若相等，则没有被其他线程修改过，使用新值进行更新，否则进行重试,一般情况下是一个自旋，即不断的重试。

#### CAS(Compare-and-Swap)

CAS 算法是由硬件直接支持来保证原子性的，有三个操作数：内存位置 V、旧的预期值 A 和新值 B，当且仅当 V 符合预期值 A 时，CAS 用新值 B 原子化地更新 V 的值，否则，它什么都不做。

- CAS 的 ABA 问题

  当然 CAS 也并不完美，它存在"ABA"问题，假若一个变量初次读取是 A，在 compare 阶段依然是 A，但其实可能在此过程中，它先被改为 B，再被改回 A，而 CAS 是无法意识到这个问题的。CAS 只关注了比较前后的值是否改变，而无法清楚在此过程中变量的变更明细，这就是所谓的 ABA 漏洞。

#### volatile 关键字

1. 保证了不同线程对这个变量进行操作时的可见性，即一个线程修改了某个变量的值，这新值对其他线程来说是立即可见的。
2. 禁止进行指令重排序。

但是并不能保证 **原子性**,即：i 用 volatile 修饰,1000 个线程调 i++,结果并不一定 1000.因为自增操作不具备原子性，它包括读取变量的原始值、进行加一操作、写入内存。那么就是说自增操作的三个子操作可能会分割执行。

#### Synchronized

synchronized 是 jvm 层面的锁，是 Java 的内置特性
在 jvm 层面实现对临界资源的互斥访问，不可响应中断
锁的释放有虚拟机完成,不用人工干预。这既是缺点又是优点.优点是不用担心死锁,缺点是有可能获取到锁的线程阻塞之后其他线程会一直等待,性能不高
非公平锁
可重入锁

- 使用

  - 修饰实例方法,对当前实例加锁,进入同步代码前要获得当前实例的锁

    ```
    public synchronized void method(){
        try{
            Thread.sleep();
            System.out.println("synchronized method");
        }catch(InterruptedException e){
            e.printStackTrace();
        }
    }
    ```

  - 修饰静态方法,对当前类加锁,进入同步代码前要获得类对象的锁

    ```
    public static synchronized void method1(){
        try {
            System.out.println("static synchronized method");
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    ```

  - 修饰代码块，指定加锁对象，对指定对象加锁，进入同步代码块前要获得指定对象的锁

    ```
    public void method1(){
        try {
            synchronized (this) {
                System.out.println("synchronized block method");
                Thread.sleep(3000);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    ```

- 实现原理

  > https://blog.csdn.net/wu1226419614/article/details/73740899

  > https://blog.csdn.net/qq_37532186/article/details/79728231

  > https://blog.csdn.net/javazejian/article/details/72828483

  synchronized 可以保证变量的原子性/可见性/顺序性.并且是一个可重入锁

  - 字节码层面

    synchronized 是基于进入和退出管程(Monitor)对象实现(monitorEntor 和 monitorExit),monitorEnter 指令插入同步代码开始位置，monitorExit 指令插入到同步代码结束位置。任何一个对象都有一个 monitor 与之相关联，当一个线程处于 monitor 之后，它将处于锁定状态

    ![Synchronized字节码](./images/Synchronized字节码.png)

  - synchronized 的优化

    synchronized 在 jdk1.6 之前是重量级锁,在 jdk1.6 之后对 synchronized 做了优化，加入了偏向锁、轻量级锁、锁粗化、锁消除、适应性自旋等操作，大大增加了 synchronized 的效率

    - java 对象头

      | 长度     | 内容                   | 说明                         |
      | -------- | ---------------------- | ---------------------------- |
      | 32/64bit | Mark Word              | 存储对象 hashcode 或锁信息等 |
      | 32/64bit | Class Metadata Address | 存储到对象类型数据的指针     |
      | 32/64bit | Array Length           | 数组的长度(如果对象是数组)   |

    - Java 对象头里的 Mark Word 里默认存储对象的 HashCode，分代年龄和锁标记。

      32 位 JVM 的 Mark Word 的默认存储结构如下：

      |          | 25bit           | 4bit         | 1bit(是否偏向锁) | 2bit 锁标记位 |
      | -------- | --------------- | ------------ | ---------------- | ------------- |
      | 无锁状态 | 对象的 hashcode | 对象分代年龄 | 0                | 01            |

    - 运行期间 Mark Word 里的数据随着标志位的变化而变化。Mark Word 可能存储一下四种数据：

      ![Mark Word 数据类型](./images/MarkWord数据类型.png)

      当一个线程获取对象并加锁后，标志位从 01 变为 10，其他线程则处于排队状态。

      ![线程请求模型](./images/线程请求模型.png)


    - 偏向锁

      - 目的：为了在无线程竞争情况下，减少不必要的轻量级锁操作.因为轻量级锁的加锁解锁操作需要依赖多次的CAS原子操作。

      - 核心思想： 如果一个线程获取了锁,那么锁就进入偏向模式，此时MarkWordd的结构也变为锁偏向结构,当这个线程再次请求锁时，无需再申请锁.

      - 场景： 无其他线程竞争

      - 加锁流程：
        1.  检测MarkWord是否为可偏向状态,即是否为偏向锁1，锁标志为是否为01
        2.  若为可偏向锁，则检测线程id是否为当前线程id。
          -  如果是则执行同步代码
          -  如果不是，则通过CAS操作竞争锁
              -  如果竞争成功，则将MarkWord的线程id替换为当前线程ID
              -  如果竞争不成功，则CAS竞争失败，证明当前存在多线程竞争情况，这时候需要锁膨胀为轻量级锁，才能保证线程间公平竞争锁。
      - 解锁

        当有另一个线程来竞争锁的时候，就不能再使用偏向锁了，要膨胀为轻量级锁。

        竞争线程尝试 CAS 更新对象头失败，会等待到全局安全点（此时不会执行任何代码）撤销偏向锁。

      ![偏向锁简易模型](./images/偏向锁简易模型.png)
      ![偏向锁加锁解锁模型](./images/偏向锁加锁解锁模型.png)

    - 轻量级锁

      - 目的： 在多线程竞争的情况下，减少传统的重量级锁使用操作系统互斥量产生的性能消耗

      - 场景： 线程交替执行同步块的场合

      - 加锁流程：
        1.  判断当前对象是否处于无锁状态(hashcode,o,01)
            - 如果是，JVM将在当前线程的栈帧中建立一个名为锁记录(Lock Record)的空间，用于存储当前锁对象markword的拷贝(Displaced Mark Word)
            - 如果不是，执行步骤3
        2.  JVM利用CAS操作尝试将对象的Mark Word更新为指向锁记录(Lock Record)的指针,
            - 如果成功，则表示竞争到锁,更新锁标志位为00(轻量级锁),执行同步操作
            - 如果失败，则进行步骤3
        3.  判断当前对象的Mark Word是否指向当前线程的栈帧,
            - 如果是，则表示当前线程已经持有当前对象的锁,直接进行同步代码
            - 否则说明对象是被其他线程所抢占,这是轻量级锁需要膨胀为重量级锁,锁标志位变为10,后面等待的线程将进入阻塞状态。

      - 解锁

        用CAS操作锁置为无锁状态（偏向锁位为”0”，锁标识位为”01”），若CAS操作失败则是出现了竞争，锁已膨胀为重量级锁了，此时需要释放锁（持有重量级锁线程的指针位为”0”，锁标识位为”10”）并唤醒重量锁的线程。

      ![轻量级锁加锁解锁模型](./images/轻量级锁加锁解锁模型.png)

    - 自旋锁(自适应锁)

      轻量级锁失败后，虚拟机为了避免线程真实地在操作系统层面挂起，还会进行一项称为自旋锁的优化手段。这是基于在大多数情况下，线程持有锁的时间都不会太长，如果直接挂起操作系统层面的线程可能会得不偿失，毕竟操作系统实现线程之间的切换时需要从用户态转换到核心态，这个状态之间的转换需要相对比较长的时间，时间成本相对较高，因此自旋锁会假设在不久将来，当前的线程可以获得锁，因此虚拟机会让当前想要获取锁的线程做几个空循环(这也是称为自旋的原因)，一般不会太久，可能是50个循环或100循环，在经过若干次循环后，如果得到锁，就顺利进入临界区。如果还不能获得锁，那就会将线程在操作系统层面挂起，这就是自旋锁的优化方式，这种方式确实也是可以提升效率的。最后没办法也就只能升级为重量级锁了。


    - 重量级锁

      线程阻塞和唤醒都需要CPU从用户态转为核心态.
      让cpu通过操作系统指令,去调度多线程之间,谁执行代码，谁进行阻塞.这样会频繁出现程序运行状态的切换.会大量消耗资源

    - 锁粗化

      锁粗化的概念应该比较好理解，就是将多次连接在一起的加锁、解锁操作合并为一次，将多个连续的锁扩展成一个范围更大的锁。

      ```
      public class StringBufferTest {
          StringBuffer stringBuffer = new StringBuffer();

          public void append(){
              stringBuffer.append("a");
              stringBuffer.append("b");
              stringBuffer.append("c");
          }
      }
      ```

      这里每次调用stringBuffer.append方法都需要加锁和解锁，如果虚拟机检测到有一系列连串的对同一个对象加锁和解锁操作，就会将其合并成一次范围更大的加锁和解锁操作，即在第一次append方法时进行加锁，最后一次append方法结束后进行解锁。

    - 锁消除

      锁消除即删除不必要的加锁操作。根据代码逃逸技术，如果判断到一段代码中，堆上的数据不会逃逸出当前线程，那么可以认为这段代码是线程安全的，不必要加锁。看下面这段程序：

      ```
      public class SynchronizedTest02 {

          public static void main(String[] args) {
              SynchronizedTest02 test02 = new SynchronizedTest02();
              //启动预热
              for (int i = 0; i < 10000; i++) {
                  i++;
              }
              long start = System.currentTimeMillis();
              for (int i = 0; i < 100000000; i++) {
                  test02.append("abc", "def");
              }
              System.out.println("Time=" + (System.currentTimeMillis() - start));
          }

          public void append(String str1, String str2) {
              StringBuffer sb = new StringBuffer();
              sb.append(str1).append(str2);
          }
      }
      ```

      虽然StringBuffer的append是一个同步方法，但是这段程序中的StringBuffer属于一个局部变量，并且不会从该方法中逃逸出去，所以其实这过程是线程安全的，可以将锁消除

    - 对比

      |   锁   | 优点          | 缺点       | 场景 |
      | -------- | --------------- | ------------ | ---------------- |
      | <br>偏向锁<br><br> | 加锁和解锁不需要额外的x消耗,和非同步方法相比仅存在纳秒级的差距 | 如果线程间存在锁竞争，会增加额外的锁撤销消耗 | 只有一个线程访问同步块的场景  |
      | <br>轻量级锁<br><br> | 竞争的线程不用阻塞，提高了程序的响应速度 | 如果始终得不到锁竞争的线程会进入自旋消耗CPU | 追求响应速度,同步快执行速度非常快  |
      | <br>重量级锁<br><br> | 线程竞争不使用自旋，不消耗CPU | 线程阻塞，响应时间缓慢 | 追求吞吐量，同步块执行速度较长  |

#### Synchronized 与 ReenTrantLock

两者较大的区别是，synchronized 是 JVM 层面的锁;而 ReenTrantLock 是 jdk 提供的 API 层面的互斥锁，需要 lock()和 unlock()方法配合 try/finally 语句块来完成。

1. Synchronized

   Synchronized 通过编译，会在同步块的前后分别形成 monitorenter 和 monitorexit 这两个字节码指令。执行 monitorenter 指令时，首先要尝试获取对象锁。如果这个对象没被锁定，或者当前线程已经拥有了那个对象锁，把锁的计算器加一，相应的，在执行 monitorexit 指令时会将锁计算器减一，当计算器为零时，所就释放了。


    每个对象有一个监视器锁(monitor)。当monitor被占用时就会处于锁定状态，线程执行monitorenter指令时尝试获取monitor的所有权，过程如下：
    *    1. 如果monitor的进入数为0，则该线程进入monitor，然后将进入数设置为1，该线程即为monitor的所有者。
    *    2. 如果线程已经占有该monitor，只是重新进入，则进入monitor的进入数加1.
    *    3. ’如果其他线程已经占用了monitor，则该线程进入阻塞状态，直到monitor的进入数为0，再重新尝试获取monitor的所有权。

    **synchronized锁住的是代码还是对象？**

        synchronized锁住的是对象，同一个对象中的方法在此访问时，并不会申请锁，而是计数加一，所以synchronized是可重入锁。

2. ReentrantLock

   ReentrantLock 是 java.util.concurrent 包下提供的一套互斥锁，相比 Synchronized，ReentrantLock 类提供了一些高级功能，主要有以下 3 项：

   - 1. 等待可中断，持有锁的线程长期不释放的时候，正在等待的线程可以选择放弃等待，这相当于 Synchronized 来说可以避免出现死锁的情况。
   - 2. 公平锁，多个线程等待同一个锁时，必须按照申请锁的时间顺序获得锁，Synchronized 锁非公平锁，ReentrantLock 默认的构造函数是创建的非公平锁，可以通过参数 true 设为公平锁，但公平锁表现的性能不是很好。
   - 3. 锁绑定多个条件，一个 ReentrantLock 对象可以同时绑定对个对象。
   - 4. 常见 api
        <br>isFair() //判断锁是否是公平锁
        　　 <br>isLocked() //判断锁是否被任何线程获取了
        　　 <br>isHeldByCurrentThread() //判断锁是否被当前线程获取了
        　　 <br>hasQueuedThreads() //判断是否有线程在等待该锁


    ```
    private Lock lock=new ReentrantLock();
    public void run() {
        lock.lock();
        try{
            for(int i=0;i<5;i++)
                System.out.println(Thread.currentThread().getName()+":"+i);
        }finally{
            lock.unlock();
        }
    }
    ```

#### Java 原子类

在 java 1.5 的 java.util.concurrent.atomic 包下提供了一些原子操作类，即对基本数据类型的 自增(加 1 操作)，自减(减 1 操作)、以及加法操作(加一个数)，减法操作(减一个数)进行了封装，保证这些操作是原子性操作。atomic 是利用 CAS 来实现原子性操作的(Compare And Swap)，CAS 实际上是利用处理器提供的 CMPXCHG 指令实现的，而处理器执行 CMPXCHG 指令是一个原子性操作。

    AtomicBoolean
    AtomicInteger
    AtomicIntegerArray
    AtomicIntegerFieldUpdater
    AtomicLong
    AtomicLongArray
    AtomicLongFieldUpdater
    AtomicMarkableReference
    AtomicReference
    AtomicReferenceArray
    AtomicReferenceFieldUpdater
    AtomicStampedReference
    DoubleAccumulator
    DoubleAdder
    LongAccumulator
    LongAdder

- 实现原理

  ```
  /**
      * Atomically increments by one the current value.
      *
      * @return the updated value
      */
      public final int incrementAndGet() {
          for (;;) {
              int current = get();
              int next = current + 1;
              if (compareAndSet(current, next))
                  return next;
          }
      }
  ```

1. 先获取当前的 value 值
2. 对 value 加一
3. 第三步是关键步骤，调用 compareAndSet 方法来来进行原子更新操作，这个方法的语义是：

   (CAS)

   先检查当前 value 是否等于 current，如果相等，则意味着 value 没被其他线程修改过，更新并返回 true。如果不相等，compareAndSet 则会返回 false，然后循环继续尝试更新。

#### 线程中断与 synchronized

> https://github.com/duiliuliu/Interview/tree/master/test/src/com/thread/threadInterrupt

> https://blog.csdn.net/javazejian/article/details/72828483#%E7%BA%BF%E7%A8%8B%E4%B8%AD%E6%96%AD%E4%B8%8Esynchronized

#### java 线程通信

> http://www.importnew.com/26850.html

有时候我们需要多个线程协同工作完成某个任务，这时就涉及线程通信了。

    知识点：
    - Thread.join()
    - object.wait()
    - object.notify()
    - FutureTask
    - callable等

- 如何让两个线程一次执行
- 那如何让两个线程按照指定方式有序交叉运行呢
- 让三个线程依次打印自己的 name 十次，如 ABCABCABC...

#### java 线程池

java.util.concurrent.Executors 工厂类可以创建四种类型的线程池，通过 Executors.new\*\*\*方式创建
分别为 newFiexdThreadPool、newCachedThreadPool、newSingleThreadPool、ScheduledThreadPool

- newFixedThreadPool

  ```
  public static ExecutorService newFixedThreadPool(int nThreads){
      return new ThreadPoolExecutor(nThreads, nThreads, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>());
  }
  ```

  创建容量固定的线程池
  阻塞队列采用 LinkedBlockingQueue (poll()、take()),它是一种无界队列
  由于阻塞队列是一个无界队列，因此永远不可能拒绝执行任务
  由于采用无界队列，实际线程数将永远维持在 nThreads,因此 maximumPoolSize 和 KeepAliveTime 将无效

- newCachedThreadPool

  ```
  public static ExecutorService newCachedThreadPool(){
      return new ThreadPoolExecutor(0, Interger.MAX_VALUE, 60L, TimeUnit.SECONDS, new SynchronousQueue<Runnable>());
  }
  ```

  CachedThreadPool 是一种可以无限扩容的线程池
  CachedThreadPool 比较适合执行时间片较小的任务
  KeepAliveTime 为 60，意味着线程空闲时间超过 60 秒就会被杀死
  阻塞队列采用 SynchronousQueue,这种阻塞队列没有存储空间，意味着只要有任务到来，就必须得有一个工作线程进行处理，如果当前没有空闲线程，就新建一个线程

- SingleThreadExecutor

  ```
  public static ExecutorService newSingleExecutor(){
      return new ThreadPoolExecutor(1, 1, 0L, TimeUnit.MILLSECONDS, new LinkedBlockingQueue<Runnable>());
  }
  ```

  SingleThreadExecutor 只会创建一个工作线程来处理任务

- ScheduledThreadPool 接收 ScheduleFutureTask 类型的任务，提交任务的方式有两种
  1. scheduledAtFixedRate
  2. scheduledWithFixedDelay
  - SchduledFutureTask 接收参数：
    time：任务开始时间
    sequenceNumber：任务序号
    period：任务执行的时间间隔
  - 阻塞队列采用 DelayQueue，它是一种无界队列
  - DelayQueue 内部封装了一个 PriorityQueue，它会根据 time 的先后排序，若 time 相同，则根据 sequenceNumber 排序
  - 工作线程执行流程：
    1. 工作线程会从 DelayQueue 中取出已经到期的任务去执行
    2. 执行结束后重新设置任务的到期时间，再次放回 DelayQueue。

#### 例题

- 并行计算

  并行计算的方法：

  - 将数据拆分到节点上 如何拆分？
  - 每个节点并行的计算出结果 什么结果？
  - 将结果汇总 如何汇总？


    - 归并排序
        - 将数据分为左右俩半，分别进行归并排序，再把两个有序数据归并
        - 如何归并：

            [1,3,6,7], [1,2,3,5]

            分别保留两个指针，分别指向两个数组的最小元素，然后进行比较，选择较小的元素移除数组。

            当数据值一样时，我们选择左边的指针指向的数据作为较小数据移除数组，这样就是稳定的排序。

            [1,3,6,7], [1,2,3,5]  --  > 1
            [3,6,7], [1,2,3,5]  --  > 1
            [3,6,7], [2,3,5]  --  > 1
            [3,6,7], [3,5]  --  > 2
            [6,7], [3,5]  --  > 3
            ...
        - k路归并

            如何排序10G个元素，得到top10

            我们的电脑内存无法一次性对10g的数据进行排序，所以我们可以将部分排序分发到外面，进行外部排序。对结果进行汇总，然后归并。

            ![k路归并](./images/k路归并.png)

            我们可以看到有k个数列的第一个元素进行比较，所以我们每次要比较k次。
            k个元素比较k次的话就是o(n^2)了。

            此时我们可以大根堆，建立堆需要o(n),第一次调整堆，进行排序需要(o(nlogn),调整一次lgn，n-1次为nlogn)，而在之后每得到一个top，数列中去掉一个最大值，重新加入一个数，进行调整只需要lgn，取出最大值lgn，这样在之后的时间复杂度就达到了lgn。


## JVM

#### 类加载

> https://www.cnblogs.com/ITtangtang/p/3978102.html

Class 文件由类装载器装载后，在 JVM 中将形成一份描述 Class 结构的元信息对象，通过该元信息对象可以获知 Class 的结构信息：如构造函数，属性和方法等，Java 允许用户借由这个 Class 相关的元信息对象间接调用 Class 对象的功能。

虚拟机把描述类的数据从 class 文件加载到内存，并对数据进行校验，转换解析和初始化，最终形成可以被虚拟机直接使用的 Java 类型，这就是虚拟机的类加载机制。

- 步骤：

  ![类加载模型](./images/类加载模型.png)

  某些情况中，解析阶段可以再初始化阶段之后在开始，这是为了支持 java 语言的运行时绑定(也称为动态绑定或晚期绑定)。

  1.  装载：查找和导入 Class 文件

      - 通过一个类的全限定名来获取定义此类的二进制字节流
      - 将这个字节流所代表的静态存储结构转化为方法区的运行时数据结构
      - 在 java 堆中生成一个代表这个类的 java.lang.Class 对象,作为方法区这些数据的访问入口。

  2.  链接：把类的二进制数据合并到 JRE 中

      (a)校验：检查载入 Class 文件数据的正确性

      (b)准备：给类的静态变量分配存储空间

          准备阶段是正式为类变量分配并设置类变量初始值的阶段，这些内存都将在方法区中进行分配

          这时候进行内存分配的仅包括类变量(被static修饰的变量),而不包括实例变量,实例变量将会在对象实例化时随着对象一起分配在Java堆中;这里所说的初始值“通常情况”是数据类型的零值，假如:

           public static int value = 123;

           value在准备阶段过后的初始值为0而不是123,而把value赋值的putstatic指令将在初始化阶段才会被执行

      (c)解析：将符号引用转成直接引用

  3.  初始化：对类的静态变量，静态代码块执行初始化操作

- 类的初始化

  > https://blog.csdn.net/justloveyou_/article/details/72466105 <br>
 > https://blog.csdn.net/justloveyou_/article/details/72217806 <br>
 > https://blog.csdn.net/justloveyou_/article/details/72466416 <br>

  - 类的实例化是指创建一个类的实例(对象)的过程

  - 类的初始化是指为类中各个类成员(被 static 修饰的成员变量)赋初始值的过程，是类生命周期中的一个阶段。

  对于虚拟机初始化一个类，在虚拟机规范中指明有五种情况需立即对类进行初始化(发生在加载、验证、准备之后):

  1. 遇到 new、getstatic、putstatic 或 invokestatic、这四条字节码(注意：newarray 指令只是数组类型本身的初始化，而不会导致其相关类型的初始化。比如：new String[]只会直接触发 String[]类的初始化，也就是触发对类[Ljava.lang.String 的初始化，而不会导致触发 String 类的初始化)时，如果类没有初始化，则需要先对其初始化。生成这四条指令最常见的 java 代码场景是：

     - 使用 new 关键字实例化对象的时候
     - 读取或设置一个类的静态字段(被 final 修饰，编译器已把结果放入常量池的静态字段除外)的时候
     - 调用一个类的静态方法的时候

  2. 使用 java.lang.reflect 包的方法对类进行反射调用的时候，如果类没有进行过初始化，则进行初始化类

  3. 当初始化一个类时，如果发现其父类还未初始化过，则需要先触发其父类的初始化

  4. 当虚拟机启动时，用户需要指定一个需要运行的主类(包含 main()方法的那个类)，虚拟机会先初始化这个类

  5. 当使用 jdk1.7 动态语言支持时，如果一个 java.lang.invoke.MethodHandle 实例最后的解析结果 REF_getstatic,REF_putstatic,REF_invokeStatic 的方法句柄，并且这个方法句柄所对应的类没有进行初始化，则需要先出触发其初始化。

  - 注意，对于这五种会触发类进行初始化的场景，虚拟机规范中使用了一个很强烈的限定语：“有且只有”，这五种场景中的行为称为对一个类进行*主动引用*。除此之外，所有引用类的方式，都不会触发初始化，称为*被动引用*
  - 被动引用的几种经典场景

    > https://github.com/duiliuliu/Interview/tree/master/test/src/com/javaBasic/classInitial

    - 通过子类引用父类的静态字段

      ```
      // PassiveReferenceParent
      public class PassiveReferenceParent {
          static {
              System.out.println("This is static block in the PassiveReferenceParent Class .");
          }
      }

      // PassiveReferenceSon_1
      public class PassiveReferenceSon_1 extends PassiveReferenceParent {
          static {
              System.out.println("This is static block in the PassiveReferenceSon_1 Class .");
          }

          public static String value = "PassiveReferenceSon_1_static_feild value";

          public PassiveReferenceSon_1() {
              System.out.println("PassiveReferenceSon_1 class init!");
          }
      }

      // PassiveReferenceSon_2
      public class PassiveReferenceSon_2 extends PassiveReferenceSon_1 {
          static {
              System.out.println("This is static block in the PassiveReferenceSon_2 Class .");
          }

          public PassiveReferenceSon_2() {
              System.out.println("PassiveReferenceSon_2 class init!");
          }
      }

      public static void main(String[] args) {
          System.out.println(PassiveReferenceSon_2.value);
      }

      // output:
        This is static block in the PassiveReferenceParent Class .
        This is static block in the PassiveReferenceSon_1 Class .
        PassiveReferenceSon_1_static_feild value
      ```

      对于静态字段，只有直接定义这个字段的类才会被初始化，因此通过其子类来引用父类中定义的静态字段，只会触发父类的初始化而不会触发子类的初始化。在本例中，由于 value 字段是在类 PassiveReferenceSon_1 中定义的，因此该类会被初始化；此外，在初始化类 PassiveReferenceSon_1 时，虚拟机会发现其父类 PassiveReferenceParent 还未被初始化，因此虚拟机将先初始化父类 PassiveReferenceParent PassiveReferenceSon_1，而 PassiveReferenceSon_2 始终不会被初始化。

    - 通过数组定义来引用类，不会触发此类的初始化

      ```
      PassiveReferenceSon_1[] passiveReferenceSons_1 = new PassiveReferenceSon_1[10];
      ```

      上述案例运行之后并没有任何输出，说明虚拟机并没有初始化类 PassiveReferenceSon_1[com.javaBasic.classInitial.PassiveReference.PassiveReferenceSon_1 的类的初始化。从类名称我们可以看出，这个类代表了元素类型为 PassiveReferenceSon_1 的一维数组，它是由虚拟机自动生成的，直接继承于 Object 的子类，创建动作由字节码指令 newarray 触发。

    - 常量在编译阶段会存入调用类的常量池中，本质上并没有直接引用到定义常量的类，因此不会触发定义常量的类的初始化

      ```
      System.out.println(PassiveReferenceParent.CONSTANT);
      // output:
      hello world
      ```

      述代码运行之后，只输出 “hello world”，这是因为虽然在 Java 源码中引用了 PassiveReferenceParent 类中的常量 CONSTANT，但是编译阶段将此常量的值“hello world”存储到了 NotInitialization 常量池中，对常量 PassiveReferenceParent.CONSTANT 的引用实际都被转化为 NotInitialization 类对自身常量池的引用了。也就是说，实际上 NotInitialization 的 Class 文件之中并没有 PassiveReferenceParent 类的符号引用入口，这两个类在编译为 Class 文件之后就不存在关系了。

- 类的实例化

- 练习

  **java new 一个对象后创建了几个对象**

  **java new 一个 String 对象后，内存中发生了什么**

  **java new 一个对象后，内存中发生了什么**

  **java 创建一个对象有哪几种方法**

#### oom

- 内存溢出 out of memory

程序在申请内存空间时，没有足够的内存空间供其使用，就发生了溢出。如申请一个

- 内存泄漏 memory leak

是指程序在申请内存后，无法释放已申请的内存空间，一次内存泄露危害可以忽略，但内存泄露堆积后果很严重，无论多少内存,迟早会被占光。

- OOM 的可能原因?
  - 数据库的 cursor 没有及时关闭
  - 构造 Adapter 没有使用缓存 contentview
  - RegisterReceiver()与 unRegisterReceiver()成对出现
  - 未关闭 InputStream outputStream
  - Bitmap 使用后未调用 recycle()
  - static 等关键字
  - 非静态内部类持有外部类的引用　 context 泄露


## 分布式



## MySql

#### MyISAM 与 InnoDB 的区别


## MongoDB



## Redis



## ElasticSearch



## Python

- 解决 python 循环引用的方法
  - 延迟导入
  - 将 from xxx import yyy 改为 import xxx;xxx.yyy 来访问的形式
  - 组织代码，将循环变为单向


## JavaScript

#### javaScript 读取 json

- var obj = eval(str)
- var obj = str.parseJSON() <---> obj.toJSONString()
- var obj = JSON.parse(str) <---> JSON.stringify(obj)


## Shell



## Spring

> https://blog.csdn.net/hrbeuwhw/article/details/79476988

#### Spring 常用注解

#### Spring AOP

- 切面（Advisor）：是 AOP 中的一个术语，表示从业务逻辑中分离出来的横切逻辑，比如性能监控，日志记录，权限控制等。
  这些功能都可以从核心的业务逻辑中抽离出去。可以解决代码耦合问题，职责更加单一。封装了增强和切点。
- 增强（Advice）：增强代码的功能的类，横切到代码中。
- 目标：目标方法（JDK 代理）或目标类（CGLIB 代理）
- 代理：JDK 代理，CGLIB 代理。或是通过 ProxyFactory 类生产。
- 切点：通过一个条件来匹配要拦截的类，这个条件称为切点。如拦截所有带 Controller 注解的类。增强的条件。

连接点：作为增强方法的入参，可以获取到目标方法的信息

Spring AOP 原理---动态代理

spring 的动态代理实现有两种：
jdk 的动态代理 (目标对象的实现类实现了接口)
cglib 动态代理 (目标对象的实现类没有实现接口) (通过实现修改字节码来实现)

jdk 动态代理实现的核心类库为 java.lang.reflect.Proxy 类与 java.lang.reflect.InvocationHandler 接口

动态代理的实现为实现 InvocationHandler 接口(invoke(Object o, Method m))，再通过反射接收代理类。
实际代理类继承了 Proxy 类，并实现了 ISubject 接口，由此也可以看到 JDK 动态代理为什么需要实现接口，已经继承了 Proxy 是不能再继承其余类了。

其中实现了 ISubject 的 execute() 方法，并通过 InvocationHandler 中的 invoke() 方法来进行调用的。

CGLIB 动态代理
cglib 是对一个小而快的字节码处理框架 ASM 的封装。 他的特点是继承于被代理类，这就要求被代理类不能被 final 修饰。

#### 例题

- 有哪些不同类型的 IOC（依赖注入）方式

  - 构造器依赖注入
  - setter 方法注入

- 哪种依赖注入方式你建议使用，构造器注入，还是 Setter 方法注入

  - 两种依赖方式都可以使用，构造器注入和 Setter 方法注入。最好的解决方案是用构造器参数实现强制依赖，setter 方法实现可选依赖

- 如何给 Spring 容器提供配置元数据

  - xml 配置
  - 基于注解
  - 基于 java 的配置

- 解释 Spring 支持的几种 bean 的作用域

  Spring 框架支持以下五种 bean 的作用域：

  - singleton
  - prototype
  - request
  - session
  - global-session

  缺省的 Spring bean 的作用域是 Singleton

- Spring 框架中的单例 bean 是线程安全的吗

  Spring 框架中的单例 bean 不是线程安全的


## 面试题

#### 海量数据排序

> https://blog.csdn.net/yusiguyuan/article/details/12903975
 > https://blog.csdn.net/v_JULY_v/article/details/6279498

1. 分而治之/Hash 映射 堆/快速/归并排序

   - 一个 10g 的关键词 log，找出词频最高的前 k 个词，设可用内存为 2g

     难点分析：

     1. 如何在有限的内存中对大量数据进行词频统计
     2. 如何在有限的内存中找出前 k 个高频词

     词频统计：我们自然的会想到 hash，然而数据太多内存不足，所以我们需要将数据分段，然后将每段的结果进行汇总计算即可。

     分段：

     1. 普通分段
     2. hash 计算，按照范围分段(因为是词频统计，尽可能将同样的词划分到一段中，让最后的计算只需要词频排序即可。避免最后还需要对不同分段的同一个词进行词频汇总)

     汇总：汇总所有分段结果进行排序。

     1. 一般汇总，
     2. 缓冲区，汇总部分

     排序：

     对于排序算法的选择，如果一次性将结果汇总，快排/归并/堆排序同样的时间复杂度。
     倘若是按照缓冲区一段段的读取数据进行汇总，此时选用堆排序性能更优。堆排序中，堆的构建需要 o(n)，而堆与堆的调整只需要 lgn，在第一次堆排序需要调整 n-1 次，复杂度为 o(nlogn),但是在之后的调整与读取数据中只需要 lgn 的时间。

2. 多层桶划分
3. bitmap
4. Trie 树/数据库/倒排索引
5. 外部排序
6. 分布式处理之 Map reduce

#### 浏览器中输入 www.taobao.com 发生了什么

#### LRU 算法实现

LRU 全称是 Least Recently Used，即最近最久未使用的意思。

LRU 算法的设计原则是：如果一个数据在最近一段时间没有被访问到，那么在将来它被访问的可能性也很小。也就是说，当限定的空间已存满数据时，应当把最久没有被访问到的数据淘汰。

1.  数组+时间计数
    每访问一次数据，相应的时间计数归零，其他数据时间计数加一，当数组空间已满时，将时间计数最大的数据项淘汰。
2.  链表实现
    每次插入新的数据都选择从头部插入，如果数据已存在，则将数据移到头部；那么当链表满的时候，就将链表尾部的数据丢弃。
3.  链表和 hashmap
    当需要插入新的数据项的时候，如果新数据项在链表中存在（一般称为命中），则把该节点移到链表头部，如果不存在，则新建一个节点，放到链表头部，若缓存满了，则把链表最后一个节点删除即可。在访问数据的时候，如果数据项在链表中存在，则把该节点移到链表头部，否则返回-1。这样一来在链表尾部的节点就是最近最久未访问的数据项。(LinkedHashMap)

#### 设计一个秒杀系统

1. 前端层面，必须提到浏览器缓存、尽可能减少请求数量、使用延迟加载（ajax 技术）、使用 cdn 缓存、压缩静态文件等；

2. web 服务器层面，必须提到开启 gzip 压缩技术、卸载 web 服务器不必要的模块、开启服务器缓存等；

3. PHP 语言层面，开启编译缓存、使用 PHP 扩展实现计算密集型模块、使用 HHVM 或 PHP7 替代旧版本 PHP、修改 php.ini 参数对 PHP 性能调优、减少高消耗函数使用等；

4. 数据库 MySQL 层面，合理创建索引、合理设计数据表结构、尽可能减少数据库连接、对数据库内常用数据进行缓存、读写分离、垂直（水平）分库分表等；

5. 非关系型数据库，使用非关系数据库减少数据库链接、保持数据库与非关系数据库数据一致性等


## 其他

---
---

js 中闭包
python 闭包

innodb

redis，数据类型，消息队列，过期时间问题

装饰者模式

缓冲区溢出及其影响

内存中页的大小，为什么是这么大

问了一下流量控制，还是很隐晦，当时大概问的是“一个服务器有很多 TCP 连接，然后某一时刻他可能来不及处理接受到的数据，这时候该怎么办？”。坦白说刚开始听到我是比较懵 B 的，但是仔细想过之后发现这好像就是流量控制，所以很流利的回答了流量控制，顺道说了一下原理。

1. 开始了算法，先问我二叉树学过吗，然后让我设计一个节点，再然后让我比较两棵树是否相同(手写代码)。现在我才明白，大概是在考我用递归怎么遍历树，我当时写的居然是以按层遍历的方式去遍历树，然后两棵树逐个节点作对比。

死锁，死锁预防，死锁避免，死锁检测

三次握手四次挥手的状态字，为什么 3 次，为什么 4 次

3、Redis 用在项目中的哪些地方

9、为什么要加 volatile 关键字，Synchronized 锁住了什么，如果在构造函数中使用远程调用是否会发生中断
10、一个二维数组，每行每列都是升序排列，求这个数组中第 K 小的数
11、5 亿条淘宝订单，每条订单包含不同的商品号，每个商品号对应不同的购买数量，求出销量最高的 100 个商品

、最后撸了个代码，非常简单，判断链表是否有环。。。

4. 手撕代码：按层次遍历二叉树
5. 手撕代码：按层次遍历二叉树(不完全二叉树)节点为 null 的需要输出 null

6. Elastic Search 索引建立
7. Elastic Search 查询的过程
8. Elastic Search 集群管理、Master 选举，节点特性
