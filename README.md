[dzijkplayer]

1.简单的播放器实现

rootView = getLayoutInflater().from(this).inflate(R.layout.simple_player_view_player, null);
setContentView(rootView);
String url = "http://9890.vod.myqcloud.com/9890_9c1fa3e2aea011e59fc841df10c92278.f20.mp4";
player = new PlayerView(this,rootView)
        .setTitle("什么")
        .setScaleType(PlayStateParams.fitparent)
        .hideMenu(true)
        .forbidTouch(false)
        .showThumbnail(new OnShowThumbnailListener() {
            @Override
            public void onShowThumbnail(ImageView ivThumbnail) {
                Glide.with(mContext)
                        .load("http://pic2.nipic.com/20090413/406638_125424003_2.jpg")
                        .placeholder(R.color.cl_default)
                        .error(R.color.cl_error)
                        .into(ivThumbnail);
            }
        })
        .setPlaySource(url)
        .startPlay();
		
		
		

2.多种不同的分辨率流的播放器实现

在布局中使用simple_player_view_player.xml布局

<include
    layout="@layout/simple_player_view_player"
    android:layout_width="match_parent"
    android:layout_height="180dp"/>
代码中创建一个播放器对象

/**播放资源*/
ist<VideoijkBean> list = new ArrayList<VideoijkBean>();
String url1 = "http://9890.vod.myqcloud.com/9890_4e292f9a3dd011e6b4078980237cc3d3.f20.mp4";
String url2 = "http://9890.vod.myqcloud.com/9890_4e292f9a3dd011e6b4078980237cc3d3.f30.mp4";
VideoijkBean m1 = new VideoijkBean();
m1.setStream("标清");
m1.setUrl(url1);
VideoijkBean m2 = new VideoijkBean();
m2.setStream("高清");
m2.setUrl(url2);
list.add(m1);
list.add(m2);
/**播放器*/
rootView = getLayoutInflater().from(this).inflate(你的布局, null);
setContentView(rootView);
player = new PlayerView(this,rootView)
            .setTitle("什么")
            .setScaleType(PlayStateParams.fitparent)
            .hideMenu(true)
            .forbidTouch(false)
            .showThumbnail(new OnShowThumbnailListener() {
                @Override
                public void onShowThumbnail(ImageView ivThumbnail) {
					/**加载前显示的缩略图*/
                    Glide.with(mContext)
                            .load("http://pic2.nipic.com/20090413/406638_125424003_2.jpg")
                            .placeholder(R.color.cl_default)
                            .error(R.color.cl_error)
                            .into(ivThumbnail);
                }
            })
            .setPlaySource(list)
            .startPlay();
step3:

配置生命周期方法,为了让播放器同步Activity生命周期，建议以下方法都去配置，注释的代码，主要作用是播放时屏幕常亮和暂停其它媒体的播放。

 @Override
protected void onPause() {
    super.onPause();
    if (player != null) {
        player.onPause();
    }
    /**demo的内容，恢复系统其它媒体的状态*/
    //MediaUtils.muteAudioFocus(mContext, true);
}

@Override
protected void onResume() {
    super.onResume();
    if (player != null) {
        player.onResume();
    }
    /**demo的内容，暂停系统其它媒体的状态*/
    MediaUtils.muteAudioFocus(mContext, false);
    /**demo的内容，激活设备常亮状态*/
    //if (wakeLock != null) {
    //    wakeLock.acquire();
    //}
}

@Override
protected void onDestroy() {
    super.onDestroy();
    if (player != null) {
        player.onDestroy();
    }
}

@Override
public void onConfigurationChanged(Configuration newConfig) {
    super.onConfigurationChanged(newConfig);
    if (player != null) {
        player.onConfigurationChanged(newConfig);
    }
}

@Override
public void onBackPressed() {
    if (player != null && player.onBackPressed()) {
        return;
    }
    super.onBackPressed();
    /**demo的内容，恢复设备亮度状态*/
    //if (wakeLock != null) {
    //    wakeLock.release();
    //}
}
More Actions

