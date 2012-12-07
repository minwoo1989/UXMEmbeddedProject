package com.uxm.embeddedproject.camera;

import java.io.IOException;

import android.content.Context;
import android.hardware.Camera;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class Preview extends SurfaceView implements SurfaceHolder.Callback {
	
	SurfaceHolder mHolder;
    Camera mCamera = null;
  
    public void close()
    {
    	if( mCamera != null){
    		mCamera.stopPreview();
    		mCamera.release();
    		mCamera = null;
    	
    	}
    }
    
    public Preview(Context context, AttributeSet attrs) { 
 		super(context, attrs); 
 	   
 	  	try{
 	  		if (mCamera == null)
 		  		mCamera = Camera.open();
 		  	
 	        mHolder = getHolder();
 	        mHolder.addCallback(this);
 	        mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);      
 	  	}catch (Exception e) {
 		}
     } 
    
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
	}
	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		try{
				if( mCamera == null){
						mCamera = Camera.open();
				}
				if (mCamera != null) {
					try {
						mCamera.setPreviewDisplay(holder);
					  	Camera.Parameters parameters = mCamera.getParameters();
					  	
				  		mCamera.setParameters(parameters);				  		
				  		mCamera.startPreview();				  		
					} catch (IOException exception) {
						mCamera.release();
						mCamera = null;
						// TODO: add more exception handling logic here
					}
				}	
		} catch (Exception e) {
		}
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		// TODO Auto-generated method stub
      	if(mCamera!=null ){
    		mCamera.stopPreview();
    		mCamera.release(); 
    	    mCamera = null;
    	}
	}
}
