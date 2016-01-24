package com.oldvers.xrsc;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;

import com.oldvers.rsc.R;

public class MainActivity extends AppCompatActivity
{
  private EditText  mServer;
  private Switch    mClientSwitch;
  private Button    mSign1;
  private Button    mSign2;
  private TCPClient mTcpClient;

  private final String aPicture =
            "4992244992244992" +
            "2449922449922449" +
            "9224D9B66DDB0600" +
            "00000000000000B0" +
            "6DDBB62D19000000" +
            "0000000000000000" +
            "00000000002C1900" +
            "0000000000000000" +
            "000000000000002C" +
            "1900000000000000" +
            "0000000000000000" +
            "002C190000000000" +
            "0000020001000000" +
            "0000002C1900D8B6" +
            "0D00000010200000" +
            "0060DB36002C1900" +
            "18000C0000008004" +
            "000000600030002C" +
            "190018000C000000" +
            "8004000000600030" +
            "002C1900D8B60D00" +
            "0000102000000060" +
            "DB36002C19000000" +
            "0000000002000100" +
            "00000000002C1900" +
            "0000000000000000" +
            "000000000000002C" +
            "0100000000499200" +
            "0000004992000000" +
            "0020010000000001" +
            "8000000000018000" +
            "0000002001000000" +
            "0001800000000001" +
            "8000000000200100" +
            "0000000180000000" +
            "0001800000000020" +
            "0100000000018000" +
            "0000000180000000" +
            "0020010000000001" +
            "8000000000018000" +
            "0000002001000000" +
            "0001800000000001" +
            "8000000000200100" +
            "0000004992000000" +
            "0049920000000020" +
            "1900000000000000" +
            "0000000000000000" +
            "002C190010000100" +
            "0000020001000000" +
            "0220002C19008020" +
            "0000000010200000" +
            "00001004002C1900" +
            "0004000000008004" +
            "000000008000002C" +
            "1900802000000000" +
            "8004000000001004" +
            "002C190010000100" +
            "0000102000000000" +
            "0220002C19000000" +
            "0000000002000100" +
            "00000000002C1900" +
            "0000000000000000" +
            "000000000000002C" +
            "1900000000000000" +
            "0000000000000000" +
            "002C190000000000" +
            "0000000000000000" +
            "0000002CD9B66DDB" +
            "0600000000000000" +
            "00B06DDBB62D4992" +
            "2449922449922449" +
            "9224499224499224";


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
    mSign1        = (Button)findViewById(R.id.idSendButton1);
    mSign2        = (Button)findViewById(R.id.idSendButton2);

    //mServer.clearFocus();
    getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

    // Connect to the server
    new aClientTask().execute("");
  }

  public void Sign1OnClickListener(View view)
  {
    String message = "Hallo Guys!!!";

    //sends the message to the server
    if (mTcpClient != null)
    {
      mTcpClient.sendMessage(message);
    }
  }

  public void Sign2OnClickListener(View view)
  {
    //sends the message to the server
    if (mTcpClient != null)
    {
      mTcpClient.sendMessage(aPicture);
    }
  }

  public class aClientTask extends AsyncTask <String, String, TCPClient>
  {
    @Override
    protected TCPClient doInBackground(String... message)
    {
      // We create a TCPClient object and
      mTcpClient = new TCPClient(new TCPClient.OnMessageReceived()
      {
        @Override
        //here the messageReceived method is implemented
        public void messageReceived(String message)
        {
          //this method calls the onProgressUpdate
          publishProgress(message);
        }
      });
      mTcpClient.run();

      return null;
    }

    @Override
    protected void onProgressUpdate(String... values)
    {
      super.onProgressUpdate(values);

      //in the arrayList we add the messaged received from server
      //arrayList.add(values[0]);
      // notify the adapter that the data set has changed. This means that new message received
      // from server was added to the list
      //mAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onPreExecute()
    {
      super.onPreExecute();
    }

    @Override
    protected void onPostExecute(TCPClient tcpClient)
    {
      super.onPostExecute(tcpClient);

      if(tcpClient == null) mClientSwitch.setChecked(false);
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

    //noinspection SimplifiableIfStatement
    if(id == R.id.action_settings)
    {
      return true;
    }

    return super.onOptionsItemSelected(item);
  }
}
