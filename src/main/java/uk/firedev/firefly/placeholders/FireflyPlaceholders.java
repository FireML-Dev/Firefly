package uk.firedev.firefly.placeholders;

import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;
import uk.firedev.daisylib.placeholders.IPlaceholder;
import uk.firedev.daisylib.placeholders.PlaceholderReceiver;
import uk.firedev.firefly.Firefly;

import java.util.ArrayList;
import java.util.List;

public final class FireflyPlaceholders extends PlaceholderReceiver {

    private static final FireflyPlaceholders INSTANCE = new FireflyPlaceholders();

    private final List<IPlaceholder> placeholders = new ArrayList<>();

    private FireflyPlaceholders() {}

    public static @NonNull FireflyPlaceholders get() {
        return INSTANCE;
    }

    @Override
    public @NonNull List<@NonNull IPlaceholder> getCustomPlaceholders() {
        return placeholders;
    }

    public void add(@NonNull IPlaceholder placeholder) {
        this.placeholders.add(placeholder);
    }

    @Override
    public @NotNull String getIdentifier() {
        return "Firefly";
    }

    @Override
    public @NotNull String getAuthor() {
        return "FireML";
    }

    @Override
    public @NotNull String getVersion() {
        return Firefly.getInstance().getPluginMeta().getVersion();
    }

}
