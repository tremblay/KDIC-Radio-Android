package edu.grinnell.kdic;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class StreamBannerFragment extends Fragment {

	private String STREAMURL = "http://kdic.grinnell.edu:8001/kdic128";
	private String IMAGEURL = "http://kdic.grinnell.edu/wp-content/uploads/EDM-150x150.gif";
	
	private MediaPlayer kdicStream = new MediaPlayer(); //KDIC stream
    private Button playButton; //playPause button
    private ImageView metadataImage; //Metadata image. Duhh.
    private TextView metadataText; //Double duhh.
    private final ImageDownloader mDownload = new ImageDownloader();
    
    boolean isLoading = false; //true if stream is loading but not playing
    
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        
        
    }

	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
    	
    	return inflater.inflate(R.layout.fragment_stream_banner, container, false);
    }
    
    @Override
    public void onViewCreated(View view, Bundle ofJoy){
    	//Initial playPause settings
        playButton = (Button) view.findViewById(R.id.playButton);
        playButton.setText("Pause");
        playButton.setBackgroundColor(Color.RED);
        
        metadataImage = (ImageView) view.findViewById(R.id.curPlayingImage);
        metadataText = (TextView) view.findViewById(R.id.curPlayingText);
        
        //playPause listener, stops/starts stream
        playButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                playPause();
            }
        });
        
        //Starts Stream
        setupPlayer();
        
        kdicStream.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
			@Override
			public void onPrepared(MediaPlayer mp) {
			       kdicStream.start();
			       isLoading = false;
			       playButton.setText("Pause"); // WHY WON'T THIS APPEAR FOR MORE THAN A FRACTION OF A SECOND.
			       playButton.setBackgroundColor(Color.RED);
			}
        });

        mDownload.download(IMAGEURL, metadataImage);
        
        if (!(kdicStream.isPlaying())){
        	startPlaying();
        }
        
    }
    
  //If the stream is not stopped, stop. Else, start.
    public void playPause(){
    		if(isLoading){
    			// do nothing
    		} else if ((kdicStream.isPlaying())) {
    			stopPlaying();
    		} else {
    			startPlaying();
    		}
    	}

    //Changes playPause to 'loading' state, prepares stream, starts stream
    public void startPlaying(){
    	isLoading = true;
    	playButton.setText("Starting..."); 
        playButton.setBackgroundColor(Color.YELLOW);
    	
    	try {
    		kdicStream.prepareAsync();
		} catch (IllegalStateException e) {
			e.printStackTrace();
		}
    	
    }
    
    //Stops stream, changes playPause to 'stopped' state.
    public void stopPlaying(){
    	kdicStream.stop();
    	playButton.setText("Play");
        playButton.setBackgroundColor(Color.GREEN);
    }
    
    //Sets stream's type and URL
    public void setupPlayer(){
        kdicStream.setAudioStreamType(AudioManager.STREAM_MUSIC);
        try {
			kdicStream.setDataSource(STREAMURL);
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
        
        
        
        
    }
    
    public void setMetadataImageURL(String imgurl){
        metadataText.setText("Image has been changed");
    }
        
}
