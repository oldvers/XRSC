package com.oldvers.xrsc;

/**
 * Created by oldvers on 23.06.2016.
 */

import android.app.Activity;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.oldvers.rsc.R;


public class AppStatusDialog extends DialogFragment implements OnClickListener
{
  private final String mTag           = "XRSC Status";
  private EditText     mPasswordText  = null;
  private Activity     mActivity      = null;
  private TextView     mAppStatusText = null;
  private boolean      mStatus        = false;

  private OnCompleteListener mListener;

  public static interface OnCompleteListener
  {
    public abstract void onComplete(String password);
  }

  public void onAttach(Activity activity)
  {
    super.onAttach(activity);

    try
    {
      this.mListener = (OnCompleteListener)activity;
      this.mActivity = activity;
    }
    catch (final ClassCastException e)
    {
      throw new ClassCastException(activity.toString() + " must implement OnCompleteListener");
    }
  }

  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
  {
    getDialog().setTitle("XRSC Status");
    View dview = inflater.inflate(R.layout.dialog_status, null);
    dview.findViewById(R.id.idPasswordButton).setOnClickListener(this);
    mAppStatusText = (TextView)dview.findViewById(R.id.idAppStatusText);
    mPasswordText = (EditText)dview.findViewById(R.id.idPasswordText);
    return dview;
  }

  public void onClick(View v)
  {
    Log.d(mTag, " : onClick" + ((Button) v).getText());

    //Give a password to activity
    mListener.onComplete(mPasswordText.getText().toString());

    //Clear the password
    mPasswordText.setText("");

    //Hide software keyboard
    InputMethodManager imm = (InputMethodManager)mActivity.getSystemService(Context.INPUT_METHOD_SERVICE);
    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);

    dismiss();
  }

  public void onDismiss(DialogInterface dialog)
  {
    super.onDismiss(dialog);
    Log.d(mTag, " : onDismiss");
  }

  public void onCancel(DialogInterface dialog)
  {
    super.onCancel(dialog);
    Log.d(mTag, " : onCancel");
  }

  public void setStatus(boolean status)
  {
    mStatus = status;
  }

  public void onStart()
  {
    if(mStatus)
    {
      mAppStatusText.setText(getString(R.string.textStatusOk));
      mPasswordText.setVisibility(View.INVISIBLE);
    }
    else
    {
      mAppStatusText.setText(getString(R.string.textStatusExpired));
      mPasswordText.setVisibility(View.VISIBLE);
    }
    super.onStart();
  }
}
