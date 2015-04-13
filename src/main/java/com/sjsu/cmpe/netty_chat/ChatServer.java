package com.sjsu.cmpe.netty_chat;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

public class ChatServer {

	public static void main(String[] args) {
	
		new ChatServer(8000).run();
			
	}
	
	private final int port;
	
	public ChatServer(int port){
		this.port=port;
	} 
	
	public void run(){
		EventLoopGroup bossGroup = new NioEventLoopGroup();
		EventLoopGroup workerGroup = new NioEventLoopGroup();
		
		try{
			ServerBootstrap bootstrap = new ServerBootstrap()
			.group(bossGroup, workerGroup)
			.channel(NioServerSocketChannel.class)
			.childHandler(new ChatServerInitializer());
			
			bootstrap.bind(port).sync().channel().closeFuture().sync();
			
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		finally{
			bossGroup.shutdownGracefully();
			workerGroup.shutdownGracefully();
		}
		
		
	}

}
