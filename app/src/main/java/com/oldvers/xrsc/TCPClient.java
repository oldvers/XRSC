package com.oldvers.xrsc;

/**
 * Created by oldvers on 22.01.2016.
 */

import android.util.Log;

import java.io.*;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

public class TCPClient
{
  private String             mServerMessage;
  private String             mServer          = "192.168.4.1";
  public static final int    mServerPort      = 333;
  private OnMessageReceived  mMessageListener = null;
  private boolean            mRun             = false;
  private RsPacket           mPacket;
  private boolean            mActive          = true;

  //private PrintWriter        mOS;
  private OutputStream       mOS;
  private BufferedReader     mIS;

  /**
   *  Constructor of the class. OnMessagedReceived listens for the messages received from server
   */
  public TCPClient(String aServer, boolean aActivity, OnMessageReceived aListener)
  {
    mServer = aServer;
    mActive = aActivity;
    mMessageListener = aListener;
  }

  /**
   * Sends the message entered by client to the server
   * @param message text entered by client
   */
//  public void sendMessage(String message)
//  {
//    Log.d("TCP Client", "Sending message");
//
//    if (mOS != null && !mOS.checkError())
//    {
//      //mOS.println(message);
//      mOS.print(message);
//      mOS.flush();
//
//      Log.d("TCP Client", "Message sent");
//    }
//  }

  public boolean sendRaw(byte[] message)
  {
    boolean result = false;

    Log.d("TCP Client", "Sending raw data");

    if ( (mOS != null) && (mActive) )
    {
      try
      {
        mOS.write(message);
        Log.d("TCP Client", "Message sent");
        result = true;
      }
      catch(IOException e)
      {
        Log.d("TCP Client", "ERROR: " + e.getMessage());
      }
    }

    return result;
  }

  public void stop()
  {
    mRun = false;

    Log.d("TCP Client", "Stop");
  }

  public void run()
  {
    mRun = true;

    try
    {
      // Here you must put your computer's IP address.
      InetAddress mServerAddr = InetAddress.getByName(mServer);
      //InetAddress addr = InetAddress.getByName("javacodegeeks.com");

      SocketAddress mSocketAddr = new InetSocketAddress(mServerAddr, mServerPort);

      // Creates an unconnected socket
      Socket mSocket = new Socket();

      mSocket.setKeepAlive(true);

      int mConnectTimeout = 3000;   // 3000 millis = 3 seconds

      Log.d("TCP Client", "Connecting...");

      // Connects this socket to the server with a specified timeout value
      // If timeout occurs, SocketTimeoutException is thrown
      mSocket.connect(mSocketAddr, mConnectTimeout);

      Log.d("TCP Client", "Connected..." + mSocket);

      // Create a socket to make the connection with the server
      //Socket mSocket = new Socket(mServerAddr, mServerPort);

      Log.d("TCP Client", "Setting Timeout to 200 ms");

      if (mMessageListener != null)
      {
        mMessageListener.messageReceived("Connected");
      }

      mPacket = new RsPacket();

      mSocket.setSoTimeout(3000);

      try
      {
        // Send the message to the server
        //mOS = new PrintWriter(new BufferedWriter(new OutputStreamWriter(mSocket.getOutputStream())), true);
        mOS = mSocket.getOutputStream();

        // Receive the message which the server sends back
        mIS = new BufferedReader(new InputStreamReader(mSocket.getInputStream()));

        //in this while the client listens for the messages sent by the server
        while (mRun)
        {
          try
          {
            Log.d("TCP Client", "Reading...");

            //mServerMessage = mIS.readLine();
            //if(!sendRaw(mPacket.getStatus())) throw new IOException("Socket unavailable");
            if (mOS != null) mOS.write(mPacket.getStatus());

            mSocket.getInputStream().read();

            Thread.sleep(1000);
          }
          catch (Exception e)
          {
            Log.d("TCP Client", "Reading Timeout occured.");
            mRun = false;
          }

          if (mServerMessage != null && mMessageListener != null)
          {
            // Call the method messageReceived from MyActivity class
            mMessageListener.messageReceived(mServerMessage);

            Log.d("TCP Client", "Received Message: '" + mServerMessage + "'");
          }
          mServerMessage = null;
        }
      }
      catch (Exception e)
      {
        Log.e("TCP Client", "Error", e);
      }
      finally
      {
        //the socket must be closed. It is not possible to reconnect to this socket
        // after it is closed, which means a new socket instance has to be created.
        mSocket.close();
      }
    }
    catch (SocketTimeoutException e)
    {
      Log.e("TCP Client", "Connection timeout occured: " + e.getMessage());
    }
    catch (UnknownHostException e)
    {
      Log.e("TCP Client", "Host not found: " + e.getMessage());
    }
    catch (Exception e)
    {
      Log.e("TCP Client", "Error", e);
    }
  }

  //Declare the interface. The method messageReceived(String message) will must be implemented in the MyActivity
  //class at on asynckTask doInBackground
  public interface OnMessageReceived
  {
    public void messageReceived(String message);
  }
}
