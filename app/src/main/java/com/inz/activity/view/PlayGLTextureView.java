package com.inz.activity.view;

import android.content.Context;
import android.util.AttributeSet;

import com.inz.player.gles.GLESRendererImpl;


/**
 * Created by Administrator on 2017/6/23.
 */

public class PlayGLTextureView extends ZoomableTextureView {

    private Context mContext;
    GLESRendererImpl mRenderer;
    public PlayGLTextureView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        mRenderer = new GLESRendererImpl(context,this,null);
        setRenderer(mRenderer);
        setRenderMode(RENDERMODE_WHEN_DIRTY);
    }
}
