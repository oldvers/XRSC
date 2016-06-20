package com.oldvers.xrsc;

import java.util.Arrays;

/**
 * Created by oldvers on 19.06.2016.
 */
public class RsPacket
{
  private static final byte RS_CMD_REFRESH_ON  = 0;
  private static final byte RS_CMD_SET_BRIGHT  = 1;
  private static final byte RS_CMD_SET_IMAGE   = 2;
  private static final byte RS_CMD_SET_SLIDE   = 3;
  private static final byte RS_CMD_SET_CLIP    = 4;
  private static final byte RS_CMD_GET_STATUS  = 5;
  private static final byte RS_CMD_REFRESH_OFF = 126;

  private byte[] packet = new byte[2048];
  private int    length = 0;
  private byte   imgcnt = 0;

  //0 - Length      - 2   bytes
  //2 - Command     - 1   byte
  //3 - Brightness  - 1   byte
  //4 - Parameter   - 2   bytes (Delay, Number of animation )
  //6 - Image count - 1   byte  (optionally)
  //7 - Image 0     - 576 bytes (optionally)
  //    Image 1     - 576 bytes (optionally)
  //    Image 2     - 576 bytes (optionally)
  //    CS          - 2   bytes

  public RsPacket()
  {
    this.clear();
  }

  public void clear()
  {
    length = 2;
    imgcnt = 0;
    for(int i = 0; i < 2048; i++) packet[i] = 0;
  }

  //2 - Command - 1 byte
  public void setCommand(byte command)
  {
    length += 1;
    packet[0] = (byte)(length & 0xFF);
    packet[1] = (byte)((length >> 8) & 0xFF);
    packet[2] = command;
  }

  //3 - Brightness - 1 byte
  public void setBrightness(byte brightness)
  {
    length += 1;
    packet[0] = (byte)(length & 0xFF);
    packet[1] = (byte)((length >> 8) & 0xFF);
    packet[3] = brightness;
  }

  //4 - Parameter   - 2   bytes (Delay, Number of animation )
  public void setParameter(int parameter)
  {
    length += 2;
    packet[0] = (byte)(length & 0xFF);
    packet[1] = (byte)((length >> 8) & 0xFF);
    packet[4] = (byte)(parameter & 0xFF);
    packet[5] = (byte)((parameter >> 8) & 0xFF);
  }

  //6 - Image count - 1   byte  (optionally)
  public void addImage(byte[] image)
  {
    if(imgcnt == 3) return;
    if(image.length != 576) return;

    if(imgcnt == 0) length += 1;

    for(int i = 0; i < 576; i++) packet[7 + imgcnt*576 + i] = image[i];
    imgcnt++;
    length += 576;

    packet[0] = (byte)(length & 0xFF);
    packet[1] = (byte)((length >> 8) & 0xFF);
    packet[6] = imgcnt;
  }

  //CS - 2 bytes
  public void calcCS()
  {
    int CS = 0;

    length += 2;
    packet[0] = (byte)(length & 0xFF);
    packet[1] = (byte)((length >> 8) & 0xFF);

    for(int i = 0; i < (length - 2); i++) CS += (int)(packet[i] & 0xFF);

    packet[length - 2] = (byte)(CS & 0xFF);
    packet[length - 1] = (byte)((CS >> 8) & 0xFF);
  }

  public byte[] refreshOn(int brightness)
  {
    this.clear();
    this.setCommand(RS_CMD_REFRESH_ON);
    this.setBrightness((byte) (brightness & 0xFF));
    this.setParameter(0);
    this.calcCS();

    return Arrays.copyOf(packet, length);
  }

  public byte[] refreshOff()
  {
    this.clear();
    this.setCommand(RS_CMD_REFRESH_OFF);
    this.setBrightness((byte)0);
    this.setParameter(0);
    this.calcCS();

    return Arrays.copyOf(packet, length);
  }

  public byte[] getStatus()
  {
    this.clear();
    this.setCommand(RS_CMD_GET_STATUS);
    this.setBrightness((byte)0);
    this.setParameter(0);
    this.calcCS();

    return Arrays.copyOf(packet, length);
  }

  public byte[] setRefreshBrightness(int brightness)
  {
    this.clear();
    this.setCommand(RS_CMD_SET_BRIGHT);
    this.setBrightness((byte) (brightness & 0xFF));
    this.setParameter(0);
    this.calcCS();

    return Arrays.copyOf(packet, length);
  }

  public byte[] setImage(byte[] image, int brightness)
  {
    this.clear();
    this.setCommand(RS_CMD_SET_IMAGE);
    this.setBrightness((byte) (brightness & 0xFF));
    this.setParameter(0);
    this.addImage(image);
    this.calcCS();

    return Arrays.copyOf(packet, length);
  }

  public byte[] setSlide(byte[] image1, byte[] image2, int delay, int brightness)
  {
    this.clear();
    this.setCommand(RS_CMD_SET_SLIDE);
    this.setBrightness((byte) (brightness & 0xFF));
    this.setParameter(delay);
    this.addImage(image1);
    this.addImage(image2);
    this.calcCS();

    return Arrays.copyOf(packet, length);
  }
}
