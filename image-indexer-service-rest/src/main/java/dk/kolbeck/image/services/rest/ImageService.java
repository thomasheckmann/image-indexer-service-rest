package dk.kolbeck.image.services.rest;

import static spark.Spark.before;
import static spark.Spark.exception;
import static spark.Spark.get;
import static spark.Spark.post;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import dk.kolbeck.image.hash.ImagePHash;
import dk.kolbeck.image.object.ImageHashedType;

public class ImageService {

	public static void main(String[] args) {

		get("/hello", (request, response) -> "Hello World GET!");

		post("/hello", (request, response) -> "Hello World POST!");

		// calculate phash using image on local filesystem
		get("/phash/:filename", (request, response) -> {
			String fileName = request.params(":filename");
			ImagePHash phash = new ImagePHash();
			ImageHashedType result = phash.getImageTypeWithHash(fileName, 0);
			response.status(200);
			return result;
		}, new JsonTransformer());

		// calculate phash using image base64 encoded
		// input object {filename: "just a name", data: "base64encoded image"}
		post("/phash", (request, response) -> {
			// Logger.getLogger("test").log(Level.INFO, "HELLO");
			String s = request.body();
			JsonObject jsonObject = (new JsonParser()).parse(s).getAsJsonObject();
			JsonElement filename = jsonObject.get("filename");
			JsonElement data = jsonObject.get("data");
			JsonElement width = jsonObject.get("minwidth");

			int minWidth = 0;
			if (!(width == null)) {
				minWidth = width.getAsInt();
			}
			String base64data = data.getAsString();

			ImagePHash phash = new ImagePHash();
			ImageHashedType r = phash.getImageTypeWithHash(filename.getAsString(), base64data, minWidth);
			return r;
		}, new JsonTransformer());

		exception(Exception.class, (e, request, response) -> {
			response.status(404);
			Logger.getLogger("test").log(Level.INFO, e.getMessage());
			response.body(e.getMessage() + " : Resource not found");
		});

		// set cors headers
		before((request, response) -> {
			response.header("Access-Control-Allow-Origin", "*");
			response.header("Access-Control-Allow-Methods", "GET, POST");
			// response.header("Access-Control-Allow-Headers", "Content-Type");
		});
	}
}
