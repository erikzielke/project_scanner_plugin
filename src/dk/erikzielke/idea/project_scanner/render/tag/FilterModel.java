package dk.erikzielke.idea.project_scanner.render.tag;

import com.intellij.util.containers.ContainerUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class FilterModel<Filter> {

    @NotNull
    private final Collection<Runnable> mySetFilterListeners = ContainerUtil.newArrayList();

    @Nullable
    private Filter myFilter;

    private List<String> potentialTags = new ArrayList<>();

    @Nullable
    public Filter getFilter() {
        return myFilter;
    }

    public void setFilter(@Nullable Filter filter) {
        myFilter = filter;
        for (Runnable listener : mySetFilterListeners) {
            listener.run();
        }
    }

    public void addSetFilterListener(@NotNull Runnable runnable) {
        mySetFilterListeners.add(runnable);
    }

    public void setPotentialTags(List<String> potentialTags) {
        this.potentialTags = potentialTags;
    }

    public List<String> getPotentialTags() {
        return potentialTags;
    }
}
