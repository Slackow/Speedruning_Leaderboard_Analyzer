package com.slackow.main;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class UI {

    public static final String[] GAMES = {"mc", "mcce", "mc_dmce"};

    public static final Scanner SCANNER = new Scanner(System.in);

    public static String pick(String prompt, Iterable<String> iterable) {
        return pick(prompt, StreamSupport.stream(iterable.spliterator(), false).toArray(String[]::new));
    }

    public static String pick(String prompt, String... options) {
        System.out.println();
        for (int i = 0; i < options.length; i++) {
            System.out.println((i + 1) + ". " + options[i]);
        }
        int i;
        do {
            System.out.print(prompt + ": ");
            i = Integer.parseUnsignedInt(SCANNER.next("[1-9]\\d*")) - 1;
        } while (i >= options.length);

        return options[i];
    }

    public static boolean pickBool(String prompt) {
        return pick(prompt, "Yes", "No").length() > 2;
    }

    public static void main(String[] args) {
        String game = pick("Pick a Game", GAMES);
        JsonObject data = getUrl("https://www.speedrun.com/api/v1/games/%s/categories".formatted(game));
        List<Category> categoryList = new ArrayList<>();
        Gson gson = new Gson();
        for (JsonElement category : data.getAsJsonArray("data")) {
            categoryList.add(gson.fromJson(category, Category.class));
        }
//        categoryList.forEach(category -> System.out.println(category.name + " : " + category.type + " : " +
//                category.links.stream()
//                .filter(link -> link.rel.equals("runs"))
//                .findAny()
//                .map(link -> link.uri)
//                .orElse(null)));
        Map<String, Category> nameToCategory = categoryList.stream().collect(Collectors.toMap(category -> category.name, Function.identity()));
        String category = pick("Pick a Category", nameToCategory.keySet());
    }

    public static JsonObject getUrl(String url) {
        try (InputStream is = new URL(url).openStream()){
            return JsonParser.parseString(new String(is.readAllBytes())).getAsJsonObject();
        } catch (IOException e) {
            throw new RuntimeException("Problem getting a json thingy", e);
        }
    }

    public static final class Category {
        public String id;
        public String name;
        public String weblink;
        public String type;
        public List<Link> links;

        public static class Link {
            public String rel;
            public String uri;

            @Override
            public String toString() {
                return "Link{" +
                        "rel='" + rel + '\'' +
                        ", uri='" + uri + '\'' +
                        '}';
            }
        }
    }
}
