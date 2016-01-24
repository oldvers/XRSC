package com.oldvers.xrsc;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;

import com.oldvers.rsc.R;

public class MainActivity extends AppCompatActivity
{
  private String       mTag           = "MainThread";
  private EditText     mServer;
  private Switch       mClientSwitch;
  private TextView     mStatusText;
  private Button       mSign1;
  private Button       mSign2;
  private TCPClient    mTcpClient     = null;
  private aClientTask  mClientTask    = null;

  private final String aPicture =
            "SHOW_IMG->100:000000000000009024490200000000000000D8B66D03000048F2FFFF9F04000060DBB" +
            "60D000000000020F99F2449F23F010000000000D8B66D0300E44F92244992E40F0060DBB60D00000000" +
            "803C4992FC7F9224790000000000D8B66D03902749FEFFFFFF24C90360DBB60D00000000F224F9FFFFF" +
            "FFF3F491E00000000D8B66D439E24FFFFFFFFFFFF49F260DBB60D000000409EE4FFFFFFFFFFFF4FF200" +
            "000000000000C893FCFFFFFFFFFFFF7F9207000000000000C893FCFFFFFFFFFFFF7F920700000000000" +
            "07992FF3F00E03F00FCFF933C0000000000007992FF0700E00700E0FF933C0000000000007992FF07FE" +
            "FF077EE0FF933C00000000000079F2FF0780FF077EE0FF9F3C00000000000079F2FF0700E0077EE0FF9" +
            "F3C00000000000079F2FFFF7FE0077EE0FF9F3C00000000000079F2FFFF7FE0077EE0FF9F3C00000000" +
            "000079F2FF077EE0077EE0FF9F3C0000000000007992FF0700E00700E0FF933C0000000000007992FF3" +
            "F00FC3F00FCFF933C000000000000C893FCFFFFFFFFFFFF7F9207000000000000C893FCFFFFFFFFFFFF" +
            "7F9207000000000000409EE4FFFFFFFFFFFF4FF200000000000000409E24FFFFFFFFFFFF49F20000000" +
            "000000000F224F9FFFFFFFF3F491E00000000000000009027C9FFFFFFFF27C903000000000000000080" +
            "3C4992FFFF93247900000000000000000000E44F92244992E40F0000000000000000000020F99324499" +
            "23F01000000000000000000000048FEFFFFFF0400000000000000000000000000922449120000000000" +
            "0000$2FFE";


  @Override
  protected void onCreate(Bundle savedInstanceState)
  {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
    setSupportActionBar(toolbar);

    FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
    fab.setOnClickListener(new View.OnClickListener()
    {
      @Override
      public void onClick(View view)
      {
        Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show();
      }
    });

    mServer       = (EditText)findViewById(R.id.idServer);
    mClientSwitch = (Switch)findViewById(R.id.idClientSwitch);
    mStatusText   = (TextView)findViewById(R.id.idStatusText);
    mSign1        = (Button)findViewById(R.id.idSendButton1);
    mSign2        = (Button)findViewById(R.id.idSendButton2);

    // Hide Keyboard
    getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
  }

  public void onClickListenerSendButton1(View view)
  {
    String message = "Hallo Guys!!!";

    // Sends the message to the server
    if (mTcpClient != null)
    {
      mTcpClient.sendMessage(message);
    }
  }

  public void onClickListenerSendButton2(View view)
  {
    // Sends the message to the server
    if (mTcpClient != null)
    {
      mTcpClient.sendMessage(aPicture);
    }
  }

  public void onClickListenerClientSwitch(View view)
  {
    if (mClientSwitch.isChecked())
    {
      Log.d(mTag, "ClientSwitch chesked ON");
      mClientTask = new aClientTask();
      mClientTask.execute("");
    }
    else
    {
      Log.d(mTag, "ClientSwitch chesked OFF");

      mTcpClient.stop();
    }
  }

  public class aClientTask extends AsyncTask <String, String, TCPClient>
  {
    private String cTag    = "ClientTask";
    private String cServer = null;

    @Override
    protected TCPClient doInBackground(String... message)
    {
      publishProgress("Connection...");

      // We create a TCPClient object and
      mTcpClient = new TCPClient(cServer, new TCPClient.OnMessageReceived()
      {
        @Override
        // Here the messageReceived method is implemented
        public void messageReceived(String message)
        {
          // This method calls the onProgressUpdate
          publishProgress(message);
        }
      });
      mTcpClient.run();

//      while (!this.isCancelled())
//      {
//        Log.d(tag, "Do In Background...");
//
//        try
//        {
//          Thread.sleep(1000);
//        }
//        catch (Exception e)
//        {
//          //
//        }
//      }

      return null;
    }

    @Override
    protected void onProgressUpdate(String... values)
    {
      super.onProgressUpdate(values);

      Log.d(cTag, "Do On Progress");

      mStatusText.setText(values[0]);
    }

    @Override
    protected void onPreExecute()
    {
      super.onPreExecute();

      cServer = mServer.getText().toString();

      Log.d(cTag, "Do Pre Execute : Server = " + cServer);
    }

    @Override
    protected void onCancelled()
    {
      super.onCancelled();

      Log.d(cTag, "Do On Cancelled");

      mTcpClient = null;
      mClientTask = null;
      mClientSwitch.setChecked(false);
      mStatusText.setText("Not connected");
    }

    @Override
    protected void onPostExecute(TCPClient tcpClient)
    {
      super.onPostExecute(tcpClient);

      Log.d(cTag, "Do Post Execute");

      mTcpClient = null;
      mClientTask = null;
      mClientSwitch.setChecked(false);
      mStatusText.setText("Not connected");
    }
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu)
  {
    // Inflate the menu; this adds items to the action bar if it is present.
    getMenuInflater().inflate(R.menu.menu_main, menu);
    return true;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item)
  {
    // Handle action bar item clicks here. The action bar will
    // automatically handle clicks on the Home/Up button, so long
    // as you specify a parent activity in AndroidManifest.xml.
    int id = item.getItemId();

    // Noinspection SimplifiableIfStatement
    if(id == R.id.action_settings)
    {
      return true;
    }

    return super.onOptionsItemSelected(item);
  }
}
