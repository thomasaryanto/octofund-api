package com.thomasariyanto.octofund.util;

public class SmilarityValues {
    public String word;
    public double score;

    public SmilarityValues(String word, double score) {
        this.word = word;
        this.score = score;
    }

	public String getWord() {
		return word;
	}

	public void setWord(String word) {
		this.word = word;
	}

	public double getScore() {
		return score;
	}

	public void setScore(double score) {
		this.score = score;
	}
    
    
}
