package se.chalmers.justintime.gui.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ViewFlipper;

import se.chalmers.justintime.Presenter;
import se.chalmers.justintime.R;

import static android.view.View.OnTouchListener;

/**
 * Created by David on 2017-04-25.
 */

public class TimerSequenceFragment extends Fragment {


    private View view;
    private ViewFlipper viewFlipper;
    private Presenter presenter;
    private Animation swipeRight, swipeLeft;


    private float touchDownX, touchUpX;
    final int MINIMUM_SWIPE_DISTANCE = 150;

    public static TimerSequenceFragment newInstance() {
        TimerSequenceFragment timerSequenceFragment = new TimerSequenceFragment();
        Bundle args = new Bundle();
        timerSequenceFragment.setArguments(args);
        return timerSequenceFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_sequence_timer, container, false);
        viewFlipper = (ViewFlipper) view.findViewById(R.id.seq_timer_view_flipper);
        swipeRight = AnimationUtils.loadAnimation(getActivity(), R.anim.from_right_swipe);
        swipeLeft = AnimationUtils.loadAnimation(getActivity(), R.anim.from_left_swipe);

        view.setOnTouchListener(new TimerSequenceSwipeListener());
        viewFlipper.addView(inflater.inflate(R.layout.fragment_timer, container, false));
        viewFlipper.addView(inflater.inflate(R.layout.fragment_statistics, container, false));
        viewFlipper.addView(inflater.inflate(R.layout.fragment_timer, container, false));

        viewFlipper.setInAnimation(swipeRight);
        viewFlipper.setOutAnimation(swipeLeft);

        return view;
    }


    private boolean setNextTimer() {
        viewFlipper.showNext();
        return false;
    }

    private boolean setPreviousTimer() {
        viewFlipper.showPrevious();
        return false;
    }

    private class TimerSequenceSwipeListener implements OnTouchListener {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            switch (motionEvent.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    touchDownX = motionEvent.getX();
                    return true;
                case MotionEvent.ACTION_UP:
                    touchUpX = motionEvent.getX();
                    if (isSwipeValid(touchDownX, touchUpX, MINIMUM_SWIPE_DISTANCE)) {
                        if (getDirectionOfSwipe(touchDownX, touchUpX).equals(Direction.RIGHT)) {
                            setNextTimer();
                        } else {
                            setPreviousTimer();
                        }
                        return true;
                    }
                default:
                    break;
            }

            return false;
        }

        private Direction getDirectionOfSwipe(float x1, float x2) {
            if ((x2 - x1) < 0) {
                return Direction.LEFT;
            } else {
                return Direction.RIGHT;
            }
        }

        private boolean isSwipeValid(float touchDownX, float touchUpX, int swipeDistance) {
            return Math.abs(touchUpX - touchDownX) > swipeDistance;
        }

    }

    private enum Direction {
        LEFT, RIGHT
    }

}
