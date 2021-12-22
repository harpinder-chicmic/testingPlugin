package org.apache.cordova.smaato;

import android.annotation.SuppressLint;

import android.annotation.TargetApi;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnDismissListener;
import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.net.Uri;
import android.os.Build;
import android.view.SurfaceHolder;
import android.view.Window;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;

import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;
import android.widget.VideoView;
import androidx.annotation.NonNull;

import com.smaato.sdk.banner.widget.BannerError;
import com.smaato.sdk.core.Config;
import com.smaato.sdk.core.SmaatoSdk;
import com.smaato.sdk.core.log.LogLevel;
import com.smaato.sdk.interstitial.EventListener;
import com.smaato.sdk.interstitial.Interstitial;
import com.smaato.sdk.interstitial.InterstitialAd;
import com.smaato.sdk.interstitial.InterstitialError;
import com.smaato.sdk.interstitial.InterstitialRequestError;

import com.smaato.sdk.banner.ad.BannerAdSize;
import com.smaato.sdk.banner.widget.BannerView;

import com.smaato.sdk.rewarded.RewardedInterstitial;
import com.smaato.sdk.rewarded.RewardedInterstitialAd;
import com.smaato.sdk.rewarded.RewardedError;
import com.smaato.sdk.rewarded.RewardedRequestError;
//import com.smaato.sdk.rewarded.EventListener;
import java.util.TimeZone;

