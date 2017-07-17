package com.example.rui.androidstudy.socket;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

import com.example.rui.androidstudy.R;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Inet4Address;
import java.net.InetSocketAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.Socket;
import java.net.SocketException;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.Bind;
import butterknife.ButterKnife;

public class SocketActivity extends AppCompatActivity {

    String strLocalPath, strLocalIp, strVersion;
    Socket socket = null;

    String strServerIp = "121.40.178.87";
    int iServerPort = 15888;
    Boolean connectServer = false;
    OutputStream ou;
    InputStream in;
    public final static int MAXSOCKETDATALEN = 1500;
    public final static int MAXRECVSOCKETDATALEN = (1500 * 4);

    /**
     * Socket线程函数
     */
    class MyThread extends Thread {
        private boolean flag = true;
        @Override
        public void run() {
            //等待结束后,如果 socket已连接至服务器  则跳出线程
            //定义消息
            Message msg = new Message();
            msg.what = 0xFF;
            Bundle bundle = new Bundle();
            if (flag) {
                try {
                    socket = new Socket();
                    socket.connect(new InetSocketAddress(strServerIp, iServerPort), 5000);
                    Log.i(this.toString(),"连接状态"+socket.isConnected());
                    if (socket.isConnected()) {
                        msg.what = 0xFF;
                        msg.arg1 = 1;
                        bundle.putByteArray("msg", "连接服务器成功".getBytes());
                        msg.setData(bundle);
                        connectServer = true;
                        mySocketHandler.sendMessage(msg);
                        socket.setTcpNoDelay(true);
                        socket.setSendBufferSize(1024 * 10240);
                        socket.setReceiveBufferSize(1024 * 5120);
                    } else {
                        msg.what = 0xFF;
                        msg.arg1 = 2;
                        bundle.putByteArray("msg", ("正在连接服务器[" + strServerIp + "],请稍后...").getBytes());
                        msg.setData(bundle);
                        connectServer = false;
                        mySocketHandler.sendMessage(msg);
                    }
                } catch (IOException e) {
                    msg.what = 0xFF;
                    msg.arg1 = 2;
                    bundle.putByteArray("msg", ("正在连接服务器[" + strServerIp + "],请稍后...").getBytes());
                    msg.setData(bundle);
                    connectServer = false;
                    mySocketHandler.sendMessage(msg);
                }

                if (connectServer) {
                    StartRecvData();
                }
            }
        }
    }

    public Handler mySocketHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            Bundle bundle = msg.getData();
            int iCmdLen = bundle.getInt("length");

            byte[] buffer = new byte[iCmdLen];
            Arrays.fill(buffer, (byte) '\0');

            byte[] bufToPrcess = new byte[iCmdLen];
            Arrays.fill(bufToPrcess, (byte) '\0');