1.视频界面裁剪设置，可通过方法setScaleType(int type)去设置

1. PlayStateParams.fitParent:可能会剪裁,保持原视频的大小，显示在中心,当原视频的大小超过view的大小超过部分裁剪处理
2. PlayStateParams.fillParent:可能会剪裁,等比例放大视频，直到填满View为止,超过View的部分作裁剪处理
3. PlayStateParams.wrapcontent:将视频的内容完整居中显示，如果视频大于view,则按比例缩视频直到完全显示在view中
4. PlayStateParams.fitXY:不剪裁,非等比例拉伸画面填满整个View
5. PlayStateParams.f16_9:不剪裁,非等比例拉伸画面到16:9,并完全显示在View中
6. PlayStateParams.f4_3:不剪裁,非等比例拉伸画面到4:3,并完全显示在View中
2.自定义视频界面，可以复制以下布局内容到自己的项目中，注意已有的id不能修改或删除，可以增加view，可以对以下布局内容调整显示位置或者自行隐藏

<?xml version="1.0" encoding="utf-8"?>
<RelativeLayout
    android:id="@+id/app_video_box"
    xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:background="@android:color/black"
    android:orientation="vertical">


    <com.dou361.ijkplayer.widget.IjkVideoView
        android:id="@+id/video_view"
        android:layout_width="match_parent"
        android:layout_height="match_parent"/>

    <LinearLayout
        android:id="@+id/ll_bg"
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        android:background="@android:color/black"
        android:orientation="vertical">

        <!-- 封面显示-->
        <ImageView
            android:id="@+id/iv_trumb"
            android:layout_width="match_parent"
            android:layout_height="match_parent"
            android:scaleType="fitXY"
            android:visibility="visible"/>
    </LinearLayout>

    <!--重新播放-->
    <LinearLayout
        android:id="@+id/app_video_replay"
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        android:background="#33000000"
        android:gravity="center"
        android:orientation="vertical"
        android:visibility="gone">
        <!-- 播放状态-->
        <TextView
            android:id="@+id/app_video_status_text"
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:text="@string/small_problem"
            android:textColor="@android:color/white"
            android:textSize="14dp"/>

        <ImageView
            android:id="@+id/app_video_replay_icon"
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:layout_centerHorizontal="true"
            android:layout_marginTop="8dp"
            android:src="@drawable/simple_player_circle_outline_white_36dp"/>
    </LinearLayout>
    <!-- 网络提示-->
    <LinearLayout
        android:id="@+id/app_video_netTie"
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        android:background="#33000000"
        android:gravity="center"
        android:orientation="vertical"
        android:visibility="gone">

        <TextView
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:layout_marginBottom="8dp"
            android:gravity="center"
            android:paddingLeft="8dp"
            android:paddingRight="8dp"
            android:text="您正在使用移动网络播放视频\n可能产生较高流量费用"
            android:textColor="@android:color/white"/>

        <TextView
            android:id="@+id/app_video_netTie_icon"
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:background="@drawable/simple_player_btn"
            android:gravity="center"
            android:paddingLeft="8dp"
            android:paddingRight="8dp"
            android:text="继续"
            android:textColor="@android:color/white"/>
    </LinearLayout>

    <!--加载中-->
    <LinearLayout
        android:id="@+id/app_video_loading"
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        android:gravity="center"
        android:orientation="vertical"
        android:visibility="gone">

        <ProgressBar
            android:layout_width="50dp"
            android:layout_height="50dp"
            android:indeterminateBehavior="repeat"
            android:indeterminateOnly="true"/>
        <TextView
            android:id="@+id/app_video_speed"
            android:layout_width="wrap_content"
            android:layout_marginTop="4dp"
            android:layout_height="wrap_content"
            android:gravity="center"
            android:visibility="gone"
            android:text="188Kb/s"
            android:textColor="@android:color/white"/>
    </LinearLayout>

    <!-- 中间触摸提示-->
    <include
        layout="@layout/simple_player_touch_gestures"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:layout_centerInParent="true"/>

    <!-- 顶部栏-->
    <include layout="@layout/simple_player_topbar"/>
    <!-- 底部栏-->
    <include
        layout="@layout/simple_player_controlbar"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:layout_alignParentBottom="true"/>

    <!--声音亮度控制-->
    <LinearLayout
        android:id="@+id/simple_player_settings_container"
        android:layout_width="250dp"
        android:layout_height="match_parent"
        android:layout_alignParentLeft="true"
        android:background="#80000000"
        android:gravity="center_vertical"
        android:orientation="vertical"
        android:visibility="visible">

        <LinearLayout
            android:id="@+id/simple_player_volume_controller_container"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:gravity="center"
            android:orientation="horizontal">

            <ImageView
                android:layout_width="30dp"
                android:layout_height="30dp"
                android:src="@drawable/qcloud_player_icon_audio_vol_mute"/>

            <SeekBar
                android:id="@+id/simple_player_volume_controller"
                style="?android:attr/progressBarStyleHorizontal"
                android:layout_width="150dp"
                android:layout_height="wrap_content"/>

            <ImageView
                android:layout_width="30dp"
                android:layout_height="30dp"
                android:src="@drawable/qcloud_player_icon_audio_vol"/>
        </LinearLayout>

        <LinearLayout
            android:id="@+id/simple_player_brightness_controller_container"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:layout_marginTop="20dp"
            android:gravity="center"
            android:orientation="horizontal">

            <ImageView
                android:layout_width="30dp"
                android:layout_height="30dp"
                android:padding="5dp"
                android:src="@drawable/qcloud_player_icon_brightness"/>

            <SeekBar
                android:id="@+id/simple_player_brightness_controller"
                style="?android:attr/progressBarStyleHorizontal"
                android:layout_width="150dp"
                android:layout_height="wrap_content"/>

            <ImageView
                android:layout_width="30dp"
                android:layout_height="30dp"
                android:src="@drawable/qcloud_player_icon_brightness"/>
        </LinearLayout>

    </LinearLayout>


    <!--分辨率选择-->
    <LinearLayout
        android:id="@+id/simple_player_select_stream_container"
        android:layout_width="150dp"
        android:layout_height="match_parent"
        android:layout_alignParentRight="true"
        android:background="#80000000"
        android:gravity="center_vertical"
        android:visibility="gone">

        <ListView
            android:id="@+id/simple_player_select_streams_list"
            android:layout_width="150dp"
            android:layout_height="wrap_content"/>
    </LinearLayout>


    <ImageView
        android:id="@+id/play_icon"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:layout_centerInParent="true"
        android:layout_marginTop="8dp"
        android:src="@drawable/simple_player_center_play"/>

