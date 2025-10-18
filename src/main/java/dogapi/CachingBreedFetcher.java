package dogapi;

import java.util.*;

/**
 * This BreedFetcher caches fetch request results to improve performance and
 * lessen the load on the underlying data source. An implementation of BreedFetcher
 * must be provided. The number of calls to the underlying fetcher are recorded.
 *
 * If a call to getSubBreeds produces a BreedNotFoundException, then it is NOT cached
 * in this implementation. The provided tests check for this behaviour.
 *
 * The cache maps the name of a breed to its list of sub breed names.
 */
public class CachingBreedFetcher implements BreedFetcher {
    private final BreedFetcher innerFetcher;
    private final Map<String, List<String>> cache;
    private int callsMade = 0;

    public CachingBreedFetcher(BreedFetcher fetcher) {
        this.innerFetcher = Objects.requireNonNull(fetcher);
        this.cache = new HashMap<>();
    }

    @Override
    public List<String> getSubBreeds(String breed) throws BreedNotFoundException {
        // return statement included so that the starter code can compile and run.
        if (breed == null) {
            throw new BreedFetcher.BreedNotFoundException("Breed is null");
        }

        String key = breed.trim();
        if (cache.containsKey(key)) {
            return new ArrayList<>(cache.get(key));
        }

        callsMade++;
        List <String> result = innerFetcher.getSubBreeds(breed);
        cache.put(key, new ArrayList<>(result));
        return new ArrayList<>(result);
    }

    public int getCallsMade() {
        return callsMade;
    }
}