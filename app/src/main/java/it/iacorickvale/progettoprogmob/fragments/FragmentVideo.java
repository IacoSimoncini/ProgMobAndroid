package it.iacorickvale.progettoprogmob.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Application;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.extractor.ExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.BandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import it.iacorickvale.progettoprogmob.R;
import it.iacorickvale.progettoprogmob.adapters.CardsAdapter;
import it.iacorickvale.progettoprogmob.firebase.CardsFunctions;
import it.iacorickvale.progettoprogmob.firebase.DatabaseReferences;
import it.iacorickvale.progettoprogmob.firebase.ExerciseFunctions;
import it.iacorickvale.progettoprogmob.utilities.Cards;
import it.iacorickvale.progettoprogmob.utilities.Esercizi;

import static android.app.Activity.RESULT_CANCELED;

public class FragmentVideo extends Fragment{

    private static final int PICK_VIDEO = 1;
    VideoView videoView;
    ProgressBar upload;
    EditText editText;
    Button choose;
    Button up;
    SimpleExoPlayer exoPlayer;
    private PlayerView mExoPlayerView;
    private Boolean aux;
    private Uri videoUri;
    private String pre_uri;
    private String pre_name;
    private String pre_diff;
    private String pre_desc;
    private String pre_cal;
    FirebaseDatabase database;
    DatabaseReference databaseReferences;
    private StorageReference storageReference;
    private UploadTask uploadTask;
    private String current_uid;
    private MediaController mediaController;
    private ImageButton btnUpld;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.fragment_video, container, false);
        up = view.findViewById(R.id.button_up);
        choose = view.findViewById(R.id.button_play);
        assert this.getArguments() != null;
        String docref_user = null;
        String control = this.getArguments().getString("type");
        if(control.equals("admin")){
            current_uid = this.getArguments().getString("u_id");
        }
        if (control.equals("admin")) {
            aux = true;
        } else {
            up.setVisibility(View.INVISIBLE);
            choose.setVisibility(View.INVISIBLE);
            aux = false;
        }
        final String doc = docref_user;

        pre_uri = this.getArguments().getString("uri");
        pre_name = this.getArguments().getString("name");
        pre_diff = this.getArguments().getString("difficulty");
        pre_desc = this.getArguments().getString("description");
        pre_cal = this.getArguments().getString("cal");
        Log.d("URI DA LEGGERE", pre_uri);

        upload = view.findViewById(R.id.progress_bar_upload);

        up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UploadVideo();
            }
        });

        choose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ChooseVideo(v);
            }
        });

        TextView descText = view.findViewById(R.id.text_descrizione);
        descText.setText(this.getArguments().getString("description"));


        storageReference = FirebaseStorage.getInstance().getReference("exercises");
        database = FirebaseDatabase.getInstance();
        databaseReferences = FirebaseDatabase.getInstance().getReference("Esercizi");
        mExoPlayerView = view.findViewById(R.id.video_view);
        videoView = view.findViewById(R.id.video_two);

        try {
            BandwidthMeter bandwidthMeter = new DefaultBandwidthMeter.Builder(getContext()).build();
            TrackSelector trackSelector = new DefaultTrackSelector(new AdaptiveTrackSelection.Factory(bandwidthMeter));
            exoPlayer = (SimpleExoPlayer) ExoPlayerFactory.newSimpleInstance(getContext());
            Uri video = Uri.parse(pre_uri);
            DefaultHttpDataSourceFactory dataSourceFactory = new DefaultHttpDataSourceFactory("Esercizi");
            ExtractorsFactory extractorsFactory = new DefaultExtractorsFactory();
            MediaSource mediaSource = new ExtractorMediaSource(video, dataSourceFactory, extractorsFactory, null, null);
            mExoPlayerView.setPlayer(exoPlayer);
            exoPlayer.prepare(mediaSource);
            exoPlayer.setPlayWhenReady(false);
        }catch (Exception e){
            Toast.makeText(getContext(), "Failed to play", Toast.LENGTH_SHORT).show();
        }

        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode != RESULT_CANCELED) {
            if (requestCode == PICK_VIDEO || resultCode == Activity.RESULT_OK || data != null || data.getData() != null) {
                videoUri = data.getData();
                mExoPlayerView.setVisibility(View.INVISIBLE);
                videoView.setVisibility(View.VISIBLE);

                videoView.setVideoURI(videoUri);
                mediaController = new MediaController(this.getActivity());
                videoView.setMediaController(mediaController);
                mediaController.setAnchorView(videoView);
                videoView.start();
                //upload su firebase poi dopo lo modifichi
                //ExerciseFunctions.deleteEx(this.getArguments().getString("name"));
                //ExerciseFunctions.createEx(this.getArguments().getString("description"), this.getArguments().getString("difficulty"), this.getArguments().getString("name"), this.getArguments().getString("cal"), videoUri.toString());
                //ExerciseFunctions.modifyEx(this.getArguments().getString("difficulty"), this.getArguments().getString("description") , this.getArguments().getString("difficulty") ,this.getArguments().getString("name") , videoUri.toString());

            }
        }else{
            Toast.makeText(getContext(), "Failed", Toast.LENGTH_SHORT).show();
        }
    }

    public void ChooseVideo(View view) {
        Intent intent = new Intent();
        intent.setType("*/*");
        intent.setAction(Intent.ACTION_OPEN_DOCUMENT);
        startActivityForResult(intent, PICK_VIDEO);
    }



    private String getExt(Uri uri) {
        ContentResolver contentResolver = getActivity().getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }


    private void UploadVideo(){
        if (videoUri != null) {

            upload.setVisibility(View.VISIBLE);
            final StorageReference reference = storageReference.child(System.currentTimeMillis()  +  "." + getExt(videoUri));
            uploadTask = reference.putFile(videoUri);

            Task<Uri> urltask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public  Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (!task.isSuccessful()) {
                        throw task.getException();
                    }
                    return reference.getDownloadUrl();
                }
            })
                    .addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task) {

                            if (task.isSuccessful()){
                                Uri downloadUrl = task.getResult();
                                upload.setVisibility(View.INVISIBLE);
                                Toast.makeText(getContext(), "VideoSaved", Toast.LENGTH_SHORT).show();
                                ExerciseFunctions.deleteEx(pre_name);
                                ExerciseFunctions.createEx(pre_desc, pre_diff, pre_name, pre_cal, downloadUrl.toString());
                                //String i = databaseReferences.push().getKey();
                                //databaseReferences.child(i).setValue();
                            }
                            else{
                                Toast.makeText(getContext(), "Failed", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }
}