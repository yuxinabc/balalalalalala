package com.my51c.see51.widget;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.my51see.see51.R;

import java.util.Timer;
import java.util.TimerTask;

/**
 * 
 * �Զ���DeviceListView���̳���ListView�� �������listview��ͷ����������ˢ����ʽ����ʵ���书��
 * 
 * @author huangtiebing
 * 
 */

public class DeviceListView extends ListView implements OnScrollListener
{
	private final static int RELEASE_To_REFRESH = 0;
	private final static int PULL_To_REFRESH = 1;
	private final static int REFRESHING = 2;
	private final static int DONE = 3;

	private LayoutInflater inflater;

	private Timer timer;
	private TimerTask timerTask;
	
	private LinearLayout headView; // ͷ��
	private TextView tipsTextview;// ����ˢ��
	private ImageView arrowImageView;// ��ͷ
	private ProgressBar progressBar;// ˢ�½�����

	private RotateAnimation animation;// ��ת��Ч ˢ���м�ͷ��ת ���±�����
	private RotateAnimation reverseAnimation;

	// ���ڱ�֤startY��ֵ��һ��������touch�¼���ֻ����¼һ��
	private boolean isRecored;

	private int headContentWidth;// ͷ�����
	private int headContentHeight;// ͷ���߶�

	private int startY;// �߶���ʼλ�ã�������¼��ͷ������
	private int firstItemIndex;// �б�������������������¼����ͷ������

	private int state = DONE;// ����ˢ���С��ɿ�ˢ���С�����ˢ���С����ˢ��

	private boolean isBack;

	public OnRefreshListener refreshListener;// ˢ�¼���

	private final static String TAG = "abc";
	
	private Handler myHandler = new Handler()
	{
		@Override
		public void handleMessage(Message msg)
		{
			switch (msg.what)
			{
			default :
				state = DONE;
				changeHeaderViewByState();
				break;
			}
		}
	};

	public DeviceListView(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		init(context);
	}

	private void init(Context context)
	{
		inflater = LayoutInflater.from(context);

		headView = (LinearLayout) inflater.inflate(R.layout.device_list_head, null);// listviewƴ��headview

		arrowImageView = (ImageView) headView.findViewById(R.id.head_arrowImageView);// headview�и�view
		arrowImageView.setMinimumWidth(50);
		arrowImageView.setMinimumHeight(50);
		progressBar = (ProgressBar) headView.findViewById(R.id.head_progressBar);// headview�и�view
		tipsTextview = (TextView) headView.findViewById(R.id.head_tipsTextView);// headview�и�view

		measureView(headView);
		headContentHeight = headView.getMeasuredHeight();// ͷ���߶�
		headContentWidth = headView.getMeasuredWidth();// ͷ�����

		headView.setPadding(0, -1 * headContentHeight, 0, 0);// setPadding(int
																// left, int
																// top, int
																// right, int
																// bottom)
		headView.invalidate();// Invalidate the whole view

		Log.v("size", "width:" + headContentWidth + " height:" + headContentHeight);

		addHeaderView(headView);// ��ӽ�headview
		setOnScrollListener(this);// ��������

		animation = new RotateAnimation(0, -180, RotateAnimation.RELATIVE_TO_SELF, 0.5f, RotateAnimation.RELATIVE_TO_SELF, 0.5f);
		animation.setInterpolator(new LinearInterpolator());
		animation.setDuration(250);
		animation.setFillAfter(true);// ��Чanimation����

		reverseAnimation = new RotateAnimation(-180, 0, RotateAnimation.RELATIVE_TO_SELF, 0.5f, RotateAnimation.RELATIVE_TO_SELF, 0.5f);
		reverseAnimation.setInterpolator(new LinearInterpolator());
		reverseAnimation.setDuration(250);
		reverseAnimation.setFillAfter(true);// ��ЧreverseAnimation����
	}

	public void onScroll(AbsListView arg0, int firstVisiableItem, int arg2,// �����¼�
			int arg3)
	{
		firstItemIndex = firstVisiableItem;// �õ���item����
	}

	public void onScrollStateChanged(AbsListView arg0, int arg1)
	{
	}

