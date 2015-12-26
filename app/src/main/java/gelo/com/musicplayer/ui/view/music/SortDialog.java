package gelo.com.musicplayer.ui.view.music;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;

import java.util.ArrayList;

import gelo.com.musicplayer.R;

public class SortDialog extends DialogFragment {

    private String selectedSortingType;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.select_sorting)
                .setItems(R.array.colors_array, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // The 'which' argument contains the index position
                        // of the selected item
                        switch (which){
                            case 0:
                                selectedSortingType = MediaStore.Audio.Media.TITLE;
                                break;
                            case 1:
                                selectedSortingType = MediaStore.Audio.Media.ARTIST;
                                break;
                            case 2:
                                selectedSortingType = MediaStore.Audio.Media.ALBUM;
                                break;

                        }
                    }
                });
        return builder.create();
    }
}
