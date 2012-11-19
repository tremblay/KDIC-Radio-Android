package edu.grinnell.kdic;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import com.larvalabs.svgandroid.SVG;
import com.larvalabs.svgandroid.SVGParser;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

public class StreamBannerFragment extends Fragment {

	private String STREAMURL = "http://kdic.grinnell.edu:8001/kdic128";
	private String IMAGEURL = "http://kdic.grinnell.edu/wp-content/uploads/EDM-150x150.gif";
	
	private MediaPlayer kdicStream = new MediaPlayer(); //KDIC stream
    private ImageButton playButton; //playPause button
    private ImageView metadataImage; //Metadata image. Duhh.
    private TextView metadataText; //Double duhh.
    private final ImageDownloader mDownload = new ImageDownloader();
    
    boolean isLoading = false; //true if stream is loading but not playing
    
    private SVG onPreparedButtonImage;
    private SVG onStartButtonImage;
    private SVG onStopButtonImage;
    
    
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
    	
    	//Initializing widget variables. 
        playButton = (ImageButton) view.findViewById(R.id.playButton);
        metadataImage = (ImageView) view.findViewById(R.id.curPlayingImage);
        metadataText = (TextView) view.findViewById(R.id.curPlayingText);
        
        //initializing playButton images variables.
        onStartButtonImage = SVGParser.getSVGFromResource(getResources(), R.drawable.loadingfinalgray); 
        onPreparedButtonImage = SVGParser.getSVGFromResource(getResources(), R.drawable.pausefinal); 
        onStopButtonImage = SVGParser.getSVGFromResource(getResources(), R.drawable.playfinal); 
        
        //playPause listener. Stops/starts stream
        playButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                playPause();
            }
        });
        
        // onPrepared listener. Starts stream and changes playButton image when the stream has finished setting up.
        kdicStream.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
			@Override
			public void onPrepared(MediaPlayer mp) {
			       kdicStream.start();
			       isLoading = false;
			       playButton.setImageDrawable(onPreparedButtonImage.createPictureDrawable());
			}
        });

        // Set metadata image to hardcoded URL
        mDownload.download(IMAGEURL, metadataImage);
        
        //Starts Stream
        setupPlayer();
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
	    playButton.setImageDrawable(onStartButtonImage.createPictureDrawable());
    	
    	try {
    		kdicStream.prepareAsync();
		} catch (IllegalStateException e) {
			e.printStackTrace();
		}
    }
    
    //Stops stream, changes playPause to 'stopped' state.
    public void stopPlaying(){
    	kdicStream.stop();
	       playButton.setImageDrawable(onStopButtonImage.createPictureDrawable());
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
    
}
