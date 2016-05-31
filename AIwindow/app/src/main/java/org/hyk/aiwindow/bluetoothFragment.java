package org.hyk.aiwindow;


import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 * Created by hyk on 2016/5/28.
 */
public class bluetoothFragment extends Fragment implements View.OnClickListener{

    //蓝牙搜索
    private ListView bluetoothlistview;
    private BluetoothAdapter bluetoothAdapter;
    private List<String> bluetoothDevices = new ArrayList<>();
    private ArrayAdapter<String> arrayAdapter;
    private BluetoothDevice device;
    private BluetoothSocket clientSocket;
    private AcceptThread acceptThread;
    private UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    private String Name = "SZU_CIE_1548";//启动蓝牙服务端要开启一个名字
    private Button scan;

    private View bluetoothView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        bluetoothView = inflater.inflate(R.layout.bluetoothlayout, container, false);
        bluetoothlistview = (ListView) bluetoothView.findViewById(R.id.bluetoothlist);
        scan = (Button) bluetoothView.findViewById(R.id.scan);
        scan.setOnClickListener(this);
        init2();
        return bluetoothView;
    }

    //蓝牙初始化
    private void init2() {
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();//得到本机蓝牙适配器

        //设置蓝牙设备可见性
        if (!bluetoothAdapter.isEnabled()) {
            Intent discoverableIntent = new Intent(bluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
            discoverableIntent.putExtra(bluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 200);//设置蓝牙的可见时间200秒
            startActivityForResult(discoverableIntent, 1);
        }

        final Set<BluetoothDevice> devices = bluetoothAdapter.getBondedDevices();
        if (devices.size() > 0) {
            for (BluetoothDevice device : devices) { // 将已配对的设备信息先加入到List中
                for(String device2 : bluetoothDevices) {//如果已经存在就不放入list表中
                    if (!device.getAddress().toString().equals(device2)) {
                        bluetoothDevices.add(device.getName() + ":" + device.getAddress() + "\n");
                    }
                }
            }
        }

        arrayAdapter =
                new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, android.R.id.text1, bluetoothDevices);
        bluetoothlistview.setAdapter(arrayAdapter);
        bluetoothlistview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                String s = arrayAdapter.getItem(i);//拿到设备
                String address = s.substring(s.indexOf(":") + 1).trim();

                if (bluetoothAdapter.isDiscovering()) {
                    bluetoothAdapter.cancelDiscovery();
                }
                Toast.makeText(getContext(),"正在尝试连接",Toast.LENGTH_SHORT);
                try {
                    if (device == null) {
                        device = bluetoothAdapter.getRemoteDevice(address);
                    }

                    if (clientSocket == null) {
                        clientSocket = device.createRfcommSocketToServiceRecord(uuid);
                        clientSocket.connect();
                        //  os=clientSocket.getOutputStream();//得到蓝牙发送过来的消息
                        Toast.makeText(getActivity(), "成功连接", Toast.LENGTH_SHORT).show();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(getActivity(), "连接失败，请检查原因", Toast.LENGTH_SHORT).show();
                }
            }
        });
        // 创建一个intentFilter，将其action指定为FOUND，只有符合该过滤器的意图才会被broadcast receiver所接收
        IntentFilter intentFilter = new IntentFilter(BluetoothDevice.ACTION_FOUND);

        getActivity().registerReceiver(bluetoothReceiver, intentFilter);

    }


    //注册广播接收器
    private BroadcastReceiver bluetoothReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);// BluetoothDevice.EXTRA_DEVICE是一个键，拿到远程蓝牙设备

            if (device.getBondState() != BluetoothDevice.BOND_BONDED) {
                bluetoothDevices.add(device.getName() + ":" + device.getAddress() + "\n");
                arrayAdapter.notifyDataSetChanged();
            }
        }
    };


    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.scan:
                if (bluetoothAdapter.isEnabled()) {//蓝牙启动则开始扫描

                    if (bluetoothAdapter.isDiscovering()) {
                        bluetoothAdapter.cancelDiscovery();
                    }

                    bluetoothAdapter.startDiscovery();
                    acceptThread = new AcceptThread();
                    acceptThread.start();
                }
                break;
        }

    }

    private class AcceptThread extends Thread {

        private BluetoothServerSocket serverSocket;//服务端，用于监听蓝牙设备
        //socket连接请求
        private BluetoothSocket socket;//客户端，使用它发生和接收字符串
        private InputStream is;
        private OutputStream os;

        public AcceptThread() {
            try {
                //启动名为Name的服务端监听器监听蓝牙设备
                serverSocket = bluetoothAdapter.listenUsingRfcommWithServiceRecord(Name, uuid);
                Toast.makeText(getContext(),"正在搜索附近设备",Toast.LENGTH_SHORT);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void run() {
            try {
                socket = serverSocket.accept(); // 如果没有客户端请求，会阻塞
                is = socket.getInputStream();
                while (true) {
                    //这里编写接受到的is数据，之后要编写button开关窗
                }

            } catch (IOException e) {
                e.printStackTrace();
            }

        }

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }
}