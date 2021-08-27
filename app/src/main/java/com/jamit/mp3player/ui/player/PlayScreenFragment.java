package com.jamit.mp3player.ui.player;

import androidx.constraintlayout.motion.widget.MotionLayout;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.MediaMetadata;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.slider.Slider;
import com.jamit.mp3player.R;
import com.jamit.mp3player.custom_views.PlayerScreenMotionLayout;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class PlayScreenFragment extends Fragment {

    private PlayScreenViewModel mViewModel;

    public static PlayScreenFragment newInstance() {
        return new PlayScreenFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mViewModel = new ViewModelProvider(this).get(PlayScreenViewModel.class);
        View view = inflater.inflate(R.layout.play_screen_fragment, container, false);

        BottomNavigationView navView = requireActivity().findViewById(R.id.nav_view);
        PlayerScreenMotionLayout root_layout = view.findViewById(R.id.root_layout);
        //set listener on motion layout to intercept user events
        root_layout.addTransitionListener(new MotionLayout.TransitionListener() {
            @Override
            public void onTransitionStarted(MotionLayout motionLayout, int startId, int endId) {

            }

            @Override
            public void onTransitionChange(MotionLayout motionLayout, int startId, int endId, float progress) {

            }

            @Override
            public void onTransitionCompleted(MotionLayout motionLayout, int currentId) {
                if (currentId == R.id.play_screen_expanded_normal){
                    //If Player is Expanded
                    //Close the Navigation Bar
                    navView.setVisibility(View.GONE);
                    requireActivity().getWindow().setStatusBarColor(ContextCompat.getColor(requireActivity(),R.color.dark_grey));
                }else {
                    //If Player is Closed
                    //Open the Navigation Bar
                    navView.setVisibility(View.VISIBLE);
                    requireActivity().getWindow().setStatusBarColor(ContextCompat.getColor(requireActivity(),R.color.black));                }
            }

            @Override
            public void onTransitionTrigger(MotionLayout motionLayout, int triggerId, boolean positive, float progress) {

            }
        });

        setupPlayer();
        setupView(view);
        return view;
    }

    SimpleExoPlayer player;
    CountDownTimer countDownTimer;
    Map<String,String> nameOfTrack;
    Map<String,String> nameOfArtist;

    //Initialization of Exoplayer Occurs Here.
    private void setupPlayer(){
        //Map To Hold Names of Tracks and Artist
        nameOfTrack = new HashMap<>();
        nameOfArtist = new HashMap<>();
        player = new SimpleExoPlayer.Builder(requireActivity()).build();
        for (int i=1;i<=11;i++){
            //Url Of Audio To Be Played
            String url = "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-"+i+".mp3";
            MediaItem mediaItem = new MediaItem.Builder().setUri(url).setMediaId(String.valueOf(i)).build();

            /*
             * Hardcoding of Audio Name and Artist Takes Place Here.
             * Getting Audio Name and Artist From network proves futile
             * since audio needs to be downloaded before they can be gotten.
             * This will be used until a better solution is found.
             */
            nameOfTrack.put(String.valueOf(i),"Sound Helix Song "+i);
            nameOfArtist.put(String.valueOf(i),"SoundHelix");

            //Adding Audio To Playlist Goes On Here
            player.addMediaItem(mediaItem);
        }
        player.prepare();
        //player.
    }

    //Initialization Of Views Takes Place Here
    private void setupView(View view){
        ImageView play_pause_image_view = view.findViewById(R.id.play_pause_image_view);
        ImageView back_15_image_view = view.findViewById(R.id.back_15_image_view);
        ImageView forward_15_image_view = view.findViewById(R.id.forward_15_image_view);
        ImageView prev_image_view = view.findViewById(R.id.prev_image_view);
        ImageView next_image_view = view.findViewById(R.id.next_image_view);
        Slider player_duration = view.findViewById(R.id.player_duration);
        TextView audio_name_text_view = view.findViewById(R.id.audio_name_text_view);
        TextView artist_name_text_view = view.findViewById(R.id.artist_name_text_view);
        ProgressBar progressBar = view.findViewById(R.id.progressBar);

        TextView audio_name_text_view_min = view.findViewById(R.id.audio_name_text_view_min);
        TextView artist_name_text_view_min = view.findViewById(R.id.artist_name_text_view_min);

        play_pause_image_view.setOnClickListener(v -> {
            if (player!=null){
                if (player.isPlaying()){
                    player.pause();
                    countDownTimer.cancel();
                    play_pause_image_view.setImageDrawable(ContextCompat.getDrawable(requireActivity(),R.drawable.play_arrow));
                }else {
                    player.play();
                    player_duration.setValue(0);
                    player_duration.setValueTo(player.getDuration());
                    countDownTimer = new CountDownTimer(player.getDuration()-player.getCurrentPosition(), 1000) {
                        public void onTick(long millisUntilFinished) {
                            player_duration.setValue(player.getCurrentPosition());
                        }

                        public void onFinish() {

                        }
                    }.start();
                    play_pause_image_view.setImageDrawable(ContextCompat.getDrawable(requireActivity(),R.drawable.pause));
                }
            }
        });

        back_15_image_view.setOnClickListener(v -> {
            long pos = player.getCurrentPosition()-1500;
            if (pos>0){
                player.seekTo(pos);
            }else {
                player.seekTo(0);
            }
        });

        forward_15_image_view.setOnClickListener(v -> {
            long pos = player.getCurrentPosition()+1500;
            if (pos < player.getDuration()){
                player.seekTo(player.getCurrentPosition()+1500);
            }else {
                player.seekTo(player.getDuration());
            }
        });

        prev_image_view.setOnClickListener(v -> {
            if (player.hasPreviousWindow()){
                player.seekToPrevious();
                countDownTimer = new CountDownTimer(player.getDuration()-player.getCurrentPosition(), 1000) {
                    public void onTick(long millisUntilFinished) {
                        player_duration.setValue(player.getCurrentPosition());
                    }

                    public void onFinish() {

                    }
                }.start();
            }
        });

        next_image_view.setOnClickListener(v -> {
            if (player.hasNextWindow()){
                player.seekToNext();
                countDownTimer = new CountDownTimer(player.getDuration()-player.getCurrentPosition(), 1000) {
                    public void onTick(long millisUntilFinished) {
                        player_duration.setValue(player.getCurrentPosition());
                    }

                    public void onFinish() {

                    }
                }.start();
            }
        });

        player_duration.addOnChangeListener((slider, value, fromUser) -> {
            if (player!=null && player_duration.getValueTo()>0){
                if (fromUser){
                    player.seekTo((long) value);
                }
            }
        });

        //Listener For Player
        player.addListener(new Player.Listener() {
            @Override
            public void onPlaybackStateChanged(int playbackState) {
                //When Player has ended, move to next audio on queue
                //if there is
                if (playbackState == Player.STATE_ENDED){
                    if (player.hasNextWindow()){
                        player.seekToNext();
                        countDownTimer = new CountDownTimer(player.getDuration()-player.getCurrentPosition(), 1000) {
                            public void onTick(long millisUntilFinished) {
                                player_duration.setValue(player.getCurrentPosition());
                            }

                            public void onFinish() {

                            }
                        }.start();
                    }
                }
                //When Player Is Ready, set the audio and artist name
                //set the duration
                //hide progress bar
                else if (playbackState == Player.STATE_READY){
                    MediaItem mediaItem = player.getCurrentMediaItem();
                    String id = mediaItem.mediaId;

                    audio_name_text_view.setText(nameOfTrack.get(id));
                    audio_name_text_view_min.setText(nameOfTrack.get(id));

                    artist_name_text_view.setText(nameOfArtist.get(id));
                    artist_name_text_view_min.setText(nameOfArtist.get(id));

                    player_duration.setValueFrom(0);
                    player_duration.setValueTo(player.getDuration());

                    progressBar.setVisibility(View.GONE);
                }
                //When Player Is getting prepared
                //show progress bar
                else if (playbackState == Player.STATE_BUFFERING){
                    progressBar.setVisibility(View.VISIBLE);
                }
            }
        });
    }
}