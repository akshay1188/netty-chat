package com.sjsu.cmpe.netty_chat;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.ObjectInputStream.GetField;
import java.nio.file.Files;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.Date;

import javassist.bytecode.ByteArray;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
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

	public ChatClient(String host, int port) {
		this.host = host;
		this.port = port;
	}

	public void run() {

		EventLoopGroup group = new NioEventLoopGroup();

		try {

			// String str = "test string";
			//
			// Kryo kryo = new Kryo();
			// kryo.register(String.class);
			//
			// ByteArrayOutputStream stream = new ByteArrayOutputStream();
			// Output output = new Output(stream);
			// kryo.writeObject(output, str);
			// output.close(); // Also calls output.flush()
			// byte[] buffer = stream.toByteArray(); // Serialization done, get
			// bytes
			//
			//
			// System.out.println("test string "+ Arrays.toString(buffer));
			//
			// System.out.println("reading "+ kryo.readObject(new Input(new
			// ByteArrayInputStream(buffer)), String.class));

			// Output op = new Output;
			// kryo.writeOb
			File fi = new File(
					"/Users/akshay/Desktop/e68557dc-2725-420f-8315-6977ef23cdbe.png");
			byte[] fileContent = Files.readAllBytes(fi.toPath());
			System.out.println(Arrays.toString(fileContent));
			System.out.println("size " + fileContent.length);

			byte[][] res = divideArray(fileContent, 10000);
			System.out.println("res size " + res.length);
			sendChunks(res);
			/*
			Bootstrap bootstrap = new Bootstrap().group(group)
					.channel(NioSocketChannel.class)
					.handler(new ChatClientInitializer());

			Channel channel = bootstrap.connect(host, port).sync().channel();

			Header.Builder header = Header.newBuilder();
			header.setClientId(1);
			header.setClusterId(4);
			header.setIsClient(true);

			PayLoad.Builder payload = PayLoad.newBuilder();
			fi = new File(
					"/Users/akshay/Desktop/e68557dc-2725-420f-8315-6977ef23cdbe.png");
			fileContent = Files.readAllBytes(fi.toPath());
			payload.setData(ByteString.copyFrom(fileContent));
			System.out.println(Arrays.toString(fileContent));
			System.out.println("size " + fileContent.length);
			
			Ping.Builder ping = Ping.newBuilder();
			ping.setIsPing(false);

			Request.Builder request = Request.newBuilder();
			request.setPayload(payload);
			request.setPing(ping);

			for (int i = 0; i < 1; i++) {
				System.out.println("Please enter caption");
				caption = "" + i;
				header.setCaption(caption);
				System.out.println("Caption enterd is " + caption);
				request.setHeader(header);

				if (channel.isActive()) {
					channel.write(request.build());
					channel.flush();
				}
			}*/
		} catch (Exception e1) {
			e1.printStackTrace();
		} finally {
			group.shutdownGracefully();
		}
	}

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
	
	public static void sendChunks(byte[][] chunks){
		EventLoopGroup group = new NioEventLoopGroup();
		
		Bootstrap bootstrap = new Bootstrap().group(group)
				.channel(NioSocketChannel.class)
				.handler(new ChatClientInitializer());

		Channel channel;
		try {
			channel = bootstrap.connect("localhost", 5570).sync().channel();
			
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
