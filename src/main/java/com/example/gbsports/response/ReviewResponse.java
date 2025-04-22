package com.example.gbsports.response;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class ReviewResponse {
    private double averageRating;
    private int totalReviews;
    private List<Map<String, Object>> reviews;
    private Map<Integer, Integer> ratingDistribution;
}