	public boolean onTouchEvent(MotionEvent event)
	{// �����¼�
		switch (event.getAction())
		{
		case MotionEvent.ACTION_DOWN:// �ְ��� ��Ӧ����ˢ��״̬
			if (firstItemIndex == 0 && !isRecored)
			{// �����item����Ϊ0������δ��¼startY,��������ʱ��¼֮����ִ��isRecored = true;
				startY = (int) event.getY();
				isRecored = true;

				Log.v(TAG, "��downʱ���¼��ǰλ�á�");
			}
			break;

		case MotionEvent.ACTION_UP:// ���ɿ� ��Ӧ�ɿ�ˢ��״̬
			if (state != REFRESHING)
			{// ���ɿ���4��״̬������ˢ�¡��ɿ�ˢ�¡�����ˢ�¡����ˢ�¡������ǰ��������ˢ��
				if (state == DONE)
				{// �����ǰ�����ˢ�£�ʲô������
				}
				if (state == PULL_To_REFRESH)
				{// �����ǰ������ˢ�£�״̬��Ϊ���ˢ�£��⼴����ˢ���о��ɿ��ˣ�ʵ��δ���ˢ�£���ִ��changeHeaderViewByState()
					state = DONE;
					changeHeaderViewByState();

					Log.v(TAG, "������ˢ��״̬����done״̬");
				}
				if (state == RELEASE_To_REFRESH)
				{// �����ǰ���ɿ�ˢ�£�״̬��Ϊ����ˢ�£��⼴�ɿ�ˢ�����ɿ��֣�����������ˢ�£���ִ��changeHeaderViewByState()
					state = REFRESHING;
					changeHeaderViewByState();
					onRefresh();// ����ˢ�£�����ִ��onrefresh��ִ�к�״̬��Ϊ���ˢ��

					Log.v(TAG, "���ɿ�ˢ��״̬����done״̬");
				}
			}

			isRecored = false;// ���ɿ����������������������¼�¼startY,��ΪֻҪ���ɿ�����Ϊһ��ˢ�������
			isBack = false;

			break;

		case MotionEvent.ACTION_MOVE:// ���϶����϶������в��ϵ�ʵʱ��¼��ǰλ��
			int tempY = (int) event.getY();
			if (!isRecored && firstItemIndex == 0)
			{// �����item����Ϊ0������δ��¼startY,�����϶�ʱ��¼֮����ִ��isRecored = true;
				Log.v(TAG, "��moveʱ���¼��λ��");
				isRecored = true;
				startY = tempY;
			}
			if (state != REFRESHING && isRecored)
			{// ���״̬��������ˢ�£����Ѽ�¼startY��tempYΪ�϶�������һֱ�ڱ�ĸ߶ȣ�startYΪ�϶���ʼ�߶�
				// ��������ȥˢ����
				if (state == RELEASE_To_REFRESH)
				{// ���״̬���ɿ�ˢ��
					// �������ˣ��Ƶ�����Ļ�㹻�ڸ�head�ĳ̶ȣ����ǻ�û���Ƶ�ȫ���ڸǵĵز�
					if ((tempY - startY < headContentHeight)// ���ʵʱ�߶ȴ�����ʼ�߶ȣ�������֮��С��ͷ���߶ȣ���״̬��Ϊ����ˢ��
							&& (tempY - startY) > 0)
					{
						state = PULL_To_REFRESH;
						changeHeaderViewByState();

						Log.v(TAG, "���ɿ�ˢ��״̬ת�䵽����ˢ��״̬");
					}
					// һ�����Ƶ�����
					else if (tempY - startY <= 0)
					{// ���ʵʱ�߶�С�ڵ�����ʼ�߶��ˣ���˵�������ˣ�״̬��Ϊ���ˢ��
						state = DONE;
						changeHeaderViewByState();

						Log.v(TAG, "���ɿ�ˢ��״̬ת�䵽done״̬");
					}
					// �������ˣ����߻�û�����Ƶ���Ļ�����ڸ�head�ĵز�
					else
					{// �����ǰ�϶������м�û�е�����ˢ�µĵز���Ҳû�е����ˢ�£��������ĵز����򱣳��ɿ�ˢ��״̬
						// ���ý����ر�Ĳ�����ֻ�ø���paddingTop��ֵ������
					}
				}
				// ��û�е�����ʾ�ɿ�ˢ�µ�ʱ��,DONE������PULL_To_REFRESH״̬
				if (state == PULL_To_REFRESH)
				{// ���״̬������ˢ��
					// ���������Խ���RELEASE_TO_REFRESH��״̬
					if (tempY - startY >= headContentHeight)
					{// ���ʵʱ�߶�����ʼ�߶�֮����ڵ���ͷ���߶ȣ���״̬��Ϊ�ɿ�ˢ��
						state = RELEASE_To_REFRESH;
						isBack = true;
						changeHeaderViewByState();

						Log.v(TAG, "��done��������ˢ��״̬ת�䵽�ɿ�ˢ��");
					}
					// ���Ƶ�����
					else if (tempY - startY <= 0)
					{// ���ʵʱ�߶�С�ڵ�����ʼ�߶��ˣ���˵�������ˣ�״̬��Ϊ���ˢ��
						state = DONE;
						changeHeaderViewByState();

						Log.v(TAG, "��DOne��������ˢ��״̬ת�䵽done״̬");
					}
				}

				// done״̬��
				if (state == DONE)
				{// ���״̬�����ˢ��
					if (tempY - startY > 0)
					{// ���ʵʱ�߶ȴ�����ʼ�߶��ˣ���״̬��Ϊ����ˢ��
						state = PULL_To_REFRESH;
						changeHeaderViewByState();
					}
				}

				// ����headView��size
				if (state == PULL_To_REFRESH)
				{// ���״̬������ˢ�£�����headview��size ?
					headView.setPadding(0, -1 * headContentHeight + (tempY - startY), 0, 0);
					headView.invalidate();
				}

				// ����headView��paddingTop
				if (state == RELEASE_To_REFRESH)
				{// ���״̬���ɿ�ˢ�£����� headview��paddingtop ?
					headView.setPadding(0, tempY - startY - headContentHeight, 0, 0);
					headView.invalidate();
				}
			}
			break;
		}
		return super.onTouchEvent(event);
	}
	
	
	// ��״̬�ı�ʱ�򣬵��ø÷������Ը��½���
	private void changeHeaderViewByState()
	{
		switch (state)
		{
		case RELEASE_To_REFRESH:
			arrowImageView.setVisibility(View.VISIBLE);
			progressBar.setVisibility(View.GONE);
			tipsTextview.setVisibility(View.VISIBLE);

			arrowImageView.clearAnimation();
			arrowImageView.startAnimation(animation);

			tipsTextview.setText(R.string.releationtorefresh);

			Log.v(TAG, "��ǰ״̬���ɿ�ˢ��");
			break;
		case PULL_To_REFRESH:
			progressBar.setVisibility(View.GONE);
			tipsTextview.setVisibility(View.VISIBLE);
			arrowImageView.clearAnimation();
			arrowImageView.setVisibility(View.VISIBLE);
			// ����RELEASE_To_REFRESH״̬ת������
			if (isBack)
			{
				isBack = false;
				arrowImageView.clearAnimation();
				arrowImageView.startAnimation(reverseAnimation);

				tipsTextview.setText(R.string.pulltorefresh);
			} else
			{
				tipsTextview.setText(R.string.pulltorefresh);
			}
			Log.v(TAG, "��ǰ״̬������ˢ��");
			break;

		case REFRESHING:

			headView.setPadding(0, 0, 0, 0);
			headView.invalidate();

			progressBar.setVisibility(View.VISIBLE);
			arrowImageView.clearAnimation();
			arrowImageView.setVisibility(View.GONE);
			tipsTextview.setText(R.string.refreshing);

			Log.v(TAG, "��ǰ״̬,����ˢ��...");
			break;
		case DONE:
			headView.setPadding(0, -1 * headContentHeight, 0, 0);
			headView.invalidate();

			progressBar.setVisibility(View.GONE);
			arrowImageView.clearAnimation();
			arrowImageView.setImageResource(R.drawable.ic_pulltorefresh_arrow);
			tipsTextview.setText(R.string.pulltorefresh);

			Log.v(TAG, "��ǰ״̬��done");
			break;
		}
	}

