package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

@Autonomous (name="Mechhawks Auto Red")
public class AutoModeRed extends TeleOpMode {
    //move to up red hive (limelights probably, unless up hive is standardized)

    //shoot preloaded nectar/pollen

    @Override
    public void shooter_on_near() {
        super.shooter_far_on = true;
    }

    //move to flower (pedropathing, need ryan)
    //get pollen from flower (waiting on build for mechanism)
    //move back to hive, shoot more balls
        //depending on our alliance partner, we might have to go to the other side
        //of the hive if they've tipped it, so mix of pedropathing and limelights
    //shoot pollen from flower
}
