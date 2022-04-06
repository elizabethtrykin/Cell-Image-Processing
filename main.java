
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.awt.image.*;
import javax.imageio.ImageIO;



public class main {

	private static ArrayList<BufferedImage> imageList;
	private static ArrayList<String> functionList;

	public static void main(String args[]) throws IOException {
		try {
			// Reading in file name form the commmand line
			if (args.length != 1) {
				System.out.println("Error please enter 1 image file.");
				return;
			}

			String fileName = args[0];
			String[] fileType = fileName.split("\\.");


			// reading image
			File f = new File(fileName);
			BufferedImage img = ImageIO.read(f);

			int cellNum;
				
			imageList = new ArrayList<>();
			functionList = new ArrayList<>();

			newGuiData(img,"Original image");

			img = ImageProcessing.contrast(img, 0.1);
			newGuiData(img,"Contrast");

			img = ImageProcessing.gaussianBlur(img);
			newGuiData(img,"Gaussian Blur");

			img = ImageProcessing.gamma(img, 4);
			newGuiData(img,"Gamma");

			img = ImageProcessing.thresholding(img, 30);
			newGuiData(img,"Thresholding");
	
			img = ImageProcessing.opening(img, 4);
			newGuiData(img, "Opening");

			img = ImageProcessing.closing(img, 3);
			newGuiData(img, "Closing");

			cellNum = ImageProcessing.cellCount(img);
			newGuiData(img,"Output");
			
			//Creating the main GUI
		    new CellGUI(imageList, functionList, cellNum);

			// write image
			f = new File(fileType[0] + "-output." + fileType[1] );
			ImageIO.write(img, fileType[1], f);

		} catch (Exception e) {
			System.out.print(e);
			System.out.println();
		}
	}

	private static void newGuiData(BufferedImage image, String function){
		imageList.add(ImageProcessing.deepCopy(image)); //Calls deepcopy method from Image Processor class
		functionList.add(function);
	}
}
