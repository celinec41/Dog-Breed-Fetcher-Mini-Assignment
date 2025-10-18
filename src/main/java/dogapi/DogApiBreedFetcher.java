package dogapi;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.*;

/**
 * BreedFetcher implementation that relies on the dog.ceo API.
 * Note that all failures get reported as BreedNotFoundException
 * exceptions to align with the requirements of the BreedFetcher interface.
 */
public class DogApiBreedFetcher implements BreedFetcher {
    private final OkHttpClient client = new OkHttpClient();

    /**
     * Fetch the list of sub breeds for the given breed from the dog.ceo API.
     * @param breed the breed to fetch sub breeds for
     * @return list of sub breeds for the given breed
     * @throws BreedNotFoundException if the breed does not exist (or if the API call fails for any reason)
     */
    @Override
    public List<String> getSubBreeds(String breed) {
        if (breed == null) {
            throw new BreedNotFoundException("Breed cannot be empty");
        }
        breed = breed.toLowerCase();
        String url = "https://dog.ceo/api/breed/" + breed + "/list";

        Request request = new Request.Builder().url(url).build();
        try(Response response = client.newCall(request).execute()){
            if (response.body() == null){
                throw new BreedNotFoundException("Empty response");
            }
            String body = response.body().string();
            if (!response.isSuccessful()){
                throw new BreedNotFoundException("Unexpected code " + response);
            }

            JSONObject json = new JSONObject(body);

            String status = json.optString("status","error");
            if (!"success".equals(status)){
                String message = json.optString("message","Breed not found.");
                throw new BreedNotFoundException(message);
            }

            JSONArray arr = json.getJSONArray("message");
            List<String> result = new ArrayList<>(arr.length());
            for (int i = 0; i< arr.length(); i++){
                result.add(arr.getString(i));
            }
            return result;

        }catch(IOException | JSONException e){
            throw new BreedNotFoundException("Error parsing response" + e.getMessage());
        }
    }
}