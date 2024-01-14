package com.github.darylyeung.zentaoplugin.extension.zentao;

import com.github.darylyeung.zentaoplugin.common.ZentaoConstant;
import com.github.darylyeung.zentaoplugin.extension.zentao.model.ZentaoBug;
import com.github.darylyeung.zentaoplugin.extension.zentao.model.ZentaoProduct;
import com.github.darylyeung.zentaoplugin.util.ZentaoUtil;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.intellij.tasks.Task;
import com.intellij.tasks.TaskRepositoryType;
import com.intellij.tasks.impl.BaseRepository;
import com.intellij.tasks.impl.httpclient.NewBaseRepositoryImpl;
import com.intellij.tasks.impl.httpclient.TaskResponseUtil;
import com.intellij.util.containers.ContainerUtil;
import com.intellij.util.xmlb.annotations.Tag;
import org.apache.http.HttpRequestInterceptor;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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
        final List<ZentaoBug> bugs = fetchBugs((offset / limit) + 1, limit, !withClosed);
        return ContainerUtil.map2Array(bugs, ZentaoTask.class, issue -> new ZentaoTask(this, issue));
    }

    private List<ZentaoBug> fetchBugs(int pageNumber, int pageSize, boolean openedOnly) throws Exception {
        ensureProjectsDiscovered();
        final URIBuilder uriBuilder = new URIBuilder(getBugsUrl())
                .addParameter("page", String.valueOf(pageNumber))
                .addParameter("per_page", String.valueOf(pageSize))
                // Ordering was added in v7.8
                .addParameter("order_by", "updated_at");
        if (openedOnly) {
            // Filtering by state was added in v7.3
            uriBuilder.addParameter("state", "opened");
        }
        final ResponseHandler<List<ZentaoBug>> handler = new TaskResponseUtil.GsonMultipleObjectsDeserializer<>(new Gson(), LIST_OF_BUGS_TYPE);
        return getHttpClient().execute(new HttpGet(uriBuilder.build()), handler);
    }

    private String getBugsUrl() {
        if (myCurrentProduct != null && myCurrentProduct != UNSPECIFIED_PRODUCT) {
            return getRestApiUrl("projects", myCurrentProduct.getId(), "issues");
        }
        return getRestApiUrl("issues");
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

    @Override
    public @NotNull String getRestApiPathPrefix() {
        return "/api.php/v1";
    }

    @Nullable
    @Override
    protected HttpRequestInterceptor createRequestInterceptor() {
        return (request, context) -> request.addHeader(ZentaoConstant.TOKEN.getCode(), myPassword);
    }

    /**
     * Always forcibly attempt to fetch new projects from server.
     */
    @NotNull
    public List<ZentaoProduct> fetchProducts() throws Exception {
        final ResponseHandler<List<ZentaoProduct>> handler = new TaskResponseUtil.GsonMultipleObjectsDeserializer<>(ZentaoUtil.GSON, LIST_OF_PRODUCTS_TYPE);
        final String productUrl = getRestApiUrl("products");
        final URIBuilder paginatedProjectsUrl = new URIBuilder(productUrl);
        final List<ZentaoProduct> page = getHttpClient().execute(new HttpGet(paginatedProjectsUrl.build()), handler);
        if (page.isEmpty()) {
            return Collections.emptyList();
        }
        myProducts = page;
        return Collections.unmodifiableList(myProducts);
    }

    public ZentaoProduct getCurrentProduct() {
        return myCurrentProduct;
    }
}
