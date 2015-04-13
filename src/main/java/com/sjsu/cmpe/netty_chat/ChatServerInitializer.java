package com.sjsu.cmpe.netty_chat;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.Delimiters;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;

public class ChatServerInitializer extends ChannelInitializer<SocketChannel> {

	@Override
	protected void initChannel(SocketChannel arg0) throws Exception {
		// TODO Auto-generated method stub
ChannelPipeline pipeline = arg0.pipeline();
		
		pipeline.addLast("framer",new DelimiterBasedFrameDecoder(8162, Delimiters.lineDelimiter()));
		pipeline.addLast("decoder",new StringDecoder());
		pipeline.addLast("encoder",new StringEncoder());
		
		pipeline.addLast("handler", new ChatServerHandler());
	}

}
