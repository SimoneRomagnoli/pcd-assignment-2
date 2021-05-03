package part3.v2;

import java.io.File;
import java.io.IOException;

public interface InputListener {

	void start(File dir, File wordsFile, int limitWords) throws IOException;
	
	void stop();
}
