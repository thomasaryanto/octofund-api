package com.thomasariyanto.octofund;

import java.util.List;

import com.google.cloud.vision.v1.AnnotateImageResponse;
import com.google.cloud.vision.v1.EntityAnnotation;
import com.google.cloud.vision.v1.Feature.Type;
import com.google.cloud.vision.v1.ImageAnnotatorClient;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gcp.vision.CloudVisionTemplate;
import org.springframework.core.io.ResourceLoader;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

@RestController
public class VisionController {
	@Autowired
	private ResourceLoader resourceLoader;

	@Autowired
	private CloudVisionTemplate cloudVisionTemplate;

	/**
	 * This method downloads an image from a URL and sends its contents to the Vision API for label detection.
	 *
	 * @param imageUrl the URL of the image
	 * @param map the model map to use
	 * @return a string with the list of labels and percentage of certainty
	 * @throws org.springframework.cloud.gcp.vision.CloudVisionException if the Vision API call
	 *    produces an error
	 */
	@GetMapping("/extractLabels")
	public ModelAndView extractLabels(String imageUrl, ModelMap map) {
		AnnotateImageResponse response = this.cloudVisionTemplate.analyzeImage(
				this.resourceLoader.getResource(imageUrl), Type.LABEL_DETECTION);

		// This gets the annotations of the image from the response object.
		List<EntityAnnotation> annotations = response.getLabelAnnotationsList();

		map.addAttribute("annotations", annotations);
		map.addAttribute("imageUrl", imageUrl);

		return new ModelAndView("result", map);
	}

	@GetMapping("/extractText")
	public ModelAndView extractText(String imageUrl, ModelMap map) {
		String text = this.cloudVisionTemplate.extractTextFromImage(
				this.resourceLoader.getResource(imageUrl));

		map.addAttribute("text", text);
		map.addAttribute("imageUrl", imageUrl);

		return new ModelAndView("result", map);
	}
}
