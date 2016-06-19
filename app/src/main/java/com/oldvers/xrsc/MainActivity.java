package com.oldvers.xrsc;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.oldvers.rsc.R;

public class MainActivity extends AppCompatActivity
{
  private final Integer Images[] =
                         {
                           R.drawable.rs_01,
                           R.drawable.rs_02,
                           R.drawable.rs_03,
                           R.drawable.rs_04,
                           R.drawable.rs_05,
                           R.drawable.rs_06,
                           R.drawable.rs_07,
                         };
  private final Integer Animations[] =
                         {
                           R.drawable.rsb_01,
                           R.drawable.rsb_02,
                           R.drawable.rsb_03,
                           R.drawable.rsb_04,
                           R.drawable.rsb_05,
                           R.drawable.rsb_06,
                           R.drawable.rsb_07,
                         };

  private final String   HexSymbols     = "0123456789ABCDEF";
  private final byte     PACKET_IMAGE   = 0;
  private final byte     PACKET_SLIDE   = 1;

  private String         mTag           = "MainThread";
  private Switch         mClientSwitch;
  private Switch         mRoadSignSwitch;
  private TextView       mStatusText;
  private TextView       mBrightPercents;
  private TextView       mDelaySeconds;
  private Button         mSend;
  private ImageView      mImageView;
  private Bitmap         mBitmap        = null;
  private SeekBar        mBrightness;
  private SeekBar        mDelay;
  private TCPClient      mTcpClient     = null;
  private aClientTask    mClientTask    = null;

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

  private class RSId
  {
    private String id;
    private Bitmap bmp;

    public RSId(String aStr, Bitmap aBmp)
    {
      id = aStr;
      bmp = aBmp;
    }

    public String getId()
    {
      return id;
    }

    public Bitmap getBmp()
    {
      return bmp;
    }
  }

  private void addImagesToTheGallery()
  {
    final LinearLayout mImageGallery = (LinearLayout)findViewById(R.id.idImageGallery);
    final LinearLayout mAnimationGallery = (LinearLayout)findViewById(R.id.idAnimationGallery);
    Bitmap uBitmap;
    String uString;

    View.OnClickListener uOnImageClick = new View.OnClickListener()
    {
      @Override
      public void onClick(View view)
      {
        Log.d("ImgClick", "On image click. ID = " + ((RSId)view.getTag()).getId());

        mBitmap = ((RSId)view.getTag()).getBmp();

        mImageView.setImageBitmap(mBitmap);

        if(mTcpClient != null)
        {
          mTcpClient.sendMessage(formatPacket(packBitmap(), PACKET_IMAGE));
        }
      }
    };

    View.OnClickListener uOnAnimationClick = new View.OnClickListener()
    {
      @Override
      public void onClick(View view)
      {
        Log.d("AnimClick", "On animation click. ID = " + ((RSId)view.getTag()).getId());

        mBitmap = ((RSId)view.getTag()).getBmp();

        mImageView.setImageBitmap(mBitmap);

        if(mTcpClient != null)
        {
          mTcpClient.sendMessage("SET_ANIMATION_" + ((RSId)view.getTag()).getId());
        }
      }
    };

    BitmapFactory.Options uOptions = new BitmapFactory.Options();
    uOptions.inScaled = false;
    uOptions.inPreferredConfig = Bitmap.Config.ARGB_8888;

    for (Integer image : Images)
    {
      uString = getResources().getResourceEntryName(image).substring(4, 5);
      uBitmap = BitmapFactory.decodeResource(getResources(), image, uOptions);
      mImageGallery.addView(getImageView(image, new RSId(uString, uBitmap), uOnImageClick));
    }

    for (Integer anime : Animations)
    {
      uString = getResources().getResourceEntryName(anime).substring(5, 6);
      uBitmap = BitmapFactory.decodeResource(getResources(), anime, uOptions);
      mAnimationGallery.addView(getImageView(anime, new RSId(uString, uBitmap), uOnAnimationClick));
    }
  }

  private View getImageView(Integer aImage, Object aObject, View.OnClickListener aOnClick)
  {
    ImageView mImageView = new ImageView(getApplicationContext());
    LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(192, 128);
    lp.width = 192;
    lp.height = 128;
    lp.setMargins(0, 0, 10, 0);
    mImageView.setLayoutParams(lp);
    mImageView.setTag(aObject);
    mImageView.setImageResource(aImage);
    mImageView.setClickable(true);
    mImageView.setOnClickListener(aOnClick);
    return mImageView;
  }

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

