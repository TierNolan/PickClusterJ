package org.tiernolan.pickcluster.types;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

import org.tiernolan.pickcluster.types.endian.EndianDataInputStream;
import org.tiernolan.pickcluster.types.endian.EndianDataOutputStream;
import org.tiernolan.pickcluster.util.StringCreator;

public class NetAddr implements NetType {
	
	public static final NetAddr NULL_ADDRESS;
	public static final NetAddr EXAMPLE;
	
	static {
		NetAddr addr = null;
		try {
			addr = new NetAddr(0, new SocketAddressType(InetAddress.getByAddress(new byte[16]), 0));
		} catch (UnknownHostException e) {
			throw new IllegalStateException(e);
		}
		NULL_ADDRESS = addr;
		EXAMPLE = NULL_ADDRESS;
		
	}
	
	private final boolean version;
	private final int timestamp;
	private final long services;
	private final SocketAddressType socket;
	
	public NetAddr(long services, SocketAddressType socket) {
		this(true, 0, services, socket);
	}
	
	public NetAddr(int timestamp, long services, SocketAddressType socket) {
		this(false, timestamp, services, socket);
	}
	
	private NetAddr(boolean version, int timestamp, long services, SocketAddressType socket) {
		this.timestamp = timestamp;
		this.services = services;
		this.socket = socket;
		this.version = version;
	}
	
	public NetAddr(boolean version, EndianDataInputStream in) throws IOException {
		this.version = version;
		if (!version) {
			this.timestamp = in.readLEInt();
		} else {
			this.timestamp = 0;
		}
		this.services = in.readLELong();
		this.socket = new SocketAddressType(in);
	}
	
	public long getAddressPrefix() {
		return socket.getAddressPrefix();
	}
	
	public long getTimestamp() {
		return timestamp & 0xFFFFFFFFL;
	}
	
	public long getServices() {
		return services;
	}
	
	public InetAddress getAddress() {
		return socket.getAddress();
	}
	
	public int getPort() {
		return socket.getPort();
	}
	
	public SocketAddressType getAddr() {
		return socket;
	}
	
	public void write(int version, EndianDataOutputStream out) throws IOException {
		write(out);
	}
	
	public void write(EndianDataOutputStream out) throws IOException {
		if (!version) {
			out.writeLEInt(this.timestamp);
		}
		out.writeLELong(this.services);
		this.socket.write(out);
	}
	
	public NetAddr read(int version, EndianDataInputStream in, Object ... extraParams) throws IOException {
		return new NetAddr((Boolean) extraParams[0], in);
	}
	
	public String toString() {
		StringCreator sc = new StringCreator();
		if (!version) {
			sc.add("timestamp", timestamp);
		}
		sc.add("services", Long.toHexString(services));
		sc.add("socket", socket);
		return sc.toString();
	}

	@Override
	public int estimateSize() {
		return 4 + 8 + this.socket.estimateSize();
	}
	
	@Override
	public boolean equals(Object o) {
		if (o instanceof NetAddr) {
			NetAddr other = (NetAddr) o;
			return other.version == this.version && other.timestamp == this.timestamp && other.services == this.services && other.socket.equals(this.socket);
		}
		return false;
	}
	
	@Override
	public int hashCode() {
		return (int) (this.timestamp + (this.services ^ (this.services >> 32)) + this.socket.hashCode());
	}
	
}
