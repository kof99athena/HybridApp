package com.nha2023.ex96hybridapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.webkit.JavascriptInterface
import android.webkit.WebChromeClient
import android.webkit.WebViewClient
import com.nha2023.ex96hybridapp.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    lateinit var binding : ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //웹뷰 설정들
        binding.wv.settings.javaScriptEnabled = true
        binding.wv.settings.allowFileAccess = true //원래 AJAX 기술은 서버에서만 동작한다. 내가 네트워크를 쓰지않고도 AJAX를 쓰고싶다면? 하이브리드면 assets파일에 있을수도있잖아 (이걸 로컬에서 동작하도록 허용하는 속성)

        binding.wv.webViewClient = WebViewClient() //웹뷰안에서 브라우저가 열리게. 새 창 안뜨게한다.
        binding.wv.webChromeClient = WebChromeClient()


        //2) webview에서 사용할 메소드들을 가지고 있는 중계인 객체를 생성 및 웹뷰에 설정
        binding.wv.addJavascriptInterface(WebViewConnector(),"Droid") //"Droid"라는 이름이 웹문서의 window객체 멤버변수로 자동 추가됨.
        //name이 가장 중요 !! "Droid"가 누군지 알아야함

        //웹뷰가 보여줄 웹문서(.html) 로드하기 : android_asset s안붙임
        binding.wv.loadUrl("file:///android_asset/index.html")

        //1. 네이티브에서 웹 UI 제어
        binding.btn.setOnClickListener {
            //HTML 직접 제어는 못함. JS의 특정 함수를 호출하여 대신 제어하기
            //원래 홈페이지 주소를 넣는데, 여기 안에 실행할 함수를 넣어준다.
            var msg : String = binding.et.text.toString()
            binding.wv.loadUrl("javascript:setMessage('$msg')") // ('') ''은 문자열로 받는다는 뜻이다.
            binding.et.setText("")  //
        }

    }//onCreate method

    //2) 웹뷰의 JS에서 호출할 수 있는 메소드를 모아놓은 클래스 설계
    inner class WebViewConnector{ //액티비티의 기능을 맘대로 쓸수있다

        //javascript에서 호출할 수 있는 메소드
        @JavascriptInterface
        fun showMessage(msg:String){
            binding.tv.text = "웹뷰로부터 받은 메세지 : $msg"
        }

        //디바이스의 고유 기능인 사진 선택 앱을 알려주는 기능 메소드
        @JavascriptInterface
        fun openPhotoApp(){
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivity(intent) //원래는 startActivityforResult로 해야한다. test니까 간단하게 하자

            //선택한 사진을 웹서버에 전송하고 웹 문서에서 업로드된 사진 파일을 보여주는 방식
        }

    }
}//MainActivity class