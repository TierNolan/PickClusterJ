package org.tiernolan.pickcluster.net.chainparams.bitcoin.message;

import java.io.IOException;

import org.tiernolan.pickcluster.net.chainparams.bitcoin.BitcoinMessage;
import org.tiernolan.pickcluster.net.message.PongMessage;
import org.tiernolan.pickcluster.types.endian.EndianDataInputStream;
import org.tiernolan.pickcluster.types.endian.EndianDataOutputStream;
import org.tiernolan.pickcluster.util.StringCreator;

public class Pong extends BitcoinMessage implements PongMessage {
	
	private final long nonce;
	
	public Pong(long nonce) {
		super("pong");
		this.nonce = nonce;
	}
	
	public Pong(int version, EndianDataInputStream in) throws IOException {
		super("pong");
		this.nonce = in.readLELong();
	}

	@Override
	public void write(int version, EndianDataOutputStream out) throws IOException {
		out.writeLELong(nonce);
	}
	
	public long getNonce() {
		return this.nonce;
	}
	
	@Override
	public String getDataString() {
		StringCreator sc = new StringCreator();
		sc.add("nonce", this.nonce);
		return sc.toString();
		
	}

	@Override
	public int estimateDataSize() {
		return 8;
	}
	
	@Override
	public long getPingNonce() {
		return nonce;
	}

}