package com.example.asus_.xunfeivoice;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.iflytek.cloud.RecognizerListener;
import com.iflytek.cloud.RecognizerResult;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechRecognizer;
import com.iflytek.cloud.SpeechSynthesizer;
import com.iflytek.cloud.SpeechUtility;
import com.iflytek.cloud.SynthesizerListener;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

public class MainActivity extends AppCompatActivity {
private Button btnSynthese;
    private EditText edtInputSynthese;
    private Button btnRecognize;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btnSynthese= (Button) findViewById(R.id.btn_Synthese);
        edtInputSynthese= (EditText) findViewById(R.id.edt_input_Synthese);
        btnRecognize= (Button) findViewById(R.id.btn_Recognize);

        SpeechUtility.createUtility(MainActivity.this, SpeechConstant.APPID + "=58ddc5ea");
        final SpeechRecognizer mIat= SpeechRecognizer.createRecognizer(MainActivity.this, null);
//2.设置听写参数，详见《科大讯飞MSC API手册(Android)》SpeechConstant类
        mIat.setParameter(SpeechConstant.DOMAIN, "iat");
        mIat.setParameter(SpeechConstant.LANGUAGE, "zh_cn");
        mIat.setParameter(SpeechConstant.ACCENT, "mandarin ");
//3.开始听写   mIat.startListening(mRecoListener);
//听写监听器
         final RecognizerListener mRecoListener = new RecognizerListener(){

             @Override
             public void onVolumeChanged(int i, byte[] bytes) {

             }

             @Override
             public void onBeginOfSpeech() {

             }

             @Override
             public void onEndOfSpeech() {

             }

             @Override
             public void onResult(RecognizerResult recognizerResult, boolean b) {
                 String text=parseIatResult(recognizerResult.getResultString ());
                  Toast.makeText(MainActivity.this,text,Toast.LENGTH_LONG).show();



             }


             @Override
             public void onError(SpeechError speechError) {

             }

             @Override
             public void onEvent(int i, int i1, int i2, Bundle bundle) {

             }
         };

        final SpeechSynthesizer mTts = SpeechSynthesizer.createSynthesizer(MainActivity.this, null);
        mTts.setParameter(SpeechConstant.VOICE_NAME, "xiaoyan");//设置发音人
        mTts.setParameter(SpeechConstant.SPEED, "50");//设置语速
        mTts.setParameter(SpeechConstant.VOLUME, "80");//设置音量，范围0~100
        mTts.setParameter(SpeechConstant.ENGINE_TYPE, SpeechConstant.TYPE_CLOUD); //设置云端

         final SynthesizerListener mSynListener = new SynthesizerListener() {
            //会话结束回调接口，没有错误时，error为null
            public void onCompleted(SpeechError error) {

            }

            //缓冲进度回调
            //percent为缓冲进度0~100，beginPos为缓冲音频在文本中开始位置，endPos表示缓冲音频在文本中结束位置，info为附加信息。
            public void onBufferProgress(int percent, int beginPos, int endPos, String info) {
            }

            //开始播放
            public void onSpeakBegin() {
            }

            //暂停播放
            public void onSpeakPaused() {
            }

            //播放进度回调
            //percent为播放进度0~100,beginPos为播放音频在文本中开始位置，endPos表示播放音频在文本中结束位置.
            public void onSpeakProgress(int percent, int beginPos, int endPos) {
            }

            //恢复播放回调接口
            public void onSpeakResumed() {
            }

            //会话事件回调接口
            public void onEvent(int arg0, int arg1, int arg2, Bundle arg3) {
            }
        };
        btnSynthese.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text=edtInputSynthese.getText().toString().trim();
                mTts.startSpeaking(text, mSynListener);
            }
        });

        btnRecognize.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mIat.startListening(mRecoListener);
                //String text=edtInputRecognize.getText().toString().trim();
            }
        });

    }


    public static String parseIatResult(String json) {
        StringBuffer ret = new StringBuffer();
        try {
            JSONTokener tokener = new JSONTokener(json);
            JSONObject joResult = new JSONObject(tokener);

            JSONArray words = joResult.getJSONArray("ws");
            for (int i = 0; i < words.length(); i++) {
                // 转写结果词，默认使用第一个结果
                JSONArray items = words.getJSONObject(i).getJSONArray("cw");
                JSONObject obj = items.getJSONObject(0);
                ret.append(obj.getString("w"));
//				如果需要多候选结果，解析数组其他字段
//				for(int j = 0; j < items.length(); j++)
//				{
//					JSONObject obj = items.getJSONObject(j);
//					ret.append(obj.getString("w"));
//				}
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ret.toString();
    }
}