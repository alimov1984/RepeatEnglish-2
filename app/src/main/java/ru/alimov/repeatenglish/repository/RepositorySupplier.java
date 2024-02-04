package ru.alimov.repeatenglish.repository;

import android.content.Context;

/**
 * Factory for repositories.
 */
public final class RepositorySupplier {
    private static volatile WordRepository wordRepository;

    public static WordRepository getWordRepository(Context context) {
        if (wordRepository == null) {
            synchronized (RepositorySupplier.class) {
                if (wordRepository == null) {
                    wordRepository = new WordRepository(context);
                }
            }
        }
        return wordRepository;
    }
}
