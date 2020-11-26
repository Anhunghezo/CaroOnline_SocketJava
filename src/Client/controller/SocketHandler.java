/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package client.controller;

import client.RunClient;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;
import shared.constant.StreamData;
import shared.security.AES;
import shared.security.RSA;

/**
 *
 * @author Hoang Tran < hoang at 99.hoangtran@gmail.com >
 */
public class SocketHandler {

    Socket s;
    DataInputStream dis;
    DataOutputStream dos;

    Thread listener = null;
    AES aes;

    public String connect(String addr, int port) {
        try {
            // getting ip 
            InetAddress ip = InetAddress.getByName(addr);

            // establish the connection with server port 
            s = new Socket();
            s.connect(new InetSocketAddress(ip, port), 4000);
            System.out.println("Connected to " + ip + ":" + port + ", localport:" + s.getLocalPort());

            // obtaining input and output streams
            dis = new DataInputStream(s.getInputStream());
            dos = new DataOutputStream(s.getOutputStream());

            // close old listener
            if (listener != null && listener.isAlive()) {
                listener.interrupt();
            }

            // listen to server
            listener = new Thread(() -> {
                listen();
            });
            listener.start();

            // security
            initSecurityAES();

            // connect success
            return "success";

        } catch (IOException e) {

            // connect failed
            return "failed;" + e.getMessage();
        }
    }

    private void initSecurityAES() {
        // create new key
        aes = new AES();

        // encrypt aes key using rsa with server's public key 
        RSA clientSideRSA = new RSA()
                .preparePublicKey("src/Server/rsa_keypair/publicKey");

        String aesKey = aes.getSecretKey();
        String aesKeyEncrypted = clientSideRSA.encrypt(aesKey);

        // send to server
        sendPureData(StreamData.Type.AESKEY.name() + ";" + aesKeyEncrypted);

        System.out.println("Client send AES key: " + aesKey);
    }

    private void listen() {
        boolean running = true;

        while (running) {
            try {
                // read input stream
                String received = dis.readUTF();

                // decrypt data if needed
                if (aes != null) {
                    received = aes.decrypt(received);
                }

                // process received data
                StreamData.Type type = StreamData.getTypeFromData(received);

                switch (type) {
                    case AESKEY:
                        // client nhận được phản hồi "đã nhận được aes key từ server"
                        // tắt scene connectServer
                        RunClient.closeScene(RunClient.SceneName.CONNECTSERVER);
                        // mở scene login
                        RunClient.openScene(RunClient.SceneName.LOGIN);
                        break;
                    case LOGIN:
                    case SIGNUP:
                    case LIST_ROOM:
                    case CREATE_ROOM:
                    case JOIN_ROOM:
                    case LEAVE_ROOM:
                    case ROOM_CHAT:
                    case PROFILE:
                    case FIND_GAME:
                    case MOVE:
                    case UNDO:
                    case UNDO_ACCEPT:
                    case NEW_GAME:
                    case NEW_GAME_ACCEPT:
                    case EXIT:
                        running = false;
                }

            } catch (IOException ex) {
                Logger.getLogger(SocketHandler.class.getName()).log(Level.SEVERE, null, ex);
                break;
            }
        }

        try {
            // closing resources
            s.close();
            dis.close();
            dos.close();
        } catch (IOException ex) {
            Logger.getLogger(SocketHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void sendPureData(String data) {
        try {
            dos.writeUTF(data);
        } catch (IOException ex) {
            Logger.getLogger(SocketHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void sendData(String data) {
        try {

            String encrypted = aes.encrypt(data);
            dos.writeUTF(encrypted);

        } catch (IOException ex) {
            Logger.getLogger(SocketHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
