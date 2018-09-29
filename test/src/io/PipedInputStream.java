package java.io;

/** 
 *PipedInputStream�����PipedOutputStream����ʹ�á��������������벿�֡�
 *��ԭ��Ϊ��PipedInputStream�ڲ���һ��Buffer��
 *PipedInputStream����ʹ��InputStream�ķ�����ȡ��Buffer�е��ֽڡ�
 *PipedInputStream��Buffer�е��ֽ���PipedOutputStream����PipedInputStream�ķ�������ġ�
 */
public class PipedInputStream extends InputStream {

    boolean closedByWriter = false;                      //��ʶ�ж�ȡ����д�뷽�ر�
    volatile boolean closedByReader = false;
    boolean connected = false;                           //�Ƿ�������
    Thread readSide;                                     //��ʶ�ĸ��߳�
    Thread writeSide;

    protected static final int PIPE_SIZE = 1024;         //��������Ĭ�ϴ�С
    protected byte buffer[] = new byte[PIPE_SIZE];       //������
    protected int in = -1;                               //��һ��д���ֽڵ�λ�á�0����գ�in==out������
    protected int out = 0;                               //��һ����ȡ�ֽڵ�λ��

    public PipedInputStream(PipedOutputStream src) throws IOException {         //����Դ��������
                    connect(src);
    }

    public PipedInputStream() {}                                                //Ĭ�Ϲ��������²�һ��ҪconnectԴ

    public void connect(PipedOutputStream src) throws IOException {              //��������Դ,����Դ��connect�������ӵ�ǰ����
        src.connect(this);                                                       
    }

    protected synchronized void receive(int b) throws IOException {             //ֻ��PipedOuputStream����
        checkStateForReceive();                                                 //���״̬��д��
        writeSide = Thread.currentThread();                                     //��Զ��PipedOuputStream
        if (in == out)     awaitSpace();                                        //����������ȣ��ȴ��ռ�

        if (in < 0) {
            in = 0;
            out = 0;
        }

        buffer[in++] = (byte)(b & 0xFF);                                        //����buffer��Ӧ��λ��
        if (in >= buffer.length) {      in = 0;         }                       //inΪ0��ʾbuffer�ѿ�
    }



    synchronized void receive(byte b[], int off, int len)  throws IOException {
        checkStateForReceive();
        writeSide = Thread.currentThread();                                     //��PipedOutputStream���Կ���
        int bytesToTransfer = len;
        while (bytesToTransfer > 0) {
            if (in == out)    awaitSpace();                                     //���ˣ���֪ͨ��ȡ�ģ��ջ�֪ͨд��
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

    private void checkStateForReceive() throws IOException {                           //��鵱ǰ״̬���ȴ�����
        if (!connected) {
            throw new IOException("Pipe not connected");
        } else if (closedByWriter || closedByReader) {
                throw new IOException("Pipe closed");
            } else if (readSide != null && !readSide.isAlive()) {
            throw new IOException("Read end dead");
        }
    }

    private void awaitSpace() throws IOException {                               //Buffer�������ȴ�һ��ʱ��
        while (in == out) {                                                      //in==out��ʾ���ˣ�û�пռ�
            checkStateForReceive();                                              //�����ܶ˵�״̬
            notifyAll();                                                         //֪ͨ��ȡ��
            try {
                wait(1000);
            } catch (InterruptedException ex) {
                throw new java.io.InterruptedIOException();
            }
        }
    }

    synchronized void receivedLast() {                                            //֪ͨ���еȴ����̣߳����Ѿ����ܵ������ֽ�

            closedByWriter = true;                            

            notifyAll();

    }



    public synchronized int read()  throws IOException {
        if (!connected) {                                                         //���һЩ�ڲ�״̬
            throw new IOException("Pipe not connected");
        } else if (closedByReader) {
            throw new IOException("Pipe closed");
        } else if (writeSide != null && !writeSide.isAlive()&& !closedByWriter && (in < 0)) {
            throw new IOException("Write end dead");
        }

        readSide = Thread.currentThread();                                        //��ǰ�̶߳�ȡ
        int trials = 2;                                                           //�ظ�����
        while (in < 0) {
            if (closedByWriter) {              return -1;        }                 //����Ϲرշ���-1
            if ((writeSide != null) && (!writeSide.isAlive()) && (--trials < 0)) { //״̬����
                throw new IOException("Pipe broken");
            }
            notifyAll();                                                          // ���ˣ�֪ͨд��˿���д��
            try {
                wait(1000);
            } catch (InterruptedException ex) {
                throw new java.io.InterruptedIOException();
            }
        }
        int ret = buffer[out++] & 0xFF;                                             
        if (out >= buffer.length) {             out = 0;                }
        if (in == out) {           in = -1;                 }                     //û���κ��ֽ�
        return ret;
    }

    public synchronized int read(byte b[], int off, int len)  throws IOException {
        if (b == null) {                                                          //��������������ȷ��
            throw new NullPointerException();
        } else if (off < 0 || len < 0 || len > b.length - off) {
            throw new IndexOutOfBoundsException();
        } else if (len == 0) {
            return 0;
        }
        int c = read();                                                                //��ȡ��һ��
        if (c < 0) {    return -1;       }                                             //�Ѿ�����ĩβ�ˣ�����-1
        b[off] = (byte) c;                                                             //�����ⲿbuffer��
        int rlen = 1;                                                                  //return-len
        while ((in >= 0) && (--len > 0)) {                                             //��һ��in���ڣ���û�е���len
            b[off + rlen] = buffer[out++];                                             //���η����ⲿbuffer
            rlen++;
            if (out >= buffer.length) {         out = 0;           }       ????????    //����buffer��ĩβ������ͷ��
            if (in == out) {     in = -1;      }                                       //����дλ��һ��ʱ����ʾû������
        }
        return rlen;                                                                   //�������ĳ���
    }

    public synchronized int available() throws IOException {             //���ػ��ж����ֽڿ��Զ�ȡ
        if(in < 0)
            return 0;                                                    //����ĩ�ˣ�û���ֽ�
        else if(in == out)
            return buffer.length;                                        //д��ĺͶ�����һ�£���ʾ��
        else if (in > out)
            return in - out;                                             //д��Ĵ��ڶ���
        else
            return in + buffer.length - out;                             //д���С�ڶ�����
    }

    public void close()  throws IOException {                            //�رյ�ǰ����ͬʱ�ͷ�������ص���Դ
        closedByReader = true;                                           //��ʾ���������ر�
        synchronized (this) {     in = -1;    }                          //ͬ������ǰ����inΪ-1
    }
}
