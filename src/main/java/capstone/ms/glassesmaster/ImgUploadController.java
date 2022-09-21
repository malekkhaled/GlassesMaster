package capstone.ms.glassesmaster;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Random;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

@Controller
public class ImgUploadController {
	
	public static String UPLOAD_DIR = System.getProperty("user.dir") + "/uploads";
	
	// Displays the home page index.html
	@GetMapping("/index")
	public String displayForm() {
		return "index";
	}
	
	// Handles the image upload and posts to the result endpoint, displaying result.html
	@PostMapping("/result")
	public String uploadImage(Model model, @RequestParam("image") MultipartFile file) throws IOException {
		StringBuilder fileNames = new StringBuilder();
		
		// Generate random 6-digit ID to anonymize image
		Random rand = new Random();
		Integer imageID = 100000 + rand.nextInt(900000);
		
		// Get file extension type
		String fileExtension = '.' + file.getContentType().substring(file.getContentType().lastIndexOf('/') + 1);
		
		// Create path to save image to
		Path fileNameAndPath = Paths.get(UPLOAD_DIR, imageID.toString() + fileExtension);
		fileNames.append(imageID.toString() + fileExtension);
		Files.write(fileNameAndPath, file.getBytes());			// Write file to specified path
		
		model.addAttribute("msg", "Uploaded image: " + fileNames.toString());
		
		return "result";
	}
}
