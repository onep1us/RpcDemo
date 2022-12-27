package client.net;

import client.discovery.RpcDiscovery;
import common.model.RpcRequest;
import common.model.RpcResponse;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author wanjiahao
 */
public class SocketClient implements RpcClient{

    Map<String,Socket> cache = new ConcurrentHashMap<>();
    String address;
    int port;
    RpcDiscovery rpcDiscovery;

    public SocketClient(String address, int port,RpcDiscovery rpcDiscovery) {
        this.address = address;
        this.port = port;
        this.rpcDiscovery = rpcDiscovery;
    }

    @Override
    public RpcResponse sendRequest(RpcRequest rpcRequest) {
        try {
            rpcDiscovery.lookupService(rpcRequest.getInterfaceName());
            Socket socket = new Socket();
            socket.connect(new InetSocketAddress(address, port));
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());
            objectOutputStream.writeObject(rpcRequest);
            Object o = objectInputStream.readObject();
            return (RpcResponse) o;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
