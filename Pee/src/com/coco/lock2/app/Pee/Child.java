package com.coco.lock2.app.Pee;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.view.MotionEvent;

public class Child extends Base {

	private final static int space1 = 18;
	private final static int space2 = 14;
	private int childstate = 0;
	private int refreshstate = 0;

	private Context mContext;
	private int width;
	private int height;
	private float scale;
	private boolean touched = false;
	private boolean moved = false;
	private boolean beginUnlock = false;
	private int index;
	private Handler mHandler;
	private Lock4Screen mView;
	private float touchDownY;
	private Drawable noteDrawables[];
	private int noteNum = 0;
	private final int[] noteImageId = { R.drawable.note001, R.drawable.note004,
			R.drawable.note003, R.drawable.note002 };
	private Drawable childDrawables[];
	private Drawable peeingDrawables[];
	private final int[] childImageId = { R.drawable.pen001, R.drawable.pen002,
			R.drawable.pen003, R.drawable.pen004, R.drawable.pen005,
			R.drawable.pen006 };
	private final int[] childpeeingID = { R.drawable.peeing002,
			R.drawable.peeing004, R.drawable.peeing006, R.drawable.peeing008,
			R.drawable.peeing010, R.drawable.peeing012, R.drawable.peeing014,
			R.drawable.peeing016, R.drawable.peeing018, R.drawable.peeing020,
			R.drawable.peeing021, R.drawable.peeing022, R.drawable.peeing023,
			R.drawable.peeing024, R.drawable.peeing025, R.drawable.peeing026,
			R.drawable.peeing027, R.drawable.peeing028, R.drawable.peeing029,
			R.drawable.peeing030 };
	private Drawable fireDrawables[];
	private Drawable extinguishDrawables[];
	private int fireIndex;
	private final int[] fireImageId = { R.drawable.fire001, R.drawable.fire002,
			R.drawable.fire003, R.drawable.fire004, R.drawable.fire005,
			R.drawable.fire006, R.drawable.fire007, R.drawable.fire008 };

	private final int[] ExtinguishImageId = { R.drawable.extinguish001,
			R.drawable.extinguish002, R.drawable.extinguish003,
			R.drawable.extinguish004, R.drawable.extinguish005,
			R.drawable.extinguish006, R.drawable.extinguish007,
			R.drawable.extinguish008, R.drawable.extinguish009,
			R.drawable.extinguish010, R.drawable.extinguish012,
			R.drawable.extinguish013, R.drawable.extinguish015,
			R.drawable.extinguish017, R.drawable.extinguish019 };

	public Child(Context context, int w, int h) {
		super();
		mContext = context;
		width = w;
		height = h;
		scale = width / 480f;
		createDrawable();
	}

	public void setViewHeight(int h) {
		setDrawableBounds(h);
	}

	public void setView(Lock4Screen view, Handler handler) {
		mView = view;
		mHandler = handler;
	}

	private void setDrawableBounds(int h) {
		if (childDrawables != null) {
			for (int i = 0; i < childDrawables.length; i++) {
				childDrawables[i].setBounds((int) (space1 * scale), (int) (h
						- space2 * scale - childDrawables[i]
						.getIntrinsicHeight()),
						(int) (space1 * scale + childDrawables[i]
								.getIntrinsicWidth()), (int) (h - space2
								* scale));
			}
		}

		if (peeingDrawables != null) {
			for (int i = 0; i < childpeeingID.length; i++) {
				peeingDrawables[i].setBounds((int) (space1 * scale), (int) (h
						- space2 * scale - peeingDrawables[i]
						.getIntrinsicHeight()),
						(int) (space1 * scale + peeingDrawables[i]
								.getIntrinsicWidth()), (int) (h - space2
								* scale));
			}
		}

		if (noteDrawables != null) {
			for (int i = 0; i < noteDrawables.length; i++) {
				float x = 250 * scale
						+ (noteDrawables[i].getIntrinsicWidth() + 2) * i;
				float y = h - 300 * scale
						- (noteDrawables[i].getIntrinsicHeight() - 20) * i;
				if (i % 2 == 0) {
					y += noteDrawables[i].getIntrinsicHeight() * 0.2;
					noteDrawables[i].setBounds((int) x, (int) (y),
							(int) (x + noteDrawables[i].getIntrinsicWidth()),
							(int) (y + noteDrawables[i].getIntrinsicHeight()));
				} else {
					y -= noteDrawables[i].getIntrinsicHeight() * 0.2;
					noteDrawables[i].setBounds((int) x, (int) (y),
							(int) (x + noteDrawables[i].getIntrinsicWidth()),
							(int) (y + noteDrawables[i].getIntrinsicHeight()));
				}
			}
		}

		float fire_x = (space1 * scale + peeingDrawables[childpeeingID.length - 1]
				.getIntrinsicWidth() * 0.95f);
		float fire_y = h - space2 * scale;
		if (fireDrawables != null) {
			for (int i = 0; i < fireDrawables.length; i++) {
				fireDrawables[i]
						.setBounds((int) (fire_x - fireDrawables[i]
								.getIntrinsicWidth()),
								(int) (fire_y - fireDrawables[i]
										.getIntrinsicHeight() * 2),
								(int) (fire_x + fireDrawables[i]
										.getIntrinsicWidth()), (int) fire_y);
			}
		}
		if (extinguishDrawables != null) {
			for (int i = 0; i < extinguishDrawables.length; i++) {
				extinguishDrawables[i].setBounds(
						(int) (fire_x - extinguishDrawables[i]
								.getIntrinsicWidth()),
						(int) (fire_y - extinguishDrawables[i]
								.getIntrinsicHeight() * 2),
						(int) (fire_x + extinguishDrawables[i]
								.getIntrinsicWidth()), (int) fire_y);
			}
		}

	}

