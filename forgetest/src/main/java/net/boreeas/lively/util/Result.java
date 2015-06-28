package net.boreeas.lively.util;

import org.jetbrains.annotations.NotNull;

import java.util.Optional;

/**
 * @author Malte Schütze
 */
public class Result<T, U> {
    private Optional<T> ok;
    private Optional<U> fail;
    private final boolean isOk;

    public static <T, U> Result<T, U> ok(@NotNull T t) {
        return new Result(t, null);
    }

    public static <T, U> Result<T, U> fail(@NotNull U u) {
        return new Result(null, u);
    }

    private Result(T t, U u) {
        ok = Optional.ofNullable(t);
        fail = Optional.ofNullable(u);
        isOk = t != null;
    }

    public boolean isOk() { return isOk; }

    public T getOk() {
        return ok.get();
    }

    public U getFail() {
        return fail.get();
    }
}
