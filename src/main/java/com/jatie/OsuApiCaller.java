package com.jatie;

import com.jatie.entity.Beatmap;
import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.Set;

public class OsuApiCaller {
    private static final int RANKED = 1;
    private static final int APPROVED = 2;
    private static final int QUALIFIED = 3;
    private static final int LOVED = 4;

    public static Set<Beatmap> getAllBeatmapsFromApi(String dateSince, String dateTo, String apiKey,
                                                     boolean userIncludeRankedMaps, boolean userIncludeApprovedMaps,
                                                     boolean userIncludeQualifiedMaps, boolean userIncludeLovedMaps) throws Exception {
        System.out.println("\nFetching beatmap information from osu! API...");
        Set<Beatmap> beatmapSets = new HashSet<>();

        try (HttpClient client = HttpClient.newHttpClient()) {

            while (true) {
                String jsonText = fetchJsonData(client, apiKey, dateSince);

                if (jsonText == null || jsonText.equals("[]")) {
                    break;
                }

                JSONArray jsonArray = new JSONArray(jsonText);

                if (!processBeatmaps(jsonArray, beatmapSets, dateTo, userIncludeRankedMaps, userIncludeApprovedMaps, userIncludeQualifiedMaps, userIncludeLovedMaps)) {
                    break;
                }

                // Update date range for next API request
                dateSince = jsonArray.getJSONObject(jsonArray.length() - 1).getString("approved_date");

                System.out.print(beatmapSets.size() + " beatmaps fetched from the API...\r");
            }
        }

        System.out.println(beatmapSets.size() + " total beatmaps fetched from the API!");
        return beatmapSets;
    }

    private static String fetchJsonData(HttpClient client, String apiKey, String dateSince) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI("https://osu.ppy.sh/api/get_beatmaps?k=" + apiKey + "&m=0&since=" + URLEncoder.encode(dateSince, StandardCharsets.UTF_8)))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        return response.body();
    }

    private static boolean processBeatmaps(JSONArray jsonArray, Set<Beatmap> beatmapSets, String dateTo,
                                           boolean userIncludeRankedMaps, boolean userIncludeApprovedMaps,
                                           boolean userIncludeQualifiedMaps, boolean userIncludeLovedMaps) {
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);

            // Stop fetching if we've reached the user-specified maximum year.
            if (jsonObject.getString("approved_date").substring(0, 4).equals(dateTo)) {
                return false;
            }

            Beatmap beatmap = new Beatmap(
                    jsonObject.getInt("beatmapset_id"),
                    jsonObject.getString("artist"),
                    jsonObject.getString("title"),
                    jsonObject.getInt("audio_unavailable") == 1 || jsonObject.getInt("download_unavailable") == 1
            );

            if (checkUserInputApprovedState(jsonObject.getInt("approved"), userIncludeRankedMaps, userIncludeApprovedMaps, userIncludeQualifiedMaps, userIncludeLovedMaps)) {
                beatmapSets.add(beatmap);
            }
        }
        return true;
    }

    /**
     * Checks if the user wants to include the beatmap based on their approval state.
     *
     * @param approvalState            The state of the beatmap's approval (1, 2, 3, or 4).
     * @param userIncludeRankedMaps    Whether the user wants to include Ranked beatmaps.
     * @param userIncludeApprovedMaps  Whether the user wants to include Approved beatmaps.
     * @param userIncludeQualifiedMaps Whether the user wants to include Qualified beatmaps.
     * @param userIncludeLovedMaps     Whether the user wants to include Loved beatmaps.
     * @return true if the beatmap's approval state matches the user's preference, otherwise false.
     */
    private static boolean checkUserInputApprovedState(int approvalState,
                                                       boolean userIncludeRankedMaps, boolean userIncludeApprovedMaps,
                                                       boolean userIncludeQualifiedMaps, boolean userIncludeLovedMaps) {
        return switch (approvalState) {
            case RANKED -> userIncludeRankedMaps;
            case APPROVED -> userIncludeApprovedMaps;
            case QUALIFIED -> userIncludeQualifiedMaps;
            case LOVED -> userIncludeLovedMaps;
            default -> false;
        };
    }
}
