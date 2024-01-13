package com.github.darylyeung.zentaoplugin.extension.zentao;

import com.github.darylyeung.zentaoplugin.extension.zentao.model.ZentaoBug;
import com.github.darylyeung.zentaoplugin.extension.zentao.model.ZentaoProduct;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.intellij.tasks.Task;
import com.intellij.tasks.TaskRepositoryType;
import com.intellij.tasks.impl.BaseRepository;
import com.intellij.tasks.impl.httpclient.NewBaseRepositoryImpl;
import com.intellij.tasks.impl.httpclient.TaskResponseUtil;
import com.intellij.util.xmlb.annotations.Tag;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author Yeung
 * @version v1.0
 * @date 2024-01-10 23:02:18
 */
@Tag("Zentao")
public class ZentaoRepository extends NewBaseRepositoryImpl {

    private ZentaoProduct myCurrentProduct;

    private List<ZentaoProduct> myProducts = null;
    //    private static final Gson GSON = TaskGsonUtil.createDefaultBuilder().create();
    private static final TypeToken<List<ZentaoProduct>> LIST_OF_PRODUCTS_TYPE = new TypeToken<>() {};

    private static final TypeToken<List<ZentaoBug>> LIST_OF_BUGS_TYPE = new TypeToken<>() {};

    public static final ZentaoProduct UNSPECIFIED_PRODUCT = createUnspecifiedProduct();

    private static ZentaoProduct createUnspecifiedProduct() {
        final ZentaoProduct unspecified = new ZentaoProduct() {
            @Override
            public String getName() {
                return "-- all issues created by you --";
            }
        };
        unspecified.setId(-1);
        return unspecified;
    }

    public ZentaoRepository(ZentaoRepository other) {
        super(other);
        myCurrentProduct = other.myCurrentProduct;
    }


    @Override
    public @Nullable Task findTask(@NotNull String s) throws Exception {
        return null;
    }

    public ZentaoRepository(TaskRepositoryType type) {
        super(type);
    }

    @Override
    public @NotNull BaseRepository clone() {
        return new ZentaoRepository(this);
    }

    @Override
    public Task[] getIssues(@Nullable String query, int offset, int limit, boolean withClosed) throws Exception {
        return super.getIssues(query, offset, limit, withClosed);
    }

    @NotNull
    public List<ZentaoProduct> getProducts() {
        try {
            ensureProjectsDiscovered();
        } catch (Exception ignored) {
            return Collections.emptyList();
        }
        return Collections.unmodifiableList(myProducts);
    }

    private void ensureProjectsDiscovered() throws Exception {
        if (myProducts == null) {
            fetchProducts();
        }
    }

    /**
     * Always forcibly attempt to fetch new projects from server.
     */
    @NotNull
    public List<ZentaoProduct> fetchProducts() throws Exception {
        final ResponseHandler<List<ZentaoProduct>> handler = new TaskResponseUtil.GsonMultipleObjectsDeserializer<>(new Gson(), LIST_OF_PRODUCTS_TYPE);
        final String projectUrl = getRestApiUrl("projects");
        final List<ZentaoProduct> result = new ArrayList<>();
        int pageNum = 1;
        while (true) {
            final URIBuilder paginatedProjectsUrl = new URIBuilder(projectUrl).addParameter("page", String.valueOf(pageNum)).addParameter("per_page", "30");
            // In v4 this endpoint otherwise returns all projects visible to the current user
            paginatedProjectsUrl.addParameter("membership", "true");
            final List<ZentaoProduct> page = getHttpClient().execute(new HttpGet(paginatedProjectsUrl.build()), handler);
            // Gitlab's REST API doesn't allow to know beforehand how many projects are available
            if (page.isEmpty()) {
                break;
            }
            result.addAll(page);
            pageNum++;
        }
        myProducts = result;
        return Collections.unmodifiableList(myProducts);
    }

    public ZentaoProduct getCurrentProduct() {
        return myCurrentProduct;
    }
}
