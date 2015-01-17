package com.test.vp;

import java.util.ArrayList;
import java.util.List;

import com.test.vpager.R;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Service;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;
import android.widget.LinearLayout;

@SuppressLint("NewApi")
public class MainActivity extends Activity {
	public static ViewPager mPager;
	private ArrayList<View> views;

	public int currIndex;// ��ǰҳ�����
	private int screenW;// ��Ļ���
	LayoutInflater mInflater;
	List<String> mTabs;// ��ǩ�б�
	LinearLayout ll_tabs;// ��ǩ����

	private List<TabView> mTabViews;// ��ǩ��ͼ�б�
	private List<Integer> mHeights;// ÿ��tab���ڵĸ߶�
	private List<Integer> mOldHeights;// ÿ��tabԭ���ĸ߶�

	SlowScrollView hsrcoll;// ˮƽ����
	int width = 0;// Ӧ�ù����ľ���
	int oldposition = 0;// ��һ��ֹͣʱ��λ��
	int oldwidth = 0;// ��һ�εĹ����ľ���
	boolean isRight = true;// �Ƿ������ҹ���
	int speed = 1;// ����ʱ���ٶ�
	float oldx = 0;// ��һ����ָ������λ��

	private Handler handler = new Handler();
	private Runnable runnable = new Runnable() {
		public void run() {
			if (isRight) {// ���ҹ�
				oldwidth = oldwidth + speed;
				if (oldwidth <= width) {
					hsrcoll.smoothScrollTo(oldwidth, 0);
					handler.postDelayed(this, 10);// ����
				} else {
					oldwidth = width;
				}

			} else {// �����
				oldwidth = oldwidth - speed;
				if (oldwidth >= width) {
					hsrcoll.smoothScrollTo(oldwidth, 0);
					handler.postDelayed(this, 10);// ����
				} else {
					oldwidth = width;
				}
			}

		}

	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		init();// ��ʼ��
		InitTabViews();// ��ǩ��ʼ��
		InitViewPager();// viewpager��ʼ��
	}

	private void init() {
		mInflater = LayoutInflater.from(MainActivity.this);
		ll_tabs = (LinearLayout) findViewById(R.id.ll_tabs);
		hsrcoll = (SlowScrollView) findViewById(R.id.rl_bottom);

		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		screenW = dm.widthPixels;

		String[] mStrings = { "�ҵ�", "����", "����", "�ռ�", "����", "����", "�ռ�", "����",
				"����", "�ռ�", "����", "����", "�ռ�", "����", "����", "�ռ�", "����", "����",
				"����", "�ռ�", "����", "����", "�ռ�", "����", "����", "�ռ�", "����", "����",
				"����", "�ռ�", "����", "����", "�ռ�", "����", "����", "�ռ�", "����", "����",
				"�ռ�", "����", "����", "����", "�ռ�", "����", "����", "�ռ�", "����", "����" };
		mTabs = new ArrayList<String>();
		for (String s : mStrings) {
			mTabs.add(s);
		}
	}

	/**
	 * ��ʼ����ǩ��
	 */
	public void InitTabViews() {
		mTabViews = new ArrayList<TabView>();
		mOldHeights = new ArrayList<Integer>();
		mHeights = new ArrayList<Integer>();
		for (int i = 0; i < mTabs.size(); i++) {
			mTabViews.add(setTabView());
			ll_tabs.addView(mTabViews.get(i));
			mOldHeights.add(65);
			mHeights.add(65);
			// �����¼�
			mTabViews.get(i).setOnTouchListener(new MyTouchListener(i));
		}

	}

	/**
	 * ��ǩ�Ĵ����¼�
	 * 
	 * @param index
	 */
	public void Touched(int index) {
		currIndex = index;
		mHeights = new ArrayList<Integer>();
		for (int i = 0; i < mTabViews.size(); i++) {
			if (i == index) {
				Animation translateAnimation = new TranslateAnimation(0, 0,
						mOldHeights.get(i), 0);
				translateAnimation.setFillAfter(true);
				translateAnimation
						.setInterpolator(AnimationUtils
								.loadInterpolator(
										this,
										android.R.anim.accelerate_decelerate_interpolator));
				// ���ö���ʱ��
				translateAnimation.setDuration(150);
				mTabViews.get(i).startAnimation(translateAnimation);
				mHeights.add(0);
			} else if (Math.abs(i - currIndex) < 7) {
				mHeights.add(5 + 9 * Math.abs(i - currIndex));
				Animation translateAnimation = new TranslateAnimation(0, 0,
						mOldHeights.get(i), mHeights.get(i));
				translateAnimation.setFillAfter(true);
				translateAnimation.setInterpolator(AnimationUtils
						.loadInterpolator(this,
								android.R.anim.overshoot_interpolator));
				// ���ö���ʱ��
				translateAnimation.setDuration(150);
				mTabViews.get(i).startAnimation(translateAnimation);
			} else {
				mHeights.add(65);
			}
		}
		mOldHeights = mHeights;
	}

