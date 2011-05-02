/** Copyright (c) 2008-2011, Brooklyn eXperimental Media Center
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *     * Redistributions of source code must retain the above copyright
 *       notice, this list of conditions and the following disclaimer.
 *     * Redistributions in binary form must reproduce the above copyright
 *       notice, this list of conditions and the following disclaimer in the
 *       documentation and/or other materials provided with the distribution.
 *     * Neither the name of Brooklyn eXperimental Media Center nor the
 *       names of its contributors may be used to endorse or promote products
 *       derived from this software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL Brooklyn eXperimental Media Center BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package edu.poly.bxmc.betaville.net;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;
import java.util.Iterator;
import java.util.Set;

/**
 * @author Skye Book
 *
 */
public class RemoteControlServer {

	/**
	 * @throws IOException 
	 * @throws CharacterCodingException 
	 * 
	 */
	public RemoteControlServer() throws CharacterCodingException, IOException {
		Charset charset = Charset.forName("ISO-8859-1");
		CharsetEncoder encoder = charset.newEncoder();
		CharsetDecoder decoder = charset.newDecoder();

		ByteBuffer buffer = ByteBuffer.allocate(512);

		Selector selector = Selector.open();

		ServerSocketChannel server = ServerSocketChannel.open();
		server.socket().bind(new java.net.InetSocketAddress(8000));
		server.configureBlocking(false);
		SelectionKey serverkey = server.register(selector, SelectionKey.OP_ACCEPT);

		for (;;) {
			selector.select();
			Set<SelectionKey> keys = selector.selectedKeys();

			for (Iterator<SelectionKey> i = keys.iterator(); i.hasNext();) {
				SelectionKey key = (SelectionKey) i.next();
				i.remove();

				if (key == serverkey) {
					if (key.isAcceptable()) {
						SocketChannel client = server.accept();
						client.configureBlocking(false);
						SelectionKey clientkey = client.register(selector, SelectionKey.OP_READ);
						clientkey.attach(new Integer(0));
					}
				} else {
					SocketChannel client = (SocketChannel) key.channel();
					if (!key.isReadable())
						continue;
					int bytesread = client.read(buffer);
					if (bytesread == -1) {
						key.cancel();
						client.close();
						continue;
					}
					buffer.flip();
					
					// read the command code and move forward
					byte commandCode = buffer.get();
					
					// do whatever needs to be done
					if(commandCode==RemoteCodes.FORWARD){
					}
					else if(commandCode==RemoteCodes.BACKWARD){
					}
					else if(commandCode==RemoteCodes.STRAFE_LEFT){
					}
					else if(commandCode==RemoteCodes.STRAFE_RIGHT){
					}
					else if(commandCode==RemoteCodes.ROTATE_UP){
					}
					else if(commandCode==RemoteCodes.ROTATE_DOWN){
					}
					else if(commandCode==RemoteCodes.ROTATE_LEFT){
					}
					else if(commandCode==RemoteCodes.ROTATE_RIGHT){
					}
					else if(commandCode==RemoteCodes.ALTITUDE_UP){
					}
					else if(commandCode==RemoteCodes.ALTITUDE_DOWN){
					}
					else if(commandCode==RemoteCodes.GO_TO){
					}
					else if(commandCode==RemoteCodes.ROTATE_TO){
					}
					else if(commandCode==RemoteCodes.VOLUME_MUTE){
					}
					else if(commandCode==RemoteCodes.VOLUME_UP){
					}
					else if(commandCode==RemoteCodes.VOLUME_DOWN){
					}
					else if(commandCode==RemoteCodes.REQUEST_CONTROL){
					}
					else if(commandCode==RemoteCodes.ABDICATE_CONTROL){
					}
					
					String request = decoder.decode(buffer).toString();
					buffer.clear();
					
					
					if (request.trim().equals("quit")) {
						client.write(encoder.encode(CharBuffer.wrap("Bye.")));
						key.cancel();
						client.close();
					} else {
						int num = ((Integer) key.attachment()).intValue();
						String response = num + ": " + request.toUpperCase();
						client.write(encoder.encode(CharBuffer.wrap(response)));
						key.attach(new Integer(num + 1));
					}
				}
			}
		}
	}
}