    mClientSwitch = (Switch) findViewById(R.id.idClientSwitch);
    mRoadSignSwitch = (Switch) findViewById(R.id.idRoadSignSwitch);
    mStatusText = (TextView) findViewById(R.id.idStatusText);
    mBrightPercents = (TextView) findViewById(R.id.idBrightnessPercent);
    mDelaySeconds = (TextView) findViewById(R.id.idSecondsText);
    mSend = (Button) findViewById(R.id.idSendButton);
    mImageView = (ImageView) findViewById(R.id.idImageView);
    mBrightness = (SeekBar) findViewById(R.id.idBrightnessSeekBar);
    mDelay = (SeekBar) findViewById(R.id.idDelaySeekBar);

    BitmapFactory.Options mOptions = new BitmapFactory.Options();
    mOptions.inScaled = false;
    mOptions.inPreferredConfig = Bitmap.Config.ARGB_8888;
    mBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.default_road_sign, mOptions);

    addImagesToTheGallery();

    mClientSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
      {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
        {
          manageConnection();
        }
      });

    mRoadSignSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
    {
      @Override
      public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
      {
        StringBuilder packet = new StringBuilder();
        int i, CRC;

        if(mTcpClient == null) return;

        if(mRoadSignSwitch.isChecked())
        {
          Log.d(mTag, "RoadSignSwitch chesked ON");
          packet.append("ROAD_SIGN->ON$");
        }
        else
        {
          Log.d(mTag, "RoadSignSwitch chesked OFF");
          packet.append("ROAD_SIGN->OFF$");
        }

        CRC = 0;
        for (i = 0; i < packet.toString().length(); i++)
          CRC += (int)packet.toString().charAt(i);

        packet.append(HexSymbols.charAt(((CRC >> 4) & 0x0F)));
        packet.append(HexSymbols.charAt((CRC & 0x0F)));
        packet.append(HexSymbols.charAt(((CRC >> 12) & 0x0F)));
        packet.append(HexSymbols.charAt(((CRC >> 8) & 0x0F)));

        mTcpClient.sendMessage(packet.toString());
      }
    });

    mBrightness.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener()
    {
      @Override
      public void onProgressChanged(SeekBar seekBar, int i, boolean b)
      {
        mBrightPercents.setText(mBrightness.getProgress() + " %");
      }

      @Override
      public void onStartTrackingTouch(SeekBar seekBar)
      {
        //
      }

      @Override
      public void onStopTrackingTouch(SeekBar seekBar)
      {
        //
      }
    });

    mDelay.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener()
    {
      @Override
      public void onProgressChanged(SeekBar seekBar, int i, boolean b)
      {
        mDelaySeconds.setText(mDelay.getProgress() + " s");
      }

      @Override
      public void onStartTrackingTouch(SeekBar seekBar)
      {
        //
      }

      @Override
      public void onStopTrackingTouch(SeekBar seekBar)
      {
        //
      }
    });

    /*mClientSwitch.setOnClickListener(new View.OnClickListener()
    {
      @Override
      public void onClick(View view)
      {
        manageConnection();
      }
    });*/

    // Hide Keyboard
    getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
  }

  public void onClickListenerSendButton(View view)
  {
    // Sends the message to the server
    if(mTcpClient != null)
    {
      mTcpClient.sendMessage(formatPacket(packBitmap(), PACKET_IMAGE));
    }
  }

  public void onClickListenerOffButton(View view)
  {
    StringBuilder packet = new StringBuilder();
    int i, CRC;

    if(mTcpClient == null) return;

    packet.append("ROAD_SIGN->OFF$");

    CRC = 0;
    for (i = 0; i < packet.toString().length(); i++)
      CRC += (int)packet.toString().charAt(i);

    packet.append(HexSymbols.charAt(((CRC >> 4) & 0x0F)));
    packet.append(HexSymbols.charAt((CRC & 0x0F)));
    packet.append(HexSymbols.charAt(((CRC >> 12) & 0x0F)));
    packet.append(HexSymbols.charAt(((CRC >> 8) & 0x0F)));

    mTcpClient.sendMessage(packet.toString());
  }

  public void onClickListenerSlideButton(View view)
  {
    // Sends the message to the server
    if(mTcpClient != null)
    {
      mTcpClient.sendMessage(formatPacket(packBitmap(), PACKET_SLIDE));
    }
  }

  public void manageConnection()
  {
    if(mClientSwitch.isChecked())
    {
      Log.d(mTag, "ClientSwitch chesked ON");
      mClientTask = new aClientTask();
      mClientTask.execute("");
    }
    else
    {
      Log.d(mTag, "ClientSwitch chesked OFF");

      if (mTcpClient != null) mTcpClient.stop();
    }
  }

  public class aClientTask extends AsyncTask<String, String, TCPClient>
  {
    private String cTag = "ClientTask";
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

      cServer = getString(R.string.defaultServer);

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


  /************************************************************************************************/
  /* Working with Image */
  /************************************************************************************************/

  private byte[] packBitmap()
  {
    if (mBitmap == null) return null;

    int x, y, bi, ba, v, c, color;
    int size = mBitmap.getWidth() * mBitmap.getHeight() * 3;

    if (size % 8 != 0)
    {
      size = size / 8 + 1;
    }
    else
    {
      size = size / 8;
    }

    byte[] pb = new byte[size];

    bi = 0;
    ba = 0;
    v = 0;

    for (y = 0; y < mBitmap.getHeight(); y++)
    {
      for(x = 0; x < mBitmap.getWidth(); x++)
      {
        for(c = 0; c < 3; c++)
        {
          color = mBitmap.getPixel(x, y) & (0xFF << ((2 - c) * 8));

          if(color != 0)
          {
            ba = ba | 0x80;
          }

          if(bi % 8 == 7)
          {
            pb[v] = (byte)(ba & 0xFF);
            ba = 0;
            v++;
          }

          bi++;
          ba = ba >> 1;
        }
      }
    }

    return pb;
  }

  private String formatPacket(byte[] aPackedBitmap, byte aPacketType)
  {
    StringBuilder packet = new StringBuilder("");

    int i, CRC;

    //SHOW_IMG->100:aabbb..............$bbaa\r
    //SHOW_SLD->100:12:aabbb..............$bbaa\r

    if (aPackedBitmap == null) return null;

    switch(aPacketType)
    {
      case PACKET_IMAGE:
        packet.append("SHOW_IMG->");
        packet.append(mBrightness.getProgress());
        packet.append(":");
        break;
      case PACKET_SLIDE:
        packet.append("SHOW_SLD->");
        packet.append(mBrightness.getProgress());
        packet.append(":");
        packet.append(mDelay.getProgress());
        packet.append(":");
        break;
      default:
        return null;
    }

    CRC = 0;
    for (i = 0; i < packet.toString().length(); i++)
      CRC += (int)packet.toString().charAt(i);

    for (i = 0; i < aPackedBitmap.length; i++)
    {
      packet.append(HexSymbols.charAt(((aPackedBitmap[i] >> 4) & 0x0F)));
      CRC += (int)HexSymbols.charAt(((aPackedBitmap[i] >> 4) & 0x0F));
      packet.append(HexSymbols.charAt(aPackedBitmap[i] & 0x0F));
      CRC += (int)HexSymbols.charAt(aPackedBitmap[i] & 0x0F);
    }

    packet.append("$");
    CRC += (int)'$';

    packet.append(HexSymbols.charAt(((CRC >> 4) & 0x0F)));
    packet.append(HexSymbols.charAt((CRC & 0x0F)));
    packet.append(HexSymbols.charAt(((CRC >> 12) & 0x0F)));
    packet.append(HexSymbols.charAt(((CRC >> 8) & 0x0F)));

    return packet.toString();
  }

  private class OpenBitmapListener implements OpenFileDialog.OpenDialogListener
  {
    @Override
    public void OnSelectedFile(String aFileName)
    {
      BitmapFactory.Options mOptions = new BitmapFactory.Options();
      mOptions.inPreferredConfig = Bitmap.Config.ARGB_8888;
      mOptions.inScaled = false;
      mOptions.inJustDecodeBounds = true;
      mBitmap = BitmapFactory.decodeFile(aFileName, mOptions);

      if ((mOptions.outWidth != 48) || (mOptions.outHeight != 32))
      {
        Toast.makeText(getApplicationContext(), "Wrond Image Dimentions!", Toast.LENGTH_LONG).show();
      }
      else
      {
        mOptions.inJustDecodeBounds = false;
        mBitmap = BitmapFactory.decodeFile(aFileName, mOptions);
        mImageView.setImageBitmap(mBitmap);
      }
    }
  }

  public void onClickListenerOpenButton(View view)
  {
    OpenFileDialog mFileDialog = new OpenFileDialog(this)
            .setFilter(".*\\.bmp")
            .setOpenDialogListener(new OpenBitmapListener());
    mFileDialog.show();
  }
}
