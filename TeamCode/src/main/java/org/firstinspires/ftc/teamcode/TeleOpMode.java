package org.firstinspires.ftc.teamcode;

import static com.qualcomm.robotcore.util.ElapsedTime.Resolution.MILLISECONDS;
//import static java.lang.Thread.sleep;

import static java.lang.Thread.sleep;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.navigation.CurrentUnit;

//import org.firstinspires.ftc.robotcore.external.Telemetry;

@TeleOp(name="Mechawks-Tele-1")
public class   TeleOpMode extends HwInit {
//far and mid use the trigger buttons which are analog,
// but near is on the bumper which is just on/off
  float shooter_far_on = 0.0F;
  float shooter_mid_on = 0.0F;
  boolean shooter_near_on = false;
  boolean intake_on = false;
  boolean intake_clear = false;
  double carousel_dir = 1;
  ElapsedTime lift_up_timer = new ElapsedTime(MILLISECONDS);
  double lift_up_time_limit = 1300;
  boolean move_lift_up = false;
  boolean move_lift_down = false;
  boolean limelight_read = false;

  long tick = 0;

@Override
    public void init()
    {
        Hw_init();

        //set drive speed at 0.5 initially
        speed = 0.85;
        //initialise bumpers as "not pressed"
        r_bump_1 = false;
        l_bump_1 = false;

        //you probably dont want to do this.
        //telemetry.setAutoClear(false);
    }
    @Override
    public void init_loop()
    {
        RGB_light.blink(.275);
    }

    @Override
    public void loop() {
        LimeLightLocalize();


        double curVelocity = shooter1.getVelocity();
        telemetry.addData("Shooter Velocity: ", curVelocity);
        double current = shooter1.getCurrent(CurrentUnit.MILLIAMPS);
        telemetry.addData("Shooter current(mA): ", current);
       double curVelocity2 = shooter2.getVelocity();
       telemetry.addData("Shooter 2 Velocity: ", curVelocity);
       double current2 = shooter2.getCurrent(CurrentUnit.MILLIAMPS);
       telemetry.addData("Shooter 2 current(mA): "current2);
        Boolean state = LimeLightRead();
        telemetry.addData("current tag: ", current_tag);
        ColorSensor.DetectedColor color = color_sense.getDetectedColor(telemetry);
        update_light(color.name());
        do_p1_things();
        do_p2_things();

        if (limelight_read){
            LimeLightRead();
            if (current_tag == 20){
                update_light("BLUE");
            } else if (current_tag == 24) {
                update_light("RED");
            }
        }

        if(carousel_on)
        {
            set_carousel_mode();
        }
        if (move_to_load)
        {
            move_to_load_from_shoot(carousel_dir);
        }
        if (move_to_shoot)
        {
            move_to_shoot_from_load(carousel_dir);
        }
        try {
            if (lift_on) {
                if (ShootSw.isLimitSwitchClosed()) {
                    /*telemetry.addLine()
                            .addData("lift_on: lift UP: ", move_lift_up)
                            .addData(" lift DOWN: ", move_lift_down);*/
                    move_lift_up = true;
                    lift.setPower(-1);
                    //TODO: adjust this time if needed
                    sleep(200);
                    lift.setPower(0);
                    lift_up_timer.reset();
                }
            }
            if (move_lift_up) {
                lift.setPower(1.0);
                telemetry.addData("timer: ", lift_up_timer.time());

                if (lift_up_timer.time() >= lift_up_time_limit) {
                    //lift.setPower(0); //removed while debugging - may overload command bus?
                    move_lift_up = false;
                    move_lift_down = true;
                    lift_up_timer.reset();
                }
                /*telemetry.addLine()
                        .addData("end of move up: lift UP: ", move_lift_up)
                        .addData(" lift DOWN: ", move_lift_down);*/
            }
            if (move_lift_down) {
                lift.setPower(-1.0);
                if (shooterPosSw.isLimitSwitchPressed()) {
                /*telemetry.addLine()
                        .addData("down switch pressed: lift UP: " , move_lift_up)
                        .addData(" lift DOWN: ", move_lift_down);*/
                    move_lift_down = false;
                    lift.setPower(0.0);
                    liftPow = lift.getPower();
                }
            }
        } catch (Exception e) {
            telemetry.addLine("EXCEPTIONAL!!!");
            try {
                sleep(1000);
            } catch (InterruptedException ex) {
                throw new RuntimeException(ex);
            }
            throw new RuntimeException(e);
        }
        //failsafe
        if (!move_lift_down &&
                !move_lift_up)
        {
            lift.setPower(0.0);
            telemetry.addLine()
                    .addData("end failsafe: lift UP: " , move_lift_up)
                    .addData(" lift DOWN: ", move_lift_down);
        }

        run_motors();
        telemetry.update();
    }
    public void run_motors() {

        if (LoadSw.isLimitSwitchClosed()) {
            telemetry.addData("Load State", LoadSw.isLimitSwitchClosed());
        }else {
            telemetry.addData("Load State", LoadSw.isLimitSwitchClosed());
        }
        if (ShootSw.isLimitSwitchClosed()){
            telemetry.addData("Shoot State", ShootSw.isLimitSwitchClosed());
        }else {
            telemetry.addData("Shoot State", ShootSw.isLimitSwitchClosed());
        }
        telemetry.addData("Shoot Pos Switch: ", shooterPosSw.isLimitSwitchPressed());



        if (intake_on)
        {
            intake.setPower(0.9);
        }
        else if (intake_clear)
        {
            intake.setPower(-0.4);
        }
        else
        {
            intake.setPower(0);
        }

        if (shooter_mid_on > 0.23)
        {
            shooter_on_mid();
        }else if (shooter_far_on  > 0.2)
        {
            shooter_on_far();
        }else if (shooter_near_on)
        {
            shooter_on_near();
        }else {
            shooter_off();
        }

    }


