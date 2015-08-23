package com.example.ppp.rgbbutton;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;

import com.example.ppp.rgbbutton.network.ApiService;
import com.jakewharton.rxbinding.view.RxView;

import java.util.concurrent.TimeUnit;

import butterknife.Bind;
import butterknife.ButterKnife;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.subjects.BehaviorSubject;

public class MainActivity extends AppCompatActivity {

    @Bind(R.id.red_button)
    Button mRedButton;
    @Bind(R.id.green_button)
    Button mGreenButton;
    @Bind(R.id.blue_button)
    Button mBlueButton;
    @Bind(R.id.clear_button)
    Button mClearButton;
    @Bind(R.id.rgb_text)
    TextView mRgbText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        BehaviorSubject<Object> clearSubject = BehaviorSubject.create((Object) null);
        mClearButton.setOnClickListener(v -> {
            clearSubject.onNext(null);
        });

        ApiService.IgApiService service = ApiService.getIgApiService();

        clearSubject
                .switchMap(clearEvent -> createRgbStream())
                .throttleLast(1, TimeUnit.SECONDS)
                .flatMap(service::getLedChika)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(result -> {
                    mRgbText.setText("rgb:" + result.rgb);
                }, Throwable::printStackTrace);
    }

    @NonNull
    private Observable<String> createRgbStream() {
        Observable<Integer> redStream = RxView.clicks(mRedButton).doOnNext(event -> Log.d("mylog", "red"))
                .map(event -> 1).startWith(0).scan((sum, count) -> (sum + count) % 2).map(value -> value * 4);
        Observable<Integer> greenStream = RxView.clicks(mGreenButton).map(value -> 2)
                .map(event -> 1).startWith(0).scan((sum, count) -> (sum + count) % 2).map(value -> value * 2);
        Observable<Integer> blueStream = RxView.clicks(mBlueButton).map(value -> 4)
                .map(event -> 1).startWith(0).scan((sum, count) -> (sum + count) % 2).map(value -> value);

        return Observable.combineLatest(redStream, greenStream, blueStream, (r, g, b) -> {
            String binaryStr = Integer.toBinaryString(r + g + b);
            return String.format("%3s", binaryStr).replace(" ", "0");
        });
    }
}
