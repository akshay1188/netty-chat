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
		System.out.println(req.toString());
		createImage(req);
	}
	
	public boolean createImage(Request req){
		boolean rtn = false;
		BufferedImage img;
		try {
			img = ImageIO.read(new ByteArrayInputStream(req.getPayload().getData().toByteArray()));
			try {
				ImageIO.write(img, "png", new File("/images/"+imageId+".png"));
//				String query = "insert into CMPE_275.Data values ("+ElectionManager.getInstance().getNodeId()+","+ElectionManager.getInstance().getTermId()+
//						","+imageId+", ../../images/"+imageId+","+req.getHeader().getCaption()+")";
//				db.execute_query(query);
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
