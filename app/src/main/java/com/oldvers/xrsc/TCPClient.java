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
  private String             mServer          = "192.168.4.1";;
  public static final int    mServerPort      = 333;
  private OnMessageReceived  mMessageListener = null;
  private boolean            mRun             = false;

  private PrintWriter        mOS;
  private BufferedReader     mIS;

  /**
   *  Constructor of the class. OnMessagedReceived listens for the messages received from server
   */
  public TCPClient(String aServer, OnMessageReceived aListener)
  {
    mServer = aServer;
    mMessageListener = aListener;
  }

  /**
   * Sends the message entered by client to the server
   * @param message text entered by client
   */
  public void sendMessage(String message)
  {
    Log.d("TCP Client", "Sending message");

    if (mOS != null && !mOS.checkError())
    {
      mOS.println(message);
      mOS.flush();

      Log.d("TCP Client", "Message sent");
    }
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

      mSocket.setSoTimeout(200);

      try
      {
        // Send the message to the server
        mOS = new PrintWriter(new BufferedWriter(new OutputStreamWriter(mSocket.getOutputStream())), true);

        // Receive the message which the server sends back
        mIS = new BufferedReader(new InputStreamReader(mSocket.getInputStream()));

        //in this while the client listens for the messages sent by the server
        while (mRun)
        {
          try
          {
            Log.d("TCP Client", "Reading...");

            mServerMessage = mIS.readLine();
          }
          catch (Exception e)
          {
            Log.d("TCP Client", "Reading Timeout occured.");
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
