package common;


import io.netty.channel.Channel;

import java.net.InetSocketAddress;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author wanjiahao
 */
public class ChannelHolder {

    private final Map<InetSocketAddress, Channel> channelMap;

    public ChannelHolder() {
        channelMap = new ConcurrentHashMap<>();
    }

    public Channel get(InetSocketAddress inetSocketAddress){
        if(!channelMap.containsKey(inetSocketAddress)){
            return null;
        }
        Channel channel = channelMap.get(inetSocketAddress);
        if(null == channel || channel.isActive()){
            channelMap.remove(inetSocketAddress);
            return null;
        }
        return channel;
    }

    public void remove(InetSocketAddress inetSocketAddress){
        channelMap.remove(inetSocketAddress);
    }
}
