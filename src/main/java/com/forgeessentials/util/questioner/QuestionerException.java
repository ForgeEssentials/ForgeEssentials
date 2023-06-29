package com.forgeessentials.util.questioner;

public class QuestionerException extends Exception {
	public static class QuestionerStillActiveException extends QuestionerException {
		public QuestionerStillActiveException() {
		}
	}
}