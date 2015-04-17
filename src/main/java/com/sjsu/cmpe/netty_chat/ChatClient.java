package com.sjsu.cmpe.netty_chat;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.io.File;
import java.nio.file.Files;
import java.util.Arrays;


import com.google.protobuf.ByteString;
import com.sjsu.cmpe.netty_chat.Image.Header;
import com.sjsu.cmpe.netty_chat.Image.PayLoad;
import com.sjsu.cmpe.netty_chat.Image.Ping;
import com.sjsu.cmpe.netty_chat.Image.Request;

public class ChatClient implements Runnable {

	int id = 10;

	public static void main(String[] args) {
		new ChatClient("localhost", 5570).run();
	}

	private final String host;
	private final int port;
	private static int clientId = 10;
	private static int clusterId = 9;
	private String caption;
	private String path;
	private final int CHUNK_SIZE = 1000000;//chunk size 1 MB 

	public ChatClient(String host, int port) {
		this.host = host;
		this.port = port;
	}

	public void run() {

		EventLoopGroup group = new NioEventLoopGroup();

		try {

			//take a sample image from disk
			File fi = new File(
					"/Users/devendra/Desktop/test_img.png");
			//convert it into byte array
			byte[] fileContent = Files.readAllBytes(fi.toPath());

			//convert it into chunks of smaller arrays
			byte[][] res = divideArray(fileContent, CHUNK_SIZE);
			//send the chunks
			sendChunks(res);
		} catch (Exception e1) {
			e1.printStackTrace();
		} finally {
			group.shutdownGracefully();
		}
	}

	//this method divides the byte array into chunks
	public static byte[][] divideArray(byte[] source, int chunksize) {

		byte[][] ret = new byte[(int) Math.ceil(source.length
				/ (double) chunksize)][chunksize];

		int start = 0;

		for (int i = 0; i < ret.length; i++) {
			ret[i] = Arrays.copyOfRange(source, start, start + chunksize);
			start += chunksize;
		}

		return ret;
	}
	
	//sending the chunks over the channel
	public void sendChunks(byte[][] chunks){
		EventLoopGroup group = new NioEventLoopGroup();
		
		Bootstrap bootstrap = new Bootstrap().group(group)
				.channel(NioSocketChannel.class)
				.handler(new ChatClientInitializer());

		Channel channel;
		try {
			channel = bootstrap.connect(host, port).sync().channel();
			
			Header.Builder header = Header.newBuilder();
			header.setClientId(1);
			header.setClusterId(4);
			header.setIsClient(true);

			PayLoad.Builder payload = PayLoad.newBuilder();

			Ping.Builder ping = Ping.newBuilder();
			ping.setIsPing(false);

			Request.Builder request = Request.newBuilder();
			
			request.setPing(ping);
			
			header.setCaption("Image caption");
			request.setHeader(header);

			payload.setImgId((int)System.currentTimeMillis());
			payload.setTotalChunks(chunks.length);
			
			//we will break the message into chunks and send it chunk by chunk
			for (int i = 0; i < chunks.length; i++) {
				payload.setData(ByteString.copyFrom(chunks[i]));
				payload.setChunkId(i);
				request.setPayload(payload);
				
				if (channel.isActive()) {
					channel.write(request.build());
					channel.flush();
				}	
			}
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
