package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;


@TeleOp
public class MecanumDriveTeleOP extends OpMode {
    //public DcMotor intake1 = null; //artifacts go into robot
    public Servo intake2o1 = null; //artifacts go into cannon
    public Servo intake2o2 = null; //artifacts go into cannon
    //public DcMotor cannon = null; //artifacts go BOOM
    MecanumDriveCode drive = new MecanumDriveCode();

    double forward, strafe, rotate;
    int mode = -1;

    @Override
    public void init() {
        drive.init(hardwareMap);
        telemetry.addData("Mode", "null");
        //intake1 = hardwareMap.get(DcMotor.class, "intake1");
        intake2o1 = hardwareMap.get(Servo.class, "intake2o1");
        intake2o2 = hardwareMap.get(Servo.class, "intake2o2");
        //cannon = hardwareMap.get(DcMotor.class, "cannon");
        //intake1.setDirection(DcMotor.Direction.FORWARD);
        intake2o1.setDirection(Servo.Direction.FORWARD);
        intake2o2.setDirection(Servo.Direction.FORWARD);
        //cannon.setDirection(DcMotor.Direction.FORWARD);
        //Things go brrr
    }

    @Override
    public void loop() {

        forward = gamepad1.left_stick_y;
        strafe = gamepad1.left_stick_x;
        rotate = gamepad1.right_stick_x;
        boolean advCarousel = gamepad2.right_bumper; //Carousel on right bumper (Marissa)
        //boolean advintake1 = gamepad2.left_bumper; //Intake 1 on left bumper (Marissa)
        boolean advintake2o1 = gamepad2.a; //Intake 2 on a button (Marissa)
        boolean advintake2o2 = gamepad2.a; //Intake 2 on a button (Marissa)
        //boolean advCannon =  gamepad2.b; //Cannon launch on b button (Marissa)

        if(mode == -1){
            telemetry.addData("Mode", "null");
        }else if (mode == 0){
            telemetry.addData("Mode", "Robot Oriented");
            drive.drive(forward, strafe, rotate);
        }else if (mode == 1){
            telemetry.addData("Mode", "Field Oriented");
            drive.driveFieldRelative(forward, strafe, rotate);
        }
        if(gamepad1.dpad_down){
            mode = 0;
        }else if(gamepad1.dpad_up){
            mode = 1;
        }
    }
}
//CODE OVERVIEW
// - Wheels go brrr
// - Intake 1 go brrr
// - Intake 2 go brrr
// - Cannon go brrr