</RelativeLayout>
3.播放器PlayerView对象的方法如下：

PlayerView(Activity activity)

//生命周期方法回调
PlayerView onPause()
PlayerView onResume()
PlayerView onDestroy()
PlayerView onConfigurationChanged(final Configuration newConfig)
boolean onBackPressed()
//显示缩略图
PlayerView showThumbnail(OnShowThumbnailListener onShowThumbnailListener)
//设置播放信息监听回调
PlayerView setOnInfoListener(IMediaPlayer.OnInfoListener onInfoListener)
//设置播放器中的返回键监听
PlayerView setPlayerBackListener(OnPlayerBackListener listener)
//设置控制面板显示隐藏监听
PlayerView setOnControlPanelVisibilityChangListenter(OnControlPanelVisibilityChangeListener listener)
//百分比显示切换
PlayerView toggleAspectRatio()
//设置播放区域拉伸类型
PlayerView setScaleType(int showType)
//旋转角度
PlayerView setPlayerRotation()
//旋转指定角度
PlayerView setPlayerRotation(int rotation)
//设置播放地址包括视频清晰度列表对应地址列表
PlayerView setPlaySource(List<VideoijkBean> list)
//设置播放地址单个视频VideoijkBean
PlayerView setPlaySource(VideoijkBean videoijkBean)
//设置播放地址单个视频地址时带流名称
PlayerView setPlaySource(String stream, String url)
//设置播放地址单个视频地址时
PlayerView setPlaySource(String url)
//自动播放
PlayerView autoPlay(String path)
//开始播放
PlayerView startPlay()
//设置视频名称
PlayerView setTitle(String title)
//选择要播放的流
PlayerView switchStream(int index)
//暂停播放
PlayerView pausePlay()
//停止播放
PlayerView stopPlay()
//设置播放位置
PlayerView seekTo(int playtime)
//获取当前播放位置
int getCurrentPosition()
//获取视频播放总时长
long getDuration()
//设置2/3/4/5G和WiFi网络类型提示 true为进行2/3/4/5G网络类型提示 false 不进行网络类型提示
PlayerView setNetWorkTypeTie(boolean isGNetWork)
//是否仅仅为全屏
PlayerView setOnlyFullScreen(boolean isFull)
//设置是否禁止双击
PlayerView setForbidDoulbeUp(boolean flag)
//当前播放的是否是直播
boolean isLive()
//是否禁止触摸
PlayerView forbidTouch(boolean forbidTouch)
//隐藏所有状态界面
PlayerView hideAllUI()
获取顶部控制barview
View getTopBarView()
//获取底部控制barview
View getBottonBarView()
//获取旋转view
ImageView getRationView()
//获取返回view
ImageView getBackView()
//获取菜单view
ImageView getMenuView()
//获取全屏按钮view
ImageView getFullScreenView()
//获取底部bar的播放view
ImageView getBarPlayerView()
//获取中间的播放view
ImageView getPlayerView()
//隐藏返回键，true隐藏，false为显示
PlayerView hideBack(boolean isHide)
//隐藏菜单键，true隐藏，false为显示
PlayerView hideMenu(boolean isHide)
//隐藏分辨率按钮，true隐藏，false为显示
PlayerView hideSteam(boolean isHide)
//隐藏旋转按钮，true隐藏，false为显示
PlayerView hideRotation(boolean isHide)
//隐藏全屏按钮，true隐藏，false为显示
PlayerView hideFullscreen(boolean isHide)
//隐藏中间播放按钮,ture为隐藏，false为不做隐藏处理，但不是显示
PlayerView hideCenterPlayer(boolean isHide)
//显示或隐藏操作面板
PlayerView operatorPanl()
//全屏切换
PlayerView toggleFullScreen()
//设置自动重连的模式或者重连时间，isAuto true 出错重连，false出错不重连，connectTime重连的时间
setAutoReConnect(boolean isAuto, int connectTime)
//进度条和时长显示的方向切换
PlayerView toggleProcessDurationOrientation()
//设置进度条和时长显示的方向，默认为上下显示，PlayStateParams.PROCESS_PORTRAIT为上下显示PlayStateParams.PROCESS_LANDSCAPE为左右显示PlayStateParams.PROCESS_CENTER为中间两边样式
setProcessDurationOrientation(int portrait)
//显示菜单设置
showMenu()
//获取界面方向
int getScreenOrientation()
//显示加载网速
PlayerView setShowSpeed(boolean isShow)
//是否隐藏topbar，true为隐藏，false为不隐藏，但不一定是显示
PlayerView hideHideTopBar(boolean isHide)
//是否隐藏bottonbar，true为隐藏，false为不隐藏，但不一定是显示
PlayerView hideBottonBar(boolean isHide)
//是否隐藏上下bar，true为隐藏，false为不隐藏，但不一定是显示
PlayerView hideControlPanl(boolean isHide)
//设置是否禁止隐藏bar,优先级低于hideControlPanl
PlayerView setForbidHideControlPanl(boolean flag)
4.全屏隐藏虚拟按键方法

