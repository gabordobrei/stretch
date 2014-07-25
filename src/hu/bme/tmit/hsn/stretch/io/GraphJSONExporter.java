package hu.bme.tmit.hsn.stretch.io;

import java.io.File;
import java.io.IOException;

import org.jgrapht.Graph;

import com.google.common.base.Charsets;
import com.google.common.io.Files;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class GraphJSONExporter {

	static Gson gson;
	boolean isPretty = true;
	String fileName;

	private static GraphJSONExporter instance = null;

	private GraphJSONExporter() {
		gson = isPretty ? new GsonBuilder().setPrettyPrinting().create() : new Gson();
	}

	public static GraphJSONExporter getInstance() {
		if (instance == null) {
			instance = new GraphJSONExporter();
		}
		return instance;
	}

	public void export(Graph graph, String fileName) throws IOException {
		Files.write(gson.toJson(graph), new File(fileName), Charsets.UTF_8);
	}

}
