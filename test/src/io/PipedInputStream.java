package java.io;

/** 
 *PipedInputStream必须和PipedOutputStream联合使用。即必须连接输入部分。
 *其原理为：PipedInputStream内部有一个Buffer，
 *PipedInputStream可以使用InputStream的方法读取其Buffer中的字节。
 *PipedInputStream中Buffer中的字节是PipedOutputStream调用PipedInputStream的方法放入的。
 */
public class PipedInputStream extends InputStream {

    boolean closedByWriter = false;                      //标识有读取方或写入方关闭
    volatile boolean closedByReader = false;
    boolean connected = false;                           //是否建立连接
    Thread readSide;                                     //标识哪个线程
    Thread writeSide;

    protected static final int PIPE_SIZE = 1024;         //缓冲区的默认大小
    protected byte buffer[] = new byte[PIPE_SIZE];       //缓冲区
    protected int in = -1;                               //下一个写入字节的位置。0代表空，in==out代表满
    protected int out = 0;                               //下一个读取字节的位置

    public PipedInputStream(PipedOutputStream src) throws IOException {         //给定源的输入流
                    connect(src);
    }

    public PipedInputStream() {}                                                //默认构造器，下部一定要connect源

    public void connect(PipedOutputStream src) throws IOException {              //连接输入源,调用源的connect方法连接当前对象
        src.connect(this);                                                       
    }

    protected synchronized void receive(int b) throws IOException {             //只被PipedOuputStream调用
        checkStateForReceive();                                                 //检查状态，写入
        writeSide = Thread.currentThread();                                     //永远是PipedOuputStream
        if (in == out)     awaitSpace();                                        //输入和输出相等，等待空间

        if (in < 0) {
            in = 0;
            out = 0;
        }

        buffer[in++] = (byte)(b & 0xFF);                                        //放入buffer相应的位置
        if (in >= buffer.length) {      in = 0;         }                       //in为0表示buffer已空
    }



    synchronized void receive(byte b[], int off, int len)  throws IOException {
        checkStateForReceive();
        writeSide = Thread.currentThread();                                     //从PipedOutputStream可以看出
        int bytesToTransfer = len;
        while (bytesToTransfer > 0) {
            if (in == out)    awaitSpace();                                     //满了，会通知读取的；空会通知写入
            int nextTransferAmount = 0;
            if (out < in) {
                nextTransferAmount = buffer.length - in;
            } else if (in < out) {
                if (in == -1) {
                    in = out = 0;
                    nextTransferAmount = buffer.length - in;
                } else {
                    nextTransferAmount = out - in;
                }
            }

            if (nextTransferAmount > bytesToTransfer)     
                nextTransferAmount = bytesToTransfer;
            assert(nextTransferAmount > 0);
            System.arraycopy(b, off, buffer, in, nextTransferAmount);
            bytesToTransfer -= nextTransferAmount;
            off += nextTransferAmount;
            in += nextTransferAmount;
            if (in >= buffer.length) {     in = 0;      }
        }
    }

    private void checkStateForReceive() throws IOException {                           //检查当前状态，等待输入
        if (!connected) {
            throw new IOException("Pipe not connected");
        } else if (closedByWriter || closedByReader) {
                throw new IOException("Pipe closed");
            } else if (readSide != null && !readSide.isAlive()) {
            throw new IOException("Read end dead");
        }
    }

    private void awaitSpace() throws IOException {                               //Buffer已满，等待一段时间
        while (in == out) {                                                      //in==out表示满了，没有空间
            checkStateForReceive();                                              //检查接受端的状态
            notifyAll();                                                         //通知读取端
            try {
                wait(1000);
            } catch (InterruptedException ex) {
                throw new java.io.InterruptedIOException();
            }
        }
    }

    synchronized void receivedLast() {                                            //通知所有等待的线程（）已经接受到最后的字节

            closedByWriter = true;                            

            notifyAll();

    }



    public synchronized int read()  throws IOException {
        if (!connected) {                                                         //检查一些内部状态
            throw new IOException("Pipe not connected");
        } else if (closedByReader) {
            throw new IOException("Pipe closed");
        } else if (writeSide != null && !writeSide.isAlive()&& !closedByWriter && (in < 0)) {
            throw new IOException("Write end dead");
        }

        readSide = Thread.currentThread();                                        //当前线程读取
        int trials = 2;                                                           //重复两次
        while (in < 0) {
            if (closedByWriter) {              return -1;        }                 //输入断关闭返回-1
            if ((writeSide != null) && (!writeSide.isAlive()) && (--trials < 0)) { //状态错误
                throw new IOException("Pipe broken");
            }
            notifyAll();                                                          // 空了，通知写入端可以写入
            try {
                wait(1000);
            } catch (InterruptedException ex) {
                throw new java.io.InterruptedIOException();
            }
        }
        int ret = buffer[out++] & 0xFF;                                             
        if (out >= buffer.length) {             out = 0;                }
        if (in == out) {           in = -1;                 }                     //没有任何字节
        return ret;
    }

    public synchronized int read(byte b[], int off, int len)  throws IOException {
        if (b == null) {                                                          //检查输入参数的正确性
            throw new NullPointerException();
        } else if (off < 0 || len < 0 || len > b.length - off) {
            throw new IndexOutOfBoundsException();
        } else if (len == 0) {
            return 0;
        }
        int c = read();                                                                //读取下一个
        if (c < 0) {    return -1;       }                                             //已经到达末尾了，返回-1
        b[off] = (byte) c;                                                             //放入外部buffer中
        int rlen = 1;                                                                  //return-len
        while ((in >= 0) && (--len > 0)) {                                             //下一个in存在，且没有到达len
            b[off + rlen] = buffer[out++];                                             //依次放入外部buffer
            rlen++;
            if (out >= buffer.length) {         out = 0;           }                   //读到buffer的末尾，返回头部
            if (in == out) {     in = -1;      }                                       //读、写位置一致时，表示没有数据
        }
        return rlen;                                                                   //返回填充的长度
    }

    public synchronized int available() throws IOException {             //返回还有多少字节可以读取
        if(in < 0)
            return 0;                                                    //到达末端，没有字节
        else if(in == out)
            return buffer.length;                                        //写入的和读出的一致，表示满
        else if (in > out)
            return in - out;                                             //写入的大于读出
        else
            return in + buffer.length - out;                             //写入的小于读出的
    }

    public void close()  throws IOException {                            //关闭当前流，同时释放与其相关的资源
        closedByReader = true;                                           //表示由输入流关闭
        synchronized (this) {     in = -1;    }                          //同步化当前对象，in为-1
    }
}