            buffer = bundle.getByteArray("msg");
            switch (msg.what) {
                case 0xFF:
                    Log.i(this.toString(),"信息"+buffer.toString());
                    socketConnect.setText(new String(buffer));
                    break;
                case 0x00:
                    socketConnect.setText(""+socket.isConnected());
                    break;
            }
        }
    };

    @Bind(R.id.socket_connect)
    TextView socketConnect;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_socket);
        ButterKnife.bind(this);
        strLocalPath = this.getFilesDir().getAbsolutePath();

        //获取本机IP
        strLocalIp = getPhoneIp().toString();

        //获取软件版本号
        strVersion = getVersion();
        Log.i(this.toString(),"ip地址"+strLocalIp);
        MyThread myThread = new MyThread();
        myThread.start();
    }

    /**
     * Socket数据接收
     */
    private void StartRecvData() {
        BufferedReader bff = null;
        try {
            //获取输出流
            ou = socket.getOutputStream();
            //获取输入流
            in = socket.getInputStream();
            bff = new BufferedReader(new InputStreamReader(socket.getInputStream(), "GBK"));

            SendLoacalIpInfo();

            byte[] RecvBuff, StoreBuff;
            RecvBuff = new byte[MAXSOCKETDATALEN];
            StoreBuff = new byte[MAXSOCKETDATALEN];
            Arrays.fill(RecvBuff, (byte) '\0');
            Arrays.fill(StoreBuff, (byte) '\0');

            byte[] pDivbuffer = new byte[MAXRECVSOCKETDATALEN];
            byte[] pSendBuffer = new byte[MAXRECVSOCKETDATALEN];
            Arrays.fill(pDivbuffer, (byte) '\0');
            Arrays.fill(pSendBuffer, (byte) '\0');

            int i_StoreLen = 0, i_GetBuffLen = 0;

            int i_RecieveCount = 0, i_CmdLength = 0, i_HaveProcessLen = 0;
            String sendBuffer = null;

            int iErrorCount = 0;
            boolean bSockBreaked = false;
            //读取发来服务器信息
            while (!bSockBreaked) {
                try {
                    i_RecieveCount = in.read(RecvBuff);
                    iErrorCount = 0;
                } catch (SocketException e) {
                    String strError = e.toString();
                    if (strError.indexOf("recvfrom failed: ETIMEDOUT (Connection timed out)") != -1) {
                        iErrorCount++;
                        Thread.sleep(1000);
                        if (iErrorCount > 10) {
                            break;
                        }
                        i_RecieveCount = 0;
                    } else {
                        break;
                    }
                }
                if (i_RecieveCount > 0) {
                    i_HaveProcessLen = 0;
                    i_GetBuffLen = 0;
                    Arrays.fill(pDivbuffer, (byte) '\0');

                    if (i_StoreLen > 0) {
                        System.arraycopy(StoreBuff, 0, pDivbuffer, 0, i_StoreLen);
                        i_GetBuffLen = i_StoreLen;
                        i_StoreLen = 0;
                    }

                    System.arraycopy(RecvBuff, 0, pDivbuffer, i_GetBuffLen, i_RecieveCount);
                    i_GetBuffLen += i_RecieveCount;

                    //分解连续包
                    while ((i_GetBuffLen - i_HaveProcessLen) > 0) {
                        i_CmdLength = FromByteToInt(pDivbuffer[i_HaveProcessLen]) * 0xFF + FromByteToInt(pDivbuffer[i_HaveProcessLen + 1]);    //包长度
                        if (i_CmdLength < 3) {
                            break;
                        }
                        if ((i_GetBuffLen - i_HaveProcessLen) < i_CmdLength) {
                            Arrays.fill(StoreBuff, (byte) '\0');
                            i_StoreLen = i_GetBuffLen - i_HaveProcessLen;
                            System.arraycopy(pDivbuffer, i_HaveProcessLen, StoreBuff, 0, i_StoreLen);
                            break;
                        }
                        Arrays.fill(pSendBuffer, (byte) '\0');
                        System.arraycopy(pDivbuffer, i_HaveProcessLen, pSendBuffer, 0, i_CmdLength);
                        HandlerReqBytes(pSendBuffer, i_CmdLength);
                        i_HaveProcessLen = i_HaveProcessLen + i_CmdLength;
                    }
                } else {
                    if (i_RecieveCount < 0) {
                        bSockBreaked = true;
                    }
                }
                Arrays.fill(RecvBuff, (byte) '\0');
            }
            //关闭输出流
            try {
                bff.close();
                ou.close();
                socket.close();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            // ConnectServer(15);
        } catch (Exception e) {
            //关闭输出流
            try {
                bff.close();
                ou.close();
                socket.close();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
    }

    /**
     * Socket接收处理函数
     * byte[]
     **/
    void HandlerReqBytes(byte[] buffer, int iCmdLen)  //Socket 接收处理函数
    {
        //定义消息
        Message msg = new Message();
        Bundle bundle = new Bundle();
        bundle.clear();

        byte[] GetByte = new byte[iCmdLen];
        Arrays.fill(GetByte, (byte) '\0');
        System.arraycopy(buffer, 0, GetByte, 0, iCmdLen);

        msg.what = buffer[2];

        try {
            bundle.putByteArray("msg", GetByte);
            bundle.putInt("length", iCmdLen);
        } catch (Exception e) {
            e.printStackTrace();
        }
        msg.setData(bundle);

        mySocketHandler.sendMessage(msg);
    }

    /**
     * 字符串转码
     * Byte2int
     */
    public static int FromByteToInt(byte SourceByte) {
        int iConvertValue = 0x00;
        iConvertValue = (SourceByte & 0xFF);
        return iConvertValue;
    }

    /**
     * 发送本地ip
     */
    private void SendLoacalIpInfo() {
        strLocalIp = getPhoneIp().toString();
        int iType = 4;
        String strSend = String.format("<COL>%s</COL><COL>%s</COL><COL>%d</COL>", strLocalIp, strServerIp, iType);
        int iSendLen = strSend.length();

        char[] SendCmd = new char[iSendLen + 4];
        Arrays.fill(SendCmd, '\0');

        SendCmd[0] = (char) ((iSendLen + 4) / 255);
        SendCmd[1] = (char) ((iSendLen + 4) % 255);
        SendCmd[2] = (char) 0x00;
        SendCmd[3] = (char) 0x03;

        System.arraycopy(strSend.toCharArray(), 0, SendCmd, 4, iSendLen);
        SendBufferBySocket(String.copyValueOf(SendCmd));
    }

    /**
     * Socket发送函数
     * String
     */
    int SendBufferBySocket(String buffer)           //Socket 发送函数
    {
        int iReslen = -1;
        //定义消息
        Message msg = new Message();
        Bundle bundle = new Bundle();
        bundle.clear();
        try {
            if (connectServer) {
                //向服务器发送信息
                if (!ou.equals(null)) {
                    ou.write(buffer.getBytes("gbk"));
                    ou.flush();
                    iReslen = buffer.length();
                }
            }
        } catch (Exception e) {
            //ConnectServer(6);
            iReslen = -1;
        }

        return iReslen;
    }

    /**
     * 得到当前的手机IP地址
     *
     * @return
     */

    public String getPhoneIp() {
        try {
            for (Enumeration<NetworkInterface> e = NetworkInterface.getNetworkInterfaces(); e.hasMoreElements(); ) {
                NetworkInterface item = e.nextElement();
                if (item.getName().toLowerCase().equals("eth0") || item.getName().toLowerCase().equals("wlan0")) {
                    for (InterfaceAddress address : item.getInterfaceAddresses()) {
                        if (address.getAddress() instanceof Inet4Address) {
                            Inet4Address inet4Address = (Inet4Address) address.getAddress();
                            return inet4Address.getHostAddress();
                        }
                    }
                }
            }
        } catch (IOException ex) {
        }
        return "127.0.0.1";
    }

    /**
     * 获取版本号
     *
     * @return 当前应用的版本号
     */
    public String getVersion() {
        try {
            PackageManager manager = this.getPackageManager();
            PackageInfo info = manager.getPackageInfo(this.getPackageName(), 0);
            String version = info.versionName;
            return version;
        } catch (Exception e) {
            e.printStackTrace();
            return "1.26.01";
        }
    }
}

