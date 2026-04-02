package View;

import android.app.Activity;
import android.app.AlertDialog;
import android.view.LayoutInflater;

import com.example.cookingmagic.R;

public class LoadingDialog {

    private Activity myActivity;
    private AlertDialog alertDialog;

    public LoadingDialog(Activity myActivity){
        this.myActivity = myActivity;
    }

    public void startLoadingDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(myActivity);

        LayoutInflater inflater = myActivity.getLayoutInflater();
        builder.setView(inflater.inflate(R.layout.custom_alertdialog_loading, null));
        builder.setCancelable(false);

        alertDialog = builder.create();
        alertDialog.show();

    }

    public void dismiss(){
        alertDialog.dismiss();
    }
}
