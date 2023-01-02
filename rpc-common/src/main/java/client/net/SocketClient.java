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
    RpcDiscovery rpcDiscovery;

    public SocketClient(RpcDiscovery rpcDiscovery) {
        this.rpcDiscovery = rpcDiscovery;
    }

    @Override
    public RpcResponse sendRequest(RpcRequest rpcRequest) {
        try {
            InetSocketAddress inetSocketAddress = rpcDiscovery.lookupService(rpcRequest.getInterfaceName());
            Socket socket = new Socket();
            socket.connect(new InetSocketAddress(inetSocketAddress.getHostString(), inetSocketAddress.getPort()));
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
