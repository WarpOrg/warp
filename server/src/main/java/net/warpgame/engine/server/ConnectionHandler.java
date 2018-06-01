package net.warpgame.engine.server;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.DatagramPacket;
import net.warpgame.engine.core.component.ComponentRegistry;
import net.warpgame.engine.net.ConnectionState;
import net.warpgame.engine.net.ConnectionStateHolder;
import net.warpgame.engine.net.PacketType;
import net.warpgame.engine.net.event.StateChangeHandler;
import net.warpgame.engine.net.event.StateChangeRequestMessage;
import net.warpgame.engine.net.event.receiver.EventReceiver;
import net.warpgame.engine.server.envelope.ServerInternalMessageEnvelope;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;


/**
 * @author Hubertus
 * Created 26.11.2017
 */
public class ConnectionHandler extends SimpleChannelInboundHandler<DatagramPacket> {

    private ClientRegistry clientRegistry;
    private ComponentRegistry componentRegistry;
    private IncomingPacketProcessor packetProcessor;
    private ConnectionUtil connectionUtil;
    private StateChangeHandler stateChangeHandler;
    private ServerRemoteEventQueue eventQueue;
    private static final Logger logger = LoggerFactory.getLogger(ConnectionHandler.class);


    ConnectionHandler(ClientRegistry clientRegistry,
                      ComponentRegistry componentRegistry,
                      IncomingPacketProcessor packetProcessor,
                      ConnectionUtil connectionUtil,
                      StateChangeHandler stateChangeHandler,
                      ServerRemoteEventQueue eventQueue) {
        this.clientRegistry = clientRegistry;
        this.componentRegistry = componentRegistry;
        this.packetProcessor = packetProcessor;
        this.connectionUtil = connectionUtil;
        this.stateChangeHandler = stateChangeHandler;
        this.eventQueue = eventQueue;
    }

    /**
     * Packet Header:
     * (int) PacketType, (long) timestamp, (int) clientId(except for PACKET_CONNECT)
     */
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, DatagramPacket msg) throws Exception {
        ByteBuf packet = msg.content();
        int packetType = packet.readInt();
        long timeStamp = packet.readLong();
        if (packetType == PacketType.PACKET_CONNECT) registerClient(ctx.channel(), msg.sender());
        else packetProcessor.processPacket(packetType, timeStamp, packet);
    }

    private ByteBuf writeHeader(int packetType) {
        ByteBuf buffer = Unpooled.buffer();
        buffer.writeInt(packetType);
        buffer.writeLong(System.currentTimeMillis());
        return buffer;
    }

    private void registerClient(Channel channel, InetSocketAddress address) {
        Client c = new Client(
                address,
                new EventReceiver(componentRegistry, stateChangeHandler),
                new ConnectionStateHolder(componentRegistry.getComponent(0)));
        int id = clientRegistry.addClient(c);
        ByteBuf packet = connectionUtil.getHeader(PacketType.PACKET_CONNECTED, 4);

        channel.writeAndFlush(
                new DatagramPacket(writeHeader(PacketType.PACKET_CONNECTED).writeInt(id), address));
//        componentRegistry.getComponent(0).triggerEvent(new ConnectedEvent(c));
        logger.info("Client connected from address " + address.toString());
        c.getConnectionStateHolder().setRequestedConnectionState(ConnectionState.SYNCHRONIZING);
        eventQueue.pushEvent(new ServerInternalMessageEnvelope(new StateChangeRequestMessage(ConnectionState.SYNCHRONIZING)));
    }
}