	private void createDrawable() {
		childDrawables = new Drawable[childImageId.length];
		for (int i = 0; i < childDrawables.length; i++) {
			childDrawables[i] = mContext.getResources().getDrawable(
					childImageId[i]);
		}
		peeingDrawables = new Drawable[childpeeingID.length];
		for (int i = 0; i < childpeeingID.length; i++) {
			peeingDrawables[i] = mContext.getResources().getDrawable(
					childpeeingID[i]);
		}
		noteDrawables = new Drawable[noteImageId.length];
		for (int i = 0; i < noteDrawables.length; i++) {
			noteDrawables[i] = mContext.getResources().getDrawable(
					noteImageId[i]);
		}
		fireDrawables = new Drawable[fireImageId.length];
		for (int i = 0; i < fireDrawables.length; i++) {
			fireDrawables[i] = mContext.getResources().getDrawable(
					fireImageId[i]);
		}
		extinguishDrawables = new Drawable[ExtinguishImageId.length];
		for (int i = 0; i < extinguishDrawables.length; i++) {
			extinguishDrawables[i] = mContext.getResources().getDrawable(
					ExtinguishImageId[i]);
		}
	}

	private Runnable animRunnable = new Runnable() {

		@Override
		public void run() {
			if (!moved) {
				index++;
				if (index >= childImageId.length) {
					index = 0;
				}
			} else if (beginUnlock) {
				if (refreshstate == 2) {
					fireIndex++;
					if (fireIndex >= ExtinguishImageId.length) {
						fireIndex = ExtinguishImageId.length - 1;
					}
					if (noteNum < noteImageId.length) {
						noteNum++;
					}
					index++;
					if (index >= childpeeingID.length) {
						index = childpeeingID.length - 1;
						mView.exitLock();
					}
				} else {
					index++;
					if (index >= childpeeingID.length) {
						index = childpeeingID.length - 1;
					}
					if (index >= childpeeingID.length - 15) {
						refreshstate = 2;
						fireIndex = 0;
					}
				}
			}
			if (refreshstate != 2) {
				fireIndex++;
				if (fireIndex >= fireImageId.length) {
					fireIndex = 0;
				}
			}
			if (refreshstate == 2) {
				mHandler.postDelayed(this, 30);
				mView.postInvalidate();
			} else {
				mHandler.postDelayed(this, 50);
				mView.postInvalidate();
			}
		}
	};

	@Override
	public void onDraw(Canvas canvas) {
		if (beginUnlock) {
			peeingDrawables[index].draw(canvas);
		} else {
			childDrawables[index].draw(canvas);
		}

		if (refreshstate == 2) {
			extinguishDrawables[fireIndex].draw(canvas);
			for (int i = 0; i < noteNum; i++) {
				noteDrawables[i].draw(canvas);
			}
		} else {
			fireDrawables[fireIndex].draw(canvas);
		}
	}

	public int getChildstate() {
		return childstate;
	}

	public void onTouchEvent(MotionEvent event) {
		if (event.getAction() == MotionEvent.ACTION_DOWN) {
			touched = true;
			touchDownY = event.getY();
		} else if (event.getAction() == MotionEvent.ACTION_MOVE) {
			if (touched && !beginUnlock) {
				moved = true;
				if (touchDownY - event.getY() > height / 6f) {
					beginUnlock = true;
					index = 0;
					fireIndex = 0;
				}
			}
		} else if (event.getAction() == MotionEvent.ACTION_UP) {
			if (!beginUnlock) {
				touched = false;
				moved = false;
				index = 0;
				noteNum = 0;
				fireIndex = 0;
				refreshstate = 0;
			} else {
				mView.hidePrompt();
			}
		}
	}

	private void freedDrawable(Drawable drawable) {
		if (drawable != null) {
			drawable.setCallback(null);
			drawable = null;
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		if (childDrawables != null) {
			for (int i = 0; i < childDrawables.length; i++) {
				freedDrawable(childDrawables[i]);
			}
		}
		if (peeingDrawables != null) {
			for (int i = 0; i < peeingDrawables.length; i++) {
				freedDrawable(peeingDrawables[i]);
			}
		}

		if (noteDrawables != null) {
			for (int i = 0; i < noteDrawables.length; i++) {
				freedDrawable(noteDrawables[i]);
			}
		}

		if (fireDrawables != null) {
			for (int i = 0; i < fireDrawables.length; i++) {
				freedDrawable(fireDrawables[i]);
			}
		}
		if (extinguishDrawables != null) {
			for (int i = 0; i < extinguishDrawables.length; i++) {
				freedDrawable(extinguishDrawables[i]);
			}
		}
	}

	@Override
	public void onResume() {
		super.onResume();
		index = 0;
		touched = false;
		moved = false;
		beginUnlock = false;
		refreshstate = 0;
		mHandler.postDelayed(animRunnable, 100);
	}

	@Override
	public void onPause() {
		super.onPause();
		mHandler.removeCallbacks(animRunnable);
	}
}
