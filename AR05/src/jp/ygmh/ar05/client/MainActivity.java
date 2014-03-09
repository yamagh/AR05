package jp.ygmh.ar05.client;

import org.xmlrpc.android.XMLRPCClient;
import org.xmlrpc.android.XMLRPCException;
import com.ymgh.ar05.R;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.View;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	public void onClickCameraButton(View v) {

		Intent intent = new Intent(MainActivity.this, CameraActivity.class);
		startActivity(intent);
	}

	public void onClickGetImage(View v) {
		ImageGetterTask igt = new ImageGetterTask();
		igt.execute();
	}

	/**
	 * 画像取得用の内部クラス
	 *
	 * @author yama
	 *
	 */
	class ImageGetterTask extends AsyncTask<String, Integer, Long> {

		static final String _TAG    = "BG_TASK";
		static final String _HOST   = "http://192.168.24.55:8081";  // XML-RPCサーバのIPアドレス
		static final String _CLASS  = "Picture";  // RPCするクラス
		static final String _METHOD = "getPictureBase64";  // Callするメソッド
		Object o = null;  // 実行結果を受取るオブジェクト

		@Override
		protected Long doInBackground(String... params) {
			Log.d(_TAG, "thread in background");

			try {
				// XML-RPCサーバ接続
				XMLRPCClient client = new XMLRPCClient(_HOST);

				// RPC実行: 画像をBase64でエンコードした文字列が返る
				o = client.call(_CLASS + "." + _METHOD);

				// バイトデータに変換
				byte[] encodedByte = o.toString().getBytes();

			} catch (XMLRPCException e) {
				Log.e(_TAG, e.getMessage());
				return null;
			}


			// Base64をデコード
			byte[] decodedByte = Base64.decode(encodedByte, Base64.DEFAULT);

			// バイトデータから画像を生成
//			ba[0] = BitmapFactory.decodeByteArray(decodedByte, 0,
//					decodedByte.length);

			Log.d(_TAG, "" + o.toString());


			Log.d(_TAG, "DB格納処理開始");

			// 画像格納用のDB接続 (DBがなければCREATE)
			ARDbOpenHelper ardbh = new ARDbOpenHelper(getApplicationContext());
			SQLiteDatabase sqldb = ardbh.getWritableDatabase();

			ContentValues val = new ContentValues();
			val.put("image", decodedByte);

			try{
				sqldb.insert("marker_table", null, val);
				Log.d(_TAG, "DB格納成功");
			}catch (Exception e){
				Log.d(_TAG, "DB格納失敗");
			}
			return null;
		}
	}
}