参考HPlayerActivity类，获取Activity的根目录

main = getLayoutInflater().from(this).inflate(R.layout.activity_h, null);
然在在oncreate()方法中设置监听

	/**虚拟按键的隐藏方法*/
    main.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {

        @Override
        public void onGlobalLayout() {

            //比较Activity根布局与当前布局的大小
            int heightDiff = main.getRootView().getHeight() - main.getHeight();
            if (heightDiff > 100) {
                //大小超过100时，一般为显示虚拟键盘事件
                main.setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
            } else {
                //大小小于100时，为不显示虚拟键盘或虚拟键盘隐藏
                main.setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);

            }
        }
    });
5.半屏视频，横竖屏切换时不填满问题

1.确保Activity中调用生命周期方法

@Override
public void onConfigurationChanged(Configuration newConfig) {
    super.onConfigurationChanged(newConfig);
    if (player != null) {
        player.onConfigurationChanged(newConfig);
    }
}
2.确保清单文件中配置属性

android:configChanges="orientation|keyboardHidden|screenSize"
        android:screenOrientation="portrait"
例如

<activity
        android:name="com.dou361.jjdxm_ijkplayer.HPlayerActivity"
        android:configChanges="orientation|keyboardHidden|screenSize"
        android:screenOrientation="portrait"
        android:theme="@style/AppTheme"/>
