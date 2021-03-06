package net.warpgame.engine.server;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.socket.DatagramPacket;
import net.warpgame.engine.core.context.service.Profile;
import net.warpgame.engine.core.context.service.Service;
import net.warpgame.engine.net.ConnectionTools;
import net.warpgame.engine.net.PacketType;

import java.net.InetSocketAddress;

/**
 * @author Hubertus
 * Created 29.12.2017
 */
@Service
@Profile("server")
public class ConnectionUtil implements ConnectionTools {

    private Channel outChannel;

    public ByteBuf getHeader(PacketType packetType, int initialCapacity) {
        ByteBuf byteBuf = Unpooled.buffer(initialCapacity + 12, 2048);
        byteBuf.writeInt(packetType.ordinal());
        byteBuf.writeLong(System.currentTimeMillis());
        return byteBuf;
    }

    public void sendPacket(ByteBuf content, Client target) {
        sendPacket(content, target.getAddress());
    }

    public void sendPacket(ByteBuf content, InetSocketAddress targetAddress) {
        outChannel.writeAndFlush(new DatagramPacket(content, targetAddress));
    }

    public Channel getOutChannel() {
        return outChannel;
    }

    public void setOutChannel(Channel outChannel) {
        this.outChannel = outChannel;
    }

    public void confirmEvent(int dependencyId, Client client) {
        ByteBuf packet = getHeader(PacketType.PACKET_MESSAGE_CONFIRMATION, 4);
        packet.writeInt(dependencyId);
        sendPacket(packet, client.getAddress());
    }

    @Override
    public int getPeerId() {
        return 0;
    }
}