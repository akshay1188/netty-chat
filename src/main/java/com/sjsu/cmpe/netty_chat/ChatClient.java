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
import java.nio.file.Files;
import java.util.Arrays;

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
			
			String str = "test string";
			
			Kryo kryo = new Kryo();
			kryo.register(String.class);
			
			ByteArrayOutputStream stream = new ByteArrayOutputStream();
			Output output = new Output(stream);
			kryo.writeObject(output, str);
			output.close(); // Also calls output.flush()
			byte[] buffer = stream.toByteArray(); // Serialization done, get bytes
			
			System.out.println("test string "+ Arrays.toString(buffer));
			
			System.out.println("reading "+ kryo.readObject(new Input(new ByteArrayInputStream(buffer)), String.class));

//			Output op = new Output;
//			kryo.writeOb
			
			Bootstrap bootstrap = new Bootstrap().group(group)
					.channel(NioSocketChannel.class)
					.handler(new ChatClientInitializer());

			Channel channel = bootstrap.connect(host, port).sync().channel();
			
			Header.Builder header = Header.newBuilder();
			header.setClientId(1);
			header.setClusterId(4);
			header.setIsClient(true); 
			
			PayLoad.Builder payload = PayLoad.newBuilder();
			File fi = new File("/Users/akshay/Desktop/e68557dc-2725-420f-8315-6977ef23cdbe.png");
			byte[] fileContent = Files.readAllBytes(fi.toPath());
			payload.setData(ByteString.copyFrom(fileContent));
			System.out.println(Arrays.toString(fileContent));
			
			Ping.Builder ping = Ping.newBuilder();
			ping.setIsPing(false);
			
			Request.Builder request = Request.newBuilder();
			request.setPayload(payload);
			request.setPing(ping);
			
			BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
			while(true){
				System.out.println("Please enter caption");
				caption = br.readLine().toString();
				header.setCaption(caption);
				System.out.println("Caption enterd is "+caption);
				request.setHeader(header);
				
//				System.out.println("Please enter image path");
//				path = br.readLine().toString();
//				System.out.println("Path enterd is "+path);
				
//				header.setCaption(caption);
				
				//System.out.println(Arrays.toString(fileContent));
				
//				req.setHeader(header.build());
//				req.setPing(ping.build());
				
					if(channel.isActive()){
						channel.write(request.build());
						channel.flush();
					}
			 }
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			group.shutdownGracefully();
		}
	}
}