    public float avg(float[] nums) {
        int numlen = nums.length;
        float tot = 0;
        for (int i = 0; i < numlen; i++) {
            tot += nums[i];
        }
        tot /= numlen;
        return tot;
    }

    public void dual_joy_control(float left_stick_x, float left_stick_y, float right_stick_x, float right_stick_y) {
        /*TABLE OF INP
             LX  LY  RX
        RR   +   -   -
        RL   +   +   -
        FR   -   -   -
        FL   -   +   -
        */

        backRightMotor.setPower(speed * (left_stick_x - left_stick_y - right_stick_x));
        backLeftMotor.setPower(speed * (left_stick_x + left_stick_y - right_stick_x));
        frontRightMotor.setPower(speed * (-left_stick_x - left_stick_y - right_stick_x));
        frontLeftMotor.setPower(speed * (-left_stick_x + left_stick_y - right_stick_x));
    }

    public void p1_fine_speed_control() {
        if (gamepad1.a) {
            speed = 1;
        }
        if (gamepad1.x) {
            speed = 0.1;
        }
        if (gamepad1.y) {
            speed = 0.45;
        }
        if (gamepad1.b) {
            speed = 0.8;
        }
        if (gamepad1.right_bumper) {
            if (!r_bump_1) {
                speed += speed_fine_inc;
            }
            r_bump_1 = true;
        } else {
            r_bump_1 = false;
        }
        if (gamepad1.left_bumper) {
            if (!l_bump_1) {
                speed -= speed_fine_inc;
            }
            l_bump_1 = true;
        } else {
            l_bump_1 = false;
        }
    }
    public void do_p1_things() {
        p1_fine_speed_control();

        dual_joy_control(gamepad1.left_stick_x,gamepad1.left_stick_y,gamepad1.right_stick_x,gamepad1.right_stick_y);
        boolean targeted = false;
        limelight_read = gamepad1.right_bumper;


        if (limelight_read)
        {
            // Turn encoders on, target, turn encoders off
            pos_drive_init();
            LimelightTarget();
            motor_pow_drive_init();

        }

    }
    public void do_p2_things() {

        carousel_on = (gamepad2.x || gamepad2.b);

        if (gamepad2.x)
        {
            carousel_dir = 1;
        }

        if (gamepad2.b)
        {
             carousel_dir = -1;
        }

        intake_on = gamepad2.dpad_up;
        intake_clear = gamepad2.dpad_down;
        shooter_near_on = gamepad2.left_bumper;
        shooter_mid_on = gamepad2.left_trigger;
        shooter_far_on = gamepad2.right_trigger;
        lift_on = gamepad2.yWasPressed();
        if (gamepad2.aWasPressed())
        {
            try {
                lift.setPower(-1);
                //TODO: adjust this time if needed
                sleep(250);
                lift.setPower(0);

            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

    }
}