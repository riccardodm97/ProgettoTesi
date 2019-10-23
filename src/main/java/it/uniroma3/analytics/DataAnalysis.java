package it.uniroma3.analytics;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.github.jfasttext.JFastText;

import it.uniroma3.model.Comment;
import it.uniroma3.service.CommentService;

@Component
public class DataAnalysis {
	
	@Autowired
	private CommentService commentService;
	
	@Autowired
	private TextPreprocessing txtProcess;
	
	private JFastText jft;           //libreria per il machine learning sui commenti
	
	public void StartDataAnalysis() {
		
	}
	
	public void AnalyzeComments() {
		this.jft=new JFastText();           //inizializzo la classe della libreria da usare per la classification del testo
		List<Comment> commenti=this.commentService.getAllComments();
		for(Comment c:commenti) {
			float prob=this.TextClassification(c.getText());     //per ogni commento setto il livello di attendibilità 
			c.setTrustworthiness(prob);
			this.commentService.inserisci(c);
		}
	}
	
	public float TextClassification(String text) {
		this.jft.loadModel("src/main/resources/models/supervised.model.bin");
		//preprocessing
		String processed_text= txtProcess.Process(text);
		JFastText.ProbLabel probLabel = jft.predictProba(processed_text);
		float prob=probLabel.logProb;
		if(probLabel.label.equals("___label___true"))
			prob=1-prob;
		return prob;
	}
}
