package com.example.core;

public interface UseCase<I, O> {
    O execute(I input);
}
