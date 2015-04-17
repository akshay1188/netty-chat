package com.sjsu.cmpe.netty_chat;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import com.sjsu.cmpe.netty_chat.Image.Request;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundMessageHandlerAdapter;

public class ChatClientHandler extends ChannelInboundMessageHandlerAdapter<Request> {
	private static int imageId; 
	@Override
	public void messageReceived(ChannelHandlerContext arg0, Request req) throws Exception{
		createImage(req);
	}
	
	//save the image to disk and add an entry to the database
	public boolean createImage(Request req){
		boolean rtn = false;
		BufferedImage img;
		try {
			img = ImageIO.read(new ByteArrayInputStream(req.getPayload().getData().toByteArray()));
			try {
				ImageIO.write(img, "png", new File("/images/"+imageId+".png"));
			}
			catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			imageId++;
			rtn = true;
		}
		catch (IOException e) {
			e.printStackTrace();
			rtn = false;
		}
		return rtn;
	}

}
