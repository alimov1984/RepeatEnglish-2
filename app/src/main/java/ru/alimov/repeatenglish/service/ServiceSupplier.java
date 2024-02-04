package ru.alimov.repeatenglish.service;

import android.content.Context;

/**
 * Factory for services.
 */
public final class ServiceSupplier {
    private static volatile WordService wordService;

    public static WordService getWordService(Context context) {
        if (wordService == null) {
            synchronized (ServiceSupplier.class) {
                if (wordService == null) {
                    wordService = new WordServiceImpl(context);
                }
            }
        }
        return wordService;
    }
}
