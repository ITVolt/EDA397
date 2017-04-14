package se.chalmers.justintime.timer;

import android.os.CountDownTimer;

/**
 * Created by JonasPC on 2017-04-02.
 */

    public abstract class AbstractTimer implements Timer{
        long millisInFuture = 0;
        long countDownInterval = 0;
        long millisRemaining =  0;

        CountDownTimer countDownTimer = null;

        boolean isPaused = true;

        public AbstractTimer(long millisInFuture, long countDownInterval) {
            super();
            this.millisInFuture = millisInFuture;
            this.countDownInterval = countDownInterval;
            this.millisRemaining = this.millisInFuture;
        }

        private void createCountDownTimer(){
            countDownTimer = new CountDownTimer(millisRemaining,countDownInterval) {

                @Override
                public void onTick(long millisUntilFinished) {
                    millisRemaining = millisUntilFinished;
                    AbstractTimer.this.onTick(millisUntilFinished);

                }

                @Override
                public void onFinish() {
                    AbstractTimer.this.onFinish();

                }
            };
        }
        /**
         * Callback fired on regular interval.
         *
         * @param millisUntilFinished The amount of time until finished.
         */

        public abstract void onTick(long millisUntilFinished);
        /**
         * Callback fired when the time is up.
         */

        public abstract void onFinish();
        /**
         * Cancel the countdown.
         */
        public final void cancel(){
            if(countDownTimer!=null){
                countDownTimer.cancel();
            }
            this.millisRemaining = 0;
        }
        /**
         * Start or Resume the countdown.
         * @return CountDownTimerPausable current instance
         */
        public synchronized final void start(){
            if(isPaused){
                createCountDownTimer();
                countDownTimer.start();
                isPaused = false;
            }
        }
        /**
         * Pauses the CountDownTimerPausable, so it could be resumed(start)
         * later from the same point where it was paused.
         */
        public void pause()throws IllegalStateException{
            if(isPaused==false){
                countDownTimer.cancel();
            } else{
                throw new IllegalStateException("CountDownTimerPausable is already in pause state, start counter before pausing it.");
            }
            isPaused = true;
        }
        public boolean isPaused() {
            return isPaused;
        }
    }

