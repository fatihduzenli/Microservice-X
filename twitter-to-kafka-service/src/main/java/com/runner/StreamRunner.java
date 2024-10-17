package com.runner;

import twitter4j.TwitterException;

public interface StreamRunner {//This interface defines a contract for classes that will run a stream (e.g., Twitter stream).

        void start() throws TwitterException; //This method is responsible for starting the stream.

}
