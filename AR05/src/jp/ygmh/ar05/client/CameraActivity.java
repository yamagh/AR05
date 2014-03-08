package jp.ygmh.ar05.client;

import javax.microedition.khronos.opengles.GL10;

import jp.androidgroup.nyartoolkit.markersystem.NyARAndMarkerSystem;
import jp.androidgroup.nyartoolkit.markersystem.NyARAndSensor;
import jp.androidgroup.nyartoolkit.sketch.AndSketch;
import jp.androidgroup.nyartoolkit.utils.camera.CameraPreview;
import jp.androidgroup.nyartoolkit.utils.gl.AndGLBox;
import jp.androidgroup.nyartoolkit.utils.gl.AndGLDebugDump;
import jp.androidgroup.nyartoolkit.utils.gl.AndGLFpsLabel;
import jp.androidgroup.nyartoolkit.utils.gl.AndGLTextLabel;
import jp.androidgroup.nyartoolkit.utils.gl.AndGLView;
import jp.nyatla.nyartoolkit.markersystem.NyARMarkerSystemConfig;

import com.ymgh.ar05.R;

import android.os.Bundle;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.FrameLayout;

public class CameraActivity extends AndSketch implements AndGLView.IGLFunctionEvent {
	
	Bitmap img = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		// 使用するレイアウトの指定
		setContentView(R.layout.activity_cam);
		
		// DB操作用ヘルパー生成
		ARDbOpenHelper ardboh;
		try{
			ardboh = new ARDbOpenHelper(this);
		}catch(Exception e){
			Log.d("ERR", "Occured error of ARDbOpenHelper");
			return;
		}
		
		// DB接続
		SQLiteDatabase sqldb = ardboh.getReadableDatabase();
		
		// DBからレコードを取得してカーソルに格納
		String[] cols = {"image"};
		Cursor cur = null;
		try{
			cur = sqldb.query(false, ARDbOpenHelper._TABLE, cols, null, null, null, null, null, null, null);
		}catch(Exception e){
			Log.d("ERR", "Occured error by query execute.");
			return;
		}

		Log.v("INFO", Integer.toString(cur.getCount()));
		if(cur.moveToFirst()){
			// バイナリを取得
			byte[] imgByte = cur.getBlob(0);
		
			// バイナリから画像に変換
			img = BitmapFactory.decodeByteArray(imgByte, 0, imgByte.length);
		}
		
		// DB切断
		sqldb.close();
	}

	CameraPreview _camera_preview;
	AndGLView _glv;
	Camera.Size _cap_size;
	
	@Override
	public void onStart(){
		super.onStart();
		// FrameLayout(カメラの映像を表示するところ)の取得
		FrameLayout fr = ((FrameLayout)this.findViewById(R.id.sketchLayout));
		
		// CameraPreview開始
		this._camera_preview = new CameraPreview(this);
		
		// カメラサイズの取得
		this._cap_size = this._camera_preview.getRecommendPreviewSize(320, 240);
		
		// デフォルトの画面の高さを取得
		int h = this.getWindowManager().getDefaultDisplay().getHeight();
		
		// 画面サイズの計算
		int screen_w = (this._cap_size.width * h / this._cap_size.height);
		int screen_h = h;
		
		// FrameLayoutにカメラの映像を制御するためのViewを追加
		// (映像の開始/終了/停止/フレームレートなどを制御?)
		fr.addView(this._camera_preview, 0, new LayoutParams(screen_w, screen_h));
		
		// FrameLayoutにカメラの映像に合成するViewを追加
		this._glv = new AndGLView(this);
		fr.addView(this._glv, 0, new LayoutParams(screen_w, screen_h));
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	public void onClickCameraBackButton(View v){
		finish();
	}
	
	
	
	

	NyARAndSensor _ss;
	NyARAndMarkerSystem _ms;
	private int _mid;
	AndGLTextLabel text;
	AndGLBox box;
	AndGLFpsLabel fps;
	AndGLDebugDump _debug=null;
	GlImage gli;
	
	@Override
	public void setupGL(GL10 i_gl) {
		try{
			AssetManager assetMng = getResources().getAssets();
			//create sensor controller.
			this._ss=new NyARAndSensor(this._camera_preview,this._cap_size.width,this._cap_size.height,30);
			//create marker system
			this._ms=new NyARAndMarkerSystem(new NyARMarkerSystemConfig(this._cap_size.width,this._cap_size.height));
			this._mid=this._ms.addARMarker(assetMng.open("AR/data/hiro.pat"),16,25,80);
			this._ss.start();
			//setup openGL Camera Frustum
			i_gl.glMatrixMode(GL10.GL_PROJECTION);
			i_gl.glLoadMatrixf(this._ms.getGlProjectionMatrix(),0);
			this.text=new AndGLTextLabel(this._glv);
			this.box=new AndGLBox(this._glv,40);
			this.gli = new GlImage(this._glv, i_gl, img);
			this._debug=new AndGLDebugDump(this._glv);
			this.fps=new AndGLFpsLabel(this._glv,"MarkerPlaneActivity");
			this.fps.prefix=this._cap_size.width+"x"+this._cap_size.height+":";
			
		}catch(Exception e){
			e.printStackTrace();
			this.finish();
			
		}
	}

	Exception ex=null;
	
	@Override
	public void drawGL(GL10 i_gl) {
		try{
			//背景塗り潰し色の指定
			i_gl.glClearColor(0,0,0,0);
	        //背景塗り潰し
	        i_gl.glClear(GL10.GL_COLOR_BUFFER_BIT|GL10.GL_DEPTH_BUFFER_BIT);
	        if(ex!=null){
	        	_debug.draw(ex);
	        	return;
	        }
	        fps.draw(0, 0);
			synchronized(this._ss){
				this._ms.update(this._ss);
				if(this._ms.isExistMarker(this._mid)){
			        this.text.draw("found"+this._ms.getConfidence(this._mid),0,16);
					i_gl.glMatrixMode(GL10.GL_MODELVIEW);
					i_gl.glLoadMatrixf(this._ms.getGlMarkerMatrix(this._mid),0);
//					this.box.draw(0,0,20);
					this.gli.draw(0, 0, 20);  // add yama
				}
		}
		}catch(Exception e)
		{
			ex=e;
		}
		
	}

}