关于定制

隐藏部分不想要的界面

//隐藏返回键，true隐藏，false为显示
PlayerView hideBack(boolean isHide)
//隐藏菜单键，true隐藏，false为显示
PlayerView hideMenu(boolean isHide)
//隐藏分辨率按钮，true隐藏，false为显示
PlayerView hideSteam(boolean isHide)
//隐藏旋转按钮，true隐藏，false为显示
PlayerView hideRotation(boolean isHide)
//隐藏全屏按钮，true隐藏，false为显示
PlayerView hideFullscreen(boolean isHide)
//隐藏中间播放按钮,ture为隐藏，false为不做隐藏处理，但不是显示
PlayerView hideCenterPlayer(boolean isHide)
加载时显示网速

默认加载时不显示网速，可以通过setShowSpeed(boolean isShow)设置加载时是否需要显示，true为显示，false为不显示

播放器底部bar播放进度条样式定制

默认的进度样式是竖屏为上下样式，即进度条在播放时长的上面，横屏为左右样式，即进度条在播放时长的中间。样式定制主要是两个方法搭配使用toggleProcessDurationOrientation方法和setProcessDurationOrientation方法，横竖屏切换2中情况，和3种进度条样式

/**上下样式*/
PlayStateParams.PROCESS_PORTRAIT
/**左右样式*/
PlayStateParams.PROCESS_LANDSCAPE
/**中间两边样式*/
PlayStateParams.PROCESS_CENTER
总共有2的3次方中样式，下面只罗列几种样式

1.横竖屏都为上下样式

rootView = getLayoutInflater().from(this).inflate(你的布局, null);
setContentView(rootView);
player = new PlayerView(this,rootView) {
        @Override
        public PlayerView toggleProcessDurationOrientation() {
            return setProcessDurationOrientation(PlayStateParams.PROCESS_PORTRAIT);
        }
    }
            .setTitle("什么")
            .setProcessDurationOrientation(PlayStateParams.PROCESS_PORTRAIT)
            .setScaleType(PlayStateParams.fitparent)
            .forbidTouch(false)
            .hideCenterPlayer(true)
            .setPlaySource(list)
            .startPlay();
2.横竖屏都为左右样式

rootView = getLayoutInflater().from(this).inflate(你的布局, null);
setContentView(rootView);
player = new PlayerView(this,rootView) {
        @Override
        public PlayerView toggleProcessDurationOrientation() {
            return setProcessDurationOrientation(PlayStateParams.PROCESS_LANDSCAPE);
        }
    }
            .setTitle("什么")
            .setProcessDurationOrientation(PlayStateParams.PROCESS_LANDSCAPE)
            .setScaleType(PlayStateParams.fitparent)
            .forbidTouch(false)
            .hideCenterPlayer(true)
            .setPlaySource(list)
            .startPlay();
3.横屏为上下样式竖屏为左右样式

