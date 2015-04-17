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
	private static int clientId = 3;//the server id to which client is connected 
	private static int clusterId = 4;//the cluster to which the client is connected
	private final int CHUNK = 2000000;//size of an individual chunk - 2MB

	public ChatClient(String host, int port) {
		this.host = host;
		this.port = port;
	}

	public void run() {

		EventLoopGroup group = new NioEventLoopGroup();

		try {
			//pick an image
			File file = new File(
					"/Users/akshay/Desktop/e68557dc-2725-420f-8315-6977ef23cdbe.png");
			
			//convert the image into byte array
			byte[] fileToBytesArray = Files.readAllBytes(file.toPath());

			//convert the byte array further into smaller chunks
			byte[][] res = splitArrayToChunks(fileToBytesArray, CHUNK);
			
			//transfer these chunks to the server over the channel
			transferChunks(res);
			
		} catch (Exception e1) {
			e1.printStackTrace();
		} finally {
			group.shutdownGracefully();
		}
	}

	//this method divides the byte array to smaller chunks
	public static byte[][] splitArrayToChunks(byte[] from, int chunksize) {

		byte[][] ret = new byte[(int) Math.ceil(from.length
				/ (double) chunksize)][chunksize];

		int start = 0;

		//split the byte array
		for (int i = 0; i < ret.length; i++) {
			ret[i] = Arrays.copyOfRange(from, start, start + chunksize);
			start += chunksize;
		}

		return ret;
	}
	
	//send the chunks over the channel
	public void transferChunks(byte[][] chunks){
		EventLoopGroup group = new NioEventLoopGroup();
		
		Bootstrap bootstrap = new Bootstrap().group(group)
				.channel(NioSocketChannel.class)
				.handler(new ChatClientInitializer());

		Channel channel;
		try {
			channel = bootstrap.connect(host, port).sync().channel();
			
			Header.Builder header = Header.newBuilder();
			header.setClientId(clientId);
			header.setClusterId(clusterId);
			header.setIsClient(true);

			PayLoad.Builder payload = PayLoad.newBuilder();
			Ping.Builder ping = Ping.newBuilder();
			ping.setIsPing(false);

			Request.Builder request = Request.newBuilder();
			
			request.setPing(ping);
			
			header.setCaption("Caption");
			request.setHeader(header);

			//identify image using unique id
			payload.setImgId((int)System.currentTimeMillis());
			
			//no of chunks for server(receiver) to know exactly how many chunks are incoming
			payload.setTotalChunks(chunks.length);
			
			//send all the chunks over the channel 
			for (int chunk_no = 0; chunk_no < chunks.length; chunk_no++) {
				
				payload.setData(ByteString.copyFrom(chunks[chunk_no]));
				
				//to identify each chunk
				payload.setChunkId(chunk_no);
				
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
