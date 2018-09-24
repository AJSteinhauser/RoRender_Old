


import com.sun.net.httpserver.*;
import java.io.*;
import java.net.InetSocketAddress;
import java.awt.image.BufferedImage;
import javafx.embed.swing.SwingFXUtils;
import javax.imageio.ImageIO;
import java.util.*;

import java.lang.*;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.scene.Cursor;
import javafx.scene.paint.*;
import javafx.scene.shape.*;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.text.*;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.image.WritableImage;
import javafx.scene.image.*;
import javafx.geometry.Pos;
import javafx.geometry.Insets;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.application.Platform;








public class HttpTesting extends Application{
	static boolean Activated = false;

	static int XTotal = 0;
	static int YTotal = 0;

	static int XCount = 0;
	static int YCount = -1;

	static WritableImage Image;
	static PixelWriter PixelWrite;
   static ImageView ImageViewer = new ImageView(Image);

	static String TotalData = "";
   
   
	static Button ExportImage = new Button("Export Image");

	public void start(Stage Main){
		BorderPane Pane = new BorderPane();

		Pane.setPadding(new Insets(10, 20, 10, 20));

		GridPane TitlePane = new GridPane();
		Label TitleLabel = new Label("RoRender");
		TitleLabel.setFont(new Font("Ariel",20));
		Label SubTitleLabel = new Label("By: VineyardVine");
		Font SubTitleFont = new Font("Ariel Italic",15);
		SubTitleLabel.setFont(SubTitleFont);


		TitlePane.add(TitleLabel,0,0);
		TitlePane.add(SubTitleLabel,0,1);

		GridPane BottomPane = new GridPane();
		Button OpenStream = new Button("Start Server");
		BottomPane.add(OpenStream,1,0);
		BottomPane.add(ExportImage,0,0);

		ExportImage.setDisable(true);

		Pane.setAlignment(BottomPane,Pos.BOTTOM_RIGHT);
		Pane.setBottom(BottomPane);


		HBox CenterPane = new HBox();
		
		Pane.getChildren().addAll(ImageViewer);
		Pane.setCenter(CenterPane);

		Pane.setAlignment(TitlePane,Pos.TOP_LEFT);
		Pane.setTop(TitlePane);

		//OpenStream.setDisable(false);

		OpenStream.setOnAction(new EventHandler<ActionEvent>(){
			public void handle(ActionEvent Event){
				if (!Activated){
					try{
						HttpServer Server = HttpServer.create(new InetSocketAddress(8080),0);
						Server.createContext("/requests", new MyHandler());
						Server.setExecutor(null);
						Server.start();
						Activated = true;
						XTotal = 0;
						YTotal = 0;
						YCount = 0;
						YTotal = 0;
						OpenStream.setDisable(true);
					}
					catch (Exception E){
						System.out.println(E);
					}
				}
			}
		});

		ExportImage.setOnAction(new EventHandler<ActionEvent>(){
			public void handle(ActionEvent Event){
				FileChooser FileChooser = new FileChooser();
            	FileChooser.ExtensionFilter ExtFilter = new FileChooser.ExtensionFilter("png files (*.png)", "*.png");
            	FileChooser.getExtensionFilters().add(ExtFilter);
            	File File = FileChooser.showSaveDialog(Main);
            	Activated = false;
            	if (File != null){
            		try{
            			ImageIO.write(SwingFXUtils.fromFXImage(Image, null), "png", File);
                 		}
            		catch(Exception E){
            			System.out.println(E);
            		}
            	}

			}	
		});

		Main.setOnCloseRequest(new EventHandler<WindowEvent>() {
			public void handle(WindowEvent t) {
		        Platform.exit();
		        System.exit(0);
		    }
		});
		Scene Frame = new Scene(Pane);
	    Main.setTitle("ImageRender");
	    Main.setScene(Frame);
	    Main.show();
	}

	static class MyHandler implements HttpHandler{
		public void handle(HttpExchange Exchanger) throws IOException{
			String Response = "Request Recieved";
			Exchanger.sendResponseHeaders(200,Response.length());
			InputStream Input = Exchanger.getRequestBody();
			String Value = convertStreamToString(Input);
         	Input.close();
         	OutputStream Output = Exchanger.getResponseBody();
	        Output.write(Response.getBytes());
	        Output.close();
	        if (XTotal == 0 && YTotal == 0 && Activated){
	        		Scanner Scan = new Scanner(Value);
					XTotal = Scan.nextInt();
					YTotal = Scan.nextInt();
					Image = new WritableImage(XTotal,YTotal);
					PixelWrite = Image.getPixelWriter();
	        }
	        else{
	        	if (Value.equals("StreamComplete")){
	        		ExportImage.setDisable(false);
	        		System.out.println("Going");
	        	}
	        	else{
		        	int[][] TempArray = ParseData(Value);
		        	if (TempArray.length > XTotal){
		        		YCount++;
	                	for (int i = 0; i < XTotal ; i++){
			        		PixelWrite.setColor(i,YCount, Color.rgb(TempArray[i][0],TempArray[i][1],TempArray[i][2],1));
			        	}
                 		YCount++;
		        		for (int i = XTotal; i < XTotal*2; i++){
		        			PixelWrite.setColor(i-XTotal,YCount, Color.rgb(TempArray[i][0],TempArray[i][1],TempArray[i][2],1));
		        		}
                
		        	}
		        	else{
		        		YCount++;
	                	for (int i = 0; i < XTotal ; i++){
			        		PixelWrite.setColor(i,YCount, Color.rgb(TempArray[i][0],TempArray[i][1],TempArray[i][2],1));
			        	}
		        	}
		        }
	        }



	        /*
	        if (Value.equals("StreamEnd")){
	        	System.out.println("Going");
	        	AddPixels(TotalData);
	        }
	        else{
				if (XTotal == 0 && YTotal == 0){
					Scanner Scan = new Scanner(Value);
					XTotal = Scan.nextInt();
					YTotal = Scan.nextInt();
					Image = new WritableImage(XTotal,YTotal);
					PixelWrite = Image.getPixelWriter();
				}
				else{
					TotalData = TotalData + Value;
				}
         	}
         	*/
		}
	}

	//public static 

	static String convertStreamToString(java.io.InputStream is) {
    	java.util.Scanner s = new java.util.Scanner(is).useDelimiter("\\A");
    	return s.hasNext() ? s.next() : "";
   	} 

   	static int[][] ParseData(String text) {
	    if (! text.startsWith("[[") || ! text.endsWith("]]"))
		        throw new IllegalArgumentException("Invalid text: " + text);
		    String[] subTexts = text.substring(2, text.length() - 2).split("\\],\\[");
		    int[][] result = new int[subTexts.length][];
		    for (int i = 0; i < subTexts.length; i++) {
		        String[] valueTexts = subTexts[i].split(",");
		        result[i] = new int[valueTexts.length];
		        for (int j = 0; j < valueTexts.length; j++)
		            result[i][j] = Integer.parseInt(valueTexts[j]);
	    }
    return result;
	}



	public static void main(String[] args) {
		Application.launch(args);
	}
}