	public void setonRefreshListener(OnRefreshListener refreshListener)
	{
		this.refreshListener = refreshListener;
	}

	public interface OnRefreshListener
	{
		public void onRefresh();
	}

	public void onRefreshComplete()
	{
		state = DONE;
		changeHeaderViewByState();
	}

	private void onRefresh()
	{
		if (refreshListener != null)
		{
			refreshListener.onRefresh();
		}
		timer = new Timer();
		timerTask = new TimerTask()
		{
			@Override
			public void run()
			{
				// TODO Auto-generated method stub
				myHandler.sendEmptyMessage(0);
			}
		};
		timer.schedule(timerTask, 1000);
	}

	// �˷���ֱ���հ��������ϵ�һ������ˢ�µ�demo���˴��ǡ����ơ�headView��width�Լ�height
	private void measureView(View child)
	{
		ViewGroup.LayoutParams p = child.getLayoutParams();
		if (p == null)
		{
			p = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
		}
		int childWidthSpec = ViewGroup.getChildMeasureSpec(0, 0 + 0, p.width);
		int lpHeight = p.height;
		int childHeightSpec;
		if (lpHeight > 0)
		{
			childHeightSpec = MeasureSpec.makeMeasureSpec(lpHeight, MeasureSpec.EXACTLY);
		} else
		{
			childHeightSpec = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
		}
		child.measure(childWidthSpec, childHeightSpec);
	}
}
