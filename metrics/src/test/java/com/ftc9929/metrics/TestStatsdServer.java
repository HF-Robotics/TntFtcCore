/*
 Copyright (c) 2021 The Tech Ninja Team (https://ftc9929.com)

 Permission is hereby granted, free of charge, to any person obtaining a copy
 of this software and associated documentation files (the "Software"), to deal
 in the Software without restriction, including without limitation the rights
 to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 copies of the Software, and to permit persons to whom the Software is
 furnished to do so, subject to the following conditions:

 The above copyright notice and this permission notice shall be included in all
 copies or substantial portions of the Software.

 THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 SOFTWARE.
 */

/**
 * A wrapper around DogstatsD's Dummy server from their unit tests, that finds available
 * ports (borrowed from Spring Framework, but we did not want to pull all of that in)
 * to allow us to assert that the metrics collector is correctly sending data.
 */
package com.ftc9929.metrics;

import java.io.Closeable;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

import javax.net.ServerSocketFactory;

public class TestStatsdServer implements Closeable {
    private final int portNumber;

    private final DummyStatsDServer statsDServer;

    public TestStatsdServer() throws IOException {
        portNumber = findAvailableRandomPort();
        statsDServer = new DummyStatsDServer(portNumber);
    }

    public int getPortNumber() {
        return portNumber;
    }

    public List<String> messagesReceived() {
        return statsDServer.messagesReceived();
    }

    @Override
    public void close() throws IOException {
        statsDServer.close();
    }

    // From DogstatsD client
    @SuppressWarnings("unused")
    class DummyStatsDServer {
        private final List<String> messagesReceived = new ArrayList<String>();
        private AtomicInteger packetsReceived = new AtomicInteger(0);

        protected final DatagramChannel server;
        protected volatile Boolean freeze = false;

        DummyStatsDServer(int port) throws IOException {
            server = DatagramChannel.open();
            server.bind(new InetSocketAddress(port));
            this.listen();
        }

        protected void listen() {
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    final ByteBuffer packet = ByteBuffer.allocate(1500);

                    while (server.isOpen()) {
                        if (freeze) {
                            try {
                                Thread.sleep(10);
                            } catch (InterruptedException e) {
                            }
                        } else {
                            try {
                                ((Buffer) packet).clear();  // Cast necessary to handle Java9 covariant return types
                                // see: https://jira.mongodb.org/browse/JAVA-2559 for ref.
                                server.receive(packet);
                                packetsReceived.addAndGet(1);

                                packet.flip();

                                for (String msg : StandardCharsets.UTF_8.decode(packet).toString().split("\n")) {
                                    synchronized (messagesReceived) {
                                        messagesReceived.add(msg.trim());
                                    }
                                }
                            } catch (IOException e) {
                            }
                        }
                    }
                }
            });
            thread.setDaemon(true);
            thread.start();
        }

        public void waitForMessage() {
            waitForMessage(null);
        }

        public void waitForMessage(String prefix) {
            boolean done = false;

            while (!done) {
                try {
                    synchronized (messagesReceived) {
                        done = !messagesReceived.isEmpty();
                    }

                    if (done && prefix != null && prefix != "") {
                        done = false;
                        List<String> messages = this.messagesReceived();
                        for (String message : messages) {
                            if (message.contains(prefix)) {
                                return;
                            }
                        }
                    }
                    Thread.sleep(100L);
                } catch (InterruptedException e) {
                }
            }
        }

        public List<String> messagesReceived() {
            synchronized (messagesReceived) {
                return new ArrayList<String>(messagesReceived);
            }
        }

        public int packetsReceived() {
            return packetsReceived.get();
        }

        public void freeze() {
            freeze = true;
        }

        public void unfreeze() {
            freeze = false;
        }

        public void close() throws IOException {
            try {
                server.close();
            } catch (Exception e) {
                //ignore
            }
        }

        public void clear() {
            packetsReceived.set(0);
            messagesReceived.clear();
        }
    }

    // Influenced by SpringFramework's SocketUtils
    private static int findAvailableRandomPort() throws IOException {
        int portStart = 1024;
        int portEnd = 65536;
        Random random = new Random(System.nanoTime());
        int numTries = 1000;

        for (int i = 0; i < numTries; i++) {
            int portRange = portEnd - portStart;
            int randomPort = portStart + random.nextInt(portRange + 1);

            final InetAddress localhost = InetAddress.getByName("localhost");

            try (ServerSocket serverSocket = ServerSocketFactory.getDefault().createServerSocket(
                    randomPort, 1, localhost)) {
                return randomPort;
            } catch (Exception ex) {
                // try something else, no real unexpected error here
            }
        }

        throw new IllegalArgumentException("Cannot find available random server port");
    }
}
