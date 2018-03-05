/**
 * @author Daniel Ivanov
 * @date 3/4/18
 * @verson 1.0
 *
 * This is a simple timer app created for Tony as a proof of concept.
 */
package com.example.danielivanov.timerexample

import android.content.res.Configuration
import android.graphics.Color
import android.graphics.PorterDuff
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.view.View
import android.widget.*
import android.media.RingtoneManager


class MainActivity : AppCompatActivity() {

    // need a few globals
    var running = false
    var currSec = 0
    var currMin = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //get resources
        val min = findViewById<TextView>(R.id.Minutes)
        val sec = findViewById<TextView>(R.id.Seconds)
        val colon = findViewById<TextView>(R.id.colon)
        val numPicker = findViewById<LinearLayout>(R.id.LinearNumPicker)
        val minPicker = findViewById<NumberPicker>(R.id.Min_picker)
        val secPicker = findViewById<NumberPicker>(R.id.Sec_picker)
        var secondsCircle = findViewById<ProgressBar>(R.id.secondsCircle)

        //set initial data
        secondsCircle.progress = 100
        minPicker.minValue = 0
        minPicker.maxValue = 59

        secPicker.minValue = 0
        secPicker.maxValue = 59

        //display the timer time
        displayTime(min, sec)


        val setTimeButton = findViewById<Button>(R.id.SetTime)

        val startTime = findViewById<Button>(R.id.Start)

        val set = findViewById<Button>(R.id.reset)
        //set listener for when the "Set time" button is clicked
        set.setOnClickListener{
            if(running){
                running = false
            }
            min.visibility = View.INVISIBLE
            sec.visibility = View.INVISIBLE
            startTime.visibility = View.INVISIBLE
            set.visibility = View.INVISIBLE
            colon.visibility = View.INVISIBLE
            //hide the seconds circle when in landscape mode
            if(resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
                secondsCircle.visibility = View.INVISIBLE
            }

            //show numpicker and set button instead
            numPicker.visibility = View.VISIBLE
            setTimeButton.visibility = View.VISIBLE

        }

        //set listener for when the "Set Time" button is clicked
        setTimeButton.setOnClickListener{
            currMin = minPicker.value
            currSec = secPicker.value

            displayTime(min, sec)


            min.visibility = View.VISIBLE
            sec.visibility = View.VISIBLE
            startTime.visibility = View.VISIBLE
            set.visibility = View.VISIBLE
            colon.visibility = View.VISIBLE
            secondsCircle.visibility = View.VISIBLE

            numPicker.visibility = View.INVISIBLE
            setTimeButton.visibility = View.INVISIBLE
        }

        //set listener for when the "start"/"stop" button is clicked
        startTime.setOnClickListener {
            var totalSec = currMin * 60 + currSec

            if(totalSec > 0) {
                //create the CountDownTimer object
                var timer = object : CountDownTimer((totalSec * 1000).toLong(), 1000) {
                    //reset settings when countdown finnished
                    override fun onFinish() {
                        running = false
                        currSec--
                        displayTime(min, sec)
                        secondsCircle.progress = 0
                        startTime.text = "Start"
                        try {
                            // play notification sound
                            val notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
                            val r = RingtoneManager.getRingtone(applicationContext, notification)
                            r.play()
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }

                    }
                    // update display (MM:SS) and seconds circle on every tick
                    override fun onTick(millsUntilFinished: Long) {
                        if (currSec == 0 && currMin != 0) {
                            currMin--
                            currSec = 60
                        }
                        currSec--
                        displayTime(min, sec)
                        secondsCircle.progress = (((millsUntilFinished - 1000)/(totalSec*10)).toInt())

                        // if the total time is above 15, add color codes to the circle (yellow and red) when count is getting low
                        if(totalSec > 15) {
                            if (currMin == 0 && currSec > 4 && currSec < 11) {
                                secondsCircle.progressDrawable.mutate().setColorFilter(Color.YELLOW, PorterDuff.Mode.SRC_IN)
                            }

                            if (currMin == 0 && currSec < 4) {
                                secondsCircle.progressDrawable.mutate().setColorFilter(Color.RED, PorterDuff.Mode.SRC_IN)
                            }
                        }
                        // cancel if stop is pressed
                        if (!running) {
                            cancel()
                        }
                    }
                }

                if (running) {
                    startTime.text = "Start"
                    running = false
                } else {
                    running = true
                    startTime.text = "Stop"
                    timer.start()
                }
            }
        }
    }
    // for when landscape mode is used. Need to save isRunning, and current time left on clock
    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
        outState?.putBoolean("running", running)
        outState?.putInt("currSec", currSec)
        outState?.putInt("currMin", currMin)
    }
    // restore the current time and running settings (
    override fun onRestoreInstanceState(savedInstanceState: Bundle?) {
        super.onRestoreInstanceState(savedInstanceState)
        if (savedInstanceState != null) {
            running = savedInstanceState.getBoolean("running")
            currSec = savedInstanceState.getInt("currSec")
            currMin = savedInstanceState.getInt("currMin")
        }
    }
    // small extra method that updates the MM:SS when the number is under 10 (adds leading '0')
    fun displayTime(min:TextView, sec:TextView) {
            min.text = currMin.toString()
            sec.text = currSec.toString()

            if (currMin < 10) {
                var correctedMin = '0' + currMin.toString()
                min.text = correctedMin
            }
            if (currSec < 10) {
                var correctedSec = '0' + currSec.toString()
                sec.text = correctedSec
            }
    }
}
