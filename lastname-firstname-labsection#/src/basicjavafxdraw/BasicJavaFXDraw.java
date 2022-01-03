package basicjavafxdraw;

import java.util.Random;

import javafx.animation.Animation;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.Spinner;
import javafx.scene.control.ToolBar;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

/**
 * 
 * @author Shariar (Shawn) Emami
 * @version Sep 14, 2020
 */
public class BasicJavaFXDraw extends Application {
	/**
	 * Background color
	 */
	private static final Color BACKGROUND = Color.LIGHTPINK;
	/**
	 * Size of the scene
	 */
	private static final double WIDTH = 900, HEIGHT = 600;
	/**
	 * Title of application
	 */
	private static final String TITLE = "JavaFX Draw Particle";
	/**
	 * {@link BorderPane} is a layout manager that manages all nodes in 5 areas as
	 * below:
	 * 
	 * <pre>
	 * -----------------------
	 * |        top          |
	 * -----------------------
	 * |    |          |     |
	 * |left|  center  |right|
	 * |    |          |     |
	 * -----------------------
	 * |       bottom        |
	 * -----------------------
	 * </pre>
	 * 
	 * This object is passed to {@link Scene} object in
	 * {@link BasicJavaFXDraw#start(Stage)} method.
	 */
	private BorderPane root;
	private Canvas canvas;
	private Animator animator;
	private Slider hueShift;
	private Spinner<Integer> dotsPerFrame;

	/**
	 * <p>
	 * This method is called at the very beginning of the JavaFX application and can
	 * be used to initialize all components in the application. However,
	 * {@link Scene} and {@link Stage} must not be created in this method. This
	 * method does not run on JavaFX thread, it runs on JavaFX-Launcher thread. For
	 * example if you try to execute:<br>
	 * Alert alert = new Alert( AlertType.INFORMATION); <br>
	 * It will fail. If placed in start method it will pass.
	 * </p>
	 */
	@Override
	public void init() throws Exception {
		canvas = new Canvas(WIDTH, HEIGHT);
//	    Animator animator = new Animator();//this is a local variable, because the type is defined again at the beginning
		animator = new Animator();// this is an instance variable because it was declared as field and now just
									// simply initialized
		// Initialize animator.
		Button exit = new Button("Exit");
		exit.setOnAction((ActionEvent e) -> Platform.exit());
		Button start = new Button("Start");
		start.setOnAction((EventHandler<ActionEvent>) new EventHandler<ActionEvent>() {
			// @Override
			public void handle(ActionEvent event) {
				animator.start();

			}
		});

		Button clear = new Button("clear");
		clear.setOnAction((ActionEvent e) -> canvas.getGraphicsContext2D().clearRect(0, 0, WIDTH, HEIGHT));
		hueShift = new Slider(0, 360, 0);
		// double value = hueShift.getValue();
		dotsPerFrame = new Spinner<>(1, 1000, 100, 10);
		// double value = dotsPerFrame.getValue();
		Label hs = new Label("HUE SHIFT");
		Label dpf = new Label("dotsPerFrame");
		ToolBar toolbar = new ToolBar(exit, start, clear, hs, hueShift, dpf, dotsPerFrame);
		Label mousePosLabel = new Label("Outside");
		root = new BorderPane();
		root.setOnDragExited(e -> mousePosLabel.setText("Outside"));
		root.setOnMouseMoved(e -> mousePosLabel.setText(String.format("Moved: (%.2f,%.2f)", e.getX(), e.getY())));
		root.setOnMouseDragged(e -> mousePosLabel.setText(String.format("Dragged: (%.2f,%.2f)", e.getX(), e.getY())));
		root.setTop(toolbar);
		root.setCenter(canvas);
		root.setBottom(mousePosLabel);

		System.out.println("init method");

	}

	/**
	 * <p>
	 * This method is called when JavaFX application is started and it is running on
	 * JavaFX thread. This method must at least create {@link Scene} and finish
	 * customizing {@link Stage}. These two objects must be on JavaFX thread when
	 * created.
	 * </p>
	 * <p>
	 * {@link Stage} represents the frame of your application, such as minimize,
	 * maximize and close buttons.<br>
	 * {@link Scene} represents the holder of all your JavaFX {@link Node}s.<br>
	 * {@link Node} is the super class of every javaFX class.
	 * </p>
	 * 
	 * @param primaryStage - Primary stage of your application that will be rendered
	 */
	@Override
	public void start(Stage primaryStage) throws Exception {
		// To be called after init() on JavaFX-Application Thread.
		// This method can fully replace init() if desired.

		System.out.println("start method");
		// Scene holds everything made by user to be displayed
		Scene scene = new Scene(root, BACKGROUND);
		// Primary stage is everything else such as title, decoration, bevels, and etc
		primaryStage.setScene(scene);
		primaryStage.setTitle(TITLE);
		// If escape key is pressed close the application
		primaryStage.addEventHandler(KeyEvent.KEY_RELEASED, (KeyEvent event) -> {
			if (KeyCode.ESCAPE == event.getCode()) {
				primaryStage.hide();
			}
		});
		// Display the JavaFX application
		primaryStage.show();
	}

	/**
	 * This method is called at the very end when the application is about to exit.
	 * This method is used to stop or release any resources used during the
	 * application.
	 */
	@Override
	public void stop() throws Exception {
		// To be called at the very end application runtime on JavaFX-Application
		// Thread.
		// Can be used to stop, close, save, or any finalizations.
		System.out.println("stop method");

		animator.stop();
	}

	public static void main(String[] args) {
		System.out.println("main method");
		// This method must be in main method to launch JavaFX application.
		launch(args);

	}

	private class Animator extends AnimationTimer {
		private Random rand = new Random();

		@Override
		public void handle(long now) {
			GraphicsContext GC = canvas.getGraphicsContext2D();
			for (int i = 0; i < dotsPerFrame.getValue(); i++) {
				double x = getRand(0, WIDTH);
				double y = getRand(0, HEIGHT);
				double hue = mapTo(x, 0, WIDTH, 0, 360);
				// Create a hue variable and initialize it by mapping the x from step 2.1 which
				// was between 0 and WIDTH to 0 and 360.
				Color color = Color.hsb(hue + hueShift.getValue(), 1, 1);
				GC.setFill(color);
				GC.fillOval(x, y, 4,4);
				// Use fillOval from object of step 1 and draw a circle with x and y of steps
				// 2.1 and 2.2 and height and width of 4.

			}

		}

		private double getRand(double min, double max) {
			return min + (max - min) * rand.nextDouble();
		}

	}

	private double mapTo(double x, double a, double b, double c, double d) {
		return (x - a) / (b - a) * (d - c) + c;
	}
}
