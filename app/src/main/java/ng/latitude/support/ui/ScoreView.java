package ng.latitude.support.ui;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.os.Handler;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

import ng.latitude.R;
import ng.latitude.support.conf.Latitude;

/**
 * Created by Ng on 15/6/20.
 */
public class ScoreView {

    private final TextView tvPlayer;
    private final TextView tvForce;
    private final Handler handler;
    private final AnimatorSet upOut;
    private final AnimatorSet upIn;
    private final AnimatorSet downOut;
    private final AnimatorSet downIn;
    private boolean isPlayerScoreInit = false;
    private boolean isForceScoreInit = false;

    public ScoreView(Toolbar toolbar) {
        tvPlayer = (TextView) toolbar.findViewById(R.id.toolbar_score_player);
        tvForce = (TextView) toolbar.findViewById(R.id.toolbar_score_force);
        handler = new Handler();

        upOut = (AnimatorSet) AnimatorInflater.loadAnimator(Latitude.getContext(), R.animator.up_scale_fade_out);
        upOut.setInterpolator(GravityInterpolator.getInstance(false));
        upIn = (AnimatorSet) AnimatorInflater.loadAnimator(Latitude.getContext(), R.animator.up_scale_fade_in);
        upIn.setInterpolator(GravityInterpolator.getInstance(true));
        downOut = (AnimatorSet) AnimatorInflater.loadAnimator(Latitude.getContext(), R.animator.down_scale_fade_out);
        downOut.setInterpolator(GravityInterpolator.getInstance(false));
        downIn = (AnimatorSet) AnimatorInflater.loadAnimator(Latitude.getContext(), R.animator.down_scale_fade_in);
        downIn.setInterpolator(GravityInterpolator.getInstance(true));
    }

    public void updateScore() {

        int oldPlayerScore = Integer.parseInt(isPlayerScoreInit ? tvPlayer.getText().toString() : "0");
        int oldForceScore = Integer.parseInt(isForceScoreInit ? tvForce.getText().toString() : "0");

        if (Latitude.getUserInfo().getPlayerScore() != oldPlayerScore) {
            setScore(tvPlayer, Latitude.getUserInfo().getPlayerScore(), Latitude.getUserInfo().getPlayerScore() > oldPlayerScore);
            isPlayerScoreInit = true;
        }
        if (Latitude.getUserInfo().getForceScore() != oldForceScore) {
            setScoreDelay(tvForce, Latitude.getUserInfo().getForceScore(), Latitude.getUserInfo().getForceScore() > oldForceScore);
            isForceScoreInit = true;
        }


    }

    private void setScore(final TextView v, final int score, boolean isIncreased) {
        final AnimatorSet first;
        final AnimatorSet second;

        if (isIncreased) {
            first = upOut.clone();
            second = upIn.clone();
        } else {
            first = downOut.clone();
            second = downIn.clone();
        }

        first.setTarget(v);
        second.setTarget(v);


        first.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                v.setText(String.valueOf(score));
                second.start();
                first.removeAllListeners();
            }
        });
        first.start();
    }

    private void setScoreDelay(final TextView v, final int score, final boolean isIncreased) {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                setScore(v, score, isIncreased);
            }
        }, 200);
    }


}

