package part3.view;

import java.io.File;
import java.io.IOException;

public interface InputListener {

	void start(File dir, File wordsFile, int limitWords);
	
	void stop();
}