import org.apache.cordova.CordovaWebView;
import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaArgs;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CordovaResourceApi;
import org.apache.cordova.CordovaInterface;
import org.apache.cordova.PluginResult;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class Smaato extends CordovaPlugin  implements OnCompletionListener, OnPreparedListener, OnErrorListener, OnDismissListener {

    enum rewardAdEvent{
        onAdLoaded,
        onAdFailedToLoad,
        onAdError,
        onAdClosed,
        onAdClicked,
        onAdStarted,
        onAdReward,
        onAdTTLExpired
    }
    enum bannerAdEvent{
        onAdLoaded,
        onAdFailedToLoad,
        onAdClicked,
        onAdImpression,
        onAdTTLExpired
    }
    enum interstitialAdEvent{
        onAdLoaded,
        onAdFailedToLoad,
        onAdError,
        onAdClosed,
        onAdClicked,
        onAdOpened,
        onAdImpression,
        onAdTTLExpired
    }

    //Video player
    protected static final String LOG_TAG = "VideoPlayer";
    protected static final String ASSETS = "/android_asset/";
    private CallbackContext callbackContext = null;

    private CallbackContext callbackRewardAd = null;
    private CallbackContext callbackInterstitialAd = null;
    private CallbackContext callbackBannerAd = null;

    private Dialog dialog;
    private MediaPlayer player;

    private static final String EMPTY = "";
    public static final String TAG = "Smaato";

    private RelativeLayout adViewLayout = null;
    private BannerView bannerView = null;
    private BannerView bannerView2 = null;

    private static final String ACTION_INIT_SMAATO = "initSmaato";
    private static final String ACTION_SET_OPTIONS = "setOptions";
    private static final String ACTION_SHOW_BANNER_AD = "showBannerAd";
    private static final String ACTION_LOAD_REWARDED_AD = "loadRewardedAd";
    private static final String ACTION_SHOW_REWARDED_AD = "showRewardedAd";
    private static final String ACTION_CREATE_INTERSTITIAL_VIEW = "showInterstitialAd";
    private static final String ACTION_PLAY_VIDEO = "play";
    private static final String ACTION_CLOSE_VIDEO = "close";
    private static final String ACTION_CLOSE_BANNER_AD = "closeBannerAd";
    private static final String ACTION_CLOSE_REWARDED_AD = "closeRewardedAd";

    /* options */
    private static final String OPT_PUBLISHER_ID = "publisherId";
    private static final String OPT_INTERSTITIAL_AD_ID = "interstitialAdId";
    private static final String OPT_BANNER_AD_ID = "bannerAdId";
    private static final String OPT_REWARDED_AD_ID = "rewardedAdId";
    private static final String OPT_AD_SIZE = "adSize";

    //Options
    private String publisherId = EMPTY;
    private String interstitialAdId = EMPTY;
    private String bannerAdId = EMPTY;
    private String rewardedAdId = EMPTY;
//    private AdSize adSize = AdSize.SMART_BANNER;
    /** Whether or not the ad should be positioned at top or bottom of screen. */
    private boolean bannerAtTop = false;
    /** Whether or not the banner will overlap the webview instead of push it up or down */
    private boolean bannerOverlap = false;
    private boolean offsetTopBar = false;
    private boolean isTesting = false;
    private JSONObject adExtras = null;
    private boolean autoShow = true;

    RewardedInterstitialAd rewardAd = null;
    public Smaato() {
    }
    /**
     * Executes the request and returns PluginResult.
     *
     * @param action            The action to execute.
     * @param args              JSONArry of arguments for the plugin.
     * @param callbackContext   The callback id used when calling back into JavaScript.
     * @return                  True if the action was valid, false if not.
     */
    public boolean execute(String action,  CordovaArgs args, CallbackContext callbackContext) throws JSONException {
        Log.i("execute ","called "+action);
        PluginResult result = null;
        Log.v("PLAY","Check 00");
//        JSONArray inputs = args.getJSONArray(0);


        Log.v("PLAY","Check 0");
        if (ACTION_INIT_SMAATO.equals(action)) {
//            JSONObject options = inputs.optJSONObject(0);
            JSONObject options = args.getJSONObject(0);
            result = initSmaato(options, callbackContext);
        }
        else if(ACTION_SET_OPTIONS.equals(action)){
//            JSONObject options = inputs.optJSONObject(0);
            JSONObject options = args.getJSONObject(0);
            result = executeSetOptions(options, callbackContext);
        }
        else if (ACTION_SHOW_BANNER_AD.equals(action)) {
//            JSONObject options = inputs.optJSONObject(0);
            JSONObject options = args.getJSONObject(0);
            result = executeShowBannerAd(options, callbackContext);
        }
        else if (ACTION_CREATE_INTERSTITIAL_VIEW.equals(action)) {
//            JSONObject options = inputs.optJSONObject(0);
            JSONObject options = args.getJSONObject(0);
            result = executeShowInterstitialAd(options, callbackContext);
        }
        else if (ACTION_SHOW_REWARDED_AD.equals(action)) {
//            JSONObject options = inputs.optJSONObject(0);
            JSONObject options = args.getJSONObject(0);
            result = executeShowRewardedAd(options, callbackContext);
        }
        else if (ACTION_LOAD_REWARDED_AD.equals(action)) {
//            JSONObject options = inputs.optJSONObject(0);
            JSONObject options = args.getJSONObject(0);
            result = executeLoadRewardedAd(options, callbackContext);
        }
        else if (ACTION_PLAY_VIDEO.equals(action)) {

            Log.v("PLAY","Check 1");
            this.callbackContext = callbackContext;

            CordovaResourceApi resourceApi = webView.getResourceApi();
            String target = args.getString(0);
            final JSONObject optionsVideo = args.getJSONObject(1);

            String fileUriStr;
            try {
                Uri targetUri = resourceApi.remapUri(Uri.parse(target));
                fileUriStr = targetUri.toString();
            } catch (IllegalArgumentException e) {
                fileUriStr = target;
            }

            Log.v(LOG_TAG, fileUriStr);

            final String path = stripFileProtocol(fileUriStr);

            // Create dialog in new thread
            cordova.getActivity().runOnUiThread(new Runnable() {
                public void run() {
                    openVideoDialog(path, optionsVideo);
                }
            });

            // Don't return any result now
            PluginResult pluginResult = new PluginResult(PluginResult.Status.NO_RESULT);
            pluginResult.setKeepCallback(true);
            callbackContext.sendPluginResult(pluginResult);
            callbackContext = null;
            return true;
        }
        else if (ACTION_CLOSE_VIDEO.equals(action)) {
            if (dialog != null) {
                if(player.isPlaying()) {
                    player.stop();
                }
                player.release();
                dialog.dismiss();
            }

            if (callbackContext != null) {
                result = new PluginResult(PluginResult.Status.OK);
                result.setKeepCallback(false); // release status callback in JS side
                callbackContext.sendPluginResult(result);
                callbackContext = null;
            }

            return true;
        }
        else if (ACTION_CLOSE_BANNER_AD.equals(action)) {

            Log.d(TAG, String.format("Action passed: %s", action));
            bannerView.destroy();
            bannerView2.destroy();
            Log.d(TAG, String.format("Action passed: %s", action));

            return true;
        }
        else if (ACTION_CLOSE_REWARDED_AD.equals(action)) {
            
            return true;
        }
        else {
            Log.d(TAG, String.format("Invalid action passed: %s", action));
            result = new PluginResult(PluginResult.Status.INVALID_ACTION);
        }

        Log.v("PLAY","Check 2");
        if(result != null) callbackContext.sendPluginResult( result );

        return true;
    }

    //--------------------------------------------------------------------------
    // LOCAL METHODS
    //--------------------------------------------------------------------------

    private PluginResult initSmaato(JSONObject options, CallbackContext callbackContext) {
        Log.w(TAG, "executeSetOptions");
        Log.i(TAG,"Publisher ID 2 : " +publisherId);

        final CallbackContext delayCallback = callbackContext;
        cordova.getActivity().runOnUiThread(new Runnable(){
            @Override
            public void run() {

                Config config = Config.builder().setLogLevel(LogLevel.ERROR).setHttpsOnly(true).build();
                SmaatoSdk.init(cordova.getActivity().getApplication(),config, publisherId);
                SmaatoSdk.setGPSEnabled(true);

                if(delayCallback!=null)
                    delayCallback.success();
            }
        });

        return null;
    }

    private PluginResult executeSetOptions(JSONObject options, CallbackContext callbackContext) {
        Log.w(TAG, "executeSetOptions");

        this.setOptions( options );

        callbackContext.success();
        return null;
    }

    private void setOptions( JSONObject options ) {
        if(options == null) return;

        if(options.has(OPT_PUBLISHER_ID)) this.publisherId = options.optString( OPT_PUBLISHER_ID );
        if(options.has(OPT_INTERSTITIAL_AD_ID)) this.interstitialAdId = options.optString( OPT_INTERSTITIAL_AD_ID );
        if(options.has(OPT_BANNER_AD_ID)) this.bannerAdId = options.optString( OPT_BANNER_AD_ID );
        if(options.has(OPT_REWARDED_AD_ID)) this.rewardedAdId = options.optString( OPT_REWARDED_AD_ID );

//        if(options.has(OPT_AD_SIZE)) this.adSize = adSizeFromString( options.optString( OPT_AD_SIZE ) );

        Log.i(TAG,"Publisher ID 1: " +publisherId);
    }

    // @SuppressLint("LongLogTag")

    private PluginResult executeShowBannerAd(JSONObject options, CallbackContext callbackContext) {
        Log.d(TAG, "executeShowBannerAd: called");
        this.setOptions( options );
        callbackBannerAd = callbackContext;

        this.cordova.getActivity().runOnUiThread(new Runnable(){
            @Override
            public void run() {
                RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
                        RelativeLayout.LayoutParams.MATCH_PARENT,
                        RelativeLayout.LayoutParams.WRAP_CONTENT);
                layoutParams.addRule(bannerAtTop ? RelativeLayout.ALIGN_PARENT_TOP : RelativeLayout.ALIGN_PARENT_BOTTOM);
                if (adViewLayout == null) {
                    Log.d("Raj","adViewLayout is null re!");
                    adViewLayout = new RelativeLayout(cordova.getActivity());
                    RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                            RelativeLayout.LayoutParams.MATCH_PARENT,
                            RelativeLayout.LayoutParams.MATCH_PARENT);
                    try {
                        ((ViewGroup)(((View)webView.getClass().getMethod("getView").invoke(webView)).getParent())).addView(adViewLayout, params);
                    } catch (Exception e) {
                        ((ViewGroup) webView).addView(adViewLayout, params);
                    }
                }
                adViewLayout.bringToFront();
                bannerView =  new BannerView(adViewLayout.getContext());
                adViewLayout.addView(bannerView,layoutParams);
                if(bannerAdId==null || bannerAdId.equals(EMPTY)){
                    Log.e("bannerAd", "Please put your smaato bannerAd id into the javascript code. No ad to display.");
//                    return null;
                }
                bannerView.loadAd(bannerAdId,BannerAdSize.XX_LARGE_320x50);
                bannerView.setEventListener(bannerAdEventListener);


                RelativeLayout.LayoutParams layoutParams2 = new RelativeLayout.LayoutParams(
                        RelativeLayout.LayoutParams.MATCH_PARENT,
                        RelativeLayout.LayoutParams.WRAP_CONTENT);
                layoutParams2.addRule(!bannerAtTop ? RelativeLayout.ALIGN_PARENT_TOP : RelativeLayout.ALIGN_PARENT_BOTTOM);

                bannerView2 =  new BannerView(adViewLayout.getContext());
                adViewLayout.addView(bannerView2,layoutParams2);
                bannerView2.loadAd(bannerAdId,BannerAdSize.XX_LARGE_320x50);
                bannerView2.setEventListener(bannerAdEventListener);

//                if(callbackBannerAd!=null)
//                    callbackBannerAd.success();
            }
        });
        return null;
    }

    private PluginResult executeShowInterstitialAd(JSONObject options, CallbackContext callbackContext) {
        Log.d(TAG,"executeShowInterstitialAd : called");
        adViewLayout = null;
        this.setOptions( options );
        callbackInterstitialAd = callbackContext;
        cordova.getActivity().runOnUiThread(new Runnable(){
            @Override
            public void run() {

                if(interstitialAdId==null || interstitialAdId.equals(EMPTY) ){
                    Log.e("interstitial", "Please put your interstialAd id into the javascript code. No ad to display.");
//                    return null;
                }       

                Interstitial.loadAd(interstitialAdId, interstitialAdEventListener);

//                if(callbackInterstitialAd!=null)
//                    callbackInterstitialAd.success();
            }
        });
        return null;
    }

    private PluginResult executeLoadRewardedAd(JSONObject options, CallbackContext callbackContext) {
        Log.d(TAG, "executeLoadRewardedAd: called");
        this.setOptions( options );
        callbackRewardAd = callbackContext;

        cordova.getActivity().runOnUiThread(new Runnable(){
            @Override
            public void run() {

                if(rewardedAdId==null || rewardedAdId.equals(EMPTY) ){
                    Log.e("rewardedAd", "Please put your smaato rewardedAd id into the javascript code. No ad to display.");
//                    return null;
                } 
                RewardedInterstitial.loadAd(rewardedAdId,eventListenerReward);
//                if(callbackRewardAd!=null)
//                    callbackRewardAd.success();
            }
        });

        return null;
    }

    private PluginResult executeShowRewardedAd(JSONObject options, CallbackContext callbackContext) {
        Log.d(TAG, "executeShowRewardedAd: called");
        callbackRewardAd = callbackContext;

        cordova.getActivity().runOnUiThread(new Runnable(){
            @Override
            public void run() {

                if(rewardAd != null)
                    rewardAd.showAd();
                else{
                    PluginResult result = new PluginResult(PluginResult.Status.ERROR,rewardAdEvent.onAdFailedToLoad.ordinal() );
                    result.setKeepCallback(true);
                    callbackRewardAd.sendPluginResult(result);
                }
            }
        });

        return null;
    }

    BannerView.EventListener bannerAdEventListener = new BannerView.EventListener() {
        @Override
        public void onAdLoaded(@NonNull BannerView bannerView) {
            PluginResult result = new PluginResult(PluginResult.Status.OK,bannerAdEvent.onAdLoaded.ordinal() );
            result.setKeepCallback(true);
            callbackBannerAd.sendPluginResult(result);
        }
        @Override
        public void onAdFailedToLoad(@NonNull BannerView bannerView, @NonNull BannerError bannerError) {
            Log.d(TAG, "Banner load failed"+bannerError.toString());
            bannerView.destroy();
            bannerView2.destroy();
            PluginResult result = new PluginResult(PluginResult.Status.ERROR,bannerAdEvent.onAdFailedToLoad.ordinal() );
            result.setKeepCallback(true);
            callbackBannerAd.sendPluginResult(result);
        }
        @Override
        public void onAdImpression(@NonNull BannerView bannerView) {
            PluginResult result = new PluginResult(PluginResult.Status.ERROR,bannerAdEvent.onAdImpression.ordinal() );
            result.setKeepCallback(true);
            callbackBannerAd.sendPluginResult(result);
        }
        @Override
        public void onAdClicked(@NonNull BannerView bannerView) {
            PluginResult result = new PluginResult(PluginResult.Status.ERROR,bannerAdEvent.onAdClicked.ordinal() );
            result.setKeepCallback(true);
            callbackBannerAd.sendPluginResult(result);
        }
        @Override
        public void onAdTTLExpired(@NonNull BannerView bannerView) {
            PluginResult result = new PluginResult(PluginResult.Status.ERROR,bannerAdEvent.onAdTTLExpired.ordinal() );
            result.setKeepCallback(true);
            callbackBannerAd.sendPluginResult(result);
        }
    };

    com.smaato.sdk.interstitial.EventListener interstitialAdEventListener = new com.smaato.sdk.interstitial.EventListener() {
        @Override
        public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
            interstitialAd.setBackgroundColor(0xff123456);
            interstitialAd.showAd(cordova.getActivity());
            PluginResult result = new PluginResult(PluginResult.Status.OK,interstitialAdEvent.onAdLoaded.ordinal() );
            result.setKeepCallback(true);
            callbackInterstitialAd.sendPluginResult(result);
        }
        @Override
        public void onAdFailedToLoad(@NonNull InterstitialRequestError interstitialRequestError) {
            Log.d(TAG, "[" + interstitialRequestError.getInterstitialError() + "]");
            PluginResult result = new PluginResult(PluginResult.Status.ERROR,interstitialAdEvent.onAdFailedToLoad.ordinal() );
            result.setKeepCallback(true);
            callbackInterstitialAd.sendPluginResult(result);
        }
        @Override
        public void onAdError(@NonNull InterstitialAd interstitialAd, @NonNull InterstitialError interstitialError) {
            Log.d(TAG, "InterstitialError = [" + interstitialError + "]");
            PluginResult result = new PluginResult(PluginResult.Status.ERROR,interstitialAdEvent.onAdError.ordinal() );
            result.setKeepCallback(true);
            callbackInterstitialAd.sendPluginResult(result);
        }
        @Override
        public void onAdOpened(@NonNull InterstitialAd interstitialAd) {
            PluginResult result = new PluginResult(PluginResult.Status.OK,interstitialAdEvent.onAdOpened.ordinal() );
            result.setKeepCallback(true);
            callbackInterstitialAd.sendPluginResult(result);
        }
        @Override
        public void onAdClosed(@NonNull InterstitialAd interstitialAd) {
            PluginResult result = new PluginResult(PluginResult.Status.OK,interstitialAdEvent.onAdClosed.ordinal() );
            result.setKeepCallback(true);
            callbackInterstitialAd.sendPluginResult(result);
        }
        @Override
        public void onAdClicked(@NonNull InterstitialAd interstitialAd) {
            callbackInterstitialAd.error(interstitialAdEvent.onAdClicked.ordinal());
            PluginResult result = new PluginResult(PluginResult.Status.OK,interstitialAdEvent.onAdClicked.ordinal() );
            result.setKeepCallback(true);
            callbackInterstitialAd.sendPluginResult(result);
        }
        @Override
        public void onAdImpression(@NonNull InterstitialAd interstitialAd) {
            PluginResult result = new PluginResult(PluginResult.Status.OK,interstitialAdEvent.onAdImpression.ordinal() );
            result.setKeepCallback(true);
            callbackInterstitialAd.sendPluginResult(result);
        }
        @Override
        public void onAdTTLExpired(@NonNull InterstitialAd interstitialAd) {
            PluginResult result = new PluginResult(PluginResult.Status.ERROR,interstitialAdEvent.onAdTTLExpired.ordinal() );
            result.setKeepCallback(true);
            callbackInterstitialAd.sendPluginResult(result);
        }
    };

    com.smaato.sdk.rewarded.EventListener eventListenerReward = new com.smaato.sdk.rewarded.EventListener() {
        @Override
        public void onAdLoaded(@NonNull RewardedInterstitialAd rewardedInterstitialAd) {
            PluginResult result = new PluginResult(PluginResult.Status.OK,rewardAdEvent.onAdLoaded.ordinal() );
            result.setKeepCallback(true);
            callbackRewardAd.sendPluginResult(result);
            rewardAd = rewardedInterstitialAd;
        }
        @Override
        public void onAdFailedToLoad(@NonNull RewardedRequestError rewardedRequestError) {
            Log.d(TAG,"[ " + rewardedRequestError.getRewardedError() + " ]");
            PluginResult result = new PluginResult(PluginResult.Status.ERROR,rewardAdEvent.onAdFailedToLoad.ordinal() );
            result.setKeepCallback(true);
            callbackRewardAd.sendPluginResult(result);
        }
        @Override
        public void onAdError(@NonNull RewardedInterstitialAd rewardedInterstitialAd, @NonNull RewardedError rewardedError) {
            PluginResult result = new PluginResult(PluginResult.Status.ERROR,rewardAdEvent.onAdError.ordinal() );
            result.setKeepCallback(true);
            callbackRewardAd.sendPluginResult(result);
        }
        @Override
        public void onAdClosed(@NonNull RewardedInterstitialAd rewardedInterstitialAd) {
            PluginResult result = new PluginResult(PluginResult.Status.OK,rewardAdEvent.onAdClosed.ordinal() );
            result.setKeepCallback(true);
            callbackRewardAd.sendPluginResult(result);
        }
        @Override
        public void onAdClicked(@NonNull RewardedInterstitialAd rewardedInterstitialAd) {
            PluginResult result = new PluginResult(PluginResult.Status.OK,rewardAdEvent.onAdClicked.ordinal() );
            result.setKeepCallback(true);
            callbackRewardAd.sendPluginResult(result);
        }
        @Override
        public void onAdStarted(@NonNull RewardedInterstitialAd rewardedInterstitialAd) {
            PluginResult result = new PluginResult(PluginResult.Status.OK,rewardAdEvent.onAdStarted.ordinal() );
            result.setKeepCallback(true);
            callbackRewardAd.sendPluginResult(result);
        }
        @Override
        public void onAdReward(@NonNull RewardedInterstitialAd rewardedInterstitialAd) {
            PluginResult result = new PluginResult(PluginResult.Status.OK,rewardAdEvent.onAdReward.ordinal() );
            result.setKeepCallback(true);
            callbackRewardAd.sendPluginResult(result);
        }
        @Override
        public void onAdTTLExpired(@NonNull RewardedInterstitialAd rewardedInterstitialAd) {
            PluginResult result = new PluginResult(PluginResult.Status.ERROR,rewardAdEvent.onAdTTLExpired.ordinal() );
            result.setKeepCallback(true);
            callbackRewardAd.sendPluginResult(result);
        }
    };

    //Custom Video
    public static String stripFileProtocol(String uriString) {
        if (uriString.startsWith("file://")) {
            return Uri.parse(uriString).getPath();
        }
        return uriString;
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    protected void openVideoDialog(String path, JSONObject options) {
        // Let's create the main dialog
        dialog = new Dialog(cordova.getActivity(), android.R.style.Theme_NoTitleBar);
        dialog.getWindow().getAttributes().windowAnimations = android.R.style.Animation_Dialog;
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setOnDismissListener(this);

        // Main container layout
        LinearLayout main = new LinearLayout(cordova.getActivity());
        main.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        main.setOrientation(LinearLayout.VERTICAL);
        main.setHorizontalGravity(Gravity.CENTER_HORIZONTAL);
        main.setVerticalGravity(Gravity.CENTER_VERTICAL);

        VideoView videoView = new VideoView(cordova.getActivity());
        videoView.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        // videoView.setVideoURI(uri);
        // videoView.setVideoPath(path);
        main.addView(videoView);

        player = new MediaPlayer();
        player.setOnPreparedListener(this);
        player.setOnCompletionListener(this);
        player.setOnErrorListener(this);

        if (path.startsWith(ASSETS)) {
            String f = path.substring(15);
            AssetFileDescriptor fd = null;
            try {
                fd = cordova.getActivity().getAssets().openFd(f);
                player.setDataSource(fd.getFileDescriptor(), fd.getStartOffset(), fd.getLength());
            } catch (Exception e) {
                PluginResult result = new PluginResult(PluginResult.Status.ERROR, e.getLocalizedMessage());
                result.setKeepCallback(false); // release status callback in JS side
                callbackContext.sendPluginResult(result);
                callbackContext = null;
                return;
            }
        }
        else {
            try {
                player.setDataSource(path);
            } catch (Exception e) {
                PluginResult result = new PluginResult(PluginResult.Status.ERROR, e.getLocalizedMessage());
                result.setKeepCallback(false); // release status callback in JS side
                callbackContext.sendPluginResult(result);
                callbackContext = null;
                return;
            }
        }

        try {
            float volume = Float.valueOf(options.getString("volume"));
            Log.d(LOG_TAG, "setVolume: " + volume);
            player.setVolume(volume, volume);
        } catch (Exception e) {
            PluginResult result = new PluginResult(PluginResult.Status.ERROR, e.getLocalizedMessage());
            result.setKeepCallback(false); // release status callback in JS side
            callbackContext.sendPluginResult(result);
            callbackContext = null;
            return;
        }

        if(android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN) {
            try {
                int scalingMode = options.getInt("scalingMode");
                switch (scalingMode) {
                    case MediaPlayer.VIDEO_SCALING_MODE_SCALE_TO_FIT_WITH_CROPPING:
                        Log.d(LOG_TAG, "setVideoScalingMode VIDEO_SCALING_MODE_SCALE_TO_FIT_WITH_CROPPING");
                        player.setVideoScalingMode(MediaPlayer.VIDEO_SCALING_MODE_SCALE_TO_FIT_WITH_CROPPING);
                        break;
                    default:
                        Log.d(LOG_TAG, "setVideoScalingMode VIDEO_SCALING_MODE_SCALE_TO_FIT");
                        player.setVideoScalingMode(MediaPlayer.VIDEO_SCALING_MODE_SCALE_TO_FIT);
                }
            } catch (Exception e) {
                PluginResult result = new PluginResult(PluginResult.Status.ERROR, e.getLocalizedMessage());
                result.setKeepCallback(false); // release status callback in JS side
                callbackContext.sendPluginResult(result);
                callbackContext = null;
                return;
            }
        }

        final SurfaceHolder mHolder = videoView.getHolder();
        mHolder.setKeepScreenOn(true);
        mHolder.addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                player.setDisplay(holder);
                try {
                    player.prepare();
                } catch (Exception e) {
                    PluginResult result = new PluginResult(PluginResult.Status.ERROR, e.getLocalizedMessage());
                    result.setKeepCallback(false); // release status callback in JS side
                    callbackContext.sendPluginResult(result);
                    callbackContext = null;
                }
            }
            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                player.release();
            }
            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {}
        });

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.MATCH_PARENT;

        dialog.setContentView(main);
        dialog.show();
        dialog.getWindow().setAttributes(lp);
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        Log.e(LOG_TAG, "MediaPlayer.onError(" + what + ", " + extra + ")");
        if(mp.isPlaying()) {
            mp.stop();
        }
        mp.release();
        dialog.dismiss();
        return false;
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        mp.start();
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        Log.d(LOG_TAG, "MediaPlayer completed");
        mp.release();
        dialog.dismiss();
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        Log.d(LOG_TAG, "Dialog dismissed");
        if (callbackContext != null) {
            PluginResult result = new PluginResult(PluginResult.Status.OK);
            result.setKeepCallback(false); // release status callback in JS side
            callbackContext.sendPluginResult(result);
            callbackContext = null;
        }
    }
}