rootView = getLayoutInflater().from(this).inflate(你的布局, null);
setContentView(rootView);
player = new PlayerView(this,rootView) {
        @Override
        public PlayerView toggleProcessDurationOrientation() {
            return setProcessDurationOrientation(getScreenOrientation() == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE?PlayStateParams.PROCESS_LANDSCAPE:PlayStateParams.PROCESS_PORTRAIT);
        }
    }
            .setTitle("什么")
            .setProcessDurationOrientation(PlayStateParams.PROCESS_LANDSCAPE)
            .setScaleType(PlayStateParams.fitparent)
            .forbidTouch(false)
            .hideCenterPlayer(true)
            .setPlaySource(list)
            .startPlay();
4.横屏为左右样式竖屏为上下样式

rootView = getLayoutInflater().from(this).inflate(你的布局, null);
setContentView(rootView);
player = new PlayerView(this,rootView) {
        @Override
        public PlayerView toggleProcessDurationOrientation() {
            return setProcessDurationOrientation(getScreenOrientation() == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT?PlayStateParams.PROCESS_PORTRAIT:PlayStateParams.PROCESS_LANDSCAPE);
        }
    }
            .setTitle("什么")
            .setProcessDurationOrientation(PlayStateParams.PROCESS_CENTER)
            .setScaleType(PlayStateParams.fitparent)
            .forbidTouch(false)
            .hideCenterPlayer(true)
            .setPlaySource(list)
            .startPlay();
5.横屏为左右样式竖屏为中间两边样式

rootView = getLayoutInflater().from(this).inflate(你的布局, null);
setContentView(rootView);
player = new PlayerView(this,rootView) {
        @Override
        public PlayerView toggleProcessDurationOrientation() {
            return setProcessDurationOrientation(getScreenOrientation() == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT?PlayStateParams.PROCESS_CENTER:PlayStateParams.PROCESS_LANDSCAPE);
        }
    }
            .setTitle("什么")
            .setProcessDurationOrientation(PlayStateParams.PROCESS_CENTER)
            .setScaleType(PlayStateParams.fitparent)
            .forbidTouch(false)
            .hideCenterPlayer(true)
            .setPlaySource(list)
            .startPlay();
3.ijkplayer封装的视频播放信息返回码监听，可以通过setOnInfoListener去监听

/*
 * Do not change these values without updating their counterparts in native
 */
int MEDIA_INFO_UNKNOWN = 1;//未知信息
int MEDIA_INFO_STARTED_AS_NEXT = 2;//播放下一条
int MEDIA_INFO_VIDEO_RENDERING_START = 3;//视频开始整备中
int MEDIA_INFO_VIDEO_TRACK_LAGGING = 700;//视频日志跟踪
int MEDIA_INFO_BUFFERING_START = 701;//开始缓冲中
int MEDIA_INFO_BUFFERING_END = 702;//缓冲结束
int MEDIA_INFO_NETWORK_BANDWIDTH = 703;//网络带宽，网速方面
int MEDIA_INFO_BAD_INTERLEAVING = 800;//
int MEDIA_INFO_NOT_SEEKABLE = 801;//不可设置播放位置，直播方面
int MEDIA_INFO_METADATA_UPDATE = 802;//
int MEDIA_INFO_TIMED_TEXT_ERROR = 900;
int MEDIA_INFO_UNSUPPORTED_SUBTITLE = 901;//不支持字幕
int MEDIA_INFO_SUBTITLE_TIMED_OUT = 902;//字幕超时

int MEDIA_INFO_VIDEO_INTERRUPT= -10000;//数据连接中断
int MEDIA_INFO_VIDEO_ROTATION_CHANGED = 10001;//视频方向改变
int MEDIA_INFO_AUDIO_RENDERING_START = 10002;//音频开始整备中

int MEDIA_ERROR_UNKNOWN = 1;//未知错误
int MEDIA_ERROR_SERVER_DIED = 100;//服务挂掉
int MEDIA_ERROR_NOT_VALID_FOR_PROGRESSIVE_PLAYBACK = 200;//数据错误没有有效的回收
int MEDIA_ERROR_IO = -1004;//IO错误
int MEDIA_ERROR_MALFORMED = -1007;
int MEDIA_ERROR_UNSUPPORTED = -1010;//数据不支持
int MEDIA_ERROR_TIMED_OUT = -110;//数据超时

