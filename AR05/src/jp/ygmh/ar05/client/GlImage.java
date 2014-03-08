package jp.ygmh.ar05.client;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import javax.microedition.khronos.opengles.GL10;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLU;
import android.opengl.GLUtils;
import android.util.Log;
import jp.androidgroup.nyartoolkit.utils.gl.AndGLView;

public class GlImage implements AndGLView.IGLViewEventListener {

	// ログ出力用
	private static final String TAG = "GlTest01";
	
	private GL10 _ref_gl;

	// 頂点座標
	private float apexs[] = new float[] {
			//	  x    y   z
				-50,   0, -50,
				 50,   0, -50,
				-50,   0,  50,
				 50,   0,  50
			};
//	private float apexs[] = new float[] { -20f, -20f, 20f, -40f,   -20f, 20f, 40f, 20f, };
//	private float apexs[] = new float[] { -1f, -1f, 1f, -1f, -1f, 1f, 1f, 1f, };

	// 頂点座標バッファ
	private FloatBuffer apexBuff;

	// 頂点テクスチャ
	private float coords[] = new float[] {
			// 上下反転
			0.0f, 1.0f, 1.0f, 1.0f, 0.0f, 0.0f, 1.0f, 0.0f, };
	// 頂点テクスチャバッファ
	private FloatBuffer coordsBuff;

	private int[] textures = new int[1];
	private int textureId = 0;

	public GlImage(AndGLView i_context, GL10 gl, Bitmap bitmap) {
		i_context._evl.add(this);

		this._ref_gl = gl;

		apexBuff = makeFloatBuffer(apexs);
		coordsBuff = makeFloatBuffer(coords);

		// テクスチャ管理番号割り当て
		gl.glDeleteTextures(1, textures, 0);
		gl.glGenTextures(1, textures, 0);
		textureId = textures[0];

		// テクスチャ管理番号バインド
		gl.glBindTexture(GL10.GL_TEXTURE_2D, textureId);

//		Bitmap bitmap = BitmapFactory.decodeResource(i_context.getResources(), R.drawable.cat512);
		Log.v(TAG, "" + bitmap.getHeight());
		GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, bitmap, 0);
//		gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_NEAREST);
		gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_LINEAR);
//		gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_NEAREST);
		gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_LINEAR);
		bitmap.recycle();
	}

	public void draw(float i_x, float i_y, float i_z) {
//		Log.v(TAG, "draw...");
		GL10 gl = this._ref_gl;
		gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
		gl.glPushMatrix();
//		GLU.gluLookAt(gl, 0, 0, 0, 0, 0, 0, 0, 0, 0);
		GLU.gluLookAt(gl, i_x, i_y, 3, i_x, i_y, 0, 0, 1, 0);
		gl.glPushMatrix();
		gl.glVertexPointer(3, GL10.GL_FLOAT, 0, apexBuff);

		gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);

		// テクスチャ配列の指定
		gl.glBindTexture(GL10.GL_TEXTURE_2D, textureId);
		gl.glTexCoordPointer(2, GL10.GL_FLOAT, 0, coordsBuff);

		// テクスチャーの有効化
		gl.glEnable(GL10.GL_TEXTURE_2D);
		gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);

		// テクスチャ描画
		gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 0, 4);

		// テクスチャーの無効化
		gl.glDisableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
		gl.glDisable(GL10.GL_TEXTURE_2D);
		gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);

		gl.glPopMatrix();
		gl.glPopMatrix();

	}

	@Override
	public void onGlChanged(GL10 i_gl, int i_width, int i_height) {
		if (this._ref_gl != null) {
		}
		this._ref_gl = i_gl;
	}

	@Override
	public void onGlMayBeStop() {
		this._ref_gl = null;
	}

	// 頂点の配列をバッファーに変換するメソッド
	private static FloatBuffer makeFloatBuffer(float[] values) {
		ByteBuffer bb = ByteBuffer.allocateDirect(values.length * 4);
		bb.order(ByteOrder.nativeOrder());
		FloatBuffer fb = bb.asFloatBuffer();
		fb.put(values);
		fb.position(0);
		return fb;
	}
}