	/**
	 * ����ѡ��tab
	 * 
	 * @param arg0
	 */
	public void Selected(int index) {// ���õ�ǰtab
		currIndex = index;
		mPager.setCurrentItem(index, true);
		for (int i = 0; i < mTabViews.size(); i++) {
			if (i == index) {
				Animation translateAnimation = new TranslateAnimation(0, 0,
						mOldHeights.get(i), 0);
				translateAnimation.setFillAfter(true);
				translateAnimation
						.setInterpolator(AnimationUtils
								.loadInterpolator(
										this,
										android.R.anim.accelerate_decelerate_interpolator));
				// ���ö���ʱ��
				translateAnimation.setDuration(150);
				mTabViews.get(i).startAnimation(translateAnimation);
			} else if (Math.abs(i - currIndex) < 7) {

				Animation translateAnimation = new TranslateAnimation(0, 0,
						mOldHeights.get(i), 65);
				translateAnimation.setFillAfter(true);
				translateAnimation.setInterpolator(AnimationUtils
						.loadInterpolator(this,
								android.R.anim.overshoot_interpolator));
				// ���ö���ʱ��
				translateAnimation.setDuration(150);
				mTabViews.get(i).startAnimation(translateAnimation);
				mHeights.add(65);
			}else{
				mHeights.add(65);
			}
		}

		scrollTo();

	}

	/**
	 * ��ʼ����
	 */
	private void scrollTo() {
		width = (screenW / 7) * (currIndex - 3);
		if (width > oldposition) {
			speed = (width - oldposition) / (screenW / 7) + 1;
			isRight = true;
			oldposition = width;
		} else if (width < oldposition) {
			speed = (oldposition - width) / (screenW / 7) + 1;
			isRight = false;
			oldposition = width;
		}
		handler.post(runnable);
	}

	/**
	 * ����viewpager��item����
	 * 
	 * @return view
	 */
	private View setView() {
		View view = mInflater.inflate(R.layout.viewpager_item, null);
		return view;
	}

	/**
	 * ���ñ�ǩ
	 */
	private TabView setTabView() {
		TabView mTabView = new TabView(this, null);
		LinearLayout.LayoutParams lp;
		lp = new LinearLayout.LayoutParams(screenW / 7, 120);
		mTabView.setLayoutParams(lp);
		return mTabView;
	}

	/**
	 * ��ʼ��ViewPager
	 */
	public void InitViewPager() {
		mPager = (ViewPager) findViewById(R.id.viewpager);
		views = new ArrayList<View>();
		for (int i = 0; i < mTabs.size(); i++) {
			views.add(setView());
		}

		// ��ViewPager����������
		mPager.setAdapter(new MyViewPagerAdapter(views));
		mPager.setCurrentItem(0);// ���õ�ǰ��ʾ��ǩҳΪ��һҳ
		Selected(0);
		mPager.setOnPageChangeListener(new MyOnPageChangeListener());// ҳ��仯ʱ�ļ�����
	}

	/**
	 * ��������
	 * 
	 * @author Administrator
	 * 
	 */
	public class MyOnPageChangeListener implements OnPageChangeListener {

		@Override
		public void onPageScrolled(int position, float positionOffset,
				int positionOffsetPx) {

		}

		@Override
		public void onPageScrollStateChanged(int arg0) {

		}

		@Override
		public void onPageSelected(int arg0) {
			Selected(arg0);

		}
	}

	/**
	 * �����¼�
	 * 
	 * @author Administrator
	 * 
	 */

	public class MyTouchListener implements OnTouchListener {
		private int index = 0;

		public MyTouchListener(int i) {
			index = i;
		}

		@Override
		public boolean onTouch(View v, MotionEvent event) {
			switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				oldx = event.getX();
				if (oldx % (screenW / 7) != (screenW / 7) / 2) {// ����λ��ƫ�Ƶ���
					oldx = oldx - oldx % (screenW / 7) + (screenW / 7) / 2;
				}
				currIndex = index;
				Touched(currIndex);
				return true;

			case MotionEvent.ACTION_MOVE:
				float newx = event.getX();
				currIndex = index + (int) (newx - oldx) / (screenW / 7);
				Touched(currIndex);
				return true;
			case MotionEvent.ACTION_CANCEL:
			case MotionEvent.ACTION_UP:
				Selected(currIndex);

				Vibrator mVibrator01 = (Vibrator) getSystemService(Service.VIBRATOR_SERVICE);
				mVibrator01.vibrate(new long[] { 10, 10 }, -1);
				return true;
			default:
				return false;
			}

		}
	}
}