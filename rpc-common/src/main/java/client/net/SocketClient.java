package client.net;

import client.discovery.RpcDiscovery;
import model.RpcRequest;
import model.RpcResponse;
import protocol.RpcProtocol;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;

/**
 * @author wanjiahao
 */
public class SocketClient implements RpcClient{

    RpcDiscovery rpcDiscovery;

    public SocketClient(RpcDiscovery rpcDiscovery) {
        this.rpcDiscovery = rpcDiscovery;
    }

    @Override
    public RpcResponse sendRequest(RpcProtocol<RpcRequest> rpcProtocol) {
        try {
            RpcRequest rpcRequest = rpcProtocol.getBody